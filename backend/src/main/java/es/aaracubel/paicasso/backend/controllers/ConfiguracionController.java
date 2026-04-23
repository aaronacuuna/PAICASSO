package es.aaracubel.paicasso.backend.controllers;

import es.aaracubel.paicasso.backend.dtos.ConfiguracionDTO;
import es.aaracubel.paicasso.backend.services.ConfiguracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    @GetMapping
    public ResponseEntity<ConfiguracionDTO> getConfiguracion() {
        Long usuarioId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        ConfiguracionDTO dto = configuracionService.obtenerConfiguracion(usuarioId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ConfiguracionDTO> guardarConfiguracion(@RequestBody ConfiguracionDTO dto) {
        Long usuarioId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        ConfiguracionDTO configuracionGuardada = configuracionService.guardarConfiguracion(usuarioId, dto);
        return ResponseEntity.ok(configuracionGuardada);
    }
}
