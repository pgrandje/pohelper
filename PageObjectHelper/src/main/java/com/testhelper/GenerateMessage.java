package com.testhelper;

import java.net.URL;

/**
 * TODO: Add javadoc here.
 * User: pgrandje
 * Date: 1/18/15
 */
public class GenerateMessage {


    // Defaults to generating sourcecode.
    // TODO: Evaluate if a default generation method is appropriate, may be better to enforce assignment or throw an exception.
    private GenerateType generate = GenerateType.CODE;

    // URL
    private URL baseUrlToScan;

    public GenerateMessage() {
    }

    // **** Generation Configurations ****

    // Generation Choices
    public enum GenerateType {
        CODE, HINTS, CODE_FROM_HINTS, ANALYZE_AND_GENERATE }

    public GenerateType getGenerateType() {
        if (generate == null) {
            throw new PageHelperException("Generate message has not been set.");
        }
        return generate;
    }

    public void setGenerateType(GenerateType generate) {
        this.generate = generate;
    }

    public URL getUrl() {
        if (baseUrlToScan == null) {
            throw new PageHelperException("URL has not been assigned.");
        }
        return baseUrlToScan;
    }

    public void setBaseUrl(URL baseUrlToScan) {
        this.baseUrlToScan = baseUrlToScan;
    }
}