package ru.spbau.group202.notdeadbydeadline.Model;

import java.time.format.DateTimeFormatter;

public class Homework {
    private Deadline deadline;
    private String subject, description, howToSend;
    private boolean isRegular;
    private int expectedScore, actualScore = 0;

    public Homework(String year, String month, String day,
                    String hour, String minute, String subject,
                    boolean isRegular, String description, String howToSend, int expectedScore) {
        deadline = new Deadline(year, month, day, hour, minute);
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

    public Deadline getDeadline() {
        return deadline;
    }

    public String getFormattedDeadline() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm");
        return deadline.getDeadlineDate().format(formatter);
    }

    public String getSubject() {
        return subject;
    }

}
