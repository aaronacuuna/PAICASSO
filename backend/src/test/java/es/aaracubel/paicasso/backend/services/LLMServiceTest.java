package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.InformeDTO;
import es.aaracubel.paicasso.backend.entities.Analisis;
import es.aaracubel.paicasso.backend.entities.Informe;
import es.aaracubel.paicasso.backend.repositories.AnalisisRepository;
import es.aaracubel.paicasso.backend.repositories.InformeRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LLMServiceTest {

    @Mock
    private PromptBuilderService promptBuilderService;

    @Mock
    private InformeRepository informeRepository;

    @Mock
    private AnalisisRepository analisisRepository;

    @InjectMocks
    private LLMService llmService;

    @Test
    void analizar_Exito() {
        ReflectionTestUtils.setField(llmService, "apiUrl", "http://mockapi");
        ReflectionTestUtils.setField(llmService, "apiKey", "mockKey");

        when(promptBuilderService.construirPrompt(1L, "sonar", "msg")).thenReturn("Prompt de prueba");

        String mockResponse = """
                {
                    "candidates": [{
                        "content": {
                            "parts": [{
                                "text": "Respuesta generada"
                            }]
                        }
                    }]
                }
                """;

        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class,
                (mockRestTemplate, context) -> {
                    when(mockRestTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                            .thenReturn(ResponseEntity.ok(mockResponse));
                })) {

            String resultado = llmService.analizar(1L, "sonar", "msg");
            assertEquals("Respuesta generada", resultado);
        }
    }

    @Test
    void analizar_Fallo() {
        when(promptBuilderService.construirPrompt(1L, "sonar", "msg")).thenReturn("Prompt de prueba");

        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class,
                (mockRestTemplate, context) -> {
                    when(mockRestTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                            .thenThrow(new RuntimeException("API caida"));
                })) {

            String resultado = llmService.analizar(1L, "sonar", "msg");
            assertEquals("No se pudo obtener respuesta de la IA en este momento.", resultado);
        }
    }

    @Test
    void generarInforme() {
        ReflectionTestUtils.setField(llmService, "apiUrl", "http://mockapi");
        ReflectionTestUtils.setField(llmService, "apiKey", "mockKey");

        when(promptBuilderService.construirPromptDiagnostico("sonar")).thenReturn("Prompt diagnostico");
        when(promptBuilderService.construirPromptPropuesta("sonar")).thenReturn("Prompt propuesta");

        Analisis analisis = new Analisis();
        analisis.setId(10L);
        when(analisisRepository.findFirstByRepositorioIdOrderByFechaEjecucionDesc(1L))
                .thenReturn(Optional.of(analisis));

        Informe informe = new Informe();
        when(informeRepository.findByAnalisisId(10L)).thenReturn(Optional.of(informe));

        String mockResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"Texto IA\"}]}}]}";

        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class,
                (mockRestTemplate, context) -> {
                    when(mockRestTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                            .thenReturn(ResponseEntity.ok(mockResponse));
                })) {

            InformeDTO resultado = llmService.generarInforme(1L, "sonar");

            assertNotNull(resultado);
            assertEquals("Texto IA", resultado.getDiagnostico());
            assertEquals("Texto IA", resultado.getPropuesta());
            assertEquals(10L, informe.getAnalisis().getId());
        }
    }
}
