package br.com.alertasp.soa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfiguration {

    // Relógio injetável: facilita testes e evita espalhar chamadas a now() pelo código.
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
