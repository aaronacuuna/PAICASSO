package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.AnalisisDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.aaracubel.paicasso.backend.entities.Analisis;
import es.aaracubel.paicasso.backend.entities.Metrica;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SonarService {

    @Value("${sonarqube.url}")
    private String sonarUrl;

    @Value("${sonarqube.security.token}")
    private String sonarToken;

    public AnalisisDTO obtenerMetricas(String projectKey) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(sonarToken, "");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String metricKeys = "bugs,vulnerabilities,code_smells,ncloc,coverage,duplicated_lines_density";
            String endpoint = sonarUrl + "/api/measures/component?component=" + projectKey + "&metricKeys=" + metricKeys;

            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode measures = root.path("component").path("measures");

            AnalisisDTO dto = new AnalisisDTO();

            for (JsonNode measure : measures) {
                String metricName = measure.path("metric").asText();
                double value = measure.path("value").asDouble(0.0);

                switch (metricName) {
                    case "bugs":
                        dto.setBugs((int) value);
                        break;
                    case "vulnerabilities":
                        dto.setVulnerabilidades((int) value);
                        break;
                    case "code_smells":
                        dto.setCodeSmells((int) value);
                        break;
                    case "ncloc":
                        dto.setLineasCodigo((int) value);
                        break;
                    case "coverage":
                        dto.setCobertura(value);
                        break;
                    case "duplicated_lines_density":
                        dto.setDuplicaciones(value);
                        break;
                }
            }

            return dto;

        } catch (Exception e) {
            System.err.println("Error al comunicarse con la API de SonarQube: " + e.getMessage());
            return null;
        }
    }

    public List<Metrica> obtenerDetalleIncidencias(String projectKey, Analisis analisis) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(sonarToken, "");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Pedimos incidencias no resueltas de este proyecto
            String endpoint = sonarUrl + "/api/issues/search?componentKeys=" + projectKey + "&resolved=false";

            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode issues = root.path("issues");

            List<Metrica> listaMetricas = new ArrayList<>();

            for (JsonNode issue : issues) {
                Metrica m = new Metrica();
                m.setAnalisis(analisis);

                // Extraemos el nombre del archivo (viene como 'proyecto:ruta/archivo.java')
                String component = issue.path("component").asText();
                String archivo = component.contains(":") ? component.split(":")[1] : component;
                m.setArchivo(archivo);

                m.setLinea(issue.path("line").asInt());
                m.setSeveridad(issue.path("severity").asText());
                m.setDescripcion(issue.path("message").asText());
                m.setTipo(issue.path("type").asText());

                listaMetricas.add(m);
            }

            return listaMetricas;
        } catch (Exception e) {
            System.err.println("Error al obtener detalle de incidencias: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean procesamientoCompletado(String projectKey) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(sonarToken, "");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String endpoint = sonarUrl + "/api/ce/component?component=" + projectKey;
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            JsonNode queue = root.path("queue");
            if (queue.isArray() && !queue.isEmpty()) {
                return false;
            }

            JsonNode current = root.path("current");
            if (!current.isMissingNode()) {
                String status = current.path("status").asText();
                if ("IN_PROGRESS".equals(status) || "PENDING".equals(status)) {
                    return false;
                }
                if ("FAILED".equals(status) || "CANCELED".equals(status)) {
                    System.err.println("Cuidado: El procesamiento en SonarQube falló o se canceló.");
                    return true;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String obtenerContextoCodigo(String componentKey, int lineaError) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(sonarToken, "");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Calculamos una "ventana" de código: 10 líneas por arriba y 10 por abajo del error
            int fromLine = Math.max(1, lineaError - 10);
            int toLine = lineaError + 10;

            String endpoint = sonarUrl + "/api/sources/lines?key=" + componentKey + "&from=" + fromLine + "&to=" + toLine;
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode lines = root.path("sources");

            StringBuilder codigo = new StringBuilder();
            for (JsonNode lineNode : lines) {
                // SonarQube devuelve el código tal cual, línea a línea
                codigo.append(lineNode.path("code").asText()).append("\n");
            }

            return codigo.toString();

        } catch (Exception e) {
            System.err.println("No se pudo obtener el código fuente de SonarQube: " + e.getMessage());
            return "// El código fuente original no está disponible.";
        }
    }
}
