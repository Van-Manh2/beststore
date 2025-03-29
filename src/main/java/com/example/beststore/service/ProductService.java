package com.example.beststore.service;

import com.example.beststore.models.Product;
import com.example.beststore.models.ProductDto;
import com.example.beststore.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public List<Product> createProducts(List<ProductDto> productDtos) {
        if (productDtos == null || productDtos.isEmpty()) {
            throw new IllegalArgumentException("Product list cannot be empty!");
        }

        List<Product> products = productDtos.stream().map(dto -> {
            if (dto.getName() == null || dto.getPrice() == 0.0) {
                throw new IllegalArgumentException("Product name and price are required!");
            }

            Product product = new Product();
            product.setName(dto.getName());
            product.setBrand(dto.getBrand());
            product.setCategory(dto.getCategory());
            product.setPrice(dto.getPrice());
            product.setDescription(dto.getDescription());
            product.setImageFileName(dto.getImageFile().getOriginalFilename());
            return product;
        }).collect(Collectors.toList());

        return productRepository.saveAll(products);
    }
}
