package com.bekrenovr.ecommerce.catalog.feign;

import com.bekrenovr.ecommerce.catalog.item.ItemResponse;
import com.bekrenovr.ecommerce.common.security.SecurityConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient("customers")
public interface CustomersProxy {
    @GetMapping("/customers/wishlist")
    ResponseEntity<List<ItemResponse>> getWishListItems(
            @RequestHeader(SecurityConstants.AUTHENTICATED_USER_HEADER)
            String authenticatedUserHeader
    );

}
