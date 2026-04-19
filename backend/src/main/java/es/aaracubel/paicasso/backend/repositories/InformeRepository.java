package es.aaracubel.paicasso.backend.repositories;

import es.aaracubel.paicasso.backend.entities.Informe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InformeRepository extends JpaRepository<Informe, Long> {

    Optional<Informe> findByAnalisisId(Long analisisId);
}
