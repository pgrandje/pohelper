package com.testhelper;

import org.apache.log4j.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: pgrandje
 * Date: 6/2/12
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
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
