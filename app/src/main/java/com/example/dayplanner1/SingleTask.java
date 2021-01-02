package com.example.dayplanner1;

public class SingleTask {
    private String Task;
    private String Date;
    private Boolean enable;

    public SingleTask(String task, String date, Boolean enable) {
        Task = task;
        Date = date;
        this.enable = enable;
    }

    public String getTask() {
        return Task;
    }

    public void setTask(String task) {
        Task = task;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
