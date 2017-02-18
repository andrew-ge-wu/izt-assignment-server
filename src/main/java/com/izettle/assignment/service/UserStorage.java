package com.izettle.assignment.service;


import com.izettle.assignment.exception.AuthenticationException;
import com.izettle.assignment.exception.InternalException;
import com.izettle.assignment.model.Token;
import com.izettle.assignment.model.User;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public interface UserStorage {

    boolean createUser(String userName, String Password);

    Token userLogin(String userName, String Password) throws AuthenticationException, InternalException;

    Token renewToken(String userName, String token) throws AuthenticationException, InternalException;

    Pair<Boolean,String> userLogout(String token, String user);

    Token getToken(String token);

    User getUser(String userName);
}
