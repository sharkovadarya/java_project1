package ru.spbau.group202.notdeadbydeadline.model;


public enum WeekParityEnum {
    ON_EVEN_WEEK() {
        public WeekParityEnum inverse() {
            return ON_ODD_WEEK;
        }
    },
    ON_ODD_WEEK() {
        public WeekParityEnum inverse() {
            return ON_EVEN_WEEK;
        }
    },
    ALWAYS() {
        public WeekParityEnum inverse() {
            return ALWAYS;
        }
    };

    public abstract WeekParityEnum inverse();
}
