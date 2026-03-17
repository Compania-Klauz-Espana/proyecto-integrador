package com.company.backend.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter itemsCreatedCounter(MeterRegistry registry) {
        return Counter.builder("items.created.total")
                .description("Total number of items created")
                .register(registry);
    }

    @Bean
    public Counter itemsDeletedCounter(MeterRegistry registry) {
        return Counter.builder("items.deleted.total")
                .description("Total number of items deleted")
                .register(registry);
    }
}
