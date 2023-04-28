package com.example.ecommerce.service;

import com.example.ecommerce.Enum.ProductStatus;
import com.example.ecommerce.dto.RequestDto.ItemRequestDto;
import com.example.ecommerce.exception.InvalidCustomerException;
import com.example.ecommerce.exception.InvalidProductException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Item;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.transformer.ItemTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ProductRepository productRepository;

    public Item addItem(ItemRequestDto itemRequestDto) throws Exception {

        Customer customer;
        try{
            customer = customerRepository.findById(itemRequestDto.getCustomerId()).get();
        }
        catch (Exception e){
            throw new InvalidCustomerException("Customer Id is invalid !!");
        }

        Product product;
        try{
            product = productRepository.findById(itemRequestDto.getProductId()).get();
        }
        catch(Exception e){
            throw new InvalidProductException("Product doesn't exist");
        }

        if(itemRequestDto.getRequiredQuantity()>product.getQuantity() || product.getProductStatus()!= ProductStatus.AVAILABLE){
            throw new Exception("Product out of Stock");
        }

        Item item = ItemTransformer.ItemRequestDtoToItem(itemRequestDto);
        item.setCart(customer.getCart());
        item.setProduct(product);

        product.getItemList().add(item);
        return itemRepository.save(item);
    }

    public Item removedItem(ItemRequestDto itemRequestDto) throws Exception {

        Customer customer;
        try{
            customer = customerRepository.findById(itemRequestDto.getCustomerId()).get();
        }
        catch (Exception e){
            throw new InvalidCustomerException("Customer Id is invalid !!");
        }

        Cart currentCart = customer.getCart();
        List<Item> items = currentCart.getItems();

        Product currentProduct = null;

//        int count = 0;
        for(Item item: items){
            if(item.getProduct().getId() == itemRequestDto.getProductId()){
                currentProduct = item.getProduct();
//                count++;
            }
        }

        int enteredQuantity = itemRequestDto.getRequiredQuantity();
//        if(enteredQuantity > count){
//            assert currentProduct != null;
//            throw new Exception("Enter correct quantity of " + currentProduct.getName());
//        }

        Item item = new Item();
        item.setRequiredQuantity(enteredQuantity);
        item.setProduct(currentProduct);
        item.setCart(currentCart);

//        product.getItemList().remove(item);

        return itemRepository.save(item);
    }

}
