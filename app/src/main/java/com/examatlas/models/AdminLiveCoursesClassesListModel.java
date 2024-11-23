package com.examatlas.models;

import java.util.ArrayList;

public class AdminLiveCoursesClassesListModel {
    String classID,courseID,title,meetingID,time,date,addedBy,scheduledTime,status,startedAt,endedAt;
    ArrayList<String> students;

    public AdminLiveCoursesClassesListModel(String classID, String courseID, String title, String meetingID, String time, String date, String addedBy, String scheduledTime, String status, String startedAt, String endedAt, ArrayList<String> students) {
        this.classID = classID;
        this.courseID = courseID;
        this.title = title;
        this.meetingID = meetingID;
        this.time = time;
        this.date = date;
        this.addedBy = addedBy;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.students = students;
    }

    public String getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(String endedAt) {
        this.endedAt = endedAt;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
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

    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public ArrayList<String> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<String> students) {
        this.students = students;
    }
}
