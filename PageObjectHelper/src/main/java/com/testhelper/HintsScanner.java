package com.testhelper;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Reads in the Hints file for the Generator when the modified Hints file is used for code generation.
 * User: pgrandje
 * Date: 9/9/12
 */
public class HintsScanner {

    private final Logger logger = Logger.getLogger(this.getClass());

    private static HintsScanner scanner = null;

    // IntelliJ thinks filePath isn't used but it is in the open() method.
    private String filePath;
    private String defaultFilePath = "./hints.txt";
    private BufferedReader hintsFile;

    // Returns code for a given tag.
    private TagSwitcher tagSwitcher;

    // Records names used for members to avoid duplicates.
    private NameRecorder memberNameRecorder;



    // PageScanner is a singleton since we would only ever need one at a time.
    public static HintsScanner getScanner()  throws IOException {
        if (scanner == null) {
            scanner = new HintsScanner();
        }
        return scanner;
    }

    // TagSwitcher throws the IOException when it can't find it's configuration file.
    private HintsScanner() throws IOException {

        // Load a Lookup 'switcher' data-structure from the config file that defines the tag-->code translations.
        this.tagSwitcher = new TagSwitcher(Configurator.getConfigurator());;
        this.memberNameRecorder = new NameRecorder("Member Name Recorder");
        openHintsFile();
    }


    private void openHintsFile() throws FileNotFoundException {
        openHintsFile(null);
    }

    // TODO: Get hints file path from Configurator
    private void openHintsFile(String filePath) throws FileNotFoundException {

        try {
            if (filePath == null) {
                filePath = defaultFilePath;
            }
            hintsFile = new BufferedReader(new FileReader(filePath));
        }
        catch (FileNotFoundException fileNotFoundException) {
            throw fileNotFoundException;
        }

    }


    public PageDescriptor setPageName(NameRecorder classNameRecorder) throws IOException {

        PageDescriptor pageDescriptor = null;

        String line = hintsFile.readLine();

        if (line.contains(HintsFileDelimeters.PAGE_MARKER)) {
            String title = line.substring(HintsFileDelimeters.PAGE_MARKER.length());
            String pageName = classNameRecorder.makeSymbolName(title);
            logger.debug("Page name is: " + pageName);
            pageDescriptor = new PageDescriptor(pageName);
        }
        else {
            throw new TestHelperException("Page name not found in Hints file.");
        }

        return pageDescriptor;
    }



    public TagDescriptorList scan() throws IOException {

        String line = hintsFile.readLine();

        if (line.contains(HintsFileDelimeters.PAGE_MARKER)) {
            throw new TestHelperException("Reading from first line of hints file but page name should have been read already.");
        }

        TagDescriptorList tagDescriptorList = new TagDescriptorList();

        // There shouldn't be any blank lines in this file, so we'll treat that as end of file.
        while (line != null){

            logger.trace("Processing line: " + line);

            // Check for new record delimiter
            if (line.contains(HintsFileDelimeters.NEW_TAG_DELIMITER)) {

                logger.trace("Processing a new tag:");

                // Get the first field, which contains the tag.
                line = hintsFile.readLine();

                // Check whether tag should be skipped, if so, skip all lines up to the next record, and re-loop.
                if (line.charAt(0) != HintsFileDelimeters.IGNORE_CHAR) {
                    logger.trace("Skipping lines:");
                    do {
                        logger.trace(line);
                        line = hintsFile.readLine();
                    } while ((line != null) && (!line.contains(HintsFileDelimeters.NEW_TAG_DELIMITER)));
                    continue;
                }
                else {

                    // Store the tag, it has already been read above.
                    String tag = line.replace("*", "").trim();

                    TagDescriptor tagDescriptor = new TagDescriptor(tagSwitcher.getTemplate(tag));

                    // Read and store the text if we find it in the analysis.
                    line = hintsFile.readLine();
                    // The Text field should always follow; throw an exception if it's not found.
                    if (!line.startsWith(HintsFileDelimeters.TEXT_MARKER)) {
                        throw new TestHelperException("Expected text marker not found in hints file.");
                    }

                    String text = line.substring(HintsFileDelimeters.TEXT_MARKER.length());
                    logger.debug("Retrieved text '" + text + "'.");
                    tagDescriptor.setTextValue(text);
                    line = hintsFile.readLine();


                    // Read and store the list of attributes if we have them in the analysis.

                    HashMap<String, String> attributes = new HashMap<String, String>();

                    while (line.startsWith(HintsFileDelimeters.ATTRIBUTE_MARKER)) {

                        String[] attrComponents = line.substring(HintsFileDelimeters.ATTRIBUTE_MARKER.length()).split(" = ");

                        // TODO: Simplify attribute format in hints file.
                        // Attribute format: class = value where value could be null.
                        // TODO: Make the Hints file not have an '=' when the attribute value is missing.

                        if (attrComponents[0] == null) {
                            throw new TestHelperException("Hints file has Attribute record with no attribute.");
                        }

                        logger.debug("Storing attribute '" + attrComponents[0]);
                        if (attrComponents[1] != null) {
                            logger.debug("   .... with value '" + attrComponents[1] + "'");
                        }

                        attributes.put(attrComponents[0], attrComponents[1]);

                        line = hintsFile.readLine();
                    }

                    tagDescriptor.setAttributes(attributes);

                    // Read and store the css locator if we have one in the analysis.
                    if (line.contains(HintsFileDelimeters.LOCATOR_MARKER)) {
                        String locatorString = line.substring(HintsFileDelimeters.LOCATOR_MARKER.length());
                        logger.debug("Found locator string '" + locatorString + "'.");

                        Locator locator = LocatorFactory.makeLocator(locatorString);
                        tagDescriptor.setLocator(locator);
                    }
                    else {
                        throw new TestHelperException("Found missing locator in hints file.");
                    }

                    tagDescriptor.writeMemberAndMethods(memberNameRecorder);
                    tagDescriptorList.add(tagDescriptor);

                }

            }


            line = hintsFile.readLine();

        }

        return tagDescriptorList;

    }

}
