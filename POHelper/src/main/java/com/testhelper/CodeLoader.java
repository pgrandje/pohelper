package com.testhelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: pgrandje
 * Date: 10/23/11
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class CodeLoader {

    private final Logger logger = Logger.getLogger(this.getClass());
    private Configurator configurator;
    private TagSwitcher tagSwitcher;
    private String filePath;
    private BufferedReader configFile;


    // TODO: Should I consolidate the constructor and the loadConfig() into one method?";
    CodeLoader(TagSwitcher tagSwitcher) throws IOException {

        this.configurator = Configurator.getConfigurator();
        this.tagSwitcher = tagSwitcher;

        try {
            filePath = configurator.getCodeTemplateFilePath();
            if (filePath == null) {
                throw new SeleniumGeneratorException("Found null file path to Tag Switcher config file.");
            }
            logger.debug("Looking for config file: " + filePath);
            logger.debug("Using current working directory: " + System.getProperty("user.dir"));
            configFile = new BufferedReader(new FileReader(filePath));
        }
        catch (FileNotFoundException fileNotFoundException) {
            logger.fatal("File Not Found Exception in: " + fileNotFoundException.getClass());
            logger.fatal("Cause: " + fileNotFoundException.getCause());
            logger.fatal("Message: " + fileNotFoundException.getMessage());
            logger.fatal("Stack Trace: " + fileNotFoundException.getStackTrace());
            throw fileNotFoundException;
        }

    }


    void loadConfig() throws IOException {

      String currentTag = null;
      String currentMemberCode = "";
      String currentMethodCode = "";

      Configurator.CodeTemplateDelimiters fileDelimiters = configurator.getCodeTemplateDelimiters();

      try {

            String line = configFile.readLine();


            while (line != null){

                // For blank lines, just drop out of the if, go to the bottom, and advance the line.
                if (line.isEmpty()) {
                    ;
                }
                // Found a new tag, so starting a new tag-codeblock pair.
                // We'll test the delim using contains() to account for trailing whitespace.
                else if (line.contains(fileDelimiters.tagDelimeter)) {

                    logger.debug("Found a new tag");

                    // Get the tag
                    line = configFile.readLine();
                    logger.debug("Reading tag " + line);

                    // This expects the file's string to be a tag, if I need to generalize beyond tags this would be
                    // where to start making the changes for reading the new info, along with redesigning the TagSwitcher to go beyond tags.
                    // I include the <> brackets in the lookup table to distinguish tags from attribs.
                    currentTag = line;

                }

                // Process a Code Block
                else if (line.contains(fileDelimiters.codeBlockBeginDelimeter)) {

                    logger.debug("Processing tag's code-block.");
                    line = configFile.readLine();

                    // clear the code snippet for each new occurrence.
                    currentMemberCode = "";
                    currentMethodCode = "";

                    // Note: You should always have both member code and method code in the config file, otherwise it's an error.
                    // And the member code should always precede the method code.

                    // At this point we should be at the begin of a member code block, otherwise it's an error.
                    //      If it's correct, advance to the next line for loading the code.
                    if (line.contains(fileDelimiters.memberDelimeter)) {
                        // advance the line to ensure we don't addCode the delimiter itself to the code snippet.
                        line = configFile.readLine();
                    }
                    else {
                        throw new RuntimeException("Expected member code block not found.");
                    }

                    // Accumulate the code lines for the member code
                    // I also check for null here to make sure we don't have an infinite loop.
                    while (!line.contains(fileDelimiters.methodDelimeter) && (line != null)) {

                        logger.debug("Processing Member code line: " + line);
                        currentMemberCode += line;
                        currentMemberCode += "\n";
                        line = configFile.readLine();
                    }

                    if (line == null) {
                        throw new RuntimeException("Found end of file while processing member code block.");
                    }
                    else {
                        // advance the line to ensure we don't addCode the delimeter itself to the code snippet.
                        line = configFile.readLine();
                    }

                    // Accumulate the code lines for the method code.
                    while (!line.contains(fileDelimiters.codeBlockEndDelimeter) && (line != null)) {

                        logger.debug("Processing Code Line: " + line);
                        currentMethodCode += line;
                        currentMethodCode += "\n";
                        line = configFile.readLine();
                    }

                    if (line == null) {
                        throw new RuntimeException("Found end of file while processing method code block.");
                    }

                    // At code end, load the tag-codeblock pair in to the lookup table.

                    // This is just a double-check, it should never happen but may as well be safe.  Don't want to load
                    //      a null into the lookup table.  That would be a confusing bug to diagnose.
                    if (currentTag == null) {
                        logger.error("Found null tag in CodeLoader prior to loading TagSwitcher.");
                        throw new RuntimeException("Found null tag in CodeLoader prior to loading TagSwitcher.");
                    }

                    // Log the code snippets that we'll use for this tag.
                    logger.info("Loading new tag and code into TagSwitcher.  Using tag " + currentTag);
                    logger.info("With member code: ");
                    logger.info(currentMemberCode);
                    logger.info("And method code: ");
                    logger.info(currentMethodCode);

                    // Load the tag->code TagSwitcher HashTable
                    TagTemplate template = new TagTemplate(currentTag, currentMemberCode, currentMethodCode);
                    tagSwitcher.add(currentTag, template);

                }

                else if (line.contains(fileDelimiters.fileEndDelimeter)) {

                    logger.debug("Found file end.");
                }

                // If not a new tag or beginning a code block, and not end of the file, it's an error.
                else {

                    logger.error("Unknown condition in tag switcher config file.");
                    throw new SeleniumGeneratorException("Unknown condition in tag switcher config file.");

                }

                // Get the next line from the config file.
                line = configFile.readLine();

            }

        } catch (IOException e) {
            logger.error("Readline Exception in CodeLoader: " + e.getMessage());
            throw e;
        }
    }

}
