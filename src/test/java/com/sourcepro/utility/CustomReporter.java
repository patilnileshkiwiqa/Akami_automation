package com.sourcepro.utility;

import java.io.BufferedWriter;
import java.io.File;
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
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

import com.sourcepro.init.SeleniumInit;

public class CustomReporter extends CustomReporterListener {

	private static final Logger L = Logger.getLogger(CustomReporterListener.class);
	private PrintWriter m_out;
	@SuppressWarnings("unused")
	private int m_row;
	private Integer m_testIndex;
	private int m_methodIndex;
	private Scanner scanner;
	int passCount = 0;
	private static HashMap<String, String> map = new HashMap<String, String>();
	int namecount = 0;
	int qty_tests = 0;
	int passed = 0;
	int skipped = 0;
	int failedcount = 0;
	int total_a = 0;
	int qty_pass = 0;

	public CustomReporter() {
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
		TotalTime(suites);
		generateMethodSummaryReport(suites);
		m_out.flush();
		m_out.close();
	}

	String Time;

	public String TotalTime(List<ISuite> suites) { // 4.1
		long time_start = Long.MAX_VALUE;
		long time_end = Long.MIN_VALUE;
		ITestContext overview = null;

		for (ISuite suite : suites) {
			Map<String, ISuiteResult> itests = suite.getResults();
			for (ISuiteResult r : itests.values()) {

				overview = r.getTestContext();

				time_start = Math.min(overview.getStartDate().getTime(), time_start);
				time_end = Math.max(overview.getEndDate().getTime(), time_end);

			}
		}

		NumberFormat formatter = new DecimalFormat("#,##0.0");
		Time = String.valueOf(formatter.format(((time_end - time_start) / 1000.) / 60.));

		return Time;
	}

	protected PrintWriter createWriter(String outdir) throws IOException {
		// java.util.Date now = new Date();
		new File(outdir).mkdirs();
		return new PrintWriter(
				new BufferedWriter(new FileWriter(new File(outdir, "emailable-summary-report" + ".html"))));
	}

	/**
	 * Creates a table showing the highlights of each test method with links to the
	 * method details.
	 *
	 * @param suites the suites
	 */
	protected void generateMethodSummaryReport(List<ISuite> suites) {
		m_methodIndex = 0;
		startResultSummaryTable("methodOverview");
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
				resultSummary(suite, testContext.getFailedConfigurations(), testName, "failed",
						" (configuration methods)");
				resultSummary(suite, testContext.getFailedTests(), testName, "failed", "");
				resultSummary(suite, testContext.getPassedTests(), testName, "passed", "");
				resultSummary_skipped(suite, testContext.getSkippedTests(), testName, "skipped", "");
				testIndex++;
			}
		}

		testCaseNo();
		endHtml(m_out);
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

	public void testCaseNo() {
		/*
		 * m_out.println(
		 * "<table width='350px' height='30px' border='1' align='left'><tbody><tr colspan='2'><td bgcolor='#0088cc' colspan='2'><h3><center><font color='white'>Build Summary</font></center></h3></td></tr><tr><td><b>"
		 * + "Passed Test cases</b>   </td> <td> <center><b>" + passed +
		 * "</b></center></td></tr><tr><td><b> Failed Test Cases </b></td><td> <center><b>"
		 * + failedcount +
		 * "</b></center></td></tr> 	 <tr><td><b>Skipped Test cases</b>   </td><td><center><b> "
		 * + skipped +
		 * "</b></center> </td></tr><tr bgcolor='skyblue'><td> <b>Total Test Cases </b>  </td><td> <center><b>"
		 * + (failedcount+skipped+passed) + "</b></center></td></tr></tbody></table>");
		 */
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

		if (tests.getAllResults().size() > 0) {
			for (ITestNGMethod method : getMethodSet(tests, suite)) {
				String meth = method.getMethodName();
				if (!checkpassedTestCases(meth + "_" + testname)) {
					PassedTestCases.add(meth + "_" + testname);
					++passed;
				}
			}
		}
	}

	ArrayList<String> PassedTestName = new ArrayList<String>();

	public boolean checkpassedTestCases(String testName) {
		return PassedTestCases.contains(testName);
	}

	ArrayList<String> testArray = new ArrayList<String>();
	ArrayList<String> passArray = new ArrayList<String>();
	int retry = 0;

	private void resultSummary(ISuite suite, IResultMap tests, String testname, String style, String details) {

		if (tests.getAllResults().size() > 0) {

			// StringBuffer buff = new StringBuffer();
			int mq = 0;
			@SuppressWarnings("unused")
			int cq = 0;
			for (ITestNGMethod method : getMethodSet(tests, suite)) {

				String meth = method.getMethodName();
				if (!checkTestCases(meth + "_" + testname) && !isPassed(meth + "_" + testname)) {
					testArray.add(meth + "_" + testname);
					m_row += 1;
					m_methodIndex += 1;
					{
						String id = (m_testIndex == null ? null : "t" + Integer.toString(m_testIndex));
						m_out.print("<tr  style=\"background-color:#ffebeb\"");
						if (id != null) {
							m_out.print(" id=\"" + id + "\"");
						}
						failedcount++;
						m_out.println("><td width='5%'><b>" + failedcount + ".</b></td><td width='25%'><b>" + testname
								+ "<br></b></td>");
						m_row = 0;

						//
						m_testIndex = null;
						namecount++;

					}
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
						getShortException(tests, meth);

					}

					@SuppressWarnings("unused")
					String description = method.getDescription();
					@SuppressWarnings("unused")
					String testInstanceName = resultSet.toArray(new ITestResult[] {})[0].getTestName();

					m_out.println(
							"<td width='5%' class=\"numi\"><center>" + (end - start) / 1000 + "</center></td>" + "");
					m_out.println("<td width='5%' style=\"color:#c84117\"><center><b>Fail</b></center></td>" + "");

				} else {
					if (isPassed(meth + "_" + testname)) {

						String id = (m_testIndex == null ? null : "t" + Integer.toString(m_testIndex));
						m_out.print("<tr  style=\"background-color:#ccffcd\"");
						if (id != null) {
							m_out.print(" id=\"" + id + "\"");
						}
						failedcount++;
						m_out.println("><td width='5%'><b>" + failedcount + ".</b></td><td width='25%'><b>" + testname
								+ "<br></b></td>");

						m_out.println("<td width='30%'> <br/>");

						m_out.println("</td>");

						m_out.println("<td width='30%'>");

						m_out.println("</td>");

						@SuppressWarnings("unused")
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
						m_out.println("<td width='5%' class=\"numi\"><center>" + (end - start) / 1000 + "</center></td>"
								+ "");
						m_out.println("<td width='5%' style=\"color:#3d8f0d\"><center><b>Pass</b></center></td>" + "");
					}
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
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a z,MM/dd/yyyy");

		m_out.println("<tr><td bgcolor='white' colspan='6'> <table border='0' width='100%' bgcolor='#e6f7ff'><tr>"
				+ "<td  width='25%'  bgcolor='white'>"
				+ "<center><img width='150px' src='https://www.mtn.sd/mtn/wp-content/themes/mtn-theme/images/mtn-logo-nav.svg'/></center>"
				+ "</td><td ><center><font color='#008bcc'><b><h1>Test Cases Analysis</h1></b></font></center></td> "
				+ "<td width='25%' bgcolor='white'>" + "<center><img width='290px' src='https://www.solutionanalysts.com/wp-content/uploads/2021/02/SA-Logo-high.png'/></center></td> "
				+ "</tr></table> </td></tr>");
		m_out.println("<tr><td colspan='6'>Overall test suite completion: <b>" + Time
				+ " minutes</b><br/> Date and Time of Run: <b>" + sdf.format(date) + "</b><br/> Browser: <b>"
				+ SeleniumInit.browserName + " " + SeleniumInit.browserVersion + "</b><br/>OS: <b>"
				+ System.getProperty("os.name") + "</b><br/>Tested URL: <b>" + SeleniumInit.testUrl
				+ "</b> <br/></td></tr>");
		m_out.println("<tr bgcolor='SkyBlue'><th>Sr. No.</th><th>Test Cases</th>"
				+ "<th>Failure Reason</th><th>Failure Error</th><th>Total Time<br/>(sec.)</th><th>Test Result</th>");
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
	 */
	private void getShortException(IResultMap tests, String meth) {

		for (ITestResult result : tests.getAllResults()) {
			m_methodIndex++;
			ITestNGMethod method = result.getMethod();
			if (method.getMethodName().equalsIgnoreCase(meth)) {
				Throwable exception = result.getThrowable();
				List<String> msgs = Reporter.getOutput(result);
				boolean hasReporterOutput = msgs.size() > 0;
				boolean hasThrowable = exception != null;
				if (hasThrowable) {
					String str = Utils.longStackTrace(exception, true);
					scanner = new Scanner(str);
					String firstLine = scanner.nextLine();

					m_out.println("<td width='30%'>");
					for (Entry<String, String> e : map.entrySet()) {

						if (firstLine.contains(e.getKey())) {
							m_out.print(e.getValue() + "<br/>");
						} else {
						}
					}

					m_out.println("</td>");

					m_out.println("<td width='30%'>");
					boolean wantsMinimalOutput = result.getStatus() == ITestResult.SUCCESS;
					if (hasReporterOutput) {
						m_out.print("<h3>" + (wantsMinimalOutput ? "Expected Exception" : "Failure") + "</h3>");
					}
					m_out.println(firstLine);
					m_out.println("</td>");
				}
			}
		}
	}

	private void generateForResult(ITestResult ans, ITestNGMethod method, int resultSetSize) {
		Object[] parameters = ans.getParameters();
		boolean hasParameters = parameters != null && parameters.length > 0;
		if (hasParameters) {
			tableStart("result", null);
			m_out.print("<tr class=\"param\">");
			for (int x = 1; x <= parameters.length; x++) {
				m_out.print("<th>Param." + x + "</th>");
			}
			m_out.println("</tr>");
			m_out.print("<tr class=\"param stripe\">");
			for (Object p : parameters) {
				m_out.println("<td>" + Utils.escapeHtml(Utils.toString(p)) + "</td>");
			}
			m_out.println("</tr>");
		}
		List<String> msgs = Reporter.getOutput(ans);
		boolean hasReporterOutput = msgs.size() > 0;
		Throwable exception = ans.getThrowable();
		boolean hasThrowable = exception != null;
		if (hasReporterOutput || hasThrowable) {
			if (hasParameters) {
				m_out.print("<tr><td");
				if (parameters.length > 1) {
					m_out.print(" colspan=\"" + parameters.length + "\"");
				}
				m_out.println(">");
			} else {
				m_out.println("<div>");
			}
			if (hasReporterOutput) {
				if (hasThrowable) {
					m_out.println("<h3>Test Messages</h3>");
				}
				for (String line : msgs) {
					m_out.println(line + "<br/>");
				}
			}
			if (hasThrowable) {
				boolean wantsMinimalOutput = ans.getStatus() == ITestResult.SUCCESS;
				if (hasReporterOutput) {
					m_out.println("<h3>" + (wantsMinimalOutput ? "Expected Exception" : "Failure") + "</h3>");
				}
				generateExceptionReport(exception, method);
			}
			if (hasParameters) {
				m_out.println("</td></tr>");
			} else {
				m_out.println("</div>");
			}
		}
		if (hasParameters) {

			m_out.println("</table>");
		}
	}

	protected void generateExceptionReport(Throwable exception, ITestNGMethod method) {
		m_out.print("<div class=\"stacktrace\">");
		m_out.print(Utils.longStackTrace(exception, true));
		m_out.println("</div>");
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
			}
		}
	}

	private void tableStart(String cssclass, String id) {
		m_out.println("<table  width='100%' border=\"5\" cellspacing=\"0\" cellpadding=\"0\""
				+ (cssclass != null ? " class=\"" + cssclass + "\"" : " ") + (id != null ? " id=\"" + id + "\"" : "")
				+ ">");
		m_row = 0;
	}

	private void titleRow(String label, int cq) {
		titleRow(label, cq, null);
	}

	private void titleRow(String label, int cq, String id) {
		m_out.print("<tr");
		if (id != null) {
			m_out.print(" id=\"" + id + "\"");
		}
		m_out.println("><th bgcolor='#cce6ff' colspan=\"" + cq
				+ "\"><font color='black' style='text-shadow:2px 2px white;'>" + label + "<font></th></tr>");
		m_row = 0;
	}

	/**
	 *  Starts HTML stream.
	 *
	 * @param out the out
	 */
	protected void startHtml(PrintWriter out) {
		out.println(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head>");
		out.println("<title> Automation build Summary - TestNG Report</title>");
		out.println("<style type=\"text/css\">");
		out.println("table {margin-bottom:1px;border-collapse:collapse;empty-cells:show}");
		out.println("td,th {solid #009;padding:.25em .5em;word-break: break-word;}");
		out.println("td,th {solid #009;padding:.25em .5em;}");
		out.println(".result th {vertical-align:bottom}");
		out.println(".param th {padding-left:1em;padding-right:1em}");
		out.println(".param td {padding-left:.5em;padding-right:2em}");
		out.println(".stripe td,.stripe th {background-color: #E6EBF9}");
		out.println(".numi,.numi_attn {text-align:right}");
		out.println(".total td {font-weight:bold}");
		out.println(".passedodd td {background-color: #0A0}");
		out.println(".passedeven td {background-color: #3F3}");
		out.println(".skippedodd td {background-color: #CCC}");
		out.println(".skippedodd td {background-color: #DDD}");
		out.println(".failedodd td,.numi_attn {background-color: #F9C1C1}");
		out.println(".failedeven td,.stripe .numi_attn {background-color: #F9C1C1}");
		out.println(".stacktrace {white-space:pre;font-family:monospace}");
		out.println(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
		out.println("</style>");
		out.println("</head>");
		out.println("<body>");
	}

	/**
	 *  Finishes HTML stream.
	 *
	 * @param out the out
	 */
	protected void endHtml(PrintWriter out) {
		out.println(
				"<tr bgcolor='SkyBlue'><td align='right' colspan='6'><center><b><i>Report customized by SA </i><b><center></center></b></b></center></td></tr>");
		out.println("</table>");
		out.println("</body></html>");
	}

	/** Arranges methods by classname and method name */
	private class TestSorter implements Comparator<IInvokedMethod> {
		/** Arranges methods by classname and method name */

		public int compare(IInvokedMethod o1, IInvokedMethod o2) {
			return (int) (o1.getDate() - o2.getDate());
		}
	}
}
