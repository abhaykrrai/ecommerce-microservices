package com.ecom.productservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private double price;

    @Min(0)
    private long quantity;

//    @ManyToOne
//    @JoinColumn(name="admin_id")
    private Long admin;

    private String category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public Long getAdmin() {
        return admin;
    }

    public void setAdmin(Long admin) {
        this.admin = admin;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
