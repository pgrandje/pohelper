package com.testhelper;

/**
 * Simple enumerated type used to identify whether a given Node from the DOM is actually an HTML tag, or an attribute,
 * text, script, or some other DOM element.
 * User: pgrandje
 * Date: 6/10/12
 */
public class NodeType {
    // TODO:  Change NodeType to an enum and move this to which ever class uses it--probably the NodeScanner
    public static final short TAG = 1;
    public static final short TEXT = 2;
    public static final short SCRIPT = 3;
}
