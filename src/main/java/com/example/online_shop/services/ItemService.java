package com.example.online_shop.services;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.online_shop.dto.request.AddOrEditItemRequestDto;
import com.example.online_shop.dto.request.ItemListRequestDto;
import com.example.online_shop.dto.response.ItemDetailResponse;
import com.example.online_shop.dto.response.ItemListResponse;
import com.example.online_shop.dto.response.ItemListResponseDto;
import com.example.online_shop.dto.response.MessageResponse;
import com.example.online_shop.model.Item;
import com.example.online_shop.repository.ItemRepository;
import com.example.online_shop.services.specifications.ItemSpecification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public ResponseEntity<ItemListResponseDto> getItems(ItemListRequestDto itemListRequestDto, Pageable pageable){
        try {
            Specification<Item> itemSpec = ItemSpecification.itemFilter(itemListRequestDto);
            itemSpec = itemSpec.and(ItemSpecification.isActiveTrue());

            Page<Item> items = itemRepository.findAll(itemSpec, pageable);

            List<ItemListResponse> itemListResponses = items.getContent().stream().map(item -> {
                
                return new ItemListResponse(
                    item.getItemId(),
                    item.getItemsName(),
                    item.getItemsCode()
                );
            }).collect(Collectors.toList());

            ItemListResponseDto itemListResponseDto = new ItemListResponseDto();
            itemListResponseDto.setTotalData(items.getTotalElements());
            itemListResponseDto.setItemList(itemListResponses);

            return ResponseEntity.ok(itemListResponseDto);

        } catch (Exception e) {
            log.error(null, e);
            return ResponseEntity.internalServerError().body(new ItemListResponseDto(0L, Collections.emptyList()));
        }
    }

    public ResponseEntity<ItemDetailResponse> getItemById(Long itemId){
        try {
            Optional<Item> itemOptional = itemRepository.findByItemId(itemId);
            if(itemOptional.isPresent()){
                Item item = itemOptional.get();
                
                ItemDetailResponse response = ItemDetailResponse.builder()
                    .itemId(item.getItemId())
                    .itemName(item.getItemsName())
                    .itemCode(item.getItemsCode())
                    .stock(item.getStock())
                    .price(item.getPrice())
                    .lastReStock(item.getLastReStock())
                    .build();
                    return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching item details", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    public ResponseEntity<MessageResponse> addItem(AddOrEditItemRequestDto addItemRequestDto){
        try {
            Item item = new Item();
            item.setItemsName(addItemRequestDto.getItemName());
            item.setStock(addItemRequestDto.getStock());
            item.setPrice(addItemRequestDto.getPrice());
            item.setIsAvailable(true);
            item.setLastReStock(LocalDate.now());

        Item savedItem = itemRepository.save(item);

        String ItemCode = generateItemCode(savedItem);
        savedItem.setItemsCode(ItemCode);
        savedItem = itemRepository.save(savedItem);

        return ResponseEntity.ok().body(new MessageResponse(HttpStatus.CREATED.value(), "Item berhasil ditambahkan"));
        } catch (Exception e) {
            log.error("Terjadi kesalahan saat membuat item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Terjadi kesalahan saat membuat item: " + e.getMessage()));
        }
    }

    public ResponseEntity<MessageResponse> updateItem (AddOrEditItemRequestDto editItemRequestDto, Long itemId){
        try {
            Optional<Item> optionalItem = itemRepository.findByItemId(itemId);

            if(optionalItem.isPresent()){
                Item item = optionalItem.get();
                item.setItemsName(editItemRequestDto.getItemName());
                item.setStock(editItemRequestDto.getStock());
                item.setPrice(editItemRequestDto.getPrice());
                item.setIsAvailable(true);
                item.setLastReStock(LocalDate.now());

                Item savedItem = itemRepository.save(item);

                String itemCode = generateItemCode(savedItem);
                savedItem.setItemsCode(itemCode);
                savedItem = itemRepository.save(savedItem);

                return ResponseEntity.ok().body(new MessageResponse(HttpStatus.CREATED.value(), "Item berhasil diedit"));

            } else{
                return ResponseEntity.notFound().build();
            }

            
        } catch (Exception e) {
            log.error("Gagal memperbarui data item:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Gagal memperbarui data item: " + e.getMessage()));
        }
    }

    public ResponseEntity<MessageResponse> deleteItem(Long itemId){
        try {
            Optional<Item> optionalItem = itemRepository.findByItemId(itemId);

            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();
                item.setIsAvailable(false);
                itemRepository.save(item);
                return ResponseEntity.ok().body(new MessageResponse(HttpStatus.CREATED.value(), "Item berhasil dihapus"));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            log.error("Gagal menghapus data item:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Gagal menghapus data item: " + e.getMessage()));
        }
    }

    private String generateItemCode(Item item) {
        return "I" + item.getItemId() + item.getItemsName().replaceAll("[^a-zA-Z0-9]", "");
    }
}
