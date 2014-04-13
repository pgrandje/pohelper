package com.selgentests.tests;

import com.selgentests.pageobjects.Tag_Tests;
import com.selgentests.testbase.TestBase;
import com.selgentests.utils.TestException;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: pgrandje
 * Date: 5/6/13
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagOnlyNoAttributesTest extends TestBase {

    @Test
    void h1Test() throws TestException {

//		LoggerUtils.recordCurrentMethod(logger);
		Tag_Tests p1TagTestsPage = PageFactory.initElements(driver, Tag_Tests.class);

		Assert.assertEquals(p1TagTestsPage.getH1_no_attributesText(), "H1 no attributes");
//        Assert.assertEquals(p1TagTestsPage.getSecond_h1_tag_at_same_sibling_leText(), "Second h1 tag at same sibling level");
//        Assert.assertEquals(p1TagTestsPage.getH2_no_attributesText(), "H2 no attributes");
//        Assert.assertEquals(p1TagTestsPage.getSecond_h2_tag_at_same_sibling_leText(), "Second h2 tag at same sibling level");
//        Assert.assertEquals(p1TagTestsPage.getparagraph_no_attributesParagraphText(), "paragraph no attributes");

	}

}
