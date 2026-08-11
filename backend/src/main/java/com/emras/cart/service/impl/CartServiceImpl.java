package com.emras.cart.service.impl;

import com.emras.cart.constant.CartConstants;
import com.emras.cart.dto.request.AddToCartRequest;
import com.emras.cart.dto.request.UpdateCartItemRequest;
import com.emras.cart.dto.response.CartItemResponse;
import com.emras.cart.dto.response.CartResponse;
import com.emras.cart.dto.response.CartSummaryResponse;
import com.emras.cart.facade.CartFacade;
import com.emras.cart.model.Cart;
import com.emras.cart.model.CartItem;
import com.emras.cart.repository.CartItemRepository;
import com.emras.cart.repository.CartRepository;
import com.emras.cart.service.CartService;
import com.emras.product.dto.response.ProductSummaryResponse;
import com.emras.product.facade.ProductFacade;
import com.emras.product.repository.ProductVariantRepository;
import com.emras.shared.constant.ErrorMessages;
import com.emras.shared.exception.BusinessException;
import com.emras.shared.exception.ResourceNotFoundException;
import com.emras.shared.util.LogUtil;
import com.emras.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService, CartFacade {
    // ── Cross-domain dependency note ──────────────────────────────────────
    // ProductVariantRepository is injected here as a pragmatic exception
    // to the facade rule. Cart needs the actual JPA entity reference to
    // build the CartItem relationship — the facade only returns DTOs, not
    // entities. This is acceptable because:
    //   1. We only use it for findById (read-only entity lookup)
    //   2. When migrating to microservices, this becomes an HTTP call
    //      to the product service, and CartItem stores variantId as a
    //      plain Long (no JPA relationship) instead.
    // All business logic around products still goes through ProductFacade.

    private final CartRepository     cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository     userRepository;
    private final ProductFacade      productFacade;
    private final ProductVariantRepository variantRepository;

    // ── Get cart ──────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CartResponse getCart(Long userId) {
        LogUtil.logServiceEntry("CartServiceImpl", "getCart", "userId", userId);
        Cart cart = getOrCreateCart(userId);
        return toResponse(cart);
    }

    // ── Add item ──────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CartResponse addItem(Long userId, AddToCartRequest request) {
        LogUtil.logServiceEntry("CartServiceImpl", "addItem",
                "userId", userId, "variantId", request.getVariantId());

        // Validate variant exists
        if (!productFacade.variantExists(request.getVariantId())) {
            throw new ResourceNotFoundException(ErrorMessages.VARIANT_NOT_FOUND);
        }

        // Validate stock
        if (!productFacade.hasSufficientStock(
                request.getVariantId(), request.getQuantity())) {
            throw new BusinessException(
                    ErrorMessages.INSUFFICIENT_STOCK,
                    ErrorMessages.Code.CART_INSUFFICIENT_STOCK,
                    HttpStatus.CONFLICT
            );
        }

        Cart cart = getOrCreateCart(userId);

        // Check cart size limit
        if (cart.getItems().size() >= CartConstants.MAX_ITEMS_IN_CART) {
            throw new BusinessException(
                    "Cart is full. Maximum " + CartConstants.MAX_ITEMS_IN_CART + " items allowed.",
                    ErrorMessages.Code.INVALID_INPUT,
                    HttpStatus.BAD_REQUEST
            );
        }

        // If variant already in cart → update quantity
        var existingOpt = cartItemRepository
                .findByCartIdAndVariantId(cart.getId(), request.getVariantId());

        if (existingOpt.isPresent()) {
            CartItem existing = existingOpt.get();
            int newQty = existing.getQuantity() + request.getQuantity();
            if (newQty > CartConstants.MAX_QUANTITY_PER_ITEM) {
                throw new BusinessException(
                        "Maximum " + CartConstants.MAX_QUANTITY_PER_ITEM
                                + " units per item allowed.",
                        ErrorMessages.Code.INVALID_INPUT,
                        HttpStatus.BAD_REQUEST
                );
            }
            existing.setQuantity(newQty);
            cartItemRepository.save(existing);
        } else {
            // Get price from facade
            ProductSummaryResponse summary =
                    productFacade.getVariantSummary(request.getVariantId());

            // Get actual variant entity (needed for JPA relationship)
            var variant = variantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            ErrorMessages.VARIANT_NOT_FOUND));

            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .unitPrice(summary.getPrice())
                    .build();

            cartItemRepository.save(newItem);
        }

        LogUtil.logBusinessEvent("CART_ITEM_ADDED",
                "userId", userId, "variantId", request.getVariantId());

        // Reload cart fresh from DB to get all associations properly
        return toResponse(cartRepository.findByUserId(userId).orElse(cart));
    }
    // ── Update item ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public CartResponse updateItem(Long userId, Long cartItemId,
                                   UpdateCartItemRequest request) {
        LogUtil.logServiceEntry("CartServiceImpl", "updateItem",
                "cartItemId", cartItemId);

        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.CART_ITEM_NOT_FOUND));

        if (!productFacade.hasSufficientStock(
                item.getVariant().getId(), request.getQuantity())) {
            throw new BusinessException(
                    ErrorMessages.INSUFFICIENT_STOCK,
                    ErrorMessages.Code.CART_INSUFFICIENT_STOCK,
                    HttpStatus.CONFLICT
            );
        }

        item.setQuantity(request.getQuantity());
        cartRepository.save(cart);
        return toResponse(cart);
    }

    // ── Remove item ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long cartItemId) {
        LogUtil.logServiceEntry("CartServiceImpl", "removeItem",
                "cartItemId", cartItemId);
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(i -> i.getId().equals(cartItemId));
        cartRepository.save(cart);
        return toResponse(cart);
    }

    // ── Clear cart ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    // ── CartFacade impl ───────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public CartSummaryResponse getCartSummary(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart == null || cart.getItems().isEmpty()) {
            return CartSummaryResponse.builder()
                    .cartId(cart != null ? cart.getId() : null)
                    .items(List.of())
                    .subtotal(BigDecimal.ZERO)
                    .totalItems(0)
                    .build();
        }

        List<CartSummaryResponse.CartItemSummary> summaryItems = cart.getItems()
                .stream()
                .map(item -> CartSummaryResponse.CartItemSummary.builder()
                        .cartItemId(item.getId())
                        .variantId(item.getVariant().getId())
                        .productName(item.getVariant().getProduct().getName())
                        .size(item.getVariant().getSize())
                        .color(item.getVariant().getColor())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .lineTotal(item.getLineTotal())
                        .build())
                .toList();

        BigDecimal subtotal = cart.getItems().stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartSummaryResponse.builder()
                .cartId(cart.getId())
                .items(summaryItems)
                .subtotal(subtotal)
                .totalItems(cart.getTotalItems())
                .build();
    }

    @Override
    public boolean isCartEmpty(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(c -> c.getItems().isEmpty())
                .orElse(true);
    }

    @Override
    @Transactional
    public void addItem(Long userId, Long variantId, int quantity) {
        AddToCartRequest req = new AddToCartRequest();
        req.setVariantId(variantId);
        req.setQuantity(quantity);
        addItem(userId, req);
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    var user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    ErrorMessages.USER_NOT_FOUND));
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .filter(item -> item.getVariant() != null)
                .map(this::toItemResponse)
                .toList();

        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .totalItems(items.stream().mapToInt(CartItemResponse::getQuantity).sum())
                .subtotal(subtotal)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        var variant = item.getVariant();
        var product = variant != null ? variant.getProduct() : null;

        String imageUrl = null;
        if (product != null && product.getImages() != null) {
            imageUrl = product.getImages().stream()
                    .filter(img -> img.isPrimary())
                    .map(img -> img.getImageUrl())
                    .findFirst()
                    .orElse(null);
        }

        return CartItemResponse.builder()
                .cartItemId(item.getId())
                .variantId(variant != null ? variant.getId() : null)
                .productId(product != null ? product.getId() : null)
                .productName(product != null ? product.getName() : "Unknown")
                .size(variant != null ? variant.getSize() : null)
                .color(variant != null ? variant.getColor() : null)
                .sku(variant != null ? variant.getSku() : null)
                .imageUrl(imageUrl)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .availableStock(variant != null ? variant.getStockQuantity() : 0)
                .inStock(variant != null && variant.isInStock())
                .build();
    }

    private com.emras.product.model.ProductVariant getVariantEntity(Long variantId) {
        return variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.VARIANT_NOT_FOUND));
    }
}