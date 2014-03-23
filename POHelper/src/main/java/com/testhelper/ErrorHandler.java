package com.testhelper;

/**
 * Contains all string error messages for various error conditions throughout the app.
 * Used primarily after known exceptions occur.  For example, if an expected file or directory
 * are not found.
 * User: pgrandje
 * Date: 3/23/14
 */
public class ErrorHandler {

    public static final String urlParamRequired = "-url command-line parameter required.  Ex: -url http://www.mysite.com";

    // The app can write either sourcecode, or a hints file, so we just say "write its output" in this message.
    public static final String destParamRequired =
            "-dest command-line parameter required for specifying the folder where the app will write its output.";



}
