package com.example.beststore.services

import com.example.beststore.models.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductsRepository : JpaRepository<Product?, Int?>
