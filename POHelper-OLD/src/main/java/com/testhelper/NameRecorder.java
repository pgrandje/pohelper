package com.testhelper;

import java.util.HashMap;
import org.apache.log4j.Logger;


/**
 * Collects and stores the member and method names used during generation. This is to avoid duplicate
 * symbol names being generated.
 * User: pgrandje
 * Date: 8/6/12
 */
public class NameRecorder {

    private Logger logger;
    private Configurator configurator;

    private String instanceName;
    private HashMap<String, Integer> recordedNames;

    // Used for writing symbol names when there's no text or attributes available.
    private static int defaultSymbolCounter = 0;


    // TODO:  Name recorder should have a config option for removing underscores--I could use this for the className.
    public NameRecorder(String name) {

        logger = Logger.getLogger(NameRecorder.class);
        instanceName = name;

        this.configurator = Configurator.getConfigurator();

        recordedNames = new HashMap<String, Integer>(200);
        logger.debug("**** " + instanceName + " Name Recorder Created ***");

    }


    private String makeDefaultSymbolName() {

        logger.debug("**** Making a default symbol name ****");
        StringBuffer newSymbolName = new StringBuffer();

        defaultSymbolCounter++;
        newSymbolName.append(configurator.getDefaultMemberName() + defaultSymbolCounter);
        logger.debug("Returning symbol name: " + newSymbolName);

        return newSymbolName.toString();
    }


    // if text is null it returns a default symbol name.
    // This is to avoid calling methods to have to call two different methods.
    public String makeSymbolName(String text) {

        if (text == null) {
            return makeDefaultSymbolName();
        }

        StringBuffer stringBuffer = new StringBuffer();

        logger.debug("**** Making new symbol name beginning with text: " + text + " ****");

        // This is the Text-->Symbol Name SYMBOL-MAKING code.

        // Create symbol name by replacing spaces in text with underscore.
        // This seems to work.  It's for all "non-words" according to a StackOverflow post.
        String tempString = text.replaceAll("\\W+", "_");
        logger.debug("Replaced all whitespace resulting in symbol name: " + tempString);

        // And, remove all non-alphanumeric chars.
        tempString.replaceAll("[^a-zA-Z0-9_]", "");
        logger.debug("Replaced all non-alphanumeric or underscore chars resulting in symbol name: " + tempString);

        // And, limit the tag's text to the first 32 characters for it's string name.
        String symbolName = tempString.substring(0, Math.min(tempString.length(), 32));
        logger.debug("Limiting symbol name to 32 chars resulting in : " + symbolName);

        // And, remove any non-alpha chars from the beginning.  My choice here to fix this is arbitrary,
        //      there's probably a number of solutions.
        // Had to use the string buffer here, since the String is not easily changeable.
        if (Character.isDigit(symbolName.charAt(0))) {

            logger.debug("Symbol name begins with a digit, appending alphabetic text.");

            // TODO:  This string should be easily configurable from a config file.
            stringBuffer.append("BeganWithDigit_");
        }
        else if (symbolName.charAt(0) == '_') {

            logger.debug("Symbol name begins with underscore, appending alphabetic text.");
            stringBuffer.append("BeganWithUnderscore");

        };

        stringBuffer.append(symbolName);

        logger.debug("Checking for duplicates using symbol name: " + stringBuffer);
        stringBuffer = recordSymbolName(stringBuffer);

        logger.debug("Returning symbol name: " + stringBuffer);
        return stringBuffer.toString();
    }



    private StringBuffer recordSymbolName(StringBuffer symbolName) {

        if (recordedNames.get(symbolName.toString()) != null)  {

            logger.debug(instanceName + ":  Name '" + symbolName + "' already exists in recorded names.");

            // toString() is necessary otherwise the HashMap can't determine equality of keys.
            Integer nameCount = recordedNames.get(symbolName.toString());
            logger.debug(instanceName + ":  and has been encountered " + nameCount + " times.");

            logger.debug(instanceName + ": Increasing count to " + nameCount++);
            recordedNames.put(symbolName.toString(), nameCount);

            symbolName.replace(0, symbolName.length(), symbolName + nameCount.toString());

        }
        else {

            logger.debug(instanceName + ": Adding new symbol name to recorder.");
            recordedNames.put(symbolName.toString(), 1);

        }

        return symbolName;
    }

}
