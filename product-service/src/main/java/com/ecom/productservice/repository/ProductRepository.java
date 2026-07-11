package com.ecom.productservice.repository;



import com.ecom.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    void deleteByName(String name);

    Optional<Product> findByName(String name);

    @Query("select p from Product p where p.name like %:keyword%")
    List<Product> findProductByCategory(@Param("keyword") String keyword);

    Optional<Product> findByNameAndAdmin(String name, Long admin);

    List<Product> findByAdmin(Long admin);
}
