package es.aaracubel.paicasso.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_github", unique = true)
    private Long idGithub;

    @Column(name = "nombre_usuario", nullable = false)
    private String nombreUsuario;

    @Column(name = "token_acceso", length = 512)
    private String tokenAcceso;
}
