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

            // TODO: Create a helper method -- These 3 lines are the same in each branch except for the Bucket used.
            // Pre-process using info from the Document's page source and use this to store a description of the page.
            // The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
            NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");
            PageDescriptor pageDescriptor = new PageDescriptor(PageScanner.getScanner().getDom(), classNameRecorder);
            pageDescriptor.setPageObjectName(hintsBucket);

            // Now -- Scan the nodes
            TagDescriptorList tagDescriptorList = PageScanner.getScanner().scan();

            // Write the hints file.
            for(TagDescriptor tagDescriptor : tagDescriptorList) {
                hintsBucket.addTag(tagDescriptor.getTag());
                hintsBucket.addText(tagDescriptor.getTextValue());
                hintsBucket.addAttributes(tagDescriptor.getAttributePairs());
                hintsBucket.addLocator(tagDescriptor.getLocatorString());
            }

            // TODO: Hints file name should only come from the Configurator
            // TODO: Merge these 3 methods into one -- HintsBucket.writeHintsFile()
            // Dump the hints file.
            hintsBucket.createOutputFile("./Hints.txt");
            hintsBucket.dumpToFile();
            hintsBucket.closeOutputFile();

        }
        else if (configurator.getGenerateStatus() == Configurator.GenerateType.CODE) {

            // Pre-process using info from the Document's page source and use this to store a description of the page.
            // The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
            NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");
            PageDescriptor pageDescriptor = new PageDescriptor(PageScanner.getScanner().getDom(), classNameRecorder);
            pageDescriptor.setPageObjectName(CodeBucket.getBucket());

            // Scan the nodes
            TagDescriptorList tagDescriptorList = PageScanner.getScanner().scan();

            writeCodeFromTagDescriptors(tagDescriptorList);

        }
        else if (configurator.getGenerateStatus() == Configurator.GenerateType.CODE_FROM_HINTS) {

            HintsReader hintsReader = new HintsReader();
            hintsReader.openHintsFile();
            // TODO: Store the class name in the HintsDescriptorList.
            HintsDescriptorList hintsDescriptorList = hintsReader.loadHints();

            // The Class Name Recorder will need to be available for all generated classes when I'm crawling a site.
            NameRecorder classNameRecorder = new NameRecorder("Class Name Recorder");
            PageDescriptor pageObjectDescriptor = new PageDescriptor(hintsDescriptorList.getPageName(), classNameRecorder);
            pageObjectDescriptor.setPageObjectName(CodeBucket.getBucket());

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

            writeCodeFromTagDescriptors(tagDescriptorList);

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

}
