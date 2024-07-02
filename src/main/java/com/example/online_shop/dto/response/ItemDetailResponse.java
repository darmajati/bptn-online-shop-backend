package com.example.online_shop.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDetailResponse {
    private Long itemId;
    private String itemName;
    private String itemCode;
    private Integer stock;
    private Integer price;
    private LocalDate lastReStock;
}
