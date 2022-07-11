package com.sourcepro.init;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.sourcepro.utility.TestData;

/**
 * Define Common Webdriver
 */
public class Common {
	Date date = new Date();
	protected static Wait<WebDriver> wait;
	public static String alerttext;

	/**
	 * Find web-element for given locator.
	 *
	 * @param driver      the driver
	 * @param elementName the element name
	 * @return the web element
	 */
	public static WebElement findElement(WebDriver driver, String elementName) {
		String locator;
		locator = elementName;
		int count = 0;
		while (count < 4) {
			try {
				if (locator.startsWith("link=") || locator.startsWith("LINK=")) {
					locator = locator.substring(5); // remove "link=" from
					try {
						if (locator.contains(" "))
							return driver.findElement(By.partialLinkText(locator));

						return driver.findElement(By.linkText(locator));
					} catch (Exception e) {
						return null;
					}
				}
				if (locator.startsWith("id=")) {
					locator = locator.substring(3); // remove "id=" from locator
					try {
						return driver.findElement(By.id(locator));
					} catch (Exception e) {
						return null;
					}
				} else if (locator.startsWith("//")) {
					try {
						return driver.findElement(By.xpath(locator));
					} catch (Exception e) {
						return null;
					}
				} else if (locator.startsWith("css=")) {
					locator = locator.substring(4); // remove "css=" from locator
					try {
						return driver.findElement(By.cssSelector(locator));
					} catch (Exception e) {
						return null;
					}
				} else if (locator.startsWith("name=")) {
					locator = locator.substring(5); // remove "name=" from locator
					try {
						return driver.findElement(By.name(locator));
					} catch (Exception e) {
						return null;
					}
				} else {
					try {
						return driver.findElement(By.id(locator));
					} catch (Exception e) {
						return null;
					}
				}
			} catch (StaleElementReferenceException e) {
				e.toString();
				count = count + 1;
			}
			count = count + 4;
		}
		return null;
	}

	public static void moveToObjectelement(WebDriver driver, String xpath) {
		driver.switchTo().frame(driver.findElement(By.xpath(xpath)));
	}

	public static String getNextDate(String format, int currentToNextDate) {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, currentToNextDate);
		date = c.getTime();
		return formatter.format(date);
	}

	/**
	 * Perform horizontal scrolling.
	 *
	 * @param driver the driver
	 */
	public static void scrollUpToBottom(WebDriver driver) {
		JavascriptExecutor js = ((JavascriptExecutor) driver);
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	public static void scrollUpToElement(WebDriver driver, WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public static void scrollUpTop(WebDriver driver) {
		JavascriptExecutor js = ((JavascriptExecutor) driver);
		js.executeScript("window.scrollTo(0, 0)");
	}

	public static void scrollToMiddle(WebDriver driver, WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'})", element);
	}

	public static void scrollToHorizontal(WebDriver driver, WebElement element) {
		Actions dragger = new Actions(driver);
		WebElement draggablePartOfScrollbar = element;
		// drag downwards
		int numberOfPixelsToDragTheScrollbarDown = 50;
		for (int i = 10; i < 500; i = i + numberOfPixelsToDragTheScrollbarDown) {
			try {
				// this causes a gradual drag of the scroll bar, 10 units at a
				// time
				dragger.moveToElement(draggablePartOfScrollbar).clickAndHold()
						.moveByOffset(numberOfPixelsToDragTheScrollbarDown, 0).release().perform();
				Thread.sleep(1000L);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Perform vertical scrolling.
	 *
	 * @param driver  the driver
	 * @param element the element
	 */
	// js executor
	public static void scrollToVertical(WebDriver driver, WebElement element) {
		Actions dragger = new Actions(driver);
		WebElement draggablePartOfScrollbar = element;
		// drag downwards
		int numberOfPixelsToDragTheScrollbarDown = 50;
		for (int i = 10; i < 500; i = i + numberOfPixelsToDragTheScrollbarDown) {
			try {
				// this causes a gradual drag of the scroll bar, 10 units at a
				// time
				dragger.moveToElement(draggablePartOfScrollbar).clickAndHold()
						.moveByOffset(0, numberOfPixelsToDragTheScrollbarDown).release().perform();
				Thread.sleep(1000L);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void logcase(String msg) {
		System.out.println(msg);
		Reporter.log("<strong> <h3 style=\"color:DarkViolet\"> " + msg + "</h3> </strong>");
	}

	public static void logstep(String msg) {
		System.out.println(msg);
		Reporter.log("<strong>" + msg + "</strong></br>");
	}

	/**
	 * Checks checkbox or toggle element.
	 * 
	 * @param element Checkbox element.
	 */
	public static void checkChkBox(WebElement element) {
		boolean isCheckBoxSet;
		isCheckBoxSet = element.isSelected();
		if (!isCheckBoxSet) {
			element.click();
		}
	}

	public static void movetoalertAndAccept(WebDriver webDriver) {
		try {
			waitForAlert(webDriver);
			Alert alert = webDriver.switchTo().alert();
			alerttext = alert.getText();
			System.out.println("alert----:" + alerttext);
			alert.accept();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getalertText(WebDriver webDriver) {
		try {
			Alert alert = webDriver.switchTo().alert();
			alerttext = alert.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alerttext;
	}

	public static void waitForAlert(WebDriver driver) {
		int i = 0;
		while (i++ < 5) {
			try {
				driver.switchTo().alert();
				break;
			} catch (Exception e) {
				pause(2);
				continue;
			}
		}
	}

	public static String getBeforeAndAfterDateMonthWiseForSingleDigit(int i) {
		Calendar cur = Calendar.getInstance();
		NumberFormat f = new DecimalFormat("0");
		cur.add(Calendar.MONTH, i);
		return f.format((cur.get(Calendar.MONTH)) + 1) + "/" + f.format(cur.get(Calendar.DATE)) + "/"
				+ cur.get(Calendar.YEAR);
	}

	public static void acceptUnhandledAlert(WebDriver webDriver) {
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = webDriver.switchTo().alert();
			log("Alert Message: " + alert.getText());
			alert.accept();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int randomBetween(int minimum, int maximum) {
		return random.nextInt(maximum - minimum + 1) + minimum;
	}

	/**
	 * Open Mailinator.
	 *
	 * @param driver       the driver
	 * @param emailAddress the email address
	 */
	public static void openMailinator(WebDriver driver, String emailAddress) {
		pause(3);
		String[] emailParsed = emailAddress.split("@");
		String url = "http://" + emailParsed[0] + ".mailinator.com";
		goToUrl(driver, url);
	}

	/**
	 * Gets current time in the following format Month, Date, Hours, Minutes,
	 * Seconds, Millisecond.
	 * 
	 * @return Current date.
	 */
	static String getCurrentTimeStampString() {
		java.util.Date date = new java.util.Date();
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmssSS");
		TimeZone timeZone = TimeZone.getDefault();
		Calendar cal = Calendar.getInstance(new SimpleTimeZone(timeZone.getOffset(date.getTime()), "IST"));
		sd.setCalendar(cal);
		return sd.format(date);
	}

	public static String getCurrentTimeStampString2() {
		java.util.Date date = new java.util.Date();
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
		TimeZone timeZone = TimeZone.getDefault();
		Calendar cal = Calendar.getInstance(new SimpleTimeZone(timeZone.getOffset(date.getTime()), "IST"));
		sd.setCalendar(cal);
		return sd.format(date);
	}

	/**
	 * Takes screenshot and adds it to TestNG report.
	 *
	 * @param driver         WebDriver instance.
	 * @param screenshotName the screenshot name
	 */
	static void makeScreenshot(WebDriver driver, String screenshotName) {
		WebDriver augmentedDriver = new Augmenter().augment(driver);
		/* Take a screenshot */
		File screenshot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
		String nameWithExtention = screenshotName + ".png";
		/* Copy screenshot to specific folder */
		try {
			String reportFolder = "test-output" + File.separator;
			String screenshotsFolder = "screenshots";
			File screenshotFolder = new File(reportFolder + screenshotsFolder);
			if (!screenshotFolder.getAbsoluteFile().exists()) {
				screenshotFolder.mkdir();
			}
			FileUtils.copyFile(screenshot,
					new File(screenshotFolder + File.separator + nameWithExtention).getAbsoluteFile());
		} catch (IOException e) {
			log("Failed to capture screenshot: " + e.getMessage());
		}
//		return getScreenshotLink(nameWithExtention, nameWithExtention);
//		<a href='../test-output/screenshots/" + screenshotName + "' target='_new'>" + linkText + "</a>"
		log("<a href='../test-output/screenshots/" + nameWithExtention
				+ "' target='new'><img src='../test-output/screenshots/" + nameWithExtention
				+ "' width='200' height='100'/></a>");
	}

	public static void captureScreenshot(WebDriver driver) {
		/* Take a screenshot */
		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String screenshotName = driver.getTitle().replaceAll("[^a-zA-Z0-9]", "_") + getCurrentTimeStampString();
		String nameWithExtention = screenshotName + ".png";
		String screenshotsFolder = System.getProperty("scr.folder");
		/* Copy screenshot to specific folder */
		try {
			File screenshotFolder = new File(screenshotsFolder);
			if (!screenshotFolder.getAbsoluteFile().exists()) {
				screenshotFolder.mkdir();
			}
			FileUtils.copyFile(screenshot,
					new File(screenshotFolder + File.separator + nameWithExtention).getAbsoluteFile());
		} catch (IOException e) {
			log("Failed to capture screenshot: " + e.getMessage());
		}
//		log("<a href='../" + screenshotsFolder + "/" + nameWithExtention + "' target='new'><img src='../"
//				+ screenshotsFolder + "/" + nameWithExtention + "' width='200' height='100'/></a>");
	}

	public static String getBase64Image(WebDriver driver) {
		return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
	}

	public static void makeScreenshot2(WebDriver driver, String screenshotName) {
		WebDriver augmentedDriver = new Augmenter().augment(driver);
		/* Take a screenshot */
		File screenshot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
		String nameWithExtention = screenshotName + ".png";
		/* Copy screenshot to specific folder */
		try {
			String reportFolder = "test-output" + File.separator;
			String screenshotsFolder = "screenshots";
			File screenshotFolder = new File(reportFolder + screenshotsFolder);
			if (!screenshotFolder.getAbsoluteFile().exists()) {
				screenshotFolder.mkdir();
			}
			FileUtils.copyFile(screenshot,
					new File(screenshotFolder + File.separator + nameWithExtention).getAbsoluteFile());
		} catch (IOException e) {
			new SeleniumInit();
			Reporter.log("Failed to capture screenshot: " + e.getMessage());
		}
		log("<b>Please look to the screenshot - </b>" + getScreenshotLink2(nameWithExtention, nameWithExtention)
				+ "<br>");
	}

	/**
	 * Log given message to Reporter output.
	 * 
	 * @param msg Message/Log to be reported.
	 */
	public static void log(String msg) {
		System.out.println(msg);
		Reporter.log(msg + "</br>");
	}

	public static void logStatus(String status) {
		System.out.println(status);
		if (status.equalsIgnoreCase("Pass")) {
			log("<br><Strong><font color=#008000>Pass</font></strong></br>");
		} else if (status.equalsIgnoreCase("Fail")) {
			log("<br><Strong><font color=#FF0000>Fail</font></strong></br>");
		}
	}

	/**
	 * Generates link for TestNG report.
	 *
	 * @param screenshotName the screenshot name
	 * @param linkText       the link text
	 * @return Formatted link for TestNG report.
	 */
	public static String getScreenshotLink(String screenshotName, String linkText) {
		return "<a href='../test-output/screenshots/" + screenshotName + "' target='_new'>" + linkText + "</a>";
	}

	public static String getScreenshotLink2(String screenshotName, String linkText) {
		String dataFilePath = "test-output/screenshots";
		File datafile = new File(dataFilePath);
		String fullpath = datafile.getAbsolutePath();
		return "<a href='" + fullpath + "/" + screenshotName + "' target='_new'>" + linkText + "</a>";
	}

	/**
	 * Checks whether the needed WebElement is displayed or not.
	 * 
	 * @param element Needed element
	 * 
	 * @return true or false.
	 */
	public static boolean isElementDisplayed(WebElement element) {
		try {
			return element.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isElemenEnabled(WebElement element) {
		try {
			return element.isEnabled();
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isElementNotDisplayed(WebElement element) {
		try {
			return !element.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Wait(max. 1 minute) till given element does not disappear from page.
	 *
	 * @param by Locator of element.
	 * @return true, if successful
	 */
	public static boolean waitForElementIsDisplayed(WebElement by) {
		for (int second = 0;; second++) {
			if (second >= 60) {
				break;
			}
			try {
				if (isElementDisplayed(by)) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			pause(1);
		}
		return false;
	}

	/**
	 * Checks if given elements is checked.
	 *
	 * @param driver  the driver
	 * @param locator Locator of element.
	 * @return true if checked else false.
	 */
	public static boolean isChecked(WebDriver driver, String locator) {
		return findElement(driver, locator).isSelected();
	}

	/**
	 * Checks whether the needed WebElement is displayed or not.
	 *
	 * @param driver         the driver
	 * @param elementLocator the element locator
	 * @return true, if is element displayed
	 */
	public static boolean isElementDisplayed(WebDriver driver, By elementLocator) {
		try {
			return driver.findElement(elementLocator).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Set data in to clipboard.
	 *
	 * @param string the new clipboard data
	 */
	public static void setClipboardData(String string) {
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}

	/**
	 * Checks whether the visibility of Element Located.
	 *
	 * @param by the by
	 * @return the expected condition
	 */
	public static ExpectedCondition<WebElement> visibilityOfElementLocated(final By by) {
		return new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				WebElement element = driver.findElement(by);
				return element.isDisplayed() ? element : null;
			}
		};
	}

	/**
	 * Wait up to String locator present.
	 *
	 * @param driver the driver
	 * @param xpath  the xPath
	 */
	public static void waitForElement1(WebDriver driver, String xpath) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			wait.until(visibilityOfElementLocated(By.xpath(xpath)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void waitForElement(WebElement webelement, WebDriver driver) {
		(new WebDriverWait(driver, Duration.ofSeconds(20))).until(ExpectedConditions.visibilityOf(webelement));
	}

	public static void waitForInvisibility(WebElement webElement, WebDriver driver) {
		(new WebDriverWait(driver, Duration.ofSeconds(20))).until(ExpectedConditions.invisibilityOf(webElement));
	}

	public static void presenceOfElement(By locator, WebDriver driver) {
		(new WebDriverWait(driver, Duration.ofSeconds(20))).until(ExpectedConditions.presenceOfElementLocated(locator));
	}

	public static void clickableElement(By locator, WebDriver driver) {
		(new WebDriverWait(driver, Duration.ofSeconds(20))).until(ExpectedConditions.elementToBeClickable(locator));
	}

	public static void clickableElement(WebElement webelement, WebDriver driver) {
		(new WebDriverWait(driver, Duration.ofSeconds(20))).until(ExpectedConditions.elementToBeClickable(webelement));
	}

	public static void waitForIframe(String str, WebDriver driver) {
		(new WebDriverWait(driver, Duration.ofSeconds(20)))
				.until(ExpectedConditions.visibilityOfElementLocated((By.cssSelector(str))));
	}

	public static void switchToIframeByCss(String str, WebDriver driver) {
		(new WebDriverWait(driver, Duration.ofSeconds(10)))
				.until(ExpectedConditions.visibilityOfElementLocated((By.cssSelector(str))));
		driver.switchTo().frame(driver.findElement(By.cssSelector(str)));
	}

	/**
	 * Finds handle to second window other than given handle to current window and
	 * switches to as well.
	 *
	 * @param driver              the driver
	 * @param handleCurrentWindow the handle current window
	 * @return handleSecondWindow
	 */
	public static String findAndSwitchToSecondWindow(WebDriver driver, String handleCurrentWindow) {
		pause(3);
		Set<String> windows = driver.getWindowHandles();
		String handleSecondWindow = null;
		for (String window : windows) {
			if (!window.contains(handleCurrentWindow)) {
				handleSecondWindow = window;
			}
		}
		// Switch to the second window.
		try {
			pause(4);
			driver.switchTo().window(handleSecondWindow);
		} catch (Throwable failure) {
			// If there is problem in switching window, then re-try.
			pause(3);
			driver.switchTo().window(handleSecondWindow);
		}
		return handleSecondWindow;
	}

	public static String findAndSwitchToSecondWindow(WebDriver driver, String handleCurrentWindow,
			String handleCurrentWindow1) {
		pause(3);
		Set<String> windows = driver.getWindowHandles();
		String handleSecondWindow = null;
		for (String window : windows) {
			if (!window.contains(handleCurrentWindow) && !window.contains(handleCurrentWindow1)) {
				handleSecondWindow = window;
			}
		}
		// Switch to the second window.
		try {
			pause(4);
			driver.switchTo().window(handleSecondWindow);
		} catch (Throwable failure) {
			// If there is problem in switching
			// window, then re-try.
			pause(3);
			driver.switchTo().window(handleSecondWindow);
		}
		return handleSecondWindow;
	}

	public static void closeOtherWindow(WebDriver driver, String handleCurrentWindow) {
		Set<String> windows = driver.getWindowHandles();
		for (String window : windows) {
			if (!window.contains(handleCurrentWindow)) {
				driver.close();
			}
		}
		driver.switchTo().window(handleCurrentWindow);
	}

	public static String findAndSwitchToSecondWindows(WebDriver driver, String handleCurrentWindow,
			String anotherWindow) {
		pause(3);
		Set<String> windows = driver.getWindowHandles();
		String handleSecondWindow = null;
		for (String window : windows) {
			if (!window.contains(handleCurrentWindow) && !window.contains(anotherWindow)) {
				handleSecondWindow = window;
			}
		}
		try {
			pause(4);
			driver.switchTo().window(handleSecondWindow);
		} catch (Throwable failure) {
			// If there is problem in switching
			pause(3);
			driver.switchTo().window(handleSecondWindow);
		}
		return handleSecondWindow;
	}

	/**
	 * Select data from drop down or combo box by Value.
	 *
	 * @param element the element
	 * @param value   the value
	 */
	public static void selectFromCombo(WebElement element, String value) {
		Select select = new Select(element);
		select.selectByValue(value);
	}

	/**
	 * Select data form drop down or combo box by visible element.
	 *
	 * @param element the element
	 * @param value   the value
	 */
	public static void selectFromComboByVisibleElement(WebElement element, String value) {
		Select select = new Select(element);
		select.selectByVisibleText(value);
	}

	public static void selectFromComboByIndex(WebElement element, int index) {
		Select select = new Select(element);
		select.selectByIndex(index);
	}

	public static String selectFromComboByIndexReturnValue(WebElement element, int index) {
		Select select = new Select(element);
		select.selectByIndex(index);
		pause(1);
		return select.getFirstSelectedOption().getText();
	}

	public static String selectFromComboByIndexReturnValue(WebElement element) {
		Select select = new Select(element);
		return select.getFirstSelectedOption().getText();
	}

	static String mmddyyyy = "MM/dd/yyyy";

	public static String getFridayDateOfParticularMonth(String year, String month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(year), Integer.parseInt(month), 1);
		cal.add(Calendar.DAY_OF_MONTH, -(cal.get(Calendar.DAY_OF_WEEK) % 7 + 1));
		SimpleDateFormat format = new SimpleDateFormat(mmddyyyy);
		Date d1 = cal.getTime();
		return format.format(d1);
	}

	/**
	 * Wait up to By element present.
	 *
	 * @param driver  the driver
	 * @param element the element
	 */
	public static void waitForElement(WebDriver driver, By element) {
		try {
			wait = new WebDriverWait(driver, Duration.ofMinutes(1));
			wait.until(visibilityOfElementLocated(element));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clicks on visible or not visible element.
	 *
	 * @param driver  the driver
	 * @param element Web element.
	 */
	public static void jsClick(WebDriver driver, WebElement element) {
		highlightElement(driver, element);
		((JavascriptExecutor) driver).executeScript("return arguments[0].click();", element);
	}

	/**
	 * Highlight the element and click on same.
	 *
	 * @param driver  the driver
	 * @param element the element
	 */
	public static void clickOn(WebDriver driver, WebElement element) {
		mouseOver(driver, element);
		highlightElement(driver, element);
		element.click();
	}

	/**
	 * Generates random symbols;.
	 *
	 * @param length Length of the generated symbols.
	 * @return StringBuffer object.
	 */
	public static String generateRandomChars(int length) {
		return RandomStringUtils.randomAlphabetic(length);
	}

	/**
	 * Generate Random Number in Length.
	 *
	 * @param length the length
	 * @return the string
	 */
	public static String generateRandomNumber(int length) {
		String numNoRange = "";
		do {
			numNoRange = RandomStringUtils.randomNumeric(length);
		} while (numNoRange.length() != length);
		return numNoRange;
	}

	/**
	 * Mouse Hover in Web element.
	 *
	 * @param driver  the driver
	 * @param element the element
	 */
	public static void mouseOver(WebDriver driver, WebElement element) {
		Actions builder = new Actions(driver);
		builder.moveToElement(element).build().perform();
	}

	/**
	 * Get text in a given element.
	 *
	 * @param driver      the driver
	 * @param elementName Locator of element.
	 * @return text in given element.
	 */
	public static String getText(WebDriver driver, String elementName) {
		String text;
		try {
			text = findElement(driver, elementName).getText();
		} catch (Exception e) {
			text = "Element was not found";
		}
		return text;
	}

	/**
	 * Get text in of given Element using JavaScript.
	 *
	 * @param driver  the driver
	 * @param element webElement
	 * @return the text JS
	 */
	public static String getTextJS(WebDriver driver, WebElement element) {
		return (String) ((JavascriptExecutor) driver).executeScript("return jQuery(arguments[0]).text();", element);
	}

	/**
	 * Get value of given element dynamically.
	 *
	 * @param driver  the driver
	 * @param locator Locator of element.
	 * @return Dynamic value.
	 */
	public String getValue(WebDriver driver, String locator) {
		return findElement(driver, locator).getAttribute("value");
	}

	/**
	 * Checks if given element is being displayed on page.
	 *
	 * @param driver      the driver
	 * @param elementName Locator of element.
	 * @return true if displayed else false.
	 */
	public static boolean isElementDisplayed(WebDriver driver, String elementName) {
		WebElement webElement;
		try {
			webElement = findElement(driver, elementName);
			return webElement.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Wait till given element is present.
	 *
	 * @param driver  the driver
	 * @param locator Locator of element.
	 */
	public static void waitForConditionIsElementPresent(WebDriver driver, String locator) {
		for (int second = 0;; second++) {
			if (second >= 10) {
				break;
			}
			try {
				if (isElementPresent(driver, locator)) {
					break;
				}
			} catch (Throwable failure) {
				failure.printStackTrace();
			}
			pause(1000);
		}
	}

	/**
	 * Checks if element loaded in browser memory.
	 *
	 * @param driver  the driver
	 * @param locator Locator of element.
	 * @return true if loaded else false.
	 */
	public static boolean isElementPresent(WebDriver driver, String locator) {
		WebElement webElement = findElement(driver, locator);
		if (webElement != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Pauses for given seconds.
	 *
	 * @param secs the seconds
	 */
	public static void pause(int secs) {
		try {
			Thread.sleep(secs * 1000);
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}

	static Random random = new Random();

	/**
	 * Get random numeric of given length
	 *
	 * @param length the length
	 * @return the int
	 */
	public static int randomNumericValueGenerate(int length) {
		return random.nextInt(length);
	}

	/**
	 * Clears and type new value into given text-box.
	 *
	 * @param webElement the web element
	 * @param value      New text/value.
	 */
	public static void type(WebElement webElement, String value) {
		webElement.clear();
		webElement.sendKeys(value);
	}

	public static void type(WebDriver driver, WebElement webElement, String value) {
		mouseOver(driver, webElement);
		highlightElement(driver, webElement);
		webElement.sendKeys(value);
	}

	/**
	 * Wait till all ajax calls finish.
	 *
	 * @param driver the driver
	 * @param num    Number of ajax calls to finish.
	 */
	public static void waitForAjax(WebDriver driver, String num) {
		String ajax;
		ajax = ajaxFinised(driver, num);
		for (int second = 0;; second++) {
			if (second >= 20) {
				break;
			} else if (ajax.equals("true")) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Wait till ajax call finish.
	 *
	 * @param driver the driver
	 * @throws InterruptedException the interrupted exception
	 */
	public void waitForAjax(WebDriver driver) throws InterruptedException {
		String ajax;
		ajax = ajaxFinised(driver, "1");
		for (int second = 0;; second++) {
			if (second >= 15) {
				break;
			} else if (ajax.equals("true")) {
				break;
			}
			Thread.sleep(1000);
		}
	}

	/**
	 * Checks that all ajax calls are completed on page.
	 *
	 * @param driver the driver
	 * @param num    Number of ajax calls to wait for completion.
	 * @return "true" if completed else "false".
	 */
	public static String ajaxFinised(WebDriver driver, String num) {
		Object isAjaxFinished;
		JavascriptExecutor js = (JavascriptExecutor) driver;
		isAjaxFinished = js.executeScript("return jQuery.active == " + num);
		return isAjaxFinished.toString();
	}

	/**
	 * Select Random String From Combo box.
	 *
	 * @param by     the by
	 * @param driver the driver
	 * @return selected random string
	 * @throws InterruptedException the interrupted exception
	 */
	public static String selectRandomOptionFromCombo(By by, WebDriver driver) throws InterruptedException {
		String selectedOption = "";
		WebElement selectCombo = driver.findElement(by);
		Thread.sleep(2);
		List<WebElement> getAllOption = selectCombo.findElements(By.tagName("option"));
		ArrayList<String> arrayOfAllOption = new ArrayList<>();
		for (WebElement ele : getAllOption) {
			if (!ele.getText().startsWith("Select")) {
				arrayOfAllOption.add(ele.getText());
			}
		}
		int index = random.nextInt(arrayOfAllOption.size());
		if (Integer.signum(index) == -1) {
			index = -index;
		}
		selectedOption = arrayOfAllOption.get(index);
		return selectedOption;
	}

	/**
	 * Get Total Number Of Elements.
	 *
	 * @param driver the driver
	 * @param by     the by
	 * @return integer number of total elements
	 */
	public static int getNumOfElements(WebDriver driver, By by) {
		int i = 0;
		List<WebElement> ele = driver.findElements(by);
		i = ele.size();
		return i;
	}

	/**
	 * Refresh Current Page.
	 *
	 * @param driver the driver
	 */
	public static void refresh(WebDriver driver) {
		driver.navigate().refresh();
	}

	static String osName = "os.name";

	/**
	 * Open URL in New Tab.
	 *
	 * @param driver the driver
	 * @param url    the url
	 */
	public static void openUrlInNewTab(WebDriver driver, String url) {
		if (System.getProperty(osName).equalsIgnoreCase("Mac OS X")) {
			driver.findElement(By.tagName("body")).sendKeys(Keys.COMMAND + "t");
		} else {
			driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "t");
		}
		driver.get(url);
	}

	/**
	 * Close Current Tab In Web Browser.
	 *
	 * @param driver the driver
	 */
	public static void closeCurrentTab(WebDriver driver) {
		if (System.getProperty(osName).equalsIgnoreCase("Mac OS X")) {
			driver.findElement(By.tagName("body")).sendKeys(Keys.COMMAND + "w");
		} else {
			driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "w");
		}
	}

	/**
	 * Perform Mouse Hover on element.
	 *
	 * @param driver the driver
	 * @param ele    the element
	 */
	public static void mouseHover(WebDriver driver, WebElement ele) {
		Actions action = new Actions(driver);
		action.moveToElement(ele).build().perform();
	}

	public static void focusOnElement(WebDriver driver, WebElement ele) {
		Actions action = new Actions(driver);
		action.moveToElement(ele).perform();
	}

	/**
	 * Perform Mouse Hover using javaSript executor.
	 *
	 * @param driver the driver
	 * @param ele    the element
	 */
	public static void mouseHoverUsingJS(WebDriver driver, WebElement ele) {
		String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover',true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
		((JavascriptExecutor) driver).executeScript(mouseOverScript, ele);
	}

	public static void jsType(WebDriver driver, WebElement element, String value) {
		String je = "return arguments[0].value='" + value + "';";
		((JavascriptExecutor) driver).executeScript(je, element);
	}

	/**
	 * Go to URL.
	 *
	 * @param driver the driver
	 * @param url    the url
	 */
	public static void goToUrl(WebDriver driver, String url) {
		driver.get(url);
	}

	/**
	 * Go to previous page.
	 *
	 * @param driver the driver
	 */
	public static void goToPreviuosPage(WebDriver driver) {
		driver.navigate().back();
	}

	/**
	 * Highlight Element.
	 *
	 * @param driver  the driver
	 * @param element the element
	 */
	public static void highlightElement(WebDriver driver, WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].style.border = '3px solid yellow'", element);
		pause(2);
	}

	/**
	 * Stop page loading.
	 *
	 * @param driver the driver
	 */
	public static void stopPageLoading(WebDriver driver) {
		driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
	}

	public static boolean isElementPresent(WebDriver driver, By identifier) {
		return !driver.findElements(identifier).isEmpty();
	}

	public static String getBeforeAndAfterDateMonthWise(int i) {
		Calendar cur = Calendar.getInstance();
		NumberFormat f = new DecimalFormat("00");
		cur.add(Calendar.MONTH, i);
		return f.format((cur.get(Calendar.MONTH)) + 1) + "/" + f.format(cur.get(Calendar.DATE)) + "/"
				+ cur.get(Calendar.YEAR);
	}

	public static String getBeforeAndAfterDateMonthWise(int i, int n) {
		Calendar cur = Calendar.getInstance();
		NumberFormat f = new DecimalFormat("00");
		cur.add(Calendar.MONTH, i);
		return f.format((cur.get(Calendar.MONTH)) + 1) + "/" + f.format(n) + "/" + cur.get(Calendar.YEAR);
	}

	public static String[] getBeforeAndAfterMonthYear(int i, int motnh, int year) {
		Calendar cur = Calendar.getInstance();
		cur.set(year, motnh, 1);
		cur.add(Calendar.MONTH, i);
		return new String[] { cur.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
				String.valueOf(cur.get(Calendar.YEAR)) };
	}

	public static int getMonthNumber(String str) {
		return Month.valueOf(str.toUpperCase()).getValue();
	}

	public static String getBeforeAndAfetrDateYearWis(int i) {
		Calendar cur = Calendar.getInstance();
		NumberFormat f = new DecimalFormat("00");
		cur.add(Calendar.YEAR, i);
		return f.format(cur.get(Calendar.MONTH)) + "/" + f.format(cur.get(Calendar.DATE)) + "/"
				+ cur.get(Calendar.YEAR);
	}

	public static String getAbsolutePathForUpload(String document) {
		return new File(document).getAbsolutePath();
	}

	public static long dateCalculation(String addStart, String addEnd) {
		String dateStart = addStart;
		String dateStop = addEnd;
		SimpleDateFormat format = new SimpleDateFormat(mmddyyyy);
		Date d1 = null;
		Date d2 = null;
		long diffDays = 0;
		try {
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);
			long diff = d2.getTime() - d1.getTime();
			diffDays = diff / (24 * 60 * 60 * 1000) + 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return diffDays;
	}

	public static int numberOfWeekDay(String addStart, String addEnd, String dayName) {
		int add = 0;
		try {
			long diffDays = dateCalculation(addStart, addEnd);

			Calendar cur = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(mmddyyyy);

			cur.setTime(format.parse(addStart));

			LocalDate date1 = LocalDate.of(2018, 1, 1);
			for (int i = 0; i < diffDays; i++) {
				if (dayName.equalsIgnoreCase(date1.getDayOfWeek().toString())) {
					add++;
				}
				date1 = date1.plusDays(1);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return add;
	}

	public static void switchtoTab(WebDriver driver, int tabNumber) {
		ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(tabNumber));
	}

	public static String filePath(String filePath) {
		String finalPath;
		finalPath = System.getProperty("user.dir") + File.separator + filePath;
		return finalPath;
	}

	/**
	 * Clears data of the text field, when normal clear() does not work.
	 *
	 * @param element the element
	 */
	public static void forcedClear(WebElement element) {
		element.sendKeys(Keys.CONTROL + "a");
		element.sendKeys(Keys.DELETE);
	}

	public static String getDate(int fromYear, int fromMonth, int fromDate, int toYear, int toMonth, int toDate) {
		LocalDate from = LocalDate.of(fromYear, fromMonth, fromDate);
		LocalDate to = LocalDate.of(toYear, toMonth, toDate);
		long days = from.until(to, ChronoUnit.DAYS);
		long randomDays = ThreadLocalRandom.current().nextLong(days + 1);
		LocalDate randomDate = from.plusDays(randomDays);

		return randomDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
	}

	public static Set<Cookie> getAllCookies(WebDriver driver) {
		return driver.manage().getCookies();
	}

	public static void addAllCookies(WebDriver driver, Set<Cookie> cookies) {
		for (Cookie ck : cookies) {
			driver.manage().addCookie(ck);
		}
	}

	public static void closeActiveWindowAndSwitchToNew(WebDriver driver, String url) {
		String oldWindowHandleID = driver.getWindowHandle();
		driver.switchTo().newWindow(WindowType.WINDOW);
		driver.manage().window().maximize();
		String newWindowHandleID = driver.getWindowHandle();
		driver.get(url);
		driver.switchTo().window(oldWindowHandleID);
		driver.close();
		driver.switchTo().window(newWindowHandleID);
	}

	public static void selectYear(WebDriver driver, WebElement dateBox, String year) {
		clickableElement(dateBox, driver);
		scrollToMiddle(driver, dateBox);
		pause(3);
		clickOn(driver, dateBox);
		WebElement selectedYear = driver.findElement(By.xpath(
				"//div[contains(@class,'dropdown') and not(contains(@class,'hidden'))]//button[contains(@class,'year')]"));
		if (!selectedYear.getText().contentEquals(year)) {
			scrollToMiddle(driver, selectedYear);
			clickOn(driver, selectedYear);
			WebElement yearToSelect = driver
					.findElement(By.xpath("//div[contains(@class,'year')]//td[@title='" + year + "']"));
			clickableElement(yearToSelect, driver);
			scrollToMiddle(driver, yearToSelect);
			clickOn(driver, yearToSelect);
		}

	}

	public static void selectMonth(WebDriver driver, String month) {
		WebElement monthToSelect = driver
				.findElement(By.xpath("//div[contains(@class,'month')]//div[text()='" + month + "']"));
		clickableElement(monthToSelect, driver);
		scrollToMiddle(driver, monthToSelect);
		clickOn(driver, monthToSelect);
	}

	public static void selectFullDate(WebDriver driver, WebElement dateBox, String fullDate) {
		String year = fullDate.split("-")[2];
		String month = fullDate.split("-")[1];
		String date = fullDate.split("-")[0];

		selectYear(driver, dateBox, year);
		WebElement selectedMonth = driver.findElement(By.xpath(
				"//div[contains(@class,'dropdown') and not(contains(@class,'hidden'))]//button[contains(@class,'month')]"));
		if (!selectedMonth.getText().contentEquals(month)) {
			scrollToMiddle(driver, selectedMonth);
			clickOn(driver, selectedMonth);
			selectMonth(driver, month);
		}

		if (date.startsWith("0")) {
			date = date.substring(1);
		}
		WebElement dateToSelect = driver.findElement(By.xpath(
				"//div[contains(@class,'dropdown') and not(contains(@class,'hidden'))]//td[contains(@class,'in-view')]/div[text()='"
						+ date + "']"));
		clickableElement(dateToSelect, driver);
		scrollToMiddle(driver, dateToSelect);
		clickOn(driver, dateToSelect);
	}

	public static void selectCardExpiryDate(WebDriver driver, WebElement dateBox, String fullDate) {
		String year = fullDate.split("-")[2];
		String month = fullDate.split("-")[1];
		selectYear(driver, dateBox, year);
		selectMonth(driver, month);
	}

	public static String readPasswordProtectedPDF(String pdfFile, String password) throws IOException {
		PDDocument pdfDocument = PDDocument.load(new File(pdfFile), password);
		PDFTextStripper pdfStripper = new PDFTextStripper();
		pdfDocument.close();

		return pdfStripper.getText(pdfDocument);
	}

	public static void toggleMenu(WebDriver driver) throws IOException {
		String browserName = TestData.getValueFromConfig("config.properties", "Browser");
		if (StringUtils.containsIgnoreCase(browserName, "Mobile")
				&& !driver.findElements(By.xpath("//button[contains(@class,'toggler collapsed')]")).isEmpty()) {
			pause(10);
			WebElement toggleMenuBtn = driver.findElement(By.xpath("//button[contains(@class,'toggler collapsed')]"));
			clickOn(driver, toggleMenuBtn);
		}
	}

	public static String getCVV() {
		return Integer.toString(randomBetween(100, 899));
	}
}