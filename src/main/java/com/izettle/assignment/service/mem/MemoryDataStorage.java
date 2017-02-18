package com.izettle.assignment.service.mem;

import com.izettle.assignment.model.Event;
import com.izettle.assignment.service.DataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public class MemoryDataStorage implements DataStorage {
    private final Map<String, List<Event>> eventStorage = new ConcurrentHashMap<>();

    @Override
    public boolean addEvent(String user, Event.EventType eventType) {
        if (!eventStorage.containsKey(user)) {
            eventStorage.put(user, new ArrayList<>());
        }
        return eventStorage.get(user).add(new Event(eventType));
    }

    @Override
    public boolean addEvent(String user, Event.EventType eventType, String data) {

        if (!eventStorage.containsKey(user)) {
            eventStorage.put(user, new ArrayList<>());
        }
        return eventStorage.get(user).add(new Event(eventType, data));
    }

    @Override
    public List<Event> readEvent(String userName, Event.EventType eventType, int size) {
        if (eventStorage.containsKey(userName)) {
            Predicate<Event> predicate = null;
            if (eventType != null) predicate = event -> event.getType() == eventType;
            return applyFilter(eventStorage.get(userName).stream(), predicate)
                    .sorted()
                    .limit(size)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private Stream<Event> applyFilter(Stream<Event> eventStream, Predicate<Event> predicate) {
        if (predicate != null) {
            return eventStream.filter(predicate);
        }
        return eventStream;
    }
}
