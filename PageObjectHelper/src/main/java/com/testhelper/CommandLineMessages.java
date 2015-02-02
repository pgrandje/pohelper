package com.testhelper;

/**
 * Contains all string error messages for various error conditions throughout the app.
 * Used primarily after known exceptions occur.  For example, if an expected file or directory
 * are not found.
 * @author Paul Grandjean
 * @since 3/23/14
 * @version 1.0alpha
 */
public class CommandLineMessages {

    public static final String COMMAND_LINE_OPTIONS_REQUIRED = "Command-line options are required.  Use -h for help";

    public static final String GENERATE_OPTION_REQUIRED = "-generate needs an option, 'code', 'hints', or 'codeFromHints'";

    public static final String URL_VALUE_REQUIRED = "-url value required.  Ex: -url http://www.mysite.com";

    // The app can write either sourcecode, or a hints file, so we just say "write its output" in this message.
    public static final String destParamRequired =
            "-dest command-line parameter required for specifying the folder where the app will write its output.";

    public static final String DESTINATION_VALUE_REQUIRED = "A directory path is required for the -dest parameter";

    public static final String INVALID_URL = "Invalid URL supplied to -url parameter.";

    public static final String DIRECTORY_DOES_NOT_EXIST = "Supplied directory path for -dest does not exist";

    public static final String HINTS_FILE_NAME_REQUIRED = "A filename for the hints file is required.";

    public static final String CODE_SHELL_TEMPLATE_FILE_PATH_REQUIRED = "A filepath to the Code Shell Template file is required.";

    public static final String CODE_TEMPLATE_FILE_PATH_REQUIRED = "A filepath to the Code Template file is required.";

    public static final String LOCATOR_CONFIGURATION_VALUE_REQUIRED = "A locator configuration value is required.  Use one of 'attribs_css', 'attribs_only', 'css_only'";

    public static final String LOCATOR_INVALID_VALUE = "Invalid value for -locator.";

    public static final String DEFAULT_MEMBER_NAME_REQUIRED = "A default member name is required.";

    public static final String UKNOWN_OPTION = "Unknown command-line option found. Use '-h' for help.";

    public static final String REQUIRED_OPTIONS = "-generate <value> and -url <value> are required.  Use '-h' for help.";

    // TODO: When I have a help-processor, move commandLineHelp string out of CommandLineMessages
    public static final String COMMAND_LINE_HELP = "-generate  (can be set to analyze for the hints file.)\n" +
            "-codeShell or -codeShellTemplate -- for setting the filepath of the code template file.  This is the file that defines\n" +
            " the other shell, such as the class name, for the page object.\n" +
            "-tagSwitch or tagSwitchTemplate -- specifies the filepath for the tags to be used for code generation and the code template\n" +
            " snippets for code generation.\n" +
            "-loc or -locator -- specifies the strategy to use for writing WebElement locators.\n" +
            "-defMem or -defaultMemberName -- specifies the string to use by default for WebElement members when no useful string\n" +
            " from the corresponding HTML tag can be used.\n" +
            "-h or -help -- displays command-line help.";

}
