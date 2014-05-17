package com.testhelper;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores the Hints file output as it is accumulated.  Serves as a buffer which can be modified before
 * the Hints output is written or displayed.
 * User: pgrandje
 * Date: 6/3/12
 */
public class HintsBucket extends AbstractBucket {

    private final Logger logger = Logger.getLogger(HintsBucket.class);

    private static HintsBucket hintsBucket;


    // HintsBucket is a singleton since we would only ever need one at a time.
    public static HintsBucket getBucket()  {
        if (hintsBucket == null) {
            hintsBucket = new HintsBucket();
        }
        return hintsBucket;
    }


    // **** WARNING: **** HintsBucket never sets the trailer--it will throw a null ptr exception!!!!
    private HintsBucket() {
        super();
    }


    @Override
    public void setFileName(String pageName) {
        super.setFileName(pageName + "Hints.text");
        logger.info("Setting filename to '" + getFileName() + "'.");
    }


    @Override
    public void setPageObjectName(String pageName) {

        logger.debug("Setting classname to '" + pageName + "'.");

        StringBuffer tempBuffer = new StringBuffer();
        tempBuffer.append(HintsFileDelimeters.PAGE_MARKER + ": " + pageName + "\n");

        header.append(tempBuffer);
    }


    public void addTag(String tag) {
        logger.debug("Adding tag: " + tag);
        body.append(HintsFileDelimeters.NEW_TAG_DELIMITER + "\n");
        body.append(tag + " \n");
    }

    public void addText(String text) {
        logger.debug("Adding text: " + text);
        body.append(HintsFileDelimeters.TEXT_MARKER + text + " \n");
    }


    public void addAttributes(HashMap<String, String> attributePairs) {
        if (!attributePairs.isEmpty()) {
            for (Map.Entry attributePair : attributePairs.entrySet()) {
                body.append(HintsFileDelimeters.ATTRIBUTE_MARKER + attributePair.getKey() + " = " + attributePair.getValue() + "\n");
            }
        }
    }


    public void addLocator(String locator) {
        logger.debug("Adding locator: " + locator);
        body.append(HintsFileDelimeters.LOCATOR_MARKER + locator + " \n");
    }

}
