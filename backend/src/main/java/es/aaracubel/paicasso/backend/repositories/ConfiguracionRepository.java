package es.aaracubel.paicasso.backend.repositories;

import es.aaracubel.paicasso.backend.entities.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {

    Optional<Configuracion> findByUsuarioId(Long usuarioId);
}
