package com.example.ecommerce.transformer;

import com.example.ecommerce.dto.ResponseDto.OrderResponseDto;
import com.example.ecommerce.model.Ordered;

public class OrderTransformer {

    public static OrderResponseDto OrderToOrderResponseDto(Ordered ordered){

        return OrderResponseDto.builder()
                .orderNo(ordered.getOrderNo())
                .totalValue(ordered.getTotalValue())
                .orderDate(ordered.getOrderDate())
                .cardUsed(ordered.getCardUsed())
                .customerName(ordered.getCustomer().getName())
                .build();
    }
}
