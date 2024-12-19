package com.examatlas.models;

import com.examatlas.models.extraModels.BookImageModels;

import java.util.ArrayList;

public class AdminShowAllLiveCoursesModel {
    String courseID,title,subTitle,description,imageURL,teacherName,tags,categoryId,subCategoryId,subjectId,price,finalPrice,courseContent,startDate,endDate;
    ArrayList<BookImageModels> studentArrayList,liveClassArrayList;

    public AdminShowAllLiveCoursesModel(String courseID, String title, String subTitle, String description, String imageURL, String teacherName, String tags, String categoryId, String subCategoryId, String subjectId, String price, String finalPrice, String courseContent, String startDate, String endDate, ArrayList<BookImageModels> studentArrayList, ArrayList<BookImageModels> liveClassArrayList) {
        this.courseID = courseID;
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.teacherName = teacherName;
        this.tags = tags;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.subjectId = subjectId;
        this.price = price;
        this.finalPrice = finalPrice;
        this.courseContent = courseContent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.studentArrayList = studentArrayList;
        this.imageURL = imageURL;
        this.liveClassArrayList = liveClassArrayList;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getCourseContent() {
        return courseContent;
    }

    public void setCourseContent(String courseContent) {
        this.courseContent = courseContent;
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

    public ArrayList<BookImageModels> getStudentArrayList() {
        return studentArrayList;
    }

    public void setStudentArrayList(ArrayList<BookImageModels> studentArrayList) {
        this.studentArrayList = studentArrayList;
    }

    public ArrayList<BookImageModels> getLiveClassArrayList() {
        return liveClassArrayList;
    }

    public void setLiveClassArrayList(ArrayList<BookImageModels> liveClassArrayList) {
        this.liveClassArrayList = liveClassArrayList;
    }
}
