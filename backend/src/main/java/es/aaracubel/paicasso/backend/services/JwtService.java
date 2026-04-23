package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final String SECRET_KEY_STRING;
    private final Integer EXPIRATION_TIME;
    private final SecretKey SECRET_KEY;

    public JwtService(@Value("${app.jwt.secret}") String secretKeyString,
                      @Value("${app.jwt.expiration-time}") Integer expirationTime) {
        this.SECRET_KEY_STRING = secretKeyString;
        this.EXPIRATION_TIME = expirationTime;
        this.SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(Usuario usuario){
        return Jwts.builder()
                .subject(usuario.getId().toString())
                .claim("nombre", usuario.getNombreUsuario())
                .claim("githubId", usuario.getIdGithub())
                .claim("fotoPerfil", usuario.getFotoPerfil())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
}
