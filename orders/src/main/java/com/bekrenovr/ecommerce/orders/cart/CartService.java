package com.bekrenovr.ecommerce.orders.cart;

import com.bekrenovr.ecommerce.common.exception.EcommerceApplicationException;
import com.bekrenovr.ecommerce.common.security.AuthenticationUtil;
import com.bekrenovr.ecommerce.orders.feign.CatalogProxy;
import com.bekrenovr.ecommerce.orders.order.dto.CatalogItem;
import com.bekrenovr.ecommerce.orders.order.itementry.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.bekrenovr.ecommerce.orders.exception.OrdersApplicationExceptionReason.CART_ENTRY_NOT_FOUND;
import static com.bekrenovr.ecommerce.orders.exception.OrdersApplicationExceptionReason.ITEM_ALREADY_IN_CART;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ItemEntryMapper itemEntryMapper;
    private final CatalogProxy catalogProxy;

    public List<ItemEntryResponse> getCartItems() {
        return getOrCreateCart().getItemEntries().stream()
                .map(itemEntryMapper::entityToResponse)
                .toList();
    }

    public void addItemToCart(ItemEntryRequest request) {
        Cart cart = getOrCreateCart();
        if(cart.hasItemEntry(request)) {
            throw new EcommerceApplicationException(ITEM_ALREADY_IN_CART);
        }
        CatalogItem item = catalogProxy.getItemById(request.itemId()).getBody();
        ItemEntryValidator.validateEntryAgainstCatalogItem(request, item);
        ItemEntry itemEntry = itemEntryMapper.itemResponseToEntity(item, request.quantity(), request.size());
        cart.getItemEntries().add(itemEntry);
        cartRepository.save(cart);
    }

    public void updateCartItem(UUID itemEntryId, int quantity, String size) {
        Cart cart = getOrCreateCart();
        ItemEntry itemEntry = cart.getItemEntries().stream()
                .filter(entry -> entry.getId().equals(itemEntryId))
                .findFirst()
                .orElseThrow(() -> new EcommerceApplicationException(CART_ENTRY_NOT_FOUND, itemEntryId));
        CatalogItem item = catalogProxy.getItemById(itemEntry.getItemId()).getBody();
        ItemEntryValidator.validateEntryAgainstCatalogItem(new ItemEntryRequest(item.id(), quantity, size), item);

        itemEntry.setQuantity(quantity);
        itemEntry.setItemSize(size);
        cartRepository.save(cart);
    }

    public void removeItemFromCart(UUID itemEntryId) {
        Cart cart = getOrCreateCart();
        ItemEntry entryToRemove = cart.getItemEntries().stream()
                .filter(entry -> entry.getId().equals(itemEntryId))
                .findFirst()
                .orElseThrow(() -> new EcommerceApplicationException(CART_ENTRY_NOT_FOUND, itemEntryId));
        cart.getItemEntries().remove(entryToRemove);
        cartRepository.save(cart);
    }

    public void clearCart() {
        Cart cart = getOrCreateCart();
        cart.getItemEntries().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart() {
        String email = AuthenticationUtil.getAuthenticatedUser().getUsername();
        return cartRepository.findByCustomerEmail(email)
                .orElseGet(() -> {
                    Cart cart = new Cart(email, new ArrayList<>());
                    return cartRepository.save(cart);
                });
    }
}
