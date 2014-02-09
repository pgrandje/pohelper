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

        HintsDescriptorList analysisDescriptorList = new HintsDescriptorList();

        while (line != null){

            // There shouldn't be any blank lines in this file, so we'll treat that as an error.

            // Check for new record delimeter
            if (line.contains("<*** New Tag ***>")) {

                logger.debug("Processing a new tag.");

                // Get the first field, which contains the tag.
                line = hintsFile.readLine();

                // Check whether tag should be skipped, if so, skip all lines up to the next record, and re-loop.
                if (line.charAt(0) == '*') {
                    logger.debug("Record marked as skipped.");
                    do {
                        line = hintsFile.readLine();
                    } while ((line != null) && (!line.contains("<*** New Tag ***>")));
                    continue;
                }
                // Once it gets here, we have the tag field for a record that should get generated.
                else {

                    // TODO:  put in exceptions for when I don't find what I'm expecting to find.

                    HintsDescriptor analysisDescriptor = new HintsDescriptor();

                    // Store the tag, it has already been read above.
                    String[] linePieces = line.split(": ");
                    String tag = linePieces[1];
                    logger.debug("Found tag: " + tag);
                    analysisDescriptor.setTag(tag);

                    // Read and store the text if we find it in the analysis.
                    line = hintsFile.readLine();
                    if (line.contains("Text:")) {
                        linePieces = line.split(": ");
                        String text = linePieces[1];
                        logger.debug("Found text: " + text);
                        analysisDescriptor.setText(text);
                        line = hintsFile.readLine();
                    }

                    // Read and store the list of attributes if we have them in the analysis.
                    while (line.contains("Attr:")) {
                        linePieces = line.split(": ");
                        String attrNameValuePair = linePieces[1];
                        logger.debug("Found attribute name and value: " + attrNameValuePair);
                        String[] attrComponents = attrNameValuePair.split(" -- ");
                        HintsAttribute analysisAttribute = new HintsAttribute();
                        String attrName = attrComponents[0].split(" = ")[1];
                        analysisAttribute.setAttributeName(attrName);
                        String attrValue = attrComponents[1].split(" = ")[1];
                        analysisAttribute.setAttributeValue(attrValue);
                        line = hintsFile.readLine();
                    }

                    // Read and store the css locator if we have one in the analysis.
                    if (line.contains("Css Locator:")) {
                        linePieces = line.split(": ");
                        String cssLocatorString = linePieces[1];
                        logger.debug("Found css locator string: " + cssLocatorString);
                        // TODO: Why am I only writing css locators to the analysis file?
                        analysisDescriptor.setLocatorType(HintsDescriptor.LocatorType.CSS_LOCATOR);
                        analysisDescriptor.setLocatorValue(cssLocatorString);
                        line = hintsFile.readLine();
                    }

                    analysisDescriptorList.add(analysisDescriptor);

                }

            }


            line = hintsFile.readLine();

        }

        return analysisDescriptorList;

    }

}
