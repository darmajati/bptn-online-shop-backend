package com.example.online_shop.services.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.online_shop.dto.request.CustomerListRequestDto;
import com.example.online_shop.model.Customer;

import jakarta.persistence.criteria.Predicate;

public class CustomerSpecification {
    public static Specification<Customer> customerFilter(CustomerListRequestDto customerListRequestDto){
        return(root, query, criteriaBuilder) -> {
            List <Predicate> predicates = new ArrayList<>();

            if (customerListRequestDto.getCustomerName() != null) {
                String customerNameValue = "%" + customerListRequestDto.getCustomerName().toLowerCase() + "%";
                Predicate customerNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("customerName")), customerNameValue);
                predicates.add(customerNamePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    public static Specification<Customer> isActiveTrue() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isActive"));
    }
}
