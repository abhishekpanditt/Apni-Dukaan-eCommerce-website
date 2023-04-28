package com.example.ecommerce.service;

import com.example.ecommerce.dto.RequestDto.CheckoutCartRequestDto;
import com.example.ecommerce.dto.ResponseDto.CartResponseDto;
import com.example.ecommerce.dto.ResponseDto.ItemResponseDto;
import com.example.ecommerce.dto.ResponseDto.OrderResponseDto;
import com.example.ecommerce.exception.InvalidCardException;
import com.example.ecommerce.exception.InvalidCustomerException;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.transformer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired OrderService orderService;

    @Autowired
    OrderedRepository orderedRepository;

    @Autowired
    private JavaMailSender emailSender;

    public CartResponseDto saveCart(Integer customerId, Item item){

        Customer customer = customerRepository.findById(customerId).get();
        Cart cart = customer.getCart();

        int newTotal = cart.getCartTotal() + item.getRequiredQuantity()*item.getProduct().getPrice();
        cart.setCartTotal(newTotal);
        cart.getItems().add(item);

        cart.setNumberOfItems(cart.getItems().size());
        Cart savedCart = cartRepository.save(cart);

        CartResponseDto cartResponseDto = CartTransformer.CartToCartResponseDto(savedCart);

        List<ItemResponseDto> itemResponseDtoList = new ArrayList<>();
        for(Item itemEntity: savedCart.getItems()){
            ItemResponseDto itemResponseDto = ItemTransformer.ItemToItemResponseDto(itemEntity);

            itemResponseDtoList.add(itemResponseDto);
        }

        cartResponseDto.setItems(itemResponseDtoList);
        return cartResponseDto;
    }

    public OrderResponseDto checkOutCart(CheckoutCartRequestDto checkoutCartRequestDto) throws Exception {

        Customer customer;
        try{
            customer = customerRepository.findById(checkoutCartRequestDto.getCustomerId()).get();
        }
        catch (Exception e){
            throw new InvalidCustomerException("Customer id is invalid!!!");
        }

        Card card = cardRepository.findByCardNo(checkoutCartRequestDto.getCardNo());
        if(card==null || card.getCvv()!=checkoutCartRequestDto.getCvv() || card.getCustomer()!=customer){
            throw new InvalidCardException("Your card is not valid!!");
        }

        Cart cart = customer.getCart();
        if(cart.getNumberOfItems()==0){
            throw new Exception("Cart is empty!!");
        }

        try{
            Ordered order = orderService.placeOrder(customer,card);  // throw exception if product goes out of stock
            customer.getOrderList().add(order);
            Ordered savedOrder = orderedRepository.save(order);
            resetCart(cart);
//          customerRepository.save(customer);

            // prepare response dto
            OrderResponseDto orderResponseDto = OrderTransformer.OrderToOrderResponseDto(savedOrder);


            List<ItemResponseDto> items = new ArrayList<>();
            for(Item itemEntity: savedOrder.getItems()){
                ItemResponseDto itemResponseDto = ItemTransformer.ItemToItemResponseDto(itemEntity);

                items.add(itemResponseDto);
            }

            orderResponseDto.setItems(items);

            return orderResponseDto;
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public void resetCart(Cart cart){

        cart.setCartTotal(0);
        cart.setNumberOfItems(0);
        cart.setItems(new ArrayList<>());
    }

    public CartResponseDto removeFromCart(int customerId, Item item) throws Exception {

        Customer customer = customerRepository.findById(customerId).get();
        Cart cart = customer.getCart();

        List<Item> items = cart.getItems();
        for(Item itemEntity: items){
            if(itemEntity.getRequiredQuantity() == item.getRequiredQuantity()){
                cart.setNumberOfItems(cart.getItems().size() - 1);
//                items.remove(itemEntity);
            }
            else if(itemEntity.getRequiredQuantity() > item.getRequiredQuantity()){
                cart.setNumberOfItems(cart.getItems().size());
                itemEntity.setRequiredQuantity(itemEntity.getRequiredQuantity() - item.getRequiredQuantity());
            }
            else{
                throw new Exception("Enter correct quantity of " + itemEntity.getProduct().getName());
            }
        }

        int newTotal = cart.getCartTotal() - item.getRequiredQuantity() * item.getProduct().getPrice();
        if(newTotal >= 0){
            cart.setCartTotal(newTotal);
        }
        else{
            throw new Exception("No item is present in cart!!");
        }

        Cart savedCart = cartRepository.save(cart);

        //prepare cart response dto
        CartResponseDto cartResponseDto = CartTransformer.CartToCartResponseDto(savedCart);

        List<ItemResponseDto> itemResponseDtoList = new ArrayList<>();
        for(Item itemEntity: savedCart.getItems()){
            itemResponseDtoList.add(ItemTransformer.ItemToItemResponseDto(itemEntity));
        }

        cartResponseDto.setItems(itemResponseDtoList);
        return cartResponseDto;

    }

    public List<ItemResponseDto> viewItems(int customerId) throws InvalidCustomerException {

        Customer customer;
        try{
            customer = customerRepository.findById(customerId).get();
        }
        catch (Exception e){
            throw new InvalidCustomerException("Customer Id is invalid !!");
        }

        List<Item> items = customer.getCart().getItems();

        List<ItemResponseDto> itemResponseDtoList = new ArrayList<>();
        for(Item item: items){
            itemResponseDtoList.add(ItemTransformer.ItemToItemResponseDto(item));
        }

        return itemResponseDtoList;
    }
}
