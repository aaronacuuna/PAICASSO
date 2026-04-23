package es.aaracubel.paicasso.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositorioDTO {
    private Long id;
    private String nombre;
    private String url;
    private String lenguajePrincipal;
    private Boolean vinculado;
}
