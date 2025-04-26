package com.bekrenovr.ecommerce.catalog.item;

import com.bekrenovr.ecommerce.catalog.item.filters.FilterOptions;
import com.bekrenovr.ecommerce.catalog.item.sorting.SortOption;
import com.bekrenovr.ecommerce.catalog.util.swagger.ItemResponsePageModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Tag(name = "ItemController")
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "Get item by id")
    @ApiResponse(responseCode = "200", description = "Item found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ItemDetailedResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid UUID supplied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Item not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ItemDetailedResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    @Operation(summary = "Get list of items by ids")
    @ApiResponse(responseCode = "200", description = "List found items",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = ItemResponse.class))))
    @ApiResponse(responseCode = "400", description = "Invalid UUID supplied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/list")
    public ResponseEntity<List<ItemResponse>> getByIds(@RequestParam List<UUID> ids){
        return ResponseEntity.ok(itemService.getByIds(ids));
    }

    @Operation(summary = "Get items by filter and search criteria")
    @ApiResponse(responseCode = "200", description = "Page with found items",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ItemResponsePageModel.class)))
    @ApiResponse(responseCode = "400", description = "Invalid parameter(s) supplied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping
    public ResponseEntity<Page<ItemResponse>> getByCriteria(
            @RequestParam(name = "sort", required = false, defaultValue = "BY_POPULARITY") SortOption sort,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "${custom.page.default-size}") Integer pageSize,
            @ParameterObject @ModelAttribute @Valid FilterOptions filters
    ){
        return ResponseEntity.ok(itemService.getItemsByCriteria(sort, pageNumber, pageSize, filters));
    }

    @Operation(summary = "Create item")
    @ApiResponse(responseCode = "201", description = "Item created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ItemDetailedResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body supplied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping
    public ResponseEntity<ItemDetailedResponse> create(@RequestBody @Valid ItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.create(request));
    }

    @Operation(summary = "Update item")
    @ApiResponse(responseCode = "200", description = "Item updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ItemDetailedResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body supplied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @PutMapping("/{id}")
    public ResponseEntity<ItemDetailedResponse> update(@PathVariable UUID id, @RequestBody @Valid ItemRequest request) {
        return ResponseEntity.ok(itemService.update(id, request));
    }
}
