package com.testhelper;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * TODO: Add javadoc here.
 * Constructed using a factory since
 *      a) it could work off a wc3-Node or a HintsDescriptor and
 *      b) we only need it when we need it, we won't need it's instance to be retained from the calling methods, so
 *         using a factory with a fluent api will properly restrict how this is used.
 * User: pgrandje
 * Date: 4/20/14
 */
public class LocatorMaker {

    private static final Logger logger = Logger.getLogger(LocatorMaker.class);

    private Configurator configurator;

    private Locator locator;

    // Factory that creates the locatorMaker to create locators from the DOM.  Node is the UI element to be located.
    public static LocatorMaker createLocatorMaker(Configurator configurator) {

        LocatorMaker locatorMaker = new LocatorMaker();
        locatorMaker.configurator = configurator;

        return locatorMaker;
    }




    /* Writing the locator using a Node requires the DOM to be loaded using org.w3c.dom.
       Returns a boolean because if we can't write a locator we want to inform the calling program.
     */
    public Locator makeLocator(Node node) {

        if ((configurator.getLocatorConfig() == Configurator.LocatorConfig.ATTRIBS_CSS ||
             configurator.getLocatorConfig() == Configurator.LocatorConfig.ATTRIBS_ONLY
            ) &&
                makeAttributeLocator(node) == true
           ) {
            logger.debug("Locator written using an attribute.");
            return locator;
        }
        else if ((configurator.getLocatorConfig() == Configurator.LocatorConfig.ATTRIBS_CSS ||
                  configurator.getLocatorConfig() == Configurator.LocatorConfig.CSS_ONLY
                 ) &&
                    makeCssLocator(node) == true
                ) {
            logger.debug("Locator written using css locator.");
            return locator;
        }
        else {
            logger.debug("Cannot write locator for node '" + node.getNodeName() + "'.");
            return locator;
        }

    }






    // TODO:  Fix Bug--A textless <p> with no attributes returned true but should have returned false.
    // TODO: Fis Bug -- <li>s exist with attributes that can't be used as locators, but doesn't generate a css either.
    private boolean makeAttributeLocator(Node node) {

        logger.debug("Are there attributes we can use for writing the locator? ...");

        NamedNodeMap attributes =  node.getAttributes();

        if (attributes != null && attributes.getLength() != 0) {

            logger.debug("Yes, the tag has attributes.");
            // If an ID attribute exists, use its value for the symbols.
            if(attributes.getNamedItem("id") != null) {
                Attr attr = ((Attr)attributes.getNamedItem("id"));
                logger.debug("Using Attribute: " + attr.getName() + " = " + attr.getValue());
                locator = new Locator(Locator.LocatorType.ID, attr.getValue());
                // TODO: setup up a status variable to put the returns in one place.
                return true;
            }
            // If no id, but there's a name attribute, use that.
            else if(attributes.getNamedItem("name") != null) {
                Attr attr = ((Attr)attributes.getNamedItem("name"));
                logger.debug("Using Attribute: " + attr.getName() + " = " + attr.getValue());
                locator = new Locator(Locator.LocatorType.NAME, attr.getValue());
                return true;
            }
            // If no id or name, but there's a class name, use that if the configuration allows using class names.
            else if(configurator.getLocatorUsesClassnames() == true && attributes.getNamedItem("class") != null) {
                Attr attr = ((Attr)attributes.getNamedItem("class"));
                logger.debug("Using Attribute: " + attr.getName() + " = " + attr.getValue());
                locator = new Locator(Locator.LocatorType.CLASS, attr.getValue());
                return true;
            }
            // TODO:  Configure this to trap an arbitrary attribute to use for a locator.
            // If no id, name, or classname, I should see if there's another unique attribute I can use.
            else {
                logger.info("No attributes found that we can use for writing a locator, but it does have other attributes.");
                return false;
            }

        }

        // It didn't have any attributes so must return false.
        logger.debug("No, there's no attributes for this tag.");
        return false;
    }



    private boolean makeCssLocator(Node node) {

        String cssLocator = makeCssLocatorString(node);
        if ((cssLocator == null) || cssLocator.isEmpty()) {
            logger.warn("WARNING: CSS locator is null or is empty.");
            return false;
        }

        locator = new Locator(Locator.LocatorType.CSS, cssLocator);
        return true;

    }


    // Get the css string using the node's ancestors.
    // This is public because it's also used to write the analysis file.
    private String makeCssLocatorString(Node node) {

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







    // Locator write method for writing locator from Hints.
    public String writeLocator(HintsDescriptor.Locator hintsLocator) {
        locatorFromHints = hintsLocator;
        writeLocatorWithLocatorObject(locatorFromHints);
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

}
