package es.aaracubel.paicasso.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "configuracion")
@Data
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nivel_experiencia", length = 100)
    private String nivelExperiencia;

    @Column(name = "prioridades", length = 100)
    private String prioridades;

    @Column(name = "comentarios")
    private Boolean comentarios;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
