package com.example.ecommerce.transformer;

import com.example.ecommerce.dto.RequestDto.CardRequestDto;
import com.example.ecommerce.dto.ResponseDto.CardResponseDto;
import com.example.ecommerce.model.Card;

public class CardTransformer {

    public static Card CardRequestDtoToCard(CardRequestDto cardRequestDto){

        return Card.builder()
                .cardNo(cardRequestDto.getCardNo())
                .cardType(cardRequestDto.getCardType())
                .cvv(cardRequestDto.getCvv())
                .expiryDate(cardRequestDto.getExpiryDate())
                .build();
    }

    public static CardResponseDto CardToCardResponseDto(Card card){

        return CardResponseDto.builder()
                .cardNo(card.getCardNo())
                .customerName(card.getCustomer().getName())
                .customerMobNo(card.getCustomer().getMobNo())
                .build();
    }
}
