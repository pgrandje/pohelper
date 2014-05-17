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
   private WebElement ul_first_li_second_li__ulListBlock;

   @FindBy(css = "body > ul > li")
   private WebElement BeganWithUnderscore_first_li_LiListItem;

   @FindBy(css = "body > ul > li:nth-child(2)")
   private WebElement BeganWithUnderscore_second_li_LiListItem;

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

   public String getul_first_li_second_li__ulListBlockText() {
      return ul_first_li_second_li__ulListBlock.getText();
   }

   public String getBeganWithUnderscore_first_li_LiListItemText() {
      return BeganWithUnderscore_first_li_LiListItem.getText();
   }

   public String getBeganWithUnderscore_second_li_LiListItemText() {
      return BeganWithUnderscore_second_li_LiListItem.getText();
   }


}
