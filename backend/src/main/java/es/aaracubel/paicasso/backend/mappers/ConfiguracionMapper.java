package es.aaracubel.paicasso.backend.mappers;

import es.aaracubel.paicasso.backend.dtos.ConfiguracionDTO;
import es.aaracubel.paicasso.backend.entities.Configuracion;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ConfiguracionMapper {

    public ConfiguracionDTO toDTO(Configuracion configuracion) {
        if (configuracion == null) {
            return ConfiguracionDTO.builder().build();
        }

        List<String> prioridadesList = configuracion.getPrioridades() != null && !configuracion.getPrioridades().isEmpty()
                ? Arrays.asList(configuracion.getPrioridades().split(","))
                : List.of();

        return ConfiguracionDTO.builder()
                .experienceLevel(configuracion.getNivelExperiencia())
                .priorities(prioridadesList)
                .addComments(configuracion.getComentarios())
                .build();
    }

    public void updateEntityFromDTO(ConfiguracionDTO dto, Configuracion configuracion) {
        if (dto == null || configuracion == null) return;

        configuracion.setNivelExperiencia(dto.getExperienceLevel());
        configuracion.setComentarios(dto.isAddComments());

        if (dto.getPriorities() != null && !dto.getPriorities().isEmpty()) {
            configuracion.setPrioridades(String.join(",", dto.getPriorities()));
        } else {
            configuracion.setPrioridades("");
        }
    }
}
