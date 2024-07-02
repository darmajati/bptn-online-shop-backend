package com.example.online_shop.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.online_shop.dto.request.AddOrEditCustomerRequestDto;
import com.example.online_shop.dto.request.CustomerListRequestDto;
import com.example.online_shop.dto.response.CustomerDetailResponse;
import com.example.online_shop.dto.response.CustomerListResponse;
import com.example.online_shop.dto.response.CustomerListResponseDto;
import com.example.online_shop.dto.response.MessageResponse;
import com.example.online_shop.model.Customer;
import com.example.online_shop.repository.CustomerRepository;
import com.example.online_shop.services.specifications.CustomerSpecification;
import com.example.online_shop.validator.FileValidator;

import lib.minio.MinioSrvc;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Lazy
    @Autowired
    private MinioSrvc minioSrvc;

    public ResponseEntity<CustomerListResponseDto> getCustomers(CustomerListRequestDto customerListRequestDto, Pageable pageable){
        try{
            Specification<Customer> customerSpec = CustomerSpecification.customerFilter(customerListRequestDto);
            customerSpec = customerSpec.and(CustomerSpecification.isActiveTrue());

            Page<Customer> customers = customerRepository.findAll(customerSpec, pageable);

            List<CustomerListResponse> customerListResponses = customers.getContent().stream().map(customer -> {

                return new CustomerListResponse(
                    customer.getCustomerId(),
                    customer.getCustomerName(),
                    customer.getCustomerCode(),
                    getFileURL(customer.getPic())
                );
            }).collect(Collectors.toList());

            CustomerListResponseDto customerListResponseDto = new CustomerListResponseDto();
            customerListResponseDto.setTotalData(customers.getTotalElements());
            customerListResponseDto.setCustomerList(customerListResponses);

            return ResponseEntity.ok(customerListResponseDto);
        }catch (Exception e) {
            log.error(null, e);
            return ResponseEntity.internalServerError().body(new CustomerListResponseDto(0L, Collections.emptyList()));
        }
    }

    public ResponseEntity<CustomerDetailResponse> getCustomerById(Long customerId){
        try {
             Optional<Customer> customerOptional = customerRepository.findByCustomerId(customerId);
            if(customerOptional.isPresent()){
                Customer customer = customerOptional.get();
                
                CustomerDetailResponse response = CustomerDetailResponse.builder()
                    .customerId(customer.getCustomerId())
                    .customerName(customer.getCustomerName())
                    .customerAddress(customer.getCustomerAddress())
                    .customerCode(customer.getCustomerCode())
                    .customerPhone(customer.getCustomerPhone())
                    .lastOrderDate(customer.getLastOrderDate())
                    .pic(getFileURL(customer.getPic()))
                    .build();
                    return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching customer details", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<MessageResponse> addCustomer(AddOrEditCustomerRequestDto addCustomerRequestDto, MultipartFile photo){
        try{
            Customer customer = new Customer();
            customer.setCustomerName(addCustomerRequestDto.getCustomerName());
            customer.setCustomerAddress(addCustomerRequestDto.getCustomerAddress());
            customer.setCustomerPhone(addCustomerRequestDto.getCustomerPhone());
            customer.setIsActive(true);

            if (photo != null && !FileValidator.isImageFile(photo)) {
                return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.BAD_REQUEST.value(), "Foto harus dalam format JPG, JPEG, atau PNG"));
            }

            Customer savedCustomer = customerRepository.save(customer);

           
    
            if (photo != null) {
                String photoFilename = minioSrvc.uploadFileToMinio(
                    savedCustomer.getCustomerId(),
                    addCustomerRequestDto.getCustomerName(),
                    photo
                );
                savedCustomer.setPic(photoFilename);
                customerRepository.save(savedCustomer);
            }

            String customerCode = generateCustomerCode(savedCustomer);
            savedCustomer.setCustomerCode(customerCode);
            savedCustomer = customerRepository.save(savedCustomer);

            return ResponseEntity.ok().body(new MessageResponse(HttpStatus.CREATED.value(), "Customer berhasil ditambahkan"));
        } catch (Exception e) {
            log.error("Terjadi kesalahan saat membuat customer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Terjadi kesalahan saat membuat customer: " + e.getMessage()));
        } 
    }

    public ResponseEntity<MessageResponse> updateCustomer(AddOrEditCustomerRequestDto editCustomerRequestDto, MultipartFile photo, Long customerId){
        try {
            Optional<Customer> optionalCustomer = customerRepository.findByCustomerId(customerId);

            if(optionalCustomer.isPresent()){
                Customer customer = optionalCustomer.get();
                customer.setCustomerName(editCustomerRequestDto.getCustomerName());
                customer.setCustomerAddress(editCustomerRequestDto.getCustomerAddress());
                customer.setCustomerPhone(editCustomerRequestDto.getCustomerPhone());
                customer.setIsActive(true);

                if (photo != null && !FileValidator.isImageFile(photo)) {
                    return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.BAD_REQUEST.value(), "Foto harus dalam format JPG, JPEG, atau PNG"));
                }

                Customer savedCustomer = customerRepository.save(customer);

                if (photo != null) {
                    String photoFilename = minioSrvc.uploadFileToMinio(
                        savedCustomer.getCustomerId(),
                        editCustomerRequestDto.getCustomerName(),
                        photo
                    );
                    savedCustomer.setPic(photoFilename);
                    customerRepository.save(savedCustomer);
                }

                String customerCode = generateCustomerCode(savedCustomer);
                savedCustomer.setCustomerCode(customerCode);
                savedCustomer = customerRepository.save(savedCustomer);

                return ResponseEntity.ok().body(new MessageResponse(HttpStatus.CREATED.value(), "Customer berhasil diedit"));
            }  else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Gagal memperbarui data customer:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Gagal memperbarui data customer: " + e.getMessage()));
        }
    }

    public ResponseEntity<MessageResponse> deleteCustomer(Long customerId){
        try {
            Optional<Customer> optionalCustomer = customerRepository.findByCustomerId(customerId);

            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                customer.setIsActive(false);
                customerRepository.save(customer);
                return ResponseEntity.ok().body(new MessageResponse(HttpStatus.CREATED.value(), "Customer berhasil dihapus"));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            log.error("Gagal menghapus data customer:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Gagal menghapus data customer: " + e.getMessage()));
        }
    }

    private String getFileURL(String filename) {
        String url = "";

        if (filename != null) {
            url = minioSrvc.getPublicLink(filename);
        }

        return url;
    }

    private String generateCustomerCode(Customer customer) {
        return "C" + customer.getCustomerId() + customer.getCustomerName().replaceAll("[^a-zA-Z0-9]", "");
    }
    
}
