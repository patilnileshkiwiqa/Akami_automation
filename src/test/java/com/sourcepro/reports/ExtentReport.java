package com.sourcepro.reports;

import java.util.Objects;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReport {

	private ExtentReport() {
	}

	private static ExtentReports extent;
	static ExtentTest test;

	public static void initReport() {
		if (Objects.isNull(extent)) {
			extent = new ExtentReports();
			ExtentSparkReporter spark = new ExtentSparkReporter("extReport.html");
			extent.attachReporter(spark);
			spark.config().setTheme(Theme.STANDARD);
			spark.config().setDocumentTitle("SourcePro");
			spark.config().setReportName("Functional");
			spark.config().thumbnailForBase64(true);
		}
	}

	public static void flushReports() {
		if (Objects.nonNull(extent)) {
			extent.flush();
		}
	}

	public static void createTest(String testcaseName) {
		test = extent.createTest(testcaseName);
		ExtentManager.setExtentTest(test);
	}
}
