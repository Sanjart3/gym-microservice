package org.example.trainingevent.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Component
@Slf4j
public class SLF4JMDCFilter extends OncePerRequestFilter {
    private static final String MDC_UUID_TOKEN_KEY = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, FilterChain chain) {
        try {
            String transactionID = getIdOrGenerate(request);
            MDC.put(MDC_UUID_TOKEN_KEY, transactionID);
            chain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Exception occurred in filter while setting UUID for logs", ex);
        } finally {
            MDC.remove(MDC_UUID_TOKEN_KEY);
        }
    }

    private static String getIdOrGenerate(HttpServletRequest httpRequest) {
        String id = httpRequest.getHeader(MDC_UUID_TOKEN_KEY);

        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    @Override
    protected boolean isAsyncDispatch(final HttpServletRequest request) {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}
