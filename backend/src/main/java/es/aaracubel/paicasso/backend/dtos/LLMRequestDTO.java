package es.aaracubel.paicasso.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LLMRequestDTO {
    private Long analisisId;
    private Long repoId;
    private Long sesionId;
    private String componentKey;
    private Integer lineaError;
    private String mensaje;
}