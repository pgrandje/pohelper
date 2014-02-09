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


    public static void main(String[] args) throws IOException, ParserConfigurationException {

        // Used by the loggers
        PropertyConfigurator.configure("log4j.properties");

        // Sets the configuration using any command-line parameters
        Configurator configurator = Configurator.getConfigurator(args);

        // Parses the page source and provides access to the w3c document objects.
        PageSourceParser pageSourceParser = new PageSourceParser(configurator.getUrl());

        // CodeBucket accumulates and stores the code prior to writing it out.
        CodeBucket codeBucket = new CodeBucket();

        HintsBucket analysisBucket = new HintsBucket();

        // NOTE: The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
        NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");


        // Load a Lookup 'switcher' data-structure from the config file that defines the tag-->code translations.
        TagSwitcher tagSwitcher = new TagSwitcher(configurator);


        // Pre-process the CodeShell using info from the Document's page source and use this to store a description of
        // the page.
        PageDescriptor pageObjectDescriptor = new PageDescriptor(pageSourceParser.getDom(), classNameRecorder);
        pageObjectDescriptor.setPageObjectName(codeBucket);

        /* TODO: The NodeScanner, and several of the above objects, should be in the generate section below.
                 You don't want to scan the nodes when you're generating from the HINTS FILE.
                 But I do need a new TagDescriptorList that's built from the Hints file.  Is that correct?
        */
        // Now -- Scan the nodes
        NodeScanner nodeScanner = new NodeScanner(tagSwitcher);

        Node root = pageSourceParser.getRootNode();
        logger.info("Root Node is: " + root.getNodeName() + "-- value: " + root.getNodeValue());
        TagDescriptorList tagDescriptorList = nodeScanner.scanForUIElements(root, 0);




        // Process the TagDescriptorList here to generate the analysis or code output.

        if (configurator.getGenerateStatus() == Configurator.GenerateType.HINTS_ONLY) {

            // Write the analysis file.
            for(TagDescriptor tagDescriptor : tagDescriptorList) {
                analysisBucket.addTag(tagDescriptor.getTag());
                analysisBucket.addText(tagDescriptor.getTextValue());
                analysisBucket.addAttribute(tagDescriptor.getAttributes());
                analysisBucket.addCssLocator(tagDescriptor.makeCssLocator());
            }

            // Dump the analysis file.
            analysisBucket.createOutputFile("./Analysis.txt");
            analysisBucket.dumpToFile();
            analysisBucket.closeOutputFile();

        }
        else if (configurator.getGenerateStatus() == Configurator.GenerateType.CODE) {

            // Write the member code to the code buffer.
            for(TagDescriptor tagDescriptor : tagDescriptorList) {
                    codeBucket.addCode(tagDescriptor.getComment());
                    codeBucket.addCode(tagDescriptor.getMemberCode());
            }

            // Write the method code to the code buffer.
            for(TagDescriptor tagDescriptor : tagDescriptorList) {
                    codeBucket.addCode(tagDescriptor.getMethodCode());
            }

            // Dump the generated sourcecode.
            codeBucket.dumpToFile(configurator.getDestinationFilePath());

        }
        else if (configurator.getGenerateStatus() == Configurator.GenerateType.CODE_FROM_HINTS) {

            HintsReader analysisReader = new HintsReader();
            analysisReader.openHintsFile();
            analysisReader.loadAnalysis();

            // TODO: Return a TagDescriptorList from the processing of the hints file--here.

            throw new SeleniumGeneratorException("Generation from analysis file is not yet implemented.");
        }
        else {
            throw new SeleniumGeneratorException("Invalid configuration state.  Should never get here.");
        }

        logger.info("SUCCESSFUL COMPLETION");

    }
}
