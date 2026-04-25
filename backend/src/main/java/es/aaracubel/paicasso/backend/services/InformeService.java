package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.AnalisisDTO;
import es.aaracubel.paicasso.backend.dtos.InformeDTO;
import es.aaracubel.paicasso.backend.entities.Informe;
import es.aaracubel.paicasso.backend.mappers.InformeMapper;
import es.aaracubel.paicasso.backend.repositories.InformeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InformeService {

    private final InformeRepository informeRepository;
    private final InformeMapper informeMapper;
    private final AnalisisService analisisService;

    public InformeDTO obtenerInforme(Long repoId) {
        AnalisisDTO analisis = analisisService.obtenerUltimoAnalisis(repoId);
        Informe informe = informeRepository.findByAnalisisId(analisis.getId()).orElse(null);
        return informeMapper.toDTO(informe);
    }
}
