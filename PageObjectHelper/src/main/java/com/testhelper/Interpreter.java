package com.testhelper;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

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

    public void generate(URL url, Generator.GenerateType generateType) throws IOException, ParserConfigurationException {
        getGenerator().generate(url, generateType);
    }

    public TagDescriptorList getTagDescriptors(URL url) throws IOException, ParserConfigurationException {
        return getGenerator().getTagDescriptors(url);
    }

    private Generator getGenerator() {
        return Generator.getGenerator();
    }

}
