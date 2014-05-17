package com.pagerunner.pageobjects;

/* Generated Page Object source file */

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;


public class BeganWithUnderscore_CNN_com_Breaking_News_U_S_World   {

   @FindBy(id = "hdr-banner-title ")
   private WebElement null_a;

   @FindBy(css = "body > div:nth-child(4) > div:nth-child(2) > div > div:nth-child(2) > ul > li ")
   private WebElement null2LiListItem;

   @FindBy(css = "body > div:nth-child(4) > div:nth-child(2) > div > div:nth-child(2) > ul > li > span > a ")
   private WebElement null3_a;

   public String getnullLinkText() {
      return null_a.getText();
   }

   public void clicknull() {
      null_a.click();
   }

   public String getnull2LiListItemText() {
      return null2LiListItem.getText();
   }

   public String getnull3LinkText() {
      return null3_a.getText();
   }

   public void clicknull3() {
      null3_a.click();
   }


}
