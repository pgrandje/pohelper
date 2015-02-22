package com.pagehelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Stores a single link from a page.  Stored in a list of links.  The list of links is used for crawling to other pages
 * linked to from the current page.
 *
 * @author Paul Grandjean
 * @version 1.0alpha
 * @since 2/2/15
 */
public class LinkDescriptor {

    private URL url;
    String text;
    private HashMap<String, String> attributes;

    // Not sure if the TagDescriptor will be needed but it could prove useful to have a link to the code snippets.
    private TagDescriptor tagDescriptor;

    public LinkDescriptor() {
        attributes = new HashMap<String, String>();
    }

    public URL getUrl() {
        return url;
    }

    // Since the URL comes from the <a> tag's href attribute we take it in as a string and convert it.
    public void setUrl(URL url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public TagDescriptor getTagDescriptor() {
        return tagDescriptor;
    }

    public void setTagDescriptor(TagDescriptor tagDescriptor) {
        this.tagDescriptor = tagDescriptor;
    }
}
