package com.testhelper;

import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is the Main Entry Point for the entire app.
 * Creator:  Paul Grandjean
 * Date:  First created sometime in 2011 in Salt Lake City, Utah :-)
 * Has since gone through many incremental refactorings and is gradually growing mature in its years.
 */

public class GeneratorEngine
{

    private static final Logger logger = Logger.getLogger(GeneratorEngine.class);

    // The classNameRecorder needs to exist, and accumulate page names for all pages generated.
    private static final NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");


    public static void main(String[] args) throws IOException, ParserConfigurationException {

        setConfiguration(args);

        /* A new PageDescriptor is created for each page or hints file scanned.
           The PageDescriptor is then used to name the class name in the code bucket when generating code or the hints file when generating hints.
         */
        PageDescriptor pageDescriptor = null;

        // Generate the hints or code output.

        if (Configurator.getConfigurator().getGenerateStatus() == Configurator.GenerateType.HINTS_ONLY) {

            pageDescriptor = PageScanner.getScanner().setPageName(classNameRecorder);

            // Scan the DOM to get a list of tags and their attributes.
            // TODO: Should I use a different type of TagDescriptor when generating hints and not needing the code snippets?
            TagDescriptorList tagDescriptorList = PageScanner.getScanner().scan();

            writeHintsFromTagDescriptors(pageDescriptor, tagDescriptorList);
        }
        else if (Configurator.getConfigurator().getGenerateStatus() == Configurator.GenerateType.CODE) {

            pageDescriptor = PageScanner.getScanner().setPageName(classNameRecorder);

            // Scan the nodes
            TagDescriptorList tagDescriptorList = PageScanner.getScanner().scan();

            // TODO: Write the page name into the code bucket.
            writeCodeFromTagDescriptors(pageDescriptor, tagDescriptorList);

        }
        else if (Configurator.getConfigurator().getGenerateStatus() == Configurator.GenerateType.CODE_FROM_HINTS) {

            pageDescriptor = HintsScanner.getScanner().setPageName(classNameRecorder);

            TagDescriptorList hintsDescriptorList = HintsScanner.getScanner().scan();

            writeCodeFromTagDescriptors(pageDescriptor, hintsDescriptorList);

        }
        else {
            throw new SeleniumGeneratorException("Invalid configuration state.  Should never get here.");
        }

        logger.info("SUCCESSFUL COMPLETION");

    }


    private static void setConfiguration(String[] args) {

        // Used by the loggers
        PropertyConfigurator.configure("log4j.properties");

        // Sets the configuration using any command-line parameters
        Configurator.getConfigurator(args);
        if (Configurator.getConfigurator().validateCommandline() == false) {
            System.out.println(Configurator.getConfigurator().getErrorMessage());
            System.exit(0);
        };

        Configurator.getConfigurator().processArgs();

    }



    private static void writeCodeFromTagDescriptors(PageDescriptor pageDescriptor, TagDescriptorList tagDescriptorList) throws IOException {

        CodeBucket codeBucket = CodeBucket.getBucket();
        codeBucket.setPageObjectName(pageDescriptor.getPageName());

        // Write the member code to the code buffer.
        for(TagDescriptor hintsTagDescriptor : tagDescriptorList) {
                codeBucket.addCode(hintsTagDescriptor.getComment());
                codeBucket.addCode(hintsTagDescriptor.getMemberCode());
        }
        // Write the method code to the code buffer.
        for(TagDescriptor hintsTagDescriptor : tagDescriptorList) {
            codeBucket.addCode(hintsTagDescriptor.getMethodCode());
        }

        // Dump the generated sourcecode.
        codeBucket.dumpToFile(Configurator.getConfigurator().getDestinationFilePath());

    }


    private static void writeHintsFromTagDescriptors(PageDescriptor pageDescriptor, TagDescriptorList tagDescriptorList) throws IOException {

        HintsBucket hintsBucket = HintsBucket.getBucket();
        hintsBucket.setPageObjectName(pageDescriptor.getPageName());

        // Write the hints file.
        for(TagDescriptor tagDescriptor : tagDescriptorList) {
            hintsBucket.addTag(tagDescriptor.getTag());
            hintsBucket.getBucket().addText(tagDescriptor.getTextValue());
            hintsBucket.getBucket().addAttributes(tagDescriptor.getAttributePairs());
            hintsBucket.getBucket().addLocator(tagDescriptor.getLocatorString());
        }

        // Dump the hints file.
        // TODO: default file path can be stored in the Bucket and Configurator used to change it.  Don't need to pass the filename from calling method.
        hintsBucket.dumpToFile("./Hints.txt");

    }

}
