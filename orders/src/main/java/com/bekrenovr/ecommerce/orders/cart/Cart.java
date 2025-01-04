package com.bekrenovr.ecommerce.orders.cart;

import com.bekrenovr.ecommerce.common.model.entity.AbstractEntity;
import com.bekrenovr.ecommerce.orders.order.itementry.ItemEntry;
import com.bekrenovr.ecommerce.orders.order.itementry.ItemEntryRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cart extends AbstractEntity {
    @Column(name = "customer_email")
    private String customerEmail;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "cart_item_entry",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "item_entry_id")
    )
    private List<ItemEntry> itemEntries;

    public boolean hasItemEntry(ItemEntryRequest itemEntry) {
        return itemEntries.stream()
                .anyMatch(
                        cartItemEntry -> cartItemEntry.getItemId().equals(itemEntry.itemId())
                                && cartItemEntry.getItemSize().equals(itemEntry.size())
                );
    }
}
