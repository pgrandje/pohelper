package com.paulgrandjean.examples.classes;

/**
 * Created by IntelliJ IDEA.
 * User: pgrandje
 * Date: 9/5/13
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExampleClass2 extends ExampleClass1 {

    private String aString;

    public ExampleClass2() {
        aString = "unknown";
    }

    public ExampleClass2(String tempString) {
        aString = tempString;
    }

    public String getTheString() {
        return aString;
    }

}
