package es.aaracubel.paicasso.backend.entities;

import java.util.List;

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
    @JoinColumn(name = "repositorio_id")
    private Repositorio repositorio;

    @OneToMany(mappedBy = "sesionChat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mensaje> mensajes;
}
