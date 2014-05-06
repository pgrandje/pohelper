package com.testhelper;

/**
 * An exception type to identify error conditions specific to the page object helper/generator.
 * User: pgrandje
 * Date: 5/5/12
 */
public class TestHelperException extends RuntimeException {

    TestHelperException(String message) {
        super(message);
    }

}
