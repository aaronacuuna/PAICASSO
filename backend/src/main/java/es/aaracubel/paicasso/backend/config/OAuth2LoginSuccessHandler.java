package es.aaracubel.paicasso.backend.config;

import es.aaracubel.paicasso.backend.entities.Usuario;
import es.aaracubel.paicasso.backend.services.JwtService;
import es.aaracubel.paicasso.backend.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.oauth2.redirect-path}")
    private String redirectPath;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );
        String githubToken = client.getAccessToken().getTokenValue();

        Number idAttribute = oAuth2User.getAttribute("id");
        Long idGithub = idAttribute != null ? idAttribute.longValue() : null;
        String nombreUsuario = oAuth2User.getAttribute("login");
        String fotoPerfil = oAuth2User.getAttribute("avatar_url");

        Usuario usuario = usuarioService.autenticarGithub(idGithub, nombreUsuario, fotoPerfil, githubToken);

        String jwt = jwtService.generarToken(usuario);

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + redirectPath)
                .queryParam("token", jwt)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
