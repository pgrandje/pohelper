package com.pagehelper;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;


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

    // A Singleton design pattern is used here to ensure there is only ever one instance of the Configurator.

    // The Configurator is a singleton, and is created by it's static factory method.
    private static Configurator singletonConfigurator;

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
    private String pageObjectClassName = "DefaultPageObjectName";
    private String defaultMemberName = "uiElement";


    // **** File Paths with Defaults ****

    private String codeShellTemplateFilePath = "./resources/java-shell-configger.txt";
    private String codeTemplateFilePath = "./resources/java-configger2.txt";
    private String destinationFilePath = ".";
    private String configFilePath = "./resources/configuration.txt";
    // No default for the hints file name.  I'd rather force the user into knowing what they're reading.
    private String hintsFileName;

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

    public String getDestinationFilePath() {
        return destinationFilePath;
    }

    public String getHintsFilePath() {
        return hintsFileName;
    }

    public LocatorConfig getLocatorConfig() {
        return locatorConfig;
    }

    public String getPageObjectClassName() {
        return pageObjectClassName;
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
        }
        catch (FileNotFoundException e) {
            throw new PageHelperException("Configurator Config File not found. See log. Exception Message: " + e.getMessage());
        }

        try {

            String line = configFile.readLine();

            while (null != line){

                // For blank lines, just skip them.
                while(line.isEmpty()) {
                    // Get the next line from the config file.
                    line = configFile.readLine();
                }

                // Get the next line from the config file.
                line = configFile.readLine();

            }

        } catch (IOException e) {
            throw new PageHelperException("IOException loading Config File: " + e.getMessage());
        }
    }


    private void processConfigFileOption(Document doc, String option) {

        String optionValue = null;

        // TODO:  Add a loop here to process all the options in the config file.

		// Just in case the user entered more than one of the same argument, we'll get the whole list.
        NodeList nList = doc.getElementsByTagName(option);
        if (nList.getLength() > 1) {
            throw new PageHelperException("Multiple '" + option + "' tags found in the configuration file.");
        }

        // Any given option, may, or may not, be included in the config file, so the nList for a given option could return
        // as null or empty.
        if (nList.getLength() != 0) {

            Node node = nList.item(0);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
               throw new PageHelperException("Configuration file error, node '" + node.getNodeName() + "' is not an Element Node.");
            }

            Element element = (Element) node;
            optionValue = element.getNodeValue();

        }

        if(optionValue == null) {
            throw new PageHelperException("Configuration file error.  Option value not recognized.");
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
