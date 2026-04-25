package es.aaracubel.paicasso.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformeDTO {
    private Long id;
    private LocalDateTime fechaGeneracion;
    private String diagnostico;
    private String propuesta;
}
