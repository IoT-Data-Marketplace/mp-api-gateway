package com.itodatamp.mpapigateway.security.exception;

import org.springframework.security.core.AuthenticationException;

public class AlreadyLoggedOutException extends AuthenticationException {
    public AlreadyLoggedOutException(String msg) {
        super(msg);
    }
}
