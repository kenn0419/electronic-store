package com.kennn.electronicstore.domain.dto;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ProductDTO {
    @Size(min = 1, message = "Tối thiểu một ký tự")
    private String title;

    @Min(value = 1, message = "Giá trị tối thiểu phải hơn 1")
    private double price;

    @Size(min = 10, message = "Tối thiểu mười ký tự")
    private String description;

    @NotEmpty(message = "Không được để trống")
    private String manufactor;

    private MultipartFile thumbnail;

    @Min(value = 0, message = "Tối thiểu là 0")
    private int sold;

    @Min(value = 1, message = "Tối thiểu 1 sản phẩm")
    private int stock;

    private boolean status;

    private long categoryId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(MultipartFile thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getManufactor() {
        return manufactor;
    }

    public void setManufactor(String manufactor) {
        this.manufactor = manufactor;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

}
