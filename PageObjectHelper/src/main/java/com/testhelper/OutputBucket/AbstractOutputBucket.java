package com.testhelper.outputbucket;

import com.testhelper.Configurator;
import com.testhelper.TestHelperException;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Base class for both CodeOutputBucket and HintsOutputBucket file writing.  Contains 3 buffers for managing a header, body, and
 * trailer portions of the output.  Methods here handle the file opening, naming, and writing
 * also with providing an interface for setting the page object name via an abstract method.
 * User: pgrandje
 * Date: 4/27/14
 */
public abstract class AbstractOutputBucket {

    private final Logger logger = Logger.getLogger(AbstractOutputBucket.class);

    /* The source or hints to be generated.
       Note:  I'm not catching exceptions for these being null.  Body is initialized in constructor.  But header and
              trailer are not and are only set if they are explicitly set with the methods.  I decided to just throw a
              null ptr exception if this occurs.
    */
    protected StringBuffer header;
    protected StringBuffer body;
    protected StringBuffer trailer;

    // File path and name will not have defaults.  Throwing an exception if these have not been set.
    private String filePath;
    private String fileName;
    private BufferedWriter outputFile;


    AbstractOutputBucket() {
        header = new StringBuffer();
        body = new StringBuffer();
        trailer = new StringBuffer();
    }


    public abstract void setPageObjectName(String pageName);


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String name) {
        fileName = name;
    };


    public void setHeader(StringBuffer header) {
        this.header = header;
    }


    public void setTrailer(StringBuffer trailer) {
        this.trailer = trailer;
    }


    public void addCode(String codeString) {
        logger.trace("Adding string to bucket:\n" + codeString);
        body.append(codeString);
        body.append("\n");
    }


    /**
     * If filepath is configured from the command-line, and therefore stored in the Configurator, the filepath is set
     * to that value.  Otherwise, the current working directory is used for the output file destination.
     */
    public void setFilePath() {

        String configuredFilePath = Configurator.getConfigurator().getDestinationFilePath();

        if (configuredFilePath != null) {
            filePath = configuredFilePath;
        }
        else {
            filePath = System.getProperty("user.dir");
        }
    }


    public void dumpToFile() {

        try {

            if (filePath == null) {
                 throw new TestHelperException("Output file path is null.");
            }

            if (fileName == null) {
                 throw new TestHelperException("Output file name is null.");
            }

            filePath = filePath + "/" + fileName;
            logger.info("Using current working directory: " + System.getProperty("user.dir"));
            logger.info("Writing output file: " + filePath);
            outputFile = new BufferedWriter(new FileWriter(filePath));
            logger.trace("Writing code header:\n" + header.toString());
            outputFile.write(header.toString());
            logger.trace("Writing code buffer:\n" + body.toString());
            outputFile.write(body.toString());
            logger.trace("Writing code trailer:\n" + trailer.toString());
            outputFile.write(trailer.toString());
            logger.info("Closing output file.");
            outputFile.close();
        } catch (IOException e) {
            logger.error("I/O Exception writing output file");
            logger.error("Message: " + e.getMessage());
            throw new TestHelperException("Caught I/O Exception writing the file.  Message: " + e.getMessage() + ".");
        }

    }

}
