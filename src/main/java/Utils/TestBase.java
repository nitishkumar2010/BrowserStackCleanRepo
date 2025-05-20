package Utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;


@Listeners(Utils.TestListener.class)
public class TestBase {
	protected final static long DEFAULT_TEST_TIMEOUT = 900000;
	protected static ThreadLocal<Config[]> threadLocalConfig = new ThreadLocal<Config[]>();
	static List<ITestNGMethod> passedtests = new ArrayList<ITestNGMethod>();
	static List<ITestNGMethod> failedtests = new ArrayList<ITestNGMethod>();
	static List<ITestNGMethod> skippedtests = new ArrayList<ITestNGMethod>();
	static List<ITestNGMethod> totaltests = new ArrayList<ITestNGMethod>();
    public static ExtentReports extent;

	@DataProvider(name = "GetTestConfig")
	public Object[][] GetTestConfig(Method method) {
		Config testConf = new Config(method);
		testConf.testName = method.getDeclaringClass().getName() + "." + method.getName();
		testConf.testStartTime = Helper.getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
		threadLocalConfig.set(new Config[] { testConf });
		return new Object[][] { { testConf } };
	}
	
	@DataProvider(name = "GetTwoBrowserTestConfig")
	public Object[][] GetTwoBrowserTestConfig(Method method)
	{
		Config testConf = new Config(method);
		Config secondaryConfig = new Config(method);

		testConf.testName = secondaryConfig.testName = method.getDeclaringClass().getName() + "." + method.getName();
		testConf.testStartTime = secondaryConfig.testStartTime = Helper.getCurrentDateTime("yyyy-MM-dd HH:mm:ss");

		if (method.isAnnotationPresent(TestVariables.class))
		{
			// Create a object of annotation
			Annotation annotation = method.getAnnotation(TestVariables.class);
			TestVariables annotationObj = (TestVariables) annotation;
			testConf.putRunTimeProperty("newCommandTimeout", annotationObj.newCommandTimeout());
		}

		threadLocalConfig.set(new Config[] { testConf, secondaryConfig });

		return new Object[][] { { testConf, secondaryConfig } };
	}

	@BeforeClass(alwaysRun = true)
	@Parameters({ "browser", "environment", "testngOutputDir", "MobileUAFlag", "PlatformName", "RemoteAddress",
			"BrowserVersion", "RunType", "ProjectName", "BuildId" })
	public void InitializeParameters(@Optional String browser, @Optional String environment,
			@Optional String testngOutputDir, @Optional String MobileUAFlag, @Optional String PlatformName,
			@Optional String RemoteAddress, @Optional String BrowserVersion, @Optional String RunType,
			@Optional String ProjectName, @Optional String BuildId) {
		Config.BrowserName = browser;
		Config.Environment = environment;
		Config.ResultsDir = testngOutputDir;
		Config.MobileUAFlag = MobileUAFlag;
		Config.PlatformName = PlatformName;
		Config.RemoteAddress = RemoteAddress;
		Config.BrowserVersion = BrowserVersion;
		Config.RunType = RunType;
		Config.ProjectName = ProjectName;
		Config.BuildId = BuildId;
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown(ITestResult result) {
		tearDownHelper(result, true);
	}


	/**
	 * Method to initialize the ExtentReport object and setting the system info
	 */
	@BeforeSuite
	public void startSuite() {
	    InetAddress IP = null;
	    try {
	        IP = InetAddress.getLocalHost();
	    } catch (UnknownHostException e) {
	        e.printStackTrace();
	    }

	    if (extent == null) {
	        ExtentSparkReporter spark = new ExtentSparkReporter(
	            System.getProperty("user.dir") + "/test-output/ExtentReport/ExtentReport.html"
	        );

	        // Remove XML config loading to avoid FileNotFoundException
	        // spark.loadXMLConfig(new File(System.getProperty("user.dir") + "/extent-config.xml"));

	        // Configure reporter programmatically
	        spark.config().setTheme(Theme.DARK);
	        spark.config().setDocumentTitle("ExtentReports 4.x Report");
	        spark.config().setReportName("EX Squared Automation Report");
	        spark.config().setEncoding("UTF-8");
	        spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

	        extent = new ExtentReports();
	        extent.attachReporter(spark);

	        extent.setSystemInfo("Host Name", IP.getHostName() + "/" + IP.getHostAddress());
	        extent.setSystemInfo("Environment", "Build/Stage");
	        extent.setSystemInfo("User Name", "Nitish");
	    }
	}


	/**
	 * Method to close/flush the extent report object once the complete suite
	 * executes
	 */
	@AfterSuite
    public void endSuite() {
        if (extent != null) {
            extent.flush();
        }
    }

	protected void tearDownHelper(ITestResult result, Boolean clearConfig) {
	    String testcaseName = "NullConfig";
	    Config[] testConfigs = threadLocalConfig.get();
	    if (testConfigs != null) {
	        for (Config testConf : testConfigs) {
	            if (testConf != null) {
	                testcaseName = testConf.getTestName();
	                testConf.logComment("<------ AfterMethod started for : " + testConf + " " + testcaseName + " ------>");

	                WebDriver driver = testConf.driver;
	                if (driver != null && driver instanceof JavascriptExecutor) {
	                    try {
	                        JavascriptExecutor jse = (JavascriptExecutor) driver;

	                        // Set session name (optional)
	                        JSONObject nameObj = new JSONObject();
	                        nameObj.put("action", "setSessionName");
	                        JSONObject nameArgs = new JSONObject();
	                        nameArgs.put("name", testcaseName);
	                        nameObj.put("arguments", nameArgs);
	                        jse.executeScript(String.format("browserstack_executor: %s", nameObj));

	                        // Set session status
	                        JSONObject statusObj = new JSONObject();
	                        statusObj.put("action", "setSessionStatus");
	                        JSONObject statusArgs = new JSONObject();
	                        if (result.getStatus() == ITestResult.SUCCESS) {
	                            statusArgs.put("status", "passed");
	                            statusArgs.put("reason", "Test passed successfully");
	                        } else {
	                            statusArgs.put("status", "failed");
	                            statusArgs.put("reason", result.getThrowable() != null ?
	                                    result.getThrowable().getMessage() : "Test failed");
	                        }
	                        statusObj.put("arguments", statusArgs);
	                        jse.executeScript(String.format("browserstack_executor: %s", statusObj));
	                    } catch (Exception e) {
	                        System.err.println("Error updating BrowserStack status: " + e.getMessage());
	                    }
	                }

	                if (testConf.appiumDriver != null) {
	                    // Appium teardown logic (if any)
	                } else {
	                    testConf.closeBrowser(result);
	                }

	                testConf.logComment("<------ AfterMethod ended for : " + testConf + " " + testcaseName + " ------>");

	                // Clear or reset config
	                if (clearConfig) {
	                    testConf.runtimeProperties.clear();
	                    testConf = null;
	                } else {
	                    testConf.softAssert = new SoftAssert();
	                }
	            } else {
	                System.out.println("testConfig object not found");
	            }
	        }
	    }

	    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    Date startDate = new Date();
	    System.out.println("<B>Test '" + testcaseName + "' Ended on '" + dateFormat.format(startDate) + "'</B>");
	}


	@AfterSuite(alwaysRun = true)
	public void testKillFirefox() throws IOException {
		// According to my local machine -- Need to be changed in case of remote
		// execution and acc to others machine
		/*if (Config.RunType.equalsIgnoreCase("official")) {
			System.out.println("****Closing all Firefox instances****");
			String path = "C:\\AutomationRepo21\\Common\\Prerequisite\\QuitAllFirefox.bat";
			// path = System.getProperty("user.dir") +
			// "\\..\\Common\\Prerequisite\\QuitAllFirefox.bat";

			Runtime.getRuntime().exec("cmd /c start " + path);

			System.out.println("****Closing all Chrome instances****");
			// path =
			// "C:\\AutomationRepo21\\Common\\Prerequisite\\QuitAllChrome.bat";
			// path = System.getProperty("user.dir") +
			// "\\..\\Common\\Prerequisite\\QuitAllChrome.bat";
			Runtime.getRuntime().exec("cmd /c start " + path);

			System.out.println("****Closing all IE instances****");
			// path =
			// "C:\\AutomationRepo21\\Common\\Prerequisite\\QuitAllIE.bat";
			// path = System.getProperty("user.dir") +
			// "\\..\\Common\\Prerequisite\\QuitAllIE.bat";
			Runtime.getRuntime().exec("cmd /c start " + path);
		} else {
			System.out.println("****No browser is closed, running test cases on local machine****");
		}*/

		for (ITestNGMethod iTestNGMethod : passedtests) {
			System.out.println("Passed : " + iTestNGMethod.getMethodName().toString());
		}

		for (ITestNGMethod iTestNGMethod : failedtests) {
			System.out.println("Failed : " + iTestNGMethod.getMethodName().toString());
		}

	}
}
