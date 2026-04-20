package com.xfre32.stocktracker.controller;

import com.xfre32.stocktracker.dto.stock.*;
import com.xfre32.stocktracker.service.FinnhubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
class StockController {
    private final FinnhubService finnhubService;

    @GetMapping("/search")
    public SearchResponseDto search(@RequestParam String q) {
        return finnhubService.searchStock(q);
    }

    @GetMapping("/{symbol}/quote")
    public StockQuoteDto getQuote(@PathVariable String symbol) {
        return finnhubService.getQuote(symbol.toUpperCase());
    }

    @GetMapping("/{symbol}/profile")
    public CompanyProfileDto getProfile(@PathVariable String symbol) {
        return finnhubService.getCompanyProfile(symbol.toUpperCase());
    }

    @GetMapping("/{symbol}/news")
    public List<CompanyNewsDto> getNews(@PathVariable String symbol,
                                        @RequestParam String from,
                                        @RequestParam String to) {
        return finnhubService.getCompanyNews(symbol.toUpperCase(), from, to);
    }

    @GetMapping("/{symbol}/sentiment")
    public InsiderSentimentDto getSentiment(@PathVariable String symbol,
                                            @RequestParam String from,
                                            @RequestParam String to) {
        return finnhubService.getInsiderSentiment(symbol.toUpperCase(), from, to);
    }

}
