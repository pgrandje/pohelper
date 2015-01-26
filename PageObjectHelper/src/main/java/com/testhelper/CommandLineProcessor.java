package com.testhelper;

import org.apache.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Command-line processor evaluates the command-line options and their values.  It validates for correct syntax and
 * prints an error message and the help summary when errors are made.
 * If the command-line is valid it
 *   - Sets the URL for the starting page.  This is then available to the calling program via accessors.
 *   - Sets the type of generation, CODE, HINTS, or CODE_FROM_HINTS.  This is then available to the calling program via accessors.
 *   - Sets configurations passed via command-line params into the Configurator for overriding it's default,
 *   and file-supplied settings.
 *
 * @author Paul Grandjean
 * @version 1.0alpha
 * @since 1/25/15
 */
public class CommandLineProcessor {


    private static Logger logger = Logger.getLogger(CommandLineApp.class);

    private static CommandLineProcessor singletonCommandLineProcessor;

    String[] commandLineOptions;

    private URL url;
    private Generator.GenerateType generateType = Generator.GenerateType.CODE;

    // Command-line options
    private final String URL_OPTION = "-url";

    /* Generator will be created by a static factory */
    private CommandLineProcessor() {
    }

    // Generator will always be accessed using this factory-getter to ensure there is always only one instance.
    public static CommandLineProcessor getCommmandLineProcessor() {
        if (singletonCommandLineProcessor == null) {
            singletonCommandLineProcessor = new CommandLineProcessor();
        }
        return singletonCommandLineProcessor;
    }


    public void processCommandLine(String[] args) {

        this.commandLineOptions = args;

        try {
            validateCommandline();
        } catch (PageHelperException e) {
            System.out.println(e.getMessage());
            printCommandLineError();
            System.exit(0);
        }

        // Assigns the type of code or hints generation, retrieves the URL, and sets any configurations passed from the command-line.
        processArgs(commandLineOptions);
    }

     /*
     * TODO: Update this comment on Required Options.
     * Required option: -url and a valid url value are required.
     * -dest is not required and will take a default of the current working directory if not supplied.
     *
     * @return true if no command-line errors found, otherwise false.
     */
    private void validateCommandline() {

        if (commandLineOptions.length == 0) {
            printCommandLineHelp();
            throw new PageHelperException("Command-line args must be supplied.");
        }

        for (int i=0; i<commandLineOptions.length; i++) {

            // TODO: Need to test that URL is, in fact, supplied.
            if (commandLineOptions[i].equals(URL_OPTION)) {

                checkForRequiredOptionValue(++i, CommandLineMessages.urlValueRequired);

                // Verify it's a valid URL.
                try {
                    new URL(commandLineOptions[i]);
                } catch (MalformedURLException e) {
                    throw new PageHelperException(CommandLineMessages.badUrl + " -- " + e.getMessage());
                }
            }
            // -dest is not required, but if it is supplied, it requires a directory path value.
            else if (args[i].equals("-dest") || args[i].equals("-destination")) {

                i++;
                // TODO: Checking the cnt > commandLineArgs.length is too weak for determining if it's value is a valid filepath.
                // TODO: This may be overkill now--There's an existing method that checks for a supplied param.
                if (i >= args.length) {
                    errorMessage = CommandLineMessages.destValueRequired;
                    break;
                }

                checkForRequiredOptionValue(args[i]);

                // validate the file path
                if (!verifyDirectory(args[i])) {
                    errorMessage = CommandLineMessages.badDirectoryFilePath;
                }
            }
        }
    }


    private void checkForRequiredOptionValue(int args_index, String errorMessage) {

        // If there's nothing after the option, or if the next string begins with a '-', then the option's value wasn't supplied.
        if ((args_index >= commandLineOptions.length) || (commandLineOptions[args_index].charAt(0) == '-')) {
            throw new PageHelperException(errorMessage);
        }
    }


    private void processArgs(String[] args) {

        logger = Logger.getLogger(Configurator.class);

        // Command-line params will override the defaults and the config file.

        for (int i=0; i<args.length; i++) {

            if (args[i].equals("-generate")) {
                i++;
                generateType = assignGenerateValue(args[i]);
            }
            else if (args[i].equals("-url")) {
                i++;
                try {
                    url = new URL(args[i]);
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
    }


    private Generator.GenerateType assignGenerateValue(String generateOptionValue) {

        if (generateOptionValue.equals("code")) {

            logger.info("Generating sourcecode only.");
            return Generator.GenerateType.CODE;
        }
        else if (generateOptionValue.equals("hints")) {
            logger.info("Generating analysis file only.");
            return Generator.GenerateType.HINTS;
        }
        else if (generateOptionValue.equals("codefromhints")) {
            logger.info("Generating code from hints file.");
            return Generator.GenerateType.CODE_FROM_HINTS;
        }
        else {
            printCommandLineError();
            printCommandLineHelp();
            throw new PageHelperException("Error in command-line syntax.  Invalid -generate option found.");
        }
    }

    public URL getUrl() {
        return url;
    }

    public Generator.GenerateType getGenerateType() {
        return generateType;
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
    private String checkFileExists(String filePath) {

        if (!new File(filePath).exists())
        {
           throw new PageHelperException("File '" + filePath + "' doesn't exist.");
        }

        return filePath;
    }

    private void printCommandLineHelp() {
        System.out.println("****HELP****");
    }

    private void printCommandLineError() {
        System.out.println("Syntax error in command-line parameters. Use -h for help.");
    }

    private Configurator getConfigurator() {
        return Configurator.getConfigurator();
    }
}
