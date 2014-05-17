package com.pagerunner.pageobjects;

/* Generated Page Object source file */

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;


public class TagsOnly   {

   @FindBy(css = "body > h1")
   private WebElement header_1_h1;

   @FindBy(css = "body > h2")
   private WebElement header_2_h2;

   @FindBy(css = "body > p")
   private WebElement paragraph_pTag;

   @FindBy(css = "body > div")
   private WebElement div_div;

   @FindBy(css = "body > ul")
   private WebElement ul_ulListBlock;

   @FindBy(css = "body > ul > li")
   private WebElement first_liLiListItem;

   @FindBy(css = "body > ul > li:nth-child(2)")
   private WebElement second_liLiListItem;

   public String getheader_1Text() {
      return header_1_h1.getText();
   }

   public String getheader_2Text() {
      return header_2_h2.getText();
   }

   public String getparagraphParagraphText() {
      return paragraph_pTag.getText();
   }

   public String getdivDivText() {
      return div_div.getText();
   }

   public String getul_ulListBlockText() {
      return ul_ulListBlock.getText();
   }

   public String getfirst_liLiListItemText() {
      return first_liLiListItem.getText();
   }

   public String getsecond_liLiListItemText() {
      return second_liLiListItem.getText();
   }


}
