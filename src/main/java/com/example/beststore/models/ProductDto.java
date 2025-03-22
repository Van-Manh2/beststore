package com.example.beststore.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;



public class ProductDto {
    @NotEmpty(message = "The name is required ")
    private String name;

    @NotEmpty(message = "The name is required")
    private String brand;

    @NotEmpty(message = "The name is required")
    private String category;

    public @NotEmpty(message = "The name is required ") String getName() {
        return name;
    }

    public void setName(@NotEmpty(message = "The name is required ") String name) {
        this.name = name;
    }

    public @NotEmpty(message = "The name is required") String getBrand() {
        return brand;
    }

    public void setBrand(@NotEmpty(message = "The name is required") String brand) {
        this.brand = brand;
    }

    public @NotEmpty(message = "The name is required") String getCategory() {
        return category;
    }

    public void setCategory(@NotEmpty(message = "The name is required") String category) {
        this.category = category;
    }

    public @Min(0) double getPrice() {
        return price;
    }

    public void setPrice(@Min(0) double price) {
        this.price = price;
    }

    public @Size(min = 10, message = "mô tả tác giải") @Size(max = 2000, message = "không mô tả tác giải") String getDescription() {
        return description;
    }

    public void setDescription(@Size(min = 10, message = "mô tả tác giải") @Size(max = 2000, message = "không mô tả tác giải") String description) {
        this.description = description;
    }

    public MultipartFile getImgeFile() {
        return imageFile;
    }

    public void setImgeFile(MultipartFile imgeFile) {
        this.imageFile = imgeFile;
    }

    @Min(0)
    private double price;

    @Size(min = 10, message = "mô tả tác giải")
    @Size(max = 2000, message = "không mô tả tác giải")
    private String description;
    public MultipartFile imageFile;

}
