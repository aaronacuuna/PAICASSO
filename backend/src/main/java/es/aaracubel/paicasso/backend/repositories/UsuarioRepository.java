package es.aaracubel.paicasso.backend.repositories;

import es.aaracubel.paicasso.backend.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByIdGithub(Long idGithub);
}
