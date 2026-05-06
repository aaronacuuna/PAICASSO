package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.ConfiguracionDTO;
import es.aaracubel.paicasso.backend.entities.Configuracion;
import es.aaracubel.paicasso.backend.entities.Usuario;
import es.aaracubel.paicasso.backend.mappers.ConfiguracionMapper;
import es.aaracubel.paicasso.backend.repositories.ConfiguracionRepository;
import es.aaracubel.paicasso.backend.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConfiguracionServiceTest {

    @Mock
    private ConfiguracionRepository configuracionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConfiguracionMapper configuracionMapper;

    @InjectMocks
    private ConfiguracionService configuracionService;

    @Test
    void obtenerConfiguracion() {
        Configuracion config = new Configuracion();
        ConfiguracionDTO dto = new ConfiguracionDTO();
        when(configuracionRepository.findByUsuarioId(1L)).thenReturn(Optional.of(config));
        when(configuracionMapper.toDTO(config)).thenReturn(dto);

        ConfiguracionDTO resultado = configuracionService.obtenerConfiguracion(1L);

        assertNotNull(resultado);
        verify(configuracionRepository).findByUsuarioId(1L);
    }

    @Test
    void guardarConfiguracion_UsuarioNoEncontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> configuracionService.guardarConfiguracion(1L, new ConfiguracionDTO()));
    }

    @Test
    void guardarConfiguracion_Exito() {
        Usuario usuario = new Usuario();
        Configuracion config = new Configuracion();
        ConfiguracionDTO dto = new ConfiguracionDTO();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(configuracionRepository.findByUsuarioId(1L)).thenReturn(Optional.of(config));
        when(configuracionRepository.save(any())).thenReturn(config);
        when(configuracionMapper.toDTO(config)).thenReturn(dto);

        ConfiguracionDTO resultado = configuracionService.guardarConfiguracion(1L, dto);

        assertNotNull(resultado);
        verify(configuracionMapper).updateEntityFromDTO(dto, config);
        verify(configuracionRepository).save(config);
    }
}
