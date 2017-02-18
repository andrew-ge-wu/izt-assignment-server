package com.izettle.assignment.model;

import java.util.Date;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public class Event implements Comparable<Event> {


    public enum EventType {AccountCreated, LoginSuccess, LoginFailed, Logout, AccessResource, RenewTokenFailed, RenewToken}

    private final EventType type;
    private final Date date;
    private String data;

    public Event(EventType type) {
        this.type = type;
        date = new Date();
    }

    public Event(EventType eventType, String data) {
        this(eventType);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int compareTo(Event o) {
        return o.date.compareTo(date);
    }

    public EventType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }
}
