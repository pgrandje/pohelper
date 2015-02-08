package com.pagehelper;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

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
    private Generator.GenerateType generateType;

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
    private final String CRAWL_OPTION = "-crawlSite";
    private final String H_OPTION = "-h";
    private final String HELP_OPTION = "-help";

    private final String GENERATE_OPTION_CODE = "code";
    private final String GENERATE_OPTION_HINTS = "hints";
    private final String GENERATE_OPTION_CODE_FROM_HINTS = "codeFromHints";
    private final String GENERATE_OPTION_LINKS_ONLY = "linksOnly";
    private final String GENERATE_OPTION_INTERACTIVE = "interactive";


    private final String LOCATOR_OPTION_ATTRIBS_AND_CSS = "attribsAndCss";
    private final String LOCATOR_OPTION_ATTRIBS_ONLY = "attribsOnly";
    private final String LOCATOR_OPTION_CSS_ONLY = "cssOnly";


    // Interactive commands -- used strings instead of chars to allow a fully-typed "quit" and "dump" to avoid
    // accidental 'q' or 'd' key press quiting a session prematurely.
    private final String COMMAND_SKIP = "s";
    private final String COMMAND_SHOW_ATTRIBUTES = "a";
    private final String COMMAND_WRITE_TO_BUCKET = "w";
    private final String COMMAND_DUMP_CODE = "dump";
    private final String COMMAND_QUIT = "quit";


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

     /*
     * TODO: Update this comment on Required Options.
     * Required option: -url and a valid url value are required.
     * -dest is not required and will take a default of the current working directory if not supplied.
     *
     * @return true if no command-line errors found, otherwise false.
     */
    public void processCommandLine(String[] args) throws PageHelperException {

        logger = Logger.getLogger(Configurator.class);

        this.commandLineOptions = args;

        if (commandLineOptions.length == 0) {
            printCommandLineError(CommandLineMessages.COMMAND_LINE_OPTIONS_REQUIRED);
        }

        // Command-line params will override the defaults and the config file.
        for (int i=0; i<args.length; i++) {

            // -generate is not required and defaults to 'code', but if supplied, it needs a generate option.
            if (commandLineOptions[i].equalsIgnoreCase(GENERATE_OPTION)) {

                checkForRequiredOptionValue(++i, CommandLineMessages.GENERATE_VALUE_REQUIRED);
                assignGenerateValue(args[i]);

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
            else if (args[i].equalsIgnoreCase(CRAWL_OPTION)) {

                getConfigurator().setDefaultMemberName(args[i]);
                logger.info("Default Member Name is " + getConfigurator().getDefaultMemberName());

            }
            else if (args[i].equalsIgnoreCase(H_OPTION) || args[i].equalsIgnoreCase(HELP_OPTION)) {

                printCommandLineHelp();
                System.exit(0);

            }
            else {

                printCommandLineError(CommandLineMessages.UKNOWN_OPTION);

            }
        }

        // Verify the required params were supplied
        if ((url == null) || (generateType == null)) {
            printCommandLineError(CommandLineMessages.REQUIRED_OPTIONS);
        }

        // Run either batch-mode generate code or hints, or run interactive mode.
        if (generateType == Generator.GenerateType.INTERACTIVE) {
            runInteractiveMode();
        }
        else {
            getInterpreter().generate(url, generateType);
        }
    }


    private void runInteractiveMode() throws PageHelperException {

        String command = null;

        PageDescriptor pageDescriptor = getInterpreter().getPageDescriptor(url);
        TagDescriptorList tagDescriptors = getInterpreter().getTagDescriptors(url);
        getInterpreter().setWriteList(pageDescriptor);

        // TODO: Print interactive command-line command help.
        System.out.println("Enter commands:");

        //  open up standard input
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Processing Page: " + pageDescriptor.getPageObjectName());

        Iterator<TagDescriptor> tagDescriptorIterator = tagDescriptors.iterator();

        while(true) {

            if(!tagDescriptorIterator.hasNext()) {
                System.out.println("End of tag descriptors list. Exiting.  Goodbye.");
                break;
            }

            TagDescriptor tagDescriptor = tagDescriptorIterator.next();
            System.out.println("Found tag: " + tagDescriptor.getTag());

            if (getInterpreter().hasAttributes(tagDescriptor)) {
                System.out.println("With " + tagDescriptor.getAttributePairs().size() + "attributes");
            }

            System.out.print("> ");

            //  read a command from the command-line; need to use try/catch with the
            //  readLine() method
            try {
                command = br.readLine();
            } catch (IOException ioe) {
                System.out.println("I/O error trying to read your command! Try again.");
            }

            if (command == null) {
                System.out.println("I/O error trying to read your command! Try again.");
            }
            else if (command.equalsIgnoreCase(COMMAND_SKIP)) {
                ;
            }
            else if (command.equalsIgnoreCase(COMMAND_SHOW_ATTRIBUTES)) {
                System.out.println(getInterpreter().getAttributePairs(tagDescriptor));
            }
            else if (command.equalsIgnoreCase(COMMAND_WRITE_TO_BUCKET)) {
                getInterpreter().addToWriteList(tagDescriptor);
                System.out.println("Tag saved to write bucket.");
            }
            else if (command.equalsIgnoreCase(COMMAND_DUMP_CODE)) {
                getInterpreter().dumpWriteList(pageDescriptor);
                System.out.println("Code written to folder: " + Configurator.getConfigurator().getDestinationFilePath());
            }
            else if (command.equalsIgnoreCase(COMMAND_QUIT)) {
                System.out.println("Thanks, Goodbye.");
                break;
            }
            else {
                throw new PageHelperException("Unknown interactive command.");
            }
        }
    }



    // *** private utility methods ***

    private void assignGenerateValue(String generateOptionValue) {

        if (generateOptionValue.equalsIgnoreCase(GENERATE_OPTION_CODE)) {

            logger.info("Generating sourcecode only.");
            generateType = Generator.GenerateType.CODE;
        }
        else if (generateOptionValue.equalsIgnoreCase(GENERATE_OPTION_HINTS)) {
            logger.info("Generating hints file only.");
            generateType =  Generator.GenerateType.HINTS;
        }
        else if (generateOptionValue.equalsIgnoreCase(GENERATE_OPTION_CODE_FROM_HINTS)) {
            logger.info("Generating code from hints file.");
            generateType =  Generator.GenerateType.CODE_FROM_HINTS;
        }
        else if (generateOptionValue.equalsIgnoreCase(GENERATE_OPTION_INTERACTIVE)) {
            logger.info("Selected Interactive Mode for code generation.");
            generateType = Generator.GenerateType.INTERACTIVE;
        }
        else if (generateOptionValue.equalsIgnoreCase(GENERATE_OPTION_LINKS_ONLY)) {
            logger.info("Selected links only generation.");
            generateType = Generator.GenerateType.LINKS_ONLY;
        }
        else {
            printCommandLineError("Unknown value supplied to -generate. Use one of " +
                    "'" + GENERATE_OPTION_CODE + "'" +
                    "'" + GENERATE_OPTION_HINTS + "'" +
                    "'" + GENERATE_OPTION_CODE_FROM_HINTS + "'" +
                    "'" + GENERATE_OPTION_INTERACTIVE + "'"
            );
        }
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

    private Interpreter getInterpreter() {
        return Interpreter.getInterpreter();
    }
}
