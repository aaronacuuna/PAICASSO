package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.ConfiguracionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromptBuilderServiceTest {

    @Mock
    private ConfiguracionService configuracionService;

    @InjectMocks
    private PromptBuilderService promptBuilderService;

    @Test
    void construirPrompt_Junior_ConPrioridadesYComentarios() {
        ConfiguracionDTO config = new ConfiguracionDTO();
        config.setExperienceLevel("junior");
        config.setPriorities(List.of("security", "readability", "performance", "other"));
        config.setAddComments(true);

        when(configuracionService.obtenerConfiguracion(1L)).thenReturn(config);

        String prompt = promptBuilderService.construirPrompt(1L, "Contexto Sonar", "Mi mensaje", Collections.emptyList());

        assertNotNull(prompt);
        assertTrue(prompt.contains("junior"));
        assertTrue(prompt.contains("Explica cada concepto paso a paso"));
        assertTrue(prompt.contains("Seguridad"));
        assertTrue(prompt.contains("Legibilidad"));
        assertTrue(prompt.contains("Rendimiento"));
        assertTrue(prompt.contains("other"));
        assertTrue(prompt.contains("Añade comentarios explicativos"));
        assertTrue(prompt.contains("Mi mensaje"));
    }

    @Test
    void construirPrompt_Mid_SinComentariosYPrioridadesVacias() {
        ConfiguracionDTO config = new ConfiguracionDTO();
        config.setExperienceLevel("mid");
        config.setPriorities(Collections.emptyList());
        config.setAddComments(false);

        when(configuracionService.obtenerConfiguracion(1L)).thenReturn(config);

        String prompt = promptBuilderService.construirPrompt(1L, "Contexto Sonar", "Otro mensaje", Collections.emptyList());

        assertNotNull(prompt);
        assertTrue(prompt.contains("mid"));
        assertTrue(prompt.contains("Ve directo al grano pero incluye contexto técnico relevante"));
        assertTrue(prompt.contains("No añadas comentarios en el código generado"));
        assertTrue(prompt.contains("Otro mensaje"));
    }

    @Test
    void construirPrompt_Senior() {
        ConfiguracionDTO config = new ConfiguracionDTO();
        config.setExperienceLevel("senior");
        config.setPriorities(List.of("security"));
        config.setAddComments(false);

        when(configuracionService.obtenerConfiguracion(1L)).thenReturn(config);

        String prompt = promptBuilderService.construirPrompt(1L, "Contexto Sonar", "Senior mensaje", Collections.emptyList());

        assertNotNull(prompt);
        assertTrue(prompt.contains("senior"));
        assertTrue(prompt.contains("Responde de forma concisa y técnica, sin explicaciones básicas"));
        assertTrue(prompt.contains("Senior mensaje"));
    }

    @Test
    void construirPrompt_Default() {
        ConfiguracionDTO config = new ConfiguracionDTO();
        config.setExperienceLevel("unknown");
        config.setPriorities(List.of());
        config.setAddComments(true);

        when(configuracionService.obtenerConfiguracion(1L)).thenReturn(config);

        String prompt = promptBuilderService.construirPrompt(1L, "Context", "Def mensaje", Collections.emptyList());

        assertNotNull(prompt);
        assertTrue(prompt.contains("Responde de forma clara y técnica."));
        assertTrue(prompt.contains("Def mensaje"));
    }

    @Test
    void construirPrompt_ConfiguracionNula() {
        ConfiguracionDTO config = new ConfiguracionDTO();
        config.setExperienceLevel(null);
        config.setPriorities(null);
        config.setAddComments(false);

        when(configuracionService.obtenerConfiguracion(1L)).thenReturn(config);

        String prompt = promptBuilderService.construirPrompt(1L, "Context", "Mensaje sin config", Collections.emptyList());

        assertNotNull(prompt);
        assertTrue(prompt.contains("Responde de forma clara y técnica."));
        assertTrue(prompt.contains("Mensaje sin config"));
    }

    @Test
    void construirPromptDiagnostico() {
        String prompt = promptBuilderService.construirPromptDiagnostico("Contexto Diagnostico");

        assertNotNull(prompt);
        assertTrue(prompt.contains("Contexto Diagnostico"));
        assertTrue(prompt.contains("PAICASSO"));
    }

    @Test
    void construirPromptPropuesta() {
        String prompt = promptBuilderService.construirPromptPropuesta("Contexto Propuesta");

        assertNotNull(prompt);
        assertTrue(prompt.contains("Contexto Propuesta"));
        assertTrue(prompt.contains("plan de mejora"));
    }
}
