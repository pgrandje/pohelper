package com.pagerunner.pageobjects;

/* Generated Page Object source file */

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;


public class BeganWithUnderscore_CNN_com_Breaking_News_U_S_World   {

   @FindBy(id = "ad-35d8d28e57bc6dbd ")
   private WebElement cnnad_renderAd_http_ads_cnn_com__div;

   @FindBy(id = "cnn_hdr ")
   private WebElement SET_EDITION_U_S_INTERNATIONAL_M__div;

   @FindBy(id = "cnn_hdr-prompt ")
   private WebElement cnn_hdr_prompt_div;

   public String getcnnad_renderAd_http_ads_cnn_com_DivText() {
      return cnnad_renderAd_http_ads_cnn_com__div.getText();
   }

   public String getSET_EDITION_U_S_INTERNATIONAL_M_DivText() {
      return SET_EDITION_U_S_INTERNATIONAL_M__div.getText();
   }

   public String getcnn_hdr_promptDivText() {
      return cnn_hdr_prompt_div.getText();
   }


}
