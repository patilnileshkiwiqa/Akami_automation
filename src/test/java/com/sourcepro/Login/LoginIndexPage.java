package com.sourcepro.Login;




import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import com.sourcepro.init.AbstractPage;
import com.sourcepro.init.Common;
import com.sourcepro.utility.TestData;


public class LoginIndexPage extends AbstractPage{

	public LoginIndexPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}


	//Login


	@FindBy(id="user-name")
	WebElement emailID;
	@FindBy(id="password")
	WebElement passwordtxt;
	@FindBy(id="login-button")
	WebElement loginBtn;

	public LoginVerification doLogin(String email, String password) throws AWTException, IOException
	{

		Common.clickableElement(emailID, driver);
		Common.type(emailID, email);
		Common.type(passwordtxt, password);
		Common.clickableElement(loginBtn, driver);
		Common.clickOn(driver, loginBtn);

		File file = new File("Cookies.data");							
		try		
		{	  
			// Delete old file if exists
			file.delete();		
			file.createNewFile();			
			FileWriter fileWrite = new FileWriter(file);							
			BufferedWriter Bwrite = new BufferedWriter(fileWrite);							
			// loop for getting the cookie information 		

			// loop for getting the cookie information 		
			for(Cookie ck : driver.manage().getCookies())							
			{			
				Bwrite.write((ck.getName()+";"+ck.getValue()+";"+ck.getDomain()+";"+ck.getPath()+";"+ck.getExpiry()+";"+ck.isSecure()));																									
				Bwrite.newLine();             
			}			
			Bwrite.close();			
			fileWrite.close();	

		}
		catch(Exception ex)					
		{		
			ex.printStackTrace();			
		}

		return new LoginVerification(driver);
	}


	//Logout

	@FindBy(xpath="//select[@formcontrolname='companyId']")
	WebElement selectCompany;

	public LoginVerification selectCompanyDropDown(String value) {

		Common.clickableElement(selectCompany, driver);
		Common.selectFromComboByVisibleElement(selectCompany, value);
		return new LoginVerification(driver);
	}

	@FindBy(xpath="//select[@formcontrolname='companyLocationId']")
	WebElement selectLocation;

	public LoginVerification selectLocationDropDown(String value) {
		Common.clickableElement(selectLocation, driver);
		Common.selectFromComboByVisibleElement(selectLocation, value);
		return new LoginVerification(driver);
	}

	@FindBy(xpath="//button[@type='submit']")
	WebElement clickRightArrow;

	public LoginVerification clickRightArrow() {

		Common.clickableElement(clickRightArrow, driver);
		Common.clickOn(driver, clickRightArrow);
		return new LoginVerification(driver);
	}

	@FindBy(xpath="//div[text()='Sauce Labs Backpack']")
	WebElement clickOnFirstLink;
	@FindBy(id="add-to-cart-sauce-labs-backpack")
	WebElement clickAddToCart;

	public LoginVerification logout() {

		Common.clickableElement(clickOnFirstLink, driver);
		Common.clickOn(driver, clickOnFirstLink);
		Common.clickableElement(clickAddToCart, driver);
		Common.clickOn(driver, clickAddToCart);
		return new LoginVerification(driver);
	}



}
