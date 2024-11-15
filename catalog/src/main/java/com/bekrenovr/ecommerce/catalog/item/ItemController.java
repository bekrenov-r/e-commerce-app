package com.bekrenovr.ecommerce.catalog.item;

import com.bekrenovr.ecommerce.catalog.item.filters.FilterOptions;
import com.bekrenovr.ecommerce.catalog.item.image.ItemImageService;
import com.bekrenovr.ecommerce.catalog.item.sorting.SortOption;
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

    @PostMapping
    public void create(@RequestBody Item item) {
        itemService.create(item);
    }
}
