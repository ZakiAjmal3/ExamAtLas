package com.examatlas.models.Books;

public class CategoryModel {
    String imgURL,categoryName;

    public CategoryModel(String imgURL, String categoryName) {
        this.imgURL = imgURL;
        this.categoryName = categoryName;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
