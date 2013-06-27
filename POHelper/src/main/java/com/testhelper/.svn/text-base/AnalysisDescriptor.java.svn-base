package com.testhelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pgrandje
 * Date: 9/16/12
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnalysisDescriptor {

    public enum LocatorType {ID, CSS_LOCATOR};

    private String tag;
    private String text;
    private ArrayList<AnalysisAttribute> attributes;
    private LocatorType locatorType;
    private String locatorValue;


    public AnalysisDescriptor() {
        attributes = new ArrayList<AnalysisAttribute>();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<AnalysisAttribute> getAttributes() {
        return this.attributes;
    }

    public void addAttribute(AnalysisAttribute attribute) {
        this.attributes.add(attribute);
    }


    public LocatorType getLocatorType() {
        return locatorType;
    }

    public void setLocatorType(LocatorType locatorType) {
        this.locatorType = locatorType;
    }

    public String getLocatorValue() {
        return locatorValue;
    }

    public void setLocatorValue(String locatorValue) {
        this.locatorValue = locatorValue;
    }

}
