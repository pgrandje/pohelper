package com.testhelper;

import org.w3c.dom.Node;

import org.apache.log4j.Logger;

/**
 * Recursive function, the Node Scanner traverses the DOM and creates objects and initiates actions based on the DOM
 * nodes it encounters.  This is the main engine driving the code generating process.
 * User: pgrandje
 * Date: 10/23/11
 */
public class NodeScanner {

    private final Logger logger = Logger.getLogger(NodeScanner.class);

    // Returns code for a given tag.
    private TagSwitcher tagSwitcher;

    private TagDescriptorList tagDescriptorList;

    private NameRecorder memberNameRecorder;



    NodeScanner(TagSwitcher tagSwitcher) {

        this.tagSwitcher = tagSwitcher;
        this.tagDescriptorList = new TagDescriptorList();
        this.memberNameRecorder = new NameRecorder("Member Name Recorder");
    }


    // After scanning the page source, returns a list of all the nodes we want code for along with their code snippets.
    public TagDescriptorList scanForUIElements(Node parent, int level)
    {
        logger.info("Entered scanForUIElements using parent node '" + parent.getNodeName() + "' at Level " + level);

        // This should be ok, it's the HTML tags I'm traversing.
        // Anything else I'm interested in are:  text, attributes, values
        //    but those will be processed differently and would still
        //    cause the traversal to stop.
        if (parent.getNodeType() != org.w3c.dom.DocumentType.ELEMENT_NODE)
            return tagDescriptorList;
        else if (parent.hasChildNodes() == false)
            return tagDescriptorList;


        // This should now be an Element, as all non-elements were just caught
        //    above.
        // I will still probably change this to a Nodelist since that
        //   seems like the established way things are done.  If so I can check
        //   that all items in the Nodelist are elements.  Seems like they
        //   should be.  Text will always be a child if the Cleaner did it's
        //   job correctly.
        Node current = parent.getFirstChild();
        level++;

        while (current != null)
        {

            // If the node doesn't have attributes or text we won't store it for generating code.
            // getTextContent seems to return an empty string, rather than null when there's not text.
            if (    (current.getNodeType() == 1) &&
                    ( (current.hasAttributes() == true) || (!current.getTextContent().isEmpty()) )
               ) {

                logger.info("Current Node Name " + current.getNodeName() + " -- Value: "
                    + current.getNodeValue() + " -- Node type: " + current.getNodeType());

                // To ensure it's a tag I'm selecting the code for, I must turn the node name into a tag format.
                String tag = "<" + current.getNodeName() + ">";

                // We return the entire TagTemplate rather than getting the code from the tagSwitcher
                // because the tagSwitcher may not have the current node we're processing, in that case we
                // return a null.

                TagTemplate tagTemplate = tagSwitcher.getTemplate(tag);

                // If we get a tagTemplate back, this is a tag we want to generate code for.  So we create a TagDescriptor
                // for holding it's information.
                if (tagTemplate != null) {

                    // Load up a new TagDescriptor for future code processing and addCode it to the TagDescriptorList
                    TagDescriptor tagDescriptor = new TagDescriptor(tagTemplate, current);

                    // The tag is only recorded for generation if a locator can be written.  Locators are written based
                    // on an ID, a CSS Locator (which should always be obtainable), or on a attribute previously identfied
                    // via the runtime configuration.
                    // NOTE:  With the Comments feature, I may want to move this decision into the TagDescriptor so at least comments
                    //        can be written.  WriteLocator() ad writeMemberAndMethods() could be run from the TagDescriptor contructor,
                    //        decoupling these to classes further. As is, if the locator can't be computed, the tag is not added
                    //        to the list for generation.  But this would preclude writing only informational comments to the output.
                    if(tagDescriptor.writeLocator() == false) {
                        logger.warn("Locator could not be written for this node with nodename '" + current.getNodeName() + "'.");
                    }
                    else {
                        tagDescriptor.writeMemberAndMethods(memberNameRecorder);
                        tagDescriptorList.add(tagDescriptor);
                    }

                }

            }

            scanForUIElements(current, level);
            current = current.getNextSibling();
        }

       return tagDescriptorList;
    };

}
