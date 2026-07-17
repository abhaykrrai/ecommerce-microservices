package com.ecom.orderservice.feign;

import com.ecom.orderservice.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping("/product/{id}")
    public ProductResponseDto getProductById(
            @PathVariable Long id);

    @PutMapping("/product/reduce-stock/{productId}")
void reduceStock(@PathVariable Long productId,@RequestParam Integer quantity);


    @PutMapping("/product/reduce/{productId}/{quantity}")
    void restoreStock(@PathVariable Long productId,@PathVariable Integer quantity);
}
