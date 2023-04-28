package com.example.ecommerce.service;

import com.example.ecommerce.Enum.CardType;
import com.example.ecommerce.dto.RequestDto.CardRequestDto;
import com.example.ecommerce.dto.RequestDto.CustomerRequestDto;
import com.example.ecommerce.dto.ResponseDto.CardResponseDto;
import com.example.ecommerce.exception.InvalidCustomerException;
import com.example.ecommerce.exception.MobileNoAlreadyPresentException;
import com.example.ecommerce.model.Card;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CardRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.transformer.CardTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static com.example.ecommerce.Enum.CardType.*;

@Service
public class CardService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CardRepository cardRepository;

    public CardResponseDto addCard(CardRequestDto cardRequestDto) throws InvalidCustomerException {

        Customer customer = customerRepository.findByMobNo(cardRequestDto.getMobNo());
        if(customer==null){
            throw new InvalidCustomerException("Sorry! The customer doesn't exist");
        }

        Card card = CardTransformer.CardRequestDtoToCard(cardRequestDto);
        card.setCustomer(customer);

        customer.getCards().add(card);
        customerRepository.save(customer);

        // response dto
        return CardResponseDto.builder()
                .customerName(customer.getName())
                .cardNo(card.getCardNo())
                .build();

    }

    public List<CardResponseDto> getAllVisaCards() {

        List<Card> cards = cardRepository.findByCardType(VISA);

        List<CardResponseDto> cardResponseDtos = new ArrayList<>();

        for(Card card: cards){
            cardResponseDtos.add(CardTransformer.CardToCardResponseDto(card));
        }

        return cardResponseDtos;
    }

    public List<CardResponseDto> getAllMastercardGreaterThan(Date date) {

//        List<Card> cards = cardRepository.findByCardType(MASTERCARD);
        List<Card> cards = cardRepository.findAllMastercardGreaterThan(date);

        List<CardResponseDto> cardResponseDtos = new ArrayList<>();
        for(Card card: cards){
            cardResponseDtos.add(CardTransformer.CardToCardResponseDto(card));
        }

        return cardResponseDtos;
    }

    public CardType getMaxCardType() {

        List<Card> mastercard = cardRepository.findByCardType(MASTERCARD);
        List<Card> visa = cardRepository.findByCardType(VISA);
        List<Card> rupay = cardRepository.findByCardType(RUPAY);

        int countMastercard = mastercard.size();
        int countVisa = visa.size();
        int countRupay = rupay.size();

        if((countMastercard > countVisa) && (countMastercard > countRupay)){
            return MASTERCARD;
        }
        else if((countVisa > countMastercard) && (countVisa > countRupay)){
            return VISA;
        }
        else{
            return RUPAY;
        }
    }

    public String deleteCard(int customerId, String cardNo) throws InvalidCustomerException {

        Customer customer;
        try{
            customer = customerRepository.findById(customerId).get();
        }
        catch (Exception e){
            throw new InvalidCustomerException("Customer Id is invalid !!");
        }

        List<Card> cards = customer.getCards();
        for(Card card: cards){
            if(card.getCardNo() == cardNo){
                cards.remove(card);
            }
        }
        customerRepository.save(customer);

        return "Card is deleted from this account!";
    }
}