package com.testhelper;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * TODO: Add comments here.
 * User: pgrandje
 * Date: 1/18/15
 */
public class CommandLineProcessor {

    private static Logger logger = Logger.getLogger(CommandLineProcessor.class);

    static private String errorMessage;

    public static void main(String[] args) throws IOException, ParserConfigurationException {

        // TODO: Find a better place to set the logs

        // Used by the loggers
        PropertyConfigurator.configure("log4j.properties");

        if (!validateCommandline(args)) {
            System.out.println(errorMessage);
            System.exit(0);
        }

        // TODO: Configurator can get it's configuration from a config file, but then the command-line args can override some settings.
//        getConfigurator().loadConfigFile();

        GenerateMessage message = processArgs(args);

        // TODO: String[] args could be converted into a Configuration object if I de-couple the Configurator and Generator.
        Generator.getGenerator().generate(message);

        // Or, for Interactive Mode for Code generation....
//        TagDescriptorList tagDescriptorList = Generator.getGenerator().setConfiguration(args).getTagDescriptorsFromPage();
//        TagDescriptorList tagDescriptorList = Generator.getGenerator().setConfiguration(args).getTagDescriptorsFromHints();

        // And, for Interactive Mode using Hints I'll need to create a HintsList object.
    }


    /*
     * Required params: -url and a valid url value are required.
     * -dest is not required and will take a default of the current working directory if not supplied.
     *
     * @return true if no command-line errors found, otherwise false.
     * TODO:  validateCommmandline doesn't need to return a boolean if it always throws an exception when an error is found--how best to handle this?
     */
    public static boolean validateCommandline(String[] args) {
        // TODO: I don't like how I've written validateCommandLine() and processArgs() - refactor these.

        boolean returnStatus = true;


        if (args == null) {
            printCommandLineError();
            printCommandLineHelp();
            throw new PageHelperException("Command-line args must be supplied.");
        }

        for (int i=0; i<args.length; i++) {

            if (args[i].equals("-url")) {

                i++;
                if (i >= args.length) {
                    returnStatus = false;
                    errorMessage = MessageLibrary.urlValueRequired;
                    break;
                }

                checkForRequiredArgValue(args[i]);

                // The url is saved in processArgs() so we don't need to actually assign it here, but we will verify it's a valid URL.
                try {
                    new URL(args[i]);
                } catch (MalformedURLException e) {
                    returnStatus = false;
                    errorMessage = MessageLibrary.badUrl + " -- URL Exception says: " + e.getMessage();
                }

            }
            // -dest is not required, but if it is supplied, it requires a directory path value.
            else if (args[i].equals("-dest") || args[i].equals("-destination")) {

                i++;
                // TODO: Checking the cnt > commandLineArgs.length is too weak for determining if it's value is a valid filepath.
                // TODO: This may be overkill now--There's an existing method that checks for a supplied param.
                if (i >= args.length) {
                    returnStatus = false;
                    errorMessage = MessageLibrary.destValueRequired;
                    break;
                }

                checkForRequiredArgValue(args[i]);

                // validate the file path
                if (!verifyDirectory(args[i])) {
                    returnStatus = false;
                    errorMessage = MessageLibrary.badDirectoryFilePath;
                }

            }
        }

        return returnStatus;
    }

    private static void checkForRequiredArgValue(String argValue) {
        if (argValue.charAt(0) == '-') {
            throw new PageHelperException("Argument requires a value but value is missing.  In place of value found option '" + argValue + "' ");
        }
    }


    public static GenerateMessage processArgs(String[] args) {

        logger = Logger.getLogger(Configurator.class);

        GenerateMessage generateMessage = new GenerateMessage();

        // TODO: Do I need to set any other default values?  Config file should set those defaults--such as file path.  And the generate message shouldn't have defaults.

        // Command-line params will override the defaults and the config file.

        for (int i=0; i<args.length; i++) {

            if (args[i].equals("-generate")) {
                i++;
                generateMessage.setGenerateType(assignGenerateValue(args[i]));
            }
            else if (args[i].equals("-url")) {
                i++;
                try {
                    generateMessage.setBaseUrl(new URL(args[i]));
                    logger.info("Configurator using URL via -url command-line arg, URL set to '" + args[i] + "'.");
                }
                // With the command-line validator, this is not necessary, but it still makes the code safer.
                catch(MalformedURLException e) {
                    throw new PageHelperException("Invalid URL on command line, exception message: " + e.getMessage() + "--Exception cause: " + e.getCause());

                }
            }
            // The command-line validator verifies that if we get here, we have a valid filepath following.
            else if (args[i].equals("-dest") || args[i].equals("-destination")) {
                i++;
                getConfigurator().setDestinationFilePath(args[i]);
                logger.info("Set destination folder to " + args[i]);
            }
            else if (args[i].equals("-hints")) {
                i++;
                getConfigurator().setHintsFileName(args[i]);
                logger.info("Using Hints file: " + args[i]);
            }
            else if (args[i].equals("-codeShell") || args[i].equals("-codeShellTemplate")) {
                i++;
                getConfigurator().setCodeShellTemplateFilePath(checkFileExists(args[i]));
                logger.info("File path for Code Shell Template is set to: " + args[i]);
            }
            else if (args[i].equals("-tagSwitch") || args[i].equals("-tagSwitcherConfig")) {
                i++;
                getConfigurator().setCodeTemplateFilePath(checkFileExists(args[i]));
                logger.info("File path for Tag Switcher's code template file is set to: " + args[i]);
            }
            else if (args[i].equals("-loc") || args[i].equals("-locator")) {
                i++;
                if (args[i].equals("attribs_css")) {
                    getConfigurator().setLocatorConfig(Configurator.LocatorConfig.ATTRIBS_CSS);
                }
                else if (args[i].equals("attribs_only")) {
                    getConfigurator().setLocatorConfig(Configurator.LocatorConfig.ATTRIBS_ONLY);
                }
                else if (args[i].equals("css_only")) {
                    getConfigurator().setLocatorConfig(Configurator.LocatorConfig.CSS_ONLY);
                }
                else {
                    printCommandLineHelp();
                    throw new PageHelperException("Error in command-line.  Invalid value for -locator.");
                }
                logger.info("Locator generation using " + args[i]);
            }
            else if (args[i].equals("-defMem") || args[i].equals("-defaultMemberName")) {
                i++;
                getConfigurator().setDefaultMemberName(args[i]);
            }
            else if (args[i].equals("-h") || args[i].equals("-help")) {
                printCommandLineHelp();
            }
            else {
                printCommandLineHelp();
                throw new PageHelperException("Unknown argument found.");
            }

        }

        return generateMessage;
    }


    private static GenerateMessage.GenerateType assignGenerateValue(String generateOptionValue) {

        if (generateOptionValue.equals("code")) {

            logger.info("Generating sourcecode only.");
            return GenerateMessage.GenerateType.CODE;
        }
        else if (generateOptionValue.equals("hints")) {
            logger.info("Generating analysis file only.");
            return GenerateMessage.GenerateType.HINTS;
        }
        else if (generateOptionValue.equals("codefromhints")) {
            logger.info("Generating code from hints file.");
            return GenerateMessage.GenerateType.CODE_FROM_HINTS;
        }
        else {
            printCommandLineError();
            printCommandLineHelp();
            throw new PageHelperException("Error in command-line syntax.  Invalid -generate option found.");
        }
    }


    // *** private utility methods ***

    private static boolean verifyDirectory(String directoryPath) {

            boolean returnStatus = false;

            if (new File(directoryPath).isDirectory())
            {
               returnStatus = true;
            }

            return returnStatus;
    }


    /*
     * Verifies a required configuration file exists.  If not, throws an exception.
     * This is private to be used as an internal verification.  It is not meant to verify command-line supplied
     * filename values.
     */
    private static String checkFileExists(String filePath) {

        if (!new File(filePath).exists())
        {
           throw new PageHelperException("File '" + filePath + "' doesn't exist.");
        }

        return filePath;
    }

    private static void printCommandLineHelp() {
        System.out.println("");
    }

    private static void printCommandLineError() {
        System.out.println("Syntax error in command-line parameters. Use -h or -help for correct command-line parameters.");
    }

    private static Configurator getConfigurator() {
        return Configurator.getConfigurator();
    }

}
