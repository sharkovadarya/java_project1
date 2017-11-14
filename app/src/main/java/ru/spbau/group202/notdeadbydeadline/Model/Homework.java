package ru.spbau.group202.notdeadbydeadline.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Homework {
    private Deadline deadline;
    private String subject, description, howToSend;
    private boolean isRegular;
    private int expectedScore, actualScore = 0;

    public Homework(int year, int month, int day,
                    int hour, int minute, String subject,
                    boolean isRegular, String description,
                    String howToSend, int expectedScore) {
        deadline = new Deadline(LocalDateTime.of(year, month, day, hour, minute));
        this.subject = subject;
        this.isRegular = isRegular;
        this.description = description;
        this.howToSend = howToSend;
        this.expectedScore = expectedScore;
    }

    public void setActualScore(int score) {
        actualScore = score;
    }

    public boolean isAccepted() {
        return actualScore >= expectedScore;
    }

    public void checkDeadline() {
        /*if (deadline.hasPassed()) {
            // TODO move to old h/ws
        }*/
    }

    public String getFormattedDeadline() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm");
        return deadline.deadline.format(formatter);
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRegular() {
        return isRegular;
    }

    public String getHowToSend() {
        return howToSend;
    }

    public int getExpectedScore() {
        return expectedScore;
    }

    public int getActualScore() {
        return actualScore;
    }

    public int getYear() {
        return deadline.deadline.getYear();
    }

    public int getMonth() {
        return deadline.deadline.getMonthValue();
    }

    public int getDay() {
        return deadline.deadline.getDayOfMonth();
    }

    public int getHour() {
        return deadline.deadline.getHour();
    }

    public int getMinute() {
        return deadline.deadline.getMinute();
    }

    public boolean hasPassed() {
        return deadline.hasPassed();
    }

    private class Deadline {
        private LocalDateTime deadline;

        public Deadline(LocalDateTime deadline) {
            this.deadline = deadline;
        }

        public boolean hasPassed() {
            return LocalDateTime.now().compareTo(deadline) > 0;
        }

    }
}
