package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@Slf4j
public class WeatherService implements Function<WeatherRequest, WeatherResponse> {

    @Override
    public WeatherResponse apply(WeatherRequest weatherRequest) {
        log.info("WeatherRequest: {}", weatherRequest);
        return new WeatherResponse(20.0, Unit.CELSIUS);
    }

}
