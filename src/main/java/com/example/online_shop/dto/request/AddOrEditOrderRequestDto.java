package com.example.online_shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddOrEditOrderRequestDto {
    private Long customerId;
    private Long itemsId;
    private Integer quantity;
}
