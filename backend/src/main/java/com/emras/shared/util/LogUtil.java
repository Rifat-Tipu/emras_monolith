package com.emras.shared.util;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
/**
 * Structured logging utility used across all service layers.
 *
 * Instead of writing raw log.info() calls everywhere with inconsistent
 * formats, all services use these helpers so every log entry has
 * the same structure and MDC context.
 *
 * Usage in a service:
 *   LogUtil.logServiceEntry("ProductService", "findById", "id", id);
 *   LogUtil.logServiceExit("ProductService", "findById");
 *   LogUtil.logCacheHit("ProductService", "products", id);
 */
@Slf4j
public final class LogUtil {

    private LogUtil() {}

    // ── Service layer ─────────────────────────────────────────────────────
    public static void logServiceEntry(String service, String method, Object... params) {
        if (params.length > 0 && params.length % 2 == 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i += 2) {
                if (i > 0) sb.append(", ");
                sb.append(params[i]).append("=").append(params[i + 1]);
            }
            log.debug("[{}] {}.{}() called — {}",
                    TraceUtil.getTraceId(), service, method, sb);
        } else {
            log.debug("[{}] {}.{}() called",
                    TraceUtil.getTraceId(), service, method);
        }
    }

    public static void logServiceExit(String service, String method) {
        log.debug("[{}] {}.{}() completed",
                TraceUtil.getTraceId(), service, method);
    }

    // ── Cache events ──────────────────────────────────────────────────────

    public static void logCacheHit(String service, String cacheName, Object key) {
        log.debug("[{}] CACHE HIT — cache={} key={} service={}",
                TraceUtil.getTraceId(), cacheName, key, service);
    }

    public static void logCacheMiss(String service, String cacheName, Object key) {
        log.debug("[{}] CACHE MISS — cache={} key={} service={}",
                TraceUtil.getTraceId(), cacheName, key, service);
    }

    public static void logCacheEvict(String service, String cacheName, Object key) {
        log.debug("[{}] CACHE EVICT — cache={} key={} service={}",
                TraceUtil.getTraceId(), cacheName, key, service);
    }

    // ── Business events ───────────────────────────────────────────────────

    public static void logBusinessEvent(String event, Object... params) {
        StringBuilder sb = new StringBuilder();
        if (params.length % 2 == 0) {
            for (int i = 0; i < params.length; i += 2) {
                if (i > 0) sb.append(", ");
                sb.append(params[i]).append("=").append(params[i + 1]);
            }
        }
        log.info("[{}] EVENT:{} — {}",
                TraceUtil.getTraceId(), event, sb);
    }

    // ── Security events ───────────────────────────────────────────────────

    public static void logSecurityEvent(String event, String email) {
        log.info("[{}] SECURITY:{} — email={}",
                TraceUtil.getTraceId(), event, maskEmail(email));
    }

    public static void logSecurityFailure(String event, String email, String reason) {
        log.warn("[{}] SECURITY_FAIL:{} — email={} reason={}",
                TraceUtil.getTraceId(), event, maskEmail(email), reason);
    }

    // ── AI events ────────────────────────────────────────────────────────

    public static void logAiToolCall(String toolName, Long userId) {
        log.info("[{}] AI_TOOL:{} — userId={}",
                TraceUtil.getTraceId(), toolName, userId);
    }

    public static void logAiResponse(Long userId, String sessionId, boolean toolUsed) {
        log.info("[{}] AI_RESPONSE — userId={} sessionId={} toolUsed={}",
                TraceUtil.getTraceId(), userId, sessionId, toolUsed);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Masks email for safe logging — john.doe@gmail.com → j***@gmail.com
     */
    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@");
        return parts[0].charAt(0) + "***@" + parts[1];
    }
}