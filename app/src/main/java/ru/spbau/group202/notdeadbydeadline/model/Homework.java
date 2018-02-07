package ru.spbau.group202.notdeadbydeadline.model;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.*;

public class Homework extends DetailedTimedEntry {
    private Deadline deadline;
    private String subject, description, howToSend;
    private int regularity, deferral = 0;
    private double expectedScore, actualScore = -1;
    private int id;
    private ArrayList<String> materials;

    public Homework(LocalDateTime deadline, @NotNull String subject, int regularity, String description,
                    String howToSend, double expectedScore, int id, @NotNull ArrayList<String> materials) {
        this.deadline = new Deadline(deadline);
        this.subject = subject;
        this.regularity = regularity;
        this.description = description;
        this.howToSend = howToSend;
        this.expectedScore = expectedScore;
        this.id = id;
        this.materials = materials;
    }

    public void setActualScore(double score) {
        actualScore = score;
    }

    public void setDeferral(int deferral) {
        deadline.deadline.plusDays(deferral);
        assignDeferral(deferral);
    }

    public void assignDeferral(int deferral) {
        this.deferral = deferral;
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
        if (getExpectedScore() == -1.0) {
            homeworkDetails.add("Not specified");
        } else {
            homeworkDetails.add(Double.toString(getExpectedScore()));
        }
        homeworkDetails.addAll(materials);
        homeworkDetails.add(Integer.toString(id));

        return homeworkDetails;
    }

    @NotNull
    @Override
    protected LocalTime getTime() {
        return deadline.deadline.toLocalTime();
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

    public int getDeferral() {
        return deferral;
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

    public ArrayList<String> getMaterials() {
        return materials;
    }

    @NotNull
    public Homework generateNewHomeworkById(int id) {
        LocalDateTime newDeadline = deadline.deadline.minusDays(deferral);
        deadline.deadline.plusWeeks(regularity);
        return new Homework(newDeadline, subject, regularity, " ", howToSend,
                -1, id, new ArrayList<>());
    }

    public class Deadline extends DetailedTimedEntry {
        private LocalDateTime deadline;

        private Deadline(@NotNull LocalDateTime deadline) {
            this.deadline = deadline;
        }

        private boolean hasPassed() {
            return LocalDateTime.now().compareTo(deadline) > 0;
        }

        @NotNull
        private String getFormattedDeadline() {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
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

        @NotNull
        @Override
        protected LocalTime getTime() {
            return deadline.toLocalTime();
        }

    }
}