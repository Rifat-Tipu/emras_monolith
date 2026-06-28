package com.emras.ai.constant;
public final class AiConstants {
    private AiConstants() {}
    public static final int    MAX_HISTORY_MESSAGES   = 20;
    public static final int    MAX_TOKENS             = 1024;
    public static final String DEFAULT_SYSTEM_PROMPT  =
            "You are Emras AI, a friendly shopping assistant for Emras clothing store. " +
                    "You help customers discover products, check availability, manage their cart, " +
                    "and place orders. Always be helpful, concise, and fashion-forward. " +
                    "If you need to perform an action, use the available tools.";
}