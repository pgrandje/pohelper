package com.testhelper;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Stores the Hints file output as it is accumulated.  Serves as a buffer which can be modified before
 * the Hints output is written or displayed.
 * User: pgrandje
 * Date: 6/3/12
 */
public class HintsBucket {

    private final Logger logger = Logger.getLogger(HintsBucket.class);

    // The analysis text to be generated.
    private StringBuffer analysisBuffer;

    // For the output file.
    // TODO: Use the configurator to set the path for the Analysis file.
    private String outPutFilePath = "./";
    private String outPutFileName = "Hints.txt";
    private BufferedWriter outputFile;



    public HintsBucket() {
        analysisBuffer = new StringBuffer();
    }


    public void addTag(String tag) {
        analysisBuffer.append("<*** UI Element ***>\n");
        analysisBuffer.append(tag + " \n");
    }

    public void addText(String text) {
        analysisBuffer.append("Text: " + text + " \n");
    }


    public void addAttribute(NamedNodeMap attributes) {

        int numberOfAttributes = attributes.getLength();

        for (int i=0; i<numberOfAttributes; i++) {
            Attr attr = (Attr) attributes.item(i);
            analysisBuffer.append("Attribute: Type = " + attr.getName() + " -- value = " + attr.getValue() + "\n");
        }
    }


    public void addCssLocator(String locator) {
        analysisBuffer.append("Locator: " + locator + " \n");
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
            outputFile.write(analysisBuffer.toString());

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
