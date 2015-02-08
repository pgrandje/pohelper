package com.pagehelper;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Loads the code templates for given tags from a code template file.
 * @author Paul Grandjean
 * @since 10/23/11
 * @version 1.0alpha
 */
public class CodeLoader {

    private final Logger logger = Logger.getLogger(this.getClass());
    private Configurator configurator;
    private TagSwitcher tagSwitcher;
    private String filePath;
    private BufferedReader configFile;

    CodeLoader(TagSwitcher tagSwitcher) throws PageHelperException {

        this.configurator = Configurator.getConfigurator();
        this.tagSwitcher = tagSwitcher;

        try {
            filePath = configurator.getCodeTemplateFilePath();
            if (filePath == null) {
                throw new PageHelperException("Found null file path to Tag Switcher config file.");
            }
            logger.debug("Looking for config file: " + filePath);
            logger.debug("Using current working directory: " + System.getProperty("user.dir"));
            configFile = new BufferedReader(new FileReader(filePath));
        }
        catch (FileNotFoundException e) {
            logger.fatal("File Not Found Exception in: " + e.getClass());
            logger.fatal("Cause: " + e.getCause());
            logger.fatal("Message: " + e.getMessage());
            logger.fatal("Stack Trace: " + e.getStackTrace().toString());
            throw new PageHelperException("Code Template File not found. See log. Exception Message: " + e.getMessage());
        }

        loadConfig();
    }


    private void loadConfig() throws PageHelperException {

      String currentTag = null;
      String currentMemberCode;
      String currentMethodCode;

      Configurator.CodeTemplateDelimiters fileDelimiters = configurator.getCodeTemplateDelimiters();

      try {

            String line = configFile.readLine();

            while (null != line){

                // For blank lines, just skip them.
                while(line.isEmpty()) {
                    // Get the next line from the config file.
                    line = configFile.readLine();
                }

                // Found a new tag, so starting a new tag-codeblock pair.
                // We'll test the delim using contains() to account for trailing whitespace.
                if (line.contains(fileDelimiters.tagDelimeter)) {

                    logger.debug("Found a new tag");

                    // Get the tag
                    line = configFile.readLine();
                    logger.debug("Reading tag " + line);
                    // TODO: in CodeLoader--add a validation method that verifies the tags in the config file are valid tags.

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

                    // TODO: CodeLoader should handle the case where there's no method code template.

                    // At this point we should be at the begin of a member code block, otherwise it's an error.
                    //      If it's correct, advance to the next line for loading the code.
                    if (line.contains(fileDelimiters.memberDelimeter)) {
                        // advance the line to ensure we don't addCode the delimiter itself to the code snippet.
                        line = configFile.readLine();
                    }
                    else {
                        throw new PageHelperException("Expected member code block not found.");
                    }

                    // Accumulate the code lines for the member code
                    // I also check for null here to make sure we don't have an infinite loop.
                    while (!line.contains(fileDelimiters.methodDelimeter)) {

                        logger.debug("Processing Member code line: " + line);
                        currentMemberCode += line;
                        currentMemberCode += "\n";
                        line = configFile.readLine();
                    }

                    if (null == line) {
                        throw new PageHelperException("File syntax error.  End of file found but expecting method code delimiter.");
                    }
                    else {
                        // Advance the line past the method code delimiter.
                        line = configFile.readLine();
                    }

                    // Accumulate the code lines for the method code.
                    while (!line.contains(fileDelimiters.codeBlockEndDelimeter) && (null != line)) {

                        logger.debug("Processing Code Line: " + line);
                        currentMethodCode += line;
                        currentMethodCode += "\n";
                        line = configFile.readLine();
                    }

                    if (null == line) {
                        throw new PageHelperException("Found end of file while processing method code block.");
                    }

                    // At code end, load the tag-codeblock pair in to the lookup table.

                    // This is just a double-check, it should never happen but may as well be safe.  Don't want to load
                    //      a null into the lookup table.  That would be a confusing bug to diagnose.
                    if (null == currentTag) {
                        logger.error("Found null tag in CodeLoader prior to loading TagSwitcher.");
                        throw new PageHelperException("Found null tag in CodeLoader prior to loading TagSwitcher.");
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
                    throw new PageHelperException("Unknown condition in tag switcher config file.");

                }

                // Get the next line from the config file.
                line = configFile.readLine();

            }

        } catch (IOException e) {
            logger.error("Readline Exception in CodeLoader: " + e.getMessage());
            throw new PageHelperException("IOException in CodeLoader: " + e.getMessage());
        }
    }

}
