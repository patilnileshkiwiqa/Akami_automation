package com.sourcepro.reports;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.sourcepro.init.Common;
import com.sourcepro.init.SeleniumInit;

public class ExtentReportListener extends SeleniumInit implements ITestListener, ISuiteListener {

	@Override
	public void onStart(ISuite suite) {
		ExtentReport.initReport();
	}

	@Override
	public void onFinish(ISuite suite) {
		ExtentReport.flushReports();
	}

	@Override
	public void onTestStart(ITestResult result) {
//		ExtentReport.createTest(result.getMethod().getDescription()); //This will add test name from the @Test(@Description)
		ExtentReport.createTest(result.getTestContext().getName());
		
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		ExtentManager.getExtentTest().log(Status.PASS,
				MarkupHelper.createLabel(result.getTestContext().getName() + " is passed.", ExtentColor.GREEN));
	}

	@Override
	public void onTestFailure(ITestResult result) {
		ExtentManager.getExtentTest()
				.fail(MediaEntityBuilder.createScreenCaptureFromBase64String(Common.getBase64Image(driver)).build());
		ExtentManager.getExtentTest().log(Status.FAIL,
				MarkupHelper.createLabel(result.getTestContext().getName() + " is failed.", ExtentColor.RED));
		Throwable exception = result.getThrowable();
		boolean hasThrowable = exception != null;
		if (hasThrowable) {
			ExtentManager.getExtentTest().log(Status.FAIL, exception);
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		ExtentManager.getExtentTest().log(Status.SKIP,
				MarkupHelper.createLabel(result.getTestContext().getName() + " is skipped.", ExtentColor.YELLOW));
		Throwable exception = result.getThrowable();
		boolean hasThrowable = exception != null;
		if (hasThrowable) {
			ExtentManager.getExtentTest().log(Status.SKIP, exception);
		}
	}
}
