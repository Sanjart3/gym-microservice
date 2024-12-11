package org.example.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.externaldto.TrainingEventDto;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import static org.example.config.LogFilter.CORRELATION_ID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainingEventSender {

    private final JmsTemplate jmsTemplate;

    @Value("${spring.activemq.queue-name}")
    private String queueName;

    public void sendEvent(TrainingEventDto trainingEventDto) {
        log.info("Sending message from queue {} : {}", queueName, trainingEventDto);
        try {

            jmsTemplate.convertAndSend(queueName, trainingEventDto, message -> {
                message.setStringProperty(CORRELATION_ID, MDC.get(CORRELATION_ID));
                return message;
            });
        } catch (JmsException e) {
            log.warn("Error sending message to queue {} : {}", queueName, e.getMessage(), e);
        }
    }
}
