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
 * User: pgrandje
 * Date: 6/2/12
 */
public class TagDescriptor {

    private final Logger logger = Logger.getLogger(TagDescriptor.class);

    private Configurator configurator;

    private String tag;
    private HashMap<String, String> attributePairs;
    private StringBuffer memberCode;
    private StringBuffer methodCode;
    private String textContent;
    private StringBuffer comment;
    private String locator;


    // *** Constructors, Factories, and Factory support methods ***

    private TagDescriptor(TagTemplate tagTemplate, String textValue) {

        configurator = Configurator.getConfigurator();

        // I could get the tag from either the Node or the template.  I'm choosing the Template since it's
        //  working and I might have to change the string if I get it from the Node.
        tag = tagTemplate.getTag();
        logger.info("Creating new TagDescriptor with tag '" + tag + "'.");

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



    public static TagDescriptor createTagDescriptor(TagTemplate template, Node node) {

        TagDescriptor tagDescriptor = new TagDescriptor(template, node.getTextContent());

        tagDescriptor.attributePairs = new HashMap<String, String>();
        NamedNodeMap nodeAttributes = node.getAttributes();
        for(int i=0; i < nodeAttributes.getLength(); i++) {
            Attr attr = (Attr) nodeAttributes.item(i);
            tagDescriptor.attributePairs.put(attr.getName(), attr.getValue());
        }

        return tagDescriptor;
    }


    public static TagDescriptor createTagDescriptor(TagTemplate template, HintsDescriptor hintsDescriptor) {

        TagDescriptor tagDescriptor = new TagDescriptor(template, hintsDescriptor.getText());

        tagDescriptor.attributePairs = new HashMap<String, String>();
        List<HintsAttribute> hintsAttributes = hintsDescriptor.getAttributes();
        for(HintsAttribute hintsAttribute: hintsAttributes) {
            tagDescriptor.attributePairs.put(hintsAttribute.getAttributeName(), hintsAttribute.getAttributeValue());
        }

        return tagDescriptor;
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



    // **** Locator creation and storage ****


    // Locator write method for writing locator from Hints.
    public void writeLocator(HintsDescriptor.Locator hintsLocator) {
        locator = hintsLocator;
        writeLocatorWithLocatorObject(locator);
    }

    // Locator write method for writing locator from Hints.
    private void writeLocatorWithLocatorObject(HintsDescriptor.Locator locator)  {

        logger.debug("Writing locator from hints using Locator Type '" + locator.type.toString() + "' and value '" + locator.locatorValue + "'.");

        // TODO: This locatorString building logic could go in the Locator class.
        String locatorString = null;
        if (locator.type == HintsDescriptor.LocatorType.ID) {
            locatorString = "id = ";
        }
        else if (locator.type == HintsDescriptor.LocatorType.CSS_LOCATOR) {
            locatorString = "css = ";
        }
        locatorString = locatorString + "\"" + locator.locatorValue + "\"";

        // TODO: If I have to replace the entire StringBuffer it may make more sense for memberCode to just be a String.
        // TODO: Why am I using replaceAll()?  Shouldn't I be using replace()?  I thought replaceAll() required a regex.
        memberCode = new StringBuffer(
                        memberCode.toString().replaceAll(configurator.getLocatorIndicator(), locatorString));

    }

    // Note--Returns a boolean because if we can't write a locator we won't generate code.
    public boolean writeLocator() {

        if ((configurator.getLocatorConfig() == Configurator.LocatorConfig.ATTRIBS_CSS ||
             configurator.getLocatorConfig() == Configurator.LocatorConfig.ATTRIBS_ONLY
            ) &&
                writeLocatorUsingAttributes() == true
           ) {
            logger.debug("Locator written using an attribute--setting write status to true.");
            return true;
        }
        else if ((configurator.getLocatorConfig() == Configurator.LocatorConfig.ATTRIBS_CSS ||
                  configurator.getLocatorConfig() == Configurator.LocatorConfig.CSS_ONLY
                 ) &&
                    writeLocatorWithCss() == true
                ) {
            logger.debug("Locator written using css locator--setting write status to true.");
            return true;
        }
        else {
            logger.debug("Cannot write locator for node '" + tag + "', setting write status to false.");
            return false;
        }

    }






    // TODO:  For the textless <p> with no attributes, this returned true but should have been false.
    // TODO: For some reason, <li>s exist with attributes that can't be used as locators, but doesn't generate a css either.
    private boolean writeLocatorUsingAttributes() {

        logger.debug("Are there attributes we can use for writing the locator?");

        if (attributePairs != null && !attributePairs.isEmpty()) {

            logger.debug("Yes, there's attributes for this tag.");
            // If an ID attribute exists, use its value for the symbols.
            if(attributePairs.get("id") != null) {
                writeLocatorWithAttribute("id", "id");
                return true;
            }
            // TODO:  Am I duplicating my retrievals of the attribute value?  Seems that I can just retrieve the value once and pass it.
            else if(attributePairs.get("name") != null) {
                this.writeLocatorWithAttribute("name", "name");
                return true;
            }
            else if(configurator.getLocatorUsesClassnames() == true && attributePairs.get("class") != null) {
                this.writeLocatorWithAttribute("class", "className");
                return true;
            }
            // TODO:  Can I configure this to trap an arbitrary attribute to use for a locator?
            else {
                logger.info("No attributes found that we can use for writing a locator, but it does have other attributes.");
                return false;
            }

        }

        // It didn't have any attributes so must return false.
        logger.debug("No, there's no attributes for this tag.");
        return false;
    }




    // I think this will work regardless of whether the symbols have been assigned yet or not, but I haven't tested it that way.
    private void writeLocatorWithAttribute(String attribute, String locatorType)  {

        String locatorText = attributePairs.get(attribute);

        logger.debug("Using '" + attribute + "' for locator assignment. Value is " + locatorText + ".");


        StringBuffer alteredMemberCode = new StringBuffer(
                        memberCode.toString().replaceAll(configurator.getLocatorIndicator(), locatorType + " = \"" + locatorText + "\""));
        memberCode = alteredMemberCode;

    }



    private boolean writeLocatorWithCss() {

        String cssLocator = makeCssLocator();

        if ((cssLocator == null) || cssLocator.isEmpty()) {
            logger.warn("WARNING: CSS locator is null or is empty.");
            return false;
        }

        // replace the <locator> symbol in the member code snippet.
        StringBuffer alteredMemberCode = new StringBuffer(
                        memberCode.toString().replaceAll("<locator>", "css = \"" + cssLocator + "\""));
        memberCode = alteredMemberCode;

        return true;

    }



    // Get the css string using the node's ancestors.
    // This is public because it's also used to write the analysis file.
    private String makeCssLocator() {

        // This flag records the condition where an ID attribute is found and we can stop searching ancestor nodes.
        boolean foundId = false;

        logger.debug("*** Making a CSS Locator ***.");

        // This will store the path of nodes which we'll use to construct the cssLocator string.
        // Be sure to use a list that is ordered.
        LinkedList<Node> ancestorNodes = new LinkedList<Node>();

        // The loop starts with the first ancestor, not the node were starting with.  This is because the
        //  node we're building the locator for already does not have attributes we can use.  We know that
        //  because otherwise we would have used them for a locator and would not have to build a css locator.
        logger.debug("Backtracking up the node chain looking for a unique css path.  Building a list of the current node's anscestors.");
        ancestorNodes.add(node);
        logger.debug("Current node <" + node.getNodeName() + "> added to list of ancestors.");

        // Get the first parent node.
        Node ancestorNode = node.getParentNode();


        // Loop up the chain of ancestor nodes until we find an ancestor with an ID, or some othe attribute we
        // can use to uniquely identify the path, or we reach the <body> tag.
        // A null ancestor node will be set if we find a useful attribute.  This is used to terminate the loop.
        while((!foundId) &&  (!ancestorNode.getNodeName().equalsIgnoreCase("body"))) {

            logger.debug("Beginning new ancestor node iteration.");
            logger.debug("Using tag <" + ancestorNode.getNodeName() + ">.");

            // If the parent node had attributes, see if it has an ID.  If so, we exit the loop.
            if (ancestorNode.hasAttributes()) {

                NamedNodeMap parentAttributes = ancestorNode.getAttributes();
                logger.debug("Current ancestor node has " + parentAttributes.getLength() + " attributes.");

                Attr attrib = (Attr) parentAttributes.getNamedItem("ID");
                if (attrib != null) {
                    logger.debug("Attribute ID value = " + attrib.getValue());
                    ancestorNodes.add(ancestorNode);
                    foundId = true;
                    break;
                }

            }  // end - if node has attributes.

            // If we get here, the current node didn't have an attribute that we can use to terminate the locator path,
            // so we'll record the parent node and advance to next parent node.

            logger.debug("No ID attribute found, adding current ancestor node " +
                                    ancestorNode.getNodeName() + " to ancestor list.");
            ancestorNodes.add(ancestorNode);
            logger.debug("Continuing to next ancestor node.");
            ancestorNode = ancestorNode.getParentNode();

        } // end -- while not <body> and no ID attribute found

        // After exiting the loop, be sure to addCode the Node that terminated the chain to the ancestor list.
        ancestorNodes.add(ancestorNode);

        // Log the Ancestor chain.
        Iterator<Node> ancestorIterator = ancestorNodes.descendingIterator();
        while(ancestorIterator.hasNext()) {
            ancestorNode = ancestorIterator.next();
            logger.debug("Ancestor: " + ancestorNode.getNodeName());
        }

        /* Build the cssLocator. The locator will be constructed by traversing the parent node tree
           back down to the ui element we're locating.
        */

        StringBuffer cssLocator = new StringBuffer();

        /* The chain of parent nodes either ends in an ID-node or a <body> tag.
           If we have a <body> tag, the locator string must start with body > tag > tag ... etc.
           If we have an ID-Node, the locator string must start with #ID > tag > tag ... etc.
        */


        // Get an iterator to traverse the ancestors.
        ancestorIterator = ancestorNodes.descendingIterator();

        // Verify there's at least one ancestor, if not, throw an exception.
        // This condition should never be false, but it's here as an extra check in case bugs are introduced in the future.
        if (ancestorIterator.hasNext() == false) {
            throw new SeleniumGeneratorException("Unknown condition, first Node in CSS Selector ancestor not found.");
        }

        // Get the first oldest ancestor--the top of the chain.
        Node currentCssSelectorNode = ancestorIterator.next();

        // Process the first item in the css selector chain, it will either be a <body> or a tag with an ID attribute.
        if (foundId) {

            logger.info("A CSS path beginning point was found.  The starting Node is: " + ancestorNodes.getLast().getNodeName());
            // TODO:  We can generalize this from stopping at ID, to stop at any one of a list of pre-defined attribs, especially name attribs.
            Attr attr = (Attr) currentCssSelectorNode.getAttributes().getNamedItem("ID");
            logger.debug("Retrieved attribute -- attrib name is '" + attr.getName() + "' with value = " + attr.getValue()  +".");

            // Start the cssLocator string with a "#ID-Value >" string.
            // This assumes the attribute is an ID, if it's another terminating attribute this won't work.
            cssLocator.append("#" + attr.getValue() + " > ");

        }
        // else if (foundConfiguredAttribute)
        // {
        // }
        else if (currentCssSelectorNode.getNodeName().equalsIgnoreCase("body")) {

            logger.info("Starting the path at the <body> tag.");
            cssLocator.append("body > ");

        }
        else {
            logger.error("Encountered unknown state for ancestor node.  This should never happen!");
            logger.error("Current ancestor Node is '" + ancestorNode.getNodeName() + "'.");
            throw new SeleniumGeneratorException("Encountered unknown state for first ancestor node when writing CSS Locator");
        }


        // Traverse the ancestor nodes in reverse order (top to bottom in the DOM) to construct the css selector.
        //   We begin by advancing to the 'next' first, so we skip the <body> or ID'ed tag that we already processed.
        while(ancestorIterator.hasNext()) {

            currentCssSelectorNode = ancestorIterator.next();
            // TODO:  First, it should check if the UI element is the first or only one of its kind on the page.
            cssLocator.append(currentCssSelectorNode.getNodeName() + processSiblingPosition(currentCssSelectorNode) + " > ");
            logger.trace("CSS Locator intermediate result: " + cssLocator.toString());

        }

        // Need to strip of the last " > "
        cssLocator.delete(cssLocator.lastIndexOf(" > "), cssLocator.length());

        logger.debug("CSS Locator final string: " + cssLocator.toString());
        return cssLocator.toString();

    }



    private String processSiblingPosition(Node currentNode) {

        int counter = 1;
        boolean processNthChild = false;

        // save the node name; we're only concerned about siblings if there are more than one of the same tag.
        String tagName = currentNode.getNodeName();

        currentNode = currentNode.getPreviousSibling();

        while(currentNode != null) {

            // need to check that it's not a text node or attribute node.  It must be a tag to count.
            if (currentNode.getNodeType() == org.w3c.dom.DocumentType.ELEMENT_NODE)  {
                logger.trace("Prev sibling '" + currentNode + "'");
                counter++;
                if (currentNode.getNodeName().equalsIgnoreCase(tagName)) {
                    processNthChild = true;
                }
            }

            currentNode = currentNode.getPreviousSibling();
        }


        logger.debug("Sibling position is " + counter);

        if (counter == 1 || processNthChild == false) {
            return "";  // not the same as null and this makes a difference in how it's processed.
        }
        else {
            return ":nth-child(" + counter + ")";
        }

    }






    //   *** Symbol Names ***

    /** writeMemberAndMethods()
     *  Writes member and method names using in this order,
     *  - any textContent that exists,
     *  - any attributes that exist.
     *  - a default symbol name.
    */
    // TODO: If the TagDescripter stored a ref to the NameRecorder I could avoid having to pass it so often.
    public void writeMemberAndMethods(NameRecorder symbolNameRecorder) {

        if (writeMemberNameUsingTextContent(symbolNameRecorder) == true) {
            logger.debug("Symbols written using tag's textContent value.");
        }
        else if (writeMemberNameUsingAttributeValue(symbolNameRecorder) == true) {
            logger.debug("Symbols written using tag's attribute values.");
        }
        else {
            writeDefaultMemberName(symbolNameRecorder);
            logger.debug("Tag had no textContent or attributes we could use for symbol writing.  Using default symbol names.");
        }

    }



    private boolean writeMemberNameUsingTextContent(NameRecorder nameRecorder) {

        logger.debug("Checking whether there's textContent we can use for symbol writing.");

        if (textContent != null && !textContent.isEmpty()) {

            logger.debug("Using text content '" + textContent + "' for symbol replacement.");
            writeMemberAndMethodNames(nameRecorder.makeSymbolName(textContent));

            return true;

        }

        return false;
    }


    // Sets the Generate Status to true if it find attributes it can use for symbol writing.
    // Returns false if it had no attributes it could use for symbol writing.
    private boolean writeMemberNameUsingAttributeValue(NameRecorder nameRecorder) {

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
            writeMemberAndMethodNames(nameRecorder.makeSymbolName(attributeValue));
            return true;

        }
        else {
            logger.info("This tag doesn't have attributes.");
        }

        return false;
    }



    private void writeDefaultMemberName(NameRecorder nameRecorder) {

        logger.debug("Writing member and method names using default symbol names.");
        writeMemberAndMethodNames(nameRecorder.makeSymbolName(null));

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
