package ru.spbau.group202.notdeadbydeadline.Model;

import java.time.LocalDateTime;

public class Deadline {
    private LocalDateTime deadline;

    public Deadline(String year, String month, String day,
                    String hour, String minute) {
        deadline = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month),
                Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute));
    }

    public LocalDateTime getDeadlineDate() {
        return deadline;
    }

    public boolean hasPassed() {
        return LocalDateTime.now().compareTo(deadline) > 0;
    }
    
    public static class Controller {
    }
}
