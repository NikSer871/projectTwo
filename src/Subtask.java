import java.time.LocalDateTime;
import java.util.List;

public class Subtask extends Task {


    Subtask(String name, String description, String action, int hours, int minutes) {
        super(name, description, action, hours, minutes);
    }
    Subtask(String name, String description, String action, LocalDateTime startTime, int hours, int minutes) {
        super(name, description, action, startTime, hours, minutes);
    }
}
