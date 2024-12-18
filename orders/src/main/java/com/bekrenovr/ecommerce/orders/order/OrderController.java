package com.bekrenovr.ecommerce.orders.order;

import com.bekrenovr.ecommerce.orders.order.dto.OrderDetailedResponse;
import com.bekrenovr.ecommerce.orders.order.dto.OrderRequest;
import com.bekrenovr.ecommerce.orders.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailedResponse> getById(@PathVariable UUID id){
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/customer")
    public ResponseEntity<Page<OrderResponse>> getAllForCustomer(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "${custom.page.default-size}") Integer pageSize
    ){
        return ResponseEntity.ok(orderService.getAllForCustomer(status, pageNumber, pageSize));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Validated OrderRequest orderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(orderRequest));
    }

    @DeleteMapping("/{id}")
    public void cancel(@PathVariable UUID id) {
        orderService.cancel(id);
    }
}
