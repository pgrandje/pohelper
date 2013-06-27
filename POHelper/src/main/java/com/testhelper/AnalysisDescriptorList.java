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
public class AnalysisDescriptorList implements Iterable<AnalysisDescriptor> {

    private List<AnalysisDescriptor> analysisDescriptors;


    AnalysisDescriptorList() {
        // There could be quite a few of these.  One for every node in the page source that we want our page object
        //      to know about.
        analysisDescriptors = new ArrayList<AnalysisDescriptor>(100);
    }


    void add(AnalysisDescriptor descriptor) {
        analysisDescriptors.add(descriptor);
    }


    int getNumberOfBuckets() {
        return analysisDescriptors.size();
    }


    public Iterator<AnalysisDescriptor> iterator() {
        Iterator<AnalysisDescriptor> iterator = analysisDescriptors.iterator();
        return iterator;
    }


}
