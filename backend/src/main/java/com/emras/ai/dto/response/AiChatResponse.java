package com.emras.ai.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * AI assistant response exposed via AiFacade.
 */
@Getter
@Builder
public class AiChatResponse {

    private final String  sessionId;
    private final String  reply;
    private final boolean toolWasUsed;   // true if AI called a backend tool
    private final String  toolName;      // which tool was called (if any)
}
