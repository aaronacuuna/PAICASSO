package es.aaracubel.paicasso.backend.controllers;

import es.aaracubel.paicasso.backend.dtos.InformeDTO;
import es.aaracubel.paicasso.backend.dtos.LLMRequestDTO;
import es.aaracubel.paicasso.backend.entities.Mensaje;
import es.aaracubel.paicasso.backend.entities.SesionChat;
import es.aaracubel.paicasso.backend.services.ChatService;
import es.aaracubel.paicasso.backend.services.InformeService;
import es.aaracubel.paicasso.backend.services.LLMService;
import es.aaracubel.paicasso.backend.services.SonarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
public class LLMController {

    private final LLMService llmService;
    private final SonarService sonarService;
    private final ChatService chatService;
    private final InformeService informeService;

    @PostMapping("/analizar")
    public ResponseEntity<Map<String, Object>> analizar(@RequestBody LLMRequestDTO request) {
        Long usuarioId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        // Obtener o crear sesión
        SesionChat sesion = chatService.obtenerSesion(request.getRepoId());

        // Cargar historial ANTES de guardar el nuevo mensaje (memoria de la conversación)
        List<Mensaje> historial = chatService.obtenerMensajes(sesion.getId());

        // Guardar mensaje del usuario
        chatService.guardarMensaje(sesion, request.getMensaje(), "usuario");

        // Si el frontend envía solo el nombre del archivo, lo prefijamos con el projectKey
        // que SonarQube espera ("paicasso_<repoId>:<archivo>").
        String componentKey = request.getComponentKey();
        if (componentKey != null && !componentKey.contains(":") && request.getRepoId() != null) {
            componentKey = "paicasso_" + request.getRepoId() + ":" + componentKey;
        }

        // Construir contexto y llamar a la IA
        String contexto = sonarService.construirContextoSonar(
                request.getRepoId(),
                componentKey,
                request.getLineaError()
        );
        String respuesta = llmService.analizar(usuarioId, contexto, request.getMensaje(), historial);

        // Guardar respuesta de la IA
        chatService.guardarMensaje(sesion, respuesta, "LLM");

        // Devolver respuesta + sesionId para que el frontend lo mantenga
        return ResponseEntity.ok(Map.of(
                "sesionId", sesion.getId(),
                "respuesta", respuesta
        ));
    }

    @GetMapping("/sesion/{repoId}/mensajes")
    public ResponseEntity<Map<String, Object>> obtenerMensajes(@PathVariable Long repoId) {
        SesionChat sesion = chatService.obtenerSesion(repoId);
        List<Mensaje> mensajes = chatService.obtenerMensajes(sesion.getId());
        return ResponseEntity.ok(Map.of(
                "sesionId", sesion.getId(),
                "mensajes", mensajes
        ));
    }

    @GetMapping("/informe/{repoId}")
    public ResponseEntity<InformeDTO> obtenerInforme(@PathVariable Long repoId){
        InformeDTO informe = informeService.obtenerInforme(repoId);
        if (informe == null) {
            String contextoSonar = sonarService.construirContextoSonar(repoId, null, null);
            informe = llmService.generarInforme(repoId, contextoSonar);
        }
        return ResponseEntity.ok(informe);
    }
}
