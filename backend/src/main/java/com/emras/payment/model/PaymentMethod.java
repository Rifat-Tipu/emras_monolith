package com.emras.payment.model;
public enum PaymentMethod {
    CASH_ON_DELIVERY,
    BKASH_MANUAL,
    NAGAD_MANUAL,
    // Phase 5 — official gateway integration
    BKASH_GATEWAY,
    NAGAD_GATEWAY;
    public boolean isManual() {
        return this == BKASH_MANUAL || this == NAGAD_MANUAL;
    }
    public boolean isCashOnDelivery() {
        return this == CASH_ON_DELIVERY;
    }
    public boolean isGateway() {
        return this == BKASH_GATEWAY || this == NAGAD_GATEWAY;
    }
}