package com.examatlas.models;

import com.examatlas.models.extraModels.BookImageModels;

import java.util.ArrayList;

public class AdminShowAllLiveCoursesModel {
    String courseID,title,subTitle,description,teacherName,language,price,tags,categoryId,subCategoryId,subjectId,courseContent,isActive,finalPrice,startDate,endDate;
    ArrayList studentArrayList,liveClassArrayList,ratingArrayList;
    ArrayList<BookImageModels> imageArrayList;

    public AdminShowAllLiveCoursesModel(String courseID, String title, String subTitle, String description, String teacherName, String language, String price, String tags, String categoryId, String subCategoryId, String subjectId, String courseContent, String isActive, String finalPrice, String startDate, String endDate, ArrayList studentArrayList, ArrayList liveClassArrayList, ArrayList ratingArrayList, ArrayList<BookImageModels> imageArrayList) {
        this.courseID = courseID;
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.teacherName = teacherName;
        this.language = language;
        this.price = price;
        this.tags = tags;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.subjectId = subjectId;
        this.courseContent = courseContent;
        this.isActive = isActive;
        this.finalPrice = finalPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.studentArrayList = studentArrayList;
        this.liveClassArrayList = liveClassArrayList;
        this.ratingArrayList = ratingArrayList;
        this.imageArrayList = imageArrayList;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getCourseContent() {
        return courseContent;
    }

    public void setCourseContent(String courseContent) {
        this.courseContent = courseContent;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ArrayList getStudentArrayList() {
        return studentArrayList;
    }

    public void setStudentArrayList(ArrayList studentArrayList) {
        this.studentArrayList = studentArrayList;
    }

    public ArrayList getLiveClassArrayList() {
        return liveClassArrayList;
    }

    public void setLiveClassArrayList(ArrayList liveClassArrayList) {
        this.liveClassArrayList = liveClassArrayList;
    }

    public ArrayList getRatingArrayList() {
        return ratingArrayList;
    }

    public void setRatingArrayList(ArrayList ratingArrayList) {
        this.ratingArrayList = ratingArrayList;
    }

    public ArrayList<BookImageModels> getImageArrayList() {
        return imageArrayList;
    }

    public void setImageArrayList(ArrayList<BookImageModels> imageArrayList) {
        this.imageArrayList = imageArrayList;
    }
}
