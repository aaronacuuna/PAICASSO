package es.aaracubel.paicasso.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "metrica")
@Data
public class Metrica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "archivo", length = 512)
    private String archivo;

    @Column(name = "linea")
    private Integer linea;

    @Column(name = "severidad", length = 20)
    private String severidad;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "analisis_id")
    private Analisis analisis;
}
