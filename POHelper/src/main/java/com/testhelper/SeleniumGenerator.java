package com.testhelper;

import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import org.w3c.dom.Node;

/**
 * This is the Main Entry Point for the entire app.
 * Creator:  Paul Grandjean
 * Date:  Was first created sometime in 2011 :-)
 * Has since gone through multiple incremental modifications and refactorings.
 */

public class SeleniumGenerator
{

    private static final Logger logger = Logger.getLogger(SeleniumGenerator.class);

    private static Configurator configurator = null;


    public static void main(String[] args) throws IOException, ParserConfigurationException {

        // Used by the loggers
        PropertyConfigurator.configure("log4j.properties");

        // Sets the configuration using any command-line parameters
        configurator = Configurator.getConfigurator(args);

        // Process the TagDescriptorList here to generate the analysis or code output (or both?).

        if (configurator.getGenerateStatus() == Configurator.GenerateType.HINTS) {

            // Parses the page source and provides access to the w3c document objects.
            PageDocument pageDocument = new PageDocument(configurator.getUrl());

            HintsBucket hintsBucket = prepareHints(pageDocument);

            TagDescriptorList tagDescriptorList = scanDOMTags(pageDocument);
            // Write the analysis file.
            for(TagDescriptor tagDescriptor : tagDescriptorList) {
                hintsBucket.addTag(tagDescriptor.getTag());
                hintsBucket.addText(tagDescriptor.getTextValue());
                hintsBucket.addAttribute(tagDescriptor.getAttributes());
                hintsBucket.addCssLocator(tagDescriptor.makeCssLocator());
            }

            // Dump the hints file.
            // TODO:  Create another version of analysis.createOutputFile() that takes no params and uses the defaults.
            hintsBucket.createOutputFile(null);
            hintsBucket.dumpToFile();
            hintsBucket.closeOutputFile();

        }
        else if (configurator.getGenerateStatus() == Configurator.GenerateType.CODE) {

            // Parses the page source and provides access to the w3c document objects.
            PageDocument pageDocument = new PageDocument(configurator.getUrl());

            // CodeBucket accumulates and stores the code prior to writing it out.
            CodeBucket codeBucket = prepareCodeShell(pageDocument);

            TagDescriptorList tagDescriptorList = scanDOMTags(pageDocument);

            // Write the member code to the code buffer.
            for(TagDescriptor tagDescriptor : tagDescriptorList) {
                    codeBucket.addCode(tagDescriptor.getComment());
                    codeBucket.addCode(tagDescriptor.getMemberCode());
            }

            // Write the method code to the code buffer.
            for(TagDescriptor tagDescriptor : tagDescriptorList) {
                    codeBucket.addCode(tagDescriptor.getMethodCode());
            }

            // Dump the  sourcecode.
            codeBucket.dumpToFile(configurator.getDestinationFilePath());

        }
        else if (configurator.getGenerateStatus() == Configurator.GenerateType.CODE_FROM_HINTS) {

            HintsDocument hintsDocument = new HintsDocument();
            // TODO: Use the Configurator to pass in the filepath to the hints file.
            hintsDocument.loadHints();

            // CodeBucket accumulates and stores the code prior to writing it out.
            CodeBucket codeBucket = prepareCodeShell(hintsDocument);

            // TODO: Call scanHintsDoc() from here.

            throw new SeleniumGeneratorException("Generation from hints file is not yet implemented.");
        }
        else {
            throw new SeleniumGeneratorException("Invalid configuration option.");
        }

        logger.info("SUCCESS");

    }

    /* Right now this is to make the code above consisent between if statements, but it also is a stub allowing for
       future expansion to do some pre-processing on the hints, for example, for hints on the classname.
    */
    private static HintsBucket prepareHints(PageDocument pageDocument) {
        return new HintsBucket();
    }



    private static CodeBucket prepareCodeShell(PageDocument pageDocument) throws IOException, ParserConfigurationException {

        // CodeBucket accumulates and stores the code prior to writing it out.
        CodeBucket codeBucket = new CodeBucket();

        // TODO: The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
        NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");

        // Pre-process the CodeShell using info from the Document's page source and use this to store a description of
        // the page.
        // TODO: just pass the page document and call getDom inside the PageDescriptor
        PageDescriptor pageObjectDescriptor = new PageDescriptor();
        pageObjectDescriptor.setPageObjectName(pageDocument, classNameRecorder, codeBucket);

        return codeBucket;
    }

    private static CodeBucket prepareCodeShell(HintsDocument hintsDocument) throws IOException {

            // CodeBucket accumulates and stores the code prior to writing it out.
            CodeBucket codeBucket = new CodeBucket();

            // TODO: The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
            NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");

            // Pre-process the CodeShell using info from the Hints Document and use this to store a description of
            // the page.
            // TODO:  The Hints File needs a way of storing the classname.
            PageDescriptor pageObjectDescriptor = new PageDescriptor();
            pageObjectDescriptor.setPageObjectName(hintsDocument, classNameRecorder, codeBucket);

            return codeBucket;
        }

    private static TagDescriptorList scanDOMTags(PageDocument pageDocument) throws IOException {

        // Load a Lookup 'switcher' data-structure from the config file that defines the tag-->code translations.
        TagSwitcher tagSwitcher = new TagSwitcher(configurator);

        // Now -- Scan the nodes
        NodeScanner nodeScanner = new NodeScanner(tagSwitcher);

        Node root = pageDocument.getRootNode();
        logger.info("Root Node is: " + root.getNodeName() + "-- value: " + root.getNodeValue());
        return nodeScanner.scanForUIElements(root, 0);
    }


    private static TagDescriptorList scanHintsFile(HintsDocument hintsDocument) throws IOException {

            // Load a Lookup 'switcher' data-structure from the config file that defines the tag-->code translations.
            TagSwitcher tagSwitcher = new TagSwitcher(configurator);

            // Now -- Scan the nodes
//            HintsScanner hintsScanner = new HintsScanner(tagSwitcher);
//
//            Node root = hintsDocument.getRootNode();
//            logger.info("Root Node is: " + root.getNodeName() + "-- value: " + root.getNodeValue());
//            return hintsScanner.scanForUIElements(root, 0);
        // TODO: Remove this return stub when HintScanner is created.
        return new TagDescriptorList();
    }

}
