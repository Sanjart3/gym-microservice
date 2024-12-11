package org.example.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.externaldto.TrainingEventDto;
import org.example.services.TrainingService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static org.example.filter.SLF4JMDCFilter.CORRELATION_ID;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrainingEventConsumer {
    private final TrainingService trainingService;
    @Value("${spring.activemq.queue_name}")
    private final static String QUEUE_NAME = "gym-app-queue";

    @JmsListener(destination = QUEUE_NAME)
    public void receiveMessage(TrainingEventDto trainingEventDTO, @Header(CORRELATION_ID) String correlationId) {
        addTraceId(correlationId);

        try {
            log.info("Received message from queue {}: {}", QUEUE_NAME, trainingEventDTO);
            trainingService.saveTrainingEvent(trainingEventDTO);
        } catch (Exception e) {
            log.error("Error processing message from queue {}: {}", QUEUE_NAME, trainingEventDTO, e);
        }
    }

    private static void addTraceId(String correlationId) {
        MDC.put(CORRELATION_ID, correlationId);
    }
}
