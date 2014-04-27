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

public class GeneratorEngine
{

    private static final Logger logger = Logger.getLogger(GeneratorEngine.class);
    private static Configurator configurator = null;

    public static void main(String[] args) throws IOException, ParserConfigurationException {

        setUpConfiguration(args);

        // Parses the page source and provides access to the w3c document objects.
        // TODO: Some of these actions should not be run when generating code from the hints file.
        // TODO: Does the PageSourceParser need to be it's own object?  Or, can I use a factory with a fluent pattern?
        PageSourceParser pageSourceParser = new PageSourceParser(configurator.getUrl());

        // CodeBucket accumulates and stores the code prior to writing it out.
        CodeBucket codeBucket = new CodeBucket();

        // NOTE: The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
        NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");


        // Load a Lookup 'switcher' data-structure from the config file that defines the tag-->code translations.
        TagSwitcher tagSwitcher = new TagSwitcher(configurator);


        // Pre-process the CodeShell using info from the Document's page source and use this to store a description of
        // the page.
        // TODO: When generating from a hints file, I need to start the code shell without needing the remote DOM.
        // TODO: And, when generating the hints file, I need to access the DOM, without generating the code shell.
        PageDescriptor pageObjectDescriptor = new PageDescriptor(pageSourceParser.getDom(), classNameRecorder);
        pageObjectDescriptor.setPageObjectName(codeBucket);


        // Process the TagDescriptorList here to generate the analysis or code output.

        if (configurator.getGenerateStatus() == Configurator.GenerateType.HINTS_ONLY) {

            // Now -- Scan the nodes
            NodeScanner nodeScanner = new NodeScanner(tagSwitcher);

            HintsBucket hintsBucket = new HintsBucket();

            // TODO: Def should not be scanning the page when generating from the hints file. Hints running should be done without needed remote website.
            Node root = pageSourceParser.getRootNode();
            logger.debug("Root Node is: " + root.getNodeName() + "-- value: " + root.getNodeValue());
            TagDescriptorList tagDescriptorList = nodeScanner.scanForUIElements(root, 0);

            // Write the analysis file.
            for(TagDescriptor tagDescriptor : tagDescriptorList) {
                hintsBucket.addTag(tagDescriptor.getTag());
                hintsBucket.addText(tagDescriptor.getTextValue());
                hintsBucket.addAttributes(tagDescriptor.getAttributePairs());
                hintsBucket.addLocator(tagDescriptor.getLocatorString());
            }

            // Dump the analysis file.
            hintsBucket.createOutputFile("./Hints.txt");
            hintsBucket.dumpToFile();
            hintsBucket.closeOutputFile();

        }
        else if (configurator.getGenerateStatus() == Configurator.GenerateType.CODE) {

            // TODO: Fetching and parsing the DOM is identical from the hints generation above, this could be a helper method.
            // Now -- Scan the nodes
            NodeScanner nodeScanner = new NodeScanner(tagSwitcher);

            // TODO: Def should not be scanning the page when generating from the hints file. Hints running should be done without needed remote website.
            Node root = pageSourceParser.getRootNode();
            logger.debug("Root Node is: " + root.getNodeName() + "-- value: " + root.getNodeValue());
            TagDescriptorList tagDescriptorList = nodeScanner.scanForUIElements(root, 0);

            // TODO: The code generation here is copied in the generate from hints section also--put this in a helper method.
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

            HintsReader hintsReader = new HintsReader();
            hintsReader.openHintsFile();
            HintsDescriptorList hintsDescriptorList = hintsReader.loadAnalysis();

            NameRecorder memberNameRecorder = new NameRecorder("Member Name Recorder");

            // Currently I just use the tagSwitcher since it's global to main()
            TagDescriptorList tagDescriptorList = new TagDescriptorList();
            for(HintsDescriptor hintsDescriptor: hintsDescriptorList) {
                TagTemplate tagTemplate = tagSwitcher.getTemplate(hintsDescriptor.getTag());
                TagDescriptor tagDescriptor = new TagDescriptor(tagTemplate, hintsDescriptor);
                // TODO: If the tagDescriptor has already been passed the hintsDescriptor with it's stored Locator, this call to writeLocatorString() could be done internally.
                tagDescriptor.writeLocatorString(hintsDescriptor.getLocator());
                tagDescriptor.writeMemberAndMethods(memberNameRecorder);
                tagDescriptorList.add(tagDescriptor);
            }

            // TODO: The code generation here is copied in sections above also--put this in a helper method.
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
        else {
            throw new SeleniumGeneratorException("Invalid configuration state.  Should never get here.");
        }

        logger.info("SUCCESSFUL COMPLETION");

    }

    private static void setUpConfiguration(String[] args) {

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
}
