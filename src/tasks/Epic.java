package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Subtask> subtasks;

    private LocalDateTime endFullTime;

    Epic(String name, String description, String action) {
        super.name = name;
        super.description = description;
        super.action = action;
        subtasks = new ArrayList<>();
    }


    public LocalDateTime getEndFullTime() {
        return endFullTime;
    }

    public void setEndFullTime(LocalDateTime endTime) {
        this.endFullTime = endTime;
    }
}
