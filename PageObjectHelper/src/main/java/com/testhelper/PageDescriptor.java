package com.testhelper;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: pgrandje
 * Date: 8/19/12
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class PageDescriptor {

    private final Logger logger = Logger.getLogger(PageDescriptor.class);

    private String pageName;

    PageDescriptor(String pageObjectName) {
        this.pageName = pageObjectName;
    }


    public String getPageName() {
        return pageName;
    }

}
