package com.ecom.productservice.controller;

import com.ecom.productservice.dto.ProductRequestDto;
import com.ecom.productservice.dto.ProductResponseDto;
import com.ecom.productservice.entity.Product;
import com.ecom.productservice.exception.QuantityLessException;
import com.ecom.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String dir) {

        Sort sort = dir.equalsIgnoreCase("ASC")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @PostMapping
    public ResponseEntity<String> addProduct(
            @Valid @RequestBody ProductRequestDto productRequestDto) {

        if (productRequestDto.getQuantity() < 1) {
            throw new QuantityLessException("Quantity must not be less than 1");
        }

        // Temporary adminId until Auth/Admin service is implemented
        Long adminId = 1L;

        String response = productService.addProduct(productRequestDto, adminId);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {

        String response = productService.deleteProduct(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProduct(
            @RequestParam String keyword) {

        return ResponseEntity.ok(productService.searchProduct(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto productRequestDto) {

        return ResponseEntity.ok(
                productService.updateProduct(id, productRequestDto));
    }

    @PutMapping("/reduce-stock/{productId}")
    public void reduceStock(@PathVariable Long productId,@RequestParam Integer quantity){
        productService.reduceStock(productId,quantity);
    }

    @PutMapping("/reduce/{productId}/{quantity}")
    public void restoreStock(@PathVariable Long productId,@PathVariable Integer quantity){
        productService.restoreStock(productId,quantity);
    }

}