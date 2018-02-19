package ru.spbau.group202.notdeadbydeadline.model.utilities;


public class AsyncTaskResult<T, E extends Exception> {
    private T result;
    private E error;

    public T getResult() {
        return result;
    }

    public E getError() {
        return error;
    }

    public AsyncTaskResult(T result) {
        this.result = result;
    }

    public AsyncTaskResult(E error) {
        this.error = error;
    }
}
