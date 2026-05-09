package es.aaracubel.paicasso.backend.dtos;

import es.aaracubel.paicasso.backend.entities.Mensaje;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDTO {
    private Long id;
    private String contenido;
    private String remitente;
    private LocalDateTime timestamp;

    public static MensajeDTO from(Mensaje m) {
        return MensajeDTO.builder()
                .id(m.getId())
                .contenido(m.getContenido())
                .remitente(m.getRemitente())
                .timestamp(m.getTimestamp())
                .build();
    }
}
