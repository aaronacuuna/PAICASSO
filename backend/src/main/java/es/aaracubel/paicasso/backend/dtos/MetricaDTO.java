package es.aaracubel.paicasso.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricaDTO {
    private Long id;
    private String archivo;
    private Integer linea;
    private String severidad;
    private String descripcion;
    private String tipo;
}