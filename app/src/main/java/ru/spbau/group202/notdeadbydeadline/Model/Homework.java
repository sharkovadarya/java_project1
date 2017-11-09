package ru.spbau.group202.notdeadbydeadline.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Homework {
    private Deadline deadline;
    private String subject;

    public Homework(String year, String month, String day,
                    String hour, String minute, String subject) {
        deadline = new Deadline(year, month, day, hour, minute);
        this.subject = subject;
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

    public void checkDeadline() {
        /*if (deadline.hasPassed()) {
            // TODO move to old h/ws
        }*/
    }
}
