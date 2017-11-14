package ru.spbau.group202.notdeadbydeadline.Model;

import java.time.LocalDateTime;

public class Deadline {
    private LocalDateTime deadline;

    public Deadline(LocalDateTime deadline) {
        this.deadline = deadline;
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
