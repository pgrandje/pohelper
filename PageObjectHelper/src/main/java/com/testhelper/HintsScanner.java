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
    private final String recordDelimeter = "<*** New Tag ***>";

    // Returns code for a given tag.
    private TagSwitcher tagSwitcher;

    // Records names used for members to avoid duplicates.
    private NameRecorder memberNameRecorder;

    private TagDescriptorList tagDescriptorList;


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
        this.tagDescriptorList = new TagDescriptorList();
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


    public TagDescriptorList scan() throws IOException {

        String currentTag = null;

        String line = hintsFile.readLine();

        TagDescriptorList tagDescriptorList = new TagDescriptorList();


        if (line.contains(HintsDescriptor.PAGE_MARKER)) {
            String pageName = line.substring(HintsDescriptor.PAGE_MARKER.length());
            logger.debug("Page name is: " + pageName);
            tagDescriptorList.setPageName(pageName);
        }
        else {
            throw new SeleniumGeneratorException("Page name not found in Hints file.");
        }


        // There shouldn't be any blank lines in this file, so we'll treat that as end of file.
        while (line != null){

            logger.debug("Processing line: " + line);

            // Check for new record delimiter
            if (line.contains(HintsDescriptor.NEW_TAG_DELIMITER)) {

                logger.debug("Processing a new tag:");

                // Get the first field, which contains the tag.
                line = hintsFile.readLine();
                logger.debug("Processing new tag on line: " + line);

                // Check whether tag should be skipped, if so, skip all lines up to the next record, and re-loop.
                if (line.charAt(0) != HintsDescriptor.IGNORE_CHAR) {
                    logger.trace("Skipping lines:");
                    do {
                        logger.trace(line);
                        line = hintsFile.readLine();
                    } while ((line != null) && (!line.contains(HintsDescriptor.NEW_TAG_DELIMITER)));
                    continue;
                }
                else {

                    // Store the tag, it has already been read above.
                    String tag = line.replace("*", "").trim();

                    TagDescriptor tagDescriptor = new TagDescriptor(tagSwitcher.getTemplate(tag));

                    // Read and store the text if we find it in the analysis.
                    line = hintsFile.readLine();
                    // The Text field should always follow; throw an exception if it's not found.
                    if (!line.startsWith(HintsDescriptor.TEXT_MARKER)) {
                        throw new SeleniumGeneratorException("Expected text marker not found in hints file.");
                    }

                    String text = line.substring(HintsDescriptor.TEXT_MARKER.length());
                    logger.debug("Retrieved text '" + text + "'.");
                    tagDescriptor.setTextValue(text);
                    line = hintsFile.readLine();


                    // Read and store the list of attributes if we have them in the analysis.

                    HashMap<String, String> attributes = new HashMap<String, String>();

                    while (line.startsWith(HintsDescriptor.ATTRIBUTE_MARKER)) {

                        String[] attrComponents = line.substring(HintsDescriptor.ATTRIBUTE_MARKER.length()).split(" = ");

                        // TODO: Simplify attribute format in hints file.
                        // Attribute format: class = value where value could be null.
                        // TODO: Make the Hints file not have an '=' when the attribute value is missing.

                        if (attrComponents[0] == null) {
                            throw new SeleniumGeneratorException("Hints file has Attribute record with no attribute.");
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
                    if (line.contains(HintsDescriptor.LOCATOR_MARKER)) {
                        String locatorString = line.substring(HintsDescriptor.LOCATOR_MARKER.length());
                        logger.debug("Found locator string '" + locatorString + "'.");

                        Locator locator = LocatorFactory.createLocator(locatorString);
                        tagDescriptor.writeLocatorString(locator);

                        line = hintsFile.readLine();
                    }

                    tagDescriptorList.add(tagDescriptor);

                }

            }


            line = hintsFile.readLine();

        }

        return tagDescriptorList;

    }

}
