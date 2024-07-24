package com.example.online_shop.services;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.online_shop.dto.request.AddOrEditOrderRequestDto;
import com.example.online_shop.dto.request.OrderListRequestDto;
import com.example.online_shop.dto.response.MessageResponse;
import com.example.online_shop.dto.response.OrderDetailResponse;
import com.example.online_shop.dto.response.OrderListResponse;
import com.example.online_shop.dto.response.OrderListResponseDto;
import com.example.online_shop.model.Customer;
import com.example.online_shop.model.Item;
import com.example.online_shop.model.Order;
import com.example.online_shop.repository.CustomerRepository;
import com.example.online_shop.repository.ItemRepository;
import com.example.online_shop.repository.OrderRepository;
import com.example.online_shop.services.specifications.OrderSpecification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    public ResponseEntity<OrderListResponseDto> getOrders(OrderListRequestDto orderListRequestDto, Pageable pageable){
        try {
            Specification<Order> orderSpec = OrderSpecification.orderFilter(orderListRequestDto);

            Page<Order> orders = orderRepository.findAll(orderSpec, pageable);

            List<OrderListResponse> orderListResponses = orders.getContent().stream().map(order -> {

                return new OrderListResponse(
                    order.getOrderId(),
                    order.getOrderCode(),
                    order.getOrderDate(),
                    order.getTotalPrice()
                );
            }).collect(Collectors.toList());

            OrderListResponseDto orderListResponseDto = new OrderListResponseDto();
            orderListResponseDto.setTotalData(orders.getTotalElements());
            orderListResponseDto.setOrderList(orderListResponses);

            return ResponseEntity.ok(orderListResponseDto);
        } catch (Exception e) {
            log.error(null, e);
            return ResponseEntity.internalServerError().body(new OrderListResponseDto(0L, Collections.emptyList()));
        }
    }

    public ResponseEntity<OrderDetailResponse> getOrderById(Long orderId){
        try {
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();

                OrderDetailResponse response = OrderDetailResponse.builder()
                    .orderId(order.getOrderId())
                    .orderCode(order.getOrderCode())
                    .orderDate(order.getOrderDate())
                    .totalPrice(order.getTotalPrice())
                    .customerId(order.getCustomer().getCustomerId())
                    .itemId(order.getItem().getItemId())
                    .quantity(order.getQuantity())
                    .build();
                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.notFound().build();  
            }

        } catch (Exception e) {
            log.error("Error occurred while fetching order details", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<MessageResponse>addOrder(AddOrEditOrderRequestDto addOrderRequestDto){
        try {
            Optional<Customer> customerOptional = customerRepository.findByCustomerId(addOrderRequestDto.getCustomerId());
            if(!customerOptional.isPresent()){
                throw new RuntimeException("customer not found with id: " + addOrderRequestDto.getCustomerId()); 
            }

            Customer customer = customerOptional.get();

            Optional<Item> itemOptional = itemRepository.findByItemId(addOrderRequestDto.getItemsId());
            if(!itemOptional.isPresent()){
                throw new RuntimeException("item not found with id: " + addOrderRequestDto.getItemsId()); 
            }

            Item item = itemOptional.get();

            Order order = new Order();
            order.setCustomer(customer);
            order.setItem(item);
            order.setQuantity(addOrderRequestDto.getQuantity());
            order.setOrderDate(LocalDate.now());

            Order savedOrder = orderRepository.save(order);

            Integer totalPrice = countTotalPrice(savedOrder, item);

            String orderCode = generateOrderCode(savedOrder);
            savedOrder.setOrderCode(orderCode);
            savedOrder.setTotalPrice(totalPrice);
            savedOrder = orderRepository.save(savedOrder);

            customer.setLastOrderDate(LocalDate.now());
            customerRepository.save(customer);

            return ResponseEntity.ok().body(new MessageResponse(HttpStatus.CREATED.value(), "Order berhasil ditambahkan"));
        } catch (Exception e) {
            log.error("Terjadi kesalahan saat membuat order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Terjadi kesalahan saat membuat order: " + e.getMessage()));
        }
    }

    public ResponseEntity<MessageResponse> updateOrder(AddOrEditOrderRequestDto editOrderRequestDto, Long orderId){
        try {
            Optional<Customer> customerOptional = customerRepository.findByCustomerId(editOrderRequestDto.getCustomerId());
            if(!customerOptional.isPresent()){
                throw new RuntimeException("customer not found with id: " + editOrderRequestDto.getCustomerId()); 
            }

            Customer customer = customerOptional.get();

            Optional<Item> itemOptional = itemRepository.findByItemId(editOrderRequestDto.getItemsId());
            if(!itemOptional.isPresent()){
                throw new RuntimeException("item not found with id: " + editOrderRequestDto.getItemsId()); 
            }

            Item item = itemOptional.get();

            Optional<Order> optionalOrder = orderRepository.findById(orderId);

            if(optionalOrder.isPresent()){
                Order order = optionalOrder.get();
                order.setCustomer(customer);
                order.setItem(item);
                order.setQuantity(editOrderRequestDto.getQuantity());
                order.setOrderDate(LocalDate.now());

                Order savedOrder = orderRepository.save(order);

                Integer totalPrice = countTotalPrice(savedOrder, item);

                String orderCode = generateOrderCode(savedOrder);
                savedOrder.setOrderCode(orderCode);
                savedOrder.setTotalPrice(totalPrice);
                savedOrder = orderRepository.save(savedOrder);

                return ResponseEntity.ok().body(new MessageResponse(HttpStatus.CREATED.value(), "Order berhasil diedit"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Gagal memperbarui data order:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Gagal memperbarui data order: " + e.getMessage()));
        }
    }

    public ResponseEntity<MessageResponse> deleteOrder(Long orderId){
        try {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);

            if(optionalOrder.isPresent()){
                orderRepository.deleteById(orderId);
                return ResponseEntity.ok().body(new MessageResponse(HttpStatus.CREATED.value(), "Order berhasil dihapus"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Gagal menghapus data order:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Gagal menghapus data order: " + e.getMessage()));
        }
    }

    private String generateOrderCode(Order order) {
        return "O" + order.getOrderId() + order.getCustomer().getCustomerName().replaceAll("[^a-zA-Z0-9]", "") + order.getItem().getItemsName().replaceAll("[^a-zA-Z0-9]", "");
    }

    private Integer countTotalPrice(Order order, Item item){
        return order.getQuantity()*item.getPrice();
    }
}
