package com.example.online_shop.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerListResponseDto {
    private long totalData;
    private List<CustomerListResponse> customerList;
}
