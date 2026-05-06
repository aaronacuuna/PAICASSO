package es.aaracubel.paicasso.backend.mappers;

import es.aaracubel.paicasso.backend.dtos.ConfiguracionDTO;
import es.aaracubel.paicasso.backend.entities.Configuracion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConfiguracionMapperTest {

    private ConfiguracionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ConfiguracionMapper();
    }

    @Test
    void toDTO_Null() {
        ConfiguracionDTO dto = mapper.toDTO(null);
        assertNotNull(dto);
        assertNull(dto.getExperienceLevel());
        assertNull(dto.getPriorities());
        assertFalse(dto.isAddComments());
    }

    @Test
    void toDTO_ValidWithPriorities() {
        Configuracion config = new Configuracion();
        config.setNivelExperiencia("junior");
        config.setPrioridades("seguridad,rendimiento");
        config.setComentarios(true);

        ConfiguracionDTO dto = mapper.toDTO(config);

        assertNotNull(dto);
        assertEquals("junior", dto.getExperienceLevel());
        assertTrue(dto.getPriorities().contains("seguridad"));
        assertTrue(dto.getPriorities().contains("rendimiento"));
        assertEquals(2, dto.getPriorities().size());
        assertTrue(dto.isAddComments());
    }

    @Test
    void toDTO_ValidEmptyPriorities() {
        Configuracion config = new Configuracion();
        config.setNivelExperiencia("senior");
        config.setPrioridades("");
        config.setComentarios(false);

        ConfiguracionDTO dto = mapper.toDTO(config);

        assertNotNull(dto);
        assertEquals("senior", dto.getExperienceLevel());
        assertTrue(dto.getPriorities().isEmpty());
        assertFalse(dto.isAddComments());
    }
    
    @Test
    void toDTO_ValidNullPriorities() {
        Configuracion config = new Configuracion();
        config.setNivelExperiencia("mid");
        config.setPrioridades(null);
        config.setComentarios(true);

        ConfiguracionDTO dto = mapper.toDTO(config);

        assertNotNull(dto);
        assertEquals("mid", dto.getExperienceLevel());
        assertTrue(dto.getPriorities().isEmpty());
        assertTrue(dto.isAddComments());
    }

    @Test
    void updateEntityFromDTO_Nulls() {
        Configuracion config = new Configuracion();
        config.setNivelExperiencia("old");

        // DTO nulo
        mapper.updateEntityFromDTO(null, config);
        assertEquals("old", config.getNivelExperiencia()); // no debe cambiar

        // Entidad nula
        assertDoesNotThrow(() -> mapper.updateEntityFromDTO(new ConfiguracionDTO(), null));
    }

    @Test
    void updateEntityFromDTO_WithPriorities() {
        ConfiguracionDTO dto = new ConfiguracionDTO();
        dto.setExperienceLevel("mid");
        dto.setPriorities(List.of("clean_code", "security"));
        dto.setAddComments(true);

        Configuracion config = new Configuracion();
        mapper.updateEntityFromDTO(dto, config);

        assertEquals("mid", config.getNivelExperiencia());
        assertEquals("clean_code,security", config.getPrioridades());
        assertTrue(config.getComentarios());
    }

    @Test
    void updateEntityFromDTO_EmptyPriorities() {
        ConfiguracionDTO dto = new ConfiguracionDTO();
        dto.setExperienceLevel("senior");
        dto.setPriorities(List.of());
        dto.setAddComments(false);

        Configuracion config = new Configuracion();
        mapper.updateEntityFromDTO(dto, config);

        assertEquals("senior", config.getNivelExperiencia());
        assertEquals("", config.getPrioridades());
        assertFalse(config.getComentarios());
    }
    
    @Test
    void updateEntityFromDTO_NullPriorities() {
        ConfiguracionDTO dto = new ConfiguracionDTO();
        dto.setExperienceLevel("junior");
        dto.setPriorities(null);
        dto.setAddComments(true);

        Configuracion config = new Configuracion();
        mapper.updateEntityFromDTO(dto, config);

        assertEquals("junior", config.getNivelExperiencia());
        assertEquals("", config.getPrioridades());
        assertTrue(config.getComentarios());
    }
}
