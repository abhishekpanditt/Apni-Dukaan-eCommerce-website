package com.example.ecommerce.controller;

import com.example.ecommerce.Enum.ProductCategory;
import com.example.ecommerce.dto.RequestDto.ProductRequestDto;
import com.example.ecommerce.dto.RequestDto.UpdateProductRequestDto;
import com.example.ecommerce.dto.ResponseDto.ProductResponseDto;
import com.example.ecommerce.exception.InvalidSellerException;
import com.example.ecommerce.exception.InvalidProductException;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    //add products
    @PostMapping("/add")
    public ProductResponseDto addProduct(@RequestBody ProductRequestDto productRequestDto) throws InvalidSellerException {

        return productService.addProduct(productRequestDto);
    }

    // get all products of a particular category
    @GetMapping("/get/{category}")
    public List<ProductResponseDto> getAllProductsByCategory(@PathVariable("category") ProductCategory category){

        return productService.getAllProductsByCategory(category);
    }

    // Get all product by seller email id
    @GetMapping("/get_by_seller_email")
    public List<ProductResponseDto> getAllProductsBySellerEmailId(@RequestParam("emailId") String emailId) throws InvalidSellerException {

        return productService.getAllProductsBySellerEmailId(emailId);
    }

    // delete a product by seller id and product id
    @DeleteMapping("/delete")
    public String deleteProduct(@RequestParam("sellerId") int sellerId, @RequestParam("productId") int productId) throws InvalidSellerException {

        return productService.deleteProduct(sellerId, productId);
    }

    // return all out of stock products
    @GetMapping("/out_of_stock")
    public List<ProductResponseDto> outOfStockProducts(){

        return productService.outOfStockProducts();
    }

    // return all available products
    @GetMapping("/available")
    public List<ProductResponseDto> availableProducts(){

        return productService.availableProducts();
    }

    // return all products that have quantity less than quantity
    @GetMapping("/less_than_quantity")
    public List<ProductResponseDto> productLessThanQuantity(@RequestParam("quantity") int quantity){

        return productService.productLessThanQuantity(quantity);
    }

    //get all products by price and category
    @GetMapping("/get/{price}/{category}")
    public List<ProductResponseDto> getAllProductsByPriceAndCategory(
            @PathVariable("price") int price,
            @PathVariable("category") String productCategory){

        return productService.getAllProductsByPriceAndCategory(price, productCategory);
    }

    // return the cheapest product in a particular category
    @GetMapping("/cheapest_in_category")
    public ProductResponseDto cheapestProductInCategory(@RequestParam("category") ProductCategory category){

        return productService.cheapestProductInCategory(category);
    }

    // return the costliest product in a particular category
    @GetMapping("/costliest_in_category")
    public ProductResponseDto costliestProductInCategory(@RequestParam("category") ProductCategory category){

        return productService.costliestProductInCategory(category);
    }

    //update product price and quantity
    @PutMapping("/update_price_quantity")
    public ProductResponseDto updatePriceAndQuantity(@RequestBody UpdateProductRequestDto updateProductRequestDto) throws InvalidProductException, InvalidProductException {

        return productService.updatePriceAndQuantity(updateProductRequestDto);
    }

    // return top 5 cheapest products
    @GetMapping("/get5cheapest")
    public List<ProductResponseDto> getFiveCheapestProducts(){

        return productService.getFiveCheapestProducts();
    }

    // return top 5 costliest products
    @GetMapping("/get5costliest")
    public List<ProductResponseDto> getFiveCostliestProducts(){

        return productService.getFiveCostliestProducts();
    }
}
