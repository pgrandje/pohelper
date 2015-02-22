package com.pagehelper.outputbucket;

import com.pagehelper.Configurator;
import com.pagehelper.PageHelperException;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Base class for both CodeOutputBucket and HintsOutputBucket file writing.  Contains 3 buffers for managing a header, body, and
 * trailer portions of the output.  Methods here handle the file opening, naming, and writing
 * also with providing an interface for setting the page object name via an abstract method.
 * @author Paul Grandjean
 * @since 4/27/14
 * @version 1.0alpha
 */
public abstract class AbstractOutputBucket implements OutputBucket {

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

    /* Not part of the interface in case we want a more general output bucket, but we do want to require the Hints and
         CodeBuckets to set the page name.
    */
    public abstract void setPageObjectName(String pageName);

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String name) {
        fileName = name;
    }

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
    // TODO: Is it necessary to have setFilePath() as a separate method?  If it accidentally never gets called the file setup will fail.
    public void setFilePath() {

        String configuredFilePath = Configurator.getConfigurator().getDestinationFilePath();

        if (configuredFilePath != null) {
            filePath = configuredFilePath;
        }
    }

    /*
     * Checks whether the file exists based on the stored filename and path. This helps avoid accidental overwrite.
     */
    public String checkFileExists() {

        if (new File(filePath).exists() && !Configurator.getConfigurator().isOverwrite())
        {
            logger.warn("File " + filePath + " exists but -overwrite not specified.");
            // TODO: Modify PageHelperException to throw an exception with an error code and then process exit in the Interpreter or CommandlineProcessor.
            System.out.println("ERROR: File exists.  Use -overwrite or change file name or destination folder.");
            System.exit(0);
        }

        return filePath;
    }

    public void dumpToFile() {

        try {

            if (filePath == null) {
                 throw new PageHelperException("Output file path is null.");
            }

            if (fileName == null) {
                 throw new PageHelperException("Output file name is null.");
            }

            // TODO: Refactor how the complete filePath with fileName are handled to allow early evaluation of file existence.
            setCompleteFilePath();
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
            throw new PageHelperException("Caught I/O Exception writing the file.  Message: " + e.getMessage() + ".");
        }

    }

    public void setCompleteFilePath() {
        filePath = filePath + "/" + fileName;
        checkFileExists();
    }
}
