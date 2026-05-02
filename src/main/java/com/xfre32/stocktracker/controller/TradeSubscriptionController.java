package com.xfre32.stocktracker.controller;

import com.xfre32.stocktracker.service.FinnhubWebSocketRelay;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class TradeSubscriptionController {
    private final FinnhubWebSocketRelay relay;

    @MessageMapping("/subscribe")
    public void subscribe(@Payload String symbol) {
        relay.subscribe(symbol.trim().toUpperCase());
    }

    @MessageMapping("/unsubscribe")
    public void unsubscribe(@Payload String symbol) {
        relay.unsubscribe(symbol.trim().toUpperCase());
    }
}
