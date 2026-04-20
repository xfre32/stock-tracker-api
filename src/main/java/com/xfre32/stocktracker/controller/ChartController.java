package com.xfre32.stocktracker.controller;

import com.xfre32.stocktracker.dto.stock.CandleDataDto;
import com.xfre32.stocktracker.service.TwelveDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
class ChartController {

    private final TwelveDataService twelveDataService;

    @GetMapping("/{symbol}/timeseries")
    public List<CandleDataDto> getTimeSeries(@PathVariable String symbol,
                                             @RequestParam String interval,
                                             @RequestParam(defaultValue = "66") int outputsize
                                             ) {
        return twelveDataService.getTimeSeries(symbol, interval, outputsize);
    }
}
