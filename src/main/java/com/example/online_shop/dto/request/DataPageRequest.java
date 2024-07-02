package com.example.online_shop.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataPageRequest {
    private Integer pageSize;
    private Integer pageNumber;

    public Pageable getPage(){
        int pageNumberValue = (pageNumber != null) ? pageNumber < 1 ? 1 : pageNumber : 1;
        int pageSizeValue = (pageSize != null) ? pageSize < 1 ? 1 : pageSize : 10;

        return PageRequest.of(pageNumberValue - 1, pageSizeValue);
    }
    
}
