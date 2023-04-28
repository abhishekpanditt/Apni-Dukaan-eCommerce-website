package com.example.ecommerce.controller;

import com.example.ecommerce.dto.RequestDto.SellerRequestDto;
import com.example.ecommerce.dto.ResponseDto.SellerResponseDto;
import com.example.ecommerce.exception.EmailAlreadyPresentException;
import com.example.ecommerce.exception.InvalidSellerException;
import com.example.ecommerce.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    SellerService sellerService;

    //add seller
    @PostMapping("/add")
    public SellerResponseDto addSeller(@RequestBody SellerRequestDto sellerRequestDto) throws EmailAlreadyPresentException {

        return sellerService.addSeller(sellerRequestDto);
    }

    //  GET a seller by email
    @GetMapping("/get_by_email")
    public SellerResponseDto getSellerByEmailId(@RequestParam ("emailId") String emailId) throws InvalidSellerException {

        return sellerService.getSellerByEmailId(emailId);
    }


    // get by id
    @GetMapping("/get_by_id")
    public SellerResponseDto getSellerById(@RequestParam ("id") int id) throws InvalidSellerException {

        return sellerService.getSellerById(id);
    }

    // get all seller
    @GetMapping("/get_all_sellers")
    public List<SellerResponseDto> getAllSellers(){

        return sellerService.getAllSellers();
    }

    // update seller info based on email id
    @PutMapping("/update_mobile")
    public SellerResponseDto updateMobNo(@RequestParam ("emailId") String emailId, @RequestParam ("mobNo") String mobNo) throws InvalidSellerException {

        return sellerService.updateMobNo(emailId, mobNo);
    }

    // delete a seller based on email
    @DeleteMapping("/delete_by_email")
    public String deleteSellerByEmail(@RequestParam ("emailId") String emailId) throws InvalidSellerException {

        return sellerService.deleteSellerByEmail(emailId);
    }

    //delete by id
    @DeleteMapping("/delete_by_id")
    public String deleteSeller(@RequestParam ("id") int id) throws InvalidSellerException {

        return sellerService.deleteSeller(id);
    }

    // get all sellers of a particular age
    @GetMapping("/get_all_sellers_by_age")
    public List<SellerResponseDto> getAllSellersByAge(@RequestParam ("age") int age){

        return sellerService.getAllSellersByAge(age);
    }
}
