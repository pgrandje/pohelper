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

        // TODO: Configurator can get it's configuration from a config file, but then the command-line args can override some settings.
//      getConfigurator().loadConfigFile();

        getCommandLineProcessor().processCommandLine(args);

        // TODO:  See if there's some way to keep the command-line processing completely within the Command-line processor to avoid having to come back to main-app.
        // todo:  Perhaps it's best just to move the whole interaction between Command-line Processor and Generator into the Command-line Processor?
        // TODO: Passing a Command Message object would decouple the generator from the interface.
        if (getCommandLineProcessor().getGenerateType() == Generator.GenerateType.INTERACTIVE) {
            getCommandLineProcessor().runInteractiveMode();
        }
        else {
            Generator.getGenerator().generate(getCommandLineProcessor().getUrl(),
                                     getCommandLineProcessor().getGenerateType()
            );
        }
    }

    private static CommandLineProcessor getCommandLineProcessor() {
        return CommandLineProcessor.getCommmandLineProcessor();
    }

}
