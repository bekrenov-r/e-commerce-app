package com.bekrenovr.ecommerce.catalog.service;

import com.bekrenovr.ecommerce.catalog.dto.mapper.ItemImageMapper;
import com.bekrenovr.ecommerce.catalog.dto.response.ItemImageResponse;
import com.bekrenovr.ecommerce.catalog.exception.ItemApplicationException;
import com.bekrenovr.ecommerce.catalog.model.entity.ItemImage;
import com.bekrenovr.ecommerce.catalog.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.bekrenovr.ecommerce.catalog.exception.ItemApplicationExceptionReason.ITEM_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ItemImageService {
    private final ItemRepository itemRepository;
    private final ItemImageMapper itemImageMapper;

    public List<ItemImageResponse> getAllImagesForItem(UUID itemId){
        List<ItemImage> itemImages = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemApplicationException(ITEM_NOT_FOUND, itemId))
                .getImages();
        return itemImages.stream()
                .map(itemImageMapper::entityToResponse)
                .toList();
    }
}
