package es.aaracubel.paicasso.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "repositorio")
@Data
public class Repositorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "url", length = 512)
    private String url;

    @Column(name = "lenguaje_principal", length = 50)
    private String lenguajePrincipal;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
