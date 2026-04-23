package es.aaracubel.paicasso.backend.mappers;

import es.aaracubel.paicasso.backend.dtos.MetricaDTO;
import es.aaracubel.paicasso.backend.entities.Metrica;
import org.springframework.stereotype.Component;

@Component
public class MetricaMapper {

    public MetricaDTO toDTO(Metrica metrica) {
        if (metrica == null) {
            return null;
        }


        return MetricaDTO.builder()
                .id(metrica.getId())
                .archivo(metrica.getArchivo())
                .linea(metrica.getLinea())
                .severidad(metrica.getSeveridad())
                .descripcion(metrica.getDescripcion())
                .tipo(metrica.getTipo())
                .build();
    }
}