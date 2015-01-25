package com.testhelper;

/*
 * Descriptors are small objects that control a single unit of the generation process and serve as a
 * handle to an element to be generated.  The HintsToCodeDescriptor stores the information for a single
 * Analysis record corresponding to a specific HTML tag in the DOM which is targeted for an
 * Analysis record to be displayed to the user.
 * User: pgrandje
 * Date: 9/16/12
 */
public class HintsFileDelimeters {

    static public final String PAGE_MARKER = "Page Name: ";
    static public final String NEW_TAG_DELIMITER = "<*** UI Element ***>";
    static public final char IGNORE_CHAR = '*';
    static public final String TEXT_MARKER = "Text: ";
    static public final String ATTRIBUTE_MARKER = "Attribute: ";
    static public final String LOCATOR_MARKER = "Locator: ";
    static public final String LOCATOR_TYPE_STRING_ID = "id";
    static public final String LOCATOR_TYPE_STRING_NAME = "name";
    static public final String LOCATOR_TYPE_STRING_CLASS = "class";
    static public final String LOCATOR_TYPE_STRING_CSS = "css";

}
