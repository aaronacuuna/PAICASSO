package es.aaracubel.paicasso.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.aaracubel.paicasso.backend.dtos.InformeDTO;
import es.aaracubel.paicasso.backend.entities.Analisis;
import es.aaracubel.paicasso.backend.entities.Informe;
import es.aaracubel.paicasso.backend.repositories.AnalisisRepository;
import es.aaracubel.paicasso.backend.repositories.InformeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LLMService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final PromptBuilderService promptBuilderService;
    private final InformeRepository informeRepository;
    private final AnalisisRepository analisisRepository;

    public String analizar(Long usuarioId, String contextoSonarQube, String preguntaUsuario) {
        String prompt = promptBuilderService.construirPrompt(usuarioId, contextoSonarQube, preguntaUsuario);
        return llamarLLM(prompt);
    }

    private String llamarLLM(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = """
                    {
                        "contents": [{
                            "parts": [{
                                "text": %s
                            }]
                        }]
                    }
                    """.formatted(new ObjectMapper().writeValueAsString(prompt));

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            String endpoint = apiUrl + "?key=" + apiKey;

            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            return root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            System.err.println("Error al llamar a Gemini: " + e.getMessage());
            return "No se pudo obtener respuesta de la IA en este momento.";
        }
    }

    public InformeDTO generarInforme(Long repoId, String contextoSonar) {
        String promptDiagnostico = promptBuilderService.construirPromptDiagnostico(contextoSonar);
        String diagnostico = llamarLLM(promptDiagnostico);

        String promptPropuesta = promptBuilderService.construirPromptPropuesta(contextoSonar);
        String propuesta = llamarLLM(promptPropuesta);

        Analisis analisis = analisisRepository.findFirstByRepositorioIdOrderByFechaEjecucionDesc(repoId)
                .orElseThrow(() -> new RuntimeException("No hay análisis para el repositorio: " + repoId));

        Informe informe = informeRepository.findByAnalisisId(analisis.getId())
                .orElse(new Informe());

        informe.setAnalisis(analisis);
        informe.setDiagnostico(diagnostico);
        informe.setPropuesta(propuesta);
        informe.setFechaGeneracion(LocalDateTime.now());
        informeRepository.save(informe);

        return InformeDTO.builder()
                .id(informe.getId())
                .fechaGeneracion(informe.getFechaGeneracion())
                .diagnostico(diagnostico)
                .propuesta(propuesta)
                .build();
    }
}