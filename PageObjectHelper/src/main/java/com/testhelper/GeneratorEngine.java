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

            HintsBucket hintsBucket = new HintsBucket();

            // Parses the page source and provides the root node to the DOM.
            // TODO: To generate hints we still need to get the page name so we can add this at the top of the hints file.
            PageSourceParser pageSourceParser = new PageSourceParser();

            // TODO: Hint's generation still needs the PageDescriptor for setting the suggested PageName into the Hints file.

            // Now -- Scan the nodes
            TagDescriptorList tagDescriptorList = PageScanner.getNodeScanner().scan(pageSourceParser.getRootNode());

            // Write the hints file.
            for(TagDescriptor tagDescriptor : tagDescriptorList) {
                hintsBucket.addTag(tagDescriptor.getTag());
                hintsBucket.addText(tagDescriptor.getTextValue());
                hintsBucket.addAttributes(tagDescriptor.getAttributePairs());
                hintsBucket.addLocator(tagDescriptor.getLocatorString());
            }

            // Dump the hints file.
            hintsBucket.createOutputFile("./Hints.txt");
            hintsBucket.dumpToFile();
            hintsBucket.closeOutputFile();

        }
        else if (configurator.getGenerateStatus() == Configurator.GenerateType.CODE) {

            // CodeBucket accumulates and stores the code prior to writing it out.
            CodeBucket codeBucket = new CodeBucket();

            // TODO: Does the PageSourceParser need to be it's own object?  Or, can I use a factory with a fluent pattern?
            // Parses the page source and provides access to the w3c document objects.
            PageSourceParser pageSourceParser = new PageSourceParser();
            // Pre-process the CodeShell using info from the Document's page source and use this to store a description of
            // the page.
            // TODO: When generating from a hints file, I need to start the code shell without needing the remote DOM.
            // TODO: And, when generating the hints file, I need to access the DOM, without generating the code shell.
            // The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
            NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");
            PageDescriptor pageDescriptor = new PageDescriptor(pageSourceParser.getDom(), classNameRecorder);
            pageDescriptor.setPageObjectName(codeBucket);

            // TODO: Fetching and parsing the DOM is identical from the hints generation above, this could be a helper method.

            // TODO: Def should not be scanning the page when generating from the hints file. Hints running should be done without needed remote website.

            // Scan the nodes
            TagDescriptorList tagDescriptorList = PageScanner.getNodeScanner().scan(pageSourceParser.getRootNode());

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

            // CodeBucket accumulates and stores the code prior to writing it out.
            CodeBucket codeBucket = new CodeBucket();

            HintsReader hintsReader = new HintsReader();
            hintsReader.openHintsFile();
            // TODO: Store the class name in the HintsDescriptorList.
            HintsDescriptorList hintsDescriptorList = hintsReader.loadHints();

            // The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
            NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");
            PageDescriptor pageObjectDescriptor = new PageDescriptor(hintsDescriptorList.getPageName(), classNameRecorder);
            pageObjectDescriptor.setPageObjectName(codeBucket);

            TagSwitcher tagSwitcher = new TagSwitcher(configurator);
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
}
