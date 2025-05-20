package EXSquared.Brookfield;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;
import Utils.Helper;

public class QMIPage extends BRPHelper {

	public enum PricingBreakdownSection {
		BasePrice, LotPremium, OptionsIncluded, HOACondo
	}

	public enum QMIHeaders {
		Overview, FloorPlan, Community, Map
	}

	public enum LinksFromQMIPage {
		Community, Plan, ViewOnMap
	}

	@FindBy(xpath = ".//button[text()='Overview']")
	private WebElement qmiOverviewTab;

	public QMIPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		PageFactory.initElements(this.testConfig.driver, this);
		try {
			Helper.removeCookies(testConfig);
			testConfig.driver.navigate().refresh();
			Browser.waitWithoutLogging(testConfig, 5);
			WebElement acceptCookies = Element.getPageElement(testConfig, How.css,
					"button#onetrust-accept-btn-handler");
			Element.click(testConfig, acceptCookies, "One trust accept cookies button");
			testConfig.putRunTimeProperty("CookieSetting", "Yes");
		} catch (Exception e) {
			testConfig.driver.navigate().refresh();
			testConfig.putRunTimeProperty("CookieSetting", "No");
			testConfig.logComment("Accept cookie section not displayed");
		}
		Browser.waitForPageLoad(testConfig, qmiOverviewTab);
	}

	public void verifyJumpAnchorTags(QMIHeaders qmiHeaders) {

		String tabLocator = "";
		String sectionLocator = "";

		switch (qmiHeaders) {
		case Overview:
			tabLocator = ".//button[text()='Overview']";
			sectionLocator = "(.//div[contains(@class,'md:breadcrumbText')])[2]/../../../..";
			break;

		case Community:
			tabLocator = ".//button[text()='Community']";
			sectionLocator = ".//section[@id='explore-community']";
			break;

		case FloorPlan:
			tabLocator = ".//button[text()='Floor Plan']";
			sectionLocator = ".//section[contains(@class,'brp-section-floorplan')]";
			break;

		case Map:
			tabLocator = ".//button[text()='Map']";
			sectionLocator = ".//section[@id='meet-the-new-neighborhood']";
			break;
			
		}

		try {
			WebElement tabElement = Element.getPageElement(testConfig, How.xPath, tabLocator);
			Element.click(testConfig, tabElement, qmiHeaders.toString() + " tab from plan nav header");
			WebElement sectionElement = Element.getPageElement(testConfig, How.xPath, sectionLocator);

			Browser.wait(testConfig, 2);
			Helper.compareContains(testConfig,
					"Jump anchor tag associated with " + qmiHeaders.toString() + " header tab",
					sectionElement.getAttribute("id"), tabElement.getAttribute("id"));
		} catch (Exception e) {
			testConfig.logComment(
					"Not having " + qmiHeaders.toString() + " tab over plan nav header so skipping that case");
		}
	}

	public Object verifyNavigationsToOtherDetailPages(LinksFromQMIPage linksFromQMIPage) {

		Object obj = null;
		String locator = "";
		WebElement differentLinks = null;

		switch (linksFromQMIPage) {
		case Community:
			locator = ".//p[text()='Community:']/../a";
			differentLinks = Element.getPageElement(testConfig, How.xPath, locator);
			testConfig.putRunTimeProperty("CommunityPlanPage", differentLinks.getText());
			Element.click(testConfig, differentLinks, "Community link");
			obj = new CommunityPage(testConfig);
			break;

		case Plan:
			locator = ".//p[text()='Plan:']/../a";
			differentLinks = Element.getPageElement(testConfig, How.xPath, locator);
			testConfig.putRunTimeProperty("PlanName", differentLinks.getText());
			Element.clickThroughJS(testConfig, differentLinks, locator);
			obj = new PlanPage(testConfig);
			break;

		case ViewOnMap:
			locator = ".//p[contains(text(),'Lot')]/../a";
			differentLinks = Element.getPageElement(testConfig, How.xPath, locator);
			Element.click(testConfig, differentLinks, "View On Map link");
			testConfig.putRunTimeProperty("RedirectionValue", "yes");
			obj = new FYHPage(testConfig);
			break;
		}
		return obj;
	}

	public void verifyNextStepsSectionBelowPricing(String title, String desc) {

		String titleLoc = ".//section[@id='home-details-section']//div[contains(@class,'items-start')]//div[contains(@class,'text-3xl')]";
		String descLoc = ".//section[@id='home-details-section']//div[contains(@class,'items-start')]//div[contains(@class,'text-brp-grey-900')]";

		String scheduleTourLoc = "(.//div[contains(@class,'items')]//div/button/span[text()='Schedule a Tour'])[1]";
		String contactUsLoc = "(.//div[contains(@class,'items')]//div/div[contains(text(),'Contact Us')])[1]";

		WebElement titleText = Element.getPageElement(testConfig, How.xPath, titleLoc);
		WebElement description = Element.getPageElement(testConfig, How.xPath, descLoc);
		WebElement scheduleTourBtn = Element.getPageElement(testConfig, How.xPath, scheduleTourLoc);
		WebElement contactUsBtn = Element.getPageElement(testConfig, How.xPath, contactUsLoc);

		String titleTextValue = Element.getText(testConfig, titleText, "Title");
		String descriptionText = Element.getText(testConfig, description, "Description");
		
		if(titleTextValue.trim().isEmpty()) {
			titleLoc = ".//section[@id='home-details-section']//div[contains(@class,'items-end')]//div[contains(@class,'text-3xl')]";
			descLoc = ".//section[@id='home-details-section']//div[contains(@class,'items-end')]//div[contains(@class,'text-brp-grey-900')]";
			titleText = Element.getPageElement(testConfig, How.xPath, titleLoc);
			description = Element.getPageElement(testConfig, How.xPath, descLoc);
			titleTextValue = Element.getText(testConfig, titleText, "Title");
			descriptionText = Element.getText(testConfig, description, "Description");
		}
		
		Helper.compareEquals(testConfig, "Title for the section", title, titleTextValue);
		Helper.compareContains(testConfig, "Description for the section", desc, descriptionText);

		Element.clickThroughJS(testConfig, scheduleTourBtn, "Schedule A Tour button");
		verifyFormOpensCorrectlyScheduleTour();

		Element.clickThroughJS(testConfig, contactUsBtn, "Contact Us button");
		Browser.wait(testConfig, 2);
		verifyFormOpensCorrectlyRequestInfo();
	}

	private void verifyFormOpensCorrectlyScheduleTour() {

		Browser.wait(testConfig, 2);
		String formLocator = "(.//button[@class='contained lg btn']//ancestor::div[@data-headlessui-state='open']//*[contains(@class,'cnt-relative')])[1]";

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

	public void verifyFactsAndFeaturesSection() {

		List<String> itemValues = new ArrayList<String>();

		String valuesDisplaying = ".//h2[contains(text(),'Facts & features')]/..//*[name()='svg']/parent::div[not(contains(@class,'hidden'))]/div/p[1]";
		List<WebElement> factsDisplaying = Element.getListOfElements(testConfig, How.xPath, valuesDisplaying);
		for (Iterator<WebElement> iterator = factsDisplaying.iterator(); iterator.hasNext();) {
			WebElement webElement = (WebElement) iterator.next();
			itemValues.add(webElement.getText().trim());
		}

		String sectionSVGLocator = ".//h2[contains(text(),'Facts & features')]/..//*[name()='svg']/parent::div[not(contains(@class,'hidden'))]";
		List<WebElement> allSVG = Element.getListOfElements(testConfig, How.xPath, sectionSVGLocator);

		for (int i = 0; i < allSVG.size(); i++) {
			if (allSVG.get(i).isDisplayed()) {
				testConfig.logPass("Verify getting SVG displaying for " + itemValues.get(i));
			} else {
				testConfig.logFail("SVG not displaying for " + itemValues.get(i));
			}
		}

		String factsValues = ".//h2[contains(text(),'Facts & features')]/..//*[name()='svg']/parent::div[not(contains(@class,'hidden'))]/div/p[2]";		
		List<WebElement> itemDetails = Element.getListOfElements(testConfig, How.xPath, factsValues);

		for (int i = 0; i < itemDetails.size(); i++) {
			String value = Element.getText(testConfig, itemDetails.get(i), itemValues.get(i) + " values");
			if (!value.isEmpty()) {
				testConfig.logPass("Getting " + itemValues.get(i) + " as " + value);
			} else {
				testConfig.logFail("Getting " + itemValues.get(i) + " value as blank... failing the scenario");
			}
		}
	}

	public void verifyDownloadBrochureButton() {
		String downloadBrochureLocator = ".//div[contains(@class,'items-start')]//a[contains(@class,'GA-download-brochure')]";
		WebElement brochureBtn = Element.getPageElement(testConfig, How.xPath, downloadBrochureLocator);

		if(brochureBtn.getAttribute("href").contains("pdf")) {

			String linkVal = brochureBtn.getAttribute("href");
			String expectedBrochure = linkVal.substring(linkVal.lastIndexOf("/") + 1, linkVal.lastIndexOf("."));

			Element.clickThroughJS(testConfig, brochureBtn, downloadBrochureLocator);
			Browser.wait(testConfig, 30);

			File brochureDownloadedFile = Browser.lastFileModified(testConfig, testConfig.downloadPath);

			if(expectedBrochure.substring(0, 5).toLowerCase().equals(brochureDownloadedFile.getName().substring(0, 5).toLowerCase())) {
				testConfig.logPass("Verified the downloaded brochure file name " + brochureDownloadedFile.getName());
			} else {
				testConfig.logFail("Failed to verify the downloaded brochure file name " + brochureDownloadedFile.getName());
			}
		} else {
			String imageLink = brochureBtn.getAttribute("href");
			try {
				if (imageLink.contains("brookfieldresidential") || imageLink.contains("azureedge.net")) {
					verifyURLStatus(imageLink);
				} else {
					verifyURLStatus(homeurl + imageLink);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void selectingImageFromCarouseOpenGalleryModal() {

		String imagesLocator = "section#explore-community div>button.btn.light";
		List<WebElement> allImagesCarousel = Element.getListOfElements(testConfig, How.css, imagesLocator);
		Element.click(testConfig, allImagesCarousel.get(0), "First image in the grid gallery carousel");
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

	public void verifyQMINameTagAddressAndDirection(String qmiName, String cityName) {

		String finalVal = "";
		String[] array = qmiName.split(",");
		if(array.length == 2) {
			finalVal = array[0].concat(",").concat(array[1]);
		} else {
			finalVal = array[0];
		}

		String locator = ".//*[text()='" + finalVal.trim() + "'][contains(@class,'font-semibold')]";
		WebElement qmiNameElement = Element.getPageElement(testConfig, How.xPath, locator);
		Helper.compareEquals(testConfig, "Tag associated with QMI Name element", "h1", qmiNameElement.getTagName());

		String cityLocator = ".//h2[contains(text(),'" + cityName + "')]";
		WebElement cityNameElement = Element.getPageElement(testConfig, How.xPath, cityLocator);
		Helper.compareContains(testConfig, "City name below QMI Name", cityName, cityNameElement.getText());

		String getDirectionLinkLocator = ".//section[@id='home-details-section']//a[contains(@href,'maps')]";
		WebElement getDirectionElement = Element.getPageElement(testConfig, How.xPath, getDirectionLinkLocator);
		String getDirectionLink = Element.getAttribute(testConfig, getDirectionElement, "href", "Get direction link");
		Helper.compareContains(testConfig, "Get direction link", "goo", getDirectionLink);
		try {
			verifyURLStatus(getDirectionLink);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void verifyAllIncludedOptionsBehavior() {

		Browser.wait(testConfig, 2);
		String linkLocator = ".//button[contains(text(),'View all included')]";
		WebElement viewAllIncludedOptionselement = Element.getPageElement(testConfig, How.xPath, linkLocator);
		Element.click(testConfig, viewAllIncludedOptionselement, "View All Included Options button");
		Browser.wait(testConfig, 2);

		String modalLocator = ".//div[@id='print']";
		try {
			WebElement includedOptionsModal = Element.getPageElement(testConfig, How.xPath, modalLocator);
			if(includedOptionsModal.isDisplayed()) {
				testConfig.logPass("Verified clicking View All Included Options button opens modal successfully");
			} else {
				testConfig.logFail("Failure to verify that clicking View All Included Options button opens modal successfully");
			}
		} catch (Exception e) {
			testConfig.logFail("Failure to verify that clicking View All Included Options button opens modal successfully");
		}

		String headingLocator = ".//div[@id='print']//h2";
		WebElement modalHeading = Element.getPageElement(testConfig, How.xPath, headingLocator);
		Helper.compareEquals(testConfig, "Modal heading", "Included Options", modalHeading.getText());
		Browser.wait(testConfig, 1);

		String printPageLocator = ".//div[contains(@class,'border-brp-blue-100')]/span[text()='Print Page']";
		WebElement printPage = Element.getPageElement(testConfig, How.xPath, printPageLocator);
		Element.click(testConfig, printPage, "Print page button");
		Browser.wait(testConfig, 2);

		try {
			Robot r = new Robot();
			r.keyPress(KeyEvent.VK_ESCAPE);
			r.keyRelease(KeyEvent.VK_ESCAPE);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}

		Browser.wait(testConfig, 1);
		WebElement modalCrossBtn = Element.getPageElement(testConfig, How.xPath, ".//div[@id='print']//button[2]");
		Element.click(testConfig, modalCrossBtn, "Cross button over Included Options modal form");
		Browser.wait(testConfig, 2);
		modalLocator = ".//div[@id='print']";
		try {
			WebElement includedOptionsModal = Element.getPageElement(testConfig, How.xPath, modalLocator);
			if(includedOptionsModal.isDisplayed()) {
				testConfig.logFail("Failed to verify that clicking cross button closes View All Included Options modal successfully");
			} else {
				testConfig.logPass("Verified that clicking cross button closes View All Included Options modal successfully");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified that clicking cross button closes View All Included Options modal successfully");
		}
	}

	public QMIPage verifySimilarHomesSectionDetails(String title, String subtitle) {

		String titleLocator = ".//div[@id='similar-homes']//div[contains(@class,'col-span-3')]//div[not(contains(@class,'justify-between'))]/div[contains(@class,'3xl')]";
		String subtitleLocator = ".//div[@id='similar-homes']//div[contains(@class,'col-span-3')]//div[not(contains(@class,'justify-between'))]/h3[contains(@class,'3xl')]";

		WebElement titleOnPageElement = Element.getPageElement(testConfig, How.xPath, titleLocator);
		WebElement subtitleOnPageElement = Element.getPageElement(testConfig, How.xPath, subtitleLocator);

		Helper.compareEquals(testConfig, "Section title", title, titleOnPageElement.getText().trim());
		Helper.compareEquals(testConfig, "Section subtitle", subtitle, subtitleOnPageElement.getText().trim());

		//String similarQMINameLoc = "(.//div[@id='similar-homes']//div[contains(@class,'relative')]/div[contains(@class,'slide')]//div[contains(@class,'description')])[1]";
		String similarQMINameLoc = "(.//div[@id='similar-homes']//div[contains(@class,'card-title')])[1]/span";
		WebElement unitNoElement = Element.getPageElement(testConfig, How.xPath, similarQMINameLoc);

		///String addressLoc = "(.//div[@id='similar-homes']//div[contains(@class,'relative')]/div[contains(@class,'slide')]//div[contains(@class,'font-medium')]/div)[1]";
		String addressLoc = "(.//div[@id='similar-homes']//div[contains(@class,'card-subtitle')])[1]/span";
		WebElement addressElement = Element.getPageElement(testConfig, How.xPath, addressLoc);

		String completeQMIName = addressElement.getText().split(",")[0] + " " + unitNoElement.getText().replace("#", "");
		String finalVal = "";
		String[] array = unitNoElement.getText().split(",");
		if(array.length == 2) {
			finalVal = array[0].concat(array[1]);
		} else {
			finalVal = array[0];
		}

		testConfig.putRunTimeProperty("SimilarHomeName", finalVal.trim());

		Element.click(testConfig, unitNoElement, completeQMIName + " similar home card");
		Set<String> windowHandle = testConfig.driver.getWindowHandles();
		testConfig.driver.switchTo().window(getLastElement(windowHandle));
		
		testConfig.logComment("Switching to the new window opened...");
		
		return new QMIPage(testConfig);
	}

	public void verifyRedirectionToSimilarHomePage(String qmiName) {

		String qmiNameLocator = ".//button[contains(@class,'whitespace-nowrap')][contains(@class,'border-brp-blue-gray-200')]";
		WebElement qmiNameOverPage = Element.getPageElement(testConfig, How.xPath, qmiNameLocator);
		String qmiNamePage = Element.getText(testConfig, qmiNameOverPage, "QMI name over page");
		Helper.compareEquals(testConfig, "Redirection being done to correct community", qmiName, qmiNamePage);
	}

	public void verifyQMIStatusFrontend(String expectedStatus) {
		// TODO Auto-generated method stub

	}

	public void verifyProductGallerySection() {

		String imageLoc = ".//section[@id='gallery-teaser']//li[contains(@class,'_slide')][not(contains(@id,'clone'))]/img";
		List<WebElement> allImages = Element.getListOfElements(testConfig, How.xPath, imageLoc);

		if(allImages.size() > 0) {
			testConfig.logPass("Verified getting " + allImages.size() + " images displaying under Gallery Teaser section");
			Element.click(testConfig, allImages.get(0), "First image under Gallery Teaser section");
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
		} else {
			testConfig.logFail("Failed to verify any image displaying under Gallery Teaser section");
		}
	}

	public void verifyContentOverPricingBreakdownModal(String[] expectedErrors) {

		String fields[] = {"Interest Rate", "Down Payment"};
		String qmiNameLoc = "h1.phps-font-semibold";
		WebElement qmiName = Element.getPageElement(testConfig, How.css, qmiNameLoc);
		String expectedQMIName = Element.getText(testConfig, qmiName, "QMI Name");

		String planNameLoc = ".//p[text()='Plan:']/parent::div/a";
		WebElement planName = Element.getPageElement(testConfig, How.xPath, planNameLoc);
		String expectedPlanName = Element.getText(testConfig, planName, "Plan Name");

		String expectedNeighborhoodName = testConfig.getRunTimeProperty("NeighborhoodName");
		String communityNameLoc = ".//p[text()='Community:']/parent::div/a";
		WebElement communityName = Element.getPageElement(testConfig, How.xPath, communityNameLoc);
		String expectedCommName = Element.getText(testConfig, communityName, "Community Name") + " Community";

		String expectedCompleteAddress = expectedQMIName.split(",")[0].trim() + ", " + "San Marcos, TX, 78666";

		String pricingBreakdownLinkLoc = "(.//div[contains(@class,' prc-text-brds-v1-cta-link-teal-700')]//button[contains(@class,'link primary-underlined md')][1]/span)[1]";
		WebElement pricingBreakdownElement = Element.getPageElement(testConfig, How.xPath, pricingBreakdownLinkLoc);
		Element.clickThroughJS(testConfig, pricingBreakdownElement, "Pricing breakdown link");
		Browser.wait(testConfig, 1);

		String expectedNeighborhoodCommInfo = expectedNeighborhoodName + " · " + expectedCommName;

		String neighborhoodAndCommNameLoc = "(.//section[@name='pricing-breakdown']/header/span)[1]";
		WebElement neighborhoodAndCommName = Element.getPageElement(testConfig, How.xPath, neighborhoodAndCommNameLoc);
		Helper.compareEquals(testConfig, "Neighborhood and Community name over modal", expectedNeighborhoodCommInfo, neighborhoodAndCommName.getText());

		String planNameTextLoc = ".//section[@name='pricing-breakdown']/header/h2";
		WebElement planText = Element.getPageElement(testConfig, How.xPath, planNameTextLoc);
		Helper.compareContains(testConfig, "Plan Name over modal", expectedPlanName, planText.getText());

		String addressTextLoc = "(.//section[@name='pricing-breakdown']/header/span)[2]";
		WebElement addressText = Element.getPageElement(testConfig, How.xPath, addressTextLoc);
		Helper.compareEquals(testConfig, "Address content over modal", expectedCompleteAddress, addressText.getText().trim());
		Browser.wait(testConfig, 2);

		WebElement interestRateInput = Element.getPageElement(testConfig, How.xPath, ".//h3[contains(text(),'Interest Rate')]/..//input");
		interestRateInput.sendKeys(Keys.chord(Keys.CONTROL,"a",Keys.BACK_SPACE));
		interestRateInput.sendKeys(Keys.BACK_SPACE);
		Browser.wait(testConfig, 1);

		WebElement downPaymentInput = Element.getPageElement(testConfig, How.xPath, ".//h3[contains(text(),'Down Payment')]/..//input");
		downPaymentInput.sendKeys(Keys.chord(Keys.CONTROL,"a",Keys.DELETE));
		downPaymentInput.sendKeys(Keys.BACK_SPACE);
		Browser.wait(testConfig, 1);

		WebElement mortgageTermsDropdown = Element.getPageElement(testConfig, How.xPath, ".//h3[contains(text(),'Mortgage Term')]/../div");
		Element.click(testConfig, mortgageTermsDropdown, "Mortgage terms dropdown");
		Browser.wait(testConfig, 2);

		String mortgageTerm = "25 Years";
		List<WebElement> allYrOptions = Element.getListOfElements(testConfig, How.xPath, ".//ul[@role='listbox']/li/span");
		for (Iterator<WebElement> iterator = allYrOptions.iterator(); iterator.hasNext();) {
			WebElement webElement = (WebElement) iterator.next();
			String value = Element.getText(testConfig, webElement, "Year dropdown value");
			if(value.equals(mortgageTerm)) {
				Element.click(testConfig, webElement, mortgageTerm + " mortgage term");
				break;
			}
		}

		List<WebElement> errors = Element.getListOfElements(testConfig, How.css, "p.prc-text-red-500");
		for (int i = 0; i < errors.size(); i++) {
			Helper.compareEquals(testConfig, "Error when '" + fields[i] + "' is blank", expectedErrors[i],
					errors.get(i).getAttribute("innerText"));
		}

	}

	
	public void verifyPlanQMIDetailsSection(String[] differentPlanDetails) {

		String[] itemValues = { "Square Footage", "Bedrooms", "Bathrooms" };
		String sectionSVGLocator = ".//div[contains(@class,'text-xl')][contains(@class,'text-sm')]//*[name()='svg']";
		List<WebElement> allSVG = Element.getListOfElements(testConfig, How.xPath, sectionSVGLocator);

		for (int i = 0; i < allSVG.size(); i++) {
			if (allSVG.get(i).isDisplayed()) {
				testConfig.logPass("Verify getting SVG displaying for " + differentPlanDetails[i]);
			} else {
				testConfig.logFail("SVG not displaying for " + differentPlanDetails[i]);
			}
		}

		String planDetailsLoc = ".//div[contains(@class,'text-xl')][contains(@class,'text-sm')]//*[name()='svg']/../span[1]";
		String homeTypeLoc = ".//div[contains(@class,'text-xl')][contains(@class,'text-sm')]//*[name()='svg']/../div[contains(@class,'mr-3')]";

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
}
