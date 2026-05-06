package es.aaracubel.paicasso.backend.mappers;

import es.aaracubel.paicasso.backend.dtos.RepositorioDTO;
import es.aaracubel.paicasso.backend.entities.Repositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RepositorioMapperTest {

    private RepositorioMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RepositorioMapper();
    }

    @Test
    void toDTO_Null() {
        assertNull(mapper.toDTO(null, true));
    }

    @Test
    void toDTO_Valid() {
        Repositorio repo = new Repositorio();
        repo.setId(1L);
        repo.setNombre("paicasso");
        repo.setUrl("http://github.com/paicasso");
        repo.setLenguajePrincipal("Java");

        RepositorioDTO dto = mapper.toDTO(repo, false);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("paicasso", dto.getNombre());
        assertEquals("http://github.com/paicasso", dto.getUrl());
        assertEquals("Java", dto.getLenguajePrincipal());
        assertFalse(dto.getVinculado());
    }

    @Test
    void toEntity_Null() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toEntity_Valid() {
        RepositorioDTO dto = new RepositorioDTO();
        dto.setNombre("front");
        dto.setUrl("http://github.com/front");
        dto.setLenguajePrincipal("TypeScript");

        Repositorio repo = mapper.toEntity(dto);

        assertNotNull(repo);
        assertEquals("front", repo.getNombre());
        assertEquals("http://github.com/front", repo.getUrl());
        assertEquals("TypeScript", repo.getLenguajePrincipal());
    }
}
