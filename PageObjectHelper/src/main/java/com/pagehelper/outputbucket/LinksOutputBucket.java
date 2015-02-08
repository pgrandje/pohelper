package com.pagehelper.outputbucket;

import com.pagehelper.HintsFileDelimeters;
import com.pagehelper.LinksFileDelimeters;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores the Links file output as it is accumulated.  Serves as a buffer which can be modified before
 * the Links output is written or displayed.
 * @author Paul Grandjean
 * @since 6/3/12
 * @version 1.0alpha
 */
public class LinksOutputBucket extends AbstractOutputBucket {

    private final Logger logger = Logger.getLogger(LinksOutputBucket.class);

    private static LinksOutputBucket linksBucket;


    // HintsOutputBucket is a singleton since we would only ever need one at a time.
    public static LinksOutputBucket getBucket()  {
        if (linksBucket == null) {
            linksBucket = new LinksOutputBucket();
        }
        return linksBucket;
    }


    // **** WARNING: **** HintsOutputBucket never sets the trailer--it will throw a null ptr exception!!!!
    private LinksOutputBucket() {
        super();
    }


    @Override
    public void setFileName(String pageName) {
        super.setFileName(pageName + "_Links.txt");
        logger.info("Setting filename to '" + getFileName() + "'.");
    }

    @Override
    public void setPageObjectName(String pageName) {

        logger.debug("Setting classname to '" + pageName + "'.");

        StringBuffer tempBuffer = new StringBuffer();
        tempBuffer.append(LinksFileDelimeters.PAGE_MARKER + ": " + pageName + "\n");

        header.append(tempBuffer);
    }


    public void addLinkHref(URL href) {
        logger.debug("Adding href: " + href);
        body.append(href + " \n");
    }

    public void addLinkText(String text) {
        logger.debug("Adding text: " + text);
        body.append(HintsFileDelimeters.TEXT_MARKER + text + " \n");
    }


    public void addAttributes(HashMap<String, String> attributePairs) {
        if (!attributePairs.isEmpty()) {
            body.append(LinksFileDelimeters.ATTRIBUTE_MARKER + "\n");
            for (Map.Entry attributePair : attributePairs.entrySet()) {
                body.append(attributePair.getKey() + "=" + attributePair.getValue() + ", ");
            }
            body.append("\n");
        }
    }

    public void addLocator(String locator) {
        logger.debug("Adding locator: " + locator);
        body.append(HintsFileDelimeters.LOCATOR_MARKER + locator + " \n");
    }

}
