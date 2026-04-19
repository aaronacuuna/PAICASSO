package es.aaracubel.paicasso.backend.repositories;

import es.aaracubel.paicasso.backend.entities.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findBySesionChatIdOrderByTimestampAsc(Long sesionChatId);
}
