package com.emras.shared.config.web;

import com.emras.shared.util.TraceUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcFilter extends OncePerRequestFilter {

    private static final String X_TRACE_ID = "X-Trace-Id";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String traceId = request.getHeader(X_TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        TraceUtil.setTraceId(traceId);
        MDC_put(TraceUtil.HTTP_METHOD_KEY,  request.getMethod());
        MDC_put(TraceUtil.REQUEST_URI_KEY,  request.getRequestURI());

        response.setHeader(X_TRACE_ID, traceId);

        log.debug("Incoming request: method={} uri={} traceId={}",
                request.getMethod(), request.getRequestURI(), traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            log.debug("Completed request: method={} uri={} status={} traceId={}",
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(), traceId);
            TraceUtil.clear();
        }
    }

    private void MDC_put(String key, String value) {
        org.slf4j.MDC.put(key, value);
    }
}
