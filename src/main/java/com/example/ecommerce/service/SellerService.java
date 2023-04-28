package com.example.ecommerce.service;

import com.example.ecommerce.dto.RequestDto.SellerRequestDto;
import com.example.ecommerce.dto.ResponseDto.SellerResponseDto;
import com.example.ecommerce.exception.EmailAlreadyPresentException;
import com.example.ecommerce.exception.InvalidSellerException;
import com.example.ecommerce.model.Seller;
import com.example.ecommerce.repository.SellerRepository;
import com.example.ecommerce.transformer.SellerTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SellerService {

    @Autowired
    SellerRepository sellerRepository;

    public SellerResponseDto addSeller(SellerRequestDto sellerRequestDto) throws EmailAlreadyPresentException {

//        Seller seller = new Seller();
//        seller.setName(sellerRequestDto.getName());
//        seller.setEmailId(sellerRequestDto.getEmailId());
//        seller.setMobNo(sellerRequestDto.getMobNo());
//        seller.setAge(sellerRequestDto.getAge());

         if(sellerRepository.findByEmailId(sellerRequestDto.getEmailId())!=null)
             throw new EmailAlreadyPresentException("Email Id already registered");

        Seller seller = SellerTransformer.SellerRequestDtoToSeller(sellerRequestDto);
        Seller savedSeller = sellerRepository.save(seller);

       // prepare response Dto
        SellerResponseDto sellerResponseDto = SellerTransformer.SellerToSellerResponseDto(savedSeller);
        return sellerResponseDto;

    }

    public SellerResponseDto getSellerByEmailId(String emailId) throws InvalidSellerException {

        Seller seller;
        try{
            seller = sellerRepository.findByEmailId(emailId);
        }
        catch (Exception e){
            throw new InvalidSellerException("Seller doesn't exist!!");
        }

        return SellerTransformer.SellerToSellerResponseDto(seller);
    }

    public SellerResponseDto getSellerById(int id) throws InvalidSellerException {

        Seller seller;
        try{
            seller = sellerRepository.findById(id).get();
        }
        catch(Exception e){
            throw new InvalidSellerException("Seller doesn't exist");
        }

        return SellerTransformer.SellerToSellerResponseDto(seller);
    }

    public List<SellerResponseDto> getAllSellers() {

        List<Seller> sellers = sellerRepository.findAll();

        List<SellerResponseDto> sellerResponseDtos = new ArrayList<>();
        for(Seller seller: sellers){
            sellerResponseDtos.add(SellerTransformer.SellerToSellerResponseDto(seller));
        }

        return sellerResponseDtos;
    }

    public SellerResponseDto updateMobNo(String emailId, String mobNo) throws InvalidSellerException {

        Seller seller;
        try{
            seller = sellerRepository.findByEmailId(emailId);
        }
        catch(Exception e){
            throw new InvalidSellerException("Seller doesn't exist!!");
        }

        SellerResponseDto sellerResponseDto = new SellerResponseDto();
        sellerResponseDto.setMobile(mobNo);
        sellerResponseDto.setAge(seller.getAge());
        sellerResponseDto.setName(seller.getName());

        return sellerResponseDto;
    }

    public String deleteSeller(int id) throws InvalidSellerException {

        try{
            Seller seller = sellerRepository.findById(id).get();
        }
        catch(Exception e){
            throw new InvalidSellerException("Seller doesn't exist!!");
        }
        sellerRepository.deleteById(id);
        return "Seller deleted succesfully!";
    }

    public String deleteSellerByEmail(String emailId) throws InvalidSellerException {

        Seller seller;
        try{
             seller = sellerRepository.findByEmailId(emailId);
        }
        catch(Exception e){
            throw new InvalidSellerException("Seller doesn't exist!!");
        }

        sellerRepository.delete(seller);
        return "Seller deleted successfully!";
    }

    public List<SellerResponseDto> getAllSellersByAge(int age) {

        List<Seller> sellers = sellerRepository.findAll();

        List<SellerResponseDto> sellerResponseDtos = new ArrayList<>();
        for(Seller seller: sellers){
            if(seller.getAge() == age){
                sellerResponseDtos.add(SellerTransformer.SellerToSellerResponseDto(seller));
            }
        }

        return sellerResponseDtos;
    }
}
