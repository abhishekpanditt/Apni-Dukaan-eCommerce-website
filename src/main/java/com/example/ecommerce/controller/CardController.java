package com.example.ecommerce.controller;

import com.example.ecommerce.Enum.CardType;
import com.example.ecommerce.dto.RequestDto.CardRequestDto;
import com.example.ecommerce.dto.ResponseDto.CardResponseDto;
import com.example.ecommerce.exception.InvalidCustomerException;
import com.example.ecommerce.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    CardService cardService;

    @PostMapping("/add")
    public ResponseEntity addCard(@RequestBody CardRequestDto cardRequestDto){
        try{
            CardResponseDto cardResponseDto = cardService.addCard(cardRequestDto);
            return new ResponseEntity(cardResponseDto, HttpStatus.CREATED);
        } catch (InvalidCustomerException e) {
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    // get all VISA cards
    @GetMapping("/get_visa_card")
    public List<CardResponseDto> getAllVisaCards(){

        return cardService.getAllVisaCards();
    }

    // get all MASTERCARD cards whose expiry is greater than given date
    @GetMapping("/get_all_mastercard_greater_than_expiry")
    public List<CardResponseDto> getAllMastercardGreaterThan(@RequestParam("date") Date date){

        return cardService.getAllMastercardGreaterThan(date);
    }

    // Return the CardType which has maximum number of that card
    @GetMapping("/get_max_cardtype")
    public CardType getMaxCardType(){

        return cardService.getMaxCardType();
    }

    //delete card
    @DeleteMapping("/delete")
    public String deleteCard(@RequestParam("customerId") int customerId, @RequestParam("cardNo") String cardNo) throws InvalidCustomerException {

        return cardService.deleteCard(customerId, cardNo);
    }

}