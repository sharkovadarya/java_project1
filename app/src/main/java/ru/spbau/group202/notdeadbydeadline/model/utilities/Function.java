package ru.spbau.group202.notdeadbydeadline.model.utilities;


public interface Function<T, U> {
    U apply(T argument);
}
