package com.testhelper;

import com.testhelper.outputbucket.CodeOutputBucket;
import com.testhelper.outputbucket.HintsOutputBucket;
import org.apache.log4j.Logger;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Provides an API for page object code and hints generation.
 * @author:  Paul Grandjean
 * @Date:  First created sometime in 2011 in Salt Lake City, Utah but with many revisions since that time.  :-)
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
     * Generates either the code or the analysis file.
     */
    // TODO:  If I used a strategy pattern my passing in a configuration-strategy, could I avoid these if statements?
    public Generator generate(GenerateMessage message) throws IOException, ParserConfigurationException {

        // TODO: put in error handling to ensure the Configuration is set before running the Generator.

        /* A new PageDescriptor is created for each page or hints file scanned.
           The PageDescriptor is then used to name the class name in the code bucket when generating code or in the
           hints file when generating hints.
         */
        PageDescriptor pageDescriptor;
        classNameRecorder = new NameRecorder("Class Name Recorder");

        // Generate the hints or code output.

        if (message.getGenerateType() == GenerateMessage.GenerateType.HINTS) {

            PageScanner pageScanner = new PageScanner(message.getUrl());
            pageDescriptor = pageScanner.getPageName(classNameRecorder);

            // Scan the DOM to get a list of tags and their attributes.
            TagDescriptorList tagDescriptorList = pageScanner.scan();
            writeHintsFromTagDescriptors(pageDescriptor, tagDescriptorList);

        }
        /* Possible Design Pattern: This condition, and the one above, can both call a outputbucket.writeContents()
            method, with the same params and the same param setup code.  The only difference is the bucket used.
           So, I think a Builder Pattern or Factory Pattern could be used to build the appropriate bucket based
           on the Configurator's generate-status.  I could also pass in a Scanner object maybe which covers the Hints Scanning case also.
         */
        else if (message.getGenerateType() == GenerateMessage.GenerateType.CODE) {

            PageScanner pageScanner = new PageScanner(message.getUrl());
            pageDescriptor = pageScanner.getPageName(classNameRecorder);

            // Scan the nodes
            TagDescriptorList tagDescriptorList = pageScanner.scan();
            writeCodeFromTagDescriptors(pageDescriptor, tagDescriptorList);

        }
        /* Possible Design Pattern? --> This condition is also similar, but the difference is the Scanner used.
        */
        else if (message.getGenerateType() == GenerateMessage.GenerateType.CODE_FROM_HINTS) {

            // TODO:  Hints scanner will also need to be passed the URL.
            pageDescriptor = HintsScanner.getScanner().setPageName(classNameRecorder);

            TagDescriptorList tagDescriptorList = HintsScanner.getScanner().scan();
            writeCodeFromTagDescriptors(pageDescriptor, tagDescriptorList);

        }
        else {
            throw new PageHelperException("Invalid configuration state.  Should never get here.");
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
            throw new PageHelperException("Got null Tag Descriptor List--cannot generate code or hints.");
        }

        if (tagDescriptorList.size() == 0) {
            throw new PageHelperException("Tag Descriptor List is empty--cannot generate code or hints.");
        }
    }
}
