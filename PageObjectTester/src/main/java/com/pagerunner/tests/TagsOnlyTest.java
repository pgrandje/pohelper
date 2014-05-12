package com.pagerunner.tests;

import com.pagerunner.pageobjects.TagsOnly;
import com.pagerunner.testbase.TestBase;
import com.pagerunner.utils.TestException;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: pgrandje
 * Date: 5/6/13
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagsOnlyTest extends TestBase {

    @Test
    void h1Test() throws TestException {

		TagsOnly tagsOnlyPage = PageFactory.initElements(driver, TagsOnly.class);

		Assert.assertEquals(tagsOnlyPage.getuiElement1Text(), "header 1");
        Assert.assertEquals(tagsOnlyPage.getuiElement2Text(), "header 2");
        Assert.assertEquals(tagsOnlyPage.getuiElement3ParagraphText(), "paragraph");
        Assert.assertEquals(tagsOnlyPage.getuiElement4_ulListBlockText(), "ul\nfirst li\nsecond li");
        Assert.assertEquals(tagsOnlyPage.getuiElement5LiListItemText(), "first li");
        Assert.assertEquals(tagsOnlyPage.getuiElement6LiListItemText(), "second li");
	}

}
