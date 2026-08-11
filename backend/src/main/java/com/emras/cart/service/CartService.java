package com.emras.cart.service;

import com.emras.cart.dto.request.AddToCartRequest;
import com.emras.cart.dto.request.UpdateCartItemRequest;
import com.emras.cart.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(Long userId);

    CartResponse addItem(Long userId, AddToCartRequest request);

    CartResponse updateItem(Long userId, Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeItem(Long userId, Long cartItemId);

    void clearCart(Long userId);
}