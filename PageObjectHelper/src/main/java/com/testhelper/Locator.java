package com.testhelper;

/**
 * TODO: Add javadoc here.
 * User: pgrandje
 * Date: 4/20/14
 */
public class Locator {

    public enum LocatorType {ID, NAME, CLASS, CSS, OTHER_ATTRIBUTE};

    private LocatorType type;
    private String typeStringName;
    private String value;

    public Locator(LocatorType type, String value) {
        this.type = type;
        this.value = value;
        setTypeStringName(type);
    }

    public LocatorType getType() {
        return type;
    }

    public void setType(LocatorType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTypeStringName(LocatorType type) {

        if (type == LocatorType.ID) {
            typeStringName = "id";
        }
        else if (type == LocatorType.NAME) {
            typeStringName = "name";
        }
        else if (type == LocatorType.CLASS) {
            typeStringName = "class";
        }
        else if (type == LocatorType.CSS) {
            typeStringName = "css";
        }
        else {
            typeStringName = null;
        }
    }

    public String getTypeStringName() {
        return typeStringName;
    }
}
