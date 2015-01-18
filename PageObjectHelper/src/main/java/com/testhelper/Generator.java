package com.testhelper;

import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;

import com.testhelper.outputbucket.CodeOutputBucket;
import com.testhelper.outputbucket.HintsOutputBucket;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Currently This is the Main Entry Point for the app.
 * Eventually, this will provide an API for code and hints generation, and a separate class will provide an interactive
 * command-line.  In addition other UIs will be considered where each will send generate commands to this class.
 *
 * The Generator is the entry point for generation of code and hints from a web site, and of code from a previously
 * generated hints file.
 *
 * Creator:  Paul Grandjean
 * Date:  First created sometime in 2011 in Salt Lake City, Utah :-)
 * Has since gone through many incremental refactorings and is gradually growing mature in its years.
 */

public class Generator
{

    private static final Logger logger = Logger.getLogger(Generator.class);

    // Generator will always be a singleton so we're using the Singleton pattern.
    private static Generator singletonGenerator;

    /* Accumulates the classnames used for each page object to ensure uniqueness.
       The classNameRecorder needs to exist, and accumulate page names for all pages generated.
    */
    private static NameRecorder classNameRecorder = null;


    /* Generator will be created by a static factory */
    private Generator() {
    }

    // Generator will always be accessed using this factory-getter to ensure there is always only one instance.
    public static Generator getGenerator() {
        if (singletonGenerator == null) {
            singletonGenerator = new Generator();
        }
        return singletonGenerator;
    }

    /**
     * Handles both command-line parametes and the config file in one method.
     * @param args command-line parameters
     * @return the Generator
     */
    public Generator setConfiguration(String[] args) {
//        loadConfigFile();
        setCommandLineConfiguration(args);
        return singletonGenerator;
    }

    private Generator setCommandLineConfiguration(String[] args) {

        // Used by the loggers
        PropertyConfigurator.configure("log4j.properties");

        // Sets the configuration using any command-line parameters.
        getConfigurator().processArgs(args);

        return singletonGenerator;
    }

    private Generator loadConfigFile() {
        getConfigurator().loadConfigFile();
        return singletonGenerator;
    }

    /**
     * Generates either the code or the analysis file.
     */
    // TODO:  If I used a strategy pattern my passing in a configuration-strategy I could avoid these if statements.
    public Generator generate() throws IOException, ParserConfigurationException {

        // TODO: put in error handling to ensure the Confguration is set before running the singletonGenerator.

        /* A new PageDescriptor is created for each page or hints file scanned.
           The PageDescriptor is then used to name the class name in the code bucket when generating code or in the
           hints file when generating hints.
         */
        PageDescriptor pageDescriptor = null;
        classNameRecorder = new NameRecorder("Class Name Recorder");

        // Generate the hints or code output.

        if (getConfigurator().getGenerateStatus() == Configurator.GenerateType.HINTS) {

            pageDescriptor = PageScanner.getScanner().getPageName(classNameRecorder);

            // Scan the DOM to get a list of tags and their attributes.
            TagDescriptorList tagDescriptorList = PageScanner.getScanner().scan();
            writeHintsFromTagDescriptors(pageDescriptor, tagDescriptorList);

        }
        /* Possible Design Pattern: This condition, and the one above, can both call a outputbucket.writeContents()
            method, with the same params and the same param setup code.  The only difference is the bucket used.
           So, I think a Builder Pattern or Factory Pattern could be used to build the appropriate bucket based
           on the Configurator's generate-status.  I could also pass in a Scanner object maybe which covers the Hints Scanning case also.
         */
        else if (getConfigurator().getGenerateStatus() == Configurator.GenerateType.CODE) {

            pageDescriptor = PageScanner.getScanner().getPageName(classNameRecorder);

            // Scan the nodes
            TagDescriptorList tagDescriptorList = PageScanner.getScanner().scan();
            writeCodeFromTagDescriptors(pageDescriptor, tagDescriptorList);

        }
        /* Possible Design Pattern: This condition is also similar.  The only difference is the Scanner used.
        */
        else if (getConfigurator().getGenerateStatus() == Configurator.GenerateType.CODE_FROM_HINTS) {

            pageDescriptor = HintsScanner.getScanner().setPageName(classNameRecorder);

            TagDescriptorList tagDescriptorList = HintsScanner.getScanner().scan();
            writeCodeFromTagDescriptors(pageDescriptor, tagDescriptorList);

        }
        else {
            throw new TestHelperException("Invalid configuration state.  Should never get here.");
        }

        logger.info("SUCCESSFUL COMPLETION");

        return singletonGenerator;
    }






    /* Possible Design Pattern: Both writing hints and writing code use a TagDescriptorList and a outputbucket.  But they
       write very different things, and use different buckets.  Yet they both create a type of outputbucket.
       What type of pattern can be used here?
        - Some sort of Adapter that translates TagDescriptors to the correct bucket?
        - Some sort of Builder that returns a specific outputbucket?
        - TODO: Each bucket must override a writeBucket content method?  Then pass the Code outputbucket in as a specific type of an
            abstract output bucket.This is easiest! ****
    */
    private static void writeCodeFromTagDescriptors(PageDescriptor pageDescriptor, TagDescriptorList tagDescriptorList) throws IOException {

        verifyTagDescriptorList(tagDescriptorList);

        CodeOutputBucket codeBucket = CodeOutputBucket.getBucket();
        codeBucket.setFilePath();
        codeBucket.setFileName(pageDescriptor.getPageObjectName());
        codeBucket.setPageObjectName(pageDescriptor.getPageObjectName());

        // Write the members to the code buffer.
        for(TagDescriptor tagDescriptor : tagDescriptorList) {
            if (tagDescriptor.hasComments()) {
                codeBucket.addCode(tagDescriptor.getComment());
            }
            codeBucket.addCode(tagDescriptor.getMemberCode());
        }

        // Write the methods to the code buffer.
        for(TagDescriptor tagDescriptor : tagDescriptorList) {
            codeBucket.addCode(tagDescriptor.getMethodCode());
        }

        // Dump the generated sourcecode.
        codeBucket.dumpToFile();
    }


    private static void writeHintsFromTagDescriptors(PageDescriptor pageDescriptor, TagDescriptorList tagDescriptorList) throws IOException {

        verifyTagDescriptorList(tagDescriptorList);

        HintsOutputBucket hintsBucket = HintsOutputBucket.getBucket();
        hintsBucket.setFilePath();
        hintsBucket.setFileName(pageDescriptor.getPageObjectName());
        hintsBucket.setPageObjectName(pageDescriptor.getPageObjectName());

        // Write the hints file.
        for(TagDescriptor tagDescriptor : tagDescriptorList) {
            hintsBucket.addTag(tagDescriptor.getTag());
            hintsBucket.addText(tagDescriptor.getTextValue());
            hintsBucket.addAttributes(tagDescriptor.getAttributePairs());
            hintsBucket.addLocator(tagDescriptor.getLocatorString());
        }

        // Dump the hints file.
        // TODO: default file path can be stored in the outputbucket and Configurator used to change it.  Don't need to pass the filename from calling method.
        hintsBucket.dumpToFile();

    }


    private static void verifyTagDescriptorList(TagDescriptorList tagDescriptorList) {

        if (null == tagDescriptorList) {
            throw new TestHelperException("Got null Tag Descriptor List--cannot generate code or hints.");
        }

        if (tagDescriptorList.size() == 0) {
            throw new TestHelperException("Tag Descriptor List is empty--cannot generate code or hints.");
        }
    }

    private Configurator getConfigurator() {
        return Configurator.getConfigurator();
    }

}
