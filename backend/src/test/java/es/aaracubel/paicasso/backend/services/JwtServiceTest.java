package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.entities.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String testSecret = "my-super-secret-key-that-needs-to-be-long-enough";
        jwtService = new JwtService(testSecret, 3600000);
    }

    @Test
    void generarToken() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreUsuario("testUser");
        usuario.setIdGithub(12345L);
        usuario.setFotoPerfil("http://foto.com");

        String token = jwtService.generarToken(usuario);

        assertNotNull(token);
    }
}
