package com.example.demo.tools;

import com.example.demo.model.WeatherRequest;
import com.example.demo.model.WeatherResponse;
import com.example.demo.service.WeatherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration(proxyBeanMethods =false)
public class WeatherTools {

    public static final String CURRENT_WEATHER = "currentWeather";

    WeatherService weatherService = new WeatherService();

    @Bean(CURRENT_WEATHER)
    @Description("Get the current weather in a given location")
    Function<WeatherRequest, WeatherResponse> currentWeather(){
        return weatherService;
    }

}
