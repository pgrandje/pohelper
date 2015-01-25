package com.testhelper;

import org.apache.log4j.PropertyConfigurator;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Main entry point for the Page Helper command-line app
 * Default action is to generate code.
 * @author Paul Grandjean
 * @since 1/18/15
 * @version 1.0alpha
 */
public class CommandLineApp {

    public static void main(String[] args) throws IOException, ParserConfigurationException {

        PropertyConfigurator.configure("log4j.properties");

        // Configurator can get it's configuration from a config file, before the command-line args override settings.
//      getConfigurator().loadConfigFile();

        getCommandLineProcessor().processArgs(args);

        Generator.getGenerator().generate(getCommandLineProcessor().getUrl(),
                                          getCommandLineProcessor().getGenerateType()
                                         );
    }

    private static CommandLineProcessor getCommandLineProcessor() {
        return CommandLineProcessor.getCommmandLineProcessor();
    }

}
