package com.bekrenovr.ecommerce.catalog.controller;

import com.bekrenovr.ecommerce.catalog.dto.request.FilterOptions;
import com.bekrenovr.ecommerce.catalog.dto.response.ItemDetailedResponse;
import com.bekrenovr.ecommerce.catalog.dto.response.ItemImageResponse;
import com.bekrenovr.ecommerce.catalog.dto.response.ItemResponse;
import com.bekrenovr.ecommerce.catalog.model.entity.Item;
import com.bekrenovr.ecommerce.catalog.service.ItemImageService;
import com.bekrenovr.ecommerce.catalog.service.ItemService;
import com.bekrenovr.ecommerce.catalog.util.sort.SortOption;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemImageService itemImageService;

    @GetMapping("/{id}")
    public ResponseEntity<ItemDetailedResponse> getById(@PathVariable UUID id){
        return ResponseEntity.ok(itemService.getById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ItemResponse>> getByIds(@RequestParam List<UUID> ids){
        return ResponseEntity.ok(itemService.getByIds(ids));
    }

    @GetMapping
    public ResponseEntity<Page<ItemResponse>> getByCriteria(
            @RequestParam(name = "sort", required = false) SortOption sort,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "${custom.page.default-size}") Integer pageSize,
            @ModelAttribute @Valid FilterOptions filters
    ){
        return ResponseEntity.ok(itemService.getItemsByCriteria(sort, pageNumber, pageSize, filters));
    }

    @GetMapping("/{itemId}/images")
    public ResponseEntity<List<ItemImageResponse>> getAllImagesForItem(@PathVariable UUID itemId){
        return ResponseEntity.ok(itemImageService.getAllImagesForItem(itemId));
    }

    @PostMapping
    public void create(@RequestBody Item item) {
        itemService.create(item);
    }
}
