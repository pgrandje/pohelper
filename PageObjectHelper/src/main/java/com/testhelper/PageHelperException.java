package com.testhelper;

/*
 * Identifies error conditions specific to the Page Helper API, specifically the generator and configurator.
 * Provides meaningful string error messages to the method receiving the exception.
 * User: pgrandje
 * Date: 5/5/12
 */
public class PageHelperException extends RuntimeException {

    public PageHelperException(String message) {
        super(message);
    }

}
