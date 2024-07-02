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
public class OrderListResponse {
    private Long orderId;
    private String orderCode;
    private LocalDate orderDate;
    private Integer totalPrice;
}
