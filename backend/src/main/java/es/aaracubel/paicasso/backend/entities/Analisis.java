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

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoAnalisis estado;

    @Column(name = "fecha_ejecucion")
    private LocalDateTime fechaEjecucion;

    @Column(name = "total_bugs")
    private Integer totalBugs;

    @Column(name = "total_vulnerabilidades")
    private Integer totalVulnerabilidades;

    @Column(name = "total_code_smells")
    private Integer totalCodeSmells;

    @Column(name = "lineas_codigo")
    private Integer lineasCodigo;

    @Column(name = "cobertura")
    private Double cobertura;

    @Column(name = "duplicaciones")
    private Double duplicaciones;

    @ManyToOne
    @JoinColumn(name = "repositorio_id")
    private Repositorio repositorio;
}
