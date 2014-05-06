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

    // The hints text to be generated.
    private StringBuffer hintsHeader;
    private StringBuffer hintsBuffer;

    // For the output file.
    // TODO: Use the configurator to set the path for the Analysis file.
    private String outPutFilePath = "./";
    private String outPutFileName = "Hints.txt";
    private BufferedWriter outputFile;


    // HintsBucket is a singleton since we would only ever need one at a time.
    public static HintsBucket getBucket()  {
        if (hintsBucket == null) {
            hintsBucket = new HintsBucket();
        }
        return hintsBucket;
    }

    private HintsBucket() {
        hintsBuffer = new StringBuffer();
    }

    @Override
    public void setPageObjectName(String pageName) {

        logger.debug("Setting classname to '" + pageName + "'.");

        StringBuffer tempBuffer = new StringBuffer();
        tempBuffer.append(HintsFileDelimeters.PAGE_MARKER + ": " + pageName + "\n");

        hintsHeader = tempBuffer;
    }


    public void addTag(String tag) {
        logger.debug("Adding tag: " + tag);
        hintsBuffer.append(HintsFileDelimeters.NEW_TAG_DELIMITER + "\n");
        hintsBuffer.append(tag + " \n");
    }

    public void addText(String text) {
        logger.debug("Adding text: " + text);
        hintsBuffer.append(HintsFileDelimeters.TEXT_MARKER + text + " \n");
    }


    public void addAttributes(HashMap<String, String> attributePairs) {
        if (!attributePairs.isEmpty()) {
            for (Map.Entry attributePair : attributePairs.entrySet()) {
                hintsBuffer.append(HintsFileDelimeters.ATTRIBUTE_MARKER + attributePair.getKey() + " = " + attributePair.getValue() + "\n");
            }
        }
    }


    public void addLocator(String locator) {
        logger.debug("Adding locator: " + locator);
        hintsBuffer.append(HintsFileDelimeters.LOCATOR_MARKER + locator + " \n");
    }

    public void setOutPutFilePath(String path) {
        outPutFilePath = path;
    }


    public void setOutputFileName(String fileName) {
        outPutFileName = fileName;
    }


    private void createOutputFile(String filePath) {

        // TODO: try-catch in closeOutputFile and createOutputFile are redundant -- unless they are called separately but they don't need to be.
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


    public void dumpToFile(String filePath) {

        logger.debug("Dumping file using filepath: " + filePath);
        try {
            createOutputFile(filePath);
            outputFile.write(hintsHeader.toString());
            outputFile.write(hintsBuffer.toString());
            closeOutputFile();

        } catch (IOException e) {
            System.out.println("Exception writing to code output file");
            System.out.println("Message: " + e.getMessage());
            System.out.println(e.getStackTrace());
            throw new TestHelperException("Caught I/O Exception in CodeBucket.dumpToFile().");
        }
    }


    public void closeOutputFile() {

        // TODO: try-catch in closeOutputFile and createOutputFile are redundant
        try {
            outputFile.close();
        } catch (IOException e) {
            System.out.println("Exception writing to code output file");
            System.out.println("Message: " + e.getMessage());
            System.out.println(e.getStackTrace());
        }

    }

}
