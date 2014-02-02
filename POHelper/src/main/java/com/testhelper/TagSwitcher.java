package com.testhelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Stores a map of key-value pairs where the keys are the HTML tag types (<div>, <ul> etc) from the config file and the
 * values are their associated code-snippet templates. These pairings are specified in the code template file and
 * stored in memory with this object.  As the Generator scans the dom, it looks up each tag in this data structure and
 * if the encountered tag type is registered here, the code snippet for member-locator code, or the code snippet for
 * the related method is returned to the generation engine.
 * User: pgrandje
 * Date: 10/23/11
 */
public class TagSwitcher {

    private Logger logger;

    private HashMap<String, TagTemplate> lookUpMap;

    boolean dumpTableFlag = true;

    Configurator configurator;
    CodeLoader codeLoader;


    // Passing the configurator rather than just the filepath returned from it.
    // This allows more flexibility, I can access any aspects of the configuration.
    TagSwitcher(Configurator config) throws IOException {

        logger = Logger.getLogger(TagSwitcher.class);
        lookUpMap = new HashMap<String, TagTemplate>();
        dumpTableFlag = true;

        configurator = config;

        codeLoader = new CodeLoader(this);
        codeLoader.loadConfig();
    }


    void add(String tag, TagTemplate tagTemplate) {
        logger.debug("Adding tag " + tag);
        logger.debug("... with code: ");
        logger.debug(tagTemplate);

        lookUpMap.put(tag, tagTemplate);
    }


    TagTemplate getTemplate(String tag) {

        // First time lookup table is used, dump the table to the log.
        if (dumpTableFlag == true) {

            logger.debug("*** Dumping Tag Lookup Table ***");
            Iterator iterator = lookUpMap.entrySet().iterator();
            while (iterator.hasNext()) {
               Map.Entry<String, TagTemplate> pairs = (Map.Entry)iterator.next();
               logger.debug("Key: " + pairs.getKey() + "   Member Code: " + pairs.getValue().getMemberCode());
            }

            logger.debug("*** End Tag Lookup Table ***");
            dumpTableFlag = false;
        }

        logger.debug("Looking up " + "'" + tag + "'");
        TagTemplate template = lookUpMap.get(tag);

        if (template != null) {
            logger.debug("Returning template with member code: " + template.getMemberCode());
        }
        else {
            logger.debug("Tag not found in lookup table. Returning null.");
        }

        return template;
    }

}



