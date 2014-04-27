package com.testhelper;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;

import java.util.*;


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
    private HashMap<String, String> attributePairs;
    private String textContent;
    private String locatorString;

    private StringBuffer memberCode;
    private StringBuffer methodCode;
    private StringBuffer comment;



    // *** Constructors ***

    public TagDescriptor(TagTemplate template, Node node) {

        TagDescriptor tagDescriptor = new TagDescriptor(template, node.getTextContent());

        tagDescriptor.attributePairs = new HashMap<String, String>();
        NamedNodeMap nodeAttributes = node.getAttributes();
        for(int i=0; i < nodeAttributes.getLength(); i++) {
            Attr attr = (Attr) nodeAttributes.item(i);
            tagDescriptor.attributePairs.put(attr.getName(), attr.getValue());
        }

    }


    public TagDescriptor(TagTemplate template, HintsDescriptor hintsDescriptor) {

        TagDescriptor tagDescriptor = new TagDescriptor(template, hintsDescriptor.getText());

        tagDescriptor.attributePairs = new HashMap<String, String>();
        List<HintsAttribute> hintsAttributes = hintsDescriptor.getAttributes();
        for(HintsAttribute hintsAttribute: hintsAttributes) {
            tagDescriptor.attributePairs.put(hintsAttribute.getAttributeName(), hintsAttribute.getAttributeValue());
        }

    }


    private TagDescriptor(TagTemplate tagTemplate, String textValue) {

        configurator = Configurator.getConfigurator();

        // I could get the tag from either the Node or the template.  I'm choosing the Template since it's
        //  working and I might have to change the string if I get it from the Node.
        tag = tagTemplate.getTag();
        logger.debug("Creating new TagDescriptor with tag '" + tag + "'.");

        memberCode = new StringBuffer(tagTemplate.getMemberCode());
        methodCode = new StringBuffer(tagTemplate.getMethodCode());
        logger.debug("Using member code template:\n" + memberCode);
        logger.debug("And method code template:\n" + methodCode);

        comment = new StringBuffer();

        logger.debug("Creating new TagDescriptor for tag " + tag + ".");

        textContent = textValue;

        /* Log whether we found text or not.  When textContent doesn't exist, node.getTextContent() returns an empty
           string but not a null.  This must be handled.
         */
        if ((textContent != null) && (!textContent.isEmpty())) {
            logger.debug("In method addTextValueViaHtmlNode() -- Found textContent, saving '" + textContent + "' to tag bucket.");
        }
        else {
            logger.debug("No textContent found.");
        }

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
            logger.debug("No comments recorded due to Configurator setting of comments to false.");
        }

    }

    // *** Accessors ***

    public String getTag() {
        logger.trace("Getting the tag: " + tag);
        return tag;
    }

    public String getMemberCode() {
        logger.trace("Getting the member code:\n" + memberCode);
        return memberCode.toString();
    }

    public String getMethodCode() {
        logger.trace("Getting the method code:\n" + methodCode);
        return methodCode.toString();
    }

    public String getTextValue() {
        logger.trace("Getting the text content: " + textContent);
        return textContent;
    }

    public String getComment() {
        logger.trace("Getting the comment: " + comment);
        return comment.toString();
    }

    public HashMap<String, String> getAttributePairs() {
        return attributePairs;
    }

    // **** Locator String ****

    public void writeLocatorString(Locator locator) {
        // TODO: Verify String.replaceAll() and not String.replace() is what I want for setting locator values.
        StringBuffer alteredMemberCode = new StringBuffer(
                        memberCode.toString().replaceAll(configurator.getLocatorIndicator(), locator.getTypeStringName() + " = \"" + locator.getValue() + "\""));
        memberCode = alteredMemberCode;
    }

    public String getLocatorString() {
        return locatorString;
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
        writeMemberAndMethodNames(memberNameRecorder.makeSymbolName(null));

    }


    private void writeMemberAndMethodNames(String symbolName) {

        logger.debug("Writing member and methods using symbol name '" + symbolName + "'.");

        StringBuffer alteredMemberCode =
                new StringBuffer(memberCode.toString().replaceAll(configurator.getMemberNameIndicator(), symbolName));
        memberCode = alteredMemberCode;

        StringBuffer alteredMethodCode =
                new StringBuffer(methodCode.toString().replaceAll(configurator.getMemberNameIndicator(), symbolName));
        methodCode = alteredMethodCode;

    }

}
