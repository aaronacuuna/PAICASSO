package es.aaracubel.paicasso.backend.repositories;

import es.aaracubel.paicasso.backend.entities.Analisis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalisisRepository extends JpaRepository<Analisis, Long> {

    List<Analisis> findByRepositorioId(Long repositorioId);
}
