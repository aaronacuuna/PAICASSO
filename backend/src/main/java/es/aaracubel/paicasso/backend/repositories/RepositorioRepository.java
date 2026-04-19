package es.aaracubel.paicasso.backend.repositories;

import es.aaracubel.paicasso.backend.entities.Repositorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioRepository extends JpaRepository<Repositorio, Long> {

    List<Repositorio> findByUsuarioId(Long usuarioId);
}
