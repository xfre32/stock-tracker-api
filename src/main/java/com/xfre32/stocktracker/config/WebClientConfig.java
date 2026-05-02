package com.xfre32.stocktracker.config;

import lombok.RequiredArgsConstructor;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final AppProperties props;

    @Bean
    public WebClient finnhubWebClient() {
        return WebClient.builder()
                .baseUrl(props.finnhub().baseUrl())
                .clientConnector(reactorClientHttpConnector())
                .defaultHeader("X-Finnhub-Token", props.finnhub().apiKey())
                .build();
    }

    @Bean
    public WebClient twelveDataWebClient() {
        return WebClient.builder()
                .baseUrl(props.twelveData().baseUrl())
                .clientConnector(reactorClientHttpConnector())
                .build();
    }

    @Bean
    public ReactorClientHttpConnector reactorClientHttpConnector() {
        AppProperties.ExternalApi timeout = props.externalApi();

        HttpClient client = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout.connectTimeoutMs())
                .responseTimeout(Duration.ofMillis(timeout.responseTimeoutMs()))
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(timeout.readTimeoutMs(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(timeout.writeTimeoutMs(), TimeUnit.MILLISECONDS)));

        return new ReactorClientHttpConnector(client);
    }
}
