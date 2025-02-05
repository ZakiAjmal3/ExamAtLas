package com.examatlas.models.Books;

import java.util.List;
import java.util.Map;

public class CartItemModel {

    private String cartId;
    private String productId;
    private String title;
    private String author;
    private String price;
    private String sellingPrice;
    private String stock;
    private String description;
    private String publication;
    private String categoryId;
    private String sku;
    private String quantity;
    private String type;
    private String imageUrl;

    public CartItemModel(String cartId,Map<String, Object> productData,String quantity) {
        if (productData != null) {
            this.cartId = cartId;
            this.quantity = quantity;
            this.productId = (String) productData.get("_id");
            this.title = (String) productData.get("title");
            this.author = (String) productData.get("author");
            // Handle price and sellingPrice
            Object priceObj = productData.get("price");
            if (priceObj instanceof Double) {
                this.price = String.valueOf(priceObj);  // Convert the Double to a String
            } else if (priceObj instanceof String) {
                this.price = (String) priceObj;  // If it's already a String, cast it directly
            }

            Object sellingPriceObj = productData.get("sellingPrice");
            if (sellingPriceObj instanceof Double) {
                this.sellingPrice = String.valueOf(sellingPriceObj);  // Convert the Double to a String
            } else if (sellingPriceObj instanceof String) {
                this.sellingPrice = (String) sellingPriceObj;  // If it's already a String, cast it directly
            }
            Object stockObj = productData.get("stock");
            if (stockObj instanceof Double) {
                this.stock = String.valueOf(stockObj);  // Convert the Double to a String
            } else if (stockObj instanceof String) {
                this.stock = (String) stockObj;  // If it's already a String, cast it directly
            }
            this.description = (String) productData.get("description");
            this.publication = (String) productData.get("publication");
            this.categoryId = (String) productData.get("categoryId");
            this.sku = (String) productData.get("sku");
            // Set imageUrl to a default value (empty string or placeholder) if not found
            this.imageUrl = "";  // Default to an empty string

            // Check if images are available, get the first image URL
            if (productData.containsKey("images")) {
                Object images = productData.get("images");
                if (images instanceof List) {
                    List<?> imageList = (List<?>) images;
                    if (!imageList.isEmpty()) {
                        Map<String, Object> imageMap = (Map<String, Object>) imageList.get(0);
                        // If the 'url' is present, use it; otherwise, retain the default empty string
                        this.imageUrl = (String) imageMap.get("url");
                    }
                }
            }
        }
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    // Getters and Setters for each field
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
