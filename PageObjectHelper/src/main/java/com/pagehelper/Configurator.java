package com.pagehelper;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;


/**
 * The Configurator sets the runtime configuration. It is potentially accessible from any point within the Page Helper
 * engine and it's supporting classes.
 * This class is a singleton.  Subsequently the getConfigurator() method operates as a Factory and a getter and must be
 * used to access the Configurator.
 * @author Paul Grandjean
 * @since 5/5/12
 * @version 1.0alpha
 */
public class Configurator {

    private final Logger logger = Logger.getLogger(this.getClass());

    // A Singleton design pattern is used here to ensure there is only ever one instance of the Configurator.

    // The Configurator is a singleton, and is created by it's static factory method.
    private static Configurator singletonConfigurator;

    // Base URL
    private URL baseUrl;

    // Locator Choices
    public enum LocatorConfig {CSS_ONLY, ATTRIBS_ONLY, ATTRIBS_CSS}
    private LocatorConfig  locatorConfig = LocatorConfig.ATTRIBS_CSS;
    private boolean locatorUsesClassnames = false;

    // **** Code Template Configurations ****

    // Code Template configurable params
    public class CodeTemplateDelimiters {
        public final String tagDelimeter = "<*** New Tag ***>";
        public final String codeBlockBeginDelimeter = "<-- Code Block Begin -->";
        public final String codeBlockEndDelimeter = "<-- Code Block End -->";
        public final String memberDelimeter = "<-- Member Code Block -->";
        public final String methodDelimeter = "<-- Method Code Block -->";
        public final String fileEndDelimeter = "<*** End ***>";
    }
    private CodeTemplateDelimiters codeTemplateDelimiters = new CodeTemplateDelimiters();

    // Code template variable and locator hooks.
    private String locatorIndicator = "<locator>";
    private String memberNameIndicator = "<symbol>";

    // Code shell template hooks and parameters.
    private String codeShellCodeBlockIndicator = "<CodeBlockGoesHere>";

    // Indicates if informative comments are written to the generated code.
    // NOTE:  this is not used at this time.
    private boolean writeComments = false;


    // **** Default Symbol Names ****

    // Default variable names
    private String defaultObjectClassName = "DefaultPageObjectName";
    private String defaultMemberName = "uiElement";


    // **** File Paths with Defaults and file options ****

    private String codeShellTemplateFilePath = "./resources/java-shell-configger.txt";
    private String codeTemplateFilePath = "./resources/java-configger2.txt";
    private String destinationFilePath = ".";
    private String configFilePath = "./resources/configuration.txt";
    // No default for the hints file name.  I'd rather force the user into knowing what they're reading.
    private String hintsFileName;
    private boolean overwrite = false;

    // *** Action Options ***
    private boolean crawl = false;


    // *** Config file ***
    private BufferedReader configFile;

    /**
     * Configurator is a singleton and can only be constructed using this method.
       For thread-safety using synchronized, just in case.
     * @return a reference to the singleton Configurator
     */
    public static Configurator getConfigurator() {

        if (singletonConfigurator == null) {
           singletonConfigurator = new Configurator();
        }
        return singletonConfigurator;
    }

    private Configurator() {
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDestinationFilePath() {
        return destinationFilePath;
    }

    public String getHintsFilePath() {
        return hintsFileName;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public LocatorConfig getLocatorConfig() {
        return locatorConfig;
    }

    public String getDefaultObjectClassName() {
        return defaultObjectClassName;
    }

    public String getCodeShellTemplateFilePath() {
        return codeShellTemplateFilePath;
    }

    public String getCodeShellCodeBlockIndicator() {
        return codeShellCodeBlockIndicator;
    }

    public String getCodeTemplateFilePath() {
        return codeTemplateFilePath;
    }

    public CodeTemplateDelimiters getCodeTemplateDelimiters() {
        return codeTemplateDelimiters;
    }

    public String getLocatorIndicator() {
         return locatorIndicator;
    }

    public String getMemberNameIndicator() {
         return memberNameIndicator;
    }

    public String getDefaultMemberName() {
        return defaultMemberName;
    }

    public boolean getLocatorUsesClassnames() {
        return locatorUsesClassnames;
    }

    public boolean getWriteComments() {
        return writeComments;
    }

    public boolean isCrawlEnabled() {
        return crawl;
    }

    public void setCrawl(boolean crawl) {
        this.crawl = crawl;
    }

    //  *** XML-Config file processing methods ****

    public void loadConfigFile() {

        try {
            configFile = new BufferedReader(new FileReader(configFilePath));
            logger.info("Using config file: " + configFilePath);
        }
        catch (FileNotFoundException e) {
            throw new PageHelperException("Configurator Config File not found. See log. Exception Message: " + e.getMessage());
        }

        try {

            String line = configFile.readLine();

            while (null != line){

                // For blank lines and comments, just skip them.
                while(line.isEmpty() || line.substring(0,2).equals("//")) {
                    logger.trace("Skipping comment or empty line: " + line);
                    // Get the next line from the config file.
                    line = configFile.readLine();
                }

                logger.info("Found configuration: " + line);

                String[] keyValuePair = line.split(":");

                switch (keyValuePair[0]) {
                    case "locatorIndicator":
                        locatorIndicator = keyValuePair[1];
                        break;
                    case "memberNameIndicator":
                        memberNameIndicator = keyValuePair[1];
                        break;
                    case "codeShellCodeBlockIndicator":
                        codeShellCodeBlockIndicator = keyValuePair[1];
                        break;
                    case "writeComments":
                        writeComments = Boolean.parseBoolean(keyValuePair[1]);
                        break;
                    case "defaultPageObjectClassName":
                        defaultObjectClassName = keyValuePair[1];
                        break;
                    case "defaultMemberName":
                        defaultMemberName = keyValuePair[1];
                        break;
                    case "codeShellTemplateFilePath":
                        codeShellTemplateFilePath = keyValuePair[1];
                        break;
                    case "codeTemplateFilePath":
                        codeTemplateFilePath = keyValuePair[1];
                        break;
                    case "destinationFilePath":
                        destinationFilePath = keyValuePair[1];
                        break;
                    case "configFilePath":
                        configFilePath = keyValuePair[1];
                        break;
                    case "crawl":
                        crawl = Boolean.parseBoolean(keyValuePair[1]);
                        break;
                    default:
                        break;
                }

                // Get the next line from the config file.
                line = configFile.readLine();
            }

        } catch (IOException e) {
            throw new PageHelperException("IOException loading Config File: " + e.getMessage());
        }
    }

        // Default paths are assigned but will be overrideable via command-line or xml-based params.
    public void setCodeShellTemplateFilePath(String codeShellTemplateFilePath) {
        this.codeShellTemplateFilePath = codeShellTemplateFilePath;
    }

    public void setHintsFileName(String hintsFileName) {
        this.hintsFileName = hintsFileName;
    }

    public void setCodeTemplateFilePath(String codeTemplateFilePath) {
        this.codeTemplateFilePath = codeTemplateFilePath;
    }

    public void setDestinationFilePath(String destinationFilePath) {
        this.destinationFilePath = destinationFilePath;
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public void setDefaultMemberName(String defaultMemberName) {
        this.defaultMemberName = defaultMemberName;
    }

    public void setLocatorConfig(LocatorConfig locatorConfig) {
        this.locatorConfig = locatorConfig;
    }
}
