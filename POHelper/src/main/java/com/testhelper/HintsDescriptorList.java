package com.testhelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * List of HintsDescriptors.  This is the data structure containing hints (analysis) info on all the HTML tags currently
 * being processed.  Each element in the list corresponds to a specific HTML tag in the DOM that has
 * been targeted for displaying an analysis of its HTML info to the user as a potential candidate for code generation.
 * User: pgrandje
 * Date: 6/3/12
 */
public class HintsDescriptorList implements Iterable<HintsDescriptor> {

    private List<HintsDescriptor> analysisDescriptors;


    HintsDescriptorList() {
        // There could be quite a few of these.  One for every node in the page source that we want our page object
        //      to know about.
        analysisDescriptors = new ArrayList<HintsDescriptor>(100);
    }


    void add(HintsDescriptor descriptor) {
        analysisDescriptors.add(descriptor);
    }


    int getNumberOfBuckets() {
        return analysisDescriptors.size();
    }


    public Iterator<HintsDescriptor> iterator() {
        Iterator<HintsDescriptor> iterator = analysisDescriptors.iterator();
        return iterator;
    }


}
