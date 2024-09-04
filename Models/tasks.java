package com.HkCodes.Todolist.Models;

public class tasks {
    public tasks(String taskname, String taskdescription, String category, String date, String time) {
        this.taskname = taskname;
        this.taskdescription = taskdescription;
        this.category = category;
        this.date = date;
        this.time = time;
    }
    public tasks(String taskname, String taskdescription, String category, String time) {
        this.taskname = taskname;
        this.taskdescription = taskdescription;
        this.category = category;
        this.time = time;
    }

    public String taskname;
    public String taskdescription;
    public String category;

    public tasks() {
    }

    public String date;

    public String getRuid() {
        return ruid;
    }

    public void setRuid(String ruid) {
        this.ruid = ruid;
    }

    public  String ruid;

    public tasks(String taskname, String taskdescription, String category, String date, String ruid, String time) {
        this.taskname = taskname;
        this.taskdescription = taskdescription;
        this.category = category;
        this.date = date;
        this.ruid = ruid;
        this.time = time;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getTaskdescription() {
        return taskdescription;
    }

    public void setTaskdescription(String taskdescription) {
        this.taskdescription = taskdescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String time;
}
