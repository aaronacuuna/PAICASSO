package es.aaracubel.paicasso.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "sesion_chat")
@Data
public class SesionChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estado", length = 50)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "informe_id")
    private Informe informe;
}
