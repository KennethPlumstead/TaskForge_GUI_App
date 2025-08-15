
package com.kenneth.taskforge.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Plan {
    private LocalDate weekStart;
    private List<Task> tasks = new ArrayList<Task>();

    public LocalDate getWeekStart(){ return weekStart; }
    public void setWeekStart(LocalDate d){ weekStart = d; }
    public List<Task> getTasks(){ return tasks; }
    public void setTasks(List<Task> t){ tasks = t; }
}
