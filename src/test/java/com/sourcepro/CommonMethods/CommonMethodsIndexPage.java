package com.sourcepro.CommonMethods;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.sourcepro.init.AbstractPage;
import com.sourcepro.init.Common;

public class CommonMethodsIndexPage extends AbstractPage {

	public CommonMethodsIndexPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	public CommonMethodsVerification clickOnMenu(String sidemenu) {
		String xpath = "//span[text()='" + sidemenu + "']";

		Common.pause(1);
		Common.clickableElement(By.xpath(xpath), driver);
		driver.findElement(By.xpath(xpath)).click();

		return new CommonMethodsVerification(driver);
	}

	public CommonMethodsVerification clickOnMenuWithChild(String menu) {
		Common.pause(5);
		String xpath = "//a[text()='"+menu+"']";
		
		Common.pause(1);
		Common.presenceOfElement(By.xpath(xpath), driver);
		Common.clickableElement(By.xpath(xpath), driver);
		Common.clickOn(driver, driver.findElement(By.xpath(xpath)));

		return new CommonMethodsVerification(driver);
	}

	public WebDriver redirectOnMainMenu() {
		System.out.println("--------------->" + testUrl);
		driver.navigate().to("https://google.com");
		return driver;
	}

	// SideBar Menu Xpath
	// a[@id='inventory_sidebar_link']
	// a[@id='about_sidebar_link']
	// a[@id='logout_sidebar_link']
	// a[@id='reset_sidebar_link']

}
