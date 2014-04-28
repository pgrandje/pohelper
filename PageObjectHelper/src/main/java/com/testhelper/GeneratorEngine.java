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
    private static Configurator configurator = null;

    public static void main(String[] args) throws IOException, ParserConfigurationException {

        setConfiguration(args);

        // Generate the hints or code output.

        if (configurator.getGenerateStatus() == Configurator.GenerateType.HINTS_ONLY) {

            setPageName();

            // Scan the DOM to get a list of tags and their attributes.
            // TODO: I should use a different type of TagDescriptor when generating hints and not needing the code snippets.
            TagDescriptorList tagDescriptorList = PageScanner.getScanner().scan();

            writeHintsFromTagDescriptors(tagDescriptorList);
        }
        else if (configurator.getGenerateStatus() == Configurator.GenerateType.CODE) {

            setPageName();

            // Scan the nodes
            TagDescriptorList tagDescriptorList = PageScanner.getScanner().scan();

            writeCodeFromTagDescriptors(tagDescriptorList);

        }
        else if (configurator.getGenerateStatus() == Configurator.GenerateType.CODE_FROM_HINTS) {

            // The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
            NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");
            PageDescriptor pageObjectDescriptor = new PageDescriptor();
            pageObjectDescriptor.setPageName(hintsDescriptorList.getPageName(), classNameRecorder, CodeBucket.getBucket());
            // TODO: Should I continue to use the PageDescriptor for the page name?  Or should I store it in the List?
            setPageName(classNameRecorder);

            TagDescriptorList hintsDescriptorList = HintsScanner.getScanner().scan();

            writeCodeFromTagDescriptors(hintsDescriptorList);

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
        configurator = Configurator.getConfigurator(args);
        if (configurator.validateCommandline() == false) {
            System.out.println(configurator.getErrorMessage());
            System.exit(0);
        };

        configurator.processArgs();

    }


    /*
        Pre-process using info from the Document's page source and use this to store a description of the page.
        The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
     */
    private static void setPageName() throws IOException, ParserConfigurationException{
        classNameRecorder = new NameRecorder("Class Name Recorder");
        PageDescriptor pageDescriptor = new PageDescriptor();
        pageDescriptor.setPageName(PageScanner.getScanner().getDom(), classNameRecorder, HintsBucket.getBucket());
    }


    private static void writeCodeFromTagDescriptors(TagDescriptorList tagDescriptorList) throws IOException {

        CodeBucket codeBucket = CodeBucket.getBucket();

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
        codeBucket.dumpToFile(configurator.getDestinationFilePath());

    }


    private static void writeHintsFromTagDescriptors(TagDescriptorList tagDescriptorList) throws IOException {

        // Write the hints file.
        for(TagDescriptor tagDescriptor : tagDescriptorList) {
            HintsBucket.getBucket().addTag(tagDescriptor.getTag());
            HintsBucket.getBucket().addText(tagDescriptor.getTextValue());
            HintsBucket.getBucket().addAttributes(tagDescriptor.getAttributePairs());
            HintsBucket.getBucket().addLocator(tagDescriptor.getLocatorString());
        }


        // Dump the hints file.
        // TODO: default file path can be stored in the Bucket and Configurator used to change it.  Don't need to pass the filename from calling method.
        HintsBucket.getBucket().dumpToFile("./Hints.txt");


    }

}
