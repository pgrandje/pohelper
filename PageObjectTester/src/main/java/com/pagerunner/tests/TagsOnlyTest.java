package com.pagerunner.tests;

import com.pagerunner.pageobjects.TagsOnly;
import com.pagerunner.testbase.TestBase;
import com.pagerunner.utils.TestException;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This test verifies the page object webelements are generated correctly for specific html tags.
 * Tests for tags only.  That is, tags with no attributes.  Also varying the positions of tags are not
 * tested here.  This is the most basic test proved.
 *
 * This tests the page object generated when the PageObjectHelper runs against the standard test page tagtests.html
 * Verifies by checking the text for each tag defined in tagtest.html is correctly returned to the test by
 * the generated page object.
 *
 * @author : Paul Grandjean
 * Date: 5/6/13
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
