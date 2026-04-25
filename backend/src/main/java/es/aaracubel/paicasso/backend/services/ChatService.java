package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.entities.Mensaje;
import es.aaracubel.paicasso.backend.entities.Repositorio;
import es.aaracubel.paicasso.backend.entities.SesionChat;
import es.aaracubel.paicasso.backend.repositories.MensajeRepository;
import es.aaracubel.paicasso.backend.repositories.RepositorioRepository;
import es.aaracubel.paicasso.backend.repositories.SesionChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SesionChatRepository sesionChatRepository;
    private final MensajeRepository mensajeRepository;
    private final RepositorioRepository repositorioRepository;

    public SesionChat obtenerSesion(Long repositorioId) {
        return sesionChatRepository.findByRepositorioId(repositorioId)
                .orElseGet(() -> {
                    Repositorio repo = repositorioRepository.findById(repositorioId)
                            .orElseThrow(() -> new RuntimeException("Repositorio no encontrado"));
                    SesionChat nuevaSesion = new SesionChat();
                    nuevaSesion.setEstado("activa");
                    nuevaSesion.setRepositorio(repo);
                    return sesionChatRepository.save(nuevaSesion);
                });
    }

    public Mensaje guardarMensaje(SesionChat sesion, String contenido, String remitente) {
        Mensaje mensaje = new Mensaje();
        mensaje.setSesionChat(sesion);
        mensaje.setContenido(contenido);
        mensaje.setRemitente(remitente);
        mensaje.setTimestamp(LocalDateTime.now());
        return mensajeRepository.save(mensaje);
    }

    public List<Mensaje> obtenerMensajes(Long sesionId) {
        return mensajeRepository.findBySesionChatIdOrderByTimestampAsc(sesionId);
    }
}
