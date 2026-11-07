package com.emras.order.service.impl;

import com.emras.cart.dto.response.CartSummaryResponse;
import com.emras.cart.facade.CartFacade;
import com.emras.order.constant.OrderConstants;
import com.emras.order.dto.request.PlaceOrderRequest;
import com.emras.order.dto.request.UpdateOrderStatusRequest;
import com.emras.order.dto.response.OrderItemResponse;
import com.emras.order.dto.response.OrderResponse;
import com.emras.order.dto.response.OrderSummaryResponse;
import com.emras.order.facade.OrderFacade;
import com.emras.order.model.Order;
import com.emras.order.model.OrderItem;
import com.emras.order.model.OrderStatus;
import com.emras.order.model.PaymentStatus;
import com.emras.order.repository.OrderRepository;
import com.emras.order.service.OrderService;
import com.emras.payment.model.DiscountType;
import com.emras.payment.model.PaymentMethod;
import com.emras.payment.model.PromoCode;
import com.emras.payment.repository.PromoCodeRepository;
import com.emras.product.facade.ProductFacade;
import com.emras.shared.constant.ErrorMessages;
import com.emras.shared.exception.BusinessException;
import com.emras.shared.exception.ResourceNotFoundException;
import com.emras.shared.model.PagedResponse;
import com.emras.shared.util.LogUtil;
import com.emras.user.repository.AddressRepository;
import com.emras.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService, OrderFacade {

    private final OrderRepository    orderRepository;
    private final UserRepository     userRepository;
    private final AddressRepository  addressRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final CartFacade         cartFacade;
    private final ProductFacade      productFacade;

    // ── Place Order ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        LogUtil.logServiceEntry("OrderServiceImpl", "placeOrder", "userId", userId);

        // 1. Validate cart is not empty
        if (cartFacade.isCartEmpty(userId)) {
            throw new BusinessException(
                    ErrorMessages.CART_IS_EMPTY,
                    ErrorMessages.Code.CART_EMPTY,
                    HttpStatus.BAD_REQUEST
            );
        }

        // 2. Get cart summary
        CartSummaryResponse cart = cartFacade.getCartSummary(userId);

        // 3. Validate payment method
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "Invalid payment method: " + request.getPaymentMethod(),
                    ErrorMessages.Code.INVALID_INPUT,
                    HttpStatus.BAD_REQUEST
            );
        }

        // 4. Get address
        var address = addressRepository
                .findByIdAndUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ADDRESS_NOT_FOUND));

        // 5. Get user
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.USER_NOT_FOUND));

        // 6. Apply promo code if provided
        BigDecimal subtotal       = cart.getSubtotal();
        BigDecimal discountAmount = BigDecimal.ZERO;
        String     promoCodeStr   = null;
        PromoCode  promoCode      = null;

        if (request.getPromoCode() != null && !request.getPromoCode().isBlank()) {
            promoCode = promoCodeRepository
                    .findByCodeForUpdate(request.getPromoCode())
                    .orElseThrow(() -> new BusinessException(
                            ErrorMessages.PROMO_CODE_INVALID,
                            ErrorMessages.Code.PROMO_CODE_INVALID,
                            HttpStatus.BAD_REQUEST
                    ));

            if (!promoCode.isApplicable(subtotal)) {
                if (promoCode.isExpired()) {
                    throw new BusinessException(
                            ErrorMessages.PROMO_CODE_EXPIRED,
                            ErrorMessages.Code.PROMO_CODE_EXPIRED,
                            HttpStatus.BAD_REQUEST
                    );
                }
                if (promoCode.isUsageLimitReached()) {
                    throw new BusinessException(
                            ErrorMessages.PROMO_CODE_LIMIT_REACHED,
                            ErrorMessages.Code.PROMO_CODE_LIMIT_REACHED,
                            HttpStatus.BAD_REQUEST
                    );
                }
                throw new BusinessException(
                        ErrorMessages.PROMO_MIN_ORDER_NOT_MET,
                        ErrorMessages.Code.PROMO_MIN_ORDER_NOT_MET,
                        HttpStatus.BAD_REQUEST
                );
            }

            discountAmount = promoCode.calculateDiscount(subtotal);
            promoCodeStr   = promoCode.getCode();
        }

        BigDecimal totalAmount = subtotal.subtract(discountAmount);

        // 7. Validate stock for all items and deduct
        cart.getItems().forEach(item -> {
            if (!productFacade.hasSufficientStock(
                    item.getVariantId(), item.getQuantity())) {
                throw new BusinessException(
                        "Insufficient stock for: " + item.getProductName(),
                        ErrorMessages.Code.CART_INSUFFICIENT_STOCK,
                        HttpStatus.CONFLICT
                );
            }
        });

        // Deduct stock
        cart.getItems().forEach(item ->
                productFacade.deductStock(item.getVariantId(), item.getQuantity()));

        // 8. Build order
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .address(address)
                .status(OrderStatus.PLACED)
                .paymentStatus(paymentMethod.isCashOnDelivery()
                        ? PaymentStatus.PENDING
                        : PaymentStatus.PENDING)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .promoCode(promoCodeStr)
                .notes(request.getNotes())
                .build();

        // 9. Build order items from cart (snapshot)
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(item -> OrderItem.builder()
                        .order(order)
                        .productName(item.getProductName())
                        .size(item.getSize())
                        .color(item.getColor())
                        .sku("") // will be set below
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .lineTotal(item.getLineTotal())
                        .build())
                .toList();

        order.getItems().addAll(orderItems);
        orderRepository.save(order);

        // 10. Increment promo code usage
        if (promoCode != null) {
            promoCode.incrementUsage();
            promoCodeRepository.save(promoCode);
        }

        // 11. Clear cart
        cartFacade.clearCart(userId);

        LogUtil.logBusinessEvent("ORDER_PLACED",
                "orderId", order.getId(),
                "orderNumber", order.getOrderNumber(),
                "total", totalAmount);

        return toResponse(order);
    }

    // ── Get order ─────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ORDER_NOT_FOUND));
    }

    // ── Get user orders ───────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return PagedResponse.of(
                orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                        .map(this::toResponse));
    }

    // ── Admin: get all orders ─────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAllOrders(Pageable pageable) {
        return PagedResponse.of(
                orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                        .map(this::toResponse));
    }

    // ── Admin: update status ──────────────────────────────────────────────

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId,
                                           UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ORDER_NOT_FOUND));

        try {
            order.setStatus(OrderStatus.valueOf(request.getStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "Invalid order status: " + request.getStatus(),
                    ErrorMessages.Code.ORDER_STATUS_INVALID,
                    HttpStatus.BAD_REQUEST
            );
        }

        return toResponse(order);
    }

    // ── Cancel order ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ORDER_NOT_FOUND));

        if (!order.getStatus().isCancellable()) {
            throw new BusinessException(
                    ErrorMessages.ORDER_CANNOT_CANCEL,
                    ErrorMessages.Code.ORDER_CANNOT_CANCEL,
                    HttpStatus.BAD_REQUEST
            );
        }

        // Restore stock
        order.getItems().forEach(item -> {
            if (item.getVariant() != null) {
                productFacade.restoreStock(
                        item.getVariant().getId(), item.getQuantity());
            }
        });

        order.setStatus(OrderStatus.CANCELLED);
        LogUtil.logBusinessEvent("ORDER_CANCELLED",
                "orderId", orderId, "userId", userId);
    }

    // ── OrderFacade impl ──────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public OrderSummaryResponse getOrderSummary(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .map(o -> OrderSummaryResponse.builder()
                        .orderId(o.getId())
                        .orderNumber(o.getOrderNumber())
                        .status(o.getStatus().name())
                        .paymentStatus(o.getPaymentStatus().name())
                        .totalAmount(o.getTotalAmount())
                        .placedAt(o.getCreatedAt())
                        .build())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ORDER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getRecentOrders(Long userId, int limit) {
        return orderRepository.findRecentByUserId(
                        userId,
                        Pageable.ofSize(limit))
                .stream()
                .map(o -> OrderSummaryResponse.builder()
                        .orderId(o.getId())
                        .orderNumber(o.getOrderNumber())
                        .status(o.getStatus().name())
                        .paymentStatus(o.getPaymentStatus().name())
                        .totalAmount(o.getTotalAmount())
                        .placedAt(o.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void markPaymentVerified(Long orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setPaymentStatus(PaymentStatus.VERIFIED);
            order.setStatus(OrderStatus.PROCESSING);
        });
    }

    @Override
    @Transactional
    public void markPaymentFailed(Long orderId) {
        orderRepository.findById(orderId).ifPresent(order ->
                order.setPaymentStatus(PaymentStatus.FAILED));
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private String generateOrderNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(5);
        String random    = String.format("%04d", new Random().nextInt(10000));
        return OrderConstants.ORDER_NUMBER_PREFIX + "-" + timestamp + "-" + random;
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .orderItemId(item.getId())
                        .variantId(item.getVariant() != null
                                ? item.getVariant().getId() : null)
                        .productName(item.getProductName())
                        .size(item.getSize())
                        .color(item.getColor())
                        .sku(item.getSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .lineTotal(item.getLineTotal())
                        .build())
                .toList();

        var address = o.getAddress();

        return OrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .status(o.getStatus().name())
                .paymentStatus(o.getPaymentStatus().name())
                .subtotal(o.getSubtotal())
                .discountAmount(o.getDiscountAmount())
                .totalAmount(o.getTotalAmount())
                .promoCode(o.getPromoCode())
                .notes(o.getNotes())
                .items(items)
                .placedAt(o.getCreatedAt())
                .recipientName(address != null ? address.getRecipientName() : null)
                .phone(address != null ? address.getPhone() : null)
                .street(address != null ? address.getStreet() : null)
                .area(address != null ? address.getArea() : null)
                .city(address != null ? address.getCity() : null)
                .district(address != null ? address.getDistrict() : null)
                .build();
    }
}