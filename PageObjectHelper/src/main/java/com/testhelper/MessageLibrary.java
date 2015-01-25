package com.testhelper;

/*
 * Contains all string error messages for various error conditions throughout the app.
 * Used primarily after known exceptions occur.  For example, if an expected file or directory
 * are not found.
 * User: pgrandje
 * Date: 3/23/14
 */
public class MessageLibrary {


    public static final String urlAndDestParamsRequired = "-url and -dest parameters are required.";

    public static final String urlValueRequired = "-url command-line parameter required.  Ex: -url http://www.mysite.com";

    // The app can write either sourcecode, or a hints file, so we just say "write its output" in this message.
    public static final String destParamRequired =
            "-dest command-line parameter required for specifying the folder where the app will write its output.";

    public static final String destValueRequired = "A directory path is required for the -dest parameter";

    public static final String badUrl = "Invalid URL supplied to -url parameter.";

    public static final String badDirectoryFilePath = "Supplied directory path for -dest does not exist";

    // TODO: When I have a help-processor, move commandLineHelp string out of MessageLibrary
    public String commandLineHelp = "-generate  (can be set to analyze for the hints file.)\n" +
            "-codeShell or -codeShellTemplate -- for setting the filepath of the code template file.  This is the file that defines\n" +
            " the other shell, such as the class name, for the page object.\n" +
            "-tagSwitch or tagSwitchTemplate -- specifies the filepath for the tags to be used for code generation and the code template\n" +
            " snippets for code generation.\n" +
            "-loc or -locator -- specifies the strategy to use for writing WebElement locators.\n" +
            "-defMem or -defaultMemberName -- specifies the string to use by default for WebElement members when no useful string\n" +
            " from the corresponding HTML tag can be used.\n" +
            "-h or -help -- displays command-line help.";

}
