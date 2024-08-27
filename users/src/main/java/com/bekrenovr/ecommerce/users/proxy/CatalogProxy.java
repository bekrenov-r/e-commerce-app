package com.bekrenovr.ecommerce.users.proxy;

import com.bekrenovr.ecommerce.catalog.dto.response.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient("catalog")
public interface CatalogProxy {
    @GetMapping("/catalog/items/list")
    ResponseEntity<List<ItemResponse>> getItemsByIds(@RequestParam List<UUID> ids);
}
