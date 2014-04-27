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
public class HintsBucket {

    private final Logger logger = Logger.getLogger(HintsBucket.class);

    // The hints text to be generated.
    private StringBuffer hintsHeader;
    private StringBuffer hintsBuffer;

    // For the output file.
    // TODO: Use the configurator to set the path for the Analysis file.
    private String outPutFilePath = "./";
    private String outPutFileName = "Hints.txt";
    private BufferedWriter outputFile;



    public HintsBucket() {
        hintsBuffer = new StringBuffer();
    }


    public void setPageObjectName(String pageName) {

        logger.debug("Setting classname to '" + pageName + "'.");

        StringBuffer tempBuffer = new StringBuffer();
        tempBuffer.append(HintsDescriptor.PAGE_MARKER + ": " + pageName);

        hintsHeader = tempBuffer;
    }


    public void addTag(String tag) {
        hintsBuffer.append(HintsDescriptor.NEW_TAG_DELIMITER + "\n");
        hintsBuffer.append(tag + " \n");
    }

    public void addText(String text) {
        hintsBuffer.append(HintsDescriptor.TEXT_MARKER + text + " \n");
    }


    public void addAttributes(HashMap<String, String> attributePairs) {
        if (!attributePairs.isEmpty()) {
            for (Map.Entry attributePair : attributePairs.entrySet()) {
                hintsBuffer.append(HintsDescriptor.ATTRIBUTE_MARKER + attributePair.getKey() + " = " + attributePair.getValue() + "\n");
            }
        }
    }


    public void addLocator(String locator) {
        hintsBuffer.append(HintsDescriptor.LOCATOR_MARKER + locator + " \n");
    }

    public void setOutPutFilePath(String path) {
        outPutFilePath = path;
    }


    public void setOutputFileName(String fileName) {
        outPutFileName = fileName;
    }


    public void createOutputFile(String filePath) {

        // Set up the output file for the code output.
        try {

            // TODO:  should I redo this to require the path and filename be preset via their setters.
             if (filePath == null) {
                  filePath = outPutFilePath + outPutFileName;
             }
             logger.info("Creating output file: " + filePath);
             logger.info("Using current working directory: " + System.getProperty("user.dir"));
             outputFile = new BufferedWriter(new FileWriter(filePath));

        }
        catch (IOException ioException) {

            logger.info("File Not Opened in: " + ioException.getClass());
            logger.info("Cause: " + ioException.getCause());
            logger.info("Message: " + ioException.getMessage());
            logger.info("Stack Trace: " + ioException.getStackTrace());
            System.out.println("File Not Opened in: " + ioException.getClass());
            System.out.println(ioException.getStackTrace());
            System.exit(0);

        }

    }


    public void dumpToFile() {

        try {
            outputFile.write(hintsHeader.toString());
            outputFile.write(hintsBuffer.toString());

        } catch (IOException e) {
            System.out.println("Exception writing to code output file");
            System.out.println("Message: " + e.getMessage());
            System.out.println(e.getStackTrace());
            throw new SeleniumGeneratorException("Caught I/O Exception in CodeBucket.dumpToFile().");
        }

    }


    public void closeOutputFile() {

        try {
            outputFile.close();
        } catch (IOException e) {
            System.out.println("Exception writing to code output file");
            System.out.println("Message: " + e.getMessage());
            System.out.println(e.getStackTrace());
        }

    }

}
