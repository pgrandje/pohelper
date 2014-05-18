package com.testhelper;

/**
 * TODO: Add javadoc here.
 * User: pgrandje
 * Date: 5/17/14
 */
public interface OutputBucket {


    public abstract void setPageObjectName(String pageName);



    public String getFileName();


    public void setFileName();

    public void setHeader(StringBuffer header);
    public void setTrailer(StringBuffer trailer);
    public void addCode(String codeString);

}
