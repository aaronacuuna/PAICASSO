package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.AnalisisDTO;
import es.aaracubel.paicasso.backend.entities.Analisis;
import es.aaracubel.paicasso.backend.entities.EstadoAnalisis;
import es.aaracubel.paicasso.backend.entities.Metrica;
import es.aaracubel.paicasso.backend.entities.Repositorio;
import es.aaracubel.paicasso.backend.entities.Usuario;
import es.aaracubel.paicasso.backend.mappers.AnalisisMapper;
import es.aaracubel.paicasso.backend.repositories.AnalisisRepository;
import es.aaracubel.paicasso.backend.repositories.MetricaRepository;
import es.aaracubel.paicasso.backend.repositories.RepositorioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AnalisisServiceTest {

    @Mock
    private AnalisisRepository analisisRepository;

    @Mock
    private RepositorioRepository repositorioRepository;

    @Mock
    private MetricaRepository metricaRepository;

    @Mock
    private AnalisisMapper analisisMapper;

    @Mock
    private SonarService sonarService;

    @InjectMocks
    private AnalisisService analisisService;

    @Test
    void obtenerUltimoAnalisis_Exito() {
        Analisis analisis = new Analisis();
        AnalisisDTO dto = new AnalisisDTO();
        when(analisisRepository.findFirstByRepositorioIdOrderByFechaEjecucionDesc(1L))
                .thenReturn(Optional.of(analisis));
        when(analisisMapper.toDTO(analisis)).thenReturn(dto);

        AnalisisDTO resultado = analisisService.obtenerUltimoAnalisis(1L);

        assertNotNull(resultado);
        verify(analisisRepository).findFirstByRepositorioIdOrderByFechaEjecucionDesc(1L);
    }

    @Test
    void obtenerUltimoAnalisis_NoEncontrado() {
        when(analisisRepository.findFirstByRepositorioIdOrderByFechaEjecucionDesc(1L)).thenReturn(Optional.empty());

        AnalisisDTO resultado = analisisService.obtenerUltimoAnalisis(1L);

        assertNull(resultado);
    }

    @Test
    void iniciarAnalisis_CreaNuevoAnalisis() {
        Repositorio repo = new Repositorio();
        repo.setId(1L);
        when(repositorioRepository.findById(1L)).thenReturn(Optional.of(repo));

        Analisis guardado = new Analisis();
        guardado.setEstado(EstadoAnalisis.EN_PROGRESO);

        when(analisisRepository.save(any(Analisis.class))).thenReturn(guardado);

        AnalisisDTO dtoEsperado = new AnalisisDTO();
        dtoEsperado.setEstado(EstadoAnalisis.EN_PROGRESO);
        when(analisisMapper.toDTO(guardado)).thenReturn(dtoEsperado);

        AnalisisDTO resultado = analisisService.iniciarAnalisis(1L);

        assertNotNull(resultado);
        assertEquals(EstadoAnalisis.EN_PROGRESO, resultado.getEstado());
        verify(analisisRepository).save(any(Analisis.class));
    }

    @Test
    void ejecutarAnalisisEnSegundoPlano_Exito() throws Exception {
        ReflectionTestUtils.setField(analisisService, "sonarUrl", "http://sonar");
        ReflectionTestUtils.setField(analisisService, "sonarToken", "token");

        Repositorio repo = mock(Repositorio.class);
        Usuario usuario = mock(Usuario.class);
        when(repo.getId()).thenReturn(1L);
        when(repo.getNombre()).thenReturn("TestRepo");
        when(repo.getUrl()).thenReturn("https://github.com/paicasso/repo");
        when(repo.getUsuario()).thenReturn(usuario);
        when(usuario.getTokenAcceso()).thenReturn("token123");

        Analisis analisis = new Analisis();
        analisis.setId(10L);

        when(repositorioRepository.findById(1L)).thenReturn(Optional.of(repo));
        when(analisisRepository.findById(10L)).thenReturn(Optional.of(analisis));
        when(sonarService.procesamientoCompletado(anyString())).thenReturn(true);
        when(sonarService.obtenerMetricas(anyString())).thenReturn(new AnalisisDTO());
        when(sonarService.obtenerDetalleIncidencias(anyString(), any(Analisis.class)))
                .thenReturn(List.of(new Metrica()));

        try (MockedConstruction<ProcessBuilder> pbMock = Mockito.mockConstruction(ProcessBuilder.class,
                (mockPb, context) -> {
                    Process processMock = mock(Process.class);
                    lenient().when(processMock.waitFor()).thenReturn(0);
                    lenient().when(processMock.getInputStream()).thenReturn(new ByteArrayInputStream("log".getBytes()));
                    lenient().when(mockPb.start()).thenReturn(processMock);
                    lenient().when(mockPb.environment()).thenReturn(new HashMap<>());
                })) {

            analisisService.ejecutarAnalisisEnSegundoPlano(10L, 1L);
        }

        verify(metricaRepository).saveAll(any());
        verify(analisisRepository, times(1)).save(argThat(a -> a.getEstado() == EstadoAnalisis.COMPLETADO));
    }

    @Test
    void ejecutarAnalisisEnSegundoPlano_FalloGit() throws Exception {
        Repositorio repo = mock(Repositorio.class);
        Usuario usuario = mock(Usuario.class);
        when(repo.getId()).thenReturn(1L);
        when(repo.getUrl()).thenReturn("https://github.com/paicasso/repo");
        when(repo.getUsuario()).thenReturn(usuario);
        when(usuario.getTokenAcceso()).thenReturn("token123");

        Analisis analisis = new Analisis();
        analisis.setId(10L);

        when(repositorioRepository.findById(1L)).thenReturn(Optional.of(repo));
        when(analisisRepository.findById(10L)).thenReturn(Optional.of(analisis));

        try (MockedConstruction<ProcessBuilder> pbMock = Mockito.mockConstruction(ProcessBuilder.class,
                (mockPb, context) -> {
                    Process processMock = mock(Process.class);
                    lenient().when(processMock.waitFor()).thenReturn(1);
                    lenient().when(processMock.getInputStream())
                            .thenReturn(new ByteArrayInputStream("error git".getBytes()));
                    lenient().when(mockPb.start()).thenReturn(processMock);
                    lenient().when(mockPb.environment()).thenReturn(new HashMap<>());
                })) {

            analisisService.ejecutarAnalisisEnSegundoPlano(10L, 1L);
        }

        verify(analisisRepository).save(argThat(a -> a.getEstado() == EstadoAnalisis.ERROR));
    }

    @Test
    void ejecutarAnalisisEnSegundoPlano_FalloSonar() throws Exception {
        ReflectionTestUtils.setField(analisisService, "sonarUrl", "http://sonar");
        ReflectionTestUtils.setField(analisisService, "sonarToken", "token");

        Repositorio repo = mock(Repositorio.class);
        Usuario usuario = mock(Usuario.class);
        when(repo.getId()).thenReturn(1L);
        when(repo.getNombre()).thenReturn("TestRepo");
        when(repo.getUrl()).thenReturn("https://github.com/paicasso/repo");
        when(repo.getUsuario()).thenReturn(usuario);
        when(usuario.getTokenAcceso()).thenReturn("token123");

        Analisis analisis = new Analisis();
        analisis.setId(10L);

        when(repositorioRepository.findById(1L)).thenReturn(Optional.of(repo));
        when(analisisRepository.findById(10L)).thenReturn(Optional.of(analisis));

        int[] callCount = { 0 };

        try (MockedConstruction<ProcessBuilder> pbMock = Mockito.mockConstruction(ProcessBuilder.class,
                (mockPb, context) -> {
                    Process processMock = mock(Process.class);
                    lenient().when(processMock.waitFor()).thenAnswer(inv -> {
                        callCount[0]++;
                        return callCount[0] == 1 ? 0 : 1;
                    });
                    lenient().when(processMock.getInputStream()).thenReturn(new ByteArrayInputStream("log".getBytes()));
                    lenient().when(mockPb.start()).thenReturn(processMock);
                    lenient().when(mockPb.environment()).thenReturn(new HashMap<>());
                })) {

            analisisService.ejecutarAnalisisEnSegundoPlano(10L, 1L);
        }

        verify(analisisRepository).save(argThat(a -> a.getEstado() == EstadoAnalisis.ERROR));
    }

    @Test
    void borrarDirectorio_Exito() throws Exception {
        Path tempDir = Files.createTempDirectory("test-borrar");
        Path subFile = tempDir.resolve("test.txt");
        Files.createFile(subFile);

        ReflectionTestUtils.invokeMethod(analisisService, "borrarDirectorio", tempDir);

        assertFalse(Files.exists(tempDir));
    }
}
