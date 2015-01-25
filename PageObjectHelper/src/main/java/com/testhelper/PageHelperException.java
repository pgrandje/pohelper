package com.testhelper;

/**
 * Identifies error conditions specific to the Page Helper API, specifically the generator and configurator.
 * Provides meaningful string error messages to the method receiving the exception.
 * @author Paul Grandjean
 * @since 5/5/12
 * @version 1.0alpha
 */
public class PageHelperException extends RuntimeException {

    public PageHelperException(String message) {
        super(message);
    }

}
