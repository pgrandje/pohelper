package com.pagerunner.pageobjects;

/* Generated Page Object source file */

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;


public class TagsOnly   {

   @FindBy(css = "body > h1")
   private WebElement uiElement1_h1;

   @FindBy(css = "body > h2")
   private WebElement uiElement2_h2;

   @FindBy(css = "body > p")
   private WebElement uiElement3_pTag;

   @FindBy(css = "body > ul")
   private WebElement uiElement4_ulListBlock;

   @FindBy(css = "body > ul > li")
   private WebElement uiElement5LiListItem;

   @FindBy(css = "body > ul > li:nth-child(2)")
   private WebElement uiElement6LiListItem;

   public String getuiElement1Text() {
      return uiElement1_h1.getText();
   }

   public String getuiElement2Text() {
      return uiElement2_h2.getText();
   }

   public String getuiElement3ParagraphText() {
      return uiElement3_pTag.getText();
   }

   public String getuiElement4_ulListBlockText() {
      return uiElement4_ulListBlock.getText();
   }

   public String getuiElement5LiListItemText() {
      return uiElement5LiListItem.getText();
   }

   public String getuiElement6LiListItemText() {
      return uiElement6LiListItem.getText();
   }


}
