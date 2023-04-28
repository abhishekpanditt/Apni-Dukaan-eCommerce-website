package com.example.ecommerce.controller;

import com.example.ecommerce.dto.RequestDto.ItemRequestDto;
import com.example.ecommerce.dto.RequestDto.OrderRequestDto;
import com.example.ecommerce.dto.ResponseDto.CustomerResponseDto;
import com.example.ecommerce.dto.ResponseDto.OrderResponseDto;
import com.example.ecommerce.exception.InvalidCustomerException;
import com.example.ecommerce.model.Ordered;
import com.example.ecommerce.repository.OrderedRepository;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderedRepository orderedRepository;

    // API to order and item individually
    @PostMapping("/place")
    public OrderResponseDto placeDirectOrder(@RequestBody OrderRequestDto orderRequestDto) throws Exception {

        return orderService.placeOrder(orderRequestDto);
    }

    // get all the orders for a customer
    @GetMapping("/get_all")
    public List<OrderResponseDto> getAllOrders(@RequestParam("customerId") int customerId) throws InvalidCustomerException {

        return orderService.getAllOrders(customerId);
    }

    // get recent 5 orders
    @GetMapping("/get_five_recent")
    public List<OrderResponseDto> recentFiveOrders(){

        return orderService.recentFiveOrders();
    }


    // delete an order from the order list
    @DeleteMapping("/delete")
    public String deleteOrder(@RequestParam("customerId") int customerId, @RequestParam("orderNo") String orderNo) throws InvalidCustomerException {

        return orderService.deleteOrder(customerId, orderNo);
    }

    // select the order and also tell the customer name with the highest total value
    @GetMapping("/customer_with_highest_total")
    public OrderResponseDto customerWithHighestTotal(){

        return orderService.customerWithHighestTotal();
    }
}
