package EXSquared.Brookfield;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import EXSquared.Brookfield.QMIPage.PricingBreakdownSection;
import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;
import Utils.Helper;

public class BRPHelper {

	public static int invalidLinksCount = 0;
	public static int invalidImageCount = 0;

	public enum ExpectedPage {
		HomePage, ContactUsPage, PlanFromHomeSearch, CommunityPage, FYHPage, NeighborhoodPage, PlanPage, CommunityMapFYHPage,
		QMIPage, AboutPage, CareersPage, Sitemap, BlogPage, SitecoreLaunchPadPage, MyTime, VizPlanPage, CommunityFromHomeSearch
	}

	public enum DifferentFormTypes {
		VideoChat, OnsiteTour, RequestInformation
	}

	public Config testConfig;
	public String homeurl;

	public BRPHelper(Config testConfig) {
		this.testConfig = testConfig;
		homeurl = testConfig.getRunTimeProperty("BrookfieldHomePage").replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", "");
	}

	public HomePage homePage;
	public ContactUsPage contactUsPage;
	public CommunityPage communityPage;
	public FYHPage fyhPage;
	public NeighborhoodPage neighborhoodPage;
	public PlanPage planPage;
	public QMIPage qmiPage;
	public AboutPage aboutPage;
	public CareersPage careersPage;
	public SitemapPage sitemapPage;
	public ArizonaPage arizonaPage;
	public MyTimeTourPage myTimeTourPage;
	public EmpowerPage empowerPage;
	public PressReleasePage pressReleasePage;
	public BlogPage blogPage;
	public BlogDetailPage blogDetailPage;
	public SitecoreLaunchpadPage sitecoreLaunchpadPage;
	public SitecoreLoginPage sitecoreLoginPage;
	public SitecorePowershellISEPage powershellISEPage;
	public SearchResultsPage searchResultsPage;
	public PressReleaseArticlePage pressReleaseArticlePage;
	public VisualizerPage visualizerPage;

	public Object navigateToRequiredPage(ExpectedPage expectedPage) {

		Object obj = null;
		String text = "", expectedSearch = "", searchedProduct = "";
		switch (expectedPage) {
		case HomePage:
			obj = new HomePage(testConfig);
			break;

		case ContactUsPage:
			homePage = new HomePage(testConfig);
			contactUsPage = homePage.clickOnContactUsLink();
			obj = contactUsPage;
			break;

		case FYHPage:
			homePage = new HomePage(testConfig);
			text = testConfig.getRunTimeProperty("SearchText");
			expectedSearch = testConfig.getRunTimeProperty("ExpectedSearchText");
			searchedProduct = testConfig.getRunTimeProperty("SearchedProduct");
			fyhPage = (FYHPage) homePage.performSearch(text, expectedSearch, searchedProduct);
			obj = fyhPage;
			break;

		case CommunityMapFYHPage:
			homePage = new HomePage(testConfig);
			text = testConfig.getRunTimeProperty("SearchText");
			expectedSearch = testConfig.getRunTimeProperty("ExpectedSearchText");
			searchedProduct = testConfig.getRunTimeProperty("SearchedProduct");
			fyhPage = (FYHPage) homePage.performSearch(text, expectedSearch, searchedProduct);
			fyhPage = fyhPage.navigateToCommunityMapPage(searchedProduct);
			obj = fyhPage;
			break;
			
		case CommunityPage:
			homePage = new HomePage(testConfig);
			text = testConfig.getRunTimeProperty("SearchText");
			expectedSearch = testConfig.getRunTimeProperty("ExpectedSearchText");
			searchedProduct = testConfig.getRunTimeProperty("SearchedProduct");
			fyhPage = (FYHPage) homePage.performSearch(text, expectedSearch, searchedProduct);
			communityPage = fyhPage.navigateToCommunityPage(searchedProduct);
			obj = communityPage;
			break;

		case NeighborhoodPage:
			homePage = new HomePage(testConfig);
			text = testConfig.getRunTimeProperty("SearchText");
			expectedSearch = testConfig.getRunTimeProperty("ExpectedSearchText");
			searchedProduct = testConfig.getRunTimeProperty("SearchedProduct");
			fyhPage = (FYHPage) homePage.performSearch(text, expectedSearch, searchedProduct);
			communityPage = fyhPage.navigateToCommunityPage(searchedProduct);
			neighborhoodPage = communityPage.navigateToNeighborhoodPage();
			obj = neighborhoodPage;
			break;

		case PlanPage:
			homePage = new HomePage(testConfig);
			text = testConfig.getRunTimeProperty("SearchText");
			expectedSearch = testConfig.getRunTimeProperty("ExpectedSearchText");
			searchedProduct = testConfig.getRunTimeProperty("SearchedProduct");
			fyhPage = (FYHPage) homePage.performSearch(text, expectedSearch, searchedProduct);
			communityPage = fyhPage.navigateToCommunityPage(searchedProduct);
			neighborhoodPage = communityPage.navigateToNeighborhoodPage();
			planPage = neighborhoodPage.navigateToPlanPage();
			obj = planPage;
			break;

		case QMIPage:
			homePage = new HomePage(testConfig);
			text = testConfig.getRunTimeProperty("SearchText");
			expectedSearch = testConfig.getRunTimeProperty("ExpectedSearchText");
			searchedProduct = testConfig.getRunTimeProperty("SearchedProduct");
			fyhPage = (FYHPage) homePage.performSearch(text, expectedSearch, searchedProduct);
			communityPage = fyhPage.navigateToCommunityPage(searchedProduct);
			neighborhoodPage = communityPage.navigateToNeighborhoodPage();
			qmiPage = neighborhoodPage.navigateToQMIPage();
			obj = qmiPage;
			break;

		case AboutPage:
			homePage = new HomePage(testConfig);
			aboutPage = homePage.clickOnAboutLink();
			obj = aboutPage;
			break;

		case CareersPage:
			homePage = new HomePage(testConfig);
			careersPage = (CareersPage) homePage.clickOnCareersLink();
			obj = careersPage;
			break;

		case Sitemap:
			sitemapPage = new SitemapPage(testConfig);
			obj = sitemapPage;
			break;

		case PlanFromHomeSearch:
			homePage = new HomePage(testConfig);
			text = testConfig.getRunTimeProperty("SearchText");
			expectedSearch = testConfig.getRunTimeProperty("ExpectedSearchText");
			planPage = (PlanPage) homePage.performSearch(text, expectedSearch, "Plan");
			obj = planPage;
			break;
			
		case CommunityFromHomeSearch:
			homePage = new HomePage(testConfig);
			text = testConfig.getRunTimeProperty("SearchText");
			expectedSearch = testConfig.getRunTimeProperty("ExpectedSearchText");
			searchedProduct = testConfig.getRunTimeProperty("SearchedProduct");
			communityPage = (CommunityPage) homePage.performSearch(text, expectedSearch, searchedProduct);
			obj = communityPage;
			break;

		case BlogPage:
			homePage = new HomePage(testConfig);
			blogPage = homePage.clickOnBlogLink();
			obj = blogPage;
			break;

		case SitecoreLaunchPadPage:
			sitecoreLoginPage = new SitecoreLoginPage(testConfig);
			sitecoreLaunchpadPage = sitecoreLoginPage.navigateToLaunchpad();
			obj = sitecoreLaunchpadPage;
			break;

		case MyTime:
			homePage = new HomePage(testConfig);
			// myTimeTourPage = homePage.clickOnScheduleATourLink(); - update to below
			// dynamic redirection upon mytime page
			myTimeTourPage = homePage.navigateToMyTimePageDirectly();
			obj = myTimeTourPage;
			break;

		case VizPlanPage:
			homePage = new HomePage(testConfig);
			String vizUrlPlan = testConfig.getRunTimeProperty("VizURLPlan");
			Browser.navigateToURL(testConfig, vizUrlPlan);
			obj = new PlanPage(testConfig);
		}

		return obj;

	}

	/**
	 * Method to verify whether the provided list is sorted or not
	 * 
	 * @param obtainedList
	 * @param reverse
	 *            :- true in case of verification needs to be done in case of
	 *            decreasing list(reverse order verification) else false
	 */
	public void isPriceListSortedOrNot(ArrayList<Long> obtainedList, Boolean reverse) {

		ArrayList<Long> sortedList = new ArrayList<>();
		for (Long s : obtainedList) {
			sortedList.add(s);
		}
		Collections.sort(sortedList);

		if (reverse)
			Collections.reverse(sortedList);

		testConfig.logComment("Actual Obtained List from page is : " + Arrays.toString(obtainedList.toArray()));
		testConfig.logComment("Created Sorted List from Obtained list is : " + Arrays.toString(sortedList.toArray()));

		Helper.compareTrue(testConfig, "Sorting is working fine on the page", sortedList.equals(obtainedList));

	}

	/**
	 * Method to verify whether the provided list is sorted or not
	 * 
	 * @param obtainedList
	 * @param reverse
	 *            :- true in case of verification needs to be done in case of
	 *            decreasing list(reverse order verification) else false
	 */
	public void isListSortedOrNot(ArrayList<String> obtainedList, Boolean reverse) {

		ArrayList<String> sortedList = new ArrayList<>();
		for (String s : obtainedList) {
			sortedList.add(s);
		}
		Collections.sort(sortedList);

		if (reverse)
			Collections.reverse(sortedList);

		testConfig.logComment("Actual Obtained List from page is : " + Arrays.toString(obtainedList.toArray()));
		testConfig.logComment("Created Sorted List from Obtained list is : " + Arrays.toString(sortedList.toArray()));

		Helper.compareTrue(testConfig, "Sorting is working fine on the page", sortedList.equals(obtainedList));

	}

	public void submitScheduleTourFormAndVerifySuccessMsg(String leadForm, String expectedDescription, DifferentFormTypes differentFormTypes) {

		WebElement scheduleATourBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'btn primary')]");
		Element.click(testConfig, scheduleATourBtn, "Schedule A Tour button");
		Element.waitForElementDisplay(testConfig,
				Element.getPageElement(testConfig, How.xPath, ".//span[@class='checkbox__text'][text()='Yes']"));
		Element.click(testConfig,
				Element.getPageElement(testConfig, How.xPath, ".//span[@class='checkbox__text'][text()='Yes']"),
				"Yes checkbox");

		Browser.wait(testConfig, 2);

		String daysToVisit[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
		String timeOfDay[] = { "Morning", "Afternoon", "Evening" };

		Random rd = new Random();
		String preferredDay = daysToVisit[rd.nextInt(daysToVisit.length)];
		String preferredTime = timeOfDay[rd.nextInt(timeOfDay.length)];

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='schedule-an-appointment']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='schedule-an-appointment']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='schedule-an-appointment']//input[@placeholder='email@domain.com']");
		WebElement phoneField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='schedule-an-appointment']//input[@placeholder='Your Phone Number']");
		WebElement preferredDayToVisitField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='schedule-an-appointment']//select[@data-sc-field-name='Preferred Day']");
		WebElement preferredTimeOfDayField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='schedule-an-appointment']//select[@data-sc-field-name='Preferred Time']");

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
		Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
		Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");
		Element.enterData(testConfig, phoneField, String.valueOf(phoneNo), "Phone Number field value");

		Element.selectVisibleText(testConfig, preferredDayToVisitField, preferredDay,
				"Preferred Day to Visit field value");
		Element.selectVisibleText(testConfig, preferredTimeOfDayField, preferredTime,
				"Preferred Time Of Day field value");

		WebElement consentCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='schedule-an-appointment']//input[@data-sc-field-name='Terms and Conditions']");
		Element.click(testConfig, consentCheckbox, "Consent checkbox");

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='schedule-an-appointment']//button[@type='submit']");
		Element.click(testConfig, submitBtn, "Submit Button");

		verifySuccessResponseForScheduleTour(firstName);
		createExcelFileAndWriteEmailAddress(emailAddress, leadForm);

	}

	private void verifySuccessResponseForScheduleTour(String firstName) {

		Browser.wait(testConfig, 2);
		String expectedMsg = "Thank you, " + firstName;
		WebElement successMsg = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='schedule-an-appointment']//h2[@class='form-success__title']");
		Helper.compareEquals(testConfig, "Success message title", expectedMsg, successMsg.getText());

	}

	public void submitRequestInformationFormAndVerifySuccessMsg(String leadForm, String detailedMessage) {

		WebElement requestInfoBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'secondary lg')]");
		Element.click(testConfig, requestInfoBtn, "Request Information button");

		Browser.wait(testConfig, 2);

		submitFormAndVerifyMsg(leadForm, detailedMessage);
	}

	public void submitFormAndVerifyMsg(String leadForm, String detailedMessage) {

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='email@domain.com']");
		WebElement phoneField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@data-sc-field-name='Phone Number']");

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
		Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
		Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");
		Element.enterData(testConfig, phoneField, String.valueOf(phoneNo), "Phone Number field value");

		WebElement consentCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@data-sc-field-name='Terms and Conditions']");
		Element.click(testConfig, consentCheckbox, "Consent checkbox");

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//button[@type='submit']");
		Element.click(testConfig, submitBtn, "Submit Button");

		verifySuccessResponseForRequestInformation(firstName, detailedMessage);
		createExcelFileAndWriteEmailAddress(emailAddress, leadForm);
	}

	private void verifySuccessResponseForRequestInformation(String firstName, String expandedMessage) {

		Browser.wait(testConfig, 2);
		String expectedMsg = "Thank you, " + firstName + ".";
		WebElement successMsg = Element.getPageElement(testConfig, How.xPath,
				".//span[contains(@class,'cnt-type-brds-v2-base-300')]/../h2");
		Helper.compareEquals(testConfig, "Success message title", expectedMsg, successMsg.getText());

		WebElement messageSentText = Element.getPageElement(testConfig, How.xPath,
				".//span[contains(@class,'cnt-type-brds-v2-base-300')]");
		Helper.compareEquals(testConfig, "Success message title", "Message Sent!", messageSentText.getText());

		WebElement expandedMessageText = Element.getPageElement(testConfig, How.xPath, ".//span[contains(@class,'cnt-type-brds-v2-base-300')]/../div//span[1]");
		Helper.compareEquals(testConfig, "Detailed message", expandedMessage, expandedMessageText.getText());

		WebElement windowCloseMsg = Element.getPageElement(testConfig, How.xPath, ".//span[contains(@class,'cnt-type-brds-v2-base-300')]/../div//span[2]");
		Helper.compareEquals(testConfig, "Detailed message", "You may close this window.", windowCloseMsg.getText());

	}

	public void submitPreQualifiedFormAndVerifySuccessMsg(String leadForm, String expectedDescription) {

		WebElement getPreQualifiedBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'secondary md')]");
		Element.click(testConfig, getPreQualifiedBtn, "Get Pre-Qualified Link");

		Browser.wait(testConfig, 2);

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@placeholder='email@domain.com']");

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";

		Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
		Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
		Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");

		WebElement consentCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@data-sc-field-name='Terms and Conditions']");
		WebElement agreementCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@data-sc-field-name='Affiliated Business Agreement']");
		Element.click(testConfig, consentCheckbox, "Consent checkbox");
		Element.click(testConfig, agreementCheckbox, "Affiliated Business Agreement checkbox");

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//span[text()='Continue to BRP Home Mortgage']");
		Element.click(testConfig, submitBtn, "Continue to BRP Home Mortgage Button");

		verifySuccessResponseForGetPreQualified(firstName, expectedDescription);
		createExcelFileAndWriteEmailAddress(emailAddress, leadForm);

	}

	private void verifySuccessResponseForGetPreQualified(String firstName, String expectedDescription) {

		Browser.wait(testConfig, 2);
		String expectedMsg = "Thank you, " + firstName;
		WebElement successMsg = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//h2[@class='form-success__title']");
		Helper.compareEquals(testConfig, "Success message text", expectedMsg, successMsg.getText());

		String messageSentText = ".//div[@id='prequalifiedForm']//h4[@class='form-success__subhead']";
		WebElement messageSent = Element.getPageElement(testConfig, How.xPath, messageSentText);
		Helper.compareEquals(testConfig, "Success message sent text", "Message Sent!", messageSent.getText());

		String descriptionText = ".//div[@id='prequalifiedForm']//p[@class='form-success__description']";
		WebElement description = Element.getPageElement(testConfig, How.xPath, descriptionText);
		Helper.compareEquals(testConfig, "Description content", expectedDescription, description.getText());

	}

	public void submitPlanQMIScheduleATourFormAndVerifySuccessMsg(DifferentFormTypes formTypes, String leadForm) {

		WebElement scheduleATourBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'btn primary')]");
		Element.click(testConfig, scheduleATourBtn, "Schedule A Tour button");

		submitFormAndVerifySuccessMessage(formTypes, leadForm);

	}

	public void submitFormAndVerifySuccessMessage(DifferentFormTypes formTypes, String leadForm) {

		switch (formTypes) {
		case OnsiteTour:
			WebElement onsiteTour = Element.getPageElement(testConfig, How.xPath,
					".//span[text()='Onsite Tour with our Team']");
			Element.click(testConfig, onsiteTour, "Onsite Tour with our Team form");
			break;

		case VideoChat:
			WebElement videoChat = Element.getPageElement(testConfig, How.xPath,
					".//span[text()='Video Chat Tour with our Team']");
			Element.click(testConfig, videoChat, "Video Chat Tour with our Team form");
			break;

		case RequestInformation:
			break;
		}

		Browser.wait(testConfig, 2);

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='choose-tour']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='choose-tour']//input[@placeholder='Your Last Name']");
		WebElement emailField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='choose-tour']//input[@placeholder='email@domain.com']");
		WebElement phoneField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='choose-tour']//input[@placeholder='Your Phone Number']");
		WebElement consentCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='choose-tour']//input[@data-sc-field-name='Terms and Conditions']");
		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='choose-tour']//button[@type='submit']");
		WebElement messageField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='choose-tour']//textarea[@data-sc-field-name='Message']");

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);
		String message = "Test_" + Helper.generateRandomAlphabetsString(10);

		Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
		Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
		Element.enterData(testConfig, emailField, emailAddress, "Email Address field value");
		Element.enterData(testConfig, phoneField, String.valueOf(phoneNo), "Phone Number field value");
		Element.enterData(testConfig, messageField, message, "Message field value");

		switch (formTypes) {
		case OnsiteTour:
			WebElement preferredDayField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='choose-tour']//select[@data-sc-field-name='Preferred Day']");
			WebElement preferredTimeField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='choose-tour']//select[@data-sc-field-name='Preferred Time']");
			String daysToVisit[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
			String timeOfDay[] = { "Morning", "Afternoon", "Evening" };

			Random rd = new Random();
			String preferredDay = daysToVisit[rd.nextInt(daysToVisit.length)];
			String preferredTime = timeOfDay[rd.nextInt(timeOfDay.length)];

			Element.selectVisibleText(testConfig, preferredDayField, preferredDay,
					"Preferred Day to Visit field value");
			Element.selectVisibleText(testConfig, preferredTimeField, preferredTime,
					"Preferred Time Of Day field value");
			break;

		case VideoChat:
			break;

		case RequestInformation:
			break;
		}

		Element.click(testConfig, consentCheckbox, "Consent checkbox");
		Element.click(testConfig, submitBtn, "Submit Button");

		verifySuccessResponseForScheduleTourForm(firstName);
		createExcelFileAndWriteEmailAddress(emailAddress, leadForm);
	}

	private void verifySuccessResponseForScheduleTourForm(String firstName) {

		Browser.wait(testConfig, 2);
		String expectedMsg = "Thank you, " + firstName;
		WebElement successMsg = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='choose-tour']//h2[@class='form-success__title']");
		Helper.compareEquals(testConfig, "Success message title", expectedMsg, successMsg.getText());

	}

	public void verifyErrorMessagesScheduleTourForm(String[] expectedError) {

		WebElement scheduleATourBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'btn primary')]");
		Element.click(testConfig, scheduleATourBtn, "Schedule A Tour button");
		Element.waitForElementDisplay(testConfig,
				Element.getPageElement(testConfig, How.xPath, ".//span[@class='checkbox__text'][text()='Yes']"));
		Element.click(testConfig,
				Element.getPageElement(testConfig, How.xPath, ".//span[@class='checkbox__text'][text()='Yes']"),
				"Yes checkbox");

		Browser.wait(testConfig, 2);

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='schedule-an-appointment']//button[@type='submit']");
		Element.click(testConfig, submitBtn, "Submit Button");
		Browser.wait(testConfig, 2);

		verifyErrors(expectedError);
	}

	public void verifyErrorMessagesRequestInformationForm(String[] expectedError) {

		WebElement requestInfoBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'secondary lg')]");
		Element.click(testConfig, requestInfoBtn, "Request Information button");

		Browser.wait(testConfig, 2);

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//button[@type='submit']");
		Element.click(testConfig, submitBtn, "Submit Button");
		Browser.wait(testConfig, 1);

		verifyErrors(expectedError);
	}

	private void verifyErrors(String[] expectedError) {

		List<WebElement> errors = Element.getListOfElements(testConfig, How.xPath, ".//*[contains(@id,'Value-error')]");

		Helper.compareEquals(testConfig, "Error for 'First Name' field", expectedError[0], errors.get(0).getText());
		Helper.compareEquals(testConfig, "Error for 'Last Name' field", expectedError[1], errors.get(1).getText());
		Helper.compareEquals(testConfig, "Error for 'Email Address' field", expectedError[2], errors.get(2).getText());
		Helper.compareEquals(testConfig, "Error for 'Consent' field", expectedError[3], errors.get(3).getText());

	}

	public void submitFormAndVerifySuccessMsg(String leadForm) {

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long postalCode = Helper.generateRandomNumber(5);

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[contains(@class,'subscribe-form')]//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[contains(@class,'subscribe-form')]//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//section[contains(@class,'subscribe-form')]//input[@placeholder='email@domain.com']");
		WebElement postalCodeField = Element.getPageElement(testConfig, How.xPath,
				".//section[contains(@class,'subscribe-form')]//input[@placeholder='Postal Code']");
		WebElement consentField = Element.getPageElement(testConfig, How.xPath,
				".//section[contains(@class,'subscribe-form')]//input[@data-sc-field-name='Consent']");
		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[contains(@class,'subscribe-form')]//button[@value='Submit']");

		Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
		Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
		Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");
		Element.enterData(testConfig, postalCodeField, String.valueOf(postalCode), "Postal Code field value");
		Element.click(testConfig, consentField, "Consent field checkbox");

		Browser.wait(testConfig, 2);
		Element.click(testConfig, submitBtn, "Submit button");

		verifySuccessResponse(firstName);
		createExcelFileAndWriteEmailAddress(emailAddress, leadForm);
	}

	private void verifySuccessResponse(String firstName) {

		Browser.wait(testConfig, 2);
		String expectedMsg = "Thank you, " + firstName;
		WebElement successMsg = Element.getPageElement(testConfig, How.css, "h2.footer__formtitle");
		Helper.compareEquals(testConfig, "Success message title", expectedMsg, successMsg.getText());

	}

	public void readAllLinksAndVerifyStatus(String filePath)
			throws ParserConfigurationException, SAXException, IOException {

		int totalLinks = 0;
		List<String> list = new ArrayList<String>();
		Set<String> set = new LinkedHashSet<String>();

		File file = new File(filePath);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("loc");
		for (int temp = 0; temp < nodeList.getLength(); temp++) {
			Node node = nodeList.item(temp);
			list.add(node.getTextContent().trim());
			set.add(node.getTextContent().trim());
		}
		validateAllLinksOnPage(testConfig, set, totalLinks);
	}

	/**
	 * Method to find out the invalid link url, if there is any :- Invalid URL means
	 * the one which is throwing response status other than 200
	 * 
	 * @param testConfig
	 */
	public static void validateAllLinksOnPage(Config testConfig, Set<String> linkedHashSet, int totalLinks) {
		try {
			for (String urlObtained : linkedHashSet) {
				totalLinks++;
				if (urlObtained != null && !urlObtained.contains("javascript") && !urlObtained.contains("onetrust")
						&& !urlObtained.contains("utourhomes") && !urlObtained.contains("tel")
						&& !urlObtained.contains("blog") && !urlObtained.contains("press")) {
					verifyURLStatus(urlObtained, testConfig);
				}
			}
			testConfig.logComment("Total no. of invalid links are " + invalidLinksCount);
			testConfig.logComment("Total no. of links verified are " + totalLinks);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Method to create object of HttpClient and verify which link is not responding
	 * with status code as 200
	 * 
	 * @param url
	 * @throws UnsupportedEncodingException
	 */
	public static void verifyURLStatus(String url, Config testConfig) throws UnsupportedEncodingException {

		String username = testConfig.getRunTimeProperty("Username");
		String password = testConfig.getRunTimeProperty("Password");
		String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));

		String value = "";
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url.replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", ""));
		if (url.contains("brookfieldresidential")) {
			request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
		}
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() != 200) {
				invalidLinksCount++;
				value = url + " : " + response.getStatusLine().getStatusCode();
				testConfig.logFail(value);
			} else {
				value = url + " : " + response.getStatusLine().getStatusCode();
				testConfig.logPass(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FileWriter writer = new FileWriter(System.getProperty("user.dir") + "\\Parameters\\ProdError.txt", false);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);

			bufferedWriter.write(value);
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to find out the invalid image src link, if there is any :- Invalid URL
	 * means the one which is throwing response status other than 200
	 * 
	 * @param testConfig
	 */
	public void validateInvalidImages(Config testConfig) {
		try {
			invalidImageCount = 0;
			List<WebElement> imagesList = Element.getListOfElements(testConfig, How.tagName, "img");
			Set<String> linkedHashSet = new LinkedHashSet<>();

			for (WebElement imgElement : imagesList) {
				String url = imgElement.getAttribute("src");
				if (url.contains("bat.bing.com")) {
					testConfig.logComment("Removing " + url);
				} else if (url.contains("brookfieldresidential.com") || url.contains("cdn.brookfieldresidential.net")
						|| url.contains("azureedge.net") || url.contains("cdn.cookielaw.org") || url.contains("avidratings") || url.contains("azurewebsites.net")) {
					linkedHashSet.add(url);
				} else {
					linkedHashSet.add(homeurl + url);
				}
			}

			testConfig.logComment("Total no. of images are " + imagesList.size());
			testConfig.logComment("Total no. of unique images are " + imagesList.size());

			for (String imgSrc : linkedHashSet) {
				if (imgSrc != null) {
					testConfig.logComment(
							"URL : " + imgSrc.replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", ""));
					verifyURLStatus(imgSrc);
				}
			}
			testConfig.logComment("Total no. of invalid images are " + invalidImageCount);
		} catch (Exception e) {
			e.printStackTrace();
			testConfig.logComment(e.getMessage());
		}
	}

	/**
	 * Method to find out the invalid link url, if there is any :- Invalid URL means
	 * the one which is throwing response status other than 200
	 * 
	 * @param testConfig
	 */
	public void validateAllLinksOnPage(Config testConfig) {

		try {
			invalidLinksCount = 0;
			Set<String> linkedHashSet = new LinkedHashSet<>();
			List<WebElement> anchorTagsList = Element.getListOfElements(testConfig, How.tagName, "a");
			for (WebElement anchorTagElement : anchorTagsList) {
				String url = anchorTagElement.getAttribute("href");
				linkedHashSet.add(url);
			}

			testConfig.logComment("Total no. of links are " + anchorTagsList.size());
			testConfig.logComment("Total no. of unique links are " + linkedHashSet.size());

			for (String urlObtained : linkedHashSet) {
				if (urlObtained != null && !urlObtained.contains("javascript") && !urlObtained.contains("onetrust")
						&& !urlObtained.contains("utourhomes") && !urlObtained.contains("tel")) {
					verifyURLStatus(urlObtained);
				}
			}
			testConfig.logComment("Total no. of invalid links are " + invalidLinksCount);
		} catch (Exception e) {
			e.printStackTrace();
			testConfig.logComment(e.getMessage());
		}
	}

	/**
	 * Method to create object of HttpClient and verify which link is not responding
	 * with status code as 200
	 * 
	 * @param url
	 * @throws UnsupportedEncodingException
	 */
	public void verifyURLStatus(String url) throws UnsupportedEncodingException {

		String username = testConfig.getRunTimeProperty("Username");
		String password = testConfig.getRunTimeProperty("Password");
		String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url.replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", ""));
		if (url.contains("brookfieldresidential")) {
			request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
		}

		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() != 200) {
				invalidLinksCount++;
				testConfig.logFail("Invalid link url's are " + url.replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", "") + " and status code for that is "
						+ response.getStatusLine().getStatusCode());
			} else {
				String value = "Status code for " + url.replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", "") + " : " + response.getStatusLine().getStatusCode();
				testConfig.logPass(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to create an excel file for lead email address and in case excel is
	 * already there, then update it
	 * 
	 * @param emailAddress
	 * @param leadPage
	 */
	@SuppressWarnings("resource")
	public void createExcelFileAndWriteEmailAddress(String emailAddress, String leadPage) {

		boolean exist = checkWhetherFileIsThereOrNot(
				System.getProperty("user.dir") + "\\Parameters\\Leads_" + Helper.getCurrentDate("dd-MM-yyyy") + ".xls");

		if (exist) {
			try {
				InputStream myxls = new FileInputStream(new File(System.getProperty("user.dir") + "\\Parameters\\Leads_"
						+ Helper.getCurrentDate("dd-MM-yyyy") + ".xls"));
				Workbook workbook = new HSSFWorkbook(myxls);

				if (!workbook.getSheetName(0).equals(Helper.getCurrentDate("dd-MM-yyyy") + "_Leads")) {
					workbook.createSheet(Helper.getCurrentDate("dd-MM-yyyy") + "_Leads");
				}

				Sheet sheet = workbook.getSheetAt(0);
				Row row1 = sheet.createRow(0);
				Cell row1col1 = row1.createCell(0);
				Cell row1col2 = row1.createCell(1);
				Cell row1col3 = row1.createCell(2);
				row1col1.setCellValue("Email Addresses");
				row1col2.setCellValue("Lead Page");
				row1col3.setCellValue("Page URL");

				int rowCount = sheet.getLastRowNum();
				Row row = sheet.createRow(++rowCount);
				int columnCount = 0;

				Cell cell = row.createCell(columnCount);
				cell.setCellValue(emailAddress);
				columnCount = 1;
				cell = row.createCell(columnCount);
				cell.setCellValue(leadPage);
				columnCount = 2;
				cell = row.createCell(columnCount);
				cell.setCellValue(testConfig.driver.getCurrentUrl());

				FileOutputStream outFile = new FileOutputStream(new File(System.getProperty("user.dir")
						+ "\\Parameters\\Leads_" + Helper.getCurrentDate("dd-MM-yyyy") + ".xls"));
				workbook.write(outFile);
				outFile.close();
				testConfig.logComment("File updated!!");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else {
			// create a new workbook
			Workbook wb = new HSSFWorkbook();

			// add a new sheet to the workbook
			Sheet sheet = wb.createSheet(Helper.getCurrentDate("dd-MM-yyyy") + "_Leads");
			Row row1 = sheet.createRow(0);
			Row row2 = sheet.createRow(1);
			Cell row1col1 = row1.createCell(0);
			Cell row2col1 = row2.createCell(0);
			Cell row1col2 = row1.createCell(1);
			Cell row2col2 = row2.createCell(1);
			Cell row1col3 = row1.createCell(2);
			Cell row2col3 = row2.createCell(2);

			row1col1.setCellValue("Email Addresses");
			row2col1.setCellValue(emailAddress);

			row1col2.setCellValue("Lead Page");
			row2col2.setCellValue(leadPage);

			row1col3.setCellValue("Page URL");
			row2col3.setCellValue(testConfig.driver.getCurrentUrl());

			// write the excel to a file
			try {
				FileOutputStream fileOut = new FileOutputStream(new File(System.getProperty("user.dir")
						+ "\\Parameters\\Leads_" + Helper.getCurrentDate("dd-MM-yyyy") + ".xls"));
				wb.write(fileOut);
				fileOut.close();
				testConfig.logComment("File created!!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to verify sheet is already present or not
	 * 
	 * @param string
	 */
	private boolean checkWhetherFileIsThereOrNot(String fileName) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void verifyFAQDataOverPage(HashMap<String, String> expectedData) {

		String questionLocator = "dt>button>span.text-xl";
		String answerLocator = "dd>p";

		List<WebElement> allQuestions = Element.getListOfElements(testConfig, How.css, questionLocator);
		List<WebElement> allAnswers = Element.getListOfElements(testConfig, How.css, answerLocator);

		HashMap<String, String> actualData = new HashMap<>();

		for (int i = 0; i < allQuestions.size(); i++) {
			Element.click(testConfig, allQuestions.get(i), allQuestions.get(i).getText() + " FAQ");
			actualData.put(allQuestions.get(i).getAttribute("innerText").trim(),
					allAnswers.get(i).getAttribute("innerText").trim());
			Browser.wait(testConfig, 1);
		}

		verifyAllAnswersDisplaying(allAnswers);
		clickAllQuestionAgainAndVerifyNoAnswerDisplaying(allQuestions, allAnswers);

		Helper.compareEquals(testConfig, expectedData, actualData);

	}

	private void clickAllQuestionAgainAndVerifyNoAnswerDisplaying(List<WebElement> allQuestions,
			List<WebElement> allAnswers) {

		for (int i = 0; i < allQuestions.size(); i++) {
			Element.click(testConfig, allQuestions.get(i), allQuestions.get(i).getText() + " FAQ");
			Browser.wait(testConfig, 1);
		}
		try {
			for (int i = 0; i < allAnswers.size(); i++) {
				if (!allAnswers.get(i).isDisplayed()) {
					testConfig.logPass("Verified answer is hidden now FAQ " + (i + 1));
				} else {
					testConfig.logFail("Failed to verify that answer is hidden now FAQ " + (i + 1));
				}
			}

		} catch (Exception e) {
			testConfig.logFail("Failed to verify that answer is hidden now FAQs correctly");
		}
	}

	private void verifyAllAnswersDisplaying(List<WebElement> allAnswers) {

		try {
			for (int i = 0; i < allAnswers.size(); i++) {
				if (allAnswers.get(i).isDisplayed()) {
					testConfig.logPass("Verified getting answer displaying for FAQ " + (i + 1) + " as "
							+ allAnswers.get(i).getText());
				} else {
					testConfig.logFail("Failed to verify that we are getting answer displaying for FAQ " + (i + 1)
							+ " as " + allAnswers.get(i).getText());
				}
			}

		} catch (Exception e) {
			testConfig.logFail("Failed to verify that we are getting answer displaying for FAQs correctly");
		}

	}

	public void verifyScheduleATourBtnFunctionality() {

		WebElement scheduleATourBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'btn primary')]");
		Element.click(testConfig, scheduleATourBtn, "Schedule A Tour button");
		Browser.wait(testConfig, 2);

		String formLocator = ".//button[@class='contained lg btn']//ancestor::div[@data-headlessui-state='open']//div[contains(@class,'cnt-relative')]";

		try {
			WebElement modalForm = Element.getPageElement(testConfig, How.xPath, formLocator);
			if (modalForm.isDisplayed()) {
				testConfig.logPass("Clicking 'Schedule A Tour' button opens modal successfully");
			} else {
				testConfig
				.logFail("Failed to verify that on clicking 'Schedule A Tour' button opens modal successfully");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that on clicking 'Schedule A Tour' button opens modal successfully");
		}

		String crossButton = ".//button[@class='contained lg btn']";
		WebElement crossBtn = Element.getPageElement(testConfig, How.xPath, crossButton);
		Element.click(testConfig, crossBtn, "Cross button");
		Browser.wait(testConfig, 2);

	}

	public void verifyOnsiteAndVideoChatForms() {

		WebElement scheduleATourBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'btn primary')]");
		Element.click(testConfig, scheduleATourBtn, "Schedule A Tour button");

		WebElement onsiteTour = Element.getPageElement(testConfig, How.xPath,
				".//span[text()='Onsite Tour with our Team']");
		verifyFormViewForOnsiteTour(onsiteTour);

		Browser.waitWithoutLogging(testConfig, 3);
		WebElement backBtn = Element.getPageElement(testConfig, How.xPath,
				".//button[@class='link dark lg cnt-font-bold']");
		Element.click(testConfig, backBtn, "Back Button");

		Browser.waitWithoutLogging(testConfig, 3);
		WebElement videoChat = Element.getPageElement(testConfig, How.xPath,
				".//span[text()='Video Chat Tour with our Team']");
		verifyFormViewForVideoChatTour(videoChat);

	}

	private void verifyFormViewForVideoChatTour(WebElement videoChat) {

		String commentValue = "I’m interested in";
		Element.click(testConfig, videoChat, "Video Chat Tour with our Team");
		Browser.wait(testConfig, 1);

		String daysToVisit[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
		String timeOfDay[] = { "Morning", "Afternoon", "Evening" };

		Random rd = new Random();
		String preferredDay = daysToVisit[rd.nextInt(daysToVisit.length)];
		String preferredTime = timeOfDay[rd.nextInt(timeOfDay.length)];

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//input[@placeholder='email@domain.com']");
		WebElement phoneField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//input[@placeholder='(XXX) XXX-XXXX']");
		WebElement preferredDayToVisitField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//select[@data-sc-field-name='Preferred Day']");
		WebElement preferredTimeOfDayField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//select[@data-sc-field-name='Preferred Time']");

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
		Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
		Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");
		Element.enterData(testConfig, phoneField, String.valueOf(phoneNo), "Phone Number field value");

		String phoneNum = String.valueOf(phoneNo);
		String formattedPhoneNum = "(" + phoneNum.substring(0, 3) + ") " + phoneNum.substring(3,6) + "-" + phoneNum.substring(6);

		Element.selectVisibleText(testConfig, preferredDayToVisitField, preferredDay,
				"Preferred Day to Visit field value");
		Element.selectVisibleText(testConfig, preferredTimeOfDayField, preferredTime,
				"Preferred Time Of Day field value");

		Select select = new Select(preferredTimeOfDayField);
		WebElement preferredTimeOfDayFieldVal = select.getFirstSelectedOption();
		String preferredTimeOfDayFieldValue = preferredTimeOfDayFieldVal.getText();

		Select selectOpt = new Select(preferredDayToVisitField);
		WebElement preferredDayToVisitFieldVal = selectOpt.getFirstSelectedOption();
		String preferredDayToVisitFieldValue = preferredDayToVisitFieldVal.getText();

		String comment = ".//section[@id='video-chat-tour']//textarea[@data-sc-field-name='Message']";
		WebElement commentSection = Element.getPageElement(testConfig, How.xPath, comment);

		String formHeading = ".//section[@id='video-chat-tour']//parent::div//h2";
		WebElement heading = Element.getPageElement(testConfig, How.xPath, formHeading);

		Helper.compareEquals(testConfig, "Form heading", "Video Chat Tour with our Team", heading.getText());
		Helper.compareEquals(testConfig, "Text entered for First Name field", firstName,
				firstNameField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Last Name field", lastName,
				lastNameField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Email Address field", emailAddress,
				emailAddressField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Phone Number field", formattedPhoneNum,
				phoneField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Select option selected for preferred Day To Visit Field", preferredDay,
				preferredDayToVisitFieldValue);
		Helper.compareEquals(testConfig, "Select option selected for preferred Time To Visit Field", preferredTime,
				preferredTimeOfDayFieldValue);
		Helper.compareContains(testConfig, "Comment section value", commentValue,
				commentSection.getAttribute("placeholder"));
	}

	private void verifyFormViewForOnsiteTour(WebElement onsiteTour) {

		String commentValue = "I’m interested in";
		Element.click(testConfig, onsiteTour, "Onsite Tour with our Team");
		Browser.wait(testConfig, 1);

		String daysToVisit[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
		String timeOfDay[] = { "Morning", "Afternoon", "Evening" };

		Random rd = new Random();
		String preferredDay = daysToVisit[rd.nextInt(daysToVisit.length)];
		String preferredTime = timeOfDay[rd.nextInt(timeOfDay.length)];

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//input[@placeholder='email@domain.com']");
		WebElement phoneField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//input[@placeholder='(XXX) XXX-XXXX']");
		WebElement preferredDayToVisitField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//select[@data-sc-field-name='Preferred Day']");
		WebElement preferredTimeOfDayField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//select[@data-sc-field-name='Preferred Time']");

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
		Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
		Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");
		Element.enterData(testConfig, phoneField, String.valueOf(phoneNo), "Phone Number field value");

		String phoneNum = String.valueOf(phoneNo);
		String formattedPhoneNum = "(" + phoneNum.substring(0, 3) + ") " + phoneNum.substring(3,6) + "-" + phoneNum.substring(6);

		Element.selectVisibleText(testConfig, preferredDayToVisitField, preferredDay,
				"Preferred Day to Visit field value");
		Element.selectVisibleText(testConfig, preferredTimeOfDayField, preferredTime,
				"Preferred Time Of Day field value");

		Select select = new Select(preferredTimeOfDayField);
		WebElement preferredTimeOfDayFieldVal = select.getFirstSelectedOption();
		String preferredTimeOfDayFieldValue = preferredTimeOfDayFieldVal.getText();

		Select selectOpt = new Select(preferredDayToVisitField);
		WebElement preferredDayToVisitFieldVal = selectOpt.getFirstSelectedOption();
		String preferredDayToVisitFieldValue = preferredDayToVisitFieldVal.getText();

		String comment = ".//section[@id='onsite-tour']//textarea[@data-sc-field-name='Message']";
		WebElement commentSection = Element.getPageElement(testConfig, How.xPath, comment);

		String formHeading = ".//section[@id='onsite-tour']//parent::div//h2";
		WebElement heading = Element.getPageElement(testConfig, How.xPath, formHeading);

		Helper.compareEquals(testConfig, "Form heading", "Onsite Tour with our Team", heading.getText());
		Helper.compareEquals(testConfig, "Text entered for First Name field", firstName,
				firstNameField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Last Name field", lastName,
				lastNameField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Email Address field", emailAddress,
				emailAddressField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Phone Number field", formattedPhoneNum,
				phoneField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Select option selected for preferred Day To Visit Field", preferredDay,
				preferredDayToVisitFieldValue);
		Helper.compareEquals(testConfig, "Select option selected for preferred Time To Visit Field", preferredTime,
				preferredTimeOfDayFieldValue);
		Helper.compareContains(testConfig, "Comment section value", commentValue,
				commentSection.getAttribute("placeholder"));
	}

	public void verifyFieldsValidationForVideoChatForm(String[] expectedErrorMsgs) {

		WebElement scheduleATourBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'btn primary')]");
		Element.click(testConfig, scheduleATourBtn, "Schedule A Tour button");
		Browser.wait(testConfig, 2);

		WebElement videoChat = Element.getPageElement(testConfig, How.xPath,
				".//span[text()='Video Chat Tour with our Team']");
		Element.click(testConfig, videoChat, "Video Chat Tour with our Team");
		Browser.wait(testConfig, 1);

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//input[@placeholder='email@domain.com']");

		String[] fields = { "First Name", "Last Name", "Email Address", "Consent" };

		Element.clear(testConfig, firstNameField, "First Name field value");
		Element.clear(testConfig, lastNameField, "Last Name field value");
		Element.clear(testConfig, emailAddressField, "Email Address field value");

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//button[@type='submit']");
		Element.click(testConfig, submitBtn, "Submit Button");

		Browser.wait(testConfig, 3);
		List<WebElement> errorMsgs = Element.getListOfElements(testConfig, How.xPath,
				".//span[contains(@class,'field-validation-error')]/span");
		for (int i = 0; i < fields.length; i++) {
			Helper.compareEquals(testConfig, "Error message for empty " + fields[i], expectedErrorMsgs[i],
					errorMsgs.get(i).getText());
		}
	}

	public void verifyCheckboxesBehaviorVideoChat() {

		WebElement consentCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//input[@data-sc-field-name='Terms and Conditions']");
		Element.click(testConfig, consentCheckbox, "Consent checkbox");

		try {
			if (consentCheckbox.isSelected()) {
				testConfig.logPass("Verified 'Consent' checkbox is displaying selected");
			} else {
				testConfig.logFail("Failed to verify that 'Consent' checkbox is displaying selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Consent' checkbox is displaying selected");
		}

		WebElement agentYesCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//input[@data-sc-field-name='Agent Opt-In'][@value='true']");
		Element.click(testConfig, agentYesCheckbox, "Agent Yes checkbox");
		Browser.waitWithoutLogging(testConfig, 2);
		try {
			if (agentYesCheckbox.isSelected()) {
				testConfig.logPass("Verified 'Agent - Yes' checkbox is displaying selected");
			} else {
				testConfig.logFail("Failed to verify that 'Agent - Yes' checkbox is displaying selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Agent - Yes' checkbox is displaying selected");
		}

		WebElement agentNoCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='video-chat-tour']//input[@data-sc-field-name='Agent Opt-In'][@value='false']");
		Element.click(testConfig, agentNoCheckbox, "Agent No checkbox");
		Browser.waitWithoutLogging(testConfig, 2);

		try {
			if (agentNoCheckbox.isSelected()) {
				testConfig.logPass("Verified 'Agent - No' checkbox is displaying selected");
			} else {
				testConfig.logFail("Failed to verify that 'Agent - No' checkbox is displaying selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Agent - No' checkbox is displaying selected");
		}
	}

	public void verifyFieldsValidationForOnsiteTourForm(String[] expectedErrorMsgs) {

		WebElement scheduleATourBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'btn primary')]");
		Element.click(testConfig, scheduleATourBtn, "Schedule A Tour button");
		Browser.wait(testConfig, 2);

		WebElement onsiteTour = Element.getPageElement(testConfig, How.xPath,
				".//span[text()='Onsite Tour with our Team']");
		Element.click(testConfig, onsiteTour, "Onsite Tour with our Team");
		Browser.wait(testConfig, 1);

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//input[@placeholder='email@domain.com']");

		String[] fields = { "First Name", "Last Name", "Email Address", "Consent" };

		Element.clear(testConfig, firstNameField, "First Name field value");
		Element.clear(testConfig, lastNameField, "Last Name field value");
		Element.clear(testConfig, emailAddressField, "Email Address field value");

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//button[@type='submit']");
		Element.click(testConfig, submitBtn, "Submit Button");

		Browser.wait(testConfig, 4);
		List<WebElement> errorMsgs = Element.getListOfElements(testConfig, How.xPath,
				".//span[contains(@class,'field-validation-error')]/span");
		for (int i = 0; i < fields.length; i++) {
			Helper.compareEquals(testConfig, "Error message for empty " + fields[i], expectedErrorMsgs[i],
					errorMsgs.get(i).getText());
		}
	}

	public void verifyCheckboxesBehaviorForOnsiteTourForm() {

		WebElement consentCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//input[@data-sc-field-name='Terms and Conditions']");
		Element.click(testConfig, consentCheckbox, "Consent checkbox");

		try {
			if (consentCheckbox.isSelected()) {
				testConfig.logPass("Verified 'Consent' checkbox is displaying selected");
			} else {
				testConfig.logFail("Failed to verify that 'Consent' checkbox is displaying selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Consent' checkbox is displaying selected");
		}

		WebElement agentYesCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//input[@data-sc-field-name='Agent Opt-In'][@value='true']");
		Element.click(testConfig, agentYesCheckbox, "Agent Yes checkbox");
		Browser.waitWithoutLogging(testConfig, 2);
		try {
			if (agentYesCheckbox.isSelected()) {
				testConfig.logPass("Verified 'Agent - Yes' checkbox is displaying selected");
			} else {
				testConfig.logFail("Failed to verify that 'Agent - Yes' checkbox is displaying selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Agent - Yes' checkbox is displaying selected");
		}

		WebElement agentNoCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='onsite-tour']//input[@data-sc-field-name='Agent Opt-In'][@value='false']");
		Element.click(testConfig, agentNoCheckbox, "Agent No checkbox");
		Browser.waitWithoutLogging(testConfig, 2);

		try {
			if (agentNoCheckbox.isSelected()) {
				testConfig.logPass("Verified 'Agent - No' checkbox is displaying selected");
			} else {
				testConfig.logFail("Failed to verify that 'Agent - No' checkbox is displaying selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Agent - No' checkbox is displaying selected");
		}
	}

	public void verifyRequestInfoFormOpenClose() {

		WebElement requestInfoBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'secondary lg')]");
		Element.click(testConfig, requestInfoBtn, "Request Information button");
		Browser.wait(testConfig, 2);

		String crossButton = ".//button[@class='contained lg btn']";
		WebElement crossBtn = Element.getPageElement(testConfig, How.xPath, crossButton);
		Element.click(testConfig, crossBtn, "Cross button");
		Browser.wait(testConfig, 1);

	}

	public void verifyFormOpensCorrectlyRequestInfo() {

		String formLocator = ".//div[contains(@id,'headless')][@aria-modal='true']";
		try {
			WebElement modalForm = Element.getPageElement(testConfig, How.xPath, formLocator);
			if (modalForm.isDisplayed()) {
				testConfig.logPass("Clicking 'Request Information' button opens modal successfully");
			} else {
				testConfig.logFail(
						"Failed to verify that on clicking 'Request Information' button opens modal successfully");
			}
		} catch (Exception e) {
			testConfig
			.logFail("Failed to verify that on clicking 'Request Information' button opens modal successfully");
		}

		String crossButton = ".//button[@class='contained lg btn']";
		WebElement crossBtn = Element.getPageElement(testConfig, How.xPath, crossButton);
		Element.click(testConfig, crossBtn, "Cross button");
		Browser.wait(testConfig, 2);
	}

	public void verifyFieldsValidationForRequestInfoForm(String[] expectedErrorMsgs) {

		String[] fields = { "First Name", "Last Name", "Email Address", "Consent" };

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='email@domain.com']");
		WebElement phoneField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='(XXX) XXX-XXXX']");

		Element.clear(testConfig, firstNameField, "First Name field");
		Element.clear(testConfig, lastNameField, "Last Name field");
		Element.clear(testConfig, emailAddressField, "Email address field");
		Element.clear(testConfig, phoneField, "Phone number field");

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//button[@type='submit']");
		Element.click(testConfig, submitBtn, "Submit Button");
		Browser.wait(testConfig, 2);

		List<WebElement> errorMsgs = Element.getListOfElements(testConfig, How.css, "span.field-validation-error");

		for (int i = 0; i < fields.length; i++) {
			Helper.compareEquals(testConfig, "Error message for empty " + fields[i], expectedErrorMsgs[i],
					errorMsgs.get(i).getText());
		}
	}

	public void verifyFieldsAreEditableRequestInfoForm(boolean flag) {

		if(flag) {
			WebElement requestInfoBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='contact-us']//button[contains(@class,'secondary lg')]");
			Element.click(testConfig, requestInfoBtn, "Request Information button");
			Browser.wait(testConfig, 8);
		}
		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='email@domain.com']");
		WebElement phoneField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='(XXX) XXX-XXXX']");

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		JavascriptExecutor jst = (JavascriptExecutor) testConfig.driver;
		jst.executeScript("arguments[1].value = arguments[0]; ", firstName, firstNameField);
		jst.executeScript("arguments[1].value = arguments[0]; ", lastName, lastNameField);
		jst.executeScript("arguments[1].value = arguments[0]; ", emailAddress, emailAddressField);
		jst.executeScript("arguments[1].value = arguments[0]; ", String.valueOf(phoneNo), phoneField);

		Helper.compareEquals(testConfig, "Text entered for First Name field", firstName,
				firstNameField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Last Name field", lastName,
				lastNameField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Email Address field", emailAddress,
				emailAddressField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Phone Number field", String.valueOf(phoneNo),
				phoneField.getAttribute("value"));
	}

	public void verifyCheckboxesBehaviorRequestInformation() {

		WebElement consentCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@data-sc-field-name='Terms and Conditions']");
		Element.click(testConfig, consentCheckbox, "Consent checkbox");

		try {
			if (consentCheckbox.isSelected()) {
				testConfig.logPass("Verified 'Consent' checkbox is displaying selected");
			} else {
				testConfig.logFail("Failed to verify that 'Consent' checkbox is displaying selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Consent' checkbox is displaying selected");
		}

		WebElement agentYesCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@data-sc-field-name='Agent Opt-In'][@value='true']");
		Element.click(testConfig, agentYesCheckbox, "Agent Yes checkbox");
		Browser.waitWithoutLogging(testConfig, 2);

		try {
			if (agentYesCheckbox.isSelected()) {
				testConfig.logPass("Verified 'Agent - Yes' checkbox is displaying selected");
			} else {
				testConfig.logFail("Failed to verify that 'Agent - Yes' checkbox is displaying selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Agent - Yes' checkbox is displaying selected");
		}

		WebElement agentNoCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@data-sc-field-name='Agent Opt-In'][@value='false']");
		Element.click(testConfig, agentNoCheckbox, "Agent No checkbox");
		Browser.waitWithoutLogging(testConfig, 2);

		try {
			if (agentNoCheckbox.isSelected()) {
				testConfig.logPass("Verified 'Agent - No' checkbox is displaying selected");
			} else {
				testConfig.logFail("Failed to verify that 'Agent - No' checkbox is displaying selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Agent - No' checkbox is displaying selected");
		}
	}

	public void verifyErrorMessagesGetPrequalifiedForm(String[] errors) {

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@placeholder='email@domain.com']");

		Element.clear(testConfig, firstNameField, "First name field");
		Element.clear(testConfig, lastNameField, "Last name field");
		Element.clear(testConfig, emailAddressField, "Email Address field");

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//span[text()='Continue to BRP Home Mortgage']");
		Element.click(testConfig, submitBtn, "Continue to BRP Home Mortgage Button");

		verifyErrorsOverGetPrequalifyForm(errors);

	}

	private void verifyErrorsOverGetPrequalifyForm(String[] expectedError) {

		List<WebElement> errors = Element.getListOfElements(testConfig, How.xPath, ".//*[contains(@id,'Value-error')]");

		Helper.compareEquals(testConfig, "Error for 'First Name' field", expectedError[0], errors.get(0).getText());
		Helper.compareEquals(testConfig, "Error for 'Last Name' field", expectedError[1], errors.get(1).getText());
		Helper.compareEquals(testConfig, "Error for 'Email Address' field", expectedError[2], errors.get(2).getText());
		Helper.compareEquals(testConfig, "Error for 'Consent' field", expectedError[3], errors.get(3).getText());
		Helper.compareEquals(testConfig, "Error for 'Accept the Affiliated Business Agreement' field", expectedError[4],
				errors.get(4).getText());

	}

	public void verifyGetPrequalifiedFormOpenClose() {

		WebElement getPreQualifiedBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'secondary md')]");
		Element.click(testConfig, getPreQualifiedBtn, "Get Pre-Qualified Link");
		Browser.wait(testConfig, 2);

		String formLocator = ".//div[@id='headlessui-portal-root']//div[contains(@class,'modal-content')]";
		try {
			WebElement modalForm = Element.getPageElement(testConfig, How.xPath, formLocator);
			if (modalForm.isDisplayed()) {
				testConfig.logPass("Clicking 'Request Information' button opens modal successfully");
			} else {
				testConfig.logFail(
						"Failed to verify that on clicking 'Request Information' button opens modal successfully");
			}
		} catch (Exception e) {
			testConfig
			.logFail("Failed to verify that on clicking 'Request Information' button opens modal successfully");
		}

		String crossButton = ".//div[@id='headlessui-portal-root']//div[contains(@class,'modal-content')]/div/button";
		WebElement crossBtn = Element.getPageElement(testConfig, How.xPath, crossButton);
		Element.click(testConfig, crossBtn, "Cross button");
		Browser.wait(testConfig, 2);
	}

	public void verifyCustomerReviewsSection(String expectedSectionHeading, String expectedSectionTitle,
			String avidText, String expectedURL) {

		WebElement sectionHeading = Element.getPageElement(testConfig, How.css, "section[class*='glo'] h3");
		WebElement sectionTitle = Element.getPageElement(testConfig, How.css, "div[class*='glo-bg-brds-v2-white'] div:nth-of-type(1)");
		WebElement avidInfo = Element.getPageElement(testConfig, How.css, "section[class*='glo'] p:nth-of-type(1)");

		WebElement readAllReviews = Element.getPageElement(testConfig, How.css, "section[class*='glo'] a");
		try {
			if (readAllReviews.isDisplayed()) {
				testConfig.logPass("Verified 'Read All Reviews' CTA is displaying in the dropdown");
			} else {
				testConfig.logFail("Failed to verify that 'Read All Reviews' CTA is displaying in the dropdown");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Read All Reviews' CTA is displaying in the dropdown");
		}

		Helper.compareEquals(testConfig, "Customer Reviews module section heading", expectedSectionHeading,
				sectionHeading.getText());
		Helper.compareEquals(testConfig, "Customer Reviews section title", expectedSectionTitle, sectionTitle.getText());
		Helper.compareEquals(testConfig, "Customer Reviews section avid info", avidText, avidInfo.getText());

		Helper.compareEquals(testConfig, "Link associated with 'Read All Reviews' button", expectedURL,
				readAllReviews.getAttribute("href"));

		try {
			verifyURLStatus(readAllReviews.getAttribute("href"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void verifyFieldsAreEditableGetPrequalifiedForm() {

		WebElement getPreQualifiedBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='contact-us']//button[contains(@class,'secondary md')]");
		Element.click(testConfig, getPreQualifiedBtn, "Get Pre-Qualified Link");
		Browser.wait(testConfig, 2);

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@placeholder='email@domain.com']");

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";

		Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
		Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
		Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");

		Helper.compareEquals(testConfig, "Text entered for First Name field", firstName,
				firstNameField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Last Name field", lastName,
				lastNameField.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Email Address field", emailAddress,
				emailAddressField.getAttribute("value"));

	}

	public void verifyCheckboxesBehaviorPrequalifiedForm() {

		WebElement consentCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@data-sc-field-name='Terms and Conditions']");
		Element.click(testConfig, consentCheckbox, "Consent checkbox");

		try {
			if (consentCheckbox.isSelected()) {
				testConfig.logPass("Verified 'Consent' checkbox is displaying selected");
			} else {
				testConfig.logFail("Failed to verify that 'Consent' checkbox is displaying selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Consent' checkbox is displaying selected");
		}

		WebElement agreementCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//div[@id='prequalifiedForm']//input[@data-sc-field-name='Affiliated Business Agreement']");
		Element.click(testConfig, agreementCheckbox, "Agreement checkbox");

		try {
			if (consentCheckbox.isSelected()) {
				testConfig.logPass("Verified 'Agreement' checkbox is displaying selected");
			} else {
				testConfig.logFail("Failed to verify that 'Agreement' checkbox is displaying selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Agreement' checkbox is displaying selected");
		}
	}

	public void verifyAffiliatedBusinessAgreementLink() {

		String affiliatedBusinessLinkLoc = ".//div[@id='prequalifiedForm']//input[@data-sc-field-name='Affiliated Business Agreement']/..//a";
		WebElement affiliatedBusinessLink = Element.getPageElement(testConfig, How.xPath, affiliatedBusinessLinkLoc);
		Helper.compareContains(testConfig, " link", "affiliated-business-agreement",
				affiliatedBusinessLink.getAttribute("href"));

		try {
			verifyURLStatus(affiliatedBusinessLink.getAttribute("href"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void verifyCommunityAddressAndCommunityWebSiteLink(String addressOverForm) {

		String crossButton = ".//div[@id='headlessui-portal-root']//button[@aria-label='Close']";
		WebElement crossBtn = Element.getPageElement(testConfig, How.xPath, crossButton);
		Element.click(testConfig, crossBtn, "Cross button");

		Browser.waitWithoutLogging(testConfig, 2);
		String addressLocator = ".//h3[text()='Welcome Center']/parent::div//a[@target='_blank']";
		WebElement addressElement = Element.getPageElement(testConfig, How.xPath, addressLocator);

		Helper.compareEquals(testConfig, "Community address", addressOverForm,
				addressElement.getAttribute("innerText"));
		String url = Element.getAttribute(testConfig, addressElement, "href", "Address");
		Helper.compareContains(testConfig, "URL for address location", "goo", url);

		String websiteLinkLocator = ".//*[contains(text(),'COMMUNITY WEBSITE')]/parent::div/div/a";
		WebElement websiteLink = Element.getPageElement(testConfig, How.xPath, websiteLinkLocator);

		try {
			verifyURLStatus(websiteLink.getAttribute("href"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void verifyFloorPlanSectionOverPage() {

		String floorPlanSelectorBtn = ".//section[@id='floor-plan-section']//button[contains(@class,'phps-rounded-4xl')]";
		WebElement floorPlanSelector = Element.getPageElement(testConfig, How.xPath, floorPlanSelectorBtn);

		String belowArrowLocator = ".//section[@id='floor-plan-section']//*[name()='svg'][contains(@class,'phps-rotate-90')]";
		WebElement dropArrow = Element.getPageElement(testConfig, How.xPath, belowArrowLocator);
		
		if (dropArrow == null) {
			if (!floorPlanSelector.getAttribute("class").contains("cursor-not-allowed")) {
				testConfig.logFail(
						"Getting button as enabled even when there is only one floor plan associated with the plan item");
			} else {
				testConfig.logPass(
						"Getting button as disabled correctly when there is only one floor plan associated with the plan item");
			}

			WebElement largeImage = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='floor-plan-section']//img");
			String imageSrc = Element.getAttribute(testConfig, largeImage, "src", "Large image view");
			verifyURLAsPerDomain(imageSrc);

		} else {
			if (!floorPlanSelector.getAttribute("class").contains("cursor-not-allowed")) {
				testConfig.logPass(
						"Getting button as enabled correctly when there are more than one floor plan associated with the plan item");
			} else {
				testConfig.logFail(
						"Getting button as disabled even when there are more than one floor plan associated with the plan item");
			}

			Element.click(testConfig, dropArrow, "Down arrow for floor plan");
			Browser.wait(testConfig, 2);

			List<WebElement> allFloorPlans = Element.getListOfElements(testConfig, How.xPath,
					".//section[@id='floor-plan-section']//ul/li/div");
			Element.click(testConfig, dropArrow, "Down arrow for floor plan");
			Browser.wait(testConfig, 1);

			for (int i = 0; i < allFloorPlans.size(); i++) {
				Element.click(testConfig, dropArrow, "Down arrow for floor plan");
				Browser.wait(testConfig, 1);

				allFloorPlans = Element.getListOfElements(testConfig, How.xPath,
						".//section[@id='floor-plan-section']//ul/li/div");
				Element.click(testConfig, allFloorPlans.get(i), "Option: " + (i + 1) + " in the dropdwon");
				Browser.wait(testConfig, 2);

				WebElement largeImage = Element.getPageElement(testConfig, How.xPath,
						".//section[@id='floor-plan-section']//img");
				String imageSrc = Element.getAttribute(testConfig, largeImage, "src", "Large image view");
				verifyURLAsPerDomain(imageSrc);

			}
		}
	}

	/*
	 * public void getBase64UserNamePwdNetworkTab() {
	 * 
	 * String username = testConfig.getRunTimeProperty("Username"); String password
	 * = testConfig.getRunTimeProperty("Password");
	 * 
	 * if(!testConfig.getRunTimeProperty("Environment").equals("BRPProd")) {
	 * DevTools dev = ((ChromeDriver) testConfig.driver).getDevTools();
	 * dev.createSession(); dev.send(Network.enable(Optional.<Integer>empty(),
	 * Optional.<Integer>empty(), Optional.<Integer>empty())); Map<String, Object>
	 * map = new HashMap<>(); new com.sun.xml.messaging.saaj.util.Base64();
	 * map.put("Authorization", "Basic " + new
	 * String(com.sun.xml.messaging.saaj.util.Base64.encode((username + ":" +
	 * password).getBytes()))); dev.send(Network.setExtraHTTPHeaders(new
	 * Headers(map))); Browser.wait(testConfig, 2);
	 * //Browser.navigateToURL(testConfig, testConfig.driver.getCurrentUrl());
	 * testConfig.driver.navigate().refresh();
	 * testConfig.logComment("Refreshing the page"); } }
	 */

	public void verifyExploreCommunityGallerySection(String productType) {

		String leftNavBtnLocator = ".//section[@id='explore-community']//button[@aria-label='Previous slide']";
		String rightNavBtnLocator = ".//section[@id='explore-community']//button[@aria-label='Next slide']";
		String imageLocators = ".//section[@id='explore-community']//button[contains(@class,'flex-1')]/img";

		if(productType.equals("PlanQMI") ) {
			leftNavBtnLocator = ".//section[@id='explore-community']//button[@aria-label='Previous']";
			rightNavBtnLocator = ".//section[@id='explore-community']//button[@aria-label='Next']";
		}

		verifyLeftNavButtonIsDisabledInitially(leftNavBtnLocator);
		verifyRightNavButtonIsEnabledInitially(rightNavBtnLocator);

		List<WebElement> allImages = Element.getListOfElements(testConfig, How.xPath, imageLocators);
		for (int i = 0; i < allImages.size() / 3; i++) {
			WebElement rightNavBtn = Element.getPageElement(testConfig, How.xPath, rightNavBtnLocator);
			Element.click(testConfig, rightNavBtn, "Right navigation button");
			Browser.wait(testConfig, 1);
		}

		verifyLeftNavButtonIsEnabledNow(leftNavBtnLocator);
		verifyRightNavButtonIsDisabledNow(rightNavBtnLocator);
	}

	private void verifyRightNavButtonIsDisabledNow(String rightNavBtnLocator) {
		try {
			WebElement rightNavBtn = Element.getPageElement(testConfig, How.xPath, rightNavBtnLocator);
			if (!rightNavBtn.isEnabled()) {
				testConfig.logPass("Verified right navigation button for Gallery is disabled now");
			} else {
				testConfig.logFail("Failed to verify that right navigation button for Gallery is disabled now");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that right navigation button for Gallery is disabled now");
		}
	}

	private void verifyLeftNavButtonIsEnabledNow(String leftNavBtnLocator) {

		try {
			WebElement leftNavBtn = Element.getPageElement(testConfig, How.xPath, leftNavBtnLocator);
			if (leftNavBtn.isEnabled()) {
				testConfig.logPass("Verified left navigation button for Gallery is enabled now");
			} else {
				testConfig.logFail("Failed to verify that left navigation button for Gallery is enabled now");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that left navigation button for Gallery is enabled now");
		}
	}

	private void verifyRightNavButtonIsEnabledInitially(String rightNavBtnLocator) {

		try {
			WebElement rightNavBtn = Element.getPageElement(testConfig, How.xPath, rightNavBtnLocator);
			if (rightNavBtn.isEnabled()) {
				testConfig.logPass("Verified right navigation button for Gallery is enabled initially");
			} else {
				testConfig.logFail("Failed to verify that right navigation button for Gallery is enabled initially");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that right navigation button for Gallery is enabled initially");
		}
	}

	private void verifyLeftNavButtonIsDisabledInitially(String leftNavBtnLocator) {

		try {
			WebElement leftNavBtn = Element.getPageElement(testConfig, How.xPath, leftNavBtnLocator);
			if (!leftNavBtn.isEnabled()) {
				testConfig.logPass("Verified left navigation button for Gallery is disabled initially");
			} else {
				testConfig.logFail("Failed to verify that left navigation button for Gallery is disabled initially");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that left navigation button for Gallery is disabled initially");
		}

	}

	public void verifyClickingViewGalleryOpenModal() {

		String viewGalleryLocators = ".//section[@id='explore-community']//button[contains(@class,'btn light')]";
		WebElement viewGalleryBtn = Element.getPageElement(testConfig, How.xPath, viewGalleryLocators);
		Element.clickThroughJS(testConfig, viewGalleryBtn, "View Full Gallery button");
		Browser.wait(testConfig, 2);

		try {
			WebElement overlayModalScreen = Element.getPageElement(testConfig, How.xPath,
					".//div[@role='dialog'][contains(@class,'h-screen')]");
			if (overlayModalScreen.isDisplayed()) {
				testConfig.logPass("Verified getting image to be displaying in the modal screen");
			} else {
				testConfig.logFail("Failed to verify that image is displaying in the modal screen");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that image is displaying in the modal screen");
		}
	}

	public void verifyImageCountAndNavigationInModal() {

		String gridImageInLeft = "div.gal-mix-blend-multiply";
		List<WebElement> allImageGrid = Element.getListOfElements(testConfig, How.css, gridImageInLeft);

		String leftNavLocator = ".//div[contains(@class,'gallery-carousel')]//div/button[contains(@class,'splide__arrow--prev')]";
		String rightNavLocator = ".//div[contains(@class,'gallery-carousel')]//div/button[contains(@class,'splide__arrow--next')]";

		if (allImageGrid.size() > 1) {
			for (int i = 0; i < allImageGrid.size(); i++) {
				List<WebElement> rightNav = Element.getListOfElements(testConfig, How.xPath, rightNavLocator);
				WebElement rightNavBtn = rightNav.get(rightNav.size() - 1);
				verifyNavButtonsAreEnabled(leftNavLocator, rightNavLocator);
				Element.click(testConfig, rightNavBtn, "Right Nav button");
				Browser.wait(testConfig, 2);
			}
		} else {
			verifyNavButtonsAreDisabled(leftNavLocator, rightNavLocator);
		}
	}

	private void verifyNavButtonsAreDisabled(String leftNavLocator, String rightNavLocator) {

		try {
			List<WebElement> rightNav = Element.getListOfElements(testConfig, How.xPath, rightNavLocator);
			WebElement rightNavBtn = rightNav.get(rightNav.size() - 1);
			if (!rightNavBtn.isDisplayed()) {
				testConfig.logPass("Verified right navigation button for Gallery is not displaying");
			} else {
				testConfig.logFail("Failed to verify that right navigation button for Gallery is not displaying");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified right navigation button for Gallery is not displaying");
		}

		try {
			List<WebElement> leftNav = Element.getListOfElements(testConfig, How.xPath, leftNavLocator);
			WebElement leftNavBtn = leftNav.get(leftNav.size() - 1);
			if (!leftNavBtn.isDisplayed()) {
				testConfig.logPass("Verified left navigation button for Gallery is not displaying");
			} else {
				testConfig.logFail("Failed to verify that left navigation button for Gallery is not displaying");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified left navigation button for Gallery is not displaying");
		}
	}

	private void verifyModalDisplayingFirstItemAgain() {

		List<WebElement> allGridImages = Element.getListOfElements(testConfig, How.css, "div.gal-grid button");
		if (allGridImages.get(0).getAttribute("class").contains("border-brp-blue-1000")) {
			testConfig.logPass("Verified that the first image in grid is highlighted with blue border correctly");
		} else {
			testConfig
			.logFail("Failed to verify that the first image in grid is highlighted with blue border correctly");
		}

	}

	private void verifyNavButtonsAreEnabled(String leftNavLocator, String rightNavLocator) {

		try {
			List<WebElement> rightNav = Element.getListOfElements(testConfig, How.xPath, rightNavLocator);
			WebElement rightNavBtn = rightNav.get(rightNav.size() - 1);
			if (rightNavBtn.isEnabled()) {
				testConfig.logPass("Verified right navigation button for Gallery is enabled");
			} else {
				testConfig.logFail("Failed to verify that right navigation button for Gallery is enabled");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that right navigation button for Gallery is enabled");
		}

		try {
			List<WebElement> leftNav = Element.getListOfElements(testConfig, How.xPath, leftNavLocator);
			WebElement leftNavBtn = leftNav.get(leftNav.size() - 1);
			if (leftNavBtn.isEnabled()) {
				testConfig.logPass("Verified left navigation button for Gallery is enabled");
			} else {
				testConfig.logFail("Failed to verify that left navigation button for Gallery is enabled");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that left navigation button for Gallery is enabled");
		}
	}

	public void verifyCloseButtonClosesModal() {

		String closeBtnLocators = ".//div[contains(@class,'gal-absolute')]/button[contains(@class,'contained md btn')]";
		WebElement closeBtn = Element.getPageElement(testConfig, How.xPath, closeBtnLocators);
		Element.click(testConfig, closeBtn, "Close button to close the gallery modal");
		Browser.wait(testConfig, 2);

		try {
			WebElement overlayModalScreen = Element.getPageElement(testConfig, How.xPath,
					".//div[@role='dialog'][contains(@class,'h-screen')]");
			if (!overlayModalScreen.isDisplayed()) {
				testConfig.logPass("Verified gallery modal is not visible now after clicking close button");
			} else {
				testConfig
				.logFail("Failed to verify that gallery modal is not visible now after clicking close button");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified gallery modal is not visible now after clicking close button");
		}
	}

	public void verifyBreadcrumbDisplaying(List<String> expectedBreadcrumbs) {

		Browser.waitWithoutLogging(testConfig, 2);

		String allBreadcrumbsLocator = "(.//div[contains(@class,'md:breadcrumbText')])[2]//a";
		List<WebElement> allBreadcrumbs = Element.getListOfElements(testConfig, How.xPath, allBreadcrumbsLocator);

		if (allBreadcrumbs.size() == 0) {
			allBreadcrumbsLocator = "(.//div[contains(@class,'md:breadcrumbText')])[1]//a";

			allBreadcrumbs = Element.getListOfElements(testConfig, How.xPath, allBreadcrumbsLocator);
		}
		for (int i = 0; i < expectedBreadcrumbs.size(); i++) {
			Helper.compareEquals(testConfig, "Breadcrumb item " + (i + 1), expectedBreadcrumbs.get(i).toUpperCase(),
					allBreadcrumbs.get(i).getText().toUpperCase());

		}
		Browser.wait(testConfig, 2);

		for (int i = 0; i < allBreadcrumbs.size() - 1; i++) {
			String link = allBreadcrumbs.get(i).getAttribute("href");
			try {
				verifyURLStatus(link);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	public void verifyPlanQMIDetailsSection(String[] differentPlanDetails) {

		String[] itemValues = { "Square Footage", "Bedrooms", "Bathrooms" };
		String sectionSVGLocator = ".//div[contains(@class,'phps-text-xl')][contains(@class,'phps-text-sm')]//*[name()='svg']";
		List<WebElement> allSVG = Element.getListOfElements(testConfig, How.xPath, sectionSVGLocator);

		for (int i = 0; i < allSVG.size(); i++) {
			if (allSVG.get(i).isDisplayed()) {
				testConfig.logPass("Verify getting SVG displaying for " + differentPlanDetails[i]);
			} else {
				testConfig.logFail("SVG not displaying for " + differentPlanDetails[i]);
			}
		}

		String planDetailsLoc = ".//div[contains(@class,'phps-text-xl')][contains(@class,'phps-text-sm')]//*[name()='svg']/../span[1]";
		String homeTypeLoc = ".//div[contains(@class,'phps-text-xl')][contains(@class,'phps-text-sm')]//*[name()='svg']/../div[contains(@class,'phps-mr-3')]";

		WebElement homeType = Element.getPageElement(testConfig, How.xPath, homeTypeLoc);
		List<WebElement> itemDetails = Element.getListOfElements(testConfig, How.xPath, planDetailsLoc);

		String homeTypeVal = Element.getText(testConfig, homeType, "Home Type for Plan/QMI item");
		if (!homeTypeVal.isEmpty()) {
			testConfig.logPass("Getting Plan Home Type as " + homeTypeVal);
		} else {
			testConfig.logFail("Getting Plan Home Type value as blank... failing the scenario");
		}

		for (int i = 0; i < itemDetails.size(); i++) {
			String value = Element.getText(testConfig, itemDetails.get(i), itemValues[i] + " values");
			if (!value.isEmpty()) {
				testConfig.logPass("Getting " + itemValues[i] + " as " + value);
			} else {
				testConfig.logFail("Getting " + itemValues[i] + " value as blank... failing the scenario");
			}
		}
	}

	public void verifyTooltipText() {

		String toolTipIcon = ".//div[contains(@class,'cursor-pointer')]//*[name()='svg'][contains(@class,'tooltip')]";
		WebElement toolTip = Element.getPageElement(testConfig, How.xPath, toolTipIcon);
		Element.click(testConfig, toolTip, "Bath tooltip");
		Browser.wait(testConfig, 2);

		String textLocator = "(.//div[contains(@class,'tip-panel-lib')])[1]";
		WebElement bathToolTipText = Element.getPageElement(testConfig, How.xPath, textLocator);

		String value = Element.getText(testConfig, bathToolTipText, "Bath tooltip text");
		if (!value.isEmpty()) {
			testConfig.logPass("Getting bath tooltip text as " + value);
		} else {
			testConfig.logFail("Getting bath tooltip text value as blank... failing the scenario");
		}
	}

	public void verifyNavigationForVirtualTourItemsInGalleryModal() {

		String cardsLocator = ".//section[@id='virtual-tours']//div[contains(@class,'flex-col')]";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, cardsLocator);
		Browser.wait(testConfig, 2);

		Element.click(testConfig, allCards.get(0), "1st item in virtual tour gallery");
		Browser.wait(testConfig, 2);

		String gridImageInLeft = "div.gal-grid button";
		String imagesInModal = ".//div[contains(@class,'gallery-carousel')]//li[contains(@class,'splide__slide')][not(contains(@class,'clone'))]//img";

		List<WebElement> allImageGrid = Element.getListOfElements(testConfig, How.css, gridImageInLeft);
		List<WebElement> allModalImages = Element.getListOfElements(testConfig, How.xPath, imagesInModal);

		for (int i = 0; i < allModalImages.size(); i++) {
			String virtualTourLink = Element.getAttribute(testConfig, allModalImages.get(i), "src",
					"Virtual tour item : " + i);
			try {
				verifyURLStatus(virtualTourLink, testConfig);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		Helper.compareEquals(testConfig, "Images in grid view matches with images in modal view", allImageGrid.size(),
				allModalImages.size());

		if (allImageGrid.size() > 1) {
			String leftNavLocator = ".//div[@role='dialog'][contains(@class,'h-screen')]//button[contains(@aria-label,'slide')][1]";
			String rightNavLocator = ".//div[@role='dialog'][contains(@class,'h-screen')]//button[contains(@aria-label,'slide')][2]";
			WebElement rightNavBtn = Element.getPageElement(testConfig, How.xPath, rightNavLocator);

			for (int i = 0; i < allImageGrid.size(); i++) {
				verifyNavButtonsAreEnabled(leftNavLocator, rightNavLocator);
				Element.click(testConfig, rightNavBtn, "Right Nav button");
				Browser.wait(testConfig, 2);
			}

			verifyModalDisplayingFirstItemAgain();
		} else {
			verifyBtnsNotDisplaying();
		}
	}

	private void verifyBtnsNotDisplaying() {

		Browser.wait(testConfig, 4);
		String leftNavLocator = ".//div[@role='dialog'][contains(@class,'h-screen')]//button[contains(@class,'left-0')]";
		String rightNavLocator = ".//div[@role='dialog'][contains(@class,'h-screen')]//button[contains(@class,'right-0 z-20')]";
		try {
			WebElement leftNavBtn = Element.getPageElement(testConfig, How.xPath, leftNavLocator);
			if (leftNavBtn.isDisplayed()) {
				testConfig.logFail(
						"Left navigation button is still displaying when only 1 item is there... failing the test");
			} else {
				testConfig.logPass("Left navigation button is not displaying when only 1 item is there");
			}
		} catch (Exception e) {
			testConfig.logPass("Left navigation button is not displaying when only 1 item is there");
		}

		try {
			WebElement rightNavBtn = Element.getPageElement(testConfig, How.xPath, rightNavLocator);
			if (rightNavBtn.isDisplayed()) {
				testConfig.logFail(
						"Right navigation button is still displaying when only 1 item is there... failing the test");
			} else {
				testConfig.logPass("Right navigation button is not displaying when only 1 item is there");
			}
		} catch (Exception e) {
			testConfig.logPass("Right navigation button is not displaying when only 1 item is there");
		}

	}

	public void verifyPricingBreakdownLinkModal() {

		String hoaFeesOnSection = "";
		String priceValueLoc = "(.//div[contains(@class,'items-end')]//div[contains(text(),'Priced')]/../div[2]/div)[1]";
		WebElement priceValue = Element.getPageElement(testConfig, How.xPath, priceValueLoc);
		String pricingOnSection = Element.getText(testConfig, priceValue, "Price Value");

		try {
			String hoaValueLoc = ".//div[contains(@class,'items-end')]//span[text()='HOA:']/../span[2]";
			WebElement hoaValue = Element.getPageElement(testConfig, How.xPath, hoaValueLoc);
			hoaFeesOnSection = Element.getText(testConfig, hoaValue, "HOA Condo Fee Value").split("/")[0];
		} catch (Exception e) {
			testConfig.logComment(
					"***************** HOA/Condo Fees section missing.. skipping the verification *****************");
		}

		// edited to below link - String pricingBreakdownLinkLoc =
		// "(.//div[contains(@class,'items-end')]//div[contains(@class,'text-brp-blue-800')]/button)[1]";
		String pricingBreakdownLinkLoc = "(.//div[contains(@class,' prc-text-brds-v1-cta-link-teal-700')]//button[contains(@class,'link primary-underlined md')][1]/span)[1]";
		WebElement pricingBreakdownElement = Element.getPageElement(testConfig, How.xPath, pricingBreakdownLinkLoc);

		Browser.wait(testConfig, 1);
		Element.clickThroughJS(testConfig, pricingBreakdownElement, "Pricing breakdown link");

		String modalLocator = ".//h2[contains(text(),'Price Breakdown')]/ancestor::div[contains(@class,'prc-overflow-y-auto')]";
		WebElement modalPricingBreakdown = Element.getPageElement(testConfig, How.xPath, modalLocator);
		Browser.wait(testConfig, 4);

		if (modalPricingBreakdown.isDisplayed()) {
			testConfig.logPass("Verified pricing breakdown modal appears when clicking on the link");
		} else {
			testConfig.logFail("Failed to verify pricing breakdown modal appears when clicking on the link");
		}

		String priceOverModalLoc = "(.//span[contains(text(),'Purchase Price')]/ancestor::div[contains(@class,'prc-grid')]//span[contains(text(),'$')])[1]";
		WebElement priceOverModal = Element.getPageElement(testConfig, How.xPath, priceOverModalLoc);
		String priceValueOverModal = Element.getText(testConfig, priceOverModal, "Price displaying over Modal");

		Helper.compareEquals(testConfig, "Price displaying over Page with modal", pricingOnSection,
				priceValueOverModal);

		try {
			String hoaPriceOverModalLoc = ".//span[contains(text(),'HOA/Condo')]/ancestor::div[contains(@class,'prc-grid')]//span[contains(text(),'$')]";
			WebElement hoaPriceOverModal = Element.getPageElement(testConfig, How.xPath, hoaPriceOverModalLoc);
			String hoaPriceValueOverModal = Element.getText(testConfig, hoaPriceOverModal,
					"HOA/Condo Fees displaying over Modal");
			Helper.compareEquals(testConfig, "HOA/Condo Fees displaying over Page with modal", hoaFeesOnSection,
					hoaPriceValueOverModal);
		} catch (Exception e) {
			testConfig.logComment(
					"***************** HOA/Condo Fees section missing.. skipping the verification *****************");
		}

		String closeButton = ".//button[contains(@class,'prc-absolute')][@aria-label='Close']";
		WebElement closeBtn = Element.getPageElement(testConfig, How.xPath, closeButton);
		Element.click(testConfig, closeBtn, "Close button over modal");
		Browser.wait(testConfig, 2);

		try {
			modalPricingBreakdown = Element.getPageElement(testConfig, How.xPath, modalLocator);
			if (!modalPricingBreakdown.isDisplayed()) {
				testConfig.logPass("Verified pricing breakdown modal closes when clicking on the close button");
			} else {
				testConfig.logFail("Failed to verify pricing breakdown modal closes when clicking on the close button");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified pricing breakdown modal closes when clicking on the close button");
		}

	}

	public void verifyTooltipContentOverModal(PricingBreakdownSection pricingBreakdownSection) {

		String pricingBreakdownLinkLoc = "(.//div[contains(@class,' prc-text-brds-v1-cta-link-teal-700')]//button[contains(@class,'link primary-underlined md')][1]/span)[1]";
		WebElement pricingBreakdownElement = Element.getPageElement(testConfig, How.xPath, pricingBreakdownLinkLoc);
		Browser.wait(testConfig, 1);
		Element.clickThroughJS(testConfig, pricingBreakdownElement, "Pricing breakdown link");

		String locator = "", tooltipContentLocator = "", content = "";
		WebElement buttonForTooltip = null, contentLocator = null;

		Browser.wait(testConfig, 2);
		switch (pricingBreakdownSection) {
		case BasePrice:
			locator = ".//span[contains(text(),'Base Price')]/..//*[name()='svg']";
			tooltipContentLocator = ".//span[contains(text(),'Base Price')]/..//div[@role='tooltip']";
			break;

		case HOACondo:
			locator = ".//span[contains(text(),'HOA/Condo')]/..//*[name()='svg']";
			tooltipContentLocator = ".//span[contains(text(),'HOA/Condo')]/..//div[@role='tooltip']";
			break;

		case LotPremium:
			locator = ".//span[contains(text(),'Lot Premium')]/..//*[name()='svg']";
			tooltipContentLocator = ".//span[contains(text(),'Lot Premium')]/..//div[@role='tooltip']";
			break;

		case OptionsIncluded:
			locator = "(.//span[contains(text(),'Options')]/..//*[name()='svg'])[1]";
			tooltipContentLocator = ".//span[contains(text(),'Options')]/..//div[@role='tooltip']";
			break;
		}

		try {
			buttonForTooltip = Element.getPageElement(testConfig, How.xPath, locator);
			Element.click(testConfig, buttonForTooltip, pricingBreakdownSection.toString() + " tooltip");

			Browser.wait(testConfig, 1);
			contentLocator = Element.getPageElement(testConfig, How.xPath, tooltipContentLocator);
			content = Element.getText(testConfig, contentLocator, pricingBreakdownSection.toString() + " tooltip text");
			Browser.wait(testConfig, 1);

			if (!content.isEmpty()) {
				testConfig.logPass("Getting " + pricingBreakdownSection.toString() + " tooltip text as " + content);
			} else {
				testConfig.logFail("Getting " + pricingBreakdownSection.toString()
				+ " tooltip text value as blank... failing the scenario");
			}
		} catch (Exception e) {
			testConfig.logComment(pricingBreakdownLinkLoc.toString() + " field is not displaying.. skipping verification for the field");
		}


		String closeButton = ".//button[contains(@class,'prc-rounded-full')]";
		WebElement closeBtn = Element.getPageElement(testConfig, How.xPath, closeButton);
		Element.click(testConfig, closeBtn, "Close button over modal");
		Browser.waitWithoutLogging(testConfig, 2);

	}

	public void verifyAllSVGOverPricingBreakdownModal() {

		String pricingBreakdownLinkLoc = "(.//div[contains(@class,' prc-text-brds-v1-cta-link-teal-700')]//button[contains(@class,'link primary-underlined md')][1]/span)[1]";
		WebElement pricingBreakdownElement = Element.getPageElement(testConfig, How.xPath, pricingBreakdownLinkLoc);
		Browser.wait(testConfig, 4);
		Element.clickThroughJS(testConfig, pricingBreakdownElement, "Pricing breakdown link");

		String svgsLocator = ".//div[contains(@class,'prc-overflow-y-auto')]//span//*[name()='svg'][contains(@class,'prc-text')]";
		List<WebElement> allSVGElements = Element.getListOfElements(testConfig, How.xPath, svgsLocator);

		String svgsLabel = ".//div[contains(@class,'prc-overflow-y-auto')]//span//*[name()='svg'][contains(@class,'prc-text')]/../../span";
		List<WebElement> allSVGElementsLabel = Element.getListOfElements(testConfig, How.xPath, svgsLabel);

		for (int i = 0; i < allSVGElements.size(); i++) {
			int j = i;
			if (i >= 2) {
				j = i - 1;
			}
			if (allSVGElements.get(i).isDisplayed()) {
				testConfig.logPass("Verify getting SVG displaying for "
						+ allSVGElementsLabel.get(j).getAttribute("innerText").trim());
			} else {
				testConfig.logFail(
						"SVG not displaying for " + allSVGElementsLabel.get(j).getAttribute("innerText").trim());
			}
		}
		Browser.wait(testConfig, 2);

		String closeButton = ".//button[contains(@class,'prc-rounded-full')]";
		WebElement closeBtn = Element.getPageElement(testConfig, How.xPath, closeButton);
		Element.click(testConfig, closeBtn, "Close button over modal");
		Browser.wait(testConfig, 2);
	}

	public void verifyEstMonthlyMortgagePayment(String mortgageTerm) {

		String pricingBreakdownLinkLoc = ".//div[contains(@class,'items-end')]//div[contains(@class,'prc-justify-between')]/button[1]"; // updated
		// class
		// name
		WebElement pricingBreakdownElement = Element.getPageElement(testConfig, How.xPath, pricingBreakdownLinkLoc);
		Element.clickThroughJS(testConfig, pricingBreakdownElement, "Pricing breakdown link");
		Browser.wait(testConfig, 1);

		String totalPriceLocator = ".//span[contains(text(),'Purchase Price')]/ancestor::div[contains(@class,'prc-grid')]//span[contains(text(),'$')]";
		WebElement totalPriceElement = Element.getPageElement(testConfig, How.xPath, totalPriceLocator);
		String purchasePrice = Element.getText(testConfig, totalPriceElement, "Purchase price from").replace("$", "")
				.replace(",", "");

		WebElement mortgageTermsDropdown = Element.getPageElement(testConfig, How.xPath,
				".//h3[contains(text(),'Mortgage Term')]/../div");
		Element.click(testConfig, mortgageTermsDropdown, "Mortgage terms dropdown");
		Browser.wait(testConfig, 2);

		List<WebElement> allYrOptions = Element.getListOfElements(testConfig, How.xPath,
				".//ul[@role='listbox']/li/span");
		for (Iterator<WebElement> iterator = allYrOptions.iterator(); iterator.hasNext();) {
			WebElement webElement = (WebElement) iterator.next();
			String value = Element.getText(testConfig, webElement, "Year dropdown value");
			if (value.equals(mortgageTerm)) {
				Element.click(testConfig, webElement, mortgageTerm + " mortgage term");
				break;
			}
		}

		WebElement interestRateInput = Element.getPageElement(testConfig, How.xPath,
				".//h3[contains(text(),'Interest Rate')]/..//input");
		Element.clear(testConfig, interestRateInput, "Interest rate field");
		Element.enterData(testConfig, interestRateInput, "7.5", "Interest Rate field");
		Browser.wait(testConfig, 2);

		WebElement downPaymentInput = Element.getPageElement(testConfig, How.xPath,
				".//h3[contains(text(),'Down Payment')]/..//input");
		Element.clear(testConfig, downPaymentInput, "Down Payment field");
		Element.enterData(testConfig, downPaymentInput, "21", "Down Payment field");
		Browser.wait(testConfig, 2);

		WebElement interestRateElement = Element.getPageElement(testConfig, How.xPath,
				".//h3[contains(text(),'Interest Rate')]/../div/span/span");
		String interestRateValue = Element.getText(testConfig, interestRateElement, "Interest Rate");

		WebElement downPaymentElement = Element.getPageElement(testConfig, How.xPath,
				".//h3[contains(text(),'Down Payment')]/../div/span/span");
		String downPaymentValue = Element.getText(testConfig, downPaymentElement, "Down Payment");

		int timeMonth = Integer.parseInt(mortgageTerm.split(" ")[0]);
		double interestRate = Double.parseDouble(interestRateValue);
		double price = Double.parseDouble(purchasePrice);
		double downPayment = price * Double.parseDouble(downPaymentValue) / 100;
		double rate = (interestRate / 100) / 12;
		double principalAmount = price - downPayment;
		int timeInterval = timeMonth * 12;

		// int finalMonthlyPaymentValue = (int) ((rate * principalAmount) / (1 -
		// Math.pow((1 + rate), -timeInterval)));
		double finalMonthlyPaymentValue = (principalAmount
				* ((rate * Math.pow(1 + rate, timeInterval)) / (Math.pow(1 + rate, timeInterval) - 1)));

		DecimalFormat formatter = new DecimalFormat("#,###");
		String formattedFinalPrice = "$" + formatter.format(finalMonthlyPaymentValue);

		String monthlyPaymentLoc = "(.//span[contains(text(),'Purchase Price')]/ancestor::div[contains(@class,'prc-grid')]//span[contains(text(),'$')])[2]";
		WebElement monthlyPayment = Element.getPageElement(testConfig, How.xPath, monthlyPaymentLoc);
		String valueOverModal = Element.getText(testConfig, monthlyPayment, "Monthly Payment value over modal");

		Helper.compareEquals(testConfig, "Estimated monthly payment for Mortgage", formattedFinalPrice, valueOverModal);
	}

	public void verifyClickingGetPrequalifiedButton() {

		String pricingBreakdownLinkLoc = "(.//div[contains(@class,' prc-text-brds-v1-cta-link-teal-700')]//button[contains(@class,'link primary-underlined md')][1]/span)[1]";
		WebElement pricingBreakdownElement = Element.getPageElement(testConfig, How.xPath, pricingBreakdownLinkLoc);
		Browser.wait(testConfig, 1);
		Element.clickThroughJS(testConfig, pricingBreakdownElement, "Pricing breakdown link");

		WebElement getPreQualifiedBtn = Element.getPageElement(testConfig, How.xPath,
				".//button[contains(@class,'btn secondary sm')]");
		Element.click(testConfig, getPreQualifiedBtn, "Get Pre-Qualified Link");

		Browser.wait(testConfig, 2);

		String modalLocator = ".//div[contains(@class,'modal-content')]//h4[contains(text(),'BRP Home Mortgage')]";

		try {
			WebElement modalForm = Element.getPageElement(testConfig, How.xPath, modalLocator);
			if (modalForm.isDisplayed()) {
				testConfig.logPass("Clicking 'Get Pre-Qualified' button opens modal successfully");
			} else {
				testConfig.logFail(
						"Failed to verify that on clicking 'Get Pre-Qualified'' button opens modal successfully");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that on clicking 'Get Pre-Qualified' button opens modal successfully");
		}

	}

	public void verifyExpandButtonFunctionality() {

		String expandLocator = ".//section[@id='floor-plan-section']//div[not(contains(@class,'xl:hidden'))]/button[contains(@class,'btn light')]";
		WebElement expandBtn = Element.getPageElement(testConfig, How.xPath, expandLocator);
		Element.click(testConfig, expandBtn, "Expand button");
		Browser.wait(testConfig, 2);

		String modalLocator = ".//button[@aria-label='Close']/parent::div[contains(@class,'modal-content')]";
		try {
			WebElement expandFloorModal = Element.getPageElement(testConfig, How.xPath, modalLocator);
			if (expandFloorModal.isDisplayed()) {
				testConfig.logPass("Verified that clicking Expand button opens Expanded floor plan modal successfully");
			} else {
				testConfig.logFail(
						"Failed to verify that clicking Expand button opens Expanded floor plan modal successfully");
			}
		} catch (Exception e) {
			testConfig.logFail("Verified that clicking Expand button opens Expanded floor plan modal successfully");
		}

	}

	public void verifyFloorPlanZoomInOut(String productPage) {

		if(productPage.equals("Plan")){
			Browser.wait(testConfig, 5);
			String floorPlanLocator = ".//button/h2[contains(text(),'Floor plan')]";
			WebElement floorPlanTabGallery = Element.getPageElement(testConfig, How.xPath, floorPlanLocator);
			Element.click(testConfig, floorPlanTabGallery, "Floor Plan tab over View Full Gallery page");
			Browser.wait(testConfig, 3);
		}

		String zoomInButtonLocator = ".//div[contains(@class,'right-22')]/button[1]";
		String zoomOutButtonLocator = ".//div[contains(@class,'right-22')]/button[2]";

		WebElement zoomInBtn = Element.getPageElement(testConfig, How.xPath, zoomInButtonLocator);
		WebElement zoomOutBtn = Element.getPageElement(testConfig, How.xPath, zoomOutButtonLocator);

		Element.click(testConfig, zoomInBtn, "Zoom in button");
		Browser.wait(testConfig, 2);

		verifyZoomInOutButtonDisable(zoomInButtonLocator);

		Element.click(testConfig, zoomOutBtn, "Zoom out button");
		Browser.wait(testConfig, 2);

		verifyZoomInOutButtonDisable(zoomOutButtonLocator);
	}

	/*
	 * public void verifyFloorPlanZoomInOut(String productPage) {
	 * 
	 * if (productPage.equals("Plan")) { Browser.wait(testConfig, 5); String
	 * floorPlanLocator = ".//button/h2[contains(text(),'Floor plan')]"; WebElement
	 * floorPlanTabGallery = Element.getPageElement(testConfig, How.xPath,
	 * floorPlanLocator); Element.click(testConfig, floorPlanTabGallery,
	 * "Floor Plan tab over View Full Gallery page"); Browser.wait(testConfig, 3); }
	 * 
	 * String floorPlanFullViewLocator = ".//div[@aria-roledescription='carousel']";
	 * String zoomInButtonLocator = ".//div[contains(@class,'right-22')]/button[1]";
	 * String zoomOutButtonLocator =
	 * ".//div[contains(@class,'right-22')]/button[2]";
	 * 
	 * WebElement floorPlanView = Element.getPageElement(testConfig, How.xPath,
	 * floorPlanFullViewLocator); String classNameFullView =
	 * Element.getAttribute(testConfig, floorPlanView, "class",
	 * "Floor plan in full view"); if
	 * (classNameFullView.contains("splide--draggable")) { testConfig.
	 * logPass("Getting dragging class displaying initially means the floor plan is draggable"
	 * ); } else { testConfig.
	 * logFail("Not getting dragging class displaying initially which should not happen"
	 * ); }
	 * 
	 * WebElement zoomInBtn = Element.getPageElement(testConfig, How.xPath,
	 * zoomInButtonLocator); WebElement zoomOutBtn =
	 * Element.getPageElement(testConfig, How.xPath, zoomOutButtonLocator);
	 * 
	 * Element.click(testConfig, zoomInBtn, "Zoom in button");
	 * Browser.wait(testConfig, 2);
	 * 
	 * String floorPlanContainerLocator =
	 * ".//li[contains(@class,'splide__slide is-active')][not(contains(@class,'clone'))]//div[contains(@class,'gal-cursor-all-scroll')]"
	 * ; WebElement floorPlanContainer = Element.getPageElement(testConfig,
	 * How.xPath, floorPlanContainerLocator); String fullViewStyle =
	 * Element.getAttribute(testConfig, floorPlanContainer, "style",
	 * "Floor plan in full view");
	 * 
	 * if (fullViewStyle.contains("scale(2)")) { testConfig.
	 * logPass("Getting scale property incremented when zoomed into the floor plan"
	 * ); } else { testConfig.
	 * logFail("Unable to see scale property incremented when zoomed into the floor plan"
	 * ); }
	 * 
	 * verifyZoomInOutButtonDisable(zoomInButtonLocator);
	 * 
	 * Element.click(testConfig, zoomOutBtn, "Zoom out button");
	 * Browser.wait(testConfig, 2);
	 * 
	 * floorPlanContainer = Element.getPageElement(testConfig, How.xPath,
	 * floorPlanContainerLocator); fullViewStyle = Element.getAttribute(testConfig,
	 * floorPlanContainer, "style", "Floor plan in full view");
	 * 
	 * if (fullViewStyle.contains("scale(1)")) { testConfig.
	 * logPass("Getting scale property decremented when zoomed out from the floor plan"
	 * ); } else { testConfig.
	 * logFail("Unable to see scale property decremented when zoomed out from the floor plan"
	 * ); }
	 * 
	 * verifyZoomInOutButtonDisable(zoomOutButtonLocator); }
	 */

	private void verifyZoomInOutButtonDisable(String zoomInButtonLocator) {

		WebElement zoomInOutBtn = Element.getPageElement(testConfig, How.xPath, zoomInButtonLocator);
		String disableZoomInOutBtn = Element.getAttribute(testConfig, zoomInOutBtn, "disable", "Zoom in/out button");

		if (disableZoomInOutBtn.equals("true")) {
			testConfig.logPass("Verified zoom in/out button is disabled after clicked");
		} else {
			testConfig.logFail("Unable to verify that zoom in/out button is disabled after clicked");
		}
	}

	public CommunityPage verifyLearnMoreLinkOverExploreCommunitySection() {

		String locator = ".//p[text()='Community:']/../a";
		WebElement communityName = Element.getPageElement(testConfig, How.xPath, locator);
		testConfig.putRunTimeProperty("CommunityName", communityName.getText());

		String learnMoreLocator = ".//section[@id='explore-community']//a[contains(@href,'new-homes')]";
		WebElement communityLink = Element.getPageElement(testConfig, How.xPath, learnMoreLocator);
		Element.click(testConfig, communityLink, "Community link");
		
		return new CommunityPage(testConfig);
	}

	public FYHPage verifyDetailsAndClickViewCommunityMap(String title) {

		String titleLocator = ".//section[@id='meet-the-new-neighborhood']//div/p";
		WebElement titleOnPageElement = Element.getPageElement(testConfig, How.xPath, titleLocator);
		Helper.compareContains(testConfig, "Section title", title, titleOnPageElement.getText());

		String imageLocator = ".//section[@id='meet-the-new-neighborhood']//a[@target='_blank']";
		WebElement sitemapImage = Element.getPageElement(testConfig, How.xPath, imageLocator);

		String sitemapUrl = Element.getAttribute(testConfig, sitemapImage, "href", "Image Source");
		testConfig.logComment("Siteplan image source: " + homeurl + sitemapUrl);
		verifyURLAsPerDomain(sitemapUrl);

		String browseCommLocator = ".//section[@id='meet-the-new-neighborhood']//a[@target='_blank']";
		WebElement browseCommMap = Element.getPageElement(testConfig, How.xPath, browseCommLocator);
		Element.click(testConfig, browseCommMap, "View Community Map link");
		Set<String> windowHandle = testConfig.driver.getWindowHandles();
		testConfig.driver.switchTo().window(getLastElement(windowHandle));

		testConfig.logComment("Switching to the new window opened...");
		Helper.removeCookies(testConfig);
		testConfig.putRunTimeProperty("RedirectionValue", "yes");
		return new FYHPage(testConfig);
	}

	public String getLastElement(Set<String> set) {
		Iterator<String> itr = set.iterator();
		String lastElement = itr.next();
		while (itr.hasNext()) {
			lastElement = itr.next();
		}
		return lastElement;
	}

	public void verifyHeaderSearchModule(String[] expectedLocs) {
		
		Browser.wait(testConfig, 5);
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		js.executeScript("window.scrollTo(document.body.scrollTop,0)");
		Browser.waitWithoutLogging(testConfig, 2);
		
		String inputFieldLoc = ".//div[contains(@class,'srch-flex-grow')][contains(@class,'srch-bg-white')]/div/div";
		WebElement searchInputField = Element.getPageElement(testConfig, How.xPath, inputFieldLoc);
		Element.click(testConfig, searchInputField, "Search input field");

		Browser.wait(testConfig, 5);
		List<WebElement> allLocations = Element.getListOfElements(testConfig, How.css,
				"li.srch-items-center div.srch-flex-grow");
		Helper.compareEquals(testConfig, "Expected count of the location", expectedLocs.length, allLocations.size());

		for (int i = 0; i < expectedLocs.length; i++) {
			Helper.compareEquals(testConfig, "Location " + (i + 1) + " as ", expectedLocs[i],
					allLocations.get(i).getText());
		}
	}

	public FYHPage selectCurrentLocationFromHeaderSearch() {

		Browser.wait(testConfig, 5);
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		js.executeScript("window.scrollTo(document.body.scrollTop,0)");
		Browser.waitWithoutLogging(testConfig, 2);
		
		WebElement inputSearch = Element.getPageElement(testConfig, How.xPath,
				".//div[contains(@class,'srch-flex-grow')][contains(@class,'srch-bg-white')]/div/div");
		Element.click(testConfig, inputSearch, "Search input box");
		Browser.wait(testConfig, 5);

		WebElement currentLocation = Element.getPageElement(testConfig, How.xPath,
				".//div[contains(@class,'fyh-searchbox')]//*[@aria-labelledby='Current Location']");
		Element.click(testConfig, currentLocation, "Current location option");

		Helper.removeCookies(testConfig);
		return new FYHPage(testConfig);
	}

	public void verifyOurTeamSectionDetails(String sectionHeading, String sectionDescription) {

		WebElement title = Element.getPageElement(testConfig, How.xPath,
				".//div[contains(@class,'bg-brp-blue-150')]/div[1]");
		WebElement description = Element.getPageElement(testConfig, How.xPath,
				".//div[contains(@class,'bg-brp-blue-150')]/div[2]");

		Helper.compareEquals(testConfig, "Title for 'Our People' section over page", sectionHeading, title.getText());
		Helper.compareEquals(testConfig, "Description for 'Our People' section over page", sectionDescription,
				description.getText());
	}

	public void verifyPeopleInformationDisplaying() {

		String allPeopleTilesLocator = ".//div[contains(@class,'bg-brp-blue-150')]/div[3]/div";
		List<WebElement> allPeopleTiles = Element.getListOfElements(testConfig, How.xPath, allPeopleTilesLocator);

		if (allPeopleTiles.size() > 0) {
			testConfig.logPass("Verified getting " + allPeopleTiles.size() + " people tiles displaying in the section");
		} else {
			testConfig.logFail("Failed to verify any tile displaying for People under 'Our People' section");
		}
	}

	public String getRequiredContent(String lotStatus, String salesStatus) {

		Browser.wait(testConfig, 5);

		String filePath = System.getProperty("user.dir") + File.separator + "Parameters" + File.separator
				+ "Script.txt";

		StringBuilder builder = new StringBuilder();

		try (BufferedReader buffer = new BufferedReader(new FileReader(filePath))) {
			String str;
			while ((str = buffer.readLine()) != null) {
				builder.append(str).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		String contentForScript = builder.toString();
		String finalContent = contentForScript.replace("{LotStatus}", lotStatus);
		finalContent = finalContent.replace("{SalesStatus}", salesStatus);
		testConfig.logComment(finalContent);

		return finalContent;
	}

	public void verifyCardDetails() throws UnsupportedEncodingException {

		String allCardsLocator = ".//div[contains(@class,'brp-page-grid')]/a";
		List<WebElement> cardsVisibleInitially = Element.getListOfElements(testConfig, How.xPath, allCardsLocator);

		for (int i = 0; i < cardsVisibleInitially.size(); i++) {

			testConfig.logComment(
					"****************** Looking for Blog Card Data for card no " + (i + 1) + " ******************");

			String imageLoc = ".//div[contains(@class,'brp-page-grid')]//a//img";
			String blogHeading = ".//div[contains(@class,'brp-page-grid')]//a//h5";
			String blogDescription = ".//div[contains(@class,'brp-page-grid')]//a/div/p";

			List<WebElement> headings = Element.getListOfElements(testConfig, How.xPath, blogHeading);
			if (!headings.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass(
						"Getting Heading displaying over Blog card as " + headings.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Heading over Blog card value as blank... failing the scenario");
			}

			List<WebElement> description = Element.getListOfElements(testConfig, How.xPath, blogDescription);
			if (!description.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Description displaying over Blog card as "
						+ description.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail(
						"Getting Description displaying over Blog card value as blank... failing the scenario");
			}

			List<WebElement> allImages = Element.getListOfElements(testConfig, How.xPath, imageLoc);
			String blogImageSrc = Element.getAttribute(testConfig, allImages.get(i), "src", " Blog Card image");

			if (!blogImageSrc.isEmpty()) {
				testConfig.logPass(
						"Getting Blog Card image source displaying for Card: " + (i + 1) + " as " + blogImageSrc);
				verifyURLAsPerDomain(blogImageSrc);
			} else {
				testConfig.logFail("Getting Blog Card image source displaying as blank... failing the scenario");
			}

		}
	}

	public void verifyFormSubmission(DifferentFormTypes differentFormTypes, String leadForm) {

		WebElement scheduleATourBtn = null, requestInfoBtn = null;

		switch (differentFormTypes) {
		case OnsiteTour:
			scheduleATourBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='contact-us']//button[contains(@class,'btn primary')]");
			Element.click(testConfig, scheduleATourBtn, "Schedule A Tour button");
			Browser.waitWithoutLogging(testConfig, 2);

			WebElement onsiteTour = Element.getPageElement(testConfig, How.xPath,
					".//span[text()='Onsite Tour with our Team']");
			Element.click(testConfig, onsiteTour, "Onsite Tour with our Team form");
			break;

		case RequestInformation:
			requestInfoBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='contact-us']//button[contains(@class,'secondary lg')]");
			Element.click(testConfig, requestInfoBtn, "Request Information button");
			break;

		case VideoChat:
			scheduleATourBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='contact-us']//button[contains(@class,'btn primary')]");
			Element.click(testConfig, scheduleATourBtn, "Schedule A Tour button");

			WebElement videoChat = Element.getPageElement(testConfig, How.xPath,
					".//span[text()='Video Chat Tour with our Team']");
			Element.click(testConfig, videoChat, "Video Chat Tour with our Team form");
			break;
		}

		Browser.waitWithoutLogging(testConfig, 2);

		String daysToVisit[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
		String timeOfDay[] = { "Morning", "Afternoon", "Evening" };

		Random rd = new Random();
		String preferredDay = daysToVisit[rd.nextInt(daysToVisit.length)];
		String preferredTime = timeOfDay[rd.nextInt(timeOfDay.length)];

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		WebElement firstNameField = null, lastNameField = null, emailAddressField = null, phoneField = null,
				preferredDayToVisitField = null, preferredTimeOfDayField = null, consentCheckbox = null,
				submitBtn = null;

		switch (differentFormTypes) {
		case OnsiteTour:
			String[] additionalMsg1 = {"We have successfully received your message. "
					+ "A team member will reach out to you to confirm your tour details "
					+ "as soon as possible. Be sure to check your inbox for a confirmation email.",
			"You may close this window." };

			firstNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//input[@placeholder='Your First Name']");
			lastNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//input[@placeholder='Your Last Name']");
			emailAddressField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//input[@placeholder='email@domain.com']");
			phoneField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//input[@placeholder='(XXX) XXX-XXXX']");
			preferredDayToVisitField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//select[@data-sc-field-name='Preferred Day']");
			preferredTimeOfDayField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//select[@data-sc-field-name='Preferred Time']");

			Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
			Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
			Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");
			Element.enterData(testConfig, phoneField, String.valueOf(phoneNo), "Phone Number field value");

			Element.selectVisibleText(testConfig, preferredDayToVisitField, preferredDay,
					"Preferred Day to Visit field value");
			Element.selectVisibleText(testConfig, preferredTimeOfDayField, preferredTime,
					"Preferred Time Of Day field value");

			consentCheckbox = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//input[@data-sc-field-name='Terms and Conditions']");
			Element.click(testConfig, consentCheckbox, "Consent checkbox");

			submitBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//button[@value='Submit']");
			Element.click(testConfig, submitBtn, "Submit button");
			Browser.wait(testConfig, 2);
			verifySuccessMessageRequestInfoForm(firstName, additionalMsg1);
			createExcelFileAndWriteEmailAddress(emailAddress, leadForm);
			break;

		case RequestInformation:
			String[] additionalMsg2 = {"We have successfully received your message. "
					+ "A team member will reach out to assist you as soon as possible. "
					+ "Be sure to check your inbox for a confirmation email.",
			"You may close this window." };

			firstNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//input[@placeholder='Your First Name']");
			lastNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//input[@placeholder='Your Last Name']");
			emailAddressField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//input[@placeholder='email@domain.com']");
			phoneField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//input[@placeholder='(XXX) XXX-XXXX']");

			Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
			Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
			Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");
			Element.enterData(testConfig, phoneField, String.valueOf(phoneNo), "Phone Number field value");

			consentCheckbox = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//input[@data-sc-field-name='Terms and Conditions']");
			Element.click(testConfig, consentCheckbox, "Consent checkbox");

			submitBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//button[@value='Submit']");
			Element.click(testConfig, submitBtn, "Submit button");
			Browser.wait(testConfig, 2);
			verifySuccessMessageRequestInfoForm(firstName, additionalMsg2);
			createExcelFileAndWriteEmailAddress(emailAddress, leadForm);
			break;

		case VideoChat:
			String[] additionalMsg3 = {"We have successfully received your message. "
					+ "A team member will reach out to you to confirm your tour details "
					+ "as soon as possible. Be sure to check your inbox for a confirmation email.",
			"You may close this window." };

			firstNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//input[@placeholder='Your First Name']");
			lastNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//input[@placeholder='Your Last Name']");
			emailAddressField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//input[@placeholder='email@domain.com']");
			phoneField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//input[@placeholder='(XXX) XXX-XXXX']");
			preferredDayToVisitField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//select[@data-sc-field-name='Preferred Day']");
			preferredTimeOfDayField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//select[@data-sc-field-name='Preferred Time']");

			Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
			Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
			Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");
			Element.enterData(testConfig, phoneField, String.valueOf(phoneNo), "Phone Number field value");

			Element.selectVisibleText(testConfig, preferredDayToVisitField, preferredDay,
					"Preferred Day to Visit field value");
			Element.selectVisibleText(testConfig, preferredTimeOfDayField, preferredTime,
					"Preferred Time Of Day field value");

			consentCheckbox = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//input[@data-sc-field-name='Terms and Conditions']");
			Element.click(testConfig, consentCheckbox, "Consent checkbox");

			submitBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//button[@value='Submit']");
			Element.click(testConfig, submitBtn, "Submit button");
			Browser.wait(testConfig, 2);
			verifySuccessMessageRequestInfoForm(firstName, additionalMsg3);
			createExcelFileAndWriteEmailAddress(emailAddress, leadForm);
			break;
		}
	}

	public void verifySuccessMessageRequestInfoForm(String firstNameValue, String[] additionalMsg) {

		WebElement messageSent = Element.getPageElement(testConfig, How.xPath,
				".//span[text()='Message Sent!']");
		WebElement thankYouMsg = Element.getPageElement(testConfig, How.xPath,
				".//span[text()='Message Sent!']/../h2");
		List<WebElement> additionalContent = Element.getListOfElements(testConfig, How.xPath,
				".//div[contains(@class,'cnt-type-brds-v2-lg-300')]/p/span");

		Helper.compareEquals(testConfig, "Title over success screen", "Message Sent!",
				messageSent.getText().trim());
		Helper.compareEquals(testConfig, "Thank you message over success screen", "Thank you, " + firstNameValue + ".",
				thankYouMsg.getText().trim());

		for (int j = 0; j < additionalMsg.length; j++) {
			Helper.compareEquals(testConfig, "Additional content " + (j + 1), additionalMsg[j],
					additionalContent.get(j).getText());
		}
	}

	public void submitCommunitySectionRequestInformationForm(String leadForm, String[] additionalMsg) {

		WebElement firstNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='Your First Name']");
		WebElement lastNameField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='Your Last Name']");
		WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='email@domain.com']");
		WebElement phoneField = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@placeholder='(XXX) XXX-XXXX']");

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		JavascriptExecutor jst = (JavascriptExecutor) testConfig.driver;
		jst.executeScript("arguments[1].value = arguments[0]; ", firstName, firstNameField);
		jst.executeScript("arguments[1].value = arguments[0]; ", lastName, lastNameField);
		jst.executeScript("arguments[1].value = arguments[0]; ", emailAddress, emailAddressField);
		jst.executeScript("arguments[1].value = arguments[0]; ", String.valueOf(phoneNo), phoneField);

		Browser.wait(testConfig, 2);
		WebElement consentCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@data-sc-field-name='Terms and Conditions']");
		Element.click(testConfig, consentCheckbox, "Consent checkbox");

		WebElement agentYesCheckbox = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//input[@data-sc-field-name='Agent Opt-In'][@value='true']");
		Element.click(testConfig, agentYesCheckbox, "Agent Yes checkbox");
		Browser.waitWithoutLogging(testConfig, 2);

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath,
				".//section[@id='request-information']//button[@type='submit']");
		Element.click(testConfig, submitBtn, "Submit Button");
		Browser.wait(testConfig, 2);

		verifySuccessMessageRequestInfoFormCommunitySection(firstName, additionalMsg);
		createExcelFileAndWriteEmailAddress(emailAddress, leadForm);

	}

	public void verifySuccessMessageRequestInfoFormCommunitySection(String firstNameValue, String[] additionalMsg) {

		WebElement messageSent = Element.getPageElement(testConfig, How.xPath,
				".//span[text()='Message Sent!']");
		WebElement thankYouMsg = Element.getPageElement(testConfig, How.xPath,
				".//span[text()='Message Sent!']/../h2");
		List<WebElement> additionalContent = Element.getListOfElements(testConfig, How.xPath,
				".//div[contains(@class,'type-brds-v2-lg-300')]/p/span");

		Helper.compareEquals(testConfig, "Title over success screen", "Message Sent!",
				messageSent.getText().trim());
		Helper.compareEquals(testConfig, "Thank you message over success screen", "Thank you, " + firstNameValue,
				thankYouMsg.getText().trim());

		for (int j = 0; j < additionalMsg.length; j++) {
			Helper.compareEquals(testConfig, "Additional content " + (j + 1), additionalMsg[j],
					additionalContent.get(j).getText());
		}
	}
	
	public void clickOnLocation(String location, String pageType) {

		WebElement inputSearch = Element.getPageElement(testConfig, How.css,
				"input[class*='fyh-input'][aria-labelledby='Search']");
		Element.click(testConfig, inputSearch, "Search input box");
		Browser.wait(testConfig, 2);

		if(pageType.equals("FYHPage")) {
			WebElement clearButton = Element.getPageElement(testConfig, How.css, "button[aria-labelledby='ClearSearch']");
			Element.click(testConfig, clearButton, "Clear search value");
			Browser.wait(testConfig, 2);
		}
		List<WebElement> allLocations = Element.getListOfElements(testConfig, How.css,
				"li.srch-items-center div.srch-flex-grow");

		for (int i = 0; i < allLocations.size(); i++) {
			if(allLocations.get(i).getText().equals(location)) {
				Element.click(testConfig, allLocations.get(i), allLocations.get(i).getText() + " suggested state location");
				break;
			}
		}

		Browser.wait(testConfig, 2);
	}
	
	public void verifySelectedLocationWithSearchByMapText(String location) {

		WebElement searchBar = Element.getPageElement(testConfig, How.xPath, " .//input[contains(@class,'fyh-input')][@aria-labelledby='Search']");
		Helper.compareContains(testConfig, "Selected location", location, searchBar.getAttribute("modelvalue"));
		
		WebElement searchByMapSection = Element.getPageElement(testConfig, How.xPath, ".//li[contains(@class,'srch-text-brds-v2-navy-100')][@role='option']");
		Helper.compareEquals(testConfig, "Search by map content updated to ", "Search " + searchBar.getAttribute("modelvalue"), searchByMapSection.getText());

	}
	
	public void verifyURLAsPerDomain(String genericLink) {
		try {
			if (genericLink.contains("brookfieldresidential") || genericLink.contains("azureedge.net") || genericLink.contains("azurewebsites.net")) {
				verifyURLStatus(genericLink);
			} else {
				verifyURLStatus(homeurl + genericLink);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
