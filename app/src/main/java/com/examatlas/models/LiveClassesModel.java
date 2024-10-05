package com.examatlas.models;

public class LiveClassesModel {
    String classID,title,meetingID,description,teacherName,keyword,tags;

    public LiveClassesModel(String classID, String title, String meetingID, String description, String teacherName, String keyword, String tags) {
        this.classID = classID;
        this.title = title;
        this.meetingID = meetingID;
        this.description = description;
        this.teacherName = teacherName;
        this.keyword = keyword;
        this.tags = tags;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
