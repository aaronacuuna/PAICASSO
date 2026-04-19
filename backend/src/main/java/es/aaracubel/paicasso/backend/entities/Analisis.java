package es.aaracubel.paicasso.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "analisis")
@Data
public class Analisis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "fecha_ejecucion")
    private LocalDateTime fechaEjecucion;

    @ManyToOne
    @JoinColumn(name = "repositorio_id")
    private Repositorio repositorio;
}
