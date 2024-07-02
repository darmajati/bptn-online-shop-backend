package com.example.online_shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddOrEditItemRequestDto {
    private String itemName;
    private Integer stock;
    private Integer price;
}
