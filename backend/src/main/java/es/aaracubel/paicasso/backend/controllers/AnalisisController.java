package es.aaracubel.paicasso.backend.controllers;

import es.aaracubel.paicasso.backend.dtos.AnalisisDTO;
import es.aaracubel.paicasso.backend.services.AnalisisService;
import es.aaracubel.paicasso.backend.services.RepositorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repositorios")
@RequiredArgsConstructor
public class AnalisisController {

    private final AnalisisService analisisService;
    private final RepositorioService repositorioService;

    @GetMapping("/{repoId}/analisis/ultimo")
    public ResponseEntity<AnalisisDTO> getUltimoAnalisis(@PathVariable Long repoId) {
        Long usuarioId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        repositorioService.validarAcceso(repoId, usuarioId);
        AnalisisDTO dto = analisisService.obtenerUltimoAnalisis(repoId);
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{repoId}/analizar")
    public ResponseEntity<AnalisisDTO> analizarRepositorio(@PathVariable Long repoId) {
        Long usuarioId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        repositorioService.validarAcceso(repoId, usuarioId);
        AnalisisDTO analisisIniciado = analisisService.iniciarAnalisis(repoId);
        analisisService.ejecutarAnalisisEnSegundoPlano(analisisIniciado.getId(), repoId);
        return ResponseEntity.accepted().body(analisisIniciado);
    }
}
