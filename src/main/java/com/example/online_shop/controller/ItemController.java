package com.example.online_shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.online_shop.dto.request.AddOrEditItemRequestDto;
import com.example.online_shop.dto.request.DataPageRequest;
import com.example.online_shop.dto.request.ItemListRequestDto;
import com.example.online_shop.dto.response.ItemDetailResponse;
import com.example.online_shop.dto.response.ItemListResponseDto;
import com.example.online_shop.dto.response.MessageResponse;
import com.example.online_shop.services.ItemService;

@RestController
@RequestMapping("/api/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping("/items")
    public ResponseEntity<ItemListResponseDto> getCustomers(ItemListRequestDto itemListRequestDto, DataPageRequest pageRequest) {
        return itemService.getItems(itemListRequestDto, pageRequest.getPage());
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<ItemDetailResponse> getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @PostMapping("/items")
    public ResponseEntity<MessageResponse> addItem(@RequestBody AddOrEditItemRequestDto addItemRequestDto){
        return itemService.addItem(addItemRequestDto);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<MessageResponse> updateItem(@RequestBody AddOrEditItemRequestDto editItemRequestDto, @PathVariable Long itemId){
        return itemService.updateItem(editItemRequestDto, itemId);
    }

    @PatchMapping("/items/{itemId}/delete")
    public ResponseEntity<MessageResponse> deleteItem(
        @PathVariable Long itemId
    ) {
        return itemService.deleteItem(itemId);
    }
}
