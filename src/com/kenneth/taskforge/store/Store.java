
package com.kenneth.taskforge.store;

import com.kenneth.taskforge.model.Plan;
import com.kenneth.taskforge.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Store {
    public static Path baseDir(){
        String home = System.getProperty("user.home");
        return Paths.get(home, "TaskForge");
    }
    public static Path plansDir(){
        Path p = baseDir().resolve("plans");
        if(!Files.exists(p)){ try { Files.createDirectories(p); } catch(Exception ignored){} }
        return p;
    }
    public static Path dataFile(){
        if(!Files.exists(baseDir())){
            try { Files.createDirectories(baseDir()); } catch(Exception ignored){}
        }
        return baseDir().resolve("data.json");
    }

    // Minimal JSON writer/reader (no external libs).
    public static void save(Plan plan) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"weekStart\": ").append(q(plan.getWeekStart()==null? "" : plan.getWeekStart().toString())).append(",\n");
        sb.append("  \"tasks\": [\n");
        List<Task> ts = plan.getTasks();
        for(int i=0;i<ts.size();i++){
            Task t = ts.get(i);
            sb.append("    {")
              .append("\"title\": ").append(q(t.getTitle())).append(", ")
              .append("\"due\": ").append(q(t.dueString())).append(", ")
              .append("\"priority\": ").append(t.getPriority()).append(", ")
              .append("\"hours\": ").append(t.getHours()==null? "null" : t.getHours().toString()).append(", ")
              .append("\"tags\": ").append(q(t.getTags()))
              .append("}").append(i<ts.size()-1? ",":"").append("\n");
        }
        sb.append("  ]\n}\n");

        try (Writer w = new OutputStreamWriter(new FileOutputStream(dataFile().toFile()), StandardCharsets.UTF_8)) {
            w.write(sb.toString());
        }
    }

    public static Plan load() {
        try {
            byte[] b = Files.readAllBytes(dataFile());
            String s = new String(b, StandardCharsets.UTF_8);
            return parse(s);
        } catch (Exception e){
            return new Plan(); // empty if not found or parse error
        }
    }

    // Very small parser for our own format
    private static Plan parse(String text){
        Plan p = new Plan();
        String week = extract(text, "\"weekStart\":", ",");
        if(week==null) week = extract(text, "\"weekStart\":", "\n");
        week = unq(week);
        try{
            if(week != null && !week.trim().isEmpty())
                p.setWeekStart(LocalDate.parse(week.trim()));
        }catch(Exception ignored){}

        List<String> objects = sliceObjects(text);
        List<Task> tasks = new ArrayList<Task>();
        for(String o: objects){
            String title = unq(extract(o, "\"title\":", ","));
            String due = unq(extract(o, "\"due\":", ","));
            String prio = unq(extract(o, "\"priority\":", ","));
            String hours = unq(extract(o, "\"hours\":", ","));
            String tags = unq(extract(o, "\"tags\":", "}"));
            int pri = 2;
            try { pri = Integer.parseInt(prio.trim()); } catch(Exception ignored){}
            Double hrs = null;
            if(hours != null && !"null".equals(hours) && !hours.trim().isEmpty()){
                try { hrs = Double.parseDouble(hours.trim()); } catch(Exception ignored){}
            }
            java.time.LocalDate dueDate = null;
            try { if(due!=null && !due.trim().isEmpty()) dueDate = java.time.LocalDate.parse(due.trim());} catch(Exception ignored){}
            tasks.add(new Task(title==null? "" : title, dueDate, pri, hrs, tags==null? "" : tags));
        }
        p.setTasks(tasks);
        return p;
    }

    private static String q(String s){
        if(s==null) s="";
        return "\"" + s.replace("\"","\\\"") + "\"";
    }
    private static String unq(String s){
        if(s==null) return null;
        String x = s.trim();
        if(x.startsWith("\"")) x = x.substring(1);
        if(x.endsWith("\"")) x = x.substring(0, x.length()-1);
        return x;
    }
    private static String extract(String text, String after, String until){
        if(text==null) return null;
        int i = text.indexOf(after);
        if(i<0) return null;
        i += after.length();
        int j = text.indexOf(until, i);
        if(j<0) j = text.length();
        return text.substring(i, j);
    }
    private static List<String> sliceObjects(String text){
        List<String> res = new ArrayList<String>();
        String key = "\"tasks\":";
        int i = text.indexOf(key);
        if(i<0) return res;
        int s = text.indexOf("[", i);
        int e = text.indexOf("]", s);
        if(s<0 || e<0) return res;
        String arr = text.substring(s+1, e);
        String[] parts = arr.split("\\},\\s*\\{");
        for(int k=0;k<parts.length;k++){
            String o = parts[k].trim();
            if(!o.startsWith("{")) o = "{" + o;
            if(!o.endsWith("}")) o = o + "}";
            if(o.replace("{","").replace("}","").trim().isEmpty()) continue;
            res.add(o);
        }
        return res;
    }
}
