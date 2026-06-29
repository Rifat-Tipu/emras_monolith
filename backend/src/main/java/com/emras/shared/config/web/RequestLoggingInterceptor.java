package com.emras.shared.config.web;
import com.emras.shared.util.TraceUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
/**
 * Logs every HTTP request and response at INFO level.
 *
 * Runs AFTER MdcFilter (which sets the traceId) so every log line
 * from this interceptor already has the traceId in context.
 *
 * Logs:
 *  → REQUEST:  method, URI, userId (if authenticated)
 *  ← RESPONSE: method, URI, status, duration in ms
 */
@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private static final String START_TIME_ATTR = "requestStartTime";
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        // Inject authenticated userId into MDC if available
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !auth.getName().equals("anonymousUser")) {
            MDC.put(TraceUtil.USER_ID_KEY, auth.getName());
        }
        log.info("[{}] → {} {} | userId={}",
                TraceUtil.getTraceId(),
                request.getMethod(),
                request.getRequestURI(),
                MDC.get(TraceUtil.USER_ID_KEY) != null
                        ? MDC.get(TraceUtil.USER_ID_KEY) : "anonymous");

        return true;
    }
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {

        long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        long duration  = System.currentTimeMillis() - startTime;

        MDC.put(TraceUtil.RESPONSE_CODE_KEY, String.valueOf(response.getStatus()));

        if (ex != null) {
            log.error("[{}] ← {} {} | status={} | duration={}ms | error={}",
                    TraceUtil.getTraceId(),
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    ex.getMessage());
        } else {
            log.info("[{}] ← {} {} | status={} | duration={}ms",
                    TraceUtil.getTraceId(),
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
        }
    }
}