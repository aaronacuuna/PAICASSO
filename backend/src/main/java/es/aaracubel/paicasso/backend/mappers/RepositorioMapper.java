package es.aaracubel.paicasso.backend.mappers;

import es.aaracubel.paicasso.backend.dtos.RepositorioDTO;
import es.aaracubel.paicasso.backend.entities.Repositorio;
import org.springframework.stereotype.Component;

@Component
public class RepositorioMapper {

    public RepositorioDTO toDTO(Repositorio repositorio, boolean vinculado) {
        if (repositorio == null) return null;

        return RepositorioDTO.builder()
                .id(repositorio.getId())
                .nombre(repositorio.getNombre())
                .url(repositorio.getUrl())
                .lenguajePrincipal(repositorio.getLenguajePrincipal())
                .vinculado(vinculado)
                .build();
    }

    public Repositorio toEntity(RepositorioDTO dto) {
        if (dto == null) return null;

        Repositorio repositorio = new Repositorio();
        repositorio.setNombre(dto.getNombre());
        repositorio.setUrl(dto.getUrl());
        repositorio.setLenguajePrincipal(dto.getLenguajePrincipal());
        return repositorio;
    }
}
