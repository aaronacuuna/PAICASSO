package es.aaracubel.paicasso.backend.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class RateLimitFilter implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return true;
        }

        String userId   = auth.getName();
        String uri      = request.getRequestURI();
        String method   = request.getMethod();

        RateLimitService.Category category = resolveCategory(uri, method);
        Bucket bucket = rateLimitService.resolveBucket(userId, category);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        }

        long retryAfterSeconds = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(buildErrorBody(category, retryAfterSeconds));
        return false;
    }

    private RateLimitService.Category resolveCategory(String uri, String method) {
        if (uri.equals("/api/llm/analizar") && "POST".equalsIgnoreCase(method)) {
            return RateLimitService.Category.LLM_CHAT;
        }
        if (uri.startsWith("/api/llm/informe/")) {
            return RateLimitService.Category.LLM_INFORME;
        }
        if (uri.matches("/api/repositorios/[^/]+/analizar") && "POST".equalsIgnoreCase(method)) {
            return RateLimitService.Category.ANALISIS;
        }
        if (uri.equals("/api/repositorios/github") || uri.equals("/api/repositorios/url")) {
            return RateLimitService.Category.GITHUB;
        }
        return RateLimitService.Category.GENERAL;
    }

    private String buildErrorBody(RateLimitService.Category category, long retryAfterSeconds) {
        String mensaje = switch (category) {
            case LLM_CHAT    -> "Has superado el límite de 10 consultas al asistente por minuto.";
            case LLM_INFORME -> "Has superado el límite de 3 informes cada 10 minutos.";
            case ANALISIS    -> "Has superado el límite de 5 análisis por hora.";
            case GITHUB      -> "Has superado el límite de 20 consultas a GitHub por minuto.";
            case GENERAL     -> "Has superado el límite de peticiones. Por favor, espera un momento.";
        };
        return String.format(
                "{\"error\":\"Too Many Requests\",\"mensaje\":\"%s\",\"reintentarEn\":%d}",
                mensaje, retryAfterSeconds
        );
    }
}
