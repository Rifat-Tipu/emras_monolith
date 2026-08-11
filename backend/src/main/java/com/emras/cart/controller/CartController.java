package com.emras.cart.controller;

import com.emras.cart.dto.request.AddToCartRequest;
import com.emras.cart.dto.request.UpdateCartItemRequest;
import com.emras.cart.dto.response.CartResponse;
import com.emras.cart.service.CartService;
import com.emras.shared.constant.ApiConstants;
import com.emras.shared.constant.SuccessMessages;
import com.emras.shared.model.ApiResponse;
import com.emras.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.CART_BASE)
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CART_FETCHED,
                cartService.getCart(user.getId()),
                HttpStatus.OK));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CART_ITEM_ADDED,
                cartService.addItem(user.getId(), request),
                HttpStatus.OK));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CART_ITEM_UPDATED,
                cartService.updateItem(user.getId(), cartItemId, request),
                HttpStatus.OK));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartItemId) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CART_ITEM_REMOVED,
                cartService.removeItem(user.getId(), cartItemId),
                HttpStatus.OK));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal User user) {
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CART_CLEARED, HttpStatus.OK));
    }
}