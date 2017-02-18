package com.izettle.assignment.exception;

import java.security.NoSuchAlgorithmException;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public class InternalException extends Exception {
    public InternalException(NoSuchAlgorithmException message) {
        super(message);
    }
}
