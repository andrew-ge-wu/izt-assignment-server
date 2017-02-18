package com.izettle.assignment.service;

import com.izettle.assignment.model.Event;

import java.util.List;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public interface DataStorage {
    boolean addEvent(String user, Event.EventType eventType);

    boolean addEvent(String user, Event.EventType eventType, String data);

    List<Event> readEvent(String userName, Event.EventType eventType, int size);
}
