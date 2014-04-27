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

    PageDescriptor() {
    }

    PageDescriptor(String pageObjectName) {
        this.pageName = pageObjectName;
    }


    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageObjectName, NameRecorder classNameRecorder, AbstractBucket bucket) {

        // Coming from the hints file, the pagename should already be unique--But we'll redo it to be certain.
        pageName = classNameRecorder.makeSymbolName(pageObjectName);
        bucket.setPageObjectName(pageName);
    }


    public void setPageName(Document pageSource, NameRecorder classNameRecorder, AbstractBucket bucket) {

        // Get all <title> tags--hopefully there's one and only one.
        Element root = pageSource.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("title");

        if (nodeList.getLength() > 1) {
            logger.warn("Found more than one <title> tag, is this valid for a web page?");
            bucket.setPageObjectName(makePageNameFromTitle(nodeList.item(0), classNameRecorder));
        }
        else if (nodeList.getLength() == 1)  {
            logger.info("Found exactly one <title> tag, using it's text for the page object's classname.");
            bucket.setPageObjectName(makePageNameFromTitle(nodeList.item(0), classNameRecorder));
        }
        else if (nodeList.getLength() == 0) {
            logger.warn("<title> tag not found, using a default name for the page object.");
            bucket.setPageObjectName(makePageNameFromTitle(null, classNameRecorder));
        }
        else if (nodeList.getLength() < 0) {
            throw new SeleniumGeneratorException(
                      "Unknown condition--Retrieving <title> tag return a negative NodeList length.  " +
                      "This should never happen."
                     );
        }

    }


    private String makePageNameFromTitle(Node titleNode, NameRecorder classNameRecorder)  {

        String titleText;
        String className;

        // If there was no title node, fetch a default symbolname.  Otherwise, use the title for the symbol name.
        // TODO: A null for a page name will not work--need to create a makeDefaultPageName().
        if (titleNode == null) {
            titleText = null;
        }
        else {
            titleText = titleNode.getTextContent();
            logger.info("Using title tag '" + titleNode.getNodeName() + "' with text \"" + titleText + "\"");
        }

        className = classNameRecorder.makeSymbolName(titleText);
        logger.info("Using symbol name '" + className + "' for the page object class name.");

        // TODO: PageName in PageDescriptor is stored differently than in the simpler case--make these consistent.
        pageName = className;

        return className;

    }


}
