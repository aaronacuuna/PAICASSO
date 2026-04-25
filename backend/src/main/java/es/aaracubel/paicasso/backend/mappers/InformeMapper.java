package es.aaracubel.paicasso.backend.mappers;

import es.aaracubel.paicasso.backend.dtos.InformeDTO;
import es.aaracubel.paicasso.backend.entities.Informe;
import org.springframework.stereotype.Component;

@Component
public class InformeMapper {

    public InformeDTO toDTO(Informe informe) {
        if (informe == null) {
            return null;
        }

        return InformeDTO.builder()
                .id(informe.getId())
                .fechaGeneracion(informe.getFechaGeneracion())
                .diagnostico(informe.getDiagnostico())
                .propuesta(informe.getPropuesta())
                .build();
    }
}
