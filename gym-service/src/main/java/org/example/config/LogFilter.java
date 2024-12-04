package org.example.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class LogFilter implements Filter {

    public static final String CORRELATION_ID = "X-Correlation-Id";

    private static String getIdOrGenerate(HttpServletRequest httpRequest) {
        String id = httpRequest.getHeader(CORRELATION_ID);

        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String id = getIdOrGenerate(httpRequest);

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        MDC.put(CORRELATION_ID, id);

        logRequest(httpRequest);
        chain.doFilter(request, response);
        logResponse(httpResponse);
    }

    private void logRequest(HttpServletRequest request) {
        // Log request details
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";

        // Use a logger
        log.info("{} {}{} - Request received", method, uri, queryString);
    }

    private void logResponse(HttpServletResponse response) {
        int status = response.getStatus();

        log.info("Response: {}", status);
    }
}
