package es.aaracubel.paicasso.backend.mappers;

import es.aaracubel.paicasso.backend.dtos.MetricaDTO;
import es.aaracubel.paicasso.backend.entities.Metrica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MetricaMapperTest {

    private MetricaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MetricaMapper();
    }

    @Test
    void toDTO_Null() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toDTO_Valid() {
        Metrica metrica = new Metrica();
        metrica.setId(10L);
        metrica.setArchivo("Test.java");
        metrica.setLinea(50);
        metrica.setSeveridad("HIGH");
        metrica.setDescripcion("Bad code");
        metrica.setTipo("BUG");

        MetricaDTO dto = mapper.toDTO(metrica);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("Test.java", dto.getArchivo());
        assertEquals(50, dto.getLinea());
        assertEquals("HIGH", dto.getSeveridad());
        assertEquals("Bad code", dto.getDescripcion());
        assertEquals("BUG", dto.getTipo());
    }
}
