package com.pagehelper.outputbucket;

import com.pagehelper.CodeShellLoader;
import com.pagehelper.PageHelperException;
import org.apache.log4j.Logger;

/**
 * Stores the generated code as it is accumulated.  Serves as a buffer which can be modified before
 * the code output is written or displayed.
 * @author Paul Grandjean
 * @since 6/3/12
 * @version 1.0alpha
 */
public class CodeOutputBucket extends AbstractOutputBucket {

    private final Logger logger = Logger.getLogger(CodeOutputBucket.class);

    private static CodeOutputBucket codeBucket = null;

    private CodeShellLoader codeShellLoader;


    // CodeOutputBucket is a singleton since we would only ever need one at a time.
    public static CodeOutputBucket getBucket()  throws PageHelperException {
        if (codeBucket == null) {
            codeBucket = new CodeOutputBucket();
        }
        return codeBucket;
    }


    /**
     * The CodeShellLoader initializes the header and trailer.
     */
    private CodeOutputBucket() throws PageHelperException {
        super();
        codeShellLoader = new CodeShellLoader();
        // TODO: Re-evaluate:  Is this the best interface to pass the CodeOutputBucket into the CodeLoader?  CodeShellLoader does this but CodeLoader does not.
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
