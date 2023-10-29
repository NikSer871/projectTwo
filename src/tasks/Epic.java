package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    private LocalDateTime endFullTime;

    Epic(String name, String description, String action) {
        super.setName(name);
        super.setDescription(description);
        super.setAction(action);
        subtasks = new ArrayList<>();
    }


    public LocalDateTime getEndFullTime() {
        return endFullTime;
    }

    public void setEndFullTime(LocalDateTime endTime) {
        this.endFullTime = endTime;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }
}
