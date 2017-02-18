package com.izettle.assignment.controller;

import com.izettle.assignment.model.Event;
import com.izettle.assignment.model.Message;
import com.izettle.assignment.model.Token;
import com.izettle.assignment.model.User;
import com.izettle.assignment.service.DataStorage;
import com.izettle.assignment.service.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
@RestController
public class ResourceController {
    private final DataStorage dataStorage;
    private final UserStorage userStorage;

    @Autowired
    public ResourceController(DataStorage dataStorage, UserStorage userStorage) {
        this.dataStorage = dataStorage;
        this.userStorage = userStorage;
    }

    @RequestMapping(value = {"/user/{user}/event/{type}", "/user/{user}/event"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity event(
            @PathVariable("user") String userName,
            @PathVariable(value = "type", required = false) Event.EventType eventType,
            @RequestParam("token") String tokenString,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        Token token = userStorage.getToken(tokenString);
        if (token != null) {
            if (!token.isValid())
                return new ResponseEntity<>(new Message(HttpStatus.FORBIDDEN.value(), "Token expired!"), HttpStatus.FORBIDDEN);
            if (!token.getUserName().equals(userName))
                return new ResponseEntity<>(new Message(HttpStatus.FORBIDDEN.value(), "Token user mismatch"), HttpStatus.FORBIDDEN);

            List<Event> toReturn;
            if (eventType != null) {
                dataStorage.addEvent(userName, Event.EventType.AccessResource, "Type filter:" + eventType);
            } else {
                dataStorage.addEvent(userName, Event.EventType.AccessResource, "Type filter:" + Arrays.toString(Event.EventType.values()));
            }
            toReturn = dataStorage.readEvent(userName, eventType, size);
            if (toReturn == null) {
                return new ResponseEntity<>(new Message(HttpStatus.NOT_FOUND.value(), "Event not found."), HttpStatus.NOT_FOUND);
            } else {
                return ResponseEntity.ok(toReturn);
            }
        } else {
            return new ResponseEntity<>(new Message(HttpStatus.FORBIDDEN.value(), "Not authorized to access this resource."), HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value = {"/user/{user}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity user(
            @PathVariable("user") String userName,
            @RequestParam("token") String tokenString) {
        Token token = userStorage.getToken(tokenString);
        if (token != null) {
            if (!token.isValid())
                return new ResponseEntity<>(new Message(HttpStatus.FORBIDDEN.value(), "Token expired!"), HttpStatus.FORBIDDEN);
            if (!token.getUserName().equals(userName))
                return new ResponseEntity<>(new Message(HttpStatus.FORBIDDEN.value(), "Token user mismatch"), HttpStatus.FORBIDDEN);

            User user = userStorage.getUser(userName);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return new ResponseEntity<>(new Message(HttpStatus.INTERNAL_SERVER_ERROR.value(), "User not found?"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(new Message(HttpStatus.FORBIDDEN.value(), "Not authorized to access this resource."), HttpStatus.FORBIDDEN);
        }
    }
}
