package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.AnalisisDTO;
import es.aaracubel.paicasso.backend.dtos.InformeDTO;
import es.aaracubel.paicasso.backend.entities.Informe;
import es.aaracubel.paicasso.backend.mappers.InformeMapper;
import es.aaracubel.paicasso.backend.repositories.InformeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InformeServiceTest {

    @Mock
    private InformeRepository informeRepository;

    @Mock
    private InformeMapper informeMapper;

    @Mock
    private AnalisisService analisisService;

    @InjectMocks
    private InformeService informeService;

    @Test
    void obtenerInforme() {
        AnalisisDTO analisisDTO = new AnalisisDTO();
        analisisDTO.setId(10L);
        when(analisisService.obtenerUltimoAnalisis(1L)).thenReturn(analisisDTO);

        Informe informe = new Informe();
        when(informeRepository.findByAnalisisId(10L)).thenReturn(Optional.of(informe));

        InformeDTO dto = new InformeDTO();
        when(informeMapper.toDTO(informe)).thenReturn(dto);

        InformeDTO resultado = informeService.obtenerInforme(1L);

        assertNotNull(resultado);
        verify(analisisService).obtenerUltimoAnalisis(1L);
        verify(informeRepository).findByAnalisisId(10L);
        verify(informeMapper).toDTO(informe);
    }
}
