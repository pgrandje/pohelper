package com.pagehelper;

/**
 * Stores everything needed to construct a Selenium locator string.
 * @author Paul Grandjean
 * @since 4/20/14
 * @version 1.0alpha
 */
public class Locator {

    // TODO: Check whether LocatorTypes can be the same as the command-line options for Locator configuration.
    public enum LocatorType {
        ID ("id"),
        NAME ("name"),
        CLASS ("class"),
        CSS ("css");

        private final String typeString;

        private LocatorType(String s) {
            typeString = s;
        }

        private String getTypeStringName(){
           return typeString;
        }

    }

    private LocatorType type;
    private String value;

    public Locator(LocatorType type, String value) {
        if (type == null)
            throw new PageHelperException("Null LocatorType passed to new Locator.");
        this.type = type;
        this.value = value;
    }

    // setType() is not needed, we want to enforce setting the type when a Locator is constructed.

    public LocatorType getType() {
        return type;
    }

    public String getValue() {
        if (value == null)
            throw new PageHelperException("Locator value must not be null when assigning it to code.");
        else if (value.isEmpty())
            throw new PageHelperException("Locator value must not be an empty string when assigning it to code.");
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTypeStringName() {
        return type.getTypeStringName();
    }

}
