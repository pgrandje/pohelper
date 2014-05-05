package com.testhelper;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Stores the generated code as it is accumulated.  Serves as a buffer which can be modified before
 * the code output is written or displayed.
 * User: pgrandje
 * Date: 6/3/12
 */
public class CodeBucket extends AbstractBucket {

    private final Logger logger = Logger.getLogger(CodeBucket.class);

    private static CodeBucket codeBucket = null;

    private CodeShellLoader codeShellLoader;

    // The source code to be generated.
    private StringBuffer codeHeader;
    private StringBuffer codeBuffer;
    private StringBuffer codeTrailer;

    // For the output file.
    private String outPutFileName;
    private BufferedWriter outputFile;


    // CodeBucket is a singleton since we would only ever need one at a time.
    public static CodeBucket getBucket()  throws IOException {
        if (codeBucket == null) {
            codeBucket = new CodeBucket();
        }
        return codeBucket;
    }


    private CodeBucket() throws IOException {

        codeBuffer = new StringBuffer();

        codeShellLoader = new CodeShellLoader();
        codeShellLoader.loadConfig(this);

    }

    public void setCodeHeader(StringBuffer header) {
        codeHeader = header;
    }

    public void addCode(String string) {
        codeBuffer.append(string);
        codeBuffer.append("\n");
    }


    public void setCodeTrailer(StringBuffer trailer) {
        codeTrailer = trailer;
    }

    @Override
    public void setPageObjectName(String pageName) {

        outPutFileName = pageName + ".java";
        logger.info("Setting filename to '" + outPutFileName + "'.");

        logger.info("Setting classname to '" + pageName + "'.");
        logger.debug("Using code header:");
        logger.debug(codeHeader);

        StringBuffer tempBuffer = new StringBuffer();
        tempBuffer.append(codeHeader.toString().replaceAll("<title>", pageName));
        logger.debug("Added the class name to the code header.");
        logger.debug("Generated Code header will look like this:\n" + tempBuffer);

        codeHeader = tempBuffer;
    }




    private void createOutputFile(String filePath) {

        // TODO: try-catch in closeOutputFile and createOutputFile are redundant
        try {

             if (filePath == null) {
                  throw new SeleniumGeneratorException("Output file path is null.");
             }
             filePath = filePath + "/" + outPutFileName;
             logger.info("Writing output file: " + filePath);
             outputFile = new BufferedWriter(new FileWriter(filePath));

        }
        catch (IOException ioException) {

            logger.error("File Not Opened");
            logger.error("Cause: " + ioException.getCause());
            logger.error("Message: " + ioException.getMessage());
            logger.error("Stack Trace: " + ioException.getStackTrace());
            throw new SeleniumGeneratorException("Could not open final output file.  Message: " +
                                                  ioException.getMessage()
                                                );

        }

    }


    public void dumpToFile(String filePath) {

        try {
            createOutputFile(filePath);
            logger.trace("Writing code header:\n" + codeHeader.toString());
            outputFile.write(codeHeader.toString());
            logger.trace("Writing code buffer:\n" + codeBuffer.toString());
            outputFile.write(codeBuffer.toString());
            logger.trace("Writing code trailer:\n" + codeTrailer.toString());
            outputFile.write(codeTrailer.toString());
            closeOutputFile();

        } catch (IOException e) {
            logger.error("Exception writing to code output file");
            logger.error("Message: " + e.getMessage());
            throw new SeleniumGeneratorException("Caught I/O Exception in CodeBucket.dumpToFile().");
        }

    }


    public void closeOutputFile() {

        // TODO: try-catch in closeOutputFile and createOutputFile are redundant
        try {
            logger.info("Closing output file.");
            outputFile.close();
        } catch (IOException e) {
            logger.error("Exception writing to code output file");
            logger.error("Message: " + e.getMessage());
            throw new SeleniumGeneratorException("Exception writing to code output file.  Message: " + e.getMessage());
        }

    }

}
