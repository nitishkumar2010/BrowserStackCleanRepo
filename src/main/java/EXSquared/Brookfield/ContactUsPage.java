package EXSquared.Brookfield;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;
import Utils.Helper;

public class ContactUsPage extends BRPHelper {

	@FindBy(xpath = ".//h1[text()='Contact Us']")
	private WebElement contactUsTitle;

	public enum ContactSections {

		Careers, GeneralInquiries, InvestorAndMediaRelations
	}

	public ContactUsPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		PageFactory.initElements(this.testConfig.driver, this);
		try {
			Helper.removeCookies(testConfig);
			testConfig.driver.navigate().refresh();
			Browser.waitWithoutLogging(testConfig, 4);
			WebElement acceptCookies = Element.getPageElement(testConfig, How.css,
					"button#onetrust-accept-btn-handler");
			Element.click(testConfig, acceptCookies, "One trust accept cookies button");
			testConfig.putRunTimeProperty("CookieSetting", "Yes");
		} catch (Exception e) {
			testConfig.putRunTimeProperty("CookieSetting", "No");
			testConfig.logComment("Accept cookie section not displayed");
		}
		Browser.waitForPageLoad(testConfig, contactUsTitle);
	}

	public void verifyErrorMessageForReqInfoForm(String expectedRegion, String[] expectedErrorMsgs) {

		String fields[] = {"First Name", "Last Name", "Email", "Consent"};
		selectRegionAndCommunity(expectedRegion);

		WebElement reqInfoBtn = Element.getPageElement(testConfig, How.xPath, ".//button[contains(text(),'Request Information')]");
		Element.click(testConfig, reqInfoBtn, "Request Information button");
		Browser.wait(testConfig, 2);

		//Helper.scrollOnContactUsSendAMessageModal(testConfig, false);

		List<WebElement> submitBtn = Element.getListOfElements(testConfig, How.xPath, ".//button[@value='Submit'][not(contains(@class,'footer'))]");
		Element.click(testConfig, submitBtn.get(submitBtn.size() - 1), "Submit button");
		Browser.wait(testConfig, 2);

		String errorLoc = ".//span[contains(@class,'field-validation-error')]";
		List<WebElement> allErrors = Element.getListOfElements(testConfig, How.xPath, errorLoc);

		for (int i = 0; i < allErrors.size(); i++) {
			WebElement errorMsg = allErrors.get(i);
			Helper.compareEquals(testConfig, "Error message for " + fields[i] +  " field", expectedErrorMsgs[i], errorMsg.getAttribute("innerText").trim());
		}
	}

	private void selectRegionAndCommunity(String expectedRegion) {

		String btnLocator = ".//button/span[text()='Contact Our Team']";
		WebElement contactBtn = Element.getPageElement(testConfig, How.xPath, btnLocator);
		Element.click(testConfig, contactBtn, "Contact Our Team button");
		Browser.wait(testConfig, 2);

		try {
			WebElement modal = Element.getPageElement(testConfig, How.xPath, ".//button[@aria-hidden='true']//parent::div//div[contains(@class,'min-h-screen text-center')]");
			if(!modal.isDisplayed()) {
				testConfig.logFail("Failed to verify that modal opened after clicking 'Contact Our Team' CTA");
			} else {
				testConfig.logPass("Verified that modal opened after clicking 'Contact Our Team' CTA");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that modal opened after clicking 'Contact Our Team' CTA");
		}

		Browser.wait(testConfig, 2);
		String selectRegion = ".//p[contains(text(),'Region')]/parent::div//p[text()='Select your Region']";
		WebElement selectRgnDropdown = Element.getPageElement(testConfig, How.xPath, selectRegion);
		Element.click(testConfig, selectRgnDropdown, "Select Region Dropdown");
		Browser.wait(testConfig, 5);

		boolean flag = false;
		String regionListLocator = ".//ul[@role='listbox']//li/span";
		//Helper.scrollOnContactUsModal(testConfig, false);
		List<WebElement> allRegions = Element.getListOfElements(testConfig, How.xPath, regionListLocator);
		if(allRegions.size() < 1) {
			testConfig.logFail("Region list not populating... hence marking the test case as failed");
		} else {
			for (Iterator<WebElement> iterator = allRegions.iterator(); iterator.hasNext();) {
				WebElement region = (WebElement) iterator.next();
				if(region.getText().equals(expectedRegion)) {
					flag = true;
					Element.click(testConfig, region, region.getText() + " region");
					Browser.wait(testConfig, 2);
					break;
				}
			}
		}

		if(!flag) {
			testConfig.logFail("Expected region not displaying in the dropdown... hence marking the test as failed");
		} else {
			String selectCommunity = ".//p[contains(text(),'Community')]/parent::div//p[contains(text(),'Select your Community')]";
			WebElement selectCommunityDropdown = Element.getPageElement(testConfig, How.xPath, selectCommunity);
			Element.click(testConfig, selectCommunityDropdown, "Select Region Dropdown");
			Browser.wait(testConfig, 2);

			String communityListLocator = ".//ul[@role='listbox']//li";
			List<WebElement> allCommunities = Element.getListOfElements(testConfig, How.xPath, communityListLocator);
			testConfig.putRunTimeProperty("CommunityName", allCommunities.get(0).getText());
			Element.click(testConfig, allCommunities.get(0), allCommunities.get(0).getText() + " community");
			Browser.wait(testConfig, 2);
		}
	}

	public void verifyFieldsEditableBehavior(String expectedRegion) {

		selectRegionAndCommunity(expectedRegion);
		
		Browser.wait(testConfig, 2);
		WebElement reqInfoBtn = Element.getPageElement(testConfig, How.xPath, ".//button[contains(text(),'Request Information')]");
		Element.click(testConfig, reqInfoBtn, "Request Information button");
		Browser.wait(testConfig, 2);

		String firstNameLocator = ".//input[@data-sc-field-name='First Name'][@placeholder='Your First Name']";
		String lastNameLocator = ".//input[@data-sc-field-name='Last Name'][@placeholder='Your Last Name']";
		String emailLocator = ".//input[@data-sc-field-name='Email Address'][@placeholder='email@domain.com']";
		String phoneLocator = ".//input[@data-sc-field-name='Phone Number'][@placeholder='Your Phone Number']";

		String firstNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddressValue = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		List<WebElement> firstName = Element.getListOfElements(testConfig, How.xPath, firstNameLocator);
		List<WebElement> lastName = Element.getListOfElements(testConfig, How.xPath, lastNameLocator);
		List<WebElement> emailAddress = Element.getListOfElements(testConfig, How.xPath, emailLocator);
		List<WebElement> phone = Element.getListOfElements(testConfig, How.xPath, phoneLocator);

		Element.enterData(testConfig, firstName.get(firstName.size() - 1), firstNameValue, "First Name field value");
		Element.enterData(testConfig, lastName.get(lastName.size() - 1), lastNameValue, "Last Name field value");
		Element.enterData(testConfig, emailAddress.get(emailAddress.size() - 1), emailAddressValue, "Email Address field value");
		Element.enterData(testConfig, phone.get(phone.size() - 1), String.valueOf(phoneNo), "Phone Number field value");
		
		String phoneNum = String.valueOf(phoneNo);
		String formattedPhoneNum = "(" + phoneNum.substring(0, 3) + ") " + phoneNum.substring(3,6) + "-" + phoneNum.substring(6);
		Browser.wait(testConfig, 2);

		Helper.compareEquals(testConfig, "Text entered for First Name field", firstNameValue,
				firstName.get(firstName.size() - 1).getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Last Name field", lastNameValue,
				lastName.get(lastName.size() - 1).getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Email Address field", emailAddressValue,
				emailAddress.get(emailAddress.size() - 1).getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Phone Number field", formattedPhoneNum,
				phone.get(phone.size() - 1).getAttribute("value"));

		//Helper.scrollOnContactUsSendAMessageModal(testConfig, false);
		String comments = ".//textarea[@data-sc-field-name='Message']";
		List<WebElement> messageField = Element.getListOfElements(testConfig, How.xPath, comments);

		String communityName = testConfig.getRunTimeProperty("CommunityName");
		Helper.compareContains(testConfig, "Comments already provided for Message field", "I'm interested in getting more information about " + communityName,
				messageField.get(messageField.size() - 1).getAttribute("placeholder"));

		String messageValue = "Test_" + Helper.generateRandomAlphabetsString(30);
		Element.enterData(testConfig, messageField.get(messageField.size() - 1), messageValue, "Message field value");

		Helper.compareEquals(testConfig, "Comments entered for Message field", messageValue,
				messageField.get(messageField.size() - 1).getAttribute("value"));

		Browser.wait(testConfig, 2);

	}

	public void submitContactOurTeamFormAndVerifyResponse(String expectedRegion, String[] additionalMsg, String leadForm) {

		selectRegionAndCommunity(expectedRegion);

		WebElement reqInfoBtn = Element.getPageElement(testConfig, How.xPath, ".//button[contains(text(),'Request Information')]");
		Element.click(testConfig, reqInfoBtn, "Request Information button");
		Browser.wait(testConfig, 2);

		String firstNameLocator = ".//input[@data-sc-field-name='First Name'][@placeholder='Your First Name']";
		String lastNameLocator = ".//input[@data-sc-field-name='Last Name'][@placeholder='Your Last Name']";
		String emailLocator = ".//input[@data-sc-field-name='Email Address'][@placeholder='email@domain.com']";
		String phoneLocator = ".//input[@data-sc-field-name='Phone Number'][@placeholder='Your Phone Number']";

		String firstNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddressValue = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		List<WebElement> firstName = Element.getListOfElements(testConfig, How.xPath, firstNameLocator);
		List<WebElement> lastName = Element.getListOfElements(testConfig, How.xPath, lastNameLocator);
		List<WebElement> emailAddress = Element.getListOfElements(testConfig, How.xPath, emailLocator);
		List<WebElement> phone = Element.getListOfElements(testConfig, How.xPath, phoneLocator);

		Element.enterData(testConfig, firstName.get(firstName.size() - 1), firstNameValue, "First Name field value");
		Element.enterData(testConfig, lastName.get(lastName.size() - 1), lastNameValue, "Last Name field value");
		Element.enterData(testConfig, emailAddress.get(emailAddress.size() - 1), emailAddressValue, "Email Address field value");
		Element.enterData(testConfig, phone.get(phone.size() - 1), String.valueOf(phoneNo), "Phone Number field value");

		Helper.scrollOnContactUsSendAMessageModal(testConfig, false);

		String comments = ".//textarea[@data-sc-field-name='Message']";
		List<WebElement> messageField = Element.getListOfElements(testConfig, How.xPath, comments);
		String messageValue = "Test_" + Helper.generateRandomAlphabetsString(30);
		Element.enterData(testConfig, messageField.get(messageField.size() - 1), messageValue, "Message field value");

		String consent = ".//input[@data-sc-field-name='Terms and Conditions']";
		List<WebElement> consentField = Element.getListOfElements(testConfig, How.xPath, consent);
		Element.click(testConfig, consentField.get(consentField.size() - 1), "Consent checkbox");

		Browser.wait(testConfig, 2);

		List<WebElement> submitBtn = Element.getListOfElements(testConfig, How.xPath, ".//button[@value='Submit'][not(contains(@class,'footer'))]");
		Element.click(testConfig, submitBtn.get(submitBtn.size() - 1), "Submit button");
		Browser.wait(testConfig, 10);

		verifySuccessMessageContactOurTeamForm(firstNameValue, additionalMsg);
		createExcelFileAndWriteEmailAddress(emailAddressValue, leadForm);

	}
	
	private void verifySuccessMessageContactOurTeamForm(String firstNameValue, String[] additionalMsg) {

		List<WebElement> messageSent = Element.getListOfElements(testConfig, How.xPath, ".//div[@title='Message Sent!']/span");
		List<WebElement> thankYouMsg = Element.getListOfElements(testConfig, How.xPath, ".//h2[contains(@class,'line-clamp-2')]");
		List<WebElement> additionalContent = Element.getListOfElements(testConfig, How.xPath, ".//div[contains(@class,'text-brp-blue-100')]/p");

		Helper.compareEquals(testConfig, "Title over success screen", "Message Sent!", messageSent.get(messageSent.size() - 1).getText().trim());
		Helper.compareEquals(testConfig, "Thank you message over success screen", "Thank you " + firstNameValue, thankYouMsg.get(thankYouMsg.size() - 1).getText().trim());

		int i = additionalContent.size();
		int k = 0;
		for (int j = 0; j < additionalMsg.length; j++) {
			Helper.compareContains(testConfig, "Additional content " + ++k, additionalMsg[j], additionalContent.get((i - 2) + (j - 1)).getText());
		}

	}

	
	public void verifyErrorForLastNameField(String expectedErrorMessage) {

		String lastNameLocator = ".//input[@data-sc-field-name='Last Name'][@placeholder='Your Last Name']";
		List<WebElement> lastName = Element.getListOfElements(testConfig, How.xPath, lastNameLocator);
		String lastNameValue = Helper.generateRandomAlphabetsString(1);
		Element.enterDataAfterClick(testConfig, lastName.get(lastName.size() - 1), lastNameValue, "Last Name field value");

		String consent = ".//input[@data-sc-field-name='Terms and Conditions']";
		List<WebElement> consentField = Element.getListOfElements(testConfig, How.xPath, consent);
		Element.click(testConfig, consentField.get(consentField.size() - 1), "Consent checkbox");

		Browser.wait(testConfig, 2);

		List<WebElement> submitBtn = Element.getListOfElements(testConfig, How.xPath, ".//button[@value='Submit'][not(contains(@class,'footer'))]");
		Element.click(testConfig, submitBtn.get(submitBtn.size() - 1), "Submit button");
		Browser.wait(testConfig, 2);

		String errorLoc = ".//span[contains(@class,'field-validation-error')]";
		List<WebElement> allErrors = Element.getListOfElements(testConfig, How.xPath, errorLoc);

		for (int i = 0; i < allErrors.size(); i++) {
			WebElement errorMsg = allErrors.get(i);
			Helper.compareEquals(testConfig, "Error message for Last Name field", expectedErrorMessage, errorMsg.getAttribute("innerText").trim());
		}
	}

	public void verifyRealStateAgentCheckboxSelection() {

		String yesCheckboxLocator = "//input[@data-sc-field-name='Agent Opt-In'][@value='true']";
		String noCheckboxLocator = "//input[@data-sc-field-name='Agent Opt-In'][@value='false']";

		WebElement yesCheckbox = Element.getPageElement(testConfig, How.xPath, yesCheckboxLocator);
		WebElement noCheckbox = Element.getPageElement(testConfig, How.xPath, noCheckboxLocator);

		verifyCheckboxSelected(yesCheckbox, "Yes");
		verifyCheckboxNotSelected(noCheckbox, "No");
		verifyCheckboxSelected(noCheckbox, "No");
		verifyCheckboxNotSelected(yesCheckbox, "Yes");
	}

	private void verifyCheckboxNotSelected(WebElement checkbox, String checkboxName) {

		try {
			if(!checkbox.isSelected()) {
				testConfig.logPass("Verified that the other checkbox i.e. " + checkboxName + " checkbox, is displaying as not selected");
			} else {
				testConfig.logFail("Failed to verify that the other checkbox i.e. " + checkboxName + " checkbox, is displaying as not selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that the other checkbox i.e. " + checkboxName + " checkbox, is displaying as not selected");
		}
	}

	private void verifyCheckboxSelected(WebElement checkbox, String checkboxName) {

		Element.click(testConfig, checkbox, checkboxName + " checkbox");
		Browser.wait(testConfig, 2);

		try {
			if(checkbox.isSelected()) {
				testConfig.logPass("Verified that after clicking " + checkboxName + " checkbox, the checkbox is displaying as selected");
			} else {
				testConfig.logFail("Failed to verify that on clicking " + checkboxName + " checkbox, the checkbox is displaying as selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that on clicking " + checkboxName + " checkbox, the checkbox is displaying as selected");
		}

	}

	public void verifyCallUsAndGetDirectionButton(String expectedRegion) {

		selectRegionAndCommunity(expectedRegion);

		try {
			String callUsBtnLocator = ".//div/a[contains(text(),'Call Us')]";
			WebElement callUsBtn = Element.getPageElement(testConfig, How.xPath, callUsBtnLocator);

			String phoneNoLocator = ".//span[@class='block']/parent::div/span[2]";
			WebElement addressPhoneNo = Element.getPageElement(testConfig, How.xPath, phoneNoLocator);

			Helper.compareEquals(testConfig, "Call Us href link", "tel:" + addressPhoneNo.getAttribute("innerText").trim(), callUsBtn.getAttribute("href"));
		} catch (Exception e) {
			verifyCallUsBtnNotDisplaying();
		}

		String addressLocator = ".//span[@class='block']/parent::div/span[1]";

		try {
			WebElement addressLocatorElement = Element.getPageElement(testConfig, How.xPath, addressLocator);
			if(addressLocatorElement != null) {
				verifyGetDirectionBtnDisplaying(addressLocatorElement);
			} else {
				verifyGetDirectionBtnNotDisplaying();
			}
		} catch (Exception e) {
			verifyGetDirectionBtnNotDisplaying();
		}

	}

	private void verifyGetDirectionBtnNotDisplaying() {

		String getDirectionLocator = ".//a[contains(text(),'Get Directions')]";

		try {
			WebElement getDirectionBtn = Element.getPageElement(testConfig, How.xPath, getDirectionLocator);
			if(getDirectionBtn.isDisplayed()) {
				testConfig.logFail("Getting Get Direction button displaying which should not be there as no address has been specified for the region");
			} else {
				testConfig.logPass("Verified that Get Direction button is not displaying as no address has been specified for the region");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified that Get Direction button is not displaying as no address has been specified for the region");
		}		
	}

	private void verifyGetDirectionBtnDisplaying(WebElement addressLocatorElement) {

		String finalHrefValue = "";
		String getDirectionLocator = ".//a[contains(text(),'Get Directions')]";
		WebElement getDirectionBtn = Element.getPageElement(testConfig, How.xPath, getDirectionLocator);
		try {
			finalHrefValue = java.net.URLDecoder.decode(getDirectionBtn.getAttribute("href"), StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Helper.compareContains(testConfig, "Href attribute for the Email us button", "maps", finalHrefValue);
	}

	public void verifyContactCustomerCareCTA(String expectedRegion) {

		String btnLocator = ".//button/span[text()='Contact Customer Care']";
		WebElement contactBtn = Element.getPageElement(testConfig, How.xPath, btnLocator);
		Element.click(testConfig, contactBtn, "Contact Customer Care button");
		Browser.wait(testConfig, 2);

		try {
			WebElement modal = Element.getPageElement(testConfig, How.css, "div.min-h-screen");
			if(!modal.isDisplayed()) {
				testConfig.logFail("Failed to verify that modal opened after clicking 'Contact Our Team' CTA");
			} else {
				testConfig.logPass("Verified that modal opened after clicking 'Contact Our Team' CTA");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that modal opened after clicking 'Contact Our Team' CTA");
		}

		String selectRegion = ".//p[text()='Region']/parent::div//button/span[text()='Select Your Region']";
		WebElement selectRgnDropdown = Element.getPageElement(testConfig, How.xPath, selectRegion);
		Element.click(testConfig, selectRgnDropdown, "Select Region Dropdown");
		Browser.wait(testConfig, 2);

		boolean flag = false;
		String regionListLocator = ".//ul[@role='listbox']//li/span";
		List<WebElement> allRegions = Element.getListOfElements(testConfig, How.xPath, regionListLocator);
		if(allRegions.size() < 1) {
			testConfig.logFail("Region list not populating... hence marking the test case as failed");
		} else {
			for (Iterator<WebElement> iterator = allRegions.iterator(); iterator.hasNext();) {
				WebElement region = (WebElement) iterator.next();
				if(region.getText().equals(expectedRegion)) {
					flag = true;
					Element.click(testConfig, region, region.getText() + " region");
					Browser.wait(testConfig, 2);
					break;
				}
			}
		}

		if(flag) {
			String phoneLocator = ".//p[contains(@class,'t-xl-600')]/parent::div/p[2]";
			String emaillocator = ".//p[contains(@class,'t-xl-600')]/parent::div/p[3]";

			try {
				WebElement phoneNoField = Element.getPageElement(testConfig, How.xPath, phoneLocator);
				if(phoneNoField != null) {
					verifyCallUsBtnDisplaying(phoneNoField);
				} else {
					verifyCallUsBtnNotDisplaying();
				}
			} catch (Exception e) {
				verifyCallUsBtnNotDisplaying();
			}

			try {
				WebElement emailAddressField = Element.getPageElement(testConfig, How.xPath, emaillocator);
				if(emailAddressField != null) {
					verifyEmailUsBtnDisplaying(emailAddressField);
				} else {
					verifyEmailUsBtnNotDisplaying();
				}
			} catch (Exception e) {
				verifyEmailUsBtnNotDisplaying();
			}
		} else {
			testConfig.logFail("Expected region not displaying in the dropdown... hence marking the test as failed");
		}
	}

	private void verifyEmailUsBtnNotDisplaying() {

		String emailUsLocator = ".//div/a[contains(text(),'Call Us')]/parent::div/a[contains(text(),'Email Us')]";

		try {
			WebElement emailUsBtn = Element.getPageElement(testConfig, How.xPath, emailUsLocator);
			if(emailUsBtn.isDisplayed()) {
				testConfig.logFail("Getting Call Us button displaying which should not be there as no contact number has been specified for the region");
			} else {
				testConfig.logPass("Verified that Call Us button is not displaying as no contact number has been specified for the region");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified that Call Us button is not displaying as no contact number has been specified for the region");
		}		
	}

	private void verifyEmailUsBtnDisplaying(WebElement emailAddressField) {

		String finalHrefValue = "";
		String expectedHref = "mailto:" + emailAddressField.getAttribute("innerText").trim() + "?subject=Brookfield Residential website support request";
		String emailUsLocator = ".//div/a[contains(text(),'Call Us')]/parent::div/a[contains(text(),'Email Us')]";
		WebElement emailUsBtn = Element.getPageElement(testConfig, How.xPath, emailUsLocator);
		try {
			finalHrefValue = java.net.URLDecoder.decode(emailUsBtn.getAttribute("href"), StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Helper.compareEquals(testConfig, "Href attribute for the Email us button", expectedHref, finalHrefValue);
	}

	private void verifyCallUsBtnNotDisplaying() {

		String callUsBtnLocator = ".//div/a[contains(text(),'Call Us')]";

		try {
			WebElement callUsBtn = Element.getPageElement(testConfig, How.xPath, callUsBtnLocator);
			if(callUsBtn.isDisplayed()) {
				testConfig.logFail("Getting Call Us button displaying which should not be there as no contact number has been specified for the region");
			} else {
				testConfig.logPass("Verified that Call Us button is not displaying as no contact number has been specified for the region");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified that Call Us button is not displaying as no contact number has been specified for the region");
		}
	}

	private void verifyCallUsBtnDisplaying(WebElement phoneNoField) {

		String callUsBtnLocator = ".//div/a[contains(text(),'Call Us')]";
		WebElement callUsBtn = Element.getPageElement(testConfig, How.xPath, callUsBtnLocator);
		Helper.compareContains(testConfig, "Href attribute for the call us button", "tel:" + phoneNoField.getAttribute("innerText").trim(), callUsBtn.getAttribute("href"));
	}

	public void verifyDifferentSections(ContactSections contactSections, String expectedSectionHeading, String expectedSectionTitle, String[] additionalMsg, String leadForm) {

		String sectionTitleLocator = "", sectionHeadingLocator = "", sendAMessageLocator = "";
		String consent = ".//input[@data-sc-field-name='Terms and Conditions']";

		switch (contactSections) {
		case Careers:
			sectionTitleLocator = "(.//section[contains(@class,'lg:grid-cols-3')]//h2)[1]";
			sectionHeadingLocator = "(.//section[contains(@class,'lg:grid-cols-3')]//p)[1]";
			sendAMessageLocator = "(.//section[contains(@class,'lg:grid-cols-3')]//button)[1]";
			consent = ".//section[@id='careers']//input[@data-sc-field-name='Terms and Conditions']";
			String learnMoreLocator = ".//a[@href='/careers'][contains(text(),'Learn More')]";
			WebElement learnMoreElement = Element.getPageElement(testConfig, How.xPath, learnMoreLocator);

			String linkVal = learnMoreElement.getAttribute("href");
			verifyURLAsPerDomain(linkVal);

			Helper.compareEquals(testConfig, "href associated with Careers section", homeurl + "careers", learnMoreElement.getAttribute("href"));

			break;

		case InvestorAndMediaRelations:
			sectionTitleLocator = "(.//section[contains(@class,'lg:grid-cols-3')]//h2)[2]";
			sectionHeadingLocator = "(.//section[contains(@class,'lg:grid-cols-3')]//p)[2]";
			sendAMessageLocator = "(.//section[contains(@class,'lg:grid-cols-3')]//button)[2]";

			String investorLearnMoreLocator = ".//a[@href='/about/investor-media-relations'][contains(text(),'Learn More')]";
			WebElement investorLearnMoreElement = Element.getPageElement(testConfig, How.xPath, investorLearnMoreLocator);

			String linkValue = investorLearnMoreElement.getAttribute("href");
			verifyURLAsPerDomain(linkValue);

			Helper.compareEquals(testConfig, "href associated with Investor And Media Relations section", homeurl + "about/investor-media-relations", investorLearnMoreElement.getAttribute("href"));

			break;

		case GeneralInquiries:
			sectionTitleLocator = "(.//section[contains(@class,'lg:grid-cols-3')]//h2)[3]";
			sectionHeadingLocator = "(.//section[contains(@class,'lg:grid-cols-3')]//p)[3]";
			sendAMessageLocator = "(.//section[contains(@class,'lg:grid-cols-3')]//button)[2]";
			consent = ".//section[@id='general-inquiries-contact']//input[@data-sc-field-name='ConsentField']";
			break;
		}

		WebElement titleOverPage = Element.getPageElement(testConfig, How.xPath, sectionTitleLocator);
		WebElement headingOverPage = Element.getPageElement(testConfig, How.xPath, sectionHeadingLocator);

		Helper.compareEquals(testConfig, "Section Title", expectedSectionTitle, titleOverPage.getText());
		Helper.compareEquals(testConfig, "Section Heading", expectedSectionHeading, headingOverPage.getText());

		int i = 0;
		switch (contactSections) {
		case Careers:
			String firstNameFieldValue = "Test_" + Helper.generateRandomAlphabetsString(10);
			String emailAddress = openCareersFormAndSubmit(sendAMessageLocator, firstNameFieldValue, consent);
			verifySuccessMessage(firstNameFieldValue, i, additionalMsg);
			createExcelFileAndWriteEmailAddress(emailAddress, leadForm);
			break;
		case GeneralInquiries:
			i = 1;
			String firstNameFieldVal = "Test_" + Helper.generateRandomAlphabetsString(10);
			String emailAddressGI = openGeneralInquiresFormAndSubmit(sendAMessageLocator, firstNameFieldVal, consent, contactSections);
			verifySuccessMessage(firstNameFieldVal, i, additionalMsg);
			createExcelFileAndWriteEmailAddress(emailAddressGI, leadForm);
			break;

		case InvestorAndMediaRelations:
			break;
		}

	}

	private String openCareersFormAndSubmit(String sendAMessageLocator, String firstNameValue, String consent) {
		WebElement sendMessageElement = Element.getPageElement(testConfig, How.xPath, sendAMessageLocator);
		Element.click(testConfig, sendMessageElement, "Send A Message CTA");

		String firstNameLocator = ".//section[@id='careers']//input[@data-sc-field-name='First Name'][@placeholder='Your First Name']";
		String lastNameLocator = ".//section[@id='careers']//input[@data-sc-field-name='Last Name'][@placeholder='Your Last Name']";
		String emailLocator = ".//section[@id='careers']//input[@data-sc-field-name='Email Address'][@placeholder='email@domain.com']";
		String phoneLocator = ".//section[@id='careers']//input[@data-sc-field-name='Phone Number'][@placeholder='Your Phone Number']";

		WebElement firstName = Element.getPageElement(testConfig, How.xPath, firstNameLocator);
		WebElement lastName = Element.getPageElement(testConfig, How.xPath, lastNameLocator);
		WebElement emailAddress = Element.getPageElement(testConfig, How.xPath, emailLocator);
		WebElement phone = Element.getPageElement(testConfig, How.xPath, phoneLocator);

		String lastNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddressValue = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);
		String messageValue = "Test_" + Helper.generateRandomAlphabetsString(30);

		Element.enterData(testConfig, firstName, firstNameValue, "First Name field value");
		Element.enterData(testConfig, lastName, lastNameValue, "Last Name field value");
		Element.enterData(testConfig, emailAddress, emailAddressValue, "Email Address field value");
		Element.enterData(testConfig, phone, String.valueOf(phoneNo), "Phone Number field value");

		Helper.scrollOnContactUsSendAMessageModal(testConfig, false);
		String comments = ".//section[@id='careers']//textarea[@data-sc-field-name='Message']";
		WebElement messageField = Element.getPageElement(testConfig, How.xPath, comments);
		WebElement consentField = Element.getPageElement(testConfig, How.xPath, consent);

		Element.enterData(testConfig, messageField, messageValue, "Message field value");
		Element.click(testConfig, consentField, "Consent checkbox");

		Browser.wait(testConfig, 2);

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath, ".//section[@id='careers']//button[@value='Submit'][not(contains(@class,'footer'))]");
		Element.click(testConfig, submitBtn, "Submit button");
		Browser.wait(testConfig, 2);
		return emailAddressValue;
	}

	private String openGeneralInquiresFormAndSubmit(String sendAMessageLocator, String firstNameValue, String consent, ContactSections contactSections) {
		WebElement sendMessageElement = Element.getPageElement(testConfig, How.xPath, sendAMessageLocator);
		Element.click(testConfig, sendMessageElement, "Send A Message CTA");

		String firstNameLocator = ".//section[@id='general-inquiries-contact']//input[@data-sc-field-name='First Name'][@placeholder='Your First Name']";
		String lastNameLocator = ".//section[@id='general-inquiries-contact']//input[@data-sc-field-name='Last Name'][@placeholder='Your Last Name']";
		String emailLocator = ".//section[@id='general-inquiries-contact']//input[@data-sc-field-name='Email Address'][@placeholder='email@domain.com']";
		String phoneLocator = ".//section[@id='general-inquiries-contact']//input[@data-sc-field-name='Phone Number'][@placeholder='Your Phone Number']";

		WebElement firstName = Element.getPageElement(testConfig, How.xPath, firstNameLocator);
		WebElement lastName = Element.getPageElement(testConfig, How.xPath, lastNameLocator);
		WebElement emailAddress = Element.getPageElement(testConfig, How.xPath, emailLocator);
		WebElement phone = Element.getPageElement(testConfig, How.xPath, phoneLocator);

		String lastNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddressValue = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);
		String messageValue = "Test_" + Helper.generateRandomAlphabetsString(30);

		Element.enterData(testConfig, firstName, firstNameValue, "First Name field value");
		Element.enterData(testConfig, lastName, lastNameValue, "Last Name field value");
		Element.enterData(testConfig, emailAddress, emailAddressValue, "Email Address field value");
		Element.enterData(testConfig, phone, String.valueOf(phoneNo), "Phone Number field value");

		Helper.scrollOnContactUsSendAMessageModal(testConfig, false);
		String comments = ".//section[@id='general-inquiries-contact']//textarea[@data-sc-field-name='Message']";
		WebElement messageField = Element.getPageElement(testConfig, How.xPath, comments);
		WebElement consentField = Element.getPageElement(testConfig, How.xPath, consent);

		Element.enterData(testConfig, messageField, messageValue, "Message field value");
		Element.click(testConfig, consentField, "Consent checkbox");

		Browser.wait(testConfig, 2);

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath, ".//section[@id='general-inquiries-contact']//button[@value='Submit'][not(contains(@class,'footer'))]");
		Element.click(testConfig, submitBtn, "Submit button");
		Browser.wait(testConfig, 2);
		return emailAddressValue;

	}

	private void verifySuccessMessage(String firstNameValue, int index, String[] additionalMsg) {

		Browser.wait(testConfig, 10);
		
		List<WebElement> messageSent = Element.getListOfElements(testConfig, How.xPath, ".//div/span[contains(@class,'text-brp-blue-100')]");
		List<WebElement> thankYouMsg = Element.getListOfElements(testConfig, How.xPath, ".//h2[contains(@class,'line-clamp-2')]");
		List<WebElement> additionalContent = Element.getListOfElements(testConfig, How.xPath, ".//div[contains(@class,'text-brp-blue-100')]/p");

		Helper.compareEquals(testConfig, "Title over success screen", "Message Sent!", messageSent.get(index).getText().trim());
		Helper.compareEquals(testConfig, "Thank you message over success screen", "Thank you " + firstNameValue, thankYouMsg.get(index).getText().trim());

		/*for (int i = 0; i < additionalContent.size(); i++) {
			WebElement webElement = additionalContent.get(i);
			if(!webElement.getText().isEmpty())
				Helper.compareContains(testConfig, "Additional content " + ++i, additionalMsg[i-1], webElement.getText());
		}*/

		int k = 0;
		for (int j = 0; j < additionalMsg.length; j++) {
			if(index == 1) {
				if(!(j == 2))
					Helper.compareContains(testConfig, "Additional content " + ++k, additionalMsg[j], additionalContent.get(j+3).getText());
			} else {
				Helper.compareContains(testConfig, "Additional content " + ++k, additionalMsg[j], additionalContent.get(j).getText());
			}
		}

	}

	public void verifyErrorMessagesForDifferentSectionForms(ContactSections contactSections, String[] expectedErrorMsgs) {

		String sendAMessageLocator = "";
		int index = 0;
		String fields[] = {"First Name", "Last Name", "Email", "Consent"};
		String generalInquiriesFields[] = {"First Name", "Last Name", "Email", "Message", "Consent"};

		switch (contactSections) {
		case Careers:
			sendAMessageLocator = "(.//section[contains(@class,'lg:grid-cols-3')]//button)[1]";
			break;

		case GeneralInquiries:
			sendAMessageLocator = "(.//section[contains(@class,'lg:grid-cols-3')]//button)[2]";
			break;

		case InvestorAndMediaRelations:
			break;
		}

		WebElement sendMessageElement = Element.getPageElement(testConfig, How.xPath, sendAMessageLocator);
		Element.click(testConfig, sendMessageElement, "Send A Message CTA");

		Browser.wait(testConfig, 2);

		List<WebElement> submitBtn = Element.getListOfElements(testConfig, How.xPath, ".//button[@value='Submit'][not(contains(@class,'footer'))]");
		Element.click(testConfig, submitBtn.get(index), "Submit button");
		Browser.wait(testConfig, 2);

		switch (contactSections) {
		case Careers:
			verifyErrorMessagesForSendMessageForm(expectedErrorMsgs, fields);
			break;

		case GeneralInquiries:
			verifyErrorMessagesForSendMessageForm(expectedErrorMsgs, generalInquiriesFields);
			break;

		case InvestorAndMediaRelations:
			break;
		}

	}

	private void verifyErrorMessagesForSendMessageForm(String[] expectedErrorMsgs, String[] fields) {

		String errorLoc = ".//span[contains(@class,'field-validation-error')]";
		List<WebElement> allErrors = Element.getListOfElements(testConfig, How.xPath, errorLoc);

		for (int i = 0; i < allErrors.size(); i++) {
			WebElement errorMsg = allErrors.get(i);
			Helper.compareEquals(testConfig, "Error message for " + fields[i] +  " field", expectedErrorMsgs[i], errorMsg.getAttribute("innerText").trim());
		}
	}

	public void verifyFormFieldsBehaviorForGeneralInquiriesForm() {

		//Helper.scrollOnContactUsModal(testConfig, true);

		String firstNameLocator = ".//section[@id='general-inquiries-contact']//input[@data-sc-field-name='First Name'][@placeholder='Your First Name']";
		String lastNameLocator = ".//section[@id='general-inquiries-contact']//input[@data-sc-field-name='Last Name'][@placeholder='Your Last Name']";
		String emailLocator = ".//section[@id='general-inquiries-contact']//input[@data-sc-field-name='Email Address'][@placeholder='email@domain.com']";
		String phoneLocator = ".//section[@id='general-inquiries-contact']//input[@data-sc-field-name='Phone Number'][@placeholder='Your Phone Number']";

		WebElement firstName = Element.getPageElement(testConfig, How.xPath, firstNameLocator);
		WebElement lastName = Element.getPageElement(testConfig, How.xPath, lastNameLocator);
		WebElement emailAddress = Element.getPageElement(testConfig, How.xPath, emailLocator);
		WebElement phone = Element.getPageElement(testConfig, How.xPath, phoneLocator);

		String firstNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddressValue = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);
		String messageValue = "Test_" + Helper.generateRandomAlphabetsString(30);

		Element.enterData(testConfig, firstName, firstNameValue, "First Name field value");
		Element.enterData(testConfig, lastName, lastNameValue, "Last Name field value");
		Element.enterData(testConfig, emailAddress, emailAddressValue, "Email Address field value");
		Element.enterData(testConfig, phone, String.valueOf(phoneNo), "Phone Number field value");

		String phoneNum = String.valueOf(phoneNo);
		String formattedPhoneNum = "(" + phoneNum.substring(0, 3) + ") " + phoneNum.substring(3,6) + "-" + phoneNum.substring(6);
		
		Browser.wait(testConfig, 2);
		Helper.compareEquals(testConfig, "Text entered for First Name field", firstNameValue,
				firstName.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Last Name field", lastNameValue,
				lastName.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Email Address field", emailAddressValue,
				emailAddress.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Phone Number field", formattedPhoneNum,
				phone.getAttribute("value"));

		//Helper.scrollOnContactUsModal(testConfig, false);

		String comments = ".//section[@id='general-inquiries-contact']//textarea[@data-sc-field-name='Message']";
		WebElement messageField = Element.getPageElement(testConfig, How.xPath, comments);
		Element.enterData(testConfig, messageField, messageValue, "Message field value");

		Browser.wait(testConfig, 2);
		Helper.compareEquals(testConfig, "Comments entered for Message field", messageValue,
				messageField.getAttribute("value"));

		Browser.wait(testConfig, 2);

	}

	public void verifyFormFieldsBehaviorForCareersForm() {

		//Helper.scrollOnContactUsModal(testConfig, true);

		String firstNameLocator = ".//section[@id='careers']//input[@data-sc-field-name='First Name'][@placeholder='Your First Name']";
		String lastNameLocator = ".//section[@id='careers']//input[@data-sc-field-name='Last Name'][@placeholder='Your Last Name']";
		String emailLocator = ".//section[@id='careers']//input[@data-sc-field-name='Email Address'][@placeholder='email@domain.com']";
		String phoneLocator = ".//section[@id='careers']//input[@data-sc-field-name='Phone Number'][@placeholder='Your Phone Number']";

		WebElement firstName = Element.getPageElement(testConfig, How.xPath, firstNameLocator);
		WebElement lastName = Element.getPageElement(testConfig, How.xPath, lastNameLocator);
		WebElement emailAddress = Element.getPageElement(testConfig, How.xPath, emailLocator);
		WebElement phone = Element.getPageElement(testConfig, How.xPath, phoneLocator);

		String firstNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddressValue = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);
		String messageValue = "Test_" + Helper.generateRandomAlphabetsString(30);

		Element.enterData(testConfig, firstName, firstNameValue, "First Name field value");
		Element.enterData(testConfig, lastName, lastNameValue, "Last Name field value");
		Element.enterData(testConfig, emailAddress, emailAddressValue, "Email Address field value");
		Element.enterData(testConfig, phone, String.valueOf(phoneNo), "Phone Number field value");

		String phoneNum = String.valueOf(phoneNo);
		String formattedPhoneNum = "(" + phoneNum.substring(0, 3) + ") " + phoneNum.substring(3,6) + "-" + phoneNum.substring(6);
		
		Browser.wait(testConfig, 2);
		Helper.compareEquals(testConfig, "Text entered for First Name field", firstNameValue,
				firstName.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Last Name field", lastNameValue,
				lastName.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Email Address field", emailAddressValue,
				emailAddress.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Phone Number field", formattedPhoneNum,
				phone.getAttribute("value"));

		//Helper.scrollOnContactUsModal(testConfig, false);

		String comments = ".//section[@id='careers']//textarea[@data-sc-field-name='Message']";
		WebElement messageField = Element.getPageElement(testConfig, How.xPath, comments);
		Element.enterData(testConfig, messageField, messageValue, "Message field value");

		Browser.wait(testConfig, 2);
		Helper.compareEquals(testConfig, "Comments entered for Message field", messageValue,
				messageField.getAttribute("value"));

		Browser.wait(testConfig, 2);

	}
}
