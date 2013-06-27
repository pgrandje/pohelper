package com.testhelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pgrandje
 * Date: 6/3/12
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagDescriptorList implements Iterable<TagDescriptor> {

    private List<TagDescriptor> tagDescriptors;


    TagDescriptorList() {
        // There could be quite a few of these.  One for every node in the page source that we want our page object
        //      to know about.
        tagDescriptors = new ArrayList<TagDescriptor>(100);
    }


    void add(TagDescriptor descriptor) {
        tagDescriptors.add(descriptor);
    }


    int getNumberOfBuckets() {
        return tagDescriptors.size();
    }


    public Iterator<TagDescriptor> iterator() {
        Iterator<TagDescriptor> tagBucketIterator = tagDescriptors.iterator();
        return tagBucketIterator;
    }


}
