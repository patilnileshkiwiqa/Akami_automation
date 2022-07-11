package com.sourcepro.reports;

import com.aventstack.extentreports.ExtentTest;

public class ExtentManager {

	private ExtentManager() {
	}

	static ThreadLocal<ExtentTest> extTest = new ThreadLocal<>();

	static void setExtentTest(ExtentTest test) {
		extTest.set(test);
	}

	public static ExtentTest getExtentTest() {
		return extTest.get();
	}

	static void unload() {
		extTest.remove();
	}
}