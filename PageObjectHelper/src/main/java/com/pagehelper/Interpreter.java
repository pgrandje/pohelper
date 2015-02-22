package com.pagehelper;

import com.pagehelper.outputbucket.CodeOutputBucket;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add javadoc here.
 *
 * @author Paul Grandjean
 * @version 1.0alpha
 * @since 2/7/15
 */
public class Interpreter {

    private static Interpreter singletonInterpreter;

    // Writeable objects

    private PageDescriptor pageDescriptor;
    private TagDescriptorList writeList;

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

    public boolean hasAttributes(TagDescriptor tagDescriptor) {
        return !tagDescriptor.getAttributePairs().isEmpty();
    }

    public String getAttributePairs(TagDescriptor tagDescriptor) {

        StringBuilder pairs = new StringBuilder();

        HashMap<String, String> attributePairs = tagDescriptor.getAttributePairs();
        if (!attributePairs.isEmpty()) {
            for (Map.Entry attributePair : attributePairs.entrySet()) {
                pairs.append(attributePair.getKey() + "=" + attributePair.getValue() + " ");
            }
            pairs.append("\n");
        }

        return pairs.toString();
    }

    public void setWriteList(PageDescriptor pageDescriptor) {
        this.pageDescriptor = pageDescriptor;
        writeList = new TagDescriptorList();
    }

    public void addToWriteList(TagDescriptor tagDescriptor) {
        writeList.add(tagDescriptor);
    }

    public void dumpWriteList(PageDescriptor pageDescriptor) {
        // Need to setup the code bucket before scanning the page in case we need to abort due to accidental file overwrite.
        // TODO: I could decouple the File path processing from the CodeBucket to allow more flexibility on where the CodeBucket gets created.
        CodeOutputBucket codeBucket = CodeOutputBucket.getBucket();
        codeBucket.setFilePath();
        codeBucket.setFileName(pageDescriptor.getPageObjectName());
        codeBucket.setPageObjectName(pageDescriptor.getPageObjectName());
        getGenerator().writeCodeFromTagDescriptors(codeBucket, writeList);
    }

    private Generator getGenerator() {
        return Generator.getGenerator();
    }
}
