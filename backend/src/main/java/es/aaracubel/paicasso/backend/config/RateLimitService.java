package es.aaracubel.paicasso.backend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    public enum Category {
        LLM_CHAT,
        LLM_INFORME,
        ANALISIS,
        GITHUB,
        GENERAL
    }

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String userId, Category category) {
        String key = userId + ":" + category.name();
        return buckets.computeIfAbsent(key, k -> newBucket(category));
    }

    private Bucket newBucket(Category category) {
        return switch (category) {
            case LLM_CHAT    -> Bucket.builder()
                    .addLimit(Bandwidth.builder().capacity(10).refillGreedy(10, Duration.ofMinutes(1)).build())
                    .build();
            case LLM_INFORME -> Bucket.builder()
                    .addLimit(Bandwidth.builder().capacity(3).refillGreedy(3, Duration.ofMinutes(10)).build())
                    .build();
            case ANALISIS    -> Bucket.builder()
                    .addLimit(Bandwidth.builder().capacity(5).refillGreedy(5, Duration.ofHours(1)).build())
                    .build();
            case GITHUB      -> Bucket.builder()
                    .addLimit(Bandwidth.builder().capacity(20).refillGreedy(20, Duration.ofMinutes(1)).build())
                    .build();
            case GENERAL     -> Bucket.builder()
                    .addLimit(Bandwidth.builder().capacity(100).refillGreedy(100, Duration.ofMinutes(1)).build())
                    .build();
        };
    }
}
