package com.example.ecommerce.service;

import com.example.ecommerce.dto.RequestDto.OrderRequestDto;
import com.example.ecommerce.dto.ResponseDto.CustomerResponseDto;
import com.example.ecommerce.dto.ResponseDto.ItemResponseDto;
import com.example.ecommerce.dto.ResponseDto.OrderResponseDto;
import com.example.ecommerce.exception.InvalidCardException;
import com.example.ecommerce.exception.InvalidCustomerException;
import com.example.ecommerce.exception.InvalidProductException;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.CardRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderedRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.transformer.ItemTransformer;
import com.example.ecommerce.transformer.OrderTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired ProductService productService;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CardRepository cardRepository;
    @Autowired
    private OrderedRepository orderedRepository;

    @Autowired
    private JavaMailSender emailSender;


    public Ordered placeOrder(Customer customer, Card card) throws Exception {

        Cart cart = customer.getCart();

        Ordered order = new Ordered();
        order.setOrderNo(String.valueOf(UUID.randomUUID()));

        String maskedCardNo = generateMaskedCard(card.getCardNo());
        order.setCardUsed(maskedCardNo);
        order.setCustomer(customer);

        List<Item> orderedItems = new ArrayList<>();
        for(Item item: cart.getItems()){
            try{
                productService.decreaseProductQuantity(item);
                orderedItems.add(item);
            } catch (Exception e) {
                throw new Exception("Product Out of stock");
            }
        }
        order.setItems(orderedItems);
        for(Item item: orderedItems)
            item.setOrder(order);
        order.setTotalValue(cart.getCartTotal());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("acciojob23@gmail.com");
        message.setTo("kunaljindal995@gmail.com");
        message.setSubject("Order placed on Apni Dukaan");
        message.setText("Hi Kunal, your order "+ order.getOrderNo() + "has been placed!") ;
        emailSender.send(message);

        return order;
    }

    public OrderResponseDto placeOrder(OrderRequestDto orderRequestDto) throws Exception {

        Customer customer;
        try{
            customer = customerRepository.findById(orderRequestDto.getCustomerId()).get();
        }
        catch (Exception e){
            throw new InvalidCustomerException("Customer Id is invalid !!");
        }

        Product product;
        try{
            product = productRepository.findById(orderRequestDto.getProductId()).get();
        }
        catch(Exception e){
            throw new InvalidProductException("Product doesn't exist");
        }

        Card card = cardRepository.findByCardNo(orderRequestDto.getCardNo());
        if(card==null || card.getCvv()!=orderRequestDto.getCvv() || card.getCustomer()!=customer){
            throw new InvalidCardException("Your card is not valid!!");
        }

        Item item = Item.builder()
                .requiredQuantity(orderRequestDto.getRequiredQuantity())
                .product(product)
                .build();
        try{
            productService.decreaseProductQuantity(item);
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }

        Ordered order = new Ordered();
        order.setOrderNo(String.valueOf(UUID.randomUUID()));
        String maskedCardNo = generateMaskedCard(card.getCardNo());
        order.setCardUsed(maskedCardNo);
        order.setCustomer(customer);
        order.setTotalValue(item.getRequiredQuantity()*product.getPrice());
        order.getItems().add(item);

        customer.getOrderList().add(order);
        item.setOrder(order);
        product.getItemList().add(item);

        Ordered savedOrder = orderedRepository.save(order); // order and item

        OrderResponseDto orderResponseDto = OrderTransformer.OrderToOrderResponseDto(savedOrder);

        List<ItemResponseDto> items = new ArrayList<>();
        for(Item itemEntity: savedOrder.getItems()){
            ItemResponseDto itemResponseDto = ItemTransformer.ItemToItemResponseDto(itemEntity);

            items.add(itemResponseDto);
        }

        orderResponseDto.setItems(items);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("acciojob23@gmail.com");
        message.setTo("badi.abhi@gmail.com");
        message.setSubject("Order placed on Apni Dukaan");
        message.setText("Hi Kunal, your order "+ order.getOrderNo() + "has been placed!") ;
        emailSender.send(message);

        return orderResponseDto;

    }

    public String generateMaskedCard(String cardNo){
        String maskedCardNo = "";
        for(int i = 0;i<cardNo.length()-4;i++)
            maskedCardNo += 'X';
        maskedCardNo += cardNo.substring(cardNo.length()-4);
        return maskedCardNo;

    }

    public List<OrderResponseDto> getAllOrders(int customerId) throws InvalidCustomerException {

        Customer customer;
        try{
            customer = customerRepository.findById(customerId).get();
        }
        catch (Exception e){
            throw new InvalidCustomerException("Customer Id is invalid !!");
        }

        List<Ordered> orders = customer.getOrderList();

        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();

        for(Ordered order: orders){
            OrderResponseDto orderResponseDto = OrderTransformer.OrderToOrderResponseDto(order);

            List<Item> items = order.getItems();
            List<ItemResponseDto> itemResponseDtos = new ArrayList<>();
            for(Item item: items){
                ItemResponseDto itemResponseDto = ItemTransformer.ItemToItemResponseDto(item);
                itemResponseDtos.add(itemResponseDto);
            }

            orderResponseDto.setItems(itemResponseDtos);
            orderResponseDtoList.add(orderResponseDto);
        }

        return orderResponseDtoList;
    }

    public String deleteOrder(int customerId, String orderNo) throws InvalidCustomerException {

        Customer customer;
        try{
            customer = customerRepository.findById(customerId).get();
        }
        catch (Exception e){
            throw new InvalidCustomerException("Customer Id is invalid !!");
        }

        List<Ordered> orders = customer.getOrderList();
        for(Ordered order: orders){
            if(order.getOrderNo() == orderNo){
                orders.remove(order);
            }
        }

        customerRepository.save(customer);

        return "Order deleted successfully!";
    }

    public List<OrderResponseDto> recentFiveOrders() {

        List<Ordered> orders = orderedRepository.recentFiveOrders();

        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        for(Ordered order: orders){
            orderResponseDtoList.add(OrderTransformer.OrderToOrderResponseDto(order));
        }

        return orderResponseDtoList;
    }

    public OrderResponseDto customerWithHighestTotal() {

        List<Ordered> orders = orderedRepository.findAll();

        Ordered highestOrder = null;
        int maxTotal = 0;
        int total = 0;

        List<ItemResponseDto> itemResponseDtos = null;
        for (Ordered order : orders) {
            total = order.getTotalValue();

            if (total > maxTotal) {
                maxTotal = total;
                highestOrder = order;

                List<Item> items = order.getItems();
                itemResponseDtos = new ArrayList<>();
                for (Item item : items) {
                    ItemResponseDto itemResponseDto = ItemTransformer.ItemToItemResponseDto(item);
                    itemResponseDtos.add(itemResponseDto);
                }
            }
        }

        OrderResponseDto orderResponseDto = OrderTransformer.OrderToOrderResponseDto(highestOrder);
        orderResponseDto.setItems(itemResponseDtos);
        return orderResponseDto;
    }
}
