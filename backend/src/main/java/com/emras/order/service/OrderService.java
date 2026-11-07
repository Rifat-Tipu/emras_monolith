package com.emras.order.service;

import com.emras.order.dto.request.PlaceOrderRequest;
import com.emras.order.dto.request.UpdateOrderStatusRequest;
import com.emras.order.dto.response.OrderResponse;
import com.emras.shared.model.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse placeOrder(Long userId, PlaceOrderRequest request);

    OrderResponse getOrderById(Long userId, Long orderId);

    PagedResponse<OrderResponse> getUserOrders(Long userId, Pageable pageable);

    // Admin
    PagedResponse<OrderResponse> getAllOrders(Pageable pageable);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    void cancelOrder(Long userId, Long orderId);
}