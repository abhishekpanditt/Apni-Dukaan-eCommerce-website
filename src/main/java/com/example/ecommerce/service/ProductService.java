package com.example.ecommerce.service;

import com.example.ecommerce.Enum.ProductCategory;
import com.example.ecommerce.Enum.ProductStatus;
import com.example.ecommerce.dto.RequestDto.ProductRequestDto;
import com.example.ecommerce.dto.RequestDto.UpdateProductRequestDto;
import com.example.ecommerce.dto.ResponseDto.ProductResponseDto;
import com.example.ecommerce.exception.InvalidSellerException;
import com.example.ecommerce.exception.InvalidProductException;
import com.example.ecommerce.model.Item;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Seller;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.SellerRepository;
import com.example.ecommerce.transformer.ProductTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.ecommerce.Enum.ProductStatus.AVAILABLE;
import static com.example.ecommerce.Enum.ProductStatus.OUT_OF_STOCK;

@Service
public class ProductService {

    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    private ProductRepository productRepository;

    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) throws InvalidSellerException {

        Seller seller;
        try {
            seller = sellerRepository.findById(productRequestDto.getSellerId()).get();
        } catch (Exception e) {
            throw new InvalidSellerException("Seller doesn't exist");
        }

        Product product = ProductTransformer.ProductRequestDtoToProduct(productRequestDto);
        product.setSeller(seller);

        // add product to current products of seller
        seller.getProducts().add(product);
        sellerRepository.save(seller);                          // saves both seller and product

        // prepare Response Dto
        return ProductTransformer.ProductToProductResponseDto(product);
    }

    public List<ProductResponseDto> getAllProductsByCategory(ProductCategory category) {

        List<Product> products = productRepository.findByProductCategory(category);

        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for (Product product : products) {
            productResponseDtos.add(ProductTransformer.ProductToProductResponseDto(product));
        }

        return productResponseDtos;
    }

    public List<ProductResponseDto> getAllProductsBySellerEmailId(String emailId) throws InvalidSellerException {

        try {
            Seller seller = sellerRepository.findByEmailId(emailId);
        } catch (Exception e) {
            throw new InvalidSellerException("Seller doesn't exist!!");
        }

        List<Product> products = productRepository.findBySellerEmailId(emailId);

        List<ProductResponseDto> productResponseDtos = new ArrayList<>();

        for (Product product : products) {
            productResponseDtos.add(ProductTransformer.ProductToProductResponseDto(product));
        }

        return productResponseDtos;
    }

    public String deleteProduct(int sellerId, int productId) throws InvalidSellerException {

        Seller seller;
        try {
            seller = sellerRepository.findById(sellerId).get();
        } catch (Exception e) {
            throw new InvalidSellerException("Seller doesn't exist!!");
        }

        List<Product> products = seller.getProducts();
        for(Product product: products){
            if(product.getId() == productId){
                productRepository.delete(product);
            }
        }
        sellerRepository.save(seller);

        return ("Product deleted successfully!");
    }


    public List<ProductResponseDto> outOfStockProducts() {

        List<Product> products = productRepository.findByProductStatus(OUT_OF_STOCK);

        List<ProductResponseDto> productResponseDtos = new ArrayList<>();

        for (Product product : products) {
            productResponseDtos.add(ProductTransformer.ProductToProductResponseDto(product));
        }

        return productResponseDtos;
    }

    public List<ProductResponseDto> availableProducts() {

        List<Product> products = productRepository.findByProductStatus(AVAILABLE);

        List<ProductResponseDto> productResponseDtos = new ArrayList<>();

        for (Product product : products) {
            productResponseDtos.add(ProductTransformer.ProductToProductResponseDto(product));
        }

        return productResponseDtos;
    }

    public List<ProductResponseDto> productLessThanQuantity(int quantity) {

        List<Product> products = productRepository.findAll();

        List<ProductResponseDto> productResponseDtos = new ArrayList<>();

        for (Product product : products) {
            if (product.getQuantity() < quantity) {
                productResponseDtos.add(ProductTransformer.ProductToProductResponseDto(product));
            }
        }

        return productResponseDtos;
    }

    public List<ProductResponseDto> getAllProductsByPriceAndCategory(int price, String productCategory){

        List<Product> products = productRepository.getAllProductsByPriceAndCategory(price,productCategory);

        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for(Product product: products){
            productResponseDtos.add(ProductTransformer.ProductToProductResponseDto(product));
        }

        return productResponseDtos;
    }

    public ProductResponseDto cheapestProductInCategory(ProductCategory category) {

        List<Product> products = productRepository.findByProductCategory(category);

        int cheapest = Integer.MAX_VALUE;
        Product required = null;
        for (Product product : products) {
            if (product.getPrice() < cheapest) {
                cheapest = product.getPrice();
                required = product;
            }
        }

        return ProductTransformer.ProductToProductResponseDto(required);
    }

    public ProductResponseDto costliestProductInCategory(ProductCategory category) {

        List<Product> products = productRepository.findByProductCategory(category);

        int cheapest = Integer.MIN_VALUE;
        Product required = null;
        for (Product product : products) {
            if (product.getPrice() > cheapest) {
                cheapest = product.getPrice();
                required = product;
            }
        }

        return ProductTransformer.ProductToProductResponseDto(required);
    }

    public ProductResponseDto updatePriceAndQuantity(UpdateProductRequestDto updateProductRequestDto) throws InvalidProductException {

        Product product;
        try {
            product = productRepository.findById(updateProductRequestDto.getProductId()).get();
        } catch (Exception e) {
            throw new InvalidProductException("Product not found!!");
        }

        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setPrice(updateProductRequestDto.getPrice());
        productResponseDto.setQuantity(updateProductRequestDto.getQuantity());
        productResponseDto.setProductName(product.getName());
        productResponseDto.setSellerName(product.getSeller().getName());
        productResponseDto.setProductStatus(product.getProductStatus());

        return productResponseDto;
    }

    public void decreaseProductQuantity(Item item) throws Exception {

        Product product = item.getProduct();
        int quantity = item.getRequiredQuantity();
        int currentQuantity = product.getQuantity();
        if(quantity > currentQuantity){
            throw new Exception("Out of stock");
        }
        product.setQuantity(currentQuantity - quantity);
        if(product.getQuantity() == 0){
            product.setProductStatus(ProductStatus.OUT_OF_STOCK);
        }
    }

    public List<ProductResponseDto> getFiveCheapestProducts() {

        List<Product> products = productRepository.findFirst5ByOrderByPriceAsc();

        List<ProductResponseDto> productResponseDtos = new ArrayList<>();

        for(Product product: products){
            productResponseDtos.add(ProductTransformer.ProductToProductResponseDto(product));
        }

        return productResponseDtos;

    }

    public List<ProductResponseDto> getFiveCostliestProducts() {

        List<Product> products = productRepository.findFirst5ByOrderByPriceDesc();

        List<ProductResponseDto> productResponseDtos = new ArrayList<>();

        for(Product product: products){
            productResponseDtos.add(ProductTransformer.ProductToProductResponseDto(product));
        }

        return productResponseDtos;
    }
}
