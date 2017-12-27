package ru.spbau.group202.notdeadbydeadline.model;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.*;

public class Homework implements DetailedEntry {
    private Deadline deadline;
    private String subject, description, howToSend;
    private int regularity;
    private double expectedScore, actualScore = -1;
    private int id;

    public Homework(LocalDateTime deadline, @NotNull String subject, int regularity,
                    String description, String howToSend, double expectedScore, int id) {
        this.deadline = new Deadline(deadline);
        this.subject = subject;
        this.regularity = regularity;
        this.description = description;
        this.howToSend = howToSend;
        this.expectedScore = expectedScore;
        this.id = id;
    }

    public void setActualScore(double score) {
        actualScore = score;
    }

    public boolean isAccepted() {
        return actualScore >= expectedScore;
    }

    @NotNull
    public ArrayList<String> getDetails() {
        ArrayList<String> homeworkDetails = new ArrayList<>();
        homeworkDetails.add(getDescription());
        homeworkDetails.add(deadline.getFormattedDeadline());
        homeworkDetails.add(getHowToSend());
        homeworkDetails.add(Double.toString(getExpectedScore()));
        homeworkDetails.add(Integer.toString(id));

        return homeworkDetails;
    }

    public Deadline getDeadline() {
        return deadline;
    }

    @NotNull
    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public int getRegularity() {
        return regularity;
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
        return deadline.deadline.getMonthOfYear();
    }

    public int getDay() {
        return deadline.deadline.getDayOfMonth();
    }

    public int getHour() {
        return deadline.deadline.getHourOfDay();
    }

    public int getMinute() {
        return deadline.deadline.getMinuteOfHour();
    }

    public boolean hasPassed() {
        return deadline.hasPassed();
    }

    public boolean isBetween(int year1, int month1, int day1,
                             int year2, int month2, int day2) {
        return (new LocalDate(year1, month1, day1)).isAfter(deadline.deadline.toLocalDate()) &&
                (new LocalDate(year2, month2, day2)).isBefore(deadline.deadline.toLocalDate());
    }

    public int getId() {
        return id;
    }

    @NotNull
    public Homework generateNewHomeworkById(int id) {
        LocalDateTime newDeadline = deadline.deadline.plusWeeks(regularity);
        return new Homework(newDeadline, subject, regularity, " ", howToSend,
                -1, id);
    }

    public class Deadline implements DetailedEntry, Comparable<Deadline> {
        private LocalDateTime deadline;

        private Deadline(@NotNull LocalDateTime deadline) {
            this.deadline = deadline;
        }

        private boolean hasPassed() {
            return LocalDateTime.now().compareTo(deadline) > 0;
        }

        @NotNull
        private String getFormattedDeadline() {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy hh:mm");
            return formatter.print(deadline);
        }

        @NotNull
        public ArrayList<String> getDetails() {
            ArrayList<String> deadlineDetails = new ArrayList<>();
            deadlineDetails.add(getSubject());
            deadlineDetails.add(getDescription());
            deadlineDetails.add(getFormattedDeadline());

            return deadlineDetails;
        }

        @Override
        public int compareTo(@NotNull Deadline deadline) {
            return this.deadline.compareTo(deadline.deadline);
        }
    }
}