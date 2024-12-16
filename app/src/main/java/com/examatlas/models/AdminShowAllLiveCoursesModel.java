package com.examatlas.models;

import com.examatlas.models.extraModels.BookImageModels;

import java.util.ArrayList;

public class AdminShowAllLiveCoursesModel {
    ArrayList<BookImageModels> imageArrayList;
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

    }

    }

    }

    }
}
