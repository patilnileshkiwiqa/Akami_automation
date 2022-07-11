package com.sourcepro.init;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public abstract class AbstractPage extends SeleniumInit {
	public static final int DRIVER_WAIT = 15;

	protected AbstractPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, DRIVER_WAIT), this);
	}
}