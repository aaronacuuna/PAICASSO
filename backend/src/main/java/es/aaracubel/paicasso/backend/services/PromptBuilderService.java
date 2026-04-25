package es.aaracubel.paicasso.backend.services;

import es.aaracubel.paicasso.backend.dtos.ConfiguracionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptBuilderService {

    private final ConfiguracionService configuracionService;

    public String construirPrompt(Long usuarioId, String contexto, String mensaje) {
        ConfiguracionDTO conf = configuracionService.obtenerConfiguracion(usuarioId);
        String nivelExperiencia = conf.getExperienceLevel();
        List<String> prioridades = conf.getPriorities();
        boolean addComments = conf.isAddComments();

        String instruccionesNivel = switch (nivelExperiencia) {
            case "junior" -> """
                - Explica cada concepto paso a paso como si fuera la primera vez que lo ve.
                - Usa analogías y ejemplos del mundo real para clarificar ideas complejas.
                - Define términos técnicos antes de usarlos.
                - Divide las soluciones en pasos pequeños y fáciles de seguir.""";
            case "mid" -> """
                - Ve directo al grano pero incluye contexto técnico relevante.
                - Puedes asumir conocimiento de patrones de diseño y buenas prácticas básicas.
                - Explica el "por qué" de las decisiones técnicas, no solo el "cómo".""";
            case "senior" -> """
                - Responde de forma concisa y técnica, sin explicaciones básicas.
                - Proporciona solo código limpio y referencias a documentación oficial si es necesario.
                - Asume conocimiento profundo de arquitectura, patrones y rendimiento.""";
            default -> "- Responde de forma clara y técnica.";
        };

        String instruccionesPrioridades = prioridades.isEmpty() ? "" : buildPrioridadesPrompt(prioridades);

        String instruccionComentarios = addComments
                ? "- Añade comentarios explicativos en todo el código que generes."
                : "- No añadas comentarios en el código generado salvo que sean estrictamente necesarios.";

        return """
            Eres PAICASSO, un asistente experto en calidad de software integrado con SonarQube.
            Tu objetivo es ayudar al desarrollador a entender y corregir los problemas de calidad \
            detectados en su código.
            
            IMPORTANTE: Tu respuesta debe ser ÚNICAMENTE el mensaje final dirigido al usuario. No incluyas tu proceso de pensamiento ("thinking process"), notas de planificación, listas de pasos internos ni metadatos sobre tu rol.
            
            REGLA DE ORO: Responde SIEMPRE de la forma MÁS BREVE y CONCISA posible. Ve directamente al grano, sin rodeos, sin introducciones largas ni conclusiones innecesarias. Limítate a dar la solución o explicación de la manera más directa.
            
            ## Instrucciones de comportamiento según el nivel del desarrollador (%s)
            %s
            
            ## Instrucciones sobre el código generado
            %s
            %s
            
            ## Contexto del análisis de SonarQube
            %s
            
            ## Mensaje del usuario
            %s
            """.formatted(
                nivelExperiencia,
                instruccionesNivel,
                instruccionComentarios,
                instruccionesPrioridades,
                contexto,
                mensaje
        );
    }

    private String buildPrioridadesPrompt(List<String> prioridades) {
        if (prioridades.isEmpty()) return "";

        StringBuilder sb = new StringBuilder("- Cuando analices problemas y generes código, haz especial énfasis en:\n");

        prioridades.forEach(p -> {
            String instruccion = switch (p) {
                case "security" -> """
                    - **Seguridad**: señala activamente vulnerabilidades, valida entradas, \
                    aplica principios OWASP y advierte de riesgos aunque el usuario no los mencione.""";
                case "readability" -> """
                    - **Legibilidad**: prioriza código limpio, nombres descriptivos, \
                    funciones pequeñas y elimina duplicados aunque funcione correctamente.""";
                case "performance" -> """
                    - **Rendimiento**: identifica cuellos de botella, sugiere algoritmos más \
                    eficientes y advierte de operaciones costosas en bucles o consultas.""";
                default -> "- " + p;
            };
            sb.append(instruccion).append("\n");
        });

        return sb.toString();
    }

    public String construirPromptDiagnostico(String contextoSonar) {
        return """
            Eres PAICASSO, un experto en calidad de software.
            Analiza las métricas del siguiente proyecto y genera un diagnóstico técnico profesional.
            
            El diagnóstico debe:
            - Evaluar el estado general del proyecto (calidad, deuda técnica, seguridad)
            - Identificar los puntos críticos que necesitan atención urgente
            - Ser objetivo y basado exclusivamente en los datos proporcionados
            - Tener un tono técnico pero comprensible
            - Tener una extensión de 3-5 párrafos
            
            ## Datos del proyecto
            %s
            
            Genera únicamente el diagnóstico que se va a devolver en pantalla, sin saludos ni introducciones.
            """.formatted(contextoSonar);
    }

    public String construirPromptPropuesta(String contextoSonar) {
        return """
            Eres PAICASSO, un experto en calidad de software.
            Basándote en las métricas del siguiente proyecto, genera un plan de mejora concreto y accionable.
            
            La propuesta debe:
            - Priorizar las mejoras por impacto (primero lo más crítico)
            - Incluir acciones concretas y realizables
            - Estimar el esfuerzo relativo de cada mejora (bajo/medio/alto)
            - Estar estructurada en puntos claros
            
            ## Datos del proyecto
            %s
            
            Genera únicamente el plan de mejora que se va a devolver en pantalla, sin saludos ni introducciones.
            """.formatted(contextoSonar);
    }

}
