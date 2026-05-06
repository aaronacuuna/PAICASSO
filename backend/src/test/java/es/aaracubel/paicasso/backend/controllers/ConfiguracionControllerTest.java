package es.aaracubel.paicasso.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.aaracubel.paicasso.backend.dtos.ConfiguracionDTO;
import es.aaracubel.paicasso.backend.services.ConfiguracionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ConfiguracionController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "app.frontend.url=http://localhost:5173",
        "app.jwt.secret=PaicassoSuperSecretKeyParaFirmaDeTokens1234567890!!"
})
class ConfiguracionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ConfiguracionService configuracionService;

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
    void getConfiguracion_DevuelveOk() throws Exception {
        ConfiguracionDTO dto = ConfiguracionDTO.builder()
                .experienceLevel("INTERMEDIO")
                .priorities(List.of("seguridad", "rendimiento"))
                .addComments(true)
                .build();
        when(configuracionService.obtenerConfiguracion(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/configuracion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.experienceLevel").value("INTERMEDIO"))
                .andExpect(jsonPath("$.priorities[0]").value("seguridad"))
                .andExpect(jsonPath("$.addComments").value(true));

        verify(configuracionService).obtenerConfiguracion(1L);
    }

    @Test
    void guardarConfiguracion_DevuelveOk() throws Exception {
        ConfiguracionDTO dto = ConfiguracionDTO.builder()
                .experienceLevel("EXPERTO")
                .priorities(List.of("legibilidad"))
                .addComments(false)
                .build();
        when(configuracionService.guardarConfiguracion(eq(1L), any(ConfiguracionDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/configuracion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.experienceLevel").value("EXPERTO"))
                .andExpect(jsonPath("$.priorities[0]").value("legibilidad"))
                .andExpect(jsonPath("$.addComments").value(false));

        verify(configuracionService).guardarConfiguracion(eq(1L), any(ConfiguracionDTO.class));
    }
}
