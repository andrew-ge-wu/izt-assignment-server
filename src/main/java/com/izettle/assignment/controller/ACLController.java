package com.izettle.assignment.controller;

import com.izettle.assignment.exception.AuthenticationException;
import com.izettle.assignment.exception.InternalException;
import com.izettle.assignment.model.Event;
import com.izettle.assignment.model.Message;
import com.izettle.assignment.model.Token;
import com.izettle.assignment.service.DataStorage;
import com.izettle.assignment.service.UserStorage;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
@RestController
public class ACLController {
    private final UserStorage userStorage;
    private final DataStorage dataStorage;

    @Autowired
    public ACLController(UserStorage userStorage, DataStorage dataStorage) {
        this.userStorage = userStorage;
        this.dataStorage = dataStorage;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> register(
            @RequestParam("user") String user,
            @RequestParam("password") String password) {
        if (userStorage.createUser(user, password)) {
            dataStorage.addEvent(user, Event.EventType.AccountCreated);
            return new ResponseEntity<>(new Message(HttpStatus.CREATED.value(), "Account created!"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new Message(HttpStatus.BAD_REQUEST.value(), "Account creation failed!"), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(
            @RequestParam("user") String user,
            @RequestParam("password") String password) {
        try {
            Token token = userStorage.userLogin(user, password);
            dataStorage.addEvent(user, Event.EventType.LoginSuccess);
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            dataStorage.addEvent(user, Event.EventType.LoginFailed);
            return new ResponseEntity<>(new Message(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (InternalException e) {
            return new ResponseEntity<>(new Message(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Error!"), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @RequestMapping(value = "/renewtoken", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity renew(
            @RequestParam("user") String user,
            @RequestParam("token") String tokenString) {
        try {
            Token token = userStorage.renewToken(user, tokenString);
            dataStorage.addEvent(user, Event.EventType.RenewToken);
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            dataStorage.addEvent(user, Event.EventType.RenewTokenFailed);
            return new ResponseEntity<>(new Message(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (InternalException e) {
            return new ResponseEntity<>(new Message(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/checkToken", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity checkToken(
            @RequestParam("token") String tokenString) {
        Token token = userStorage.getToken(tokenString);
        if (token == null) {
            return new ResponseEntity<>(new Message(HttpStatus.NOT_FOUND.value(), "Token not found or no longer exist."), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(token);

    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> logout(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "user", required = false) String user) {
        Pair<Boolean, String> result = userStorage.userLogout(token, user);
        if (result.getKey()) {
            dataStorage.addEvent(result.getValue(), Event.EventType.Logout);
            return new ResponseEntity<>(new Message(HttpStatus.OK.value(), "Successfully logged out!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Message(HttpStatus.BAD_REQUEST.value(), "Logout failed!"), HttpStatus.BAD_REQUEST);
    }
}
