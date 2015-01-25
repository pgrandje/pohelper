package com.testhelper;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


/**
 * Descriptors are control a single unit of the generation process and serve as a
 * handle to an element to be generated.  The TagDescriptor stores the information for a
 * specific HTML tag in the DOM which is targeted for code members and methods to be generated.
 *
 * Also stores attribute-value information also read from the page source.
 * It performs the symbol generation based on attribute and textContent info read into an instance.
 * After symbol generation, it stores the code snippets for this tag instance ready for dumping into the
 * final source code file.
 *
 * The TagDescriptor stores the code snippets that will be used for the eventual code generation.
 *
 * TagDescriptor is also used for generating the Hints for a given tag.
 *
 * User: pgrandje
 * Date: 6/2/12
 */
public class TagDescriptor {

    private final Logger logger = Logger.getLogger(TagDescriptor.class);
    private Configurator configurator;

    private String tag;

    public void setAttributes(HashMap<String, String> attributePairs) {
        this.attributePairs = attributePairs;
    }

    private HashMap<String, String> attributePairs;
    private String textContent;
    // TODO: Refactor to two different TagDescriptor types--The hints generation use the locatorString, but the code generation doesn't need it, it uses member and method code.
    private Locator locator;

    private StringBuffer memberCode;
    private StringBuffer methodCode;
    private StringBuffer comment;



    // *** Constructors ***


    public TagDescriptor(TagTemplate tagTemplate) {

        configurator = Configurator.getConfigurator();

        tag = tagTemplate.getTag();
        logger.debug("Creating new TagDescriptor with tag '" + tag + "'.");

        memberCode = new StringBuffer(tagTemplate.getMemberCode());
        methodCode = new StringBuffer(tagTemplate.getMethodCode());
        logger.trace("Using member code template:\n" + memberCode);
        logger.trace("And method code template:\n" + methodCode);

        comment = new StringBuffer();

        recordInfoComments();

    }


    private void recordInfoComments() {

        // The Configurator decides whether to store the comments or not for the final writing.
        if(configurator.getWriteComments() == true) {

            comment.append("   // Tag: " + tag + "\n");
            comment.append("   // Text Contained by tag: " + textContent);

            if (!attributePairs.isEmpty()) {
                for (Map.Entry attributePair : attributePairs.entrySet()) {
                    comment.append("   // Attribute: " + attributePair.getKey() + " = " + attributePair.getValue());
                }
            }

            logger.debug("Stored comment block:\n");
            logger.debug(comment.toString());

        }
        else {
            logger.debug("Comments configuration == false.  No comments stored.");
        }

    }


    public boolean hasComments() {

        boolean returnStatus = true;

        if ((null == comment) || (comment.length() == 0 )) {
            returnStatus = false;
        }

        return returnStatus;
    }


    // *** Accessors ***

    public String getTag() {
        return tag;
    }

    public String getMemberCode() {
        return memberCode.toString();
    }

    public String getMethodCode() {
        return methodCode.toString();
    }

    public String getTextValue() {
        return textContent;
    }


    public void setTextValue(String text) {

        // Or, it could be only whitespace!
        if (text == null) {
            textContent = null;
        }
        else {
            // text could begin with white space, so we need to trim it first, and then afterwards, again it could be empty.
            text = text.trim();

            if (text.isEmpty()) {
                textContent = null;
            }

            textContent = text;

        }
    }

    public String getComment() {
        return comment.toString();
    }

    public HashMap<String, String> getAttributePairs() {
        return attributePairs;
    }



    // **** Locator ****

    public void setLocator(Locator locator) {
        this.locator = locator;
    }


    // TODO: Only the hints file needs to get the locator string--another reason to have two diff types of TagDescriptors. -- Or, code this to a standard interface.
    public String getLocatorString() {
        return locator.getTypeStringName() + " = " + locator.getValue();
    }


    //   *** Symbol Names ***

    /** writeMemberAndMethods()
     *  Writes member and method names using in this order,
     *  - any textContent that exists,
     *  - any attributes that exist.
     *  - a default symbol name.
    */
    // TODO: If the TagDescriptor stored a ref to the NameRecorder I could avoid having to pass it so often.
    public void writeMemberAndMethods(NameRecorder memberNameRecorder) {

        logger.debug("Writing locator string using locator type '" + locator.getTypeStringName() + "' with value '" + locator.getValue() + "'.");

        // TODO: Verify String.replaceAll() and not String.replace() is what I want for setting locator values.
        StringBuffer alteredMemberCode = new StringBuffer(
                        memberCode.toString().replaceAll(configurator.getLocatorIndicator(), locator.getTypeStringName() + " = \"" + locator.getValue() + "\""));
        logger.debug("Storing locator string as: " + alteredMemberCode);
        memberCode = alteredMemberCode;

        if (writeMemberNameUsingTextContent(memberNameRecorder) == true) {
            logger.debug("Symbols written using tag's text content.");
        }
        else if (writeMemberNameUsingAttributeValue(memberNameRecorder) == true) {
            logger.debug("Symbols written using an attribute value.");
        }
        else {
            writeDefaultMemberName(memberNameRecorder);
            logger.debug("Tag had no textContent or attributes we could use for symbol writing.  Using default symbol names.");
        }

    }


    private boolean writeMemberNameUsingTextContent(NameRecorder memberNameRecorder) {

        logger.debug("Checking whether there's textContent we can use for symbol writing.");

        if (textContent != null && !textContent.isEmpty()) {

            logger.debug("Using text content '" + textContent + "' for symbol replacement.");
            writeMemberAndMethodNames(memberNameRecorder.makeSymbolName(textContent));

            return true;

        }

        return false;
    }


    // Sets the Generate Status to true if it find attributes it can use for symbol writing.
    // Returns false if it had no attributes it could use for symbol writing.
    private boolean writeMemberNameUsingAttributeValue(NameRecorder memberNameRecorder) {

        String attributeValue;

        logger.debug("Checking whether there's attributes we can use for symbol writing.");

        // if attributes exist...
        if (attributePairs != null && !attributePairs.isEmpty()) {

            // If an ID attribute exists, use its value for the symbols.
            if(attributePairs.get("id") != null) {
                logger.debug("Using ID attribute.");
                attributeValue = attributePairs.get("id");
            }
            else if(attributePairs.get("name") != null) {
                logger.debug("Using NAME attribute.");
                attributeValue = attributePairs.get("name");
            }
            /*
            else if(attributes.getNamedItem("class") != null) {
                logger.debug("Using CLASS attribute.");
                attributeValue = attributes.getNamedItem("class").getNodeValue();
            }*/
            else {
                logger.info("No usable attribute found for the member name using the current configuration.");
                return false;
            }

            logger.debug("Using attribute value '" + attributeValue + "' for symbol replacement.");
            writeMemberAndMethodNames(memberNameRecorder.makeSymbolName(attributeValue));
            return true;

        }
        else {
            logger.info("This tag doesn't have attributes.");
        }

        return false;
    }



    private void writeDefaultMemberName(NameRecorder memberNameRecorder) {

        logger.debug("Writing member and method names using default symbol names.");
        writeMemberAndMethodNames(memberNameRecorder.makeDefaultSymbolName());
    }


    private void writeMemberAndMethodNames(String symbolName) {

        logger.debug("Writing member and methods using symbol name '" + symbolName + "'.");

        memberCode = new StringBuffer(memberCode.toString().replaceAll(configurator.getMemberNameIndicator(), symbolName));
        methodCode = new StringBuffer(methodCode.toString().replaceAll(configurator.getMemberNameIndicator(), symbolName));
    }

}
