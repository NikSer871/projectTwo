import java.security.PrivateKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;

public class Task {

    private LocalTime duration;
    private LocalDateTime startTime;
    String name;
    String description;

    String status;
    String action;

    String type;

    Epic epic;
    int id;

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
}


