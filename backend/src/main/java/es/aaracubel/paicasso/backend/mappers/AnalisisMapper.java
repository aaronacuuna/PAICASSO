package es.aaracubel.paicasso.backend.mappers;

import es.aaracubel.paicasso.backend.dtos.AnalisisDTO;
import es.aaracubel.paicasso.backend.entities.Analisis;
import org.springframework.stereotype.Component;

@Component
public class AnalisisMapper {

    public AnalisisDTO toDTO(Analisis entity) {
        if (entity == null) return null;

        return AnalisisDTO.builder()
                .id(entity.getId())
                .estado(entity.getEstado())
                .fechaEjecucion(entity.getFechaEjecucion())
                .bugs(entity.getTotalBugs())
                .vulnerabilidades(entity.getTotalVulnerabilidades())
                .codeSmells(entity.getTotalCodeSmells())
                .lineasCodigo(entity.getLineasCodigo())
                .cobertura(entity.getCobertura())
                .duplicaciones(entity.getDuplicaciones())
                .build();
    }
}
