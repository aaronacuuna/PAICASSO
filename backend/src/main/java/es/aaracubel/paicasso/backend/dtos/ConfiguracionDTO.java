package es.aaracubel.paicasso.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionDTO {
    private String experienceLevel;
    private List<String> priorities;
    private boolean addComments;
}
