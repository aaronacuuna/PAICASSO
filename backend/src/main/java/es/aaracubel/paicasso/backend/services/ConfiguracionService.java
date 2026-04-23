package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.ConfiguracionDTO;
import es.aaracubel.paicasso.backend.entities.Configuracion;
import es.aaracubel.paicasso.backend.entities.Usuario;
import es.aaracubel.paicasso.backend.mappers.ConfiguracionMapper;
import es.aaracubel.paicasso.backend.repositories.ConfiguracionRepository;
import es.aaracubel.paicasso.backend.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConfiguracionMapper configuracionMapper;

    public ConfiguracionDTO obtenerConfiguracion(Long usuarioId) {
        Configuracion config = configuracionRepository.findByUsuarioId(usuarioId).orElse(null);
        return configuracionMapper.toDTO(config);
    }

    public ConfiguracionDTO guardarConfiguracion(Long usuarioId, ConfiguracionDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Configuracion config = configuracionRepository.findByUsuarioId(usuarioId).orElse(new Configuracion());

        if (config.getUsuario() == null) {
            config.setUsuario(usuario);
        }

        configuracionMapper.updateEntityFromDTO(dto, config);
        Configuracion configGuardada = configuracionRepository.save(config);
        return configuracionMapper.toDTO(configGuardada);
    }
}
