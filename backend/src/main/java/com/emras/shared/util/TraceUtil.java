package com.emras.shared.util;

import org.slf4j.MDC;

public final class TraceUtil {

    private TraceUtil() {}

    public static final String TRACE_ID_KEY      = "traceId";
    public static final String USER_ID_KEY       = "userId";
    public static final String HTTP_METHOD_KEY   = "httpMethod";
    public static final String REQUEST_URI_KEY   = "requestUri";
    public static final String RESPONSE_CODE_KEY = "responseCode";

    public static String getTraceId() {
        String id = MDC.get(TRACE_ID_KEY);
        return id != null ? id : "N/A";
    }

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static void setUserId(String userId) {
        MDC.put(USER_ID_KEY, userId);
    }

    public static void clear() {
        MDC.clear();
    }
}
