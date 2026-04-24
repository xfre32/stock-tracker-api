package com.xfre32.stocktracker.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xfre32.stocktracker.config.AppProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(1) // Ensure this filter runs before other filters
class RateLimitFilter implements Filter {
    private final AppProperties props;
    private final Cache<String, Bucket> buckets;

    RateLimitFilter(AppProperties props) {
        this.props = props;
        this.buckets = Caffeine.newBuilder()
                .maximumSize(props.rateLimit().maxBuckets())
                .expireAfterAccess(Duration.ofSeconds(props.rateLimit().bucketTtlSeconds()))
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // Only rate-limit API endpoints
        if (!path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = httpRequest.getRemoteAddr();
        Bucket bucket = buckets.asMap().computeIfAbsent(clientIp, k -> createBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("""
                {"status": 429, "message": "Rate limit exceeded. Please wait and try again."}
                """);
        }
    }

    private Bucket createBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(props.rateLimit().capacity())
                        .refillGreedy(props.rateLimit().refillTokens(),
                                    Duration.ofSeconds(props.rateLimit().refillDuration()))
                        .build())
                .build();
    }

}
