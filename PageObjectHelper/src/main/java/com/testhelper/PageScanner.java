package com.testhelper;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.*;

import org.apache.log4j.Logger;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Recursive function, the Node Scanner traverses the DOM and creates objects and initiates actions based on the DOM
 * nodes it encounters.  This is the main engine driving the code generating process.
 * User: pgrandje
 * Date: 10/23/11
 */
public class PageScanner {

    private final Logger logger = Logger.getLogger(PageScanner.class);

    private static PageScanner scanner = null;

    // w3c.org Document object for the page's DOM.
    private Document document;

    // Returns code for a given tag.
    private TagSwitcher tagSwitcher;

    // Records names used for members to avoid duplicates.
    private NameRecorder memberNameRecorder;

    private TagDescriptorList tagDescriptorList;



    // PageScanner is a singleton since we would only ever need one at a time.
    public static PageScanner getScanner()  throws IOException, ParserConfigurationException {
        if (scanner == null) {
            scanner = new PageScanner();
        }
        return scanner;
    }

    // TagSwitcher throws the IOException when it can't find it's configuration file.
    private PageScanner() throws IOException, ParserConfigurationException {

        // Load a Lookup 'switcher' data-structure from the config file that defines the tag-->code translations.
        this.tagSwitcher = new TagSwitcher(Configurator.getConfigurator());;
        this.tagDescriptorList = new TagDescriptorList();
        this.memberNameRecorder = new NameRecorder("Member Name Recorder");
        parsePage();
    }



    // IOException comes from cleaner.clean(url), ParserConfigurationException comes from DomSerializer
    private void parsePage() throws IOException, ParserConfigurationException {

        URL url = Configurator.getConfigurator().getUrl();

        // create an instance of HtmlCleaner and configure it.
        HtmlCleaner cleaner = new HtmlCleaner();
        // take default cleaner properties
        CleanerProperties props = cleaner.getProperties();
        props.setAllowMultiWordAttributes(true);
        //TODO: props.setPruneTags(arg0);  //use this later when I know what to prune.
        props.setOmitComments(true);

        TagNode nodes = null;

        try {
            nodes = cleaner.clean(url);
        } catch (UnknownHostException e) {
            // TODO: Research a pattern for communicating error messages up to the  UI layer.
            System.out.println("Host or page not found.  Using url: " + url.toString());
            System.exit(0);
        }
        // Get the page source into a Document object.
        document = new DomSerializer(props, true).createDOM(nodes);

    }


    public PageDescriptor getPageName(NameRecorder classNameRecorder) {

        PageDescriptor pageDescriptor = null;

        // Get all <title> tags--hopefully there's one and only one.
        NodeList titleTagList = document.getElementsByTagName("title");

        if (null == titleTagList) {
            throw new SeleniumGeneratorException("Retrieving <title> returned a null list.");
        }
        else if (titleTagList.getLength() == 1)  {
            logger.info("Found exactly one <title> tag, using it's text for the page object's classname.");
            pageDescriptor = makePageNameFromTitle(titleTagList.item(0), classNameRecorder);
        }
        else if (titleTagList.getLength() > 1) {
            logger.warn("Found more than one <title> tag, is this valid for a web page?");
            pageDescriptor = makePageNameFromTitle(titleTagList.item(0), classNameRecorder);
        }
        else if (titleTagList.getLength() == 0) {
            logger.warn("<title> tag not found, using a default name for the page object.");
            pageDescriptor = makeDefaultPageName(classNameRecorder);
        }

        return pageDescriptor;
    }


    private PageDescriptor makeDefaultPageName(NameRecorder nameRecorder) {
        return  new PageDescriptor(nameRecorder.makeDefaultSymbolName());
    }


    private PageDescriptor makePageNameFromTitle(Node titleNode, NameRecorder classNameRecorder)  {

        String titleText = titleNode.getTextContent();
        logger.info("Using title tag '" + titleNode.getNodeName() + "' with text \"" + titleText + "\"");
        String className = classNameRecorder.makeSymbolName(titleText);
        logger.info("ClassRecorder returned name '" + className + "' for the page object class name.");

        return new PageDescriptor(className);

    }




    public Document getDom() {
        return document;
    }

    public TagDescriptorList scan() {
        return scanForUIElements(document.getDocumentElement(), 0);
    }


    // After scanning the page source, returns a list of all the nodes we want code for along with their code snippets.
    private TagDescriptorList scanForUIElements(Node parent, int level)
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
                    TagDescriptor tagDescriptor = new TagDescriptor(tagTemplate);

                    // The tag is only recorded for generation if a locator can be written.  Locators are written based
                    // on an ID, a CSS Locator (which should always be obtainable), or on a attribute previously identfied
                    // via the runtime configuration.
                    // NOTE:  With the Comments feature, I may want to move this decision into the TagDescriptor so at least comments
                    //        can be written.  WriteLocator() ad writeMemberAndMethods() could be run from the TagDescriptor contructor,
                    //        decoupling these to classes further. As is, if the locator can't be computed, the tag is not added
                    //        to the list for generation.  But this would preclude writing only informational comments to the output.
                    tagDescriptor.setAttributes(setAttributePairs(current));
                    Locator locator = LocatorFactory.createLocator(current);
                    tagDescriptor.writeLocatorString(locator);
                    tagDescriptor.writeMemberAndMethods(memberNameRecorder);
                    tagDescriptorList.add(tagDescriptor);
                }

            }

            scanForUIElements(current, level);
            current = current.getNextSibling();
        }

       return tagDescriptorList;
    };


    private HashMap<String, String> setAttributePairs(Node node) {

        HashMap<String,String> attributePairs = new HashMap<String, String>();
        NamedNodeMap nodeAttributes = node.getAttributes();
        for(int i=0; i < nodeAttributes.getLength(); i++) {
            Attr attr = (Attr) nodeAttributes.item(i);
            attributePairs.put(attr.getName(), attr.getValue());
        }

        return attributePairs;

    }

}
