package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.entities.Usuario;
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
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void autenticarGithub_UsuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setIdGithub(123L);
        when(usuarioRepository.findByIdGithub(123L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.autenticarGithub(123L, "user1", "fotoPerfil", "tokenAcceso");

        assertNotNull(resultado);
        verify(usuarioRepository).save(usuario);
        assertEquals("tokenAcceso", usuario.getTokenAcceso());
    }

    @Test
    void autenticarGithub_UsuarioNuevo() {
        when(usuarioRepository.findByIdGithub(123L)).thenReturn(Optional.empty());
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setIdGithub(123L);
        nuevoUsuario.setNombreUsuario("user1");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(nuevoUsuario);

        Usuario resultado = usuarioService.autenticarGithub(123L, "user1", "fotoPerfil", "tokenAcceso");

        assertNotNull(resultado);
        verify(usuarioRepository).save(any(Usuario.class));
        assertEquals("user1", resultado.getNombreUsuario());
    }
}
