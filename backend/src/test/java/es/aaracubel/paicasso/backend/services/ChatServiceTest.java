package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.entities.Mensaje;
import es.aaracubel.paicasso.backend.entities.Repositorio;
import es.aaracubel.paicasso.backend.entities.SesionChat;
import es.aaracubel.paicasso.backend.repositories.MensajeRepository;
import es.aaracubel.paicasso.backend.repositories.RepositorioRepository;
import es.aaracubel.paicasso.backend.repositories.SesionChatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private SesionChatRepository sesionChatRepository;

    @Mock
    private MensajeRepository mensajeRepository;

    @Mock
    private RepositorioRepository repositorioRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void obtenerSesion_Existente() {
        SesionChat sesion = new SesionChat();
        when(sesionChatRepository.findByRepositorioId(1L)).thenReturn(Optional.of(sesion));

        SesionChat resultado = chatService.obtenerSesion(1L);

        assertEquals(sesion, resultado);
    }

    @Test
    void obtenerSesion_Nueva() {
        when(sesionChatRepository.findByRepositorioId(1L)).thenReturn(Optional.empty());
        Repositorio repo = new Repositorio();
        when(repositorioRepository.findById(1L)).thenReturn(Optional.of(repo));
        SesionChat nuevaSesion = new SesionChat();
        nuevaSesion.setEstado("activa");
        when(sesionChatRepository.save(any(SesionChat.class))).thenReturn(nuevaSesion);

        SesionChat resultado = chatService.obtenerSesion(1L);

        assertNotNull(resultado);
        assertEquals("activa", resultado.getEstado());
        verify(sesionChatRepository).save(any(SesionChat.class));
    }

    @Test
    void guardarMensaje() {
        SesionChat sesion = new SesionChat();
        Mensaje mensaje = new Mensaje();
        when(mensajeRepository.save(any(Mensaje.class))).thenReturn(mensaje);

        Mensaje resultado = chatService.guardarMensaje(sesion, "Hola", "user");

        assertNotNull(resultado);
        verify(mensajeRepository).save(any(Mensaje.class));
    }

    @Test
    void obtenerMensajes() {
        when(mensajeRepository.findBySesionChatIdOrderByTimestampAsc(1L)).thenReturn(List.of(new Mensaje()));
        List<Mensaje> resultado = chatService.obtenerMensajes(1L);
        assertFalse(resultado.isEmpty());
    }
}
