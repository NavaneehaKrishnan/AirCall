package com.example.sipappmerge.Utils;

/**
 * Created by sachin.maske on 26-06-2017.
 */

public class ExceptionHandler extends Exception {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return super.fillInStackTrace();
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
