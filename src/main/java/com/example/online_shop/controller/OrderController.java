package com.example.online_shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.online_shop.dto.request.AddOrEditOrderRequestDto;
import com.example.online_shop.dto.request.DataPageRequest;
import com.example.online_shop.dto.request.OrderListRequestDto;
import com.example.online_shop.dto.response.MessageResponse;
import com.example.online_shop.dto.response.OrderDetailResponse;
import com.example.online_shop.dto.response.OrderListResponseDto;
import com.example.online_shop.services.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<OrderListResponseDto> getOrders(OrderListRequestDto orderListRequestDto, DataPageRequest pageRequest) {
        return orderService.getOrders(orderListRequestDto, pageRequest.getPage());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderById(@PathVariable Long orderId){
        return orderService.getOrderById(orderId);
    }

    @PostMapping("/orders")
    public ResponseEntity<MessageResponse> addOrder(@RequestBody AddOrEditOrderRequestDto addOrderRequestDto){
        return orderService.addOrder(addOrderRequestDto);
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<MessageResponse> updateOrder(@RequestBody AddOrEditOrderRequestDto editOrderRequestDto, @PathVariable Long orderId){
        return orderService.updateOrder(editOrderRequestDto, orderId);
    }

    @DeleteMapping("orders/{orderId}/delete")
    public ResponseEntity<MessageResponse> deleteOrder(@PathVariable Long orderId){
        return orderService.deleteOrder(orderId);
    }
}
