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


    // CodeBucket is a singleton since we would only ever need one at a time.
    public static CodeBucket getBucket()  throws IOException {
        if (codeBucket == null) {
            codeBucket = new CodeBucket();
        }
        return codeBucket;
    }


    /**
     * The CodeShellLoader initializes the header and trailer.
     */
    private CodeBucket() throws IOException {
        super();
        codeShellLoader = new CodeShellLoader();
        // TODO: Re-evaluate:  Is this the best interface to pass the CodeBucket into the CodeLoader?  CodeShellLoader does this but CodeLoader does not.
        codeShellLoader.loadConfig(this);
    }

    @Override
    public void setFileName(String pageName) {
        super.setFileName(pageName + ".java");
        logger.info("Setting filename to '" + getFileName() + "'.");
    }


    @Override
    public void setPageObjectName(String pageName) {

        logger.info("Setting classname to '" + pageName + "'.");
        logger.debug("Using code header:");
        logger.debug(header);

        // TODO: IMPROVEMENT NEEDED -- This removes the initialized buffer and replaces it.  And the CodeShellLoader did the same thing.  Is there a less wasteful way?
        StringBuffer alteredHeader = new StringBuffer();
        alteredHeader.append(header.toString().replaceAll("<title>", pageName));
        logger.debug("Added the class name to the code header:\n" + alteredHeader);

        // Header was already been initialized from the constructor calling the CodeShellLoader.  But we reassign it
        // here to the now altered header.
        header = alteredHeader;
    }

}
