package com.sourcepro.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
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

public class CustomReporterBuildSummary extends CustomReporterListener {

	private static final Logger L = Logger.getLogger(CustomReporterBuildSummary.class);
	private PrintWriter m_out;
	@SuppressWarnings("unused")
	private int m_row;
	private Integer m_testIndex;
	private Scanner scanner;
	int passCount = 0;
	private static HashMap<String, String> map = new HashMap<>();
	int namecount = 0;
	int qty_tests = 0;
	int passed = 0;
	int skipped = 0;
	int failedcount = 0;
	int total_a = 0;
	int qty_pass = 0;

	public CustomReporterBuildSummary() {
		super();
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

	String time;

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
		time = String.valueOf(formatter.format(((time_end - time_start) / 1000.) / 60.));

		return time;
	}

	protected PrintWriter createWriter(String outdir) throws IOException {
		new File(outdir).mkdirs();
		return new PrintWriter(
				new BufferedWriter(new FileWriter(new File(outdir, "emailable-report-BuildSummary" + ".html"))));
	}

	/**
	 * Creates a table showing the highlights of each test method with links to the
	 * method details.
	 *
	 * @param suites the suites
	 */
	protected void generateMethodSummaryReport(List<ISuite> suites) {
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
				System.out.println("Passed---");
				resultSummary_passed(suite, testContext.getPassedTests(), testName);
				System.out.println("Failed---");
				resultSummary(suite, testContext.getFailedConfigurations(), testName);
				resultSummary(suite, testContext.getFailedTests(), testName);
				System.out.println("Skipped---");

				resultSummary_skipped(suite, testContext.getSkippedTests(), testName, "skipped", "");
				testIndex++;
			}
		}
		testCaseNo();
		endHtml(m_out);
	}

	public void testCaseNo() {
		m_out.println(
				"<table width='350px' height='30px' border='1' align='left'><tbody><tr colspan='2'><td bgcolor='#0088cc' colspan='2'><h3><center><font color='white'>Build Summary</font></center></h3></td></tr><tr><td><b>"
						+ "Passed Test cases</b>   </td> <td> <center><b>" + passed
						+ "</b></center></td></tr><tr><td><b> Failed Test Cases </b></td><td> <center><b>" + failedcount
						+ "</b></center></td></tr><tr><td><b>Skipped Test cases</b></td><td><center><b> " + skipped
						+ "</b></center> </td></tr><tr bgcolor='skyblue'><td> <b>Total Test Cases </b>  </td><td> <center><b>"
						+ (failedcount + skipped + passed) + "</b></center></td></tr></tbody></table>");
	}

	private void resultSummary_skipped(ISuite suite, IResultMap tests, String testname, String style, String details) {
		if (tests.getAllResults().size() > 0) {
			for (@SuppressWarnings("unused")
			ITestNGMethod method : getMethodSet(tests, suite)) {
				skipped++;
			}
		}
	}

	ArrayList<String> passedTestCases = new ArrayList<>();

	private void resultSummary_passed(ISuite suite, IResultMap tests, String testname) { // 5.4.1
		if (tests.getAllResults().size() > 0) {
			for (ITestNGMethod method : getMethodSet(tests, suite)) {
				String meth = method.getMethodName();
				if (!checkpassedTestCases(meth + "_" + testname)) {
					passedTestCases.add(meth + "_" + testname);
					++passed;
				}
			}
		}
	}

	ArrayList<String> passedTestName = new ArrayList<>();

	public boolean checkpassedTestCases(String testName) {
		return passedTestCases.contains(testName);
	}

	ArrayList<String> testArray = new ArrayList<>();
	ArrayList<String> passArray = new ArrayList<>();
	int retry = 0;

	private void resultSummary(ISuite suite, IResultMap tests, String testname) {
		if (!tests.getAllResults().isEmpty()) {
			int mq = 0;
			@SuppressWarnings("unused")
			int cq = 0;
			for (ITestNGMethod method : getMethodSet(tests, suite)) {
				String meth = method.getMethodName();
				if (!checkTestCases(meth + "_" + testname) && !isPassed(meth + "_" + testname)) {
					testArray.add(meth + "_" + testname);
					m_row += 1;

					String id = (m_testIndex == null ? null : "t" + Integer.toString(m_testIndex));
					if (id != null) {
					}
					failedcount++;
					m_row = 0;

					m_testIndex = null;
					namecount++;

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
				}
			}
		}
	}

	public boolean checkTestCases(String testName) {
		return testArray.contains(testName);
	}

	public boolean isPassed(String testName) {
		return passedTestCases.contains(testName);
	}

	/**
	 *  Starts and defines columns result summary table.
	 *
	 * @param style the style
	 */
	private void startResultSummaryTable(String style) {
		m_row = 0;
	}

	/**
	 * Write the first line of the stack trace.
	 *
	 * @param tests the tests
	 * @param meth the method
	 */
	private void getShortException(IResultMap tests, String meth) {
		for (ITestResult result : tests.getAllResults()) {
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

					for (Entry<String, String> e : map.entrySet()) {
						if (firstLine.contains(e.getKey())) {
						} else {
						}
					}
					if (hasReporterOutput) {
					}
				}
			}
		}
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

	private void titleRow(String label, int cq) {
		titleRow(label, cq, null);
	}

	private void titleRow(String label, int cq, String id) {
		if (id != null) {
		}
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
		out.println("</body></html>");
	}

	/**
	 *  Arranges methods by classname and method name.
	 */
	private class TestSorter implements Comparator<IInvokedMethod> {
		public int compare(IInvokedMethod o1, IInvokedMethod o2) {
			return (int) (o1.getDate() - o2.getDate());
		}
	}
}
