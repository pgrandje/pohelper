package com.testhelper;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Configurator
 * The Configurator sets the runtime configuration. This class is a singleton.  It's Constructor is private and both,
 * creating an new instance, or retrieving the one and only instance are both handled by the getConfigurator() method.
 * getConfigurator() operates as a Factory and a getter.
 * @author Paul Grandjean
 * Date: 5/5/12
 */
public class Configurator {

    // A Singleton design pattern is used here to ensure there is only ever one instance of the Configurator.

    // The Configurator is a singleton, and is created by it's static factory method.
    private static Configurator singletonConfigurator;

    private Logger logger;

    private String[] commandLineArgs;

    private String errorMessage;

    // URL
    private URL baseUrlToScan;

    // **** Generation Configurations ****

    // Generation Choices
    public enum GenerateType {
        CODE, HINTS, CODE_FROM_HINTS, ANALYZE_AND_GENERATE };
    // Default if -generate param is not specified is to generate the sourcecode.
    private GenerateType generate;

    // Locator Choices
    public enum LocatorConfig {CSS_ONLY, ATTRIBS_ONLY, ATTRIBS_CSS};
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
    private CodeTemplateDelimiters codeTemplateDelimiters;

    // Code template variable and locator hooks.
    private String locatorIndicator = "<locator>";
    private String memberNameIndicator = "<symbol>";

    // Code shell template hooks and parameters.
    private String codeShellCodeBlockIndicator = "<CodeBlockGoesHere>";

    // TODO:  Investigate if this is being used yet.  And if so, is it used correctly?
    // Indicates if informative comments are written to the generated code.
    private boolean writeComments = false;


    // **** Default Symbol Names ****

    // Default variable names
    private String pageObjectClassName = "DefaultPageObjectName";
    private String defaultMemberName = "uiElement";


    // **** File Paths with Defaults****

    // Default paths are assigned but will be overrideable via command-line or xml-based params.
    private String codeShellTemplateFilePath = "./resources/java-shell-configger.txt";
    private String codeTemplateFilePath = "./resources/java-configger2.txt";
    private String destinationFilePath = ".";
    private String configFilePath = "/Users/pgrandje/IdeaProjects/selgen/resources/configuration.xml";
    // No default for the hints file name.  I'd rather force the user into knowing what they're reading.
    private String hintsFileName;

    /**
     * Configurator is a singleton and can only be constructed using this method.
       For thread-safety using synchronized, just in case.
     * @param args
     * @return
     */
    public static Configurator getConfigurator(String[] args) {

        if (singletonConfigurator == null) {
           singletonConfigurator = new Configurator(args);
        }
        return singletonConfigurator;
    }


    public static Configurator getConfigurator() {
        if (singletonConfigurator == null) {
           throw new TestHelperException("Can't get Configurator without creating a new one requiring command-line args.");
        }
        return singletonConfigurator;
    }


    private Configurator(String[] args) {
        commandLineArgs = args;
    }

    /**
     * Required params: -url and a valid url value are required.
     * -dest is not required and will take a default of the current working directory if not supplied.
     *
     * @return
     */
    public boolean validateCommandline() {
        // TODO: I don't like how I've written validateCommandLine() and processArgs() - refactor these.

        boolean returnStatus = true;

        for (int i=0; i<commandLineArgs.length; i++) {

            if (commandLineArgs[i].equals("-url")) {
                i++;
                if (i >= commandLineArgs.length) {
                    returnStatus = false;
                    errorMessage = MessageLibrary.urlValueRequired;
                    break;
                }
                try {
                    // The url is saved in processArgs() so we don't need to assign it here where we're just validating.
                    new URL(commandLineArgs[i]);
                } catch (MalformedURLException e) {
                    returnStatus = false;
                    errorMessage = MessageLibrary.badUrl + " -- URL Exception says: " + e.getMessage();
                }
            }
            // -dest is not required, but if it is supplied, it requires a directory path value.
            else if (commandLineArgs[i].equals("-dest") || commandLineArgs[i].equals("-destination")) {
                i++;
                // TODO: Checking the cnt > commandLineArgs.length is too weak for determining if it's value is a valid filepath.
                // TODO: This may be overkill now--There's an existing method that checks for a supplied param.
                if (i >= commandLineArgs.length) {
                    returnStatus = false;
                    errorMessage = MessageLibrary.destValueRequired;
                    break;
                }
                // validate the file path
                if (verifyDirectory(commandLineArgs[i]) == false) {
                    returnStatus = false;
                    errorMessage = MessageLibrary.badDirectoryFilePath;
                }
            }
        }

        return returnStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }



    private boolean verifyDirectory(String directoryPath) {

        boolean returnStatus = false;

        if (new File(directoryPath).isDirectory())
        {
           returnStatus = true;
        }

        return returnStatus;
}


    public void processArgs() {

        logger = Logger.getLogger(Configurator.class);

        // Assign the delimeters.
        codeTemplateDelimiters = new CodeTemplateDelimiters();

        // Set the default runtime values.
        // No default for the URL, if an invalid URL is supplied then an exception is thrown.  This is what we want.
        generate = GenerateType.CODE;
        destinationFilePath = ".";

        // Command-line params will override the defaults and the config file.

        for (int i=0; i<commandLineArgs.length; i++) {

            if (commandLineArgs[i].equals("-generate")) {
                i++;
                checkForRequiredArgValue(commandLineArgs[i]);
                generate = assignGenerateValue(commandLineArgs[i]);
                logger.info("Generate state set to: " + generate.toString());
            }
            else if (commandLineArgs[i].equals("-url")) {
                i++;
                checkForRequiredArgValue(commandLineArgs[i]);
                try {
                    baseUrlToScan = new URL(commandLineArgs[i]);
                    logger.info("Configurator using URL via -url command-line arg, URL set to '" + baseUrlToScan + "'.");
                }
                // With the command-line validator, this is not necessary, but it still makes the code safer.
                catch(MalformedURLException e) {
                    throw new TestHelperException("Invalid URL on command line, exception message: " + e.getMessage() + "--Exception cause: " + e.getCause());

                }
            }
            // The command-line validator verifies that if we get here, we have a valid filepath following.
            else if (commandLineArgs[i].equals("-dest") || commandLineArgs[i].equals("-destination")) {
                i++;
                checkForRequiredArgValue(commandLineArgs[i]);
                destinationFilePath = commandLineArgs[i];
                logger.info("Set destination folder to " + destinationFilePath);
            }
            else if (commandLineArgs[i].equals("-hints")) {
                i++;
                // TODO: This isn't quite a valid check because an indexing error could get thrown.
                checkForRequiredArgValue(commandLineArgs[i]);
                hintsFileName = commandLineArgs[i];
                logger.info("Using Hints file: " + hintsFileName);
            }
            else if (commandLineArgs[i].equals("-codeShell") || commandLineArgs[i].equals("-codeShellTemplate")) {
                i++;
                checkForRequiredArgValue(commandLineArgs[i]);
                codeShellTemplateFilePath = checkConfigurationFileExists(commandLineArgs[i]);
                logger.info("File path for Code Shell Template is set to: " + codeShellTemplateFilePath);
            }
            else if (commandLineArgs[i].equals("-tagSwitch") || commandLineArgs[i].equals("-tagSwitcherConfig")) {
                i++;
                checkForRequiredArgValue(commandLineArgs[i]);
                codeTemplateFilePath = checkConfigurationFileExists(commandLineArgs[i]);
                logger.info("File path for Tag Switcher's code template file is set to: " + codeTemplateFilePath);
            }
            else if (commandLineArgs[i].equals("-loc") || commandLineArgs[i].equals("-locator")) {
                i++;
                checkForRequiredArgValue(commandLineArgs[i]);
                if (commandLineArgs[i].equals("attribs_css")) {
                    locatorConfig = LocatorConfig.ATTRIBS_CSS;
                }
                else if (commandLineArgs[i].equals("attribs_only")) {
                    locatorConfig = LocatorConfig.ATTRIBS_ONLY;
                }
                else if (commandLineArgs[i].equals("css_only")) {
                    locatorConfig = LocatorConfig.CSS_ONLY;
                }
                else {
                    printCommandLineHelp();
                    throw new TestHelperException("Error in command-line.  Invalid value for -locator.");
                }
                logger.info("Locator generation using " + locatorConfig.toString());
            }
            else if (commandLineArgs[i].equals("-defMem") || commandLineArgs[i].equals("-defaultMemberName")) {
                i++;
                checkForRequiredArgValue(commandLineArgs[i]);
                defaultMemberName = commandLineArgs[i];
            }
            else if (commandLineArgs[i].equals("-h") || commandLineArgs[i].equals("-help")) {
                printCommandLineHelp();
            }
            else {
                printCommandLineHelp();
                throw new TestHelperException("Unknown argument found.");
            }

        }

    }


    /*
     * The command-line validator should make this method not necessary, but I'm keeping it as extra protection.
     * @param argValue
     */
    private void checkForRequiredArgValue(String argValue) {

        // TODO: This would be a good check, but an index exception would probably be called first from the calling method.
        if (argValue == null) {
            throw new TestHelperException("Argument requires a value; value is 'null'.");
        }

        if (argValue.charAt(0) == '-') {
            throw new TestHelperException("Argument requires a value but value is missing.  In place of value found option '" + argValue + "' ");
        }

    }


    /*
     * Verifies a required configuration file exists.  If not, throws an exception.
     * This is private to be used as an internal verification.  It is not meant to verify command-line supplied
     * filename values.
     */
    private String checkConfigurationFileExists(String filePath) {

        if (!new File(filePath).exists())
        {
           throw new TestHelperException("File '" + filePath + "' doesn't exist.");
        }

        return filePath;
    }


    private GenerateType assignGenerateValue(String generateOptionValue) {

        if (generateOptionValue.equals("code")) {

            logger.info("Generating sourcecode only.");
            return GenerateType.CODE;
        }
        else if (generateOptionValue.equals("hints")) {
            logger.info("Generating analysis file only.");
            return GenerateType.HINTS;
        }
        else if (generateOptionValue.equals("codefromhints")) {
            logger.info("Generating code from hints file.");
            return GenerateType.CODE_FROM_HINTS;
        }
        else {
            printCommandLineError();
            throw new TestHelperException("Error in command-line syntax.  Invalid -generate option found.");
        }

    }


    public GenerateType getGenerateStatus() {
        return generate;
    }

    public URL getUrl() {
        return baseUrlToScan;
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

    public void printCommandLineHelp() {
        System.out.println("");
    }

    public void printCommandLineError() {
        System.out.println("Syntax error in command-line parameters. Use -h or -help for correct command-line parameters.");
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
        logger.debug("Returning default member name: " + defaultMemberName);
        return defaultMemberName;
    }

    public boolean getLocatorUsesClassnames() {
        return locatorUsesClassnames;
    }

    public boolean getWriteComments() {
        return writeComments;
    }



    //  *** XML-Config file processing methods ****

    private void loadConfigFile() {

        DocumentBuilder dBuilder;
        Document doc;

        File fXmlFile = new File(configFilePath);
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            dBuilder = docBuilderFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException e) {
            throw new TestHelperException("Parser configuration exception parsing configuration file.  Exception message: " + e.getMessage());
        } catch (SAXException e) {
            throw new TestHelperException("SAX exception parsing configuration file.  Exception message: " + e.getMessage());
        } catch (IOException e) {
            throw new TestHelperException("I/O exception loading configuration file.  Exception message: " + e.getMessage());
        }

        processConfigFileOption(doc, "generate");
    }


    private void processConfigFileOption(Document doc, String option) {

        logger.debug("Config file Root element :" + doc.getDocumentElement().getNodeName());

        String optionValue = null;

        // TODO:  Add a loop here to process all the options in the config file.

		// Just in case the user entered more than one of the same argument, we'll get the whole list.
        NodeList nList = doc.getElementsByTagName(option);
        if (nList.getLength() > 1) {
            throw new TestHelperException("Multiple '" + option + "' tags found in the configuration file.");
        }

        // Any given option, may, or may not, be included in the config file, so the nList for a given option could return
        // as null or empty.
        if (nList == null || nList.getLength() != 0) {

            Node node = nList.item(0);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
               throw new TestHelperException("Configuration file error, node '" + node.getNodeName() + "' is not an Element Node.");
            }

            Element element = (Element) node;
            optionValue = element.getNodeValue();

        }

        if(optionValue == null) {
            throw new TestHelperException("Configuration file error.  Option value not recognized.");
        }

    }

}
