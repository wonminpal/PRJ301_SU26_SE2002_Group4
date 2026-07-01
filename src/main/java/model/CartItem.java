/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class CartItem {
    private int id;
    private int productId;
    private int quantity;
    private String variant;
    private Product product;

    public CartItem() {
    }


    public CartItem(Product product, int quantity, String variant) {
    this.product = product;
    this.quantity = quantity;
    this.variant = variant;
}
    
    public int getId() {
    return id;
}

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public Product getProduct() {
        return product;
    }

    // Getters & Setters
    public void setProduct(Product product) {
        this.product = product;
    }

    public int getProductId() {
        return this.product != null ? this.product.getId() : this.productId;
    }
}