package com.sourcepro.init;

import java.awt.AWTException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.sourcepro.reports.ExtentLogger;
import com.sourcepro.reports.ExtentManager;
import com.sourcepro.utility.TestData;

import com.sourcepro.CommonMethods.CommonMethodsIndexPage;
import com.sourcepro.CommonMethods.CommonMethodsVerification;
import com.sourcepro.Login.LoginIndexPage;
import com.sourcepro.Login.LoginVerification;



import com.aventstack.extentreports.MediaEntityBuilder;
import com.browserstack.local.Local;


public class SeleniumInit {
	private String suiteName = "";
	private String testName = "";
	public static String testUrl;
	public static String browserName;
	protected String testDataFolderPath;
	public static String browserVersion = "";
	protected String screenshotFolderPath = null;
	protected WebDriver driver;
	private Local l;

	//Object Creation
	protected LoginIndexPage loginIndexPage;
	protected LoginVerification loginVerification;

	protected CommonMethodsVerification commonMethodsVerification;
	protected CommonMethodsIndexPage commonMethodsIndexPage;



	/**
	 * Fetch suite.
	 *
	 * @param testContext the test context
	 * @throws IOException 
	 */
	@BeforeSuite(alwaysRun = true)
	public void fetchSuite(ITestContext testContext) throws IOException {


	}

	/**
	 * Fetch suite configuration.
	 *
	 * @param testContext the test context
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@BeforeTest(alwaysRun = true)
	public void fetchSuiteConfiguration(ITestContext testContext) throws IOException {
	}

	/**
	 * SetUp.
	 *
	 * @param method      the method
	 * @param testContext the test context
	 * @param testResult  the test result
	 * @throws Exception 
	 */

	@Parameters ({"config_file", "environment", "email", "password"})
	@BeforeMethod(alwaysRun = true)
	public void setUp(String config_file, String environment, String email, String password, Method method, ITestContext testContext, ITestResult testResult) throws Exception {

		email = testContext.getCurrentXmlTest().getParameter("email");
		password = testContext.getCurrentXmlTest().getParameter("password");

		if (TestData.getValueFromConfig("config.properties", "ExecutionPlatform").equalsIgnoreCase("Browserstack")) {

			JSONParser parser = new JSONParser();
			JSONObject config = (JSONObject) parser.parse(new FileReader("conf/" + config_file));
			JSONObject envs = (JSONObject) config.get("environments");

			DesiredCapabilities capabilities = new DesiredCapabilities();

			Map<String, String> envCapabilities = (Map<String, String>) envs.get(environment);
			Iterator it = envCapabilities.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
			}

			Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
			it = commonCapabilities.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				if (capabilities.getCapability(pair.getKey().toString()) == null) {
					capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
				}
			}

			String username = System.getenv("BROWSERSTACK_USERNAME");
			if (username == null) {
				username = (String) config.get("user");
			}

			String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
			if (accessKey == null) {
				accessKey = (String) config.get("key");
			}

			if (capabilities.getCapability("browserstack.local") != null
					&& capabilities.getCapability("browserstack.local") == "true") {
				l = new Local();
				Map<String, String> options = new HashMap<String, String>();
				options.put("key", accessKey);
				l.start(options);
			}

			driver = new RemoteWebDriver(
					new URL("http://" + username + ":" + accessKey + "@" + config.get("server") + "/wd/hub"), capabilities);

			testUrl=testContext.getCurrentXmlTest().getParameter("URL");
			driver.manage().window().maximize();
			driver.get(testUrl);
			
			suiteName = testContext.getSuite().getName();

			int numOfFailure=0;
			int step=1;


			// Login
			loginIndexPage = new LoginIndexPage(driver);
			loginVerification = new LoginVerification(driver);

			commonMethodsVerification = new CommonMethodsVerification(driver);
			commonMethodsIndexPage = new CommonMethodsIndexPage(driver); 

			logStep(step++,"Open URL : "+testUrl);	

			logStep(step++,"Do login with valid username and password.");
			loginVerification = loginIndexPage.doLogin(email, password);
			
		} else {
			String execution=testContext.getCurrentXmlTest().getParameter("ExecutionByXML");

			if(execution.equalsIgnoreCase("true"))
			{
				testUrl=testContext.getCurrentXmlTest().getParameter("URL");
				browserName =testContext.getCurrentXmlTest().getParameter("Browser");
			}else
			{
				testUrl=TestData.getValueFromConfig("config.properties","URL");
				browserName =TestData.getValueFromConfig("config.properties","Browser");
			}


			try {
				TestData.deleteDirectory(new File(System.getProperty("user.dir") + "\\test-output\\screenshots"));
				TestData.deleteDirectory(new File(System.getProperty("user.dir") + "\\images"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			final String SCREENSHOT_FOLDER_NAME = "screenshots";
			final String TESTDATA_FOLDER_NAME = "test_data";
			testDataFolderPath = new File(TESTDATA_FOLDER_NAME).getAbsolutePath();
			screenshotFolderPath = new File(SCREENSHOT_FOLDER_NAME).getAbsolutePath();
			String scrFolder = "images/img_" + Common.getCurrentTimeStampString();
			new File(scrFolder).mkdir();
			System.setProperty("scr.folder", scrFolder);

			try {
				final String downloadDir = "DownloadData";
				final String headless = "Headless";

				if (StringUtils.containsIgnoreCase(browserName, "Firefox")) {
					System.setProperty("webdriver.gecko.driver", "driver/geckodriver.exe");
					FirefoxOptions options = new FirefoxOptions();
					options.setAcceptInsecureCerts(true);
					options.addPreference("browser.download.folderList", 2);
					options.addPreference("browser.helperApps.alwaysAsk.force", false);
					options.addPreference("browser.download.dir", new File(downloadDir).getAbsolutePath());
					options.addPreference("browser.download.defaultFolder", new File(downloadDir).getAbsolutePath());
					options.addPreference("browser.download.manager.showWhenStarting", false);
					options.addPreference("browser.helperApps.neverAsk.saveToDisk",
							"multipart/x-zip,application/zip,application/x-zip-compressed,application/x-compressed,application/msword,application/csv,text/csv,image/png ,image/jpeg, application/pdf, text/html,text/plain,  application/excel, application/vnd.ms-excel, application/x-excel, application/x-msexcel, application/octet-stream");
					if (StringUtils.containsIgnoreCase(browserName, headless)) {
						options.setHeadless(true);
					}
					driver = new FirefoxDriver(options);
					if (StringUtils.containsIgnoreCase(browserName, headless)) {
						driver.manage().window().setPosition(new Point(0, 0));
						driver.manage().window().setSize(new Dimension(1920, 1080));
					}
				} else if (StringUtils.containsIgnoreCase(browserName, "Edge")) {
					System.setProperty("webdriver.edge.driver", "driver/msedgedriver.exe");
					Map<String, Object> prefs = new HashMap<>();
					prefs.put("download.default_directory", new File(downloadDir).getAbsolutePath());
					prefs.put("download.prompt_for_download", false);
					prefs.put("credentials_enable_service", false);
					prefs.put("profile.password_manager_enabled", false);
					EdgeOptions options = new EdgeOptions();
					options.setExperimentalOption("prefs", prefs);
					options.addArguments("start-maximized");
					if (StringUtils.containsIgnoreCase(browserName, headless)) {
						options.addArguments(headless);
						options.addArguments("window-size=1920,1080");
					}
					if (StringUtils.containsIgnoreCase(browserName, "Mobile")) {
						Map<String, Object> deviceMetrics = new HashMap<>();
						deviceMetrics.put("width", 767);
						deviceMetrics.put("height", 640);
						deviceMetrics.put("pixelRatio", 3.0);
						Map<String, Object> mobileEmulation = new HashMap<>();
						mobileEmulation.put("deviceMetrics", deviceMetrics);
						mobileEmulation.put("userAgent",
								"Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
						options.setExperimentalOption("mobileEmulation", mobileEmulation);
					}
					options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });
					driver = new EdgeDriver(options);
				} else if (StringUtils.containsIgnoreCase(browserName, "Chrome")) {
					System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
					final ChromeOptions chromeOptions = new ChromeOptions();
					chromeOptions.addArguments("--start-maximized");
					chromeOptions.addArguments("--disable-popup-blocking");
					if (StringUtils.containsIgnoreCase(browserName, headless)) {
						chromeOptions.addArguments(headless);
						chromeOptions.addArguments("window-size=1920,1080");
					}
					if (StringUtils.containsIgnoreCase(browserName, "Mobile")) {
						Map<String, Object> deviceMetrics = new HashMap<>();
						deviceMetrics.put("width", 767);
						deviceMetrics.put("height", 640);
						deviceMetrics.put("pixelRatio", 3.0);
						Map<String, Object> mobileEmulation = new HashMap<>();
						mobileEmulation.put("deviceMetrics", deviceMetrics);
						mobileEmulation.put("userAgent",
								"Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
						chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
					}
					chromeOptions.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });
					chromeOptions.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
					Map<String, Object> prefs = new HashMap<>();
					prefs.put("download.default_directory", new File(downloadDir).getAbsolutePath());
					prefs.put("credentials_enable_service", false);
					prefs.put("profile.password_manager_enabled", false);
					chromeOptions.setExperimentalOption("prefs", prefs);
					driver = new ChromeDriver(chromeOptions);
				}
				Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
				browserVersion = cap.getBrowserVersion();
				// suiteName = testContext.getSuite().getName();
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//				/* Fix for script to run on Chrome 98 */
//				String originalHandle = driver.getWindowHandle();
//				for (String handle : driver.getWindowHandles()) {
//					if (!handle.equals(originalHandle)) {
//						driver.switchTo().window(handle);
//						driver.close();
//					}
//				}
//				driver.switchTo().window(originalHandle);

			} catch (Exception e) {
				e.printStackTrace();
			}

			suiteName = testContext.getSuite().getName();

			int numOfFailure=0;
			int step=1;

			driver.get(testUrl);

			// Login
			loginIndexPage = new LoginIndexPage(driver);
			loginVerification = new LoginVerification(driver);

			commonMethodsVerification = new CommonMethodsVerification(driver);
			commonMethodsIndexPage = new CommonMethodsIndexPage(driver); 

//			String email = testContext.getCurrentXmlTest().getParameter("email");
//			String password = testContext.getCurrentXmlTest().getParameter("password");
			
			logStep(step++,"Open URL : "+testUrl);	

			logStep(step++,"Do login with valid username and password.");
			loginVerification = loginIndexPage.doLogin(email, password);

		}
		
	}

	/**
	 * Tear down.
	 *
	 * @param testResult  the test result
	 * @param testContext the test context
	 * @throws Exception 
	 */
	@AfterMethod(alwaysRun = true)
	public void tearDown(ITestResult testResult, ITestContext testContext) throws Exception {
		String screenshotName = "";
		testName = testContext.getName();

		try {
			Reporter.setCurrentTestResult(testResult);
			if (!testResult.isSuccess()) {
				System.out.println();
				System.out.println("TEST FAILED - " + testName);
				System.out.println();
				System.out.println("ERROR MESSAGE: " + testResult.getThrowable());
				System.out.println("\n");
				if (testResult.getStatus() == ITestResult.FAILURE) {
					System.out.println("1 message from tear down");
					screenshotName = "finalScreenshot_" + Common.getCurrentTimeStampString();
					Common.makeScreenshot(driver, screenshotName);
					slog("<Strong><b>" + testName + " is failed.</b></font></strong>");
				}
			} else if (testResult.getStatus() == ITestResult.SUCCESS) {
				System.out.println("1 message from tear down");
				slog("<Strong><b>" + testName + " is passed.</b></font></strong>");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		driver.manage().deleteAllCookies();
		 driver.quit();
	        if (l != null) {
	            l.stop();
	        }
	}

	@AfterSuite(alwaysRun = true)
	public void postConfigue()
	{

	}

	/**
	 * Log given message to Reporter output.
	 * 
	 * @param msg Message/Log to be reported.
	 */
	//	@AfterSuite(alwaysRun = true)
	//	public void postConfigue() {
	//		driver.manage().deleteAllCookies();
	//		driver.close();
	//	}

	protected void log(String msg) {
		ExtentLogger.log(msg);
		slog(msg);
	}

	protected static void slog(String msg) {
		Reporter.log(msg + "</br>");
		System.out.println(msg);
	}

	public void logStep(int msg1, String msg2) 
	{
		//Reporter.log("Step-"+msg1+" : "+msg2 + "</br>");
		Reporter.log(msg2 + "</br>");
		System.out.println("Step-"+msg1+" : "+msg2);// for jenkins  
	}

	public void logStatus(ITestStatus testStatus, String msg) {
		switch (testStatus) {
		case PASSED:
			ExtentLogger.pass(msg);
			slog(msg + " <Strong><font color=#32cd32><b> &#10004 Pass</b></font></strong>");
			Common.captureScreenshot(driver);
			break;
		case FAILED:
			String screenshotName = Common.getCurrentTimeStampString();
			ExtentLogger.fail(msg);
			ExtentManager.getExtentTest().fail(
					MediaEntityBuilder.createScreenCaptureFromBase64String(Common.getBase64Image(driver)).build());
			slog(msg + " <Strong><font color=#dc3545><b> &#10008 Fail</b></font></strong>");
			Common.makeScreenshot(driver, screenshotName);
			break;
		case SKIPPED:
			log(msg);
			break;
		default:
			break;
		}
	}
}