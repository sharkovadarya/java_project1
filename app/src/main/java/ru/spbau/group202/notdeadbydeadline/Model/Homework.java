package ru.spbau.group202.notdeadbydeadline.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Homework implements DetailedEntry{
    private Deadline deadline;
    private String subject, description, howToSend;
    private boolean isRegular;
    private double expectedScore;
    private double actualScore = 0;

    public Homework(int year, int month, int day,
                    int hour, int minute, String subject,
                    boolean isRegular, String description,
                    String howToSend, double expectedScore) {
        deadline = new Deadline(LocalDateTime.of(year, month, day, hour, minute));
        this.subject = subject;
        this.isRegular = isRegular;
        this.description = description;
        this.howToSend = howToSend;
        this.expectedScore = expectedScore;
    }

    public void setActualScore(double score) {
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

    public ArrayList<String> getDetails() {
        ArrayList<String> homeworkDetails = new ArrayList<>();

        homeworkDetails.add(getDescription());
        homeworkDetails.add(deadline.getFormattedDeadline());
        homeworkDetails.add(getHowToSend());
        homeworkDetails.add(Double.toString(getExpectedScore()));

        return homeworkDetails;
    }

    public Deadline getDeadline() {
        return deadline;
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

    public double getExpectedScore() {
        return expectedScore;
    }

    public double getActualScore() {
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

    public boolean isBetween(int year1, int month1, int day1,
                             int year2, int month2, int day2) {
        return LocalDate.of(year1, month1, day1).isAfter(deadline.deadline.toLocalDate()) &&
                LocalDate.of(year2, month2, day2).isBefore(deadline.deadline.toLocalDate());
    }

    public class Deadline implements DetailedEntry{
        private LocalDateTime deadline;

        private Deadline(LocalDateTime deadline) {
            this.deadline = deadline;
        }

        private boolean hasPassed() {
            return LocalDateTime.now().compareTo(deadline) > 0;
        }

        private String getFormattedDeadline() {
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm");
            return deadline.format(pattern);
        }

        public ArrayList<String> getDetails() {
            ArrayList<String> deadlineDetails = new ArrayList<>();
            deadlineDetails.add(getSubject());
            deadlineDetails.add(getFormattedDeadline());

            return deadlineDetails;
        }

    }
}
