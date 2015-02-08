package com.pagehelper;

import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Recursive function, the Node Scanner traverses the DOM and creates objects and initiates actions based on the DOM
 * nodes it encounters.  This is the main engine driving the code generating process.
 * @author Paul Grandjean
 * @since 10/23/11
 * @version 1.0alpha
 */
public class PageScanner {

    private final Logger logger = Logger.getLogger(PageScanner.class);

    private URL url;

    // w3c.org Document object for the page's DOM.
    private Document document;

    // Returns code for a given tag.
    private TagSwitcher tagSwitcher;

    // Records names used for members to avoid duplicates.
    private NameRecorder memberNameRecorder;

    private PageElementsContainer pageElementsContainer;
    private LinkDescriptorList linkDescriptorList;
    private TagDescriptorList tagDescriptorList;

    // TagSwitcher throws the IOException when it can't find it's configuration file.
    // TODO:  Find out where PageScanner is throwing a ParserConfigurationException and see if I still need to do this.
    public PageScanner(URL url) throws PageHelperException {

        this.url = url;

        // Load a Lookup 'switcher' data-structure from the config file that defines the tag-->code translations.
        this.tagSwitcher = new TagSwitcher();

        this.linkDescriptorList = new LinkDescriptorList();
        this.tagDescriptorList = new TagDescriptorList();
        this.pageElementsContainer = new PageElementsContainer();
        // Set the references to the lists in the pageElementsContainer here in the contructor so the returned
        // data structure will be ready to return regardless of what the scan finds.  However, the individual
        // lists will be interacted with directly when they're needed.  The container will .
        pageElementsContainer.setLinkDescriptorList(linkDescriptorList);
        pageElementsContainer.setTagDescriptorList(tagDescriptorList);
        this.memberNameRecorder = new NameRecorder("Member Name Recorder");

        this.parsePage();
    }



    // IOException comes from cleaner.clean(url), ParserConfigurationException comes from DomSerializer
    private void parsePage() throws PageHelperException {

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
            throw new PageHelperException("Host or page not found.  Using url: " + url.toString() + ". Exception message: " + e.getMessage());
        }
        catch (ConnectException e) {
                throw new PageHelperException("Connection problem using url: " + url.toString() + ". Exception message: " + e.getMessage());
        } catch (IOException e) {
            throw new PageHelperException("I/O Exception caught while running HTMLCleaner: " + e.getMessage());
        }

        // Get the page source into a Document object.
        try {
            document = new DomSerializer(props, true).createDOM(nodes);
        } catch (ParserConfigurationException e) {
            throw new PageHelperException("Cannot parse the DOM: " + e.getMessage());
        }

    }


    public PageDescriptor getPageName(NameRecorder classNameRecorder) {

        PageDescriptor pageDescriptor = null;

        // Get all <title> tags--hopefully there's one and only one.
        NodeList titleTagList = document.getElementsByTagName("title");

        if (null == titleTagList) {
            throw new PageHelperException("Retrieving <title> returned a null list.");
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

    public PageElementsContainer scanPage() {
        return scanForUIElements(document.getDocumentElement(), 0);
    }


    // After scanning the page source, returns a list of all the nodes we want code for along with their code snippets.
    private PageElementsContainer scanForUIElements(Node parent, int level)
    {
        logger.info("Entered scanForUIElements using parent node '" + parent.getNodeName() + "' at Level " + level);

        /* This stops the recursion when it finds either a node with no children, or a non-tag node like text or an attribute.
           text, attributes, values, etc will be processed differently from nodes.  Since they never have children they
           also must cause the recursion to stop. */
        if (parent.getNodeType() != org.w3c.dom.DocumentType.ELEMENT_NODE)
            return pageElementsContainer;
        else if (!parent.hasChildNodes())
            return pageElementsContainer;


        /* This should now be an Element, as all non-elements were just caught
             above.  Text will always be a child if the Cleaner did it's
             job correctly. */
        Node current = parent.getFirstChild();
        level++;

        while (current != null)
        {
            // If the node doesn't have attributes or text we won't store it for generating code.
            // getTextContent seems to return an empty string, rather than null when there's not text.
            if (    (current.getNodeType() == 1) &&
                    ( (current.hasAttributes()) || (!current.getTextContent().isEmpty()) )
               ) {

                logger.info("Current Node Name " + current.getNodeName() + " -- Value: "
                    + current.getNodeValue() + " -- Node type: " + current.getNodeType());

                // If crawl is set store accumulate the links as we go scan the page.
                if ((current.getNodeName().equalsIgnoreCase("a")) && (Configurator.getConfigurator().isCrawlEnabled())) {
                    logger.info("Found a link.");
                    storeLink(current);
                }

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
                    tagDescriptor.setTextValue(current.getTextContent());
                    Locator locator = LocatorFactory.makeLocator(current);
                    tagDescriptor.setLocator(locator);
                    tagDescriptor.writeMemberAndMethods(memberNameRecorder);
                    tagDescriptorList.add(tagDescriptor);
                }

            }

            scanForUIElements(current, level);
            current = current.getNextSibling();
        }

       return pageElementsContainer;
    }

    private HashMap<String, String> setAttributePairs(Node node) {

        HashMap<String,String> attributePairs = new HashMap<String, String>();
        NamedNodeMap nodeAttributes = node.getAttributes();
        for(int i=0; i < nodeAttributes.getLength(); i++) {
            Attr attr = (Attr) nodeAttributes.item(i);
            attributePairs.put(attr.getName(), attr.getValue());
        }

        return attributePairs;

    }

    private void storeLink(Node linkNode) {

        // Find the href to get the url.
        LinkDescriptor newLink = new LinkDescriptor();
        NamedNodeMap nodeAttributes = linkNode.getAttributes();
        for(int i=0; i < nodeAttributes.getLength(); i++) {
            Attr attrib = (Attr) nodeAttributes.item(i);
            if (attrib.getName().equalsIgnoreCase("href")){
                // TODO: Put in logger here showing the href it's storing.
                newLink.setUrl(attrib.getValue());
            }
        }

        newLink.setAttributes(setAttributePairs(linkNode));
        linkDescriptorList.add(newLink);
    }
}
