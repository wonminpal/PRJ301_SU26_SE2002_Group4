/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author LENOVO
 */
public class ProductVariant {

    private int id;
    private int productId;
    private String sku;
    private String color;
    private String storageCapacity;
    private double price;
    private int stockQuantity;
    private String variantImage;

    public ProductVariant() {
    }

    public ProductVariant(int id, int productId, String sku, String color, String storageCapacity, double price, int stockQuantity, String variantImage) {
        this.id = id;
        this.productId = productId;
        this.sku = sku;
        this.color = color;
        this.storageCapacity = storageCapacity;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.variantImage = variantImage;
    }

    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getColor() {
        return color;
    }

    public String getStorageCapacity() {
        return storageCapacity;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public String getVariantImage() {
        return variantImage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setStorageCapacity(String storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setVariantImage(String variantImage) {
        this.variantImage = variantImage;
    }

    @Override
    public String toString() {
        return color + " - " + storageCapacity + " (Giá: " + price + ")";
    }
}
