package es.aaracubel.paicasso.backend.repositories;

import es.aaracubel.paicasso.backend.entities.SesionChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SesionChatRepository extends JpaRepository<SesionChat, Long> {

    Optional<SesionChat> findByRepositorioId(Long repositorioId);
}
