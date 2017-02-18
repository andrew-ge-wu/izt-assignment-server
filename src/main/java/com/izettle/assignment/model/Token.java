package com.izettle.assignment.model;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public class Token {
    private String token;
    private long expireTime;
    private String userName;

    public Token(String token, long expireTime, String userName) {
        this.token = token;
        this.expireTime = expireTime;
        this.userName = userName;
    }

    public boolean isValid() {
        return expireTime > System.currentTimeMillis();
    }

    public String getToken() {
        return token;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public String getUserName() {
        return userName;
    }
}
