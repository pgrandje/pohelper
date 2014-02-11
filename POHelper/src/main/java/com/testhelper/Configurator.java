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


    private String commandLineHelp = "-generate  (can be set to analyze for the hints file.)\n" +
            "-codeShell or -codeShellTemplate -- for setting the filepath of the code template file.  This is the file that defines\n" +
            " the other shell, such as the class name, for the page object.\n" +
            "-tagSwitch or tagSwitchTemplate -- specifies the filepath for the tags to be used for code generation and the code template\n" +
            " snippets for code generation.\n" +
            "-loc or -locator -- specifies the strategy to use for writing WebElement locators.\n" +
            "-defMem or -defaultMemberName -- specifies the string to use by default for WebElement members when no useful string\n" +
            " from the corresponding HTML tag can be used.\n" +
            "-h or -help -- displays command-line help.";

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



    // Configurator is a singleton and can only be constructed using this method.
    // For thread-safety using synchronized, just in case.
    public static synchronized Configurator getConfigurator(String[] args) {

        if (singletonConfigurator == null) {
               singletonConfigurator = new Configurator(args);
        }
        return singletonConfigurator;
    }


    // This one could return an instance with default values stored, and could also load an xml-based config file.
    public static Configurator getConfigurator() {
        return singletonConfigurator;
    }


    private Configurator(String[] args) {

        logger = Logger.getLogger(Configurator.class);

        // Assign the delimeters.
        codeTemplateDelimiters = new CodeTemplateDelimiters();

        // Set the default runtime values.
        // No default for the URL, if an invalid URL is supplied then an exception is thrown.  This is what we want.
        generate = GenerateType.CODE;
        destinationFilePath = ".";

        // Command-line params will override the defaults and the config file.

        for (int i=0; i<args.length; i++) {

            if (args[i].equals("-generate")) {
                i++;
                validateArgValue(args[i]);
                generate = assignGenerateValue(args[i]);
                logger.info("Generate state set to: " + generate.toString());
            }
            else if (args[i].equals("-url")) {
                i++;
                validateArgValue(args[i]);
                try {
                    baseUrlToScan = new URL(args[i]);
                    logger.info("Configurator using URL via -url command-line arg, URL set to '" + baseUrlToScan + "'.");
                }
                catch(MalformedURLException e) {
                    throw new SeleniumGeneratorException("Invalid URL on command line, exception message: " + e.getMessage() + "--Exception cause: " + e.getCause());

                }
            }
            else if (args[i].equals("-dest") || args[i].equals("-destination")) {
                i++;
                validateArgValue(args[i]);
                destinationFilePath = verifyDirectory(args[i]);
                logger.info("Set destination folder to " + destinationFilePath);
            }
            else if (args[i].equals("-codeShell") || args[i].equals("-codeShellTemplate")) {
                i++;
                validateArgValue(args[i]);
                codeShellTemplateFilePath = verifyFile(args[i]);
                logger.info("File path for Code Shell Template is set to: " + codeShellTemplateFilePath);
            }
            else if (args[i].equals("-tagSwitch") || args[i].equals("-tagSwitcherConfig")) {
                i++;
                validateArgValue(args[i]);
                codeTemplateFilePath = verifyFile(args[i]);
                logger.info("File path for Tag Switcher's code template file is set to: " + codeTemplateFilePath);
            }
            else if (args[i].equals("-loc") || args[i].equals("-locator")) {
                i++;
                validateArgValue(args[i]);
                if (args[i].equals("attribs_css")) {
                    locatorConfig = LocatorConfig.ATTRIBS_CSS;
                }
                else if (args[i].equals("attribs_only")) {
                    locatorConfig = LocatorConfig.ATTRIBS_ONLY;
                }
                else if (args[i].equals("css_only")) {
                    locatorConfig = LocatorConfig.CSS_ONLY;
                }
                else {
                    printCommandLineHelp();
                    throw new SeleniumGeneratorException("Error in command-line.  Invalid value for -locator.");
                }
                logger.info("Locator generation using " + locatorConfig.toString());
            }
            else if (args[i].equals("-defMem") || args[i].equals("-defaultMemberName")) {
                i++;
                validateArgValue(args[i]);
                defaultMemberName = args[i];
            }
            else if (args[i].equals("-h") || args[i].equals("-help")) {
                printCommandLineHelp();
            }
            else {
                printCommandLineHelp();
                throw new SeleniumGeneratorException("Unknown argument found.");
            }

        }

    }


    private void validateArgValue(String argValue) {

        if (argValue == null) {
            throw new SeleniumGeneratorException("Argument requires a value; value is 'null'.");
        }

        if (argValue.charAt(0) == '-') {
            throw new SeleniumGeneratorException("Argument requires a value; value is missing.  In place of value found option '" + argValue + "' ");
        }

    }


    private String verifyDirectory(String directoryPath) {

        if (!new File(directoryPath).isDirectory())
        {
           throw new SeleniumGeneratorException("Directory path '" + directoryPath + "' is not an existing directory.");
        }

        return directoryPath;
    }


    private String verifyFile(String filePath) {

        if (!new File(filePath).exists())
        {
           throw new SeleniumGeneratorException("File '" + filePath + "' doesn't exist.");
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
            logger.info("Generating both analysis file and sourcecode.");
            return GenerateType.CODE_FROM_HINTS;
        }
        else {
            printCommandLineError();
            throw new SeleniumGeneratorException("Error in command-line syntax.  Invalid -generate option found.");
        }

    }


    public URL getUrl() {
        return baseUrlToScan;
    }

    public String getDestinationFilePath() {
        return destinationFilePath;
    }

    public GenerateType getGenerateStatus() {
        return generate;
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
            throw new SeleniumGeneratorException("Parser configuration exception parsing configuration file.  Exception message: " + e.getMessage());
        } catch (SAXException e) {
            throw new SeleniumGeneratorException("SAX exception parsing configuration file.  Exception message: " + e.getMessage());
        } catch (IOException e) {
            throw new SeleniumGeneratorException("I/O exception loading configuration file.  Exception message: " + e.getMessage());
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
            throw new SeleniumGeneratorException("Multiple '" + option + "' tags found in the configuration file.");
        }

        // Any given option, may, or may not, be included in the config file, so the nList for a given option could return
        // as null or empty.
        if (nList == null || nList.getLength() != 0) {

            Node node = nList.item(0);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
               throw new SeleniumGeneratorException("Configuration file error, node '" + node.getNodeName() + "' is not an Element Node.");
            }

            Element element = (Element) node;
            optionValue = element.getNodeValue();

        }

        if(optionValue == null) {
            throw new SeleniumGeneratorException("Configuration file error.  Option value not recognized.");
        }


        // TODO:  put a switch-case here to process each option


    }




}
