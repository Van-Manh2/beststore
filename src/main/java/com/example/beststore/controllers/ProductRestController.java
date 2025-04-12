package com.example.beststore.controllers;

import com.example.beststore.models.Product;
import com.example.beststore.models.ProductDto;
import com.example.beststore.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {
    private static final Logger logger = LoggerFactory.getLogger(ProductRestController.class);

    @Autowired
    private ProductRepository repo;
    @Autowired
    private ProductRepository productRepository;

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
    @PostMapping
    public ResponseEntity<?> createProducts(@Valid @RequestBody List<ProductDto> productDtos) {
        for (ProductDto productDto : productDtos) {
            Product product = new Product();
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            product.setCreatedAt(new Date());

            productRepository.save(product);
        }
        return ResponseEntity.ok("Products created successfully!");
    }


    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @Valid @RequestBody ProductDto productDto) {
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

        return ResponseEntity.ok(repo.save(product));
    }


    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        Optional<Product> productOptional = repo.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        repo.delete(productOptional.get());
        return ResponseEntity.noContent().build();
    }
}
