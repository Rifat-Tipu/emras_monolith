package com.emras.ai.facade;

import com.emras.ai.dto.response.AiChatResponse;

/**
 * Public API of the AI domain.
 *
 * Currently no other domain calls into the AI domain —
 * this facade is defined for architectural completeness and
 * future extensibility (e.g. if the notification domain wants
 * to use AI to generate personalized emails).
 *
 * The AI domain itself calls into other facades:
 *   ProductFacade  → search, stock checks
 *   CartFacade     → add/remove items, view cart
 *   OrderFacade    → place orders, check order status
 *   PaymentFacade  → apply promo codes
 */
public interface AiFacade {

    /**
     * Send a user message to the AI assistant and get a response.
     * The session ID maintains conversation history.
     *
     * @param userId    the authenticated user's ID
     * @param sessionId the conversation session ID
     * @param message   the user's message text
     * @return AiChatResponse with the assistant's reply
     */
    AiChatResponse chat(Long userId, String sessionId, String message);
}
