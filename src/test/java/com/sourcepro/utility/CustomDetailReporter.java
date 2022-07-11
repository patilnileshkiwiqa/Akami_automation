package com.sourcepro.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
//import java.util.Scanner;
import java.util.Set;

import org.testng.IInvokedMethod;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sourcepro.init.SeleniumInit;

public class CustomDetailReporter extends CustomReporterListener {

	private static final Logger L = Logger.getLogger(CustomDetailReporter.class);
	private PrintWriter m_out;
	@SuppressWarnings("unused")
	private int m_row;
	private Integer m_testIndex;
	private int m_methodIndex;
//	private Scanner scanner;
	int passCount = 0;
	private static HashMap<String, String> map = new HashMap<String, String>();
//	private static HashMap<String, String> module = new HashMap<String, String>();
	static Multimap<String, String> moduleWithTestCase = ArrayListMultimap.create();
	int namecount = 0;
	int qty_tests = 0;
	int passed = 0;
	int failNo = 0;
	int skipped = 0;
	int failedcount = 0;
	int total_a = 0;
	int qty_pass = 0;
	Date date;
	long time_end1 = Long.MIN_VALUE;
	long time_start1 = Long.MIN_VALUE;
	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy, HH:mm a z");
	SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat sdf_a = new SimpleDateFormat("h:mm:ss a");

	public CustomDetailReporter() {
		super();
		map.put("Could not start a new session. Possible causes are invalid address of the remote server or browser start-up failure.",
				"Server or Node is not running.");
		map.put("Timed out after 60 seconds waiting for visibility of Proxy element",
				"Particular Element is not located on page. 1.Page is not loaded completely 2. Element is Not found on page 3. Possibility for \"BUG\"");
		map.put("Unable to locate",
				"Particular Element is not located on page. Either Page is not loaded completely OR Element is Not found on page");
		map.put("no such element",
				"element could not be found.  Check Possibility: 1.Update automation code. 2.Element may not present on the screen. 3.Possibility for \"BUG\"");
		map.put("Timed out after 60 seconds waiting for presence of element",
				"element could not be found.  Check Possibility: 1.Update automation code. 2.Element may not present on the screen. 3.Possibility for \"BUG\"");
		map.put("Unable to bind to locking port 7054 within 45000 ms",
				"Port is already locked by other browser and may not be free.Please restart selenium node and server");
		map.put("Unexpected error launching Internet Explorer", "Unable to launch IE.");
		map.put("Unable to find element on closed window",
				"Browser window may closed unexpectedly. This will fix automatically on next run");
		map.put("Error communicating with the remote browser.",
				"remote browser may have died. Please restart selenium node and server");
		map.put("Unable to locate element: {\"method\":\"xpath\",\"selector\":",
				"xpath of the particular element getting changed OR Page is not loaded completely.");
		map.put("Error forwarding the new session Error forwarding the request Connect to",
				"Connection may be refused by the node/server. Please restart selenium node and server");
		map.put("element not visible",
				"Element is not found on page : 1.Update automation code. 2.Element may not present on the screen.");
		map.put("Timed out after 60 seconds waiting for visibility of [[AppiumDriver:",
				"Particular Element is not located on page. 1.Page is not loaded completely 2. Element is Not found on page 3. Possibility for \"BUG\"");
	}

	/** Creates summary of the run */
	@Override
	public void generateReport(List<XmlSuite> xml, List<ISuite> suites, String outdir) {
		try {
			m_out = createWriter(outdir);
		} catch (IOException e) {
			L.error("output file", e);
			return;
		}

		startHtml(m_out);
		generateSuiteSummaryReport(suites);
		totalTime(suites);
		startResultSummaryTable("methodOverview");
		beforeTest();
		generateMethodSummaryReport(suites);
		afterTest();
		beforeModueTest();
		mDetails();
		afterModuleTest();
		endHtml(m_out);
		m_out.flush();
		m_out.close();
	}

	public void beforeTest() {
		m_out.println("<div class='subview-left left'>");

		m_out.println("<div class='view-summary'>");
		m_out.println("<h5>Tests</h5>");
		m_out.println("<ul id='test-collection' class='test-collection'>");
	}

	public void afterTest() {
		m_out.println("</ul>");
		m_out.println("</div>");
		m_out.println("</div>");
		m_out.println("<div class='subview-right left'>");
		m_out.println("<div class='view-summary'>");
		m_out.println("<h5 class='test-name'></h5>");
		m_out.println("</div>");
		m_out.println("</div>");
		m_out.println("<!-- subview right -->");
		m_out.println("</div>");
	}

	public void beforeModueTest() {
		m_out.println("<div id='category-view' class='view hide'>");

		m_out.println("<section id='controls'>");
		m_out.println("<div class='controls grey lighten-4'>");
		m_out.println("<!-- search -->");
		m_out.println("<!--<div class='chip transparent' alt='Search Tests' title='Search Tests'>");
		m_out.println("<a href=\"#\" class='search-div'>");
		m_out.println("<i class='material-icons'>search</i> Search");
		m_out.println("</a>");
		m_out.println("<div class='input-field left hide'>");
		m_out.println(
				"<input id='search-tests' type='text' class='validate browser-default' placeholder='Search Tests...'>");
		m_out.println("</div>");
		m_out.println("</div>");
		m_out.println("<!-- search -->");
		m_out.println("</div>");
		m_out.println("</section>");

		m_out.println("<div class='subview-left left'>");
		m_out.println("<div class='view-summary'>");
		m_out.println("<h5>Categories</h3>");
		m_out.println("<ul id='category-collection' class='category-collection'>");
	}

	public void afterModuleTest() {
		m_out.println("</ul>");
		m_out.println("</div>");
		m_out.println("</div>");
		m_out.println("<div class='subview-right left'>");
		m_out.println("<div class='view-summary'>");
		m_out.println("<h5 class='category-name'></h5>");
		m_out.println("</div>");
		m_out.println("</div>");
		m_out.println("</div>");
	}

	public void mDetails() {
		Collection<String> arr = null;
		Collection<String> arr1 = null;
		String arg = null;
		String arg1 = null;
		int p = 0;
		int f = 0;
		Set<String> keys = moduleWithTestCase.keySet();
		// iterate through the key set and display key and values
		for (String key : keys) {

			arr1 = moduleWithTestCase.get(key);
			for (Iterator<String> i = arr1.iterator(); i.hasNext();) {
				arg1 = i.next();
				if (arg1.split("_")[1].contains("pass"))
					++p;
				else
					++f;
			}

			m_out.println("<li class='category displayed active'>");
			m_out.println("<div class='category-heading'>");
			m_out.println("<span class='category-name'>" + key + "</span>");
			m_out.println("<span class='category-status right'>");
			m_out.println("<span class='label pass'>" + p + "</span>");
			m_out.println("<span class='label fail'>" + f + "</span>");
			m_out.println("</span>");
			m_out.println("</div>");
			m_out.println("<div class='category-content hide'>");
			m_out.println("<div class='category-status-counts'>");
			m_out.println("<span class='label green accent-4 white-text'>Passed: " + p + "</span>");
			m_out.println("<span class='label red lighten-1 white-text'>Failed: " + f + "</span>");

			m_out.println("</div>");
			m_out.println("<div class='category-tests'>");
			m_out.println("<table class='bordered table-results'>");
			m_out.println("<thead>");
			m_out.println("<tr>");
			m_out.println("<th>Timestamp</th>");
			m_out.println("<th>TestName</th>");
			m_out.println("<th>Status</th>");
			m_out.println("</tr>");
			m_out.println("</thead>");
			m_out.println("<tbody>");

			arr = moduleWithTestCase.get(key);
			for (Iterator<String> i = arr.iterator(); i.hasNext();) {
				arg = i.next();
				m_out.println("<tr>");
				m_out.println("<td>" + sdf.format((new java.util.Date((Long.parseLong(arg.split("_")[2]))))) + "</td>");
				m_out.println("<td class='linked' test-id='1'>" + arg.split("_")[0] + "</td>");
				if (arg.split("_")[1].contains("pass"))
					m_out.println("<td><span class='test-status pass'>pass</span></td>");
				else
					m_out.println("<td><span class='test-status fail'>fail</span></td>");

				m_out.println("</tr>");
				p = 0;
				f = 0;
			}

			/*
			 * m_out.println("<tr>"); m_out.println("<td>Apr 30, 2020 12:01:49 PM</td>");
			 * m_out.
			 * println("<td class='linked' test-id='2'>Search - Search by Status.</td>");
			 * m_out.println("<td><span class='test-status fail'>fail</span></td>");
			 * m_out.println("<td><span class='test-status pass'>pass</span></td>");
			 * m_out.println("</tr>");
			 */

			m_out.println("</tbody>");
			m_out.println("</table>");
			m_out.println("</div>");
			m_out.println("</div>");
			m_out.println("</li>");

		}
		/*
		 * m_out.println("<li class='category displayed active'>");
		 * m_out.println("<div class='category-heading'>");
		 * m_out.println("<span class='category-name'>CreateInvoice</span>");
		 * m_out.println("<span class='category-status right'>");
		 * m_out.println("<span class='label pass'>2</span>"); m_out.println("</span>");
		 * m_out.println("</div>");
		 * m_out.println("<div class='category-content hide'>");
		 * m_out.println("<div class='category-status-counts'>"); m_out.
		 * println("<span class='label green accent-4 white-text'>Passed: 2</span>");
		 * m_out.println("</div>"); m_out.println("<div class='category-tests'>");
		 * m_out.println("<table class='bordered table-results'>");
		 * m_out.println("<thead>"); m_out.println("<tr>");
		 * m_out.println("<th>Timestamp</th>"); m_out.println("<th>TestName</th>");
		 * m_out.println("<th>Status</th>"); m_out.println("</tr>");
		 * m_out.println("</thead>"); m_out.println("<tbody>"); m_out.println("<tr>");
		 * m_out.println("<td>Apr 30, 2020 12:02:03 PM</td>"); m_out.
		 * println("<td class='linked' test-id='3'>Create Invoice - Verify Require field Validation.</td>"
		 * ); m_out.println("<td><span class='test-status pass'>pass</span></td>");
		 * m_out.println("</tr>"); m_out.println("<tr>");
		 * m_out.println("<td>Apr 30, 2020 12:02:13 PM</td>"); m_out.
		 * println("<td class='linked' test-id='4'>Create Invoice - Create Invoice with existing customer.</td>"
		 * ); m_out.println("<td><span class='test-status pass'>pass</span></td>");
		 * m_out.println("</tr>"); m_out.println("</tbody>"); m_out.println("</table>");
		 * m_out.println("</div>"); m_out.println("</div>"); m_out.println("</li>");
		 */

	}

	String time;

	public String totalTime(List<ISuite> suites) { // 4.1
		long startTime = Long.MAX_VALUE;
		long endTime = Long.MIN_VALUE;
		ITestContext overview = null;

		for (ISuite suite : suites) {
			Map<String, ISuiteResult> itests = suite.getResults();
			for (ISuiteResult r : itests.values()) {

				overview = r.getTestContext();

				startTime = Math.min(overview.getStartDate().getTime(), startTime);
				endTime = Math.max(overview.getEndDate().getTime(), endTime);

			}
		}

		NumberFormat formatter = new DecimalFormat("#,##0.0");
		time = String.valueOf(formatter.format(((endTime - startTime) / 1000.) / 60.));
		time_start1 = startTime;
		time_end1 = endTime;

		return time;
	}

	protected PrintWriter createWriter(String outdir) throws IOException {
		// java.util.Date now = new Date();
		new File(outdir).mkdirs();
		String filename = "";
		try {
			filename = getValueFromConfig("ReportConfig/report-config.properties", "FileName");
			if (filename == null || filename.equals(" ") || filename.equals(""))
				filename = "SourcePro_Report";
		} catch (Exception e) {
			filename = "SourcePro_Report";
		}

		return new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, filename + ".html"))));
	}

	/**
	 * Creates a table showing the highlights of each test method with links to the
	 * method details.
	 *
	 * @param suites the suites
	 */
	protected void generateMethodSummaryReport(List<ISuite> suites) {
		m_methodIndex = 0;
		/* startResultSummaryTable("methodOverview"); */
		int testIndex = 1;
		for (ISuite suite : suites) {
			if (suites.size() > 1) {
				titleRow(suite.getName(), 5);
			}
			Map<String, ISuiteResult> r = suite.getResults();
			for (ISuiteResult r2 : r.values()) {
				ITestContext testContext = r2.getTestContext();
				String testName = testContext.getName();
				m_testIndex = testIndex;
//				resultSummary_passed(suite, testContext.getPassedTests(), testName, "passed", "");
//				resultSummary(suite, testContext.getFailedConfigurations(), testName, "failed", " (configuration methods)");
				resultSummary(suite, testContext.getFailedTests(), testName, "failed", "");
				resultSummary(suite, testContext.getPassedTests(), testName, "Passed", "");
				resultSummary_skipped(suite, testContext.getSkippedTests(), testName, "skipped", "");
				testIndex++;
			}
		}

		// m_out.println("</table>");
	}

	protected void generateMethodSummaryReport1(List<ISuite> suites) {
		m_methodIndex = 0;
		/* startResultSummaryTable("methodOverview"); */
		int testIndex = 1;
		for (ISuite suite : suites) {
			if (suites.size() > 1) {
				titleRow(suite.getName(), 5);
			}
			Map<String, ISuiteResult> r = suite.getResults();
			for (ISuiteResult r2 : r.values()) {
				ITestContext testContext = r2.getTestContext();
				String testName = testContext.getName();
				m_testIndex = testIndex;
				resultSummary_passed(suite, testContext.getPassedTests(), testName, "passed", "");
				resultSummary_Fail(suite, testContext.getFailedTests(), testName, "failed", "");
				resultSummary(suite, testContext.getFailedConfigurations(), testName, "failed",
						" (configuration methods)");
				/*
				 * resultSummary(suite, testContext.getFailedTests(), testName, "failed", "");
				 * resultSummary(suite, testContext.getPassedTests(), testName, "Passed", "");
				 * resultSummary_skipped(suite, testContext.getSkippedTests(), testName,
				 * "skipped", ""); testIndex++;
				 */
			}
		}

		// m_out.println("</table>");
	}

	/**
	 *  Creates a section showing known results for each method.
	 *
	 * @param suites the suites
	 */
	protected void generateMethodDetailReport(List<ISuite> suites) {
		m_methodIndex = 0;
		for (ISuite suite : suites) {
			Map<String, ISuiteResult> r = suite.getResults();
			for (ISuiteResult r2 : r.values()) {
				ITestContext testContext = r2.getTestContext();
				if (r.values().size() > 0) {
					m_out.println("<h1>" + testContext.getName() + "</h1>");
				}
				resultDetail(testContext.getFailedConfigurations());
				resultDetail(testContext.getFailedTests());
			}
		}
	}

	private void resultSummary_skipped(ISuite suite, IResultMap tests, String testname, String style, String details) {

		if (tests.getAllResults().size() > 0) {

			for (@SuppressWarnings("unused")
			ITestNGMethod method : getMethodSet(tests, suite)) {

				skipped++;
			}
		}
	}

	ArrayList<String> PassedTestCases = new ArrayList<String>();

	private void resultSummary_passed(ISuite suite, IResultMap tests, String testname, String style, String details) { // 5.4.1
		if (!checkModuleName(testname.split("_")[0])) {
			moduleName.add(testname.split("_")[0]);
		}

		if (tests.getAllResults().size() > 0) {
			for (ITestNGMethod method : getMethodSet(tests, suite)) {
				long end1 = Long.MIN_VALUE;
				long start1 = Long.MAX_VALUE;
				for (ITestResult testResult : tests.getResults(method)) {
					if (testResult.getEndMillis() > end1) {
						end1 = testResult.getEndMillis();
					}
					if (testResult.getStartMillis() < start1) {
						start1 = testResult.getStartMillis();
					}
				}
				String meth = method.getMethodName();
				if (!checkpassedTestCases(meth + "_" + testname)) {
					PassedTestCases.add(meth + "_" + testname);
					moduleWithTestCase.put(testname.split("_")[0], testname.split("_")[1] + "_pass_" + start1);
					++passed;
				}
			}
		}
	}

	ArrayList<String> FailTestCases = new ArrayList<String>();

	private void resultSummary_Fail(ISuite suite, IResultMap tests, String testname, String style, String details) { // 5.4.1

		if (!checkModuleName(testname.split("_")[0])) {
			moduleName.add(testname.split("_")[0]);
		}

		if (tests.getAllResults().size() > 0) {
			for (ITestNGMethod method : getMethodSet(tests, suite)) {
				long end1 = Long.MIN_VALUE;
				long start1 = Long.MAX_VALUE;
				for (ITestResult testResult : tests.getResults(method)) {
					if (testResult.getEndMillis() > end1) {
						end1 = testResult.getEndMillis();
					}
					if (testResult.getStartMillis() < start1) {
						start1 = testResult.getStartMillis();
					}
				}
				String meth = method.getMethodName();
				if (!checkpassedTestCases(meth + "_" + testname)) {
					FailTestCases.add(meth + "_" + testname);
					moduleWithTestCase.put(testname.split("_")[0], testname.split("_")[1] + "_fail_" + start1);
					++failNo;
				}
			}
		}
	}

	// ArrayList<String> PassedTestName = new ArrayList<String>();
	public boolean checkpassedTestCases(String testName) {
		return PassedTestCases.contains(testName);
	}

	ArrayList<String> moduleName = new ArrayList<String>();

	public boolean checkModuleName(String testName) {
		return moduleName.contains(testName);
	}

	ArrayList<String> FailTestName = new ArrayList<String>();

	public boolean checkFailTestCases(String testName) {
		return FailTestCases.contains(testName);
	}

	ArrayList<String> testArray = new ArrayList<String>();
	int retry = 0;

	private void resultSummary(ISuite suite, IResultMap tests, String testname, String style, String details) {

		if (tests.getAllResults().size() > 0) {

			// StringBuffer buff = new StringBuffer();
			int mq = 0;
			@SuppressWarnings("unused")
			int cq = 0;
			for (ITestNGMethod method : getMethodSet(tests, suite)) {

				String meth = method.getMethodName();

				long end1 = Long.MIN_VALUE;
				long start1 = Long.MAX_VALUE;
				for (ITestResult testResult : tests.getResults(method)) {
					if (testResult.getEndMillis() > end1) {
						end1 = testResult.getEndMillis();
					}
					if (testResult.getStartMillis() < start1) {
						start1 = testResult.getStartMillis();
					}
				}

				if (!checkTestCases(meth + "_" + testname)) {
					testArray.add(meth + "_" + testname);
					m_row += 1;
					m_methodIndex += 1;
					{
						String id = (m_testIndex == null ? null : "t" + Integer.toString(m_testIndex));
						// m_out.print("<tr");
						if (id != null) {
							// m_out.print(" id=\"" + id + "\"");
						}

						/*
						 * m_out.println("><td width='5%'><b>"
						 * +failedcount+".</b></td><td width='25%'><b>" + testname+"<br>"+meth +
						 * "</b></td>");
						 * 
						 * m_row = 0;
						 */

						//
						if (!style.contains("Passed")) {
							failedcount++;
							m_out.println(
									"<li class='test displayed active  fail' status='fail' bdd='false' test-id='1'>");
							m_out.println("<div class='test-heading'>");
							m_out.println("<span class='test-name'>" + testname.split("_")[1] + "</span>");
							// m_out.println("<span class='test-time'>"+start1/1000+"</span>");
							m_out.println("<span class='test-time'>" + sdf.format((new java.util.Date((start1))))
									+ "</span>");
							m_out.println("<span class='label end-time'style='float: right'>Fail</span>");
						} else {
							m_out.println(
									"<li class='test displayed active  pass' status='pass' bdd='false' test-id='3'>");
							m_out.println("<div class='test-heading'>");
							m_out.println("<span class='test-name'>" + testname.split("_")[1] + "</span>");
							// m_out.println("<span class='test-time'>"+start1/1000+"</span>");
							m_out.println("<span class='test-time'>" + sdf.format((new java.util.Date((start1))))
									+ "</span>");
							m_out.println("<span class='label start-time'style='float: right'>Pass</span>");
						}
						m_out.println("</div>");
						m_out.println("<div class='test-content hide'>");
						m_out.println("<div class='test-time-info'>");
						m_out.println("<span class='label start-time'>" + sdf.format((new java.util.Date((start1))))
								+ "</span>");
						m_out.println(
								"<span class='label end-time'>" + sdf.format((new java.util.Date((end1)))) + "</span>");
						// m_out.println("<span class='label time-taken grey lighten-1
						// white-text'>"+((end1-start1)/1000)+"</span>");
						m_out.println("<span class='label time-taken grey lighten-1 white-text'>"
								+ (String.format("%02d:%02d:%02d", (((end1 - start1) / 1000) / 3600),
										((((end1 - start1) / 1000) % 3600) / 60), (((end1 - start1) / 1000) % 60)))
								+ "</span>");

						m_out.println("</div>");
						m_out.println("<div class='test-attributes'>");
						m_out.println("<div class='category-list'>");
						m_out.println("<span class='category label white-text'>" + testname.split("_")[0] + "</span>");
						m_out.println("</div>");
						m_out.println("<!--<div class='author-list'>");
						// m_out.println("<span class='author label white-text'>DharmeshPatel</span>");
						m_out.println("</div>-->");
						m_out.println("</div>");
						m_out.println("<div class='test-steps'>");
						m_out.println("<table class='bordered table-results'>");
						m_out.println("<thead>");
						m_out.println("<tr>");
						m_out.println("<th>STATUS</th>");
						m_out.println("<th>TIMESTAMP</th>");
						m_out.println("<th>DETAILS</th>");
						m_out.println("</tr>");
						m_out.println("</thead>");
						m_out.println("<tbody>");

						m_testIndex = null;
						namecount++;

					} // ----------------
					Set<ITestResult> resultSet = tests.getResults(method);
					long end = Long.MIN_VALUE;
					long start = Long.MAX_VALUE;
					for (ITestResult testResult : tests.getResults(method)) {
						if (testResult.getEndMillis() > end) {
							end = testResult.getEndMillis();
						}
						if (testResult.getStartMillis() < start) {
							start = testResult.getStartMillis();
						}
					}
					mq += 1;
					if (mq > 1) {
					}

					if (mq > 0) {
						cq += 1;
						getShortException(tests, meth, style);

					}

					@SuppressWarnings("unused")
					String description = method.getDescription();
					@SuppressWarnings("unused")
					String testInstanceName = resultSet.toArray(new ITestResult[] {})[0].getTestName();

					/*
					 * m_out.println("<td width='5%' class=\"numi\"><center>" + (end - start) / 1000
					 * + "</center></td>" + "");
					 */

				}
			}
		}
	}

	public boolean checkTestCases(String testName) {

		return testArray.contains(testName);
	}

	public boolean isPassed(String testName) {
		return PassedTestCases.contains(testName);
	}

	/**
	 *  Starts and defines columns result summary table.
	 *
	 * @param style the style
	 */
	private void startResultSummaryTable(String style) {
		tableStart(style, "summary");
		date = new Date();
		// sdf = new SimpleDateFormat("HH:mm a z,MM/dd/yyyy");
		// sdf = new SimpleDateFormat("MMM dd, yyyy, HH:mm a z");

		/*
		 * m_out.
		 * println("<tr><td bgcolor='white' colspan='5'> <table border='0' width='100%' bgcolor='#e6f7ff'><tr>"
		 * + "<td  width='25%'  bgcolor='white'>" +
		 * "<center><img width='150px' src=''/></center>" +
		 * "</td><td ><center><font color='#008bcc'><b><h1>Failed Test Cases Analysis</h1></b></font></center></td> "
		 * + "<td width='25%' bgcolor='white'>" +
		 * "<center><img width='150px' src=''/></center></td> " +
		 * "</tr></table> </td></tr>");
		 * m_out.println("<tr><td colspan='5'>Overall test suite completion : <b>" +
		 * Time + " minutes</b><br/> Date and Time of Run: <b>" + sdf.format(date) +
		 * "</b><br/> Browser : <b>"+SeleniumInit.browserName+"<t></t>" +
		 * SeleniumInit.browserVersion + "</b><br/>OS: <b>" +
		 * System.getProperty("os.name") + "</b><br/>Tested ENT URL: <b>" +
		 * SeleniumInit.testUrl + "</b> <br/> Tested Payer URL: <b>" +
		 * SeleniumInit.PayertestURL + "</b></td></tr>");
		 * m_out.println("<tr bgcolor='SkyBlue'><th>Sr. No.</th><th>Test Cases</th>" +
		 * "<th>Failure Reason</th><th>Failure Error</th><th>Total Time<br/>(sec.)</th>"
		 * );
		 */

		m_out.println("<div class='container'>");
		m_out.println("<div id='test-view' class='view'>");

		m_out.println("<section id='controls'>");
		m_out.println("<div class='controls grey lighten-4'>");
		m_out.println("<!-- test toggle -->");
		m_out.println("<div class='chip transparent'>");
		m_out.println(
				"<a class='dropdown-button tests-toggle' data-activates='tests-toggle' data-constrainwidth='true' data-beloworigin='true' data-hover='true' href='#'>");
		m_out.println("<i class='material-icons'>warning</i> Status");
		m_out.println("</a>");
		m_out.println("<ul id='tests-toggle' class='dropdown-content'>");
		m_out.println("<li status='pass'><a href='#!'>Pass <i class='material-icons green-text'>verified</i></a></li>");
		m_out.println("<li status='fail'><a href='#!'>Fail <i class='material-icons red-text'>bug_report</i></a></li>");
		m_out.println("<li class='divider'></li>");
		m_out.println(
				"<li status='clear' clear='true'><a href='#!'>Clear Filters <i class='material-icons'>clear</i></a></li>");
		m_out.println("</ul>");
		m_out.println("</div>");
		m_out.println("<!-- test toggle -->");
		m_out.println("<!-- category toggle -->");
		m_out.println("<div class='chip transparent'>");
		m_out.println(
				"<a class='dropdown-button category-toggle' data-activates='category-toggle' data-constrainwidth='false' data-beloworigin='true' data-hover='true' href='#'>");
		m_out.println("<i class='material-icons'>local_offer</i> Category");
		m_out.println("</a>");
		m_out.println("<ul id='category-toggle' class='dropdown-content'>");
		for (String mn : moduleName) {
			m_out.println("<li><a href='#'>" + mn + "</a></li>");
			/*
			 * m_out.println("<li><a href='#'>Search</a></li>");
			 * m_out.println("<li><a href='#'>CreateInvoice</a></li>");
			 */
		}
		m_out.println("<li class='divider'></li>");
		m_out.println("<li class='clear'><a href='#!' clear='true'>Clear Filters</a></li>");
		m_out.println("</ul>");
		m_out.println("</div>");
		m_out.println("<!-- category toggle -->");

		m_out.println("<!-- clear filters -->");
		m_out.println("<div class='chip transparent hide'>");
		m_out.println("<a class='' id='clear-filters' alt='Clear Filters' title='Clear Filters'>");
		m_out.println("<i class='material-icons'>close</i> Clear");
		m_out.println("</a>");
		m_out.println("</div>");
		m_out.println("<!-- clear filters -->");

		m_out.println("<!-- enable dashboard -->");
		m_out.println("<div id='toggle-test-view-charts' class='chip transparent'>");
		m_out.println("<a class='pink-text' id='enable-dashboard' alt='Enable Dashboard' title='Enable Dashboard'>");
		m_out.println("<i class='material-icons'>track_changes</i> Dashboard");
		m_out.println("</a>");
		m_out.println("</div>");
		m_out.println("<!-- enable dashboard -->");
		m_out.println("<!-- search -->");
		m_out.println("<!-- search -->");
		m_out.println("</div>");
		m_out.println("</section>");
		m_out.println("<div id='test-view-charts' class='subview-full'>");
		m_out.println("<div id='charts-row' class='row nm-v nm-h'>");
		m_out.println("<div class='col s12 m6 l6 np-h'>");
		m_out.println("<div class='card-panel nm-v'>");
		m_out.println("<center>");
		m_out.println("<div class='block text-small'>");
		m_out.println("<h5> <span class='tooltipped' data-position='top' data-tooltip='75%'><span class='strong'>"
				+ passed + "</span> test(s) passed</span> </h5>");
		m_out.println("</div>");
		m_out.println("<div class='block text-small'>");
		m_out.println("<h5> <span class='strong tooltipped' data-position='top' data-tooltip='25%'>" + failNo
				+ "</span> test(s) failed, <span class='strong tooltipped' data-position='top' data-tooltip='0%'>0</span> others </h5>");
		m_out.println("</div>");
		m_out.println("</center>");
		m_out.println("</div>");
		m_out.println("</div>");

		m_out.println("<div class='col s12 m6 l6 np-h'>");
		m_out.println("<div class='card-panel nm-v' id='parent-analysis1' >");

		m_out.println("</div>");
		m_out.println("</div>");
		m_out.println("</div>");
		m_out.println("</div>");

		m_row = 0;
	}

	private void resultDetail(IResultMap tests) {
		for (ITestResult result : tests.getAllResults()) {
			ITestNGMethod method = result.getMethod();
			m_methodIndex++;
			String cname = method.getTestClass().getName();
			m_out.println("<h2 id=\"m" + m_methodIndex + "\">" + cname + ":" + method.getMethodName() + "</h2>");
			Set<ITestResult> resultSet = tests.getResults(method);
			generateForResult(result, method, resultSet.size());
			m_out.println("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");

		}
	}

	/**
	 * Write the first line of the stack trace.
	 *
	 * @param tests the tests
	 * @param meth the method
	 * @param style the style
	 */
	private void getShortException(IResultMap tests, String meth, String style) {
		int no = 0;
		for (ITestResult result : tests.getAllResults()) {
			m_methodIndex++;
			ITestNGMethod method = result.getMethod();
			if (method.getMethodName().equalsIgnoreCase(meth)) {
				Throwable exception = result.getThrowable();
				List<String> msgs = Reporter.getOutput(result);

				// boolean hasReporterOutput = msgs.size() > 0;
				boolean hasThrowable = exception != null;

				for (String line : msgs) {
					//////////

					/*
					 * m_out.println("<div class='subview-left left'>");
					 * 
					 * m_out.println("<div class='view-summary'>"); m_out.println("<h5>Tests</h5>");
					 * m_out.println("<ul id='test-collection' class='test-collection'>");
					 */

					long end = Long.MIN_VALUE;
					long start = Long.MAX_VALUE;
					for (ITestResult testResult : tests.getResults(method)) {
						if (testResult.getEndMillis() > end) {
							end = testResult.getEndMillis();
						}
						if (testResult.getStartMillis() < start) {
							start = testResult.getStartMillis();
						}
					}
					if (line.contains("is failed.")) {
						m_out.println("<tr class='log' status='fail'>");
//						m_out.println("<td class='status fail' title='fail' alt='fail'><i class='material-icons'>bug_report</i></td>");
						m_out.println("<td><span class='label end-time'>" + "Fail" + "</span></td>");
						m_out.println("<td class='timestamp'>" + sdf_a.format((new java.util.Date(start))) + "</td>");
						m_out.println("<td class='step-details'><span class='label end-time'>" + line.split("_")[1]
								+ "</span></td>");
						m_out.println("</tr>");
					} else if (line.contains("is passed.")) {
						m_out.println("<tr class='log' status='info'>");
//						m_out.println("<td class='status info' title='info' alt='info'><i class='material-icons'>double_arrow</i></td>");
						m_out.println("<td><span class='label start-time'>" + "Pass" + "</span></td>");
						m_out.println("<td class='timestamp'>" + sdf_a.format((new java.util.Date(start))) + "</td>");
						m_out.println("<td class='step-details'><span class='label start-time'>" + line.split("_")[1]
								+ "</span></td>");
						m_out.println("</tr>");
					} else if (line.contains("Screenshot") || line.contains("screenshot") || line.contains("&#10008")) {
						m_out.println("<tr class='log' status='fail'>");
//						m_out.println("<td class='status fail' title='fail' alt='fail'><i class='material-icons'>bug_report</i></td>");
						m_out.println("<td><span class='label end-time'>" + "Fail" + "</span></td>");
						m_out.println("<td class='timestamp'>" + sdf_a.format((new java.util.Date(start))) + "</td>");
						m_out.println("<td class='step-details'>" + line + "</td>");
						m_out.println("</tr>");
					} else if (!line.contains("Error") && !line.contains("Exception") && !line.contains("Screenshot")
							&& !line.contains("screenshot")) {
						m_out.println("<tr class='log' status='info'>");
//						m_out.println("<td class='status info' title='info' alt='info'><i class='material-icons'>double_arrow</i></td>");
						m_out.println("<td><span class='label start-time'>" + "Pass" + "</span></td>");
						m_out.println("<td class='timestamp'>" + sdf_a.format((new java.util.Date(start))) + "</td>");
						m_out.println("<td class='step-details'>" + line + "</td>");
						m_out.println("</tr>");
					} else {

					}
				}

				if (!style.equalsIgnoreCase("Passed")) {

					if (hasThrowable) {
						String str = Utils.longStackTrace(exception, true);
						// scanner = new Scanner(str);
						// String firstLine = scanner.nextLine();

						m_out.println("<tr class='log' status='fail'>");
//				m_out.println("<td class='status fail' title='fail' alt='fail'><i class='material-icons'>cancel</i></td>");
						m_out.println("<td><span class='label end-time'>" + "Fail" + "</span></td>");
//				m_out.println("<td class='timestamp'>"+(++no)+"</td>");
						m_out.println("<td class='timestamp'>" + sdf_a.format((new java.util.Date())) + "</td>");
						m_out.println("<td class='step-details'><pre>" + str);

						/*
						 * m_out.println("at org.testng.Assert.fail(Assert.java:96)");
						 * m_out.println("at org.testng.Assert.failNotEquals(Assert.java:776)");
						 * m_out.println("at org.testng.Assert.assertTrue(Assert.java:44)");
						 */
						m_out.println("</pre></td>");
						m_out.println("</tr>");

						/*
						 * m_out.println("<td width='30%'>"); for (Entry<String, String> e :
						 * map.entrySet()) {
						 * 
						 * if (firstLine.contains(e.getKey())) { m_out.print(e.getValue() + "<br/>"); }
						 * else { } }
						 * 
						 * m_out.println("</td>");
						 * 
						 * m_out.println("<td width='35%'>"); boolean wantsMinimalOutput =
						 * result.getStatus() == ITestResult.SUCCESS; if (hasReporterOutput) {
						 * m_out.print("<h3>" + (wantsMinimalOutput ? "Expected Exception" : "Failure")
						 * + "</h3>"); } m_out.println(firstLine); m_out.println("</td>");
						 */
					}
				}

				m_out.println("</tbody>");
				m_out.println("</table>");
				m_out.println("</div>");
				m_out.println("</div>");
				m_out.println("</li>");
			}
			no = 0;
		}
	}

	private void generateForResult(ITestResult ans, ITestNGMethod method, int resultSetSize) {
		Object[] parameters = ans.getParameters();
		boolean hasParameters = parameters != null && parameters.length > 0;
		if (hasParameters) {
			tableStart("result", null);
			// m_out.print("<tr class=\"param\">");
			for (int x = 1; x <= parameters.length; x++) {
				// m_out.print("<th>Param." + x + "</th>");
			}
			// m_out.println("</tr>");
			// m_out.print("<tr class=\"param stripe\">");
			// for (Object p : parameters) {
			// m_out.println("<td>" + Utils.escapeHtml(Utils.toString(p))
			// + "</td>");
			// }
			// m_out.println("</tr>");
		}
		List<String> msgs = Reporter.getOutput(ans);
		boolean hasReporterOutput = msgs.size() > 0;
		Throwable exception = ans.getThrowable();
		boolean hasThrowable = exception != null;
		if (hasReporterOutput || hasThrowable) {
			if (hasParameters) {
				// m_out.print("<tr><td");
				if (parameters.length > 1) {
					// m_out.print(" colspan=\"" + parameters.length + "\"");
				}
				// m_out.println(">");
			} else {
				// m_out.println("<div>");
			}
			if (hasReporterOutput) {
				if (hasThrowable) {
					// m_out.println("<h3>Test Messages</h3>");
				}
				/*
				 * for (String line : msgs) { //m_out.println(line + "<br/>"); }
				 */
			}
			if (hasThrowable) {
				// boolean wantsMinimalOutput = ans.getStatus() == ITestResult.SUCCESS;
				if (hasReporterOutput) {
					/*
					 * m_out.println("<h3>" + (wantsMinimalOutput ? "Expected Exception" :
					 * "Failure") + "</h3>");
					 */
				}
				generateExceptionReport(exception, method);
			}
			if (hasParameters) {
				// m_out.println("</td></tr>");
			} else {
				// m_out.println("</div>");
			}
		}
		if (hasParameters) {

			// m_out.println("</table>");
		}
	}

	// @SuppressWarnings("deprecation")
	protected void generateExceptionReport(Throwable exception, ITestNGMethod method) {
		// m_out.print("<div class=\"stacktrace\">");
		// m_out.print(Utils.stackTrace(exception, true)[0]);
		// m_out.println("</div>");
	}

	/**
	 * Since the methods will be sorted chronologically, we want to return the
	 * ITestNGMethod from the invoked methods.
	 *
	 * @param tests the tests
	 * @param suite the suite
	 * @return the method set
	 */
	private Collection<ITestNGMethod> getMethodSet(IResultMap tests, ISuite suite) {
		List<IInvokedMethod> r = Lists.newArrayList();
		List<IInvokedMethod> invokedMethods = suite.getAllInvokedMethods();
		for (IInvokedMethod im : invokedMethods) {
			if (tests.getAllMethods().contains(im.getTestMethod())) {
				r.add(im);
			}
		}
		Arrays.sort(r.toArray(new IInvokedMethod[r.size()]), new TestSorter());
		List<ITestNGMethod> result = Lists.newArrayList();
		for (IInvokedMethod m : r) {
			result.add(m.getTestMethod());
		}
		for (ITestNGMethod m : tests.getAllMethods()) {
			if (!result.contains(m)) {
				result.add(m);
			}
		}
		return result;
	}

	@SuppressWarnings("unused")
	public void generateSuiteSummaryReport(List<ISuite> suites) {
		NumberFormat formatter = new DecimalFormat("#,##0.0");
		int qty_pass_m = 0;
		int qty_pass_f = 0;
		int qty_pass_s = 0;
		int qty_skip = 0;
		long time_start = Long.MAX_VALUE;
		int qty_fail = 0;
		long time_end = Long.MIN_VALUE;
		m_testIndex = 1;
		for (ISuite suite : suites) {
			if (suites.size() >= 1) {
			}
			Map<String, ISuiteResult> tests = suite.getResults();
			for (ISuiteResult r : tests.values()) {
				qty_tests += 1;
				ITestContext overview = r.getTestContext();
				int q = getMethodSet(overview.getPassedTests(), suite).size();
				qty_pass_m += q;
				int f = getMethodSet(overview.getFailedTests(), suite).size();
				qty_pass_f += q;
			}
		}
		generateMethodSummaryReport1(suites);
	}

	private void tableStart(String cssclass, String id) {
		/*
		 * m_out.
		 * println("<table  width='80%' border=\"5\" cellspacing=\"0\" cellpadding=\"0\""
		 * + (cssclass != null ? " class=\"" + cssclass + "\"" : " ") + (id != null ?
		 * " id=\"" + id + "\"" : "") + ">"); m_row = 0;
		 */
	}

	private void titleRow(String label, int cq) {
		titleRow(label, cq, null);
	}

	private void titleRow(String label, int cq, String id) {
		/*
		 * m_out.print("<tr"); if (id != null) { m_out.print(" id=\"" + id + "\""); }
		 * m_out.println("><th bgcolor='#cce6ff' colspan=\"" + cq +
		 * "\"><font color='black' style='text-shadow:2px 2px white;'>" + label +
		 * "<font></th></tr>");
		 */
		m_row = 0;
	}

	/**
	 *  Starts HTML stream.
	 *
	 * @param out the out
	 */
	protected void startHtml(PrintWriter out) {
		/*
		 * out.
		 * println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">"
		 * ); out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		 * out.println("<head>");
		 * out.println("<title> Automation build Summary - TestNG Report</title>");
		 * out.println("<style type=\"text/css\">"); out.
		 * println("table {margin-bottom:1px;border-collapse:collapse;empty-cells:show}"
		 * );
		 * out.println("td,th {solid #009;padding:.25em .5em;word-break: break-word;}");
		 * out.println("td,th {solid #009;padding:.25em .5em;}");
		 * out.println(".result th {vertical-align:bottom}");
		 * out.println(".param th {padding-left:1em;padding-right:1em}");
		 * out.println(".param td {padding-left:.5em;padding-right:2em}");
		 * out.println(".stripe td,.stripe th {background-color: #E6EBF9}");
		 * out.println(".numi,.numi_attn {text-align:right}");
		 * out.println(".total td {font-weight:bold}");
		 * out.println(".passedodd td {background-color: #0A0}");
		 * out.println(".passedeven td {background-color: #3F3}");
		 * out.println(".skippedodd td {background-color: #CCC}");
		 * out.println(".skippedodd td {background-color: #DDD}");
		 * out.println(".failedodd td,.numi_attn {background-color: #F9C1C1}");
		 * out.println(".failedeven td,.stripe .numi_attn {background-color: #F9C1C1}");
		 * out.println(".stacktrace {white-space:pre;font-family:monospace}"); out.
		 * println(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}"
		 * ); out.println("</style>"); out.println("</head>"); out.println("<body>");
		 */

		out.println("<!DOCTYPE html>");
		out.println("<html>");

		out.println("<head>");
//		out.println("<meta charset='UTF-8' />");
		out.println("<meta http-equiv='content-type' content='text/html; charset=utf-8' /> ");
		out.println("<meta name='description' content='' />");
		out.println("<meta name='robots' content='noodp, noydir' />");
		out.println("<meta name='viewport' content='width=device-width, initial-scale=1' />");
		out.println("<meta id=\"timeStampFormat\" name=\"timeStampFormat\" content='MMM d, yyyy hh:mm:ss a'/>");
		out.println(
				"<link href='https://fonts.googleapis.com/css?family=Source+Sans+Pro:400,600' rel='stylesheet' type='text/css'>");
		out.println("<link href=\"https://fonts.googleapis.com/icon?family=Material+Icons\" rel=\"stylesheet\">");
		out.println(
				"<link href='https://cdn.rawgit.com/anshooarora/extentreports-java/bee7a4abdc590e21eec8618a90c81ff4d16e500a/dist/css/extent.css' type='text/css' rel='stylesheet' />");
		out.println("<title>QA report</title>");
		out.println("<style type='text/css'>");
		out.println("</style>");
		out.println("</head>");
		out.println("<body class='extent standard default hide-overflow dark'>");
		out.println(
				"<div id='theme-selector' alt='Click to toggle theme. To enable by default, use theme configuration.' title='Click to toggle theme. To enable by default, use theme configuration.'>");
		out.println("<span><i class='material-icons'>desktop_windows</i></span>");
		out.println("</div>");
		out.println("<nav>");
		out.println("<div class=\"nav-wrapper\">");
		try {
			String TitleNm = getValueFromConfig("ReportConfig/report-config.properties", "Title");
			if (TitleNm == null || TitleNm.equalsIgnoreCase(" ") || TitleNm.equals(""))
				TitleNm = "SA-DReport";

			out.println("<a href=\"#!\" class=\"brand-logo blue darken-3\">" + TitleNm + "</a>");
		} catch (Exception e1) {
			out.println("<a href=\"#!\" class=\"brand-logo blue darken-3\">SA-DReport</a>");
			e1.printStackTrace();
		}
		out.println("<!-- slideout menu -->");
		out.println("<ul id='slide-out' class='side-nav fixed hide-on-med-and-down'>");
		out.println(
				"<li class='waves-effect active'><a href='#!' view='test-view' onclick=\"configureView(0);chartsView('test');\"><i class='material-icons'>dashboard</i></a></li>");
		out.println(
				"<li class='waves-effect'><a href='#!' view='category-view' onclick=\"configureView(1)\"><i class='material-icons'>label_outline</i></a></li>");
		out.println(
				"<li class='waves-effect'><a href='#!' onclick=\"configureView(-1);chartsView('dashboard');\" view='dashboard-view'><i class='material-icons'>track_changes</i></i></a></li>");
		out.println("</ul>");
		out.println("<!-- report name -->");
		try {
			String Heading = getValueFromConfig("ReportConfig/report-config.properties", "Heading");
			if (Heading.equalsIgnoreCase(" ") || Heading == null || Heading.equalsIgnoreCase(""))
				Heading = "Regression";

			out.println("<span class='report-name'>" + Heading + "</span>");
		} catch (Exception e) {
			out.println("<span class='report-name'>Regression</span>");
			e.printStackTrace();
		}
		out.println("<!-- report headline -->");
		out.println("<span class='report-headline'></span>");
		out.println("</div>");
		out.println("</nav>");
	}

	/**
	 *  Finishes HTML stream.
	 *
	 * @param out the out
	 */
	protected void endHtml(PrintWriter out) {
		/*
		 * out.
		 * println("<tr bgcolor='SkyBlue'><td align='right' colspan='5'><center><b><i>Report customized by Evosys </i><b><center></center></b></b></center></td></tr>"
		 * ); out.println("</body></html>");
		 */
		Collection<String> arr1 = null;
		String arg1 = null;
		int p = 0;
		int f = 0;
		int sk = 0;

		out.println("<!-- exception view -->");
		out.println("<div id='dashboard-view' class='view hide'>");
		out.println("<div class='card-panel transparent np-v'>");
		out.println("<h5>Dashboard</h5>");
		out.println("<div class='row'>");
		out.println("<div class='col s2'>");
		out.println("<div class='card-panel r'>");
		out.println("Module");
		out.println("<div class='panel-lead'>" + moduleName.size() + "</div>");// Need to add value
		out.println("</div>");
		out.println("</div>");
		out.println("<div class='col s2'>");
		out.println("<div class='card-panel r'>");
		out.println("Total Test");
		out.println("<div class='panel-lead'>" + (passed + failNo + skipped) + "</div>");// Need to add value
		out.println("</div>");
		out.println("</div>");
		out.println("<div class='col s2'>");
		out.println("<div class='card-panel r'>");
		out.println("Start");
		out.println("<div class='panel-lead'>" + sdf.format((new java.util.Date((time_start1)))) + "</div>"); // Need to
																												// add
																												// value
		out.println("</div>");
		out.println("</div>");
		out.println("<div class='col s2'>");
		out.println("<div class='card-panel r'>");
		out.println("End");
		out.println("<div class='panel-lead'>" + sdf.format((new java.util.Date((time_end1)))) + "</div>"); // Need to
																											// add value
		out.println("</div>");
		out.println("</div>");
		out.println("<div class='col s2'>");
		out.println("<div class='card-panel r'>");
		out.println("Time Taken");
		out.println("<div class='panel-lead'>" + time + " minutes</div>"); // Need to add value
		out.println("</div>");
		out.println("</div>");
		out.println("<div class='col s4'>");
		out.println("<div class='card-panel dashboard-environment'>");
		out.println("<span class='right label cyan white-text'>Environment</span><p>&nbsp;</p>");
		out.println("<table>");
		out.println("<tr>");
		out.println("<th>Name</th>");
		out.println("<th>Value</th>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>URL</td>"); // Need to add value
		out.println("<td>" + SeleniumInit.testUrl + "</td>"); // Need to add value
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>Browser</td>");
		out.println("<td>" + SeleniumInit.browserName + " v"+SeleniumInit.browserVersion+"</td>");
		out.println("</tr>");
		try {
			Set<Object> keyss = getAllKeyFromProperties("ReportConfig/report-environment.properties");
			out.println("<tr>");
			out.println("<td>Report Version</td>");
			out.println("<td>2.0</td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td>Customized By</td>");
			out.println("<td>Badal Shah</td>");
			out.println("</tr>");
			/*
			 * for (Object k : keyss) { String pkey = (String) k; System.out .println(pkey +
			 * ": " + getValueFromConfig("ReportConfig/report-environment.properties",
			 * pkey)); out.println("<tr>"); out.println("<td>" + pkey + "</td>");
			 * out.println("<td>" +
			 * getValueFromConfig("ReportConfig/report-environment.properties", pkey) +
			 * "</td>"); out.println("</tr>"); }
			 */
		} catch (Exception e) {
			System.out.println("Error in report-environment.properties");
		}
		/*
		 * out.println("<tr>"); out.println("<td>Version</td>");
		 * out.println("<td>v1</td>"); out.println("</tr>");
		 */
		out.println("</table>");
		out.println("</div>");
		out.println("</div>");
		out.println("<div class='col s4'>");
		out.println("<div class='card-panel dashboard-categories'>");
		out.println("<span class='right label cyan white-text'>Categories</span><p>&nbsp;</p>");
		out.println("<table>");
		out.println("<tr>"); // Add Module wise test result status
		out.println("<th>Name</th>");
		out.println("<th>Passed</th>");
		out.println("<th>Failed</th>");
		out.println("<th>Others</th>");
		out.println("</tr>");

		Set<String> keys = moduleWithTestCase.keySet();
		// iterate through the key set and display key and values
		for (String key : keys) {

			arr1 = moduleWithTestCase.get(key);
			for (Iterator<String> i = arr1.iterator(); i.hasNext();) {
				arg1 = i.next();
				if (arg1.split("_")[1].contains("pass"))
					++p;
				else
					++f;
			}

			out.println("<tr>");
			out.println("<td>" + key + "</td>");
			out.println("<td>" + p + "</td>");
			out.println("<td>" + f + "</td>");
			out.println("<td>" + sk + "</td>");
			out.println("</tr>");

			p = 0;
			f = 0;
			sk = 0;
		}

		/*
		 * out.println("<tr>"); out.println("<td>CreateInvoice</td>");
		 * out.println("<td>2</td>"); out.println("<td>0</td>");
		 * out.println("<td>0</td>"); out.println("</tr>");
		 */
		out.println("</table>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
		out.println("<!-- dashboard view -->");
		out.println("<!-- testrunner-logs view -->");
		out.println("</div>");
		out.println("<!-- container -->");
		out.println("<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\">");
		out.println("</script>");
		out.println("<script>");
		out.println("google.charts.load(\"current\", {packages:[\"corechart\"]});");
		out.println("google.charts.setOnLoadCallback(drawChart);");
		out.println("function drawChart() {");
		out.println("var data = google.visualization.arrayToDataTable([");
		out.println("['Task', 'Hours per Day'],");
		out.println("['Pass',     " + passed + "],");
		out.println("['Fail',     " + failNo + "],");
		out.println("['Skip',    " + skipped + "]");
		out.println("]);");

		out.println("var options = {");
		out.println("<!-- title: 'Build Summary',-->");
		out.println("legend:{textStyle: {color: '#1C8490'}},");
		out.println("'backgroundColor': 'transparent',");
		out.println("pieHole: 0.4,");
		out.println("pieSliceTextStyle: {color: '#1C8490'}};");

		out.println("var chart = new google.visualization.PieChart(document.getElementById('parent-analysis1'));");
		out.println("chart.draw(data, options);");
		out.println("}");
		out.println("</script>");

		out.println("<script>");
		out.println("var statusGroup = {");
		out.println("passParent: 3,");
		out.println("failParent: 1,");
		out.println("fatalParent: 0,");
		out.println("errorParent: 0,");
		out.println("warningParent: 0,");
		out.println("skipParent: 0,");
		out.println("exceptionsParent: 1,");

		out.println("passChild: 0,");
		out.println("failChild: 2,");
		out.println("fatalChild: 0,");
		out.println("errorChild: 0,");
		out.println("warningChild: 0,");
		out.println("skipChild: 0,");
		out.println("infoChild: 4,");
		out.println("exceptionsChild: 2,");

		out.println("passGrandChild: 0,");
		out.println("failGrandChild: 0,");
		out.println("fatalGrandChild: 0,");
		out.println("errorGrandChild: 0,");
		out.println("warningGrandChild: 0,");
		out.println("skipGrandChild: 0,");
		out.println("infoGrandChild: 0,");
		out.println("exceptionsGrandChild: 0,");
		out.println("};");
		out.println("</script>");

		out.println(
				"<script src='https://cdn.jsdelivr.net/gh/anshooarora/extentreports-java@925c18aac5e7fe935676822f33803ab0444208e6/dist/js/extent.js' type='text/javascript'>");
		out.println("</script>");

		out.println("<script type='text/javascript'>");

		out.println(" $(document).ready(function() {");
		String TitleLink;
		try {
			TitleLink = getValueFromConfig("ReportConfig/report-config.properties", "TitleLink");
			if (TitleLink == null || TitleLink.equals(" ") || TitleLink.equals(""))
				TitleLink = "#";
			out.println(
					" $(document).ready(function() { $(\".blue.darken-3\").attr(\"href\", \"" + TitleLink + "\"); });");

		} catch (IOException e) {
			out.println(" $(document).ready(function() { $(\".blue.darken-3\").attr(\"href\", \"#\"); });");
			e.printStackTrace();
		}
		out.println(" $(document).ready(function() { $(\".blue.darken-3\").attr(\"href\", \"#\"); });");
		// out.println(" $(document).ready(function() { $(\".brand-logo\").html(\"IRF\")
		// });");
		out.println(" $(document).ready(function() { $(\".blue.darken-3\").css(\"width\",\"90px\") });");
		out.println(" $(document).ready(function() { $(\".side-nav\").css(\"width\",\"90px\") });");
		out.println(" $(document).ready(function() { $(\".side-nav i\").css(\"width\",\"35px\") });");
		out.println(" $(document).ready(function() { $(\".container\").css(\"padding-left\",\"80px\") });");
		out.println(" $(document).ready(function() { $(\".side-nav li\").css(\"width\",\"90px\") });");
		out.println(" $(document).ready(function() { $(\"nav .brand-logo\").css(\"padding-left\",\"25px\") });");
		out.println(" $(document).ready(function() { $(\"element.style\").css(\"width\",\"500px\") });");

		out.println(" <!-- $(document).ready(function() { $(\".row .col.l6\").css(\"width\",\"100%\") });-->");
		out.println("  });");

		out.println("</script>");
		out.println("</body>");

		out.println("</html>");

	}

	/** Arranges methods by classname and method name */
	private class TestSorter implements Comparator<IInvokedMethod> {
		/** Arranges methods by classname and method name */

		public int compare(IInvokedMethod o1, IInvokedMethod o2) {
			return (int) (o1.getDate() - o2.getDate());
		}
	}

	public static String getValueFromConfig(String Datafile, String value) throws IOException {
		String result = "";
		File file = new File(Datafile);
		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Properties prop = new Properties();
		try {
			prop.load(fileInput);
			result = prop.getProperty(value);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			fileInput.close();
		}
		return result;
	}

	public static Set<Object> getAllKeyFromProperties(String Datafile) throws IOException {
		@SuppressWarnings("unused")
		String result = "";
		File file = new File(Datafile);
		Properties prop = new Properties();
		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			prop.load(fileInput);

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			fileInput.close();
		}
		return prop.keySet();
	}
}
