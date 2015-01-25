package com.testhelper;

import com.testhelper.outputbucket.CodeOutputBucket;
import com.testhelper.outputbucket.HintsOutputBucket;
import org.apache.log4j.Logger;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;

/**
 * Provides an API for page object code and hints generation.
 * @author Paul Grandjean
 * @since First created sometime in 2011 in Salt Lake City, Utah but with many revisions since that time.  :-)
 * @version 1.0alpha
 */

public class Generator
{
    private static final Logger logger = Logger.getLogger(Generator.class);

    // Generator will always be a singleton so we're using the Singleton pattern.
    private static Generator singletonGenerator;

    // URL
    private URL baseUrlToScan;

    // Generate Types

    public enum GenerateType {
        CODE, HINTS, CODE_FROM_HINTS, ANALYZE_AND_GENERATE }

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
     * @param url the starting page for the generate process
     * @param generateType generate, code, hints, or the code from the hints.
     */
    public void generate(URL url, GenerateType generateType) throws IOException, ParserConfigurationException {
        // TODO:  If I used a strategy pattern my passing in a configuration-strategy, could I avoid these if statements?
        // TODO:  Or, should I break this up into multiple methods, one for each type of code/hints generation?

        // TODO: put in error handling to ensure the Configuration is set before running the Generator.

        /* A new PageDescriptor is created for each page or hints file scanned.
           The PageDescriptor is then used to name the class name in the code bucket when generating code or in the
           hints file when generating hints.
         */
        PageDescriptor pageDescriptor;
        classNameRecorder = new NameRecorder("Class Name Recorder");

        // Generate the hints or code output.

        if (generateType == GenerateType.HINTS) {

            PageScanner pageScanner = new PageScanner(url);
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
        else if (generateType == GenerateType.CODE) {

            PageScanner pageScanner = new PageScanner(url);
            pageDescriptor = pageScanner.getPageName(classNameRecorder);

            // Scan the nodes
            TagDescriptorList tagDescriptorList = pageScanner.scan();
            writeCodeFromTagDescriptors(pageDescriptor, tagDescriptorList);

        }
        /* Possible Design Pattern? --> This condition is also similar, but the difference is the Scanner used.
        */
        else if (generateType == GenerateType.CODE_FROM_HINTS) {

            // TODO:  Hints scanner will also need to be passed the URL.
            pageDescriptor = HintsScanner.getScanner().setPageName(classNameRecorder);

            TagDescriptorList tagDescriptorList = HintsScanner.getScanner().scan();
            writeCodeFromTagDescriptors(pageDescriptor, tagDescriptorList);

        }
        else {
            throw new PageHelperException("Invalid configuration state.  Should never get here.");
        }

        logger.info("SUCCESSFUL COMPLETION");
    }



    /**
     * A new PageDescriptor is created which describes a page to be scanned, specified via a supplied URL.
     * The PageDescriptor is then used to name the classname for the corresponded page object to be generated.
     * @param url for the page to be scanned for generating the PageDescriptor.
     * @return The PageDescriptor.
     */
    public PageDescriptor getPageDescriptor(URL url) throws IOException, ParserConfigurationException {

        PageDescriptor pageDescriptor;
        classNameRecorder = new NameRecorder("Class Name Recorder");

        PageScanner pageScanner = new PageScanner(url);
        pageDescriptor = pageScanner.getPageName(classNameRecorder);

        return pageDescriptor;
    }


    /**
     * Generates a list of tag descriptors from all html tags in the requested page that are 'tags of interest' for
     * potential representation within a generated page object.
     * @param url for the page to be scanned for generating the tag descriptors.
     * @return The list of tag descriptors.
     */
    public TagDescriptorList getTagDescriptors(URL url) throws IOException, ParserConfigurationException {

        PageScanner pageScanner = new PageScanner(url);
        // Scan the DOM to get a list of tags and their attributes.
        TagDescriptorList tagDescriptorList = pageScanner.scan();

        return tagDescriptorList;
    }




    /* Possible Design Pattern: Both writing hints and writing code use a TagDescriptorList and a outputbucket.  But they
       write very different things, and use different buckets.  Yet they both create a type of outputbucket.
       What type of pattern can be used here?
        - Some sort of Adapter that translates TagDescriptors to the correct bucket?
        - Some sort of Builder that returns a specific outputbucket?
        - TODO: Each bucket must override a writeBucket content method?  Then pass the Code outputbucket in as a specific type of an
            abstract output bucket.This is easiest! ****
    */
    public void writeCodeFromTagDescriptors(PageDescriptor pageDescriptor, TagDescriptorList tagDescriptorList) throws IOException {

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


    public void writeHintsFromTagDescriptors(PageDescriptor pageDescriptor, TagDescriptorList tagDescriptorList) throws IOException {

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
        hintsBucket.dumpToFile();

    }


    private void verifyTagDescriptorList(TagDescriptorList tagDescriptorList) {

        if (null == tagDescriptorList) {
            throw new PageHelperException("Got null Tag Descriptor List--cannot generate code or hints.");
        }

        if (tagDescriptorList.size() == 0) {
            throw new PageHelperException("Tag Descriptor List is empty--cannot generate code or hints.");
        }
    }
}
