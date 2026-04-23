package es.aaracubel.paicasso.backend.dtos;

import es.aaracubel.paicasso.backend.entities.EstadoAnalisis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalisisDTO {
    private Long id;
    private EstadoAnalisis estado;
    private LocalDateTime fechaEjecucion;
    private Integer bugs;
    private Integer vulnerabilidades;
    private Integer codeSmells;
    private Integer lineasCodigo;
    private Double cobertura;
    private Double duplicaciones;
}
