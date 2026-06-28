package com.emras.order.model;
public enum OrderStatus {
    PLACED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURN_REQUESTED,
    RETURNED;
    public boolean isCancellable() {
        return this == PLACED || this == PROCESSING;
    }
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED || this == RETURNED;
    }
}