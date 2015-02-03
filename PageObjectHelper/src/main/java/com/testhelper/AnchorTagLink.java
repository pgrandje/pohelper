package com.testhelper;

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
public class AnchorTagLink {

    private URL url;
    private HashMap<String, String> attributes;

    // Not sure if the TagDescriptor will be needed but it could prove useful to have a link to the code snippets.
    private TagDescriptor tagDescriptor;

    public AnchorTagLink() {
        attributes = new HashMap<String, String>();
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(String hrefString) {
        try {
            this.url = new URL(hrefString);
        } catch (MalformedURLException e) {
            throw new PageHelperException("Invalid URL from href string: Exception: " + e.getMessage() + "from href=" + hrefString);
        }

    }

    public TagDescriptor getTagDescriptor() {
        return tagDescriptor;
    }

    public void setTagDescriptor(TagDescriptor tagDescriptor) {
        this.tagDescriptor = tagDescriptor;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}
