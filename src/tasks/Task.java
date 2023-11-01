package tasks;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Task implements Comparable<Task>{

    private LocalTime duration;
    private LocalDateTime startTime;
    private String name;
    private String description;

    private String status;
    private String action;

    private String type;

    private Epic epic;
    private int id;

    Task() {

    }

    Task(String name, String description, String action, int hours, int minutes) {
        this.name = name;
        this.description = description;
        this.action = action;
        duration = LocalTime.of(hours, minutes);
        LocalDateTime time = LocalDateTime.now();
        startTime = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), time.getHour(),
                time.getMinute());

    }

    public Task(String name, String description, String action, LocalDateTime startTime, int hours, int minutes) {
        this.name = name;
        this.description = description;
        this.action = action;
        duration = LocalTime.of(hours, minutes);
        setStartTime(startTime);
    }

    public LocalDateTime getEndTime() {
        return startTime.plusHours(duration.getHour()).plusMinutes(duration.getMinute());
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public int compareTo(Task o) {
        if (o.getStartTime() == null) {
            return -1;
        }
        if (this.startTime.isAfter(o.getStartTime())) {
            return 1;
        } else if (this.startTime.isBefore(o.getStartTime())) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "ID: " + id + " name " + name + " type: " + type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}


