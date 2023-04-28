package com.example.ecommerce.controller;

import com.example.ecommerce.dto.RequestDto.CheckoutCartRequestDto;
import com.example.ecommerce.dto.RequestDto.ItemRequestDto;
import com.example.ecommerce.dto.ResponseDto.CartResponseDto;
import com.example.ecommerce.dto.ResponseDto.ItemResponseDto;
import com.example.ecommerce.dto.ResponseDto.OrderResponseDto;
import com.example.ecommerce.exception.InvalidCustomerException;
import com.example.ecommerce.exception.InvalidProductException;
import com.example.ecommerce.model.Item;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    ItemService itemService;

    @Autowired
    CartService cartService;


    //add to cart
    @PostMapping("/add")
    public ResponseEntity addToCart(@RequestBody ItemRequestDto itemRequestDto) throws Exception {

        try{
            Item savedItem = itemService.addItem(itemRequestDto);
            CartResponseDto cartResponseDto = cartService.saveCart(itemRequestDto.getCustomerId(),savedItem);
            return new ResponseEntity(cartResponseDto,HttpStatus.ACCEPTED);
        }
        catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //checkout cart
    @PostMapping("/checkout")
    public OrderResponseDto checkOutCart(@RequestBody CheckoutCartRequestDto checkoutCartRequestDto) throws Exception {

        return cartService.checkOutCart(checkoutCartRequestDto);
    }

    // remove from cart
    @DeleteMapping("/remove_from_cart")
    public CartResponseDto removeFromCart(@RequestBody ItemRequestDto itemRequestDto) throws Exception {

        Item removeItem = itemService.removedItem(itemRequestDto);
        return cartService.removeFromCart(itemRequestDto.getCustomerId(), removeItem);
    }

    // view all items in cart
    @GetMapping("view")
    public List<ItemResponseDto> viewItems(@RequestParam("customerId") int customerId) throws InvalidCustomerException {

        return cartService.viewItems(customerId);
    }


    // email sending
    // my email - kunaljindal995@gmail.com
}
