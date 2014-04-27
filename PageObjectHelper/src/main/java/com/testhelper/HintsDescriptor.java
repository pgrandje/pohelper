package com.testhelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Descriptors are small objects that control a single unit of the generation process and serve as a
 * handle to an element to be generated.  The HintsDescriptor stores the information for a single
 * Analysis record corresponding to a specific HTML tag in the DOM which is targeted for an
 * Analysis record to be displayed to the user.
 * User: pgrandje
 * Date: 9/16/12
 */
public class HintsDescriptor {

    // TODO: Move the hints file indicators to they're own class.
    static public final String NEW_TAG_DELIMITER = "<*** UI Element ***>";
    static public final char IGNORE_CHAR = '*';
    static public final String TEXT_MARKER = "Text: ";
    static public final String ATTRIBUTE_MARKER = "Attribute: ";
    static public final String LOCATOR_MARKER = "Locator: ";
    static public final String LOCATOR_TYPE_STRING_ID = "id";
    static public final String LOCATOR_TYPE_STRING_NAME = "name";
    static public final String LOCATOR_TYPE_STRING_CLASS = "class";
    static public final String LOCATOR_TYPE_STRING_CSS = "css";

    private String tag;
    private String text;
    // TODO: Change the Hints attributes to use a HashMap--I don't need a separate HintsAttribute class just for named pairs.
    private ArrayList<HintsAttribute> attributes;


    /* HintsDecriptor gets a locator when the hints are read by the HintsReader.  This Locator is then passed
       to a TagDescriptor to write the locator code.  Note: the TagDescriptor does not store a Locator object like
       HintsDescriptor does. It could, but it doesn't since it hasn't needed it.  However the Hints Descriptor would
       either need to store the hints file's locator string and then later call LocatorFactory, or store the Locator
       object itself, and the the LocatorFactory is called when reading the Hints file..
    */
    private Locator locator;


    public HintsDescriptor() {
        attributes = new ArrayList<HintsAttribute>();
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

    public List<HintsAttribute> getAttributes() {
        return this.attributes;
    }

    public void addAttribute(HintsAttribute attribute) {
        this.attributes.add(attribute);
    }


    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public Locator getLocator() {
        return locator;
    }

}
