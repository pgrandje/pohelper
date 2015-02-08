package com.pagehelper;

import com.pagehelper.outputbucket.CodeOutputBucket;
import com.pagehelper.outputbucket.HintsOutputBucket;
import com.pagehelper.outputbucket.LinksOutputBucket;
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
    // Generate Types
    public enum GenerateType {CODE, HINTS, CODE_FROM_HINTS, LINKS_ONLY, INTERACTIVE}

    private static final Logger logger = Logger.getLogger(Generator.class);

    // Generator will always be a singleton so we're using the Singleton pattern.
    private static Generator singletonGenerator;

    private PageElementsContainer pageElementsContainer;

    /* Accumulates the classnames used for each page object to ensure uniqueness.
       The classNameRecorder needs to exist, and accumulate page names for all pages generated.
    */
    private static NameRecorder classNameRecorder;


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
    public void generate(URL url, GenerateType generateType) throws PageHelperException {

        // Verify the param values are valid.
        if (url == null) {
            throw new PageHelperException("No URL assigned.");
        }

        if (generateType == null) {
            throw new PageHelperException("No GenerateType assigned.");
        }

        /* A new PageDescriptor is created for each page or hints file scanned.
           The PageDescriptor is then used to name the class name in the code bucket when generating code or in the
           hints file when generating hints.
         */
        // TODO: The switch-case required the page scanner to not be defined within each case, but the hints generation doesn't actually need it.
        PageScanner pageScanner = new PageScanner(url);
        PageDescriptor pageDescriptor;
        classNameRecorder = new NameRecorder("Class Name Recorder");

        // Generate the hints or code output.

        switch (generateType) {

            case HINTS:
            pageDescriptor = pageScanner.getPageName(classNameRecorder);
            // Scan the DOM to get a list of tags and their attributes.
            writeHintsFromTagDescriptors(pageDescriptor, pageScanner.scanPage().getTagDescriptorList());
            break;

        /* Possible Design Pattern: This condition, and the one above, can both call a outputbucket.writeContents()
            method, with the same params and the same param setup code.  The only difference is the bucket used.
           So, I think a Builder Pattern or Factory Pattern could be used to build the appropriate bucket based
           on the Configurator's generate-status.  I could also pass in a Scanner object maybe which covers the Hints Scanning case also.
         */
        case CODE:
            pageDescriptor = pageScanner.getPageName(classNameRecorder);

            // Scan the nodes and write the code.
            writeCodeFromTagDescriptors(pageDescriptor, pageScanner.scanPage().getTagDescriptorList());
            break;

        /* Possible Design Pattern? --> This condition is also similar, but the difference is the Scanner used.
        */
        case CODE_FROM_HINTS:

            pageDescriptor = HintsScanner.getScanner().setPageName(classNameRecorder);

            TagDescriptorList tagDescriptorList = HintsScanner.getScanner().scan();
            writeCodeFromTagDescriptors(pageDescriptor, tagDescriptorList);
            break;

            case LINKS_ONLY:

                pageDescriptor = pageScanner.getPageName(classNameRecorder);
                LinkDescriptorList linkDescriptors = pageScanner.scanPage().getLinkDescriptorList();
                writeLinksInfo(pageDescriptor, linkDescriptors);
                break;

        default:
            throw new PageHelperException("Invalid configuration state.  Should never get here.");
        }  // end switch

        logger.info("SUCCESSFUL COMPLETION");
    }

    /**
     * A new PageDescriptor is created which describes a page to be scanned, specified via a supplied URL.
     * The PageDescriptor is then used to name the classname for the corresponded page object to be generated.
     * @param url for the page to be scanned for generating the PageDescriptor.
     * @return The PageDescriptor.
     */
    public PageDescriptor getPageDescriptor(URL url) throws PageHelperException {

        PageDescriptor pageDescriptor;
        classNameRecorder = new NameRecorder("Class Name Recorder");

        // TODO: Fix this--The PageScanner should only be created once per page, not for the pageDescriptor and again for the Tags and links.
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
    public TagDescriptorList getTagDescriptors(URL url) throws PageHelperException {
        if (pageElementsContainer == null) {
            scanPage(url);
        }
        return pageElementsContainer.getTagDescriptorList();
    }

    /**
     * Generates a list of the links in the requested page.
     * @param url for the page to be scanned for generating the tag descriptors.
     * @return The list of links.
     */
    public LinkDescriptorList getLinkDescriptors(URL url) throws PageHelperException {
        if (pageElementsContainer == null) {
            scanPage(url);
        }
        return pageElementsContainer.getLinkDescriptorList();
    }

    /* Possible Design Pattern: Both writing hints and writing code use a TagDescriptorList and a outputbucket.  But they
       write very different things, and use different buckets.  Yet they both create a type of outputbucket.
       What type of pattern can be used here?
        - Some sort of Adapter that translates TagDescriptors to the correct bucket?
        - Some sort of Builder that returns a specific outputbucket?
        - Each bucket could override a writeBucket content method. Then pass the Code outputbucket in as a specific type of an
            abstract output bucket.
    */
    public void writeCodeFromTagDescriptors(PageDescriptor pageDescriptor, TagDescriptorList tagDescriptorList) throws PageHelperException {

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

    /**
     * Scan's the page and loads the container for the links and tag descriptor lists.
      * @param url
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private void scanPage(URL url) throws PageHelperException {
        // TODO: Fix this--The PageScanner should only be created once per page, not for the pageDescriptor and again for the Tags.
        PageScanner pageScanner = new PageScanner(url);
        // Scan the DOM to get a list of tags and their attributes.
        pageElementsContainer = pageScanner.scanPage();

    }

    private void writeHintsFromTagDescriptors(PageDescriptor pageDescriptor, TagDescriptorList tagDescriptorList) throws PageHelperException {

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

    private void writeLinksInfo(PageDescriptor pageDescriptor, LinkDescriptorList linkDescriptorlist) {

        verifyLinkDescriptorList(linkDescriptorlist);

        LinksOutputBucket linksBucket = LinksOutputBucket.getBucket();
        linksBucket.setFilePath();
        linksBucket.setFileName(pageDescriptor.getPageObjectName());
        linksBucket.setPageObjectName(pageDescriptor.getPageObjectName());

        // Write the links file.
        for(LinkDescriptor linkDescriptor : linkDescriptorlist) {
            linksBucket.addLinkHref(linkDescriptor.getUrl());
            linksBucket.addLinkText(linkDescriptor.getText());
            linksBucket.addAttributes(linkDescriptor.getAttributes());
        }

        // Dump the hints file.
        linksBucket.dumpToFile();


    }

    private void verifyTagDescriptorList(TagDescriptorList tagDescriptorList) {

        if (null == tagDescriptorList) {
            throw new PageHelperException("Got null Tag Descriptor List--cannot generate code or hints.");
        }

        if (tagDescriptorList.size() == 0) {
            throw new PageHelperException("Tag Descriptor List is empty--cannot generate code or hints.");
        }
    }

    private void verifyLinkDescriptorList(LinkDescriptorList linkDescriptors) {

            if (null == linkDescriptors) {
                throw new PageHelperException("Got null Link Descriptor List--cannot write links.");
            }

            if (linkDescriptors.size() == 0) {
                throw new PageHelperException("Link Descriptor List is empty--cannot write links.");
            }
        }
}
