package com.javacook.parfehh.util.exception;

import java.io.IOException;

/**
 * Created by vollmer on 29.11.16.
 */
public class IORuntimeException extends RuntimeException {

    public IORuntimeException(IOException e) {
        super(e);
    }
}
