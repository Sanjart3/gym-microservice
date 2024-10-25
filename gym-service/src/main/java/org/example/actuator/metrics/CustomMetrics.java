package org.example.actuator.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;

@Component
public class CustomMetrics {
    private final Counter counter;
    public CustomMetrics(MeterRegistry meterRegistry) {
        this.counter = meterRegistry.counter("custom_metric_total", "type", "counter");
    }

    public void incrementCounter(){
        this.counter.increment();
    }
}
