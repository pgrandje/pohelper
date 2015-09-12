package com.pagehelper;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;

import java.util.HashMap;


/**
 * Collects and stores the member and method names used during generation. This is to avoid duplicate
 * symbol names being generated.
 * @author Paul Grandjean
 * @since 8/6/12
 * @version 1.0alpha
 */
public class AttributeRecorder {

    private Logger logger;

    String instanceName;


    private class Attribute {

        Attr attr;

        public Attribute(Attr attr) {
            this.attr = attr;
        }


        // TODO: Go through my code and put in @Override everywhere that I've missed it.
        @Override
        public boolean equals(Object o) {

            if (this == o) return true;
            if (!(o instanceof Attribute)) return false;

            Attribute attribute = (Attribute) o;

            // TODO: verify the generated attr.equals() is not correct.
//            if (!attr.equals(attribute.attr)) return false;
            //            return true;

            return this.attr.getName().equalsIgnoreCase(attribute.attr.getName())
                    &&
                   this.attr.getValue().equalsIgnoreCase(attribute.attr.getValue());
        }

        @Override
        public int hashCode() {
            return attr.hashCode();
        }

    }

    private HashMap<Attribute, Integer> recordedAttributes;

    public AttributeRecorder(String instanceName) {
        logger = Logger.getLogger(AttributeRecorder.class);
        this.instanceName = instanceName;
        recordedAttributes = new HashMap<Attribute, Integer>(200);
        logger.debug("*** New " + instanceName + " attribute recorder ***");
    }

    public void record(Attr attr) {

        Attribute attribute = new Attribute(attr);
        Integer numOfOccurrences = recordedAttributes.get(attribute);

        if (numOfOccurrences != null)  {
            logger.debug("Recorder--" + instanceName + ": Found " + attr.getName() + "=" + attr.getValue() + " exists "
                    + numOfOccurrences + " times.");
            logger.debug(instanceName + ": Increasing count to " + numOfOccurrences++);
            recordedAttributes.put(attribute, numOfOccurrences);
        }
        else {
            logger.debug("Recorder--" + instanceName + ": Adding new attribute " + attr.getName() + "="
                    + attr.getValue());
            recordedAttributes.put(attribute, 1);
        }
    }

    public int getInstances(Attr attr) {
        Attribute attribute = new Attribute(attr);
        return recordedAttributes.get(attribute);
    }
}
