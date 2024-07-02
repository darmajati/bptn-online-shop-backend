package com.example.online_shop.services.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.online_shop.dto.request.OrderListRequestDto;
import com.example.online_shop.model.Order;

import jakarta.persistence.criteria.Predicate;

public class OrderSpecification {
    public static Specification<Order> orderFilter(OrderListRequestDto orderListRequestDto){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (orderListRequestDto.getOrderCode() != null) {
                String orderCodeValue = "%" + orderListRequestDto.getOrderCode().toLowerCase() + "%";
                Predicate orderCodePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("orderCode")), orderCodeValue);
                predicates.add(orderCodePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
