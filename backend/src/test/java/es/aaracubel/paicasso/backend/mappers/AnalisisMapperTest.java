package es.aaracubel.paicasso.backend.mappers;

import es.aaracubel.paicasso.backend.dtos.AnalisisDTO;
import es.aaracubel.paicasso.backend.entities.Analisis;
import es.aaracubel.paicasso.backend.entities.EstadoAnalisis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AnalisisMapperTest {

    private AnalisisMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AnalisisMapper();
    }

    @Test
    void toDTO_Null() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toDTO_Valid() {
        Analisis analisis = new Analisis();
        analisis.setId(1L);
        analisis.setEstado(EstadoAnalisis.COMPLETADO);
        LocalDateTime now = LocalDateTime.now();
        analisis.setFechaEjecucion(now);
        analisis.setTotalBugs(5);
        analisis.setTotalVulnerabilidades(2);
        analisis.setTotalCodeSmells(10);
        analisis.setLineasCodigo(100);
        analisis.setCobertura(80.5);
        analisis.setDuplicaciones(5.0);

        AnalisisDTO dto = mapper.toDTO(analisis);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(EstadoAnalisis.COMPLETADO, dto.getEstado());
        assertEquals(now, dto.getFechaEjecucion());
        assertEquals(5, dto.getBugs());
        assertEquals(2, dto.getVulnerabilidades());
        assertEquals(10, dto.getCodeSmells());
        assertEquals(100, dto.getLineasCodigo());
        assertEquals(80.5, dto.getCobertura());
        assertEquals(5.0, dto.getDuplicaciones());
    }
}
