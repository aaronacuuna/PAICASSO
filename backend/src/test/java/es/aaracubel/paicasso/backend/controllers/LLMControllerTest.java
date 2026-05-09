package es.aaracubel.paicasso.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.aaracubel.paicasso.backend.dtos.InformeDTO;
import es.aaracubel.paicasso.backend.dtos.LLMRequestDTO;
import es.aaracubel.paicasso.backend.entities.Mensaje;
import es.aaracubel.paicasso.backend.entities.SesionChat;
import es.aaracubel.paicasso.backend.services.ChatService;
import es.aaracubel.paicasso.backend.services.InformeService;
import es.aaracubel.paicasso.backend.services.LLMService;
import es.aaracubel.paicasso.backend.services.SonarService;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LLMController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "app.frontend.url=http://localhost:5173",
        "app.jwt.secret=PaicassoSuperSecretKeyParaFirmaDeTokens1234567890!!"
})
class LLMControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private LLMService llmService;

    @MockitoBean
    private SonarService sonarService;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private InformeService informeService;

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
    void analizar_DevuelveRespuestaConSesionId() throws Exception {
        LLMRequestDTO request = LLMRequestDTO.builder()
                .repoId(5L)
                .componentKey("comp:key")
                .lineaError(42)
                .mensaje("¿Por qué falla esta línea?")
                .build();

        SesionChat sesion = new SesionChat();
        sesion.setId(99L);

        when(chatService.obtenerSesion(5L)).thenReturn(sesion);
        when(chatService.obtenerMensajes(99L)).thenReturn(Collections.emptyList());
        when(sonarService.construirContextoSonar(5L, "comp:key", 42)).thenReturn("contexto");
        when(llmService.analizar(eq(1L), eq("contexto"), eq("¿Por qué falla esta línea?"), anyList()))
                .thenReturn("respuesta-LLM");

        mockMvc.perform(post("/api/llm/analizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sesionId").value(99))
                .andExpect(jsonPath("$.respuesta").value("respuesta-LLM"));

        verify(chatService).guardarMensaje(sesion, "¿Por qué falla esta línea?", "usuario");
        verify(chatService).guardarMensaje(sesion, "respuesta-LLM", "LLM");
        verify(llmService).analizar(eq(1L), eq("contexto"), eq("¿Por qué falla esta línea?"), anyList());
    }

    @Test
    void obtenerMensajes_DevuelveSesionYMensajes() throws Exception {
        SesionChat sesion = new SesionChat();
        sesion.setId(7L);

        Mensaje mensaje = new Mensaje();
        mensaje.setId(1L);
        mensaje.setContenido("Hola");
        mensaje.setRemitente("usuario");
        mensaje.setTimestamp(LocalDateTime.now());

        when(chatService.obtenerSesion(3L)).thenReturn(sesion);
        when(chatService.obtenerMensajes(7L)).thenReturn(List.of(mensaje));

        mockMvc.perform(get("/api/llm/sesion/3/mensajes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sesionId").value(7))
                .andExpect(jsonPath("$.mensajes[0].contenido").value("Hola"))
                .andExpect(jsonPath("$.mensajes[0].remitente").value("usuario"));

        verify(chatService).obtenerSesion(3L);
        verify(chatService).obtenerMensajes(7L);
    }

    @Test
    void obtenerInforme_Existente_DevuelveInforme() throws Exception {
        InformeDTO informe = InformeDTO.builder()
                .id(11L)
                .fechaGeneracion(LocalDateTime.now())
                .diagnostico("Diagnóstico OK")
                .propuesta("Propuesta de mejora")
                .build();

        when(informeService.obtenerInforme(2L)).thenReturn(informe);

        mockMvc.perform(get("/api/llm/informe/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.diagnostico").value("Diagnóstico OK"))
                .andExpect(jsonPath("$.propuesta").value("Propuesta de mejora"));

        verify(informeService).obtenerInforme(2L);
        verify(llmService, never()).generarInforme(anyLong(), anyString());
    }

    @Test
    void obtenerInforme_NoExistente_GeneraNuevo() throws Exception {
        InformeDTO informe = InformeDTO.builder()
                .id(20L)
                .fechaGeneracion(LocalDateTime.now())
                .diagnostico("Generado")
                .propuesta("Propuesta")
                .build();

        when(informeService.obtenerInforme(4L)).thenReturn(null);
        when(sonarService.construirContextoSonar(4L, null, null)).thenReturn("contexto-sonar");
        when(llmService.generarInforme(4L, "contexto-sonar")).thenReturn(informe);

        mockMvc.perform(get("/api/llm/informe/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.diagnostico").value("Generado"));

        verify(informeService).obtenerInforme(4L);
        verify(sonarService).construirContextoSonar(4L, null, null);
        verify(llmService).generarInforme(4L, "contexto-sonar");
    }
}
