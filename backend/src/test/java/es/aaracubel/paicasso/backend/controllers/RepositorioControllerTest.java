package es.aaracubel.paicasso.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.aaracubel.paicasso.backend.dtos.RepositorioDTO;
import es.aaracubel.paicasso.backend.services.RepositorioService;
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

@WebMvcTest(controllers = RepositorioController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "app.frontend.url=http://localhost:5173",
        "app.jwt.secret=PaicassoSuperSecretKeyParaFirmaDeTokens1234567890!!"
})
class RepositorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
    void obtenerRepositorios_DevuelveListaVinculados() throws Exception {
        RepositorioDTO repo = RepositorioDTO.builder()
                .id(1L)
                .nombre("paicasso-backend")
                .url("https://github.com/aaron/paicasso-backend")
                .lenguajePrincipal("Java")
                .vinculado(true)
                .build();
        when(repositorioService.obtenerRepositoriosVinculados(1L)).thenReturn(List.of(repo));

        mockMvc.perform(get("/api/repositorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("paicasso-backend"))
                .andExpect(jsonPath("$[0].lenguajePrincipal").value("Java"))
                .andExpect(jsonPath("$[0].vinculado").value(true));

        verify(repositorioService).obtenerRepositoriosVinculados(1L);
    }

    @Test
    void obtenerRepositoriosDeGitHub_DevuelveLista() throws Exception {
        RepositorioDTO repo = RepositorioDTO.builder()
                .id(null)
                .nombre("repo-publico")
                .url("https://github.com/aaron/repo-publico")
                .lenguajePrincipal("Python")
                .vinculado(false)
                .build();
        when(repositorioService.buscarReposEnGitHub(1L)).thenReturn(List.of(repo));

        mockMvc.perform(get("/api/repositorios/github"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("repo-publico"))
                .andExpect(jsonPath("$[0].lenguajePrincipal").value("Python"))
                .andExpect(jsonPath("$[0].vinculado").value(false));

        verify(repositorioService).buscarReposEnGitHub(1L);
    }

    @Test
    void vincularRepositorio_DevuelveRepoVinculado() throws Exception {
        RepositorioDTO entrada = RepositorioDTO.builder()
                .nombre("nuevo-repo")
                .url("https://github.com/aaron/nuevo-repo")
                .lenguajePrincipal("TypeScript")
                .vinculado(false)
                .build();

        RepositorioDTO guardado = RepositorioDTO.builder()
                .id(50L)
                .nombre("nuevo-repo")
                .url("https://github.com/aaron/nuevo-repo")
                .lenguajePrincipal("TypeScript")
                .vinculado(true)
                .build();

        when(repositorioService.vincularRepositorio(eq(1L), any(RepositorioDTO.class)))
                .thenReturn(guardado);

        mockMvc.perform(post("/api/repositorios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.nombre").value("nuevo-repo"))
                .andExpect(jsonPath("$.vinculado").value(true));

        verify(repositorioService).vincularRepositorio(eq(1L), any(RepositorioDTO.class));
    }

    @Test
    void obtenerRepositorio_DevuelveRepo() throws Exception {
        RepositorioDTO repo = RepositorioDTO.builder()
                .id(7L)
                .nombre("repo-7")
                .url("https://github.com/aaron/repo-7")
                .lenguajePrincipal("Go")
                .vinculado(true)
                .build();
        when(repositorioService.obtenerRepositorio(7L)).thenReturn(repo);

        mockMvc.perform(get("/api/repositorios/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.nombre").value("repo-7"))
                .andExpect(jsonPath("$.lenguajePrincipal").value("Go"));

        verify(repositorioService).obtenerRepositorio(7L);
    }

    @Test
    void desvincularRepositorio_DevuelveNoContent() throws Exception {
        doNothing().when(repositorioService).desvincularRepositorio(1L, 7L);

        mockMvc.perform(delete("/api/repositorios/7"))
                .andExpect(status().isNoContent());

        verify(repositorioService).desvincularRepositorio(1L, 7L);
    }
}
