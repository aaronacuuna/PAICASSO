package es.aaracubel.paicasso.backend.controllers;

import es.aaracubel.paicasso.backend.dtos.AnalisisDTO;
import es.aaracubel.paicasso.backend.services.AnalisisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repositorios")
@RequiredArgsConstructor
public class AnalisisController {

    private final AnalisisService analisisService;

    @GetMapping("/{repoId}/analisis/ultimo")
    public ResponseEntity<AnalisisDTO> getUltimoAnalisis(@PathVariable Long repoId) {
        AnalisisDTO dto = analisisService.obtenerUltimoAnalisis(repoId);
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{repoId}/analizar")
    public ResponseEntity<AnalisisDTO> analizarRepositorio(@PathVariable Long repoId) {
        AnalisisDTO analisisIniciado = analisisService.iniciarAnalisis(repoId);
        analisisService.ejecutarAnalisisEnSegundoPlano(analisisIniciado.getId(), repoId);
        return ResponseEntity.accepted().body(analisisIniciado);
    }
}
