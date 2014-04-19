package com.testhelper;

import org.apache.log4j.Logger;


/**
 * Stored in the TagSwitcher, the TagTemplate holds the code snippets from the code template file for a given tag type.
 * For example, if a <div> is recorded having templatized code snippets in the code configuration template file,
 * then the code snippets would be stored here, and linked to the <div> tag type in a record in the Tag Switcher.
 * User: pgrandje
 * Date: 6/2/12
 */
public class TagTemplate {

    private final Logger logger = Logger.getLogger(this.getClass());

    // I think I'm not actually using the this tag member, since I already have that as the TagSwitcher key.
    private String tag;
    private String memberCode;
    private String methodCode;



    TagTemplate(String tag, String memberCode, String methodCode) {

        this.tag = tag;
        this.memberCode = memberCode;
        this.methodCode = methodCode;

        logger.trace("Created new TagTemplate with tag " + this.tag);
        logger.trace("Member code is:\n" + this.memberCode);
        logger.trace("Method code is:\n" + this.methodCode);

    }


    public String getTag() {
        return tag;
    }


    public String getMemberCode() {
        return memberCode;
    }


    public String getMethodCode() {
        return methodCode;
    }

}
