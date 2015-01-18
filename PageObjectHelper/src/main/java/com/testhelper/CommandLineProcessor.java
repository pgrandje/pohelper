package com.testhelper;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * TODO: Add javadoc here.
 * User: pgrandje
 * Date: 1/18/15
 */
public class CommandLineProcessor {

    static private String errorMessage;

    public static void main(String[] args) throws IOException, ParserConfigurationException {

        // TODO: Command-line validation shouldn't be done by the Configurator anymore.
        if (validateCommandline(args) == false) {
            System.out.println(errorMessage);
            System.exit(0);
        };


        Generator.getGenerator().setCommandLineConfiguration(args).generate();

    }


    /**
     * Required params: -url and a valid url value are required.
     * -dest is not required and will take a default of the current working directory if not supplied.
     *
     * @return
     */
    public static boolean validateCommandline(String[] args) {
        // TODO: I don't like how I've written validateCommandLine() and processArgs() - refactor these.

        String[] commandLineArgs = args;
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


    private static boolean verifyDirectory(String directoryPath) {

            boolean returnStatus = false;

            if (new File(directoryPath).isDirectory())
            {
               returnStatus = true;
            }

            return returnStatus;
    }

}
