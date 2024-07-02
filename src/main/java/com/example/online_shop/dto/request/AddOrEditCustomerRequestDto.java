package com.example.online_shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddOrEditCustomerRequestDto {
    private String customerName;
    private String customerAddress;
    private String customerPhone;
}
