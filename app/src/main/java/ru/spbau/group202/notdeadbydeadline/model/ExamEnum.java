package ru.spbau.group202.notdeadbydeadline.model;


public enum ExamEnum {
    TEST() {
        public String getDescription() {
            return "Test";
        }
    },
    FINAL_EXAM() {
        public String getDescription() {
            return "Exam";
        }
    };

    public abstract String getDescription();
}
