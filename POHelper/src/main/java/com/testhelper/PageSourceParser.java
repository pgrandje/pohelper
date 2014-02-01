package com.testhelper;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: pgrandje
 * Date: 10/30/12
 * Time: 9:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class PageSourceParser {

    private Document pageSource;
    private Element rootElement;


    // TODO:  Does it make sense to catch these and report, or to just rethrow them?
    // IOException comes from cleaner.clean(url), ParserConfigurationException comes from DomSerializer
    public PageSourceParser(URL url) throws IOException, ParserConfigurationException {

        // create an instance of HtmlCleaner and configure it.
        HtmlCleaner cleaner = new HtmlCleaner();
        // take default cleaner properties
        CleanerProperties props = cleaner.getProperties();
        props.setAllowMultiWordAttributes(true);
        //TODO: props.setPruneTags(arg0);  //use this later when I know what to prune.
        props.setOmitComments(true);

        TagNode nodes = cleaner.clean(url);
        // Get the page source into a Document object.
        pageSource = new DomSerializer(props, true).createDOM(nodes);

    }


    public Document getDom() {
        return pageSource;
    }

    // TODO:  Review the different between an Element and a Node.  Which should I use?
    public  Element getRootNode() {
        // Get the root element and scan the nodes of the page.
        rootElement = pageSource.getDocumentElement();
        return rootElement;
    }

}
