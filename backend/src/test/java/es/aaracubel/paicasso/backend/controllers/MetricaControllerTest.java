package es.aaracubel.paicasso.backend.controllers;

import es.aaracubel.paicasso.backend.dtos.MetricaDTO;
import es.aaracubel.paicasso.backend.services.MetricaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MetricaController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "app.frontend.url=http://localhost:5173",
        "app.jwt.secret=PaicassoSuperSecretKeyParaFirmaDeTokens1234567890!!"
})
class MetricaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MetricaService metricaService;

    @Test
    void obtenerIncidenciasPorRepo_Exito_DevuelveOk() throws Exception {
        MetricaDTO metrica = MetricaDTO.builder()
                .id(1L)
                .archivo("src/Main.java")
                .linea(15)
                .severidad("CRITICAL")
                .descripcion("Posible NPE")
                .tipo("BUG")
                .build();
        when(metricaService.obtenerIncidenciasPorRepoId(1L)).thenReturn(List.of(metrica));

        mockMvc.perform(get("/api/incidencias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].archivo").value("src/Main.java"))
                .andExpect(jsonPath("$[0].severidad").value("CRITICAL"))
                .andExpect(jsonPath("$[0].tipo").value("BUG"));

        verify(metricaService).obtenerIncidenciasPorRepoId(1L);
    }

    @Test
    void obtenerIncidenciasPorRepo_Error_DevuelveNotFound() throws Exception {
        when(metricaService.obtenerIncidenciasPorRepoId(99L))
                .thenThrow(new RuntimeException("No se encontró análisis"));

        mockMvc.perform(get("/api/incidencias/99"))
                .andExpect(status().isNotFound());

        verify(metricaService).obtenerIncidenciasPorRepoId(99L);
    }

    @Test
    void obtenerCodigoIncidencia_Exito_DevuelveOk() throws Exception {
        when(metricaService.obtenerCodigoDeIncidencia(5L)).thenReturn("public class Main {}");

        mockMvc.perform(get("/api/incidencias/5/codigo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("public class Main {}"));

        verify(metricaService).obtenerCodigoDeIncidencia(5L);
    }

    @Test
    void obtenerCodigoIncidencia_Error_DevuelveNotFound() throws Exception {
        when(metricaService.obtenerCodigoDeIncidencia(404L))
                .thenThrow(new RuntimeException("Incidencia no encontrada"));

        mockMvc.perform(get("/api/incidencias/404/codigo"))
                .andExpect(status().isNotFound());

        verify(metricaService).obtenerCodigoDeIncidencia(404L);
    }
}
