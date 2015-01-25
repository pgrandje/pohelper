package com.testhelper.outputbucket;

/**
 * Defines the interface for Output Buckets.  These are responsible for accumulating string information
 * (i.e. code or hints) and dumping them to a file.
 * @author Paul Grandjean
 * @since 5/17/14
 * @version 1.0alpha
 */
public interface OutputBucket {

    /**
      * If filepath is configured from the command-line, and therefore stored in the Configurator, the filepath is set
      * to that value.  Otherwise, the current working directory is used for the output file destination.
      */
    public String getFileName();
    public void setFileName(String name);
    public void setFilePath();
    public void dumpToFile();

    public void setHeader(StringBuffer header);
    public void addCode(String codeString);
    public void setTrailer(StringBuffer trailer);

}
