package com.bekrenovr.ecommerce.catalog.model.entity;

import com.bekrenovr.ecommerce.catalog.model.enums.SizeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "size")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "size_value")
    private String value;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SizeType type;

    @Override
    public String toString() {
        return "Size{" +
                "value='" + value + '\'' +
                '}';
    }
}
