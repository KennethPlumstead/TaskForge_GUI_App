
package com.kenneth.taskforge.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task {
    private String title;
    private LocalDate due;
    private int priority; // 1=High 2=Med 3=Low
    private Double hours;
    private String tags;

    public Task(String title, LocalDate due, int priority, Double hours, String tags) {
        this.title = title;
        this.due = due;
        this.priority = priority;
        this.hours = hours;
        this.tags = tags;
    }

    public String getTitle(){ return title; }
    public void setTitle(String v){ title = v; }
    public LocalDate getDue(){ return due; }
    public void setDue(LocalDate v){ due = v; }
    public int getPriority(){ return priority; }
    public void setPriority(int v){ priority = v; }
    public Double getHours(){ return hours; }
    public void setHours(Double v){ hours = v; }
    public String getTags(){ return tags; }
    public void setTags(String v){ tags = v; }

    public String dueString(){
        return due == null ? "" : due.format(DateTimeFormatter.ISO_DATE);
    }
}
