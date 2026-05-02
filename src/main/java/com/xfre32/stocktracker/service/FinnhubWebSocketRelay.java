package com.xfre32.stocktracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xfre32.stocktracker.config.AppProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletionStage;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinnhubWebSocketRelay implements WebSocket.Listener {
    private final AppProperties props;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    private WebSocket webSocket;
    private final Set<String> subscribedSymbols = ConcurrentHashMap.newKeySet();

    @PostConstruct
    void connect() {
        try {
            String url = props.finnhub().wsUrl() + "?token=" + props.finnhub().apiKey();
            webSocket = HttpClient.newHttpClient()
                    .newWebSocketBuilder()
                    .buildAsync(URI.create(url), this)
                    .join();
            log.info("Connected to Finnhub WebSocket");
        } catch (Exception e) {
            log.error("Failed to connect to Finnhub WebSocket", e);
        }
    }

    public void subscribe(String symbol) {
        if (subscribedSymbols.add(symbol)) {
            forceSubscribe(symbol);
            log.debug("Subscribed to {}", symbol);
        }
    }

    public void unsubscribe(String symbol) {
        if (subscribedSymbols.remove(symbol) && webSocket != null) {
            webSocket.sendText("{\"type\":\"unsubscribe\",\"symbol\":\"" + symbol + "\"}", true);
        }
    }

    private void forceSubscribe(String symbol) {
        if (webSocket != null) {
            webSocket.sendText("{\"type\":\"subscribe\",\"symbol\":\"" + symbol + "\"}", true);
        }
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        try {
            JsonNode json = objectMapper.readTree(data.toString());
            if ("trade".equals(json.path("type").asText())) {
                JsonNode trades = json.path("data");
                if (trades.isArray() && !trades.isEmpty()) {
                    JsonNode latest = trades.get(trades.size() - 1);
                    Map<String, Object> trade = Map.of(
                            "symbol", latest.path("s").asText(),
                            "price", latest.path("p").asDouble(),
                            "timestamp", latest.path("t").asLong()
                    );
                    messagingTemplate.convertAndSend("/topic/trades", (Object) trade);
                }
            }
        } catch (Exception e) {
            log.error("Error processing Finnhub message", e);
        }
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        log.warn("Finnhub WebSocket closed: {} - {}", statusCode, reason);
        // Reconnect after 5 seconds
        new Thread(() -> {
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            connect();
            subscribedSymbols.forEach(this::forceSubscribe);
        }).start();
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @PreDestroy
    void disconnect() {
        if (webSocket != null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Shutting down");
        }
    }
}
