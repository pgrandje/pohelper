package com.testhelper;

import org.apache.log4j.Logger;

/**
 * Describes a page or page component.  Stores the classname for the page object.
 *
 * User: pgrandje
 * Date: 8/19/12
 */
public class PageDescriptor {

    private final Logger logger = Logger.getLogger(PageDescriptor.class);

    private String pageObjectName;

    PageDescriptor(String pageObjectName) {
        this.pageObjectName = pageObjectName;
    }


    public String getPageObjectName() {
        return pageObjectName;
    }

}
