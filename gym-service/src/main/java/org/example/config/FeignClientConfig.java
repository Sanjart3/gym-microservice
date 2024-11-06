package org.example.config;

import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeoutException;

@Configuration
public class FeignClientConfig {

    private static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String correlationId = MDC.get(CORRELATION_ID_HEADER_NAME);
            if (correlationId != null) {
                template.header(CORRELATION_ID_HEADER_NAME, correlationId);
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {

        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            if (response.status() == 408 || response.status() == 504) {
                return new TimeoutException("Request to " + methodKey + " timed out");
            }
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
