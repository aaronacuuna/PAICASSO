package es.aaracubel.paicasso.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "informe")
@Data
public class Informe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @Column(name = "propuesta", columnDefinition = "TEXT")
    private String propuesta;

    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    @OneToOne
    @JoinColumn(name = "analisis_id")
    private Analisis analisis;
}
