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

    public RepositorioDTO obtenerRepositorio(Long repoId) {
        return repositorioRepository.findById(repoId)
                .map(repo -> repositorioMapper.toDTO(repo, true))
                .orElseThrow(() -> new RuntimeException("Repositorio no encontrado"));
    }
}
