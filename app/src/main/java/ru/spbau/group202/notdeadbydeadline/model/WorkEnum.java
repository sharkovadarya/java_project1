package ru.spbau.group202.notdeadbydeadline.model;


//TODO rename
public enum WorkEnum {
    TEST() {
        public String getDescription() {
            return "Test";
        }
    },
    EXAM() {
        public String getDescription() {
            return "Exam";
        }
    };

    public abstract String getDescription();
}
