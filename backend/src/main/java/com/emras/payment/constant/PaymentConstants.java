package com.emras.payment.constant;
import java.math.BigDecimal;
public final class PaymentConstants {
    private PaymentConstants() {
    }
    public static final BigDecimal MIN_ORDER_AMOUNT = BigDecimal.valueOf(100);
    public static final String BKASH_NUMBER = "01XXXXXXXXX";
    public static final String NAGAD_NUMBER = "01XXXXXXXXX";
    public static final int MAX_PROMO_CODE_LENGTH = 20;
}