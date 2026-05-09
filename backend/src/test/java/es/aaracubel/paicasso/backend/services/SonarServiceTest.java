package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.AnalisisDTO;
import es.aaracubel.paicasso.backend.dtos.RepositorioDTO;
import es.aaracubel.paicasso.backend.entities.Analisis;
import es.aaracubel.paicasso.backend.entities.Metrica;
import es.aaracubel.paicasso.backend.repositories.AnalisisRepository;
import es.aaracubel.paicasso.backend.repositories.MetricaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SonarServiceTest {

    @Mock
    private RepositorioService repositorioService;

    @Mock
    private AnalisisRepository analisisRepository;

    @Mock
    private MetricaRepository metricaRepository;

    @InjectMocks
    private SonarService sonarService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sonarService, "sonarUrl", "http://sonar");
        ReflectionTestUtils.setField(sonarService, "sonarToken", "token");
    }

    @Test
    void obtenerMetricas_Exito() {
        String mockResponse = """
                {
                    "component": {
                        "measures": [
                            {"metric": "bugs", "value": "5"},
                            {"metric": "vulnerabilities", "value": "2"},
                            {"metric": "code_smells", "value": "15"},
                            {"metric": "coverage", "value": "80.5"},
                            {"metric": "ncloc", "value": "100"},
                            {"metric": "duplicated_lines_density", "value": "5.5"}
                        ]
                    }
                }
                """;

        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class,
                (mockRestTemplate, context) -> {
                    when(mockRestTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                            .thenReturn(ResponseEntity.ok(mockResponse));
                })) {

            AnalisisDTO resultado = sonarService.obtenerMetricas("paicasso_1");

            assertNotNull(resultado);
            assertEquals(5, resultado.getBugs());
            assertEquals(2, resultado.getVulnerabilidades());
            assertEquals(15, resultado.getCodeSmells());
            assertEquals(80.5, resultado.getCobertura());
            assertEquals(100, resultado.getLineasCodigo());
            assertEquals(5.5, resultado.getDuplicaciones());
        }
    }

    @Test
    void obtenerMetricas_Fallo() {
        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class,
                (mockRestTemplate, context) -> {
                    when(mockRestTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                            .thenThrow(new RuntimeException("Error red"));
                })) {

            AnalisisDTO resultado = sonarService.obtenerMetricas("paicasso_1");
            assertNull(resultado);
        }
    }

    @Test
    void obtenerDetalleIncidencias_Exito() {
        String mockResponse = """
                {
                    "issues": [
                        {
                            "component": "paicasso_1:src/Main.java",
                            "line": 10,
                            "severity": "MAJOR",
                            "message": "Code smell detected",
                            "type": "CODE_SMELL"
                        },
                        {
                            "component": "sin_dos_puntos",
                            "line": 15,
                            "severity": "MINOR",
                            "message": "Issue",
                            "type": "BUG"
                        }
                    ]
                }
                """;

        Analisis analisis = new Analisis();

        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class,
                (mockRestTemplate, context) -> {
                    when(mockRestTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                            .thenReturn(ResponseEntity.ok(mockResponse));
                })) {

            List<Metrica> resultados = sonarService.obtenerDetalleIncidencias("paicasso_1", analisis);

            assertFalse(resultados.isEmpty());
            assertEquals("src/Main.java", resultados.get(0).getArchivo());
            assertEquals(10, resultados.get(0).getLinea());

            assertEquals("sin_dos_puntos", resultados.get(1).getArchivo());
        }
    }

    @Test
    void obtenerDetalleIncidencias_Fallo() {
        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class,
                (mockRestTemplate, context) -> {
                    when(mockRestTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                            .thenThrow(new RuntimeException("Error al obtener incidencias"));
                })) {

            List<Metrica> resultados = sonarService.obtenerDetalleIncidencias("paicasso_1", new Analisis());
            assertTrue(resultados.isEmpty());
        }
    }

    @Test
    void procesamientoCompletado_EnCola() {
        String mockResponse = "{\"queue\": [{\"id\": \"1\"}], \"current\": {}}";
        try (MockedConstruction<RestTemplate> mocked = mockRestTemplateReturn(mockResponse)) {
            assertFalse(sonarService.procesamientoCompletado("paicasso_1"));
        }
    }

    @Test
    void procesamientoCompletado_EnProgreso() {
        String mockResponse = "{\"queue\": [], \"current\": {\"status\": \"IN_PROGRESS\"}}";
        try (MockedConstruction<RestTemplate> mocked = mockRestTemplateReturn(mockResponse)) {
            assertFalse(sonarService.procesamientoCompletado("paicasso_1"));
        }
    }

    @Test
    void procesamientoCompletado_Pendiente() {
        String mockResponse = "{\"queue\": [], \"current\": {\"status\": \"PENDING\"}}";
        try (MockedConstruction<RestTemplate> mocked = mockRestTemplateReturn(mockResponse)) {
            assertFalse(sonarService.procesamientoCompletado("paicasso_1"));
        }
    }

    @Test
    void procesamientoCompletado_Fallido() {
        String mockResponse = "{\"queue\": [], \"current\": {\"status\": \"FAILED\"}}";
        try (MockedConstruction<RestTemplate> mocked = mockRestTemplateReturn(mockResponse)) {
            assertTrue(sonarService.procesamientoCompletado("paicasso_1"));
        }
    }

    @Test
    void procesamientoCompletado_Cancelado() {
        String mockResponse = "{\"queue\": [], \"current\": {\"status\": \"CANCELED\"}}";
        try (MockedConstruction<RestTemplate> mocked = mockRestTemplateReturn(mockResponse)) {
            assertTrue(sonarService.procesamientoCompletado("paicasso_1"));
        }
    }

    @Test
    void procesamientoCompletado_Completado() {
        String mockResponse = "{\"queue\": [], \"current\": {\"status\": \"SUCCESS\"}}";
        try (MockedConstruction<RestTemplate> mocked = mockRestTemplateReturn(mockResponse)) {
            assertTrue(sonarService.procesamientoCompletado("paicasso_1"));
        }
    }

    @Test
    void procesamientoCompletado_SinCurrent() {
        String mockResponse = "{\"queue\": []}";
        try (MockedConstruction<RestTemplate> mocked = mockRestTemplateReturn(mockResponse)) {
            assertTrue(sonarService.procesamientoCompletado("paicasso_1"));
        }
    }

    @Test
    void procesamientoCompletado_Excepcion() {
        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class,
                (mockRestTemplate, context) -> {
                    when(mockRestTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                            .thenThrow(new RuntimeException("Timeout"));
                })) {
            assertFalse(sonarService.procesamientoCompletado("paicasso_1"));
        }
    }

    @Test
    void obtenerContextoCodigo_Nulos() {
        assertEquals("// No se ha seleccionado un archivo específico.", sonarService.obtenerContextoCodigo(null, 10));
        assertEquals("// No se ha seleccionado un archivo específico.",
                sonarService.obtenerContextoCodigo("archivo", null));
    }

    @Test
    void obtenerContextoCodigo_Exito() {
        String mockResponse = """
                {
                    "sources": [
                        {"line": 1, "code": "public class Main {"},
                        {"line": 2, "code": "    System.out.println();"}
                    ]
                }
                """;
        try (MockedConstruction<RestTemplate> mocked = mockRestTemplateReturn(mockResponse)) {
            String codigo = sonarService.obtenerContextoCodigo("paicasso_1:src/Main.java", 2);
            assertTrue(codigo.contains("public class Main {"));
            assertTrue(codigo.contains("System.out.println()"));
        }
    }

    @Test
    void obtenerContextoCodigo_Excepcion() {
        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class,
                (mockRestTemplate, context) -> {
                    when(mockRestTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                            .thenThrow(new RuntimeException("API error"));
                })) {
            assertEquals("// El código fuente original no está disponible.",
                    sonarService.obtenerContextoCodigo("archivo", 10));
        }
    }

    @Test
    void construirContextoSonar_SinArchivo() {
        RepositorioDTO repoDTO = new RepositorioDTO();
        repoDTO.setId(1L);
        repoDTO.setNombre("MiRepo");
        when(repositorioService.obtenerRepositorio(1L)).thenReturn(repoDTO);

        String mockResponse = "{\"component\": {\"measures\": [{\"metric\": \"bugs\", \"value\": \"0\"}]}}";
        try (MockedConstruction<RestTemplate> mocked = mockRestTemplateReturn(mockResponse)) {
            String resultado = sonarService.construirContextoSonar(1L, null, null);
            assertTrue(resultado.contains("MiRepo"));
            assertTrue(resultado.contains("Bugs: 0"));
            assertTrue(resultado.contains("sin apuntar a ningún archivo específico"));
        }
    }

    @Test
    void construirContextoSonar_ConArchivo() {
        RepositorioDTO repoDTO = new RepositorioDTO();
        repoDTO.setId(1L);
        repoDTO.setNombre("MiRepo");
        when(repositorioService.obtenerRepositorio(1L)).thenReturn(repoDTO);

        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class,
                (mockRestTemplate, context) -> {
                    when(mockRestTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenAnswer(inv -> {
                        String url = inv.getArgument(0);
                        if (url.contains("/api/measures")) {
                            return ResponseEntity
                                    .ok("{\"component\": {\"measures\": [{\"metric\": \"bugs\", \"value\": \"3\"}]}}");
                        } else if (url.contains("/api/sources")) {
                            return ResponseEntity.ok("{\"sources\": [{\"code\": \"public void error() {}\"}]}");
                        }
                        return ResponseEntity.ok("{}");
                    });
                })) {

            String resultado = sonarService.construirContextoSonar(1L, "src/Main.java", 15);

            assertTrue(resultado.contains("MiRepo"));
            assertTrue(resultado.contains("Bugs: 3"));
            assertTrue(resultado.contains("Fragmento del archivo 'src/Main.java'"));
            assertTrue(resultado.contains("El problema está en la línea 15"));
            assertTrue(resultado.contains("public void error() {}"));
        }
    }

    private MockedConstruction<RestTemplate> mockRestTemplateReturn(String responseBody) {
        return Mockito.mockConstruction(RestTemplate.class,
                (mockRestTemplate, context) -> {
                    when(mockRestTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                            .thenReturn(ResponseEntity.ok(responseBody));
                });
    }
}
