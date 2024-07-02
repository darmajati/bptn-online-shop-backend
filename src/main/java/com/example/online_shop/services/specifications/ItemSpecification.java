package com.example.online_shop.services.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.online_shop.dto.request.ItemListRequestDto;
import com.example.online_shop.model.Item;

import jakarta.persistence.criteria.Predicate;

public class ItemSpecification {
    public static Specification<Item> itemFilter(ItemListRequestDto itemListRequestDto){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (itemListRequestDto.getItemsName() != null) {
                String itemsNameValue = "%" + itemListRequestDto.getItemsName().toLowerCase() + "%";
                Predicate itemsNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("itemsName")), itemsNameValue);
                predicates.add(itemsNamePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Item> isActiveTrue() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isAvailable"));
    }
}
