package es.aaracubel.paicasso.backend.mappers;

import es.aaracubel.paicasso.backend.dtos.InformeDTO;
import es.aaracubel.paicasso.backend.entities.Informe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InformeMapperTest {

    private InformeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InformeMapper();
    }

    @Test
    void toDTO_Null() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toDTO_Valid() {
        Informe informe = new Informe();
        informe.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        informe.setFechaGeneracion(now);
        informe.setDiagnostico("diagnostico test");
        informe.setPropuesta("propuesta test");

        InformeDTO dto = mapper.toDTO(informe);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(now, dto.getFechaGeneracion());
        assertEquals("diagnostico test", dto.getDiagnostico());
        assertEquals("propuesta test", dto.getPropuesta());
    }
}
