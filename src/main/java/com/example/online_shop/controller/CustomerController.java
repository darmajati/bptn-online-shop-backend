package com.example.online_shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.online_shop.dto.request.AddOrEditCustomerRequestDto;
import com.example.online_shop.dto.request.CustomerListRequestDto;
import com.example.online_shop.dto.request.DataPageRequest;
import com.example.online_shop.dto.response.CustomerDetailResponse;
import com.example.online_shop.dto.response.CustomerListResponseDto;
import com.example.online_shop.dto.response.MessageResponse;
import com.example.online_shop.services.CustomerService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    public ResponseEntity<CustomerListResponseDto> getCustomers(CustomerListRequestDto customerListRequestDto, DataPageRequest pageRequest) {
        return customerService.getCustomers(customerListRequestDto, pageRequest.getPage());
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerDetailResponse> getCustomerById(@PathVariable Long customerId) {
        return customerService.getCustomerById(customerId);
    }
    
    

    @PostMapping("/customers")
    public ResponseEntity<MessageResponse> addCustomer(
        @RequestPart("data") AddOrEditCustomerRequestDto addCustomerRequestDto,
        @RequestPart("photo") MultipartFile photo
    ) {
        return customerService.addCustomer(addCustomerRequestDto, photo);
    }

    @PutMapping("/customers/{customerId}")
    public ResponseEntity<MessageResponse> updateCustomer(
        @RequestPart("data") AddOrEditCustomerRequestDto addCustomerRequestDto,
        @RequestPart(value = "photo", required = false) MultipartFile photo,
        @PathVariable Long customerId
    ) {
        return customerService.updateCustomer(addCustomerRequestDto, photo, customerId);
    }

    @PatchMapping("/customers/{customerId}/delete")
    public ResponseEntity<MessageResponse> deleteCustomer(
        @PathVariable Long customerId
    ) {
        return customerService.deleteCustomer(customerId);
    }
    
}
