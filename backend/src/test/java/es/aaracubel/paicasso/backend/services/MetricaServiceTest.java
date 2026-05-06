package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.MetricaDTO;
import es.aaracubel.paicasso.backend.entities.Analisis;
import es.aaracubel.paicasso.backend.entities.Metrica;
import es.aaracubel.paicasso.backend.entities.Repositorio;
import es.aaracubel.paicasso.backend.mappers.MetricaMapper;
import es.aaracubel.paicasso.backend.repositories.AnalisisRepository;
import es.aaracubel.paicasso.backend.repositories.MetricaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MetricaServiceTest {

    @Mock
    private MetricaRepository metricaRepository;

    @Mock
    private AnalisisRepository analisisRepository;

    @Mock
    private MetricaMapper metricaMapper;

    @Mock
    private SonarService sonarService;

    @InjectMocks
    private MetricaService metricaService;

    @Test
    void obtenerIncidenciasPorRepoId() {
        Analisis analisis = new Analisis();
        analisis.setId(10L);
        when(analisisRepository.findFirstByRepositorioIdOrderByFechaEjecucionDesc(1L))
                .thenReturn(Optional.of(analisis));

        Metrica metrica = new Metrica();
        when(metricaRepository.findByAnalisisId(10L)).thenReturn(List.of(metrica));

        MetricaDTO dto = new MetricaDTO();
        when(metricaMapper.toDTO(metrica)).thenReturn(dto);

        List<MetricaDTO> resultado = metricaService.obtenerIncidenciasPorRepoId(1L);

        assertFalse(resultado.isEmpty());
        verify(analisisRepository).findFirstByRepositorioIdOrderByFechaEjecucionDesc(1L);
    }

    @Test
    void obtenerCodigoDeIncidencia() {
        Repositorio repo = new Repositorio();
        repo.setId(5L);
        Analisis analisis = new Analisis();
        analisis.setRepositorio(repo);

        Metrica incidencia = new Metrica();
        incidencia.setAnalisis(analisis);
        incidencia.setArchivo("src/main/Main.java");
        incidencia.setLinea(20);

        when(metricaRepository.findById(1L)).thenReturn(Optional.of(incidencia));
        when(sonarService.obtenerContextoCodigo("paicasso_5:src/main/Main.java", 20))
                .thenReturn("public void test() {}");

        String resultado = metricaService.obtenerCodigoDeIncidencia(1L);

        assertEquals("public void test() {}", resultado);
    }
}
