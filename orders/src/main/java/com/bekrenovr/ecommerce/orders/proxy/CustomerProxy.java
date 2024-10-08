package com.bekrenovr.ecommerce.orders.proxy;

import com.bekrenovr.ecommerce.orders.dto.CustomerDTO;
import com.bekrenovr.ecommerce.orders.dto.request.CustomerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "users")
public interface CustomerProxy {
    @GetMapping("/customers/{id}")
    ResponseEntity<CustomerDTO> getById(@PathVariable UUID id);

    @PostMapping("/customers")
    ResponseEntity<Void> createCustomer(@RequestBody CustomerRequest request);
}
