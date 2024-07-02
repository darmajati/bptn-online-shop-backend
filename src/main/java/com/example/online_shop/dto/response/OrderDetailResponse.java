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
public class OrderDetailResponse {
    private Long orderId;
    private String orderCode;
    private LocalDate orderDate;
    private Integer totalPrice;
    private Long customerId;
    private Long itemId;
    private Integer quantity;
}
