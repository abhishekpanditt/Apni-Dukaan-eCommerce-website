package com.example.ecommerce.controller;

import com.example.ecommerce.dto.RequestDto.CustomerRequestDto;
import com.example.ecommerce.dto.ResponseDto.CustomerResponseDto;
import com.example.ecommerce.exception.InvalidCustomerException;
import com.example.ecommerce.exception.MobileNoAlreadyPresentException;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PostMapping("/add")
    public CustomerResponseDto addCustomer(@RequestBody CustomerRequestDto customerRequestDto) throws MobileNoAlreadyPresentException {

        return customerService.addCustomer(customerRequestDto);
    }

    // delete a customer by email/mob
    @DeleteMapping("delete")
    public String deleteCustomer(@RequestParam("emailId") String emailId) throws InvalidCustomerException {

        return customerService.deleteCustomer(emailId);
    }

    // view all customers
    @GetMapping("/get_all")
    public List<CustomerResponseDto> getAllCustomers(){

        return customerService.getAllCustomers();
    }

    // get a customer by email/mob
    @GetMapping("/get_by_mobNo_or_email")
    public CustomerResponseDto getByMobileOrEmail(@RequestParam ("mobNo") String mobNo, @RequestParam("emailId") String emailId) throws InvalidCustomerException {

        return customerService.getByMobileOrEmail(mobNo, emailId);
    }

    // get all customers whose age is greater than 25
    @GetMapping("/get_age_greater_than_25")
    public List<CustomerResponseDto> getAgeGreaterThan25(){

        return customerService.getAgeGreaterThan25();
    }

    // get all customers who use VISA card
    @GetMapping("/use_visa")
    public List<CustomerResponseDto> getAllUsingVisa(){

        return customerService.getAllUsingVisa();
    }

    // update a customer info by email
    @PutMapping("/update_details")
    public Customer updateDetails(@RequestBody CustomerRequestDto customerRequestDto) throws InvalidCustomerException {

        return customerService.updateDetails(customerRequestDto);
    }
}
