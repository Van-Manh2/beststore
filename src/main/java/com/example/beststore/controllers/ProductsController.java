package com.example.beststore.controllers;


import com.example.beststore.models.Product;
import com.example.beststore.models.ProductDto;
import com.example.beststore.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductRepository repo ;

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Product> products = repo.findAll();
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/create-product";

    }
    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result) {
        if(productDto.getImageFile() .isEmpty()){
            result.addError(new FieldError("ProductDto", "imageFile", "The ImageFile is required"));
        }
        if(result.hasErrors()){
            return "products/create-product";
        }

        MultipartFile image = productDto.getImageFile(); // Lấy file từ DTO
        Date createdAt = new Date(); // Lấy thời gian hiện tại
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename(); // Tạo tên file lưu

        try {
            // Thư mục lưu ảnh
            String uploadDir = "src/main/resources/static/images/";
            Path uploadPath = Paths.get(uploadDir);

            // Nếu thư mục chưa tồn tại thì tạo mới
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lưu file vào thư mục
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        Product product = new Product();
        product.setName( productDto.getName() );
        product.setBrand( productDto.getBrand() );
        product.setCategory( productDto.getCategory() );
        product.setPrice( productDto.getPrice() );
        product.setDescription( productDto.getDescription() );
        product.setCreatedAT( createdAt );
        product.setImageFileName(storageFileName);

        repo.save(product);

    return "redirect:/products";
    }
    @GetMapping("/edit")
    public String showEditPage(Model model,
    @RequestParam int id) {
        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName( product.getName() );
            productDto.setBrand( product.getBrand() );
            productDto.setCategory( product.getCategory() );
            productDto.setPrice( product.getPrice() );
            productDto.setDescription( product.getDescription() );
            model.addAttribute("productDto", productDto);
        }
        catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }
        return "products/edit-product";
    }
    @PostMapping("/edit")
    public String updateProduct(Model model,
                                @RequestParam int id,
                                @Valid @ModelAttribute
                                ProductDto productDto,
                                BindingResult result) {
        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            if(result.hasErrors()){
                return "products/edit-product";
            }
            if(!productDto.getImageFile() .isEmpty()){
                //delete old image
                String uploadDir = "src/main/resources/static/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
                try {
                    Files.delete(oldImagePath);
                }
                catch (Exception ex){
                    System.out.println("Exception: " + ex.getMessage());
                }
                //save new image file
                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir +storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImageFileName(storageFileName);
            }
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice( productDto.getPrice() );
            product.setDescription( productDto.getDescription() );

            repo.save(product);
        }
        catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/products";

    }
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {

        try {
            Product product = repo.findById(id).get();
            //delete product image
            Path imagePath= Paths.get("src/main/resources/static/images/" + product.getImageFileName());
            try {
                Files.delete(imagePath);
            }
            catch (Exception ex){
                System.out.println("Exception: " + ex.getMessage());
            }
            //delete the product\
            repo.delete(product);
        }
        catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/products";
    }

}
