package es.aaracubel.paicasso.backend.controllers;

import es.aaracubel.paicasso.backend.dtos.RepositorioDTO;
import es.aaracubel.paicasso.backend.services.RepositorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositorios")
@RequiredArgsConstructor
public class RepositorioController {

    private final RepositorioService repositorioService;

    @GetMapping
    public ResponseEntity<List<RepositorioDTO>> obtenerRepositorios() {
        Long usuarioId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        List<RepositorioDTO> repos = repositorioService.obtenerRepositoriosVinculados(usuarioId);
        return ResponseEntity.ok(repos);
    }

    @GetMapping("/github")
    public ResponseEntity<List<RepositorioDTO>> obtenerRepositoriosDeGitHub() {
        Long usuarioId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        List<RepositorioDTO> repos = repositorioService.buscarReposEnGitHub(usuarioId);
        return ResponseEntity.ok(repos);
    }

    @PostMapping
    public ResponseEntity<RepositorioDTO> vincularRepositorio(@RequestBody RepositorioDTO repositorioDTO) {
        Long usuarioId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        RepositorioDTO nuevoRepo = repositorioService.vincularRepositorio(usuarioId, repositorioDTO);
        return ResponseEntity.ok(nuevoRepo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepositorioDTO> obtenerRepositorio(@PathVariable Long id) {
        RepositorioDTO repo = repositorioService.obtenerRepositorio(id);
        return ResponseEntity.ok(repo);
    }
}
