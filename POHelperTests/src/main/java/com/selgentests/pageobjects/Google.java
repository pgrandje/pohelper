package com.selgentests.pageobjects;
// TODO:  Need to programatically generate the package name.

/* Generated Page Object source file */

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;


public class Google   {



   // Anchor tag (<a>)
   @FindBy(css = "body > div > div > nobr > a")
   private WebElement Images;




   // Anchor tag (<a>)
   @FindBy(css = "body > div > div > nobr > a:nth-child(3)")
   private WebElement Maps;




   // Anchor tag (<a>)
   @FindBy(css = "body > div > div > nobr > a:nth-child(4)")
   private WebElement Play;




   // Anchor tag (<a>)
   @FindBy(css = "body > div > div > nobr > a:nth-child(5)")
   private WebElement YouTube;




   // Anchor tag (<a>)
   @FindBy(css = "body > div > div > nobr > a:nth-child(6)")
   private WebElement News;




   // Anchor tag (<a>)
   @FindBy(css = "body > div > div > nobr > a:nth-child(7)")
   private WebElement Gmail;




   // Anchor tag (<a>)
   @FindBy(css = "body > div > div > nobr > a:nth-child(8)")
   private WebElement Drive;




   // Anchor tag (<a>)
   @FindBy(css = "body > div > div > nobr > a:nth-child(9)")
   private WebElement More_;




   // Anchor tag (<a>)
   @FindBy(css = "body > div > div:nth-child(2) > nobr > a")
   private WebElement Web_History;




   // Anchor tag (<a>)
   @FindBy(css = "body > div > div:nth-child(2) > nobr > a:nth-child(5)")
   private WebElement Settings;




   // Anchor tag (<a>)
   @FindBy(id = "gb_70")
   private WebElement Sign_in;




   // Anchor tag (<a>)
   @FindBy(css = "body > center > form > table > tbody > tr > td:nth-child(3) > a")
   private WebElement Advanced_search;




   // Anchor tag (<a>)
   @FindBy(css = "body > center > form > table > tbody > tr > td:nth-child(3) > a:nth-child(2)")
   private WebElement Language_tools;




   // Anchor tag (<a>)
   @FindBy(css = "body > center > span > div > div > a")
   private WebElement Advertising_Programs;




   // Anchor tag (<a>)
   @FindBy(css = "body > center > span > div > div > a:nth-child(2)")
   private WebElement Business_Solutions;




   // Anchor tag (<a>)
   @FindBy(css = "body > center > span > div > div > a:nth-child(3)")
   private WebElement BeganWithUnderscore_Google;




   // Anchor tag (<a>)
   @FindBy(css = "body > center > span > div > div > a:nth-child(4)")
   private WebElement About_Google;




   // Paragraph <p> element
   @FindBy(css = "body > center > span > p")
   private WebElement BeganWithUnderscore_2013_Privacy_Terms;




   // Anchor tag (<a>)
   @FindBy(css = "body > center > span > p > a")
   private WebElement Privacy_Terms;


   public String getImagesLinkText() {
      return Images.getText();
   }

   public void clickImages() {
      Images.click();
   }


   public String getMapsLinkText() {
      return Maps.getText();
   }

   public void clickMaps() {
      Maps.click();
   }


   public String getPlayLinkText() {
      return Play.getText();
   }

   public void clickPlay() {
      Play.click();
   }


   public String getYouTubeLinkText() {
      return YouTube.getText();
   }

   public void clickYouTube() {
      YouTube.click();
   }


   public String getNewsLinkText() {
      return News.getText();
   }

   public void clickNews() {
      News.click();
   }


   public String getGmailLinkText() {
      return Gmail.getText();
   }

   public void clickGmail() {
      Gmail.click();
   }


   public String getDriveLinkText() {
      return Drive.getText();
   }

   public void clickDrive() {
      Drive.click();
   }


   public String getMore_LinkText() {
      return More_.getText();
   }

   public void clickMore_() {
      More_.click();
   }


   public String getWeb_HistoryLinkText() {
      return Web_History.getText();
   }

   public void clickWeb_History() {
      Web_History.click();
   }


   public String getSettingsLinkText() {
      return Settings.getText();
   }

   public void clickSettings() {
      Settings.click();
   }


   public String getSign_inLinkText() {
      return Sign_in.getText();
   }

   public void clickSign_in() {
      Sign_in.click();
   }


   public String getAdvanced_searchLinkText() {
      return Advanced_search.getText();
   }

   public void clickAdvanced_search() {
      Advanced_search.click();
   }


   public String getLanguage_toolsLinkText() {
      return Language_tools.getText();
   }

   public void clickLanguage_tools() {
      Language_tools.click();
   }


   public String getAdvertising_ProgramsLinkText() {
      return Advertising_Programs.getText();
   }

   public void clickAdvertising_Programs() {
      Advertising_Programs.click();
   }


   public String getBusiness_SolutionsLinkText() {
      return Business_Solutions.getText();
   }

   public void clickBusiness_Solutions() {
      Business_Solutions.click();
   }


   public String getBeganWithUnderscore_GoogleLinkText() {
      return BeganWithUnderscore_Google.getText();
   }

   public void clickBeganWithUnderscore_Google() {
      BeganWithUnderscore_Google.click();
   }


   public String getAbout_GoogleLinkText() {
      return About_Google.getText();
   }

   public void clickAbout_Google() {
      About_Google.click();
   }


   public String getBeganWithUnderscore_2013_Privacy_TermsParagraphText() {
      return BeganWithUnderscore_2013_Privacy_Terms.getText();
   }


   public String getPrivacy_TermsLinkText() {
      return Privacy_Terms.getText();
   }

   public void clickPrivacy_Terms() {
      Privacy_Terms.click();
   }



}
