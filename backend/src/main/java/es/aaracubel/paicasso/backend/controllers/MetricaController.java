package es.aaracubel.paicasso.backend.controllers;

import es.aaracubel.paicasso.backend.dtos.MetricaDTO;
import es.aaracubel.paicasso.backend.services.MetricaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class MetricaController {

    private final MetricaService metricaService;

    @GetMapping("/{repoId}")
    public ResponseEntity<List<MetricaDTO>> obtenerIncidenciasPorRepo(@PathVariable Long repoId) {
        try {
            List<MetricaDTO> incidencias = metricaService.obtenerIncidenciasPorRepoId(repoId);
            return ResponseEntity.ok(incidencias);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{incidenciaId}/codigo")
    public ResponseEntity<Map<String, String>> obtenerCodigoIncidencia(@PathVariable Long incidenciaId) {
        try {
            String codigoFuente = metricaService.obtenerCodigoDeIncidencia(incidenciaId);
            return ResponseEntity.ok(Map.of("codigo", codigoFuente));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}