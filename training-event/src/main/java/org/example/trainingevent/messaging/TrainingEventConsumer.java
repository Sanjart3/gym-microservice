package org.example.trainingevent.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trainingevent.dto.TrainingEventsDto;
import org.example.trainingevent.services.TrainingService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static org.springframework.jms.support.JmsHeaders.CORRELATION_ID;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrainingEventConsumer {
    private final TrainingService trainingService;
    @Value("${spring.application.queue_name}")
    private final static String QUEUE_NAME = "training-event-queue";

    @JmsListener(destination = QUEUE_NAME)
    public void receiveMessage(TrainingEventsDto trainingEventDTO, @Header(CORRELATION_ID) String correlationId) {
        addTraceId(correlationId);

        try {
            log.debug("Received message from queue {}: {}", QUEUE_NAME, trainingEventDTO);
            trainingService.saveTrainingEvent(trainingEventDTO);
        } catch (Exception e) {
            log.error("Error processing message from queue {}: {}", QUEUE_NAME, trainingEventDTO, e);
        }
    }

    private static void addTraceId(String correlationId) {
        MDC.put(CORRELATION_ID, correlationId);
    }
}
