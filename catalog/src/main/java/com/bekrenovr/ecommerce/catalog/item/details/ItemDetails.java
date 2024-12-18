package com.bekrenovr.ecommerce.catalog.item.details;

import com.bekrenovr.ecommerce.catalog.item.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "item_details")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemDetails {

    @Id
    private UUID itemId;

    @OneToOne
    @JoinColumn(name = "item_id")
    @MapsId
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Item item;

    @Column(name = "orders_count_total")
    private Integer ordersCountTotal;

    @Column(name = "orders_count_last_month")
    private Integer ordersCountLastMonth;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "creating_employee")
    private String creatingEmployeeUsername;

    public ItemDetails(Integer ordersCountTotal, Integer ordersCountLastMonth, LocalDateTime createdAt, String creatingEmployeeUsername) {
        this.ordersCountTotal = ordersCountTotal;
        this.ordersCountLastMonth = ordersCountLastMonth;
        this.createdAt = createdAt;
        this.creatingEmployeeUsername = creatingEmployeeUsername;
    }
}
