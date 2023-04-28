package com.example.ecommerce.transformer;

import com.example.ecommerce.dto.ResponseDto.CartResponseDto;
import com.example.ecommerce.model.Cart;

public class CartTransformer {

    public static CartResponseDto CartToCartResponseDto(Cart cart){

        return CartResponseDto.builder()
                .cartTotal(cart.getCartTotal())
                .customerName(cart.getCustomer().getName())
                .numberOfItems(cart.getNumberOfItems())
                .build();
    }

}
