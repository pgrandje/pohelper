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

    private Document pageSource;
    private NameRecorder classNameRecorder;


    PageDescriptor(Document document, NameRecorder nameRecorder) {
        pageSource = document;
        classNameRecorder = nameRecorder;
    }



    public void setPageObjectName(CodeBucket codeBucket) {

        // Get all <title> tags--hopefully there's one and only one.
        Element root = pageSource.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("title");

        if (nodeList.getLength() > 1) {
            logger.warn("Found more than one <title> tag, is this valid for a web page?");
            codeBucket.setPageObjectName(makePageObjectName(nodeList.item(0)));
        }
        else if (nodeList.getLength() == 1)  {
            logger.info("Found exactly one <title> tag, using it's text for the page object's classname.");
            codeBucket.setPageObjectName(makePageObjectName(nodeList.item(0)));
        }
        else if (nodeList.getLength() == 0) {
            logger.warn("<title> tag not found, using a default name for the page object.");
            codeBucket.setPageObjectName(makePageObjectName(null));
        }
        else if (nodeList.getLength() < 0) {
            throw new SeleniumGeneratorException(
                      "Unknown condition--Retrieving <title> tag return a negative NodeList length.  " +
                      "This should never happen."
                     );
        }

    }

    private String makePageObjectName(Node titleNode)  {

        String titleText;
        String className;

        // If there was no title node, fetch a default symbolname.  Otherwise, use the title for the symbol name.
        if (titleNode == null) {
            titleText = null;
        }
        else {
            titleText = titleNode.getTextContent();
            logger.info("Using title tag '" + titleNode.getNodeName() + "' with text \"" + titleText + "\"");
        }

        className = classNameRecorder.makeSymbolName(titleText);
        logger.info("Using symbol name '" + className + "' for the page object class name.");

        return className;

    }


}
