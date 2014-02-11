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
    private static PageSourceParser pageSourceParser = null;


    public static void main(String[] args) throws IOException, ParserConfigurationException {

        // Used by the loggers
        PropertyConfigurator.configure("log4j.properties");

        // Sets the configuration using any command-line parameters
        configurator = Configurator.getConfigurator(args);

        // Process the TagDescriptorList here to generate the analysis or code output (or both?).

        if (configurator.getGenerateStatus() == Configurator.GenerateType.HINTS) {

            // Parses the page source and provides access to the w3c document objects.
            pageSourceParser = new PageSourceParser(configurator.getUrl());

            HintsBucket hintsBucket = new HintsBucket();

            TagDescriptorList tagDescriptorList = scanDOMsTags();
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
            pageSourceParser = new PageSourceParser(configurator.getUrl());

            // CodeBucket accumulates and stores the code prior to writing it out.
            CodeBucket codeBucket = prepareCodeShell();

            TagDescriptorList tagDescriptorList = scanDOMsTags();

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
            // TODO: This shouldn't run the node scanner, but the other two options need it.
            HintsReader hintsReader = new HintsReader();
            hintsReader.openHintsFile();
            hintsReader.loadHints();

            throw new SeleniumGeneratorException("Generation from analysis file is not yet implemented.");
        }
        else {
            throw new SeleniumGeneratorException("Invalid configuration option.");
        }

        logger.info("SUCCESS");

    }


    private static CodeBucket prepareCodeShell() throws IOException, ParserConfigurationException {

        // CodeBucket accumulates and stores the code prior to writing it out.
        CodeBucket codeBucket = new CodeBucket();

        // NOTE: The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
        NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");

        // Pre-process the CodeShell using info from the Document's page source and use this to store a description of
        // the page.
        PageDescriptor pageObjectDescriptor = new PageDescriptor(pageSourceParser.getDom(), classNameRecorder);
        pageObjectDescriptor.setPageObjectName(codeBucket);

        return codeBucket;
    }


    private static TagDescriptorList scanDOMsTags() throws IOException, ParserConfigurationException {

        // Load a Lookup 'switcher' data-structure from the config file that defines the tag-->code translations.
        TagSwitcher tagSwitcher = new TagSwitcher(configurator);

        // Now -- Scan the nodes
        NodeScanner nodeScanner = new NodeScanner(tagSwitcher);

        Node root = pageSourceParser.getRootNode();
        logger.info("Root Node is: " + root.getNodeName() + "-- value: " + root.getNodeValue());
        return nodeScanner.scanForUIElements(root, 0);
    }

}
