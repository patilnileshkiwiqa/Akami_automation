package com.sourcepro.Login;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.sourcepro.init.SeleniumInit;

public class LoginIndex extends SeleniumInit{

	@Parameters ({"email", "password"})
	@Test(description = "Do Login")
	public void login(String email, String password) throws IOException, AWTException{

		int numOfFailure=0;
		int step=1;

		logStep(step++,"Open URL : "+testUrl);	

		logStep(step++,"Do login with valid username and password.");
		loginVerification = loginIndexPage.doLogin(email, password);

		if (numOfFailure > 0) {
			Assert.assertTrue(false);
		} 

	}

	@Test
	public void logout() throws IOException{

		int numOfFailure=0;
		int step=1;

		logStep(step++,"Open URL : "+testUrl);	
		
	

		logStep(step++,"Click on 1st link.");
		loginVerification=loginIndexPage.logout();

		if (numOfFailure > 0) {
			Assert.assertTrue(false);
		} 

	}

}
