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
    private final String GENERATE_OPTION = "-generate";
    private final String URL_OPTION = "-url";
    private final String DEST_OPTION = "-dest";
    private final String DESTINATION_OPTION = "-destination";
    private final String HINTS_FILE_NAME_OPTION = "-hintsFileName";
    private final String CODE_SHELL_TEMPLATE_OPTION = "-codeShellTemplate";
    private final String CODE_TEMPLATE_OPTION = "-codeTemplateOption";
    private final String LOCATOR_OPTION = "-locator";
    private final String DEFAULT_MEMBER_NAME_OPTION = "-defaultMemberName";
    private final String H_OPTION = "-h";
    private final String HELP_OPTION = "-help";

    /* These strings could be handled by the GenerateType enum but I preferred to do it this way so the string values
        are contained by the command-line processor.  The command-line processor should be the only place where string
        values are needed for the generate options. */
    private final String GENERATE_OPTION_CODE = "code";
    private final String GENERATE_OPTION_HINTS = "hints";
    private final String GENERATE_OPTION_CODE_FROM_HINTS = "codeFromHints";

    private final String LOCATOR_OPTION_ATTRIBS_AND_CSS = "attribsAndCss";
    private final String LOCATOR_OPTION_ATTRIBS_ONLY = "attribsOnly";
    private final String LOCATOR_OPTION_CSS_ONLY = "cssOnly";

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
        validateCommandline();

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
            printCommandLineError(CommandLineMessages.COMMAND_LINE_OPTIONS_REQUIRED);
        }

        // TODO: verifify -generate is supplied.  And -url is supplied.

    }


    private void processArgs(String[] args) {

        logger = Logger.getLogger(Configurator.class);

        // Command-line params will override the defaults and the config file.
        for (int i=0; i<args.length; i++) {

            // -generate is not required and defaults to 'code', but if supplied, it needs a generate option.
            if (args[i].equals(commandLineOptions[i].equalsIgnoreCase(GENERATE_OPTION))) {

                checkForRequiredOptionValue(++i, CommandLineMessages.GENERATE_OPTION_REQUIRED);
                generateType = assignGenerateValue(args[i]);

            }
            // -url is required.  It also requires a valid URL as a parameter.  The URL validation check is handled here.
            else if (commandLineOptions[i].equalsIgnoreCase(URL_OPTION)) {

                checkForRequiredOptionValue(++i, CommandLineMessages.URL_VALUE_REQUIRED);

                // Verify it's a valid URL.
                try {
                    url = new URL(commandLineOptions[i]);
                } catch (MalformedURLException e) {
                    printCommandLineError(CommandLineMessages.INVALID_URL + " -- " + e.getMessage());
                }

                logger.info("Set url to " + url.toString());
            }
            // -dest/-destination is not required, but if it is supplied, it requires a directory path value.
            // The default if not supplied is the current working directory.
            else if (args[i].equalsIgnoreCase(DEST_OPTION) || args[i].equalsIgnoreCase(DESTINATION_OPTION)) {

                checkForRequiredOptionValue(++i, CommandLineMessages.DESTINATION_VALUE_REQUIRED);

                // validate the file path
                if (!verifyDirectory(args[i])) {
                    printCommandLineError(CommandLineMessages.DIRECTORY_DOES_NOT_EXIST);
                }

                getConfigurator().setDestinationFilePath(args[i]);
                logger.info("Set destination folder to " + args[i]);
            }
            else if (args[i].equalsIgnoreCase(HINTS_FILE_NAME_OPTION)) {

                checkForRequiredOptionValue(++i, CommandLineMessages.HINTS_FILE_NAME_REQUIRED);
                getConfigurator().setHintsFileName(args[i]);
                logger.info("Using Hints file: " + args[i]);

            }
            else if (args[i].equalsIgnoreCase(CODE_SHELL_TEMPLATE_OPTION)) {

                checkForRequiredOptionValue(++i, CommandLineMessages.CODE_SHELL_TEMPLATE_FILE_PATH_REQUIRED);
                getConfigurator().setCodeShellTemplateFilePath(checkFileExists(args[i]));
                logger.info("File path for Code Shell Template is set to: " + getConfigurator().getCodeShellTemplateFilePath());

            }
            else if (args[i].equalsIgnoreCase(CODE_TEMPLATE_OPTION)) {

                checkForRequiredOptionValue(++i, CommandLineMessages.CODE_TEMPLATE_FILE_PATH_REQUIRED);
                getConfigurator().setCodeTemplateFilePath(checkFileExists(args[i]));
                logger.info("File path for Tag Switcher's code template file is set to: " + getConfigurator().getCodeTemplateFilePath());

            }
            else if (args[i].equalsIgnoreCase(LOCATOR_OPTION)) {

                checkForRequiredOptionValue(++i, CommandLineMessages.LOCATOR_CONFIGURATION_VALUE_REQUIRED);
                getConfigurator().setLocatorConfig(assignLocatorConfiguration(args[i]));
                logger.info("Locator generation using " + getConfigurator().getLocatorConfig());

            }
            else if (args[i].equalsIgnoreCase(DEFAULT_MEMBER_NAME_OPTION)) {

                checkForRequiredOptionValue(++i, CommandLineMessages.DEFAULT_MEMBER_NAME_REQUIRED);
                getConfigurator().setDefaultMemberName(args[i]);
                logger.info("Default Member Name is " + getConfigurator().getDefaultMemberName());

            }
            else if (args[i].equalsIgnoreCase(H_OPTION) || args[i].equalsIgnoreCase(HELP_OPTION)) {

                printCommandLineHelp();
                System.exit(0);

            }
            else {

                printCommandLineError("Unknown command-line option found. Use '-h' for help.");

            }
        }
    }

    public URL getUrl() {
        return url;
    }

    public Generator.GenerateType getGenerateType() {
        return generateType;
    }

    // *** private utility methods ***

    private Generator.GenerateType assignGenerateValue(String generateOptionValue) {

        if (generateOptionValue.equalsIgnoreCase(GENERATE_OPTION_CODE)) {

            logger.info("Generating sourcecode only.");
            return Generator.GenerateType.CODE;
        }
        else if (generateOptionValue.equalsIgnoreCase(GENERATE_OPTION_HINTS)) {
            logger.info("Generating hints file only.");
            return Generator.GenerateType.HINTS;
        }
        else if (generateOptionValue.equalsIgnoreCase(GENERATE_OPTION_CODE_FROM_HINTS)) {
            logger.info("Generating code from hints file.");
            return Generator.GenerateType.CODE_FROM_HINTS;
        }
        else {
            printCommandLineError("Unknown value supplied to -generate. Use one of " +
                    "'" + GENERATE_OPTION_CODE + "'" +
                    "'" + GENERATE_OPTION_HINTS + "'" +
                    "'" + GENERATE_OPTION_CODE_FROM_HINTS + "'"
            );
        }

        throw new PageHelperException("Invalid -generate value was uncaught resulting in no generate option to return.  This should never happen.");
    }


    private Configurator.LocatorConfig assignLocatorConfiguration(String locatorOptionValue) {

        if (locatorOptionValue.equals(LOCATOR_OPTION_ATTRIBS_AND_CSS)) {
            getConfigurator().setLocatorConfig(Configurator.LocatorConfig.ATTRIBS_CSS);
        }
        else if (locatorOptionValue.equals(LOCATOR_OPTION_ATTRIBS_ONLY)) {
            getConfigurator().setLocatorConfig(Configurator.LocatorConfig.ATTRIBS_ONLY);
        }
        else if (locatorOptionValue.equals(LOCATOR_OPTION_CSS_ONLY)) {
            getConfigurator().setLocatorConfig(Configurator.LocatorConfig.CSS_ONLY);
        }
        else {
            printCommandLineError(CommandLineMessages.LOCATOR_INVALID_VALUE);
        }

        throw new PageHelperException("Invalid -locator value was uncaught resulting in no locator value to return.  This should never happen.");
    }


    private boolean verifyDirectory(String directoryPath) {

            boolean returnStatus = false;

            if (new File(directoryPath).isDirectory())
            {
               returnStatus = true;
            }

            return returnStatus;
    }


    /*
     * Verifies a parameter value is supplied for options that require a value
     */
    private void checkForRequiredOptionValue(int args_index, String errorMessage) {

        // If there's nothing after the option, or if the next string begins with a '-', then the option's value wasn't supplied.
        if ((args_index >= commandLineOptions.length) || (commandLineOptions[args_index].charAt(0) == '-')) {
            printCommandLineError(errorMessage);
        }
    }


    /*
     * Verifies a required configuration file exists.  If not, prints the error message.
     * This is private to be used as an internal verification.  It is not meant to verify command-line supplied
     * filename values.
     */
    private String checkFileExists(String filePath) {

        if (!new File(filePath).exists())
        {
           printCommandLineError("File '" + filePath + "' doesn't exist.");
        }

        return filePath;
    }

    private void printCommandLineHelp() {
        System.out.println(CommandLineMessages.COMMAND_LINE_HELP);
    }

    private void printCommandLineError(String errorMessage) {
        System.out.print("Syntax error in command-line: ");
        System.out.println(errorMessage);
        System.out.println("Use -h for help");
        System.exit(0);
    }

    private Configurator getConfigurator() {
        return Configurator.getConfigurator();
    }
}
