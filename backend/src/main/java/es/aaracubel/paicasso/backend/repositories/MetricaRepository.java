package es.aaracubel.paicasso.backend.repositories;

import es.aaracubel.paicasso.backend.entities.Metrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetricaRepository extends JpaRepository<Metrica, Long> {

    List<Metrica> findByAnalisisId(Long analisisId);
}
