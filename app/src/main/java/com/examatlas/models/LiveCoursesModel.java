package com.examatlas.models;

import com.examatlas.models.extraModels.BookImageModels;

import java.util.ArrayList;

public class LiveCoursesModel {
    String courseID,title,description,teacherName,tags,categoryId,subCategoryId,subjectId,startDate,endDate;
    ArrayList studentArrayList,liveClassArrayList;
    ArrayList<BookImageModels> imageArrayList;
    public LiveCoursesModel(String courseID, String title, String description, String teacherName, String tags, String categoryId, String subCategoryId, String subjectId, String startDate, String endDate, ArrayList<BookImageModels> imageArrayList, ArrayList studentArrayList, ArrayList liveClassArrayList) {
        this.courseID = courseID;
        this.title = title;
        this.description = description;
        this.teacherName = teacherName;
        this.tags = tags;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.subjectId = subjectId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageArrayList = imageArrayList;
        this.studentArrayList = studentArrayList;
        this.liveClassArrayList = liveClassArrayList;
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

    public ArrayList getImageArrayList() {
        return imageArrayList;
    }

    public void setImageArrayList(ArrayList imageArrayList) {
        this.imageArrayList = imageArrayList;
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
}
