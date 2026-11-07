package com.emras.order.controller;

import com.emras.order.dto.request.PlaceOrderRequest;
import com.emras.order.dto.request.UpdateOrderStatusRequest;
import com.emras.order.dto.response.OrderResponse;
import com.emras.order.service.OrderService;
import com.emras.shared.constant.ApiConstants;
import com.emras.shared.constant.SuccessMessages;
import com.emras.shared.model.ApiResponse;
import com.emras.shared.model.PagedResponse;
import com.emras.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.ORDER_BASE)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        SuccessMessages.ORDER_PLACED,
                        orderService.placeOrder(user.getId(), request),
                        HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getUserOrders(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ORDERS_FETCHED,
                orderService.getUserOrders(user.getId(), pageable),
                HttpStatus.OK));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ORDER_FETCHED,
                orderService.getOrderById(user.getId(), orderId),
                HttpStatus.OK));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId) {
        orderService.cancelOrder(user.getId(), orderId);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ORDER_CANCELLED, HttpStatus.OK));
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ORDERS_FETCHED,
                orderService.getAllOrders(pageable),
                HttpStatus.OK));
    }

    @PatchMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ORDER_STATUS_UPDATED,
                orderService.updateOrderStatus(orderId, request),
                HttpStatus.OK));
    }
}