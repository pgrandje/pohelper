package com.testhelper;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;

/**
 * TODO: Add javadoc here.
 *
 * @author Paul Grandjean
 * @version 1.0alpha
 * @since 2/7/15
 */
public class Interpreter {

    private static Interpreter singletonInterpreter;

    /* Interpreter will be created by a static factory */
    private Interpreter() {
    }

    // Interpreter will always be accessed using this factory-getter to ensure there is always only one instance.
    public static Interpreter getInterpreter() {
        if (singletonInterpreter == null) {
            singletonInterpreter = new Interpreter();
        }
        return singletonInterpreter;
    }

    public void generate(URL url, Generator.GenerateType generateType) throws PageHelperException {
        getGenerator().generate(url, generateType);
    }

    public PageDescriptor getPageDescriptor(URL url) throws PageHelperException {
        return getGenerator().getPageDescriptor(url);
    }

    public TagDescriptorList getTagDescriptors(URL url) throws PageHelperException {
        return getGenerator().getTagDescriptors(url);
    }

    public LinkDescriptorList getLinkDescriptorList(URL url) throws PageHelperException {
        return getGenerator().getLinkDescriptors(url);
    }

    private Generator getGenerator() {
        return Generator.getGenerator();
    }
}
