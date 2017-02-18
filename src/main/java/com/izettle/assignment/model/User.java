package com.izettle.assignment.model;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public class User {
    private final String userName;
    private final String salt;
    private final String hash;

    public User(String userName, String salt, String hash) {
        this.userName = userName;
        this.salt = salt;
        this.hash = hash;
    }

    public String getUserName() {
        return userName;
    }

    public String getSalt() {
        return salt;
    }

    public String getHash() {
        return hash;
    }
}
