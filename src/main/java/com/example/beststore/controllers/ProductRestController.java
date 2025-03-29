package com.example.beststore.controllers;

import com.example.beststore.models.Product;
import com.example.beststore.models.ProductDto;
import com.example.beststore.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {
    private static final Logger logger = LoggerFactory.getLogger(ProductRestController.class);

    @Autowired
    private ProductRepository repo;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    // Lấy danh sách sản phẩm
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(repo.findAll());
    }

    // Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Tạo sản phẩm mới
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> createProduct(@Valid @ModelAttribute ProductDto productDto) {
        if (productDto.getImageFile() == null || productDto.getImageFile().isEmpty()) {
            return ResponseEntity.badRequest().body("The image file is required.");
        }

        String storageFileName = saveImage(productDto.getImageFile());
        if (storageFileName == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving image.");
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAT(new Date());
        product.setImageFileName(storageFileName);

        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(product));
    }

    // Cập nhật sản phẩm
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<?> updateProduct(@PathVariable int id, @Valid @ModelAttribute ProductDto productDto) {
        Optional<Product> productOptional = repo.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        Product product = productOptional.get();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());

        if (productDto.getImageFile() != null && !productDto.getImageFile().isEmpty()) {
            deleteImage(product.getImageFileName());
            String newFileName = saveImage(productDto.getImageFile());
            if (newFileName == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving new image.");
            }
            product.setImageFileName(newFileName);
        }

        return ResponseEntity.ok(repo.save(product));
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        Optional<Product> productOptional = repo.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        Product product = productOptional.get();
        deleteImage(product.getImageFileName());
        repo.delete(product);

        return ResponseEntity.noContent().build();
    }

    // Lưu ảnh
    private String saveImage(MultipartFile imageFile) {
        try {
            String fileName = System.currentTimeMillis() + "_" + Paths.get(imageFile.getOriginalFilename()).getFileName();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }
            return fileName;
        } catch (IOException ex) {
            logger.error("Error saving image: " + ex.getMessage());
            return null;
        }
    }

    // Xóa ảnh
    private void deleteImage(String fileName) {
        if (fileName != null && !fileName.isBlank()) {
            try {
                Path imagePath = Paths.get(UPLOAD_DIR + fileName);
                if (Files.exists(imagePath)) {
                    Files.delete(imagePath);
                }
            } catch (IOException ex) {
                logger.error("Error deleting image: " + ex.getMessage());
            }
        }
    }
}
