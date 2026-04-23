package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.MetricaDTO;
import es.aaracubel.paicasso.backend.entities.Analisis;
import es.aaracubel.paicasso.backend.entities.Metrica;
import es.aaracubel.paicasso.backend.mappers.MetricaMapper;
import es.aaracubel.paicasso.backend.repositories.AnalisisRepository;
import es.aaracubel.paicasso.backend.repositories.MetricaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricaService {

    private final MetricaRepository metricaRepository;
    private final AnalisisRepository analisisRepository;
    private final MetricaMapper metricaMapper;

    private final SonarService sonarService;

    public List<MetricaDTO> obtenerIncidenciasPorRepoId(Long repoId) {
        Analisis ultimoAnalisis = analisisRepository.findFirstByRepositorioIdOrderByFechaEjecucionDesc(repoId)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún análisis para este repositorio"));

        return metricaRepository.findByAnalisisId(ultimoAnalisis.getId())
                .stream()
                .map(metricaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public String obtenerCodigoDeIncidencia(Long incidenciaId) {
        Metrica incidencia = metricaRepository.findById(incidenciaId)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada"));

        Long repoId = incidencia.getAnalisis().getRepositorio().getId();
        String projectKey = "paicasso_" + repoId;

        String componentKey = projectKey + ":" + incidencia.getArchivo();

        return sonarService.obtenerContextoCodigo(componentKey, incidencia.getLinea());
    }
}