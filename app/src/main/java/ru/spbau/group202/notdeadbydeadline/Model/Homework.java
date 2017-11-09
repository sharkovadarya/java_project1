package ru.spbau.group202.notdeadbydeadline.Model;
public class Homework {
    private Deadline deadline;
    private String subject;

    public Homework(String year, String month, String day,
                    String hour, String minute, String subject) {
        deadline = new Deadline(year, month, day, hour, minute);
        this.subject = subject;
    }

    public void checkDeadline() {
        /*if (deadline.hasPassed()) {
            // TODO move to old h/ws
        }*/
    }
}
