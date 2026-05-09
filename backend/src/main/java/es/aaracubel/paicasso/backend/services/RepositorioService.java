package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.RepositorioDTO;
import es.aaracubel.paicasso.backend.entities.Repositorio;
import es.aaracubel.paicasso.backend.entities.Usuario;
import es.aaracubel.paicasso.backend.mappers.RepositorioMapper;
import es.aaracubel.paicasso.backend.repositories.RepositorioRepository;
import es.aaracubel.paicasso.backend.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepositorioService {

    private final RepositorioRepository repositorioRepository;
    private final UsuarioRepository usuarioRepository;
    private final RepositorioMapper repositorioMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<RepositorioDTO> obtenerRepositoriosVinculados(Long usuarioId) {
        return repositorioRepository.findByUsuarioId(usuarioId).stream()
                .map(repo -> repositorioMapper.toDTO(repo, true))
                .collect(Collectors.toList());
    }

    public List<RepositorioDTO> buscarReposEnGitHub(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(usuario.getTokenAcceso());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "https://api.github.com/user/repos?visibility=all&sort=updated&per_page=100";

        ResponseEntity<Map[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map[].class
        );

        List<String> urlsYaVinculadas = repositorioRepository.findByUsuarioId(usuarioId).stream()
                .map(Repositorio::getUrl)
                .collect(Collectors.toList());

        List<RepositorioDTO> reposGithub = new ArrayList<>();

        if (response.getBody() != null) {
            for (Map repoData : response.getBody()) {
                String nombre = (String) repoData.get("name");
                String htmlUrl = (String) repoData.get("html_url");
                String lenguaje = (String) repoData.get("language");

                boolean estaVinculado = urlsYaVinculadas.contains(htmlUrl);

                reposGithub.add(RepositorioDTO.builder()
                        .nombre(nombre)
                        .url(htmlUrl)
                        .lenguajePrincipal(lenguaje != null ? lenguaje : "Desconocido")
                        .vinculado(estaVinculado)
                        .build());
            }
        }

        return reposGithub;
    }

    public RepositorioDTO vincularRepositorio(Long usuarioId, RepositorioDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        boolean yaExiste = repositorioRepository.findByUsuarioId(usuarioId).stream().anyMatch(r -> r.getUrl().equals(dto.getUrl()));
        if (yaExiste) {
            throw new RuntimeException("Este repositorio ya está vinculado a tu cuenta");
        }
        Repositorio nuevoRepositorio = repositorioMapper.toEntity(dto);
        nuevoRepositorio.setUsuario(usuario);
        Repositorio repositorio = repositorioRepository.save(nuevoRepositorio);
        return repositorioMapper.toDTO(repositorio, true);
    }

    public RepositorioDTO vincularRepositorioPorUrl(Long usuarioId, String url) {
        if (url == null || url.isBlank()) {
            throw new RuntimeException("La URL no puede estar vacía");
        }
        String trimmed = url.trim().replaceAll("/+$", "");
        String prefix = "https://github.com/";
        if (!trimmed.startsWith(prefix)) {
            throw new RuntimeException("La URL debe ser de un repositorio de GitHub");
        }
        String[] parts = trimmed.substring(prefix.length()).split("/");
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new RuntimeException("URL de repositorio inválida");
        }
        String owner = parts[0];
        String repo = parts[1].replaceAll("\\.git$", "");

        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(usuario.getTokenAcceso());
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        headers.set("User-Agent", "paicasso-backend");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo;
        System.out.println("[vincularRepositorioPorUrl] GET " + apiUrl + " (token len=" + (usuario.getTokenAcceso() == null ? 0 : usuario.getTokenAcceso().length()) + ")");

        Map body;
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            System.out.println("[vincularRepositorioPorUrl] OK status=" + response.getStatusCode());
            body = response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            String scopes = e.getResponseHeaders() != null ? String.valueOf(e.getResponseHeaders().getFirst("X-OAuth-Scopes")) : "?";
            System.err.println("[vincularRepositorioPorUrl] 404 url=" + apiUrl + " scopes=" + scopes + " body=" + e.getResponseBodyAsString());
            throw new RuntimeException("GitHub devolvió 404 para " + owner + "/" + repo + ". Scopes del token=" + scopes + ". Si el repo es privado, tu token OAuth necesita el scope 'repo'.");
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            System.err.println("[vincularRepositorioPorUrl] " + e.getStatusCode() + " body=" + e.getResponseBodyAsString());
            throw new RuntimeException("Token sin permisos: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (HttpClientErrorException e) {
            System.err.println("[vincularRepositorioPorUrl] " + e.getStatusCode() + " body=" + e.getResponseBodyAsString());
            throw new RuntimeException("Error al consultar GitHub (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            System.err.println("[vincularRepositorioPorUrl] RestClientException: " + e.getMessage());
            throw new RuntimeException("Fallo de red al consultar GitHub: " + e.getMessage());
        }

        if (body == null) {
            throw new RuntimeException("No se pudo obtener la información del repositorio");
        }

        Object permissions = body.get("permissions");
        if (permissions instanceof Map) {
            Object pull = ((Map<?, ?>) permissions).get("pull");
            if (Boolean.FALSE.equals(pull)) {
                throw new RuntimeException("Tu token no tiene permiso de lectura sobre este repositorio");
            }
        }

        String nombre = (String) body.get("name");
        String htmlUrl = (String) body.get("html_url");
        String lenguaje = (String) body.get("language");

        RepositorioDTO dto = RepositorioDTO.builder()
                .nombre(nombre)
                .url(htmlUrl)
                .lenguajePrincipal(lenguaje != null ? lenguaje : "Desconocido")
                .build();

        return vincularRepositorio(usuarioId, dto);
    }

    public RepositorioDTO obtenerRepositorio(Long repoId) {
        return repositorioRepository.findById(repoId)
                .map(repo -> repositorioMapper.toDTO(repo, true))
                .orElseThrow(() -> new RuntimeException("Repositorio no encontrado"));
    }

    public void desvincularRepositorio(Long usuarioId, Long repoId) {
        Repositorio repositorio = repositorioRepository.findById(repoId)
                .orElseThrow(() -> new RuntimeException("Repositorio no encontrado"));

        if (!repositorio.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permisos para desvincular este repositorio");
        }

        repositorioRepository.delete(repositorio);
    }
}
