package com.testhelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * List of Tag Descriptors.  This is the data structure containing info on all the HTML tags currently
 * being processed.  Each element in the list corresponds to a specific HTML tag in the DOM that has
 * been targeted for potentially generating code.
 *
 * Note: This class wasn't initially necessary.  I could be removed and replaced with a List<TagDescriptors>.  However,
 * it remains here as a hook to allow extending the code to methods that operate on the entire list of TagDescriptors.
 *
 * @author Paul Grandjean
 * @since 6/3/12
 * @version 1.0alpha
 */
public class LinkDescriptorList implements Iterable<LinkDescriptor> {

    private List<LinkDescriptor> linkDescriptors;

    LinkDescriptorList() {
        // There could be quite a few of these.  One for every node in the page source that we want our page object
        //      to know about.
        linkDescriptors = new ArrayList<LinkDescriptor>(200);
    }

    void add(LinkDescriptor linkDescriptor) {
        linkDescriptors.add(linkDescriptor);
    }

    int size() {
        return linkDescriptors.size();
    }

    public Iterator<LinkDescriptor> iterator() {
        return linkDescriptors.iterator();
    }


}
