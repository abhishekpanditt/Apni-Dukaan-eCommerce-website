package com.example.ecommerce.service;

import com.example.ecommerce.Enum.CardType;
import com.example.ecommerce.dto.RequestDto.CustomerRequestDto;
import com.example.ecommerce.dto.ResponseDto.CustomerResponseDto;
import com.example.ecommerce.exception.InvalidCustomerException;
import com.example.ecommerce.exception.MobileNoAlreadyPresentException;
import com.example.ecommerce.model.Card;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.transformer.CardTransformer;
import com.example.ecommerce.transformer.CustomerTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    public CustomerResponseDto addCustomer(CustomerRequestDto customerRequestDto) throws MobileNoAlreadyPresentException {

        if(customerRepository.findByMobNo(customerRequestDto.getMobNo())!=null)
            throw new MobileNoAlreadyPresentException("Sorry! Customer already exists!");

        // request dto -> customer
        Customer customer = CustomerTransformer.CustomerRequestDtoToCustomer(customerRequestDto);
        Cart cart = Cart.builder()
                .cartTotal(0)
                .numberOfItems(0)
                .customer(customer)
                .build();
        customer.setCart(cart);



        Customer savedCustomer = customerRepository.save(customer);  // customer and cart

        // prepare response dto
        return CustomerTransformer.CustomerToCustomerResponseDto(savedCustomer);
    }

    public String deleteCustomer(String emailId) throws InvalidCustomerException {

        Customer customer;
        try{
            customer = customerRepository.findByEmailId(emailId);
        }
        catch(Exception e){
            throw new InvalidCustomerException("Customer doesn't exist!!");
        }

        customerRepository.delete(customer);

        return "Customer deleted successfully!";
    }

    public List<CustomerResponseDto> getAllCustomers() {

        List<Customer> customers = customerRepository.findAll();

        List<CustomerResponseDto> customerResponseDtos = new ArrayList<>();
        for(Customer customer: customers){
            customerResponseDtos.add(CustomerTransformer.CustomerToCustomerResponseDto(customer));
        }

        return customerResponseDtos;
    }

    public CustomerResponseDto getByMobileOrEmail(String mobNo, String emailId) throws InvalidCustomerException {

        Customer customer = customerRepository.findByMobNo(mobNo);;

        if(customer == null) {
            customer = customerRepository.findByEmailId(emailId);

            if(customer == null){
                throw new InvalidCustomerException("Customer doesn't exist!!");
            }
        }

        return CustomerTransformer.CustomerToCustomerResponseDto(customer);
    }

    public List<CustomerResponseDto> getAgeGreaterThan25() {

        List<Customer> customers = customerRepository.getAllGreaterThan25();

        List<CustomerResponseDto> customerResponseDtos = new ArrayList<>();
        for(Customer customer: customers){
            customerResponseDtos.add(CustomerTransformer.CustomerToCustomerResponseDto(customer));
        }

        return customerResponseDtos;
    }

    public List<CustomerResponseDto> getAllUsingVisa() {

        List<Customer> requiredCustomers = new ArrayList<>();

        List<Customer> customers = customerRepository.findAll();
        for(Customer customer: customers){

            List<Card> cards = customer.getCards();
            for(Card card: cards){
                if(card.getCardType() == CardType.VISA){
                    requiredCustomers.add(customer);
                }
            }
        }

        List<CustomerResponseDto> customerResponseDtos = new ArrayList<>();
        for(Customer customer: requiredCustomers){
            customerResponseDtos.add(CustomerTransformer.CustomerToCustomerResponseDto(customer));
        }

        return customerResponseDtos;
    }

    public Customer updateDetails(CustomerRequestDto customerRequestDto) throws InvalidCustomerException {

        Customer customer;
        try{
            customer = customerRepository.findByEmailId(customerRequestDto.getEmailId());
        }
        catch (Exception e){
            throw new InvalidCustomerException("Customer Id is invalid !!");
        }

        return CustomerTransformer.CustomerRequestDtoToCustomer(customerRequestDto);
    }
}
