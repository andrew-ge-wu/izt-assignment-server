package com.izettle.assignment.model;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public class Message {
    private final int code;
    private final Object message;

    public Message(int code, Object message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public Object getMessage() {
        return message;
    }
}
