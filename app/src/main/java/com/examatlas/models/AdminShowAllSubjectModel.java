package com.examatlas.models;

public class AdminShowAllSubjectModel {
    String title,id,isActive;

    public AdminShowAllSubjectModel(String id,String title,String isActive) {
        this.id = id;
        this.title = title;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
