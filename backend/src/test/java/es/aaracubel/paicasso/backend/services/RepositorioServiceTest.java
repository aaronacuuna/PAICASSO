package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.RepositorioDTO;
import es.aaracubel.paicasso.backend.entities.Repositorio;
import es.aaracubel.paicasso.backend.entities.Usuario;
import es.aaracubel.paicasso.backend.mappers.RepositorioMapper;
import es.aaracubel.paicasso.backend.repositories.RepositorioRepository;
import es.aaracubel.paicasso.backend.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepositorioServiceTest {

    @Mock
    private RepositorioRepository repositorioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RepositorioMapper repositorioMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RepositorioService repositorioService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(repositorioService, "restTemplate", restTemplate);
    }

    @Test
    void obtenerRepositoriosVinculados() {
        Repositorio repo = new Repositorio();
        RepositorioDTO dto = new RepositorioDTO();
        when(repositorioRepository.findByUsuarioId(1L)).thenReturn(List.of(repo));
        when(repositorioMapper.toDTO(repo, true)).thenReturn(dto);

        List<RepositorioDTO> resultado = repositorioService.obtenerRepositoriosVinculados(1L);

        assertFalse(resultado.isEmpty());
        verify(repositorioRepository).findByUsuarioId(1L);
    }

    @Test
    void buscarReposEnGitHub_ExitoConLenguajeNulo() {
        Usuario usuario = new Usuario();
        usuario.setTokenAcceso("token");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repositorioRepository.findByUsuarioId(1L)).thenReturn(List.of());

        Map<String, String> repoMap1 = Map.of(
                "name", "MiRepo",
                "html_url", "http://github.com/MiRepo",
                "language", "Java");

        Map<String, String> repoMap2 = Map.of(
                "name", "RepoSinLenguaje",
                "html_url", "http://github.com/RepoSinLenguaje"); // No language key

        Map[] responseBody = new Map[] { repoMap1, repoMap2 };
        ResponseEntity<Map[]> responseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map[].class)))
                .thenReturn(responseEntity);

        List<RepositorioDTO> resultado = repositorioService.buscarReposEnGitHub(1L);

        assertEquals(2, resultado.size());
        assertEquals("MiRepo", resultado.get(0).getNombre());
        assertEquals("Java", resultado.get(0).getLenguajePrincipal());

        assertEquals("RepoSinLenguaje", resultado.get(1).getNombre());
        assertEquals("Desconocido", resultado.get(1).getLenguajePrincipal()); // Prueba ternario nulo
    }

    @Test
    void buscarReposEnGitHub_CuerpoVacio() {
        Usuario usuario = new Usuario();
        usuario.setTokenAcceso("token");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repositorioRepository.findByUsuarioId(1L)).thenReturn(List.of());

        ResponseEntity<Map[]> responseEntity = ResponseEntity.ok(null);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map[].class)))
                .thenReturn(responseEntity);

        List<RepositorioDTO> resultado = repositorioService.buscarReposEnGitHub(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarReposEnGitHub_UsuarioNoEncontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> repositorioService.buscarReposEnGitHub(1L));
    }

    @Test
    void vincularRepositorio_UsuarioNoEncontrado() {
        RepositorioDTO dto = new RepositorioDTO();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> repositorioService.vincularRepositorio(1L, dto));
    }

    @Test
    void vincularRepositorio_YaExiste() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Repositorio repoExistente = new Repositorio();
        repoExistente.setUrl("url_existente");
        when(repositorioRepository.findByUsuarioId(1L)).thenReturn(List.of(repoExistente));

        RepositorioDTO dto = new RepositorioDTO();
        dto.setUrl("url_existente");

        assertThrows(RuntimeException.class, () -> repositorioService.vincularRepositorio(1L, dto));
    }

    @Test
    void vincularRepositorio_Nuevo() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repositorioRepository.findByUsuarioId(1L)).thenReturn(List.of());

        Repositorio nuevoRepo = new Repositorio();
        Repositorio guardado = new Repositorio();
        RepositorioDTO dto = new RepositorioDTO();
        dto.setUrl("nueva_url");
        RepositorioDTO resultadoDto = new RepositorioDTO();

        when(repositorioMapper.toEntity(dto)).thenReturn(nuevoRepo);
        when(repositorioRepository.save(nuevoRepo)).thenReturn(guardado);
        when(repositorioMapper.toDTO(guardado, true)).thenReturn(resultadoDto);

        RepositorioDTO resultado = repositorioService.vincularRepositorio(1L, dto);

        assertNotNull(resultado);
        verify(repositorioRepository).save(nuevoRepo);
    }

    @Test
    void obtenerRepositorio_Exito() {
        Repositorio repo = new Repositorio();
        RepositorioDTO dto = new RepositorioDTO();

        when(repositorioRepository.findById(1L)).thenReturn(Optional.of(repo));
        when(repositorioMapper.toDTO(repo, true)).thenReturn(dto);

        RepositorioDTO resultado = repositorioService.obtenerRepositorio(1L);

        assertNotNull(resultado);
        verify(repositorioRepository).findById(1L);
    }

    @Test
    void obtenerRepositorio_NoEncontrado() {
        when(repositorioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> repositorioService.obtenerRepositorio(1L));
    }

    @Test
    void desvincularRepositorio_Exito() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Repositorio repo = new Repositorio();
        repo.setId(10L);
        repo.setUsuario(usuario);

        when(repositorioRepository.findById(10L)).thenReturn(Optional.of(repo));

        repositorioService.desvincularRepositorio(1L, 10L);

        verify(repositorioRepository).delete(repo);
    }

    @Test
    void desvincularRepositorio_NoEncontrado() {
        when(repositorioRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> repositorioService.desvincularRepositorio(1L, 10L));
        assertEquals("Repositorio no encontrado", exception.getMessage());
        verify(repositorioRepository, never()).delete(any());
    }

    @Test
    void desvincularRepositorio_SinPermisos() {
        Usuario usuario = new Usuario();
        usuario.setId(2L); // Usuario diferente

        Repositorio repo = new Repositorio();
        repo.setId(10L);
        repo.setUsuario(usuario);

        when(repositorioRepository.findById(10L)).thenReturn(Optional.of(repo));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> repositorioService.desvincularRepositorio(1L, 10L));
        assertEquals("No tienes permisos para desvincular este repositorio", exception.getMessage());
        verify(repositorioRepository, never()).delete(any());
    }
}
