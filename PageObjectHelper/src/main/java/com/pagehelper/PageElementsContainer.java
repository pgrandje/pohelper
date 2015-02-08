package com.pagehelper;

/**
 * A simple aggregator class that combines the link and tag descriptor lists so both can be returned by the PageScanner's
 * scanForUIElements() method.
 *
 * @author Paul Grandjean
 * @version 1.0alpha
 * @since 2/7/15
 */
public class PageElementsContainer {

    private LinkDescriptorList linkDescriptorList;
    private TagDescriptorList tagDescriptorList;

    public PageElementsContainer() {
    }

    public LinkDescriptorList getLinkDescriptorList() {
        return linkDescriptorList;
    }

    public TagDescriptorList getTagDescriptorList() {
        return tagDescriptorList;
    }

    public void setLinkDescriptorList(LinkDescriptorList linkDescriptorList) {
        this.linkDescriptorList = linkDescriptorList;
    }

    public void setTagDescriptorList(TagDescriptorList tagDescriptorList) {
        this.tagDescriptorList = tagDescriptorList;
    }
}


