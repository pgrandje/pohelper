package com.pagerunner.pageobjects;

/* Generated Page Object source file */

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class TagsOnly   {

   @FindBy(css = "body > h1")
   private WebElement uiElement1_h1;

   @FindBy(css = "body > h2")
   private WebElement uiElement2_h2;

   @FindBy(css = "body > p")
   private WebElement uiElement3_pTag;

   @FindBy(css = "body > div")
   private WebElement uiElement4_div;

   @FindBy(css = "body > ul")
   private WebElement uiElement5_ulListBlock;

   @FindBy(css = "body > ul > li")
   private WebElement uiElement6LiListItem;

   @FindBy(css = "body > ul > li:nth-child(2)")
   private WebElement uiElement7LiListItem;

   public String getuiElement1Text() {
      return uiElement1_h1.getText();
   }

   public String getuiElement2Text() {
      return uiElement2_h2.getText();
   }

   public String getuiElement3ParagraphText() {
      return uiElement3_pTag.getText();
   }

   public String getuiElement4DivText() {
      return uiElement4_div.getText();
   }

   public String getuiElement5_ulListBlockText() {
      return uiElement5_ulListBlock.getText();
   }

   public String getuiElement6LiListItemText() {
      return uiElement6LiListItem.getText();
   }

   public String getuiElement7LiListItemText() {
      return uiElement7LiListItem.getText();
   }


}
