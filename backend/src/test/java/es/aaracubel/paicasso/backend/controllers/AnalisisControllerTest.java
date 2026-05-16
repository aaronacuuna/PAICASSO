package es.aaracubel.paicasso.backend.controllers;

import es.aaracubel.paicasso.backend.dtos.AnalisisDTO;
import es.aaracubel.paicasso.backend.entities.EstadoAnalisis;
import es.aaracubel.paicasso.backend.services.AnalisisService;
import es.aaracubel.paicasso.backend.services.RepositorioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AnalisisController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "app.frontend.url=http://localhost:5173",
        "app.jwt.secret=PaicassoSuperSecretKeyParaFirmaDeTokens1234567890!!"
})
class AnalisisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalisisService analisisService;

    @MockitoBean
    private RepositorioService repositorioService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUltimoAnalisis_Existente_DevuelveOk() throws Exception {
        AnalisisDTO dto = AnalisisDTO.builder()
                .id(1L)
                .estado(EstadoAnalisis.COMPLETADO)
                .fechaEjecucion(LocalDateTime.now())
                .bugs(2)
                .vulnerabilidades(1)
                .codeSmells(5)
                .lineasCodigo(1000)
                .cobertura(null)
                .duplicaciones(5.0)
                .build();
        when(analisisService.obtenerUltimoAnalisis(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/repositorios/1/analisis/ultimo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"))
                .andExpect(jsonPath("$.bugs").value(2));

        verify(analisisService).obtenerUltimoAnalisis(1L);
    }

    @Test
    void getUltimoAnalisis_Inexistente_DevuelveNoContent() throws Exception {
        when(analisisService.obtenerUltimoAnalisis(99L)).thenReturn(null);

        mockMvc.perform(get("/api/repositorios/99/analisis/ultimo"))
                .andExpect(status().isNoContent());

        verify(analisisService).obtenerUltimoAnalisis(99L);
    }

    @Test
    void analizarRepositorio_Exito_DevuelveAccepted() throws Exception {
        AnalisisDTO dto = AnalisisDTO.builder()
                .id(10L)
                .estado(EstadoAnalisis.EN_PROGRESO)
                .build();
        when(analisisService.iniciarAnalisis(1L)).thenReturn(dto);

        mockMvc.perform(post("/api/repositorios/1/analizar"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.estado").value("EN_PROGRESO"));

        verify(analisisService).iniciarAnalisis(1L);
        verify(analisisService).ejecutarAnalisisEnSegundoPlano(10L, 1L);
    }
}
