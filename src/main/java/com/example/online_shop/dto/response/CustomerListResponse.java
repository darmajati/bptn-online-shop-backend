package com.example.online_shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerListResponse {
    private Long customerId;
    private String customerName;
    private String customerCode;
    private String pic;
}
