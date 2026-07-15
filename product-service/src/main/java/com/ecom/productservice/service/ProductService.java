package com.ecom.productservice.service;

import com.ecom.productservice.dto.ProductRequestDto;
import com.ecom.productservice.dto.ProductResponseDto;
import com.ecom.productservice.entity.Product;
import com.ecom.productservice.exception.ProductNotFoundException;
import com.ecom.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public String addProduct(ProductRequestDto productRequestDto, Long adminId) {

        Optional<Product> existingProduct =
                productRepository.findByNameAndAdminId(productRequestDto.getName(), adminId);

        if (existingProduct.isPresent()) {

            Product product = existingProduct.get();

            long totalQuantity = product.getQuantity() + productRequestDto.getQuantity();

            log.info("Updating product quantity to {}", totalQuantity);

            product.setQuantity(totalQuantity);

            productRepository.save(product);

            return "Product quantity updated successfully";
        }

        Product product = dtoToEntity(productRequestDto);

        product.setAdminId(adminId);

        productRepository.save(product);

        return "Product added successfully";
    }

    @Transactional
    public String deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id : " + id));

        productRepository.delete(product);

        return "Product deleted successfully";
    }

    public Page<Product> getAllProducts(Pageable pageable) {

        return productRepository.findAll(pageable);
    }

    public List<Product> searchProduct(String keyword) {

        return productRepository.findProductByCategory(keyword);
    }

    public ProductResponseDto getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id : " + id));

        return entityToResponse(product);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id,
                                            ProductRequestDto productRequestDto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id : " + id));

        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setCategory(productRequestDto.getCategory());
        product.setPrice(productRequestDto.getPrice());
        product.setQuantity(productRequestDto.getQuantity());

        productRepository.save(product);

        return entityToResponse(product);
    }

    private ProductResponseDto entityToResponse(Product product) {

        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setId(product.getId());
        responseDto.setName(product.getName());
        responseDto.setDescription(product.getDescription());
        responseDto.setCategory(product.getCategory());
        responseDto.setPrice(product.getPrice());
        responseDto.setQuantity(product.getQuantity());

        return responseDto;
    }

    private Product dtoToEntity(ProductRequestDto productRequestDto) {

        Product product = new Product();

        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setCategory(productRequestDto.getCategory());
        product.setPrice(productRequestDto.getPrice());
        product.setQuantity(productRequestDto.getQuantity());

        return product;
    }

    @Transactional
    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setQuantity(product.getQuantity() - quantity);

        productRepository.save(product);
    }
}