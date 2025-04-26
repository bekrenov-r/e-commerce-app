package com.bekrenovr.ecommerce.catalog.item;

import com.bekrenovr.ecommerce.catalog.brand.Brand;
import com.bekrenovr.ecommerce.catalog.brand.BrandRepository;
import com.bekrenovr.ecommerce.catalog.category.Category;
import com.bekrenovr.ecommerce.catalog.category.CategoryRepository;
import com.bekrenovr.ecommerce.catalog.category.subcategory.Subcategory;
import com.bekrenovr.ecommerce.catalog.item.details.ItemDetailsService;
import com.bekrenovr.ecommerce.catalog.item.filters.FilterOptions;
import com.bekrenovr.ecommerce.catalog.item.metadata.ItemMetadata;
import com.bekrenovr.ecommerce.catalog.item.metadata.ItemMetadataService;
import com.bekrenovr.ecommerce.catalog.item.sorting.ItemSortComparators;
import com.bekrenovr.ecommerce.catalog.item.sorting.SortOption;
import com.bekrenovr.ecommerce.catalog.item.uniqueitem.UniqueItem;
import com.bekrenovr.ecommerce.catalog.item.uniqueitem.UniqueItemDTO;
import com.bekrenovr.ecommerce.catalog.item.uniqueitem.UniqueItemRepository;
import com.bekrenovr.ecommerce.common.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemSpecificationBuilder itemSpecificationBuilder;
    private final ItemMapper itemMapper;
    private final ItemMetadataService metadataService;
    private final ItemDetailsService itemDetailsService;
    private final UniqueItemRepository uniqueItemRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public Page<ItemResponse> getItemsByCriteria(
            SortOption sort, Integer pageNumber, Integer pageSize, FilterOptions filters
    ) {
        Specification<Item> specification = itemSpecificationBuilder.buildFromFilterOptions(filters);
        List<Item> items = itemRepository.findAll(specification)
                .stream()
                .sorted(ItemSortComparators.forOption(sort))
                .toList();
        Page<Item> paginatedItems = PageUtil.paginateList(items, pageNumber, pageSize);
        Map<Item, ItemMetadata> metadataMap = metadataService.generateMetadata(paginatedItems);
        return paginatedItems.map(item -> itemMapper.itemToResponse(item, metadataMap.get(item)));
    }

    public ItemDetailedResponse getById(UUID id) {
        Item item = itemRepository.findByIdOrThrowDefault(id);
        ItemMetadata metadata = metadataService.generateMetadata(item);
        return itemMapper.itemToDetailedResponse(item, metadata);
    }

    public List<ItemResponse> getByIds(List<UUID> ids) {
        List<Item> items = itemRepository.findAllById(ids);
        Map<Item, ItemMetadata> metadataMap = metadataService.generateMetadata(items);
        return items.stream()
                .map(item -> itemMapper.itemToResponse(item, metadataMap.get(item)))
                .toList();
    }

    @Transactional
    public ItemDetailedResponse create(ItemRequest request) {
        Item item = itemMapper.requestToItem(request);
        item.setRating(0.0);
        item = itemRepository.save(item);
        item = itemDetailsService.addItemDetails(item);

        if (request.uniqueItems() != null) {
            addUniqueItems(request.uniqueItems(), item);
        } else {
            item.setUniqueItems(List.of());
        }
        item.setImages(List.of());
        ItemMetadata metadata = metadataService.generateMetadata(item);
        return itemMapper.itemToDetailedResponse(item, metadata);
    }

    public ItemDetailedResponse update(UUID id, ItemRequest request) {
        Item item = itemRepository.findByIdOrThrowDefault(id);

        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setDiscount(request.discount());
        item.setPriceAfterDiscount(Item.calculatePriceAfterDiscount(request.price(), request.discount()));
        item.setColor(request.color());
        item.setGender(request.gender());
        item.setMaterial(request.material());
        item.setSeason(request.season());
        item.setItemCode(request.itemCode());

        UUID subcategoryId = Objects.nonNull(item.getSubcategory()) ? item.getSubcategory().getId() : null;
        if(!item.getCategory().getId().equals(request.categoryId())) {
            this.updateCategoryAndSubcategory(item, request.categoryId(), request.subcategoryId());
        } else if(!Objects.equals(subcategoryId, request.subcategoryId())) {
            this.updateSubcategory(item, request.subcategoryId());
        }
        if(!item.getBrand().getId().equals(request.brandId())) {
            this.updateBrand(item, request.brandId());
        }
        item = itemRepository.save(item);
        ItemMetadata metadata = metadataService.generateMetadata(item);
        return itemMapper.itemToDetailedResponse(item, metadata);
    }

    private void updateBrand(Item item, UUID brandId) {
        Brand brand = brandRepository.findByIdOrThrowDefault(brandId);
        item.setBrand(brand);
    }

    private void updateCategoryAndSubcategory(Item item, UUID categoryId, UUID subcategoryId) {
        Category category = categoryRepository.findByIdOrThrowDefault(categoryId);
        item.setCategory(category);
        this.updateSubcategory(item, subcategoryId);
    }

    private void updateSubcategory(Item item, UUID subcategoryId) {
        Subcategory subcategory = subcategoryId != null ? item.getCategory().findSubcategory(subcategoryId) : null;
        item.setSubcategory(subcategory);
    }

    private void addUniqueItems(List<UniqueItemDTO> dtos, Item item) {
        List<UniqueItem> uniqueItems = dtos.stream()
                .map(dto -> new UniqueItem(dto.size(), dto.quantity(), item))
                .toList();
        uniqueItems = uniqueItemRepository.saveAll(uniqueItems);
        item.setUniqueItems(uniqueItems);
    }
}
