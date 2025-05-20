package Utils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReporterNG implements IReporter {

    private ExtentReports extent;

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(outputDirectory + File.separator + "ExtentReportTestNG.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        for (ISuite suite : suites) {
            Map<String, ISuiteResult> result = suite.getResults();

            for (ISuiteResult r : result.values()) {
                ITestContext context = r.getTestContext();

                buildTestNodes(context.getPassedTests(), Status.PASS);
                buildTestNodes(context.getFailedTests(), Status.FAIL);
                buildTestNodes(context.getSkippedTests(), Status.SKIP);
            }
        }

        extent.flush();
    }

    private void buildTestNodes(IResultMap tests, Status status) {
        if (tests.size() > 0) {
            for (ITestResult result : tests.getAllResults()) {
                ExtentTest test = extent.createTest(result.getMethod().getMethodName());

                Reporter.setCurrentTestResult(result);
                List<String> reporterOutput = Reporter.getOutput(result);

                if (!reporterOutput.isEmpty()) {
                    // Log first line or all lines from Reporter output
                    for (String output : reporterOutput) {
                        test.log(Status.INFO, output);
                    }
                }

                test.getModel().setStartTime(getTime(result.getStartMillis()));
                test.getModel().setEndTime(getTime(result.getEndMillis()));

                // Assign groups as categories
                for (String group : result.getMethod().getGroups()) {
                    test.assignCategory(group);
                }

                String message = "Test " + status.toString().toLowerCase() + "ed";

                if (result.getThrowable() != null) {
                    message = result.getThrowable().getMessage();
                }

                test.log(status, message);
            }
        }
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }
}
