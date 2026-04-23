package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.entities.Usuario;
import es.aaracubel.paicasso.backend.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario autenticarGithub(Long idGithub, String nombreUsuario, String fotoPerfil, String tokenGithub) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByIdGithub(idGithub);

        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setTokenAcceso(tokenGithub);
            usuario.setFotoPerfil(fotoPerfil);
            return usuarioRepository.save(usuario);
        } else {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setIdGithub(idGithub);
            nuevoUsuario.setNombreUsuario(nombreUsuario);
            nuevoUsuario.setTokenAcceso(tokenGithub);
            nuevoUsuario.setFotoPerfil(fotoPerfil);
            return usuarioRepository.save(nuevoUsuario);
        }
    }
}
