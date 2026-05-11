package es.aaracubel.paicasso.backend.controllers;

import es.aaracubel.paicasso.backend.dtos.MetricaDTO;
import es.aaracubel.paicasso.backend.services.MetricaService;
import es.aaracubel.paicasso.backend.services.RepositorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class MetricaController {

    private final MetricaService metricaService;
    private final RepositorioService repositorioService;

    @GetMapping("/{repoId}")
    public ResponseEntity<List<MetricaDTO>> obtenerIncidenciasPorRepo(@PathVariable Long repoId) {
        Long usuarioId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        repositorioService.validarAcceso(repoId, usuarioId);
        try {
            List<MetricaDTO> incidencias = metricaService.obtenerIncidenciasPorRepoId(repoId);
            return ResponseEntity.ok(incidencias);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{incidenciaId}/codigo")
    public ResponseEntity<Map<String, String>> obtenerCodigoIncidencia(@PathVariable Long incidenciaId) {
        Long usuarioId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        try {
            String codigoFuente = metricaService.obtenerCodigoDeIncidencia(incidenciaId, usuarioId);
            return ResponseEntity.ok(Map.of("codigo", codigoFuente));
        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}