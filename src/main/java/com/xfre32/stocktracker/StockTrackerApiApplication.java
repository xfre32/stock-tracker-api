package com.xfre32.stocktracker;

import com.xfre32.stocktracker.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class StockTrackerApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockTrackerApiApplication.class, args);
    }
}
