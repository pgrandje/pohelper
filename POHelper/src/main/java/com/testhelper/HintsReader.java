package com.testhelper;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads in the Hints file for the Generator when the modified Hints file is used for code generation.
 * User: pgrandje
 * Date: 9/9/12
 */
public class HintsReader {

    private final Logger logger = Logger.getLogger(this.getClass());

    // IntelliJ thinks filePath isn't used but it is in the open() method.
    private String filePath;
    private String defaultFilePath = "./analysis.txt";
    private BufferedReader hintsFile;

    private final String recordDelimeter = "<*** New Tag ***>";


    HintsReader() {}


    public void openHintsFile() throws FileNotFoundException {
        openHintsFile(null);
    }


    public void openHintsFile(String filePath) throws FileNotFoundException {

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


    public HintsDescriptorList loadAnalysis() throws IOException {

        String currentTag = null;

        String line = hintsFile.readLine();

        HintsDescriptorList hintsDescriptorList = new HintsDescriptorList();

        // There shouldn't be any blank lines in this file, so we'll treat that as end of file.
        while (line != null){

            logger.debug("Processing line: " + line);

            // Check for new record delimiter
            if (line.contains(HintsDescriptor.NEW_TAG_DELIMITER)) {

                logger.debug("Processing a new tag.");

                // Get the first field, which contains the tag.
                line = hintsFile.readLine();

                // Check whether tag should be skipped, if so, skip all lines up to the next record, and re-loop.
                if (line.charAt(0) == HintsDescriptor.IGNORE_CHAR) {
                    logger.debug("Record marked as skipped.");
                    do {
                        line = hintsFile.readLine();
                    } while ((line != null) && (!line.contains(HintsDescriptor.NEW_TAG_DELIMITER)));
                    continue;
                }
                else {
                    // Once it gets here, we have the tag field for a record that should get generated.
                    // TODO:  put in exceptions for when I don't find what I'm expecting to find.

                    HintsDescriptor analysisDescriptor = new HintsDescriptor();

                    // Store the tag, it has already been read above.
                    analysisDescriptor.setTag(line);

                    // Read and store the text if we find it in the analysis.
                    line = hintsFile.readLine();
                    // TODO: the Text field should always follow, change 'if' to throw an exception if it's not found.
                    if (!line.startsWith(HintsDescriptor.TEXT_MARKER)) {
                        throw new SeleniumGeneratorException("Expected text filed in hints file not found.");
                    }

                    String text = line.substring(HintsDescriptor.TEXT_MARKER.length());
                    logger.debug("Retrieved text '" + text + "'.");
                    analysisDescriptor.setText(text);
                    line = hintsFile.readLine();

                    // Read and store the list of attributes if we have them in the analysis.
                    while (line.startsWith(HintsDescriptor.ATTRIBUTE_MARKER)) {
                        String attributePair = line.substring(HintsDescriptor.ATTRIBUTE_MARKER.length());
                        logger.debug("Found attribute line '" + attributePair + "'.");
                        // TODO: Simply attribute format in hints file.
                        // Attribute format: Type = class -- value = no-pad-left
                        String[] attrComponents = attributePair.split(" -- ");
                        String attrName = attrComponents[0].replace("Type = ", "");
                        String attrValue = attrComponents[1].replace("value = ", "");
                        HintsAttribute hintsAttribute = new HintsAttribute();

                        logger.debug("Storing attribute with name '" + attrName + "' and value '" + attrValue + "'");
                        hintsAttribute.setAttributeName(attrName);
                        hintsAttribute.setAttributeValue(attrValue);

                        // Add the hintsAttribute to the hintsDescriptor.
                        analysisDescriptor.addAttribute(hintsAttribute);

                        line = hintsFile.readLine();
                    }

                    // Read and store the css locator if we have one in the analysis.
                    if (line.contains(HintsDescriptor.LOCATOR_MARKER)) {
                        String locator = line.substring(HintsDescriptor.LOCATOR_MARKER.length());
                        logger.debug("Found locator '" + locator + "'.");
                        // TODO: Why am I only writing css locators to the analysis file?
                        analysisDescriptor.setLocatorType(HintsDescriptor.LocatorType.CSS_LOCATOR);
                        analysisDescriptor.setLocatorValue(locator);
                        line = hintsFile.readLine();
                    }

                    hintsDescriptorList.add(analysisDescriptor);

                }

            }


            line = hintsFile.readLine();

        }

        return hintsDescriptorList;

    }

}
