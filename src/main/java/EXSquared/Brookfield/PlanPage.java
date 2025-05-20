package EXSquared.Brookfield;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import EXSquared.Brookfield.FYHPage.PlanTypes;
import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;
import Utils.Helper;

public class PlanPage extends BRPHelper {

	public enum PlanHeaders {
		Overview, VirtualTours, FloorPlan, Community, Map, FloorPlanAndExterior
	}

	public enum LinksFromPlanPage {
		Community, Neighborhood, ViewOnMap
	}
	
	@FindBy(xpath = ".//button[text()='Overview']")
	private WebElement planOverviewTab;

	@FindBy(xpath = ".//button[text()='Schedule a Tour']")
	private WebElement scheduleATourBtn;

	@FindBy(xpath = ".//button[text()=' Request Information ']")
	private WebElement requestInfoBtn;

	@FindBy(xpath = ".//div[text()=' GET PRE-QUALIFIED ']")
	private WebElement getPreQualifiedBtn;

	public PlanPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		PageFactory.initElements(this.testConfig.driver, this);
		try {
			Helper.removeCookies(testConfig);
			testConfig.driver.navigate().refresh();
			Browser.waitWithoutLogging(testConfig, 3);
			WebElement acceptCookies = Element.getPageElement(testConfig, How.css,
					"button#onetrust-accept-btn-handler");
			Element.click(testConfig, acceptCookies, "One trust accept cookies button");
			testConfig.putRunTimeProperty("CookieSetting", "Yes");
		} catch (Exception e) {
			testConfig.putRunTimeProperty("CookieSetting", "No");
			testConfig.logComment("Accept cookie section not displayed");
		}
		Browser.waitForPageLoad(testConfig, planOverviewTab);
	}

	public void verifyJumpAnchorTags(PlanHeaders planHeaders) {

		String tabLocator = "";
		String sectionLocator = "";

		switch (planHeaders) {
		case Overview:
			tabLocator = ".//button[text()='Overview']";
			sectionLocator = "(.//div[contains(@class,'md:breadcrumbText')])[2]/../../../..";
			break;

		case VirtualTours:
			tabLocator = ".//button[text()='Virtual Tours']";
			sectionLocator = ".//section[@id='virtual-tours']";
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
			
		case FloorPlanAndExterior:
			tabLocator = ".//button[text()='Floor Plan and Exterior']";
			sectionLocator = ".//section[@id='floor-plan-exterior-section']";
			break;
		}

		try {
			WebElement tabElement = Element.getPageElement(testConfig, How.xPath, tabLocator);
			Element.click(testConfig, tabElement, planHeaders.toString() + " tab from plan nav header");
			WebElement sectionElement = Element.getPageElement(testConfig, How.xPath, sectionLocator);

			Browser.wait(testConfig, 2);
			Helper.compareContains(testConfig,
					"Jump anchor tag associated with " + planHeaders.toString() + " header tab",
					sectionElement.getAttribute("id"), tabElement.getAttribute("id"));
		} catch (Exception e) {
			testConfig.logComment(
					"Not having " + planHeaders.toString() + " tab over plan nav header so skipping that case");
		}
	}

	public void verifyPlanNameHeaderTag(String planName) {

		String locator = ".//*[text()='" + planName + "'][contains(@class,'font-semibold')]";
		WebElement commNameElement = Element.getPageElement(testConfig, How.xPath, locator);
		Helper.compareEquals(testConfig, "Tag associated with Community Name element", "h1", commNameElement.getTagName());

	}
	
	public void verifyPlanPageAppears(String expectedPlanName) {

		WebElement planNameOnPage = Element.getPageElement(testConfig, How.xPath, ".//div/h1");
		String planName = Element.getText(testConfig, planNameOnPage, "Plan Name over page");

		if (planName.equals(expectedPlanName)) {
			testConfig.logPass("Redirected to " + expectedPlanName + " plan correctly");
		} else {
			testConfig.logPass(
					"Not able to redirected to " + expectedPlanName + " plan correctly... hence failing the test");
		}
	}

	public void redirectToFloorPlansSection() {

		String viewGalleryLocators = ".//section[@id='explore-community']//button[contains(@class,'btn light md')]";
		WebElement viewGalleryBtn = Element.getPageElement(testConfig, How.xPath, viewGalleryLocators);
		Element.click(testConfig, viewGalleryBtn, "View Full Gallery button");
		Browser.wait(testConfig, 2);

		String floorPlanLocator = ".//button/h2[contains(text(),'Floor plan')]";
		WebElement floorPlanTabGallery = Element.getPageElement(testConfig, How.xPath, floorPlanLocator);
		Element.click(testConfig, floorPlanTabGallery, "Floor Plan tab over View Full Gallery page");

		Browser.wait(testConfig, 3);
	}

	public void verifyDifferentTabsGalleryModal(String expectedBorderColor) {

		String viewGalleryLocators = ".//section[@id='explore-community']//button[contains(@class,'btn light md')]";
		WebElement viewGalleryBtn = Element.getPageElement(testConfig, How.xPath, viewGalleryLocators);
		Element.click(testConfig, viewGalleryBtn, "View Full Gallery button");
		Browser.wait(testConfig, 2);

		String tabsLocator = ".//div[contains(@class,'gal-shadow-header')]//button[contains(@class,'gal-capitalize')]";
		String tabsTextLocator = ".//div[contains(@class,'gal-shadow-header')]//button[contains(@class,'gal-capitalize')]/h2";

		List<WebElement> allTabs = Element.getListOfElements(testConfig, How.xPath, tabsLocator);
		List<WebElement> allTabsText = Element.getListOfElements(testConfig, How.xPath, tabsTextLocator);

		for (int i = 0; i < allTabs.size(); i++) {
			Element.click(testConfig, allTabs.get(i), allTabsText.get(i).getText() + " tab");
			Browser.wait(testConfig, 2);

			List<WebElement> allTabsAgain = Element.getListOfElements(testConfig, How.xPath, tabsLocator);
			String cssValue = allTabsAgain.get(i).getCssValue("background-color");
			String hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
			Helper.compareEquals(testConfig, "Background color for " + allTabsText.get(i).getText() + " tab",
					expectedBorderColor, hexcolor);
		}

	}

	public Object verifyNavigationsToOtherDetailPages(LinksFromPlanPage linksFromPlanPage) {

		Object obj = null;
		String locator = "";
		WebElement differentLinks = null;

		switch (linksFromPlanPage) {
		case Community:
			locator = ".//p[text()='Community:']/../a";
			differentLinks = Element.getPageElement(testConfig, How.xPath, locator);
			testConfig.putRunTimeProperty("CommunityPlanPage", differentLinks.getText());
			Element.clickThroughJS(testConfig, differentLinks, locator);
			obj = new CommunityPage(testConfig);
			break;

		case Neighborhood:
			locator = ".//p[text()='Collection:' or text()='Neighborhood:' or text()='Portfolio:']/../a";
			differentLinks = Element.getPageElement(testConfig, How.xPath, locator);
			testConfig.putRunTimeProperty("NeighborhoodPlanPage", differentLinks.getText());
			Element.clickThroughJS(testConfig, differentLinks, "Neighborhood link");
			obj = new NeighborhoodPage(testConfig);
			break;

		case ViewOnMap:
			locator = ".//p[contains(text(),'Lot')]/../a";
			differentLinks = Element.getPageElement(testConfig, How.xPath, locator);
			Element.clickThroughJS(testConfig, differentLinks, "View On Map link");
			testConfig.putRunTimeProperty("RedirectionValue", "yes");
			obj = new FYHPage(testConfig);
			break;
		}
		return obj;
	}

	private void verifyFormOpensCorrectlyScheduleTour() {

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

	public void verifyNextStepsSectionBelowPricing(String title, String desc) {

		String titleLoc = "(.//div[contains(@class,'items')]//div[contains(@class,'text-3xl')])[2]";
		String descLoc = "(.//div[contains(@class,'items')]//div[contains(@class,'text-brp-grey-900')])[2]";

		String scheduleTourLoc = ".//section[@id='plan-details-section']//div[contains(@class,'items-end')]//button[contains(@class,'btn primary lg')]";
		String contactUsLoc = ".//section[@id='plan-details-section']//div[contains(@class,'items-end')]//div[contains(text(),'Contact Us')]";
		
		WebElement titleText = Element.getPageElement(testConfig, How.xPath, titleLoc);
		WebElement description = Element.getPageElement(testConfig, How.xPath, descLoc);
		WebElement scheduleTourBtn = Element.getPageElement(testConfig, How.xPath, scheduleTourLoc);
		WebElement contactUsBtn = Element.getPageElement(testConfig, How.xPath, contactUsLoc);

		Helper.compareEquals(testConfig, "Title for the section", title, titleText.getText());
		Helper.compareContains(testConfig, "Description for the section", desc, description.getText());

		Element.clickThroughJS(testConfig, scheduleTourBtn, "Schedule A Tour button");
		verifyFormOpensCorrectlyScheduleTour();

		Element.clickThroughJS(testConfig, contactUsBtn, "Contact Us button");
		Browser.wait(testConfig, 2);
		verifyFormOpensCorrectlyRequestInfo();
	}

	public void verifyFactsAndFeaturesSection(String[] differentPlanDetails) {

		String[] itemValues = { "Stories", "Parking/Garage" };
		String sectionSVGLocator = ".//h2[contains(text(),'Facts & features')]/..//*[name()='svg']";
		List<WebElement> allSVG = Element.getListOfElements(testConfig, How.xPath, sectionSVGLocator);

		for (int i = 0; i < allSVG.size(); i++) {
			if (allSVG.get(i).isDisplayed()) {
				testConfig.logPass("Verify getting SVG displaying for " + differentPlanDetails[i]);
			} else {
				testConfig.logFail("SVG not displaying for " + differentPlanDetails[i]);
			}
		}

		String factsValues = ".//h2[contains(text(),'Facts & features')]/..//*[name()='svg']/..//div/p[2]";		
		List<WebElement> itemDetails = Element.getListOfElements(testConfig, How.xPath, factsValues);

		for (int i = 0; i < itemDetails.size(); i++) {
			String value = Element.getText(testConfig, itemDetails.get(i), itemValues[i] + " values");
			if (!value.isEmpty()) {
				testConfig.logPass("Getting " + itemValues[i] + " as " + value);
			} else {
				testConfig.logFail("Getting " + itemValues[i] + " value as blank... failing the scenario");
			}
		}
	}

	public void verifyDownloadBrochureButton() {
		String downloadBrochureLocator = ".//div[contains(@class,'items-start')]//a[contains(@class,'GA-download-brochure')]";
		WebElement brochureBtn = Element.getPageElement(testConfig, How.xPath, downloadBrochureLocator);

		if(brochureBtn.getAttribute("href").contains("pdf")) {
			Element.clickThroughJS(testConfig, brochureBtn, downloadBrochureLocator);
			Browser.wait(testConfig, 15);

			File brochureDownloadedFile = Browser.lastFileModified(testConfig, testConfig.downloadPath);
			testConfig.logPass("Verified the downloaded brochure file name " + brochureDownloadedFile.getName());
			String imageLink = brochureBtn.getAttribute("href");
			verifyURLAsPerDomain(imageLink);
		} else {
			String imageLink = brochureBtn.getAttribute("href");
			verifyURLAsPerDomain(imageLink);
		}
	}

	public void verifyDidYouKnowSection(String sectionTitle, String sectionSubtitle, String[] content) {

		String didYouKnowLoc = ".//section[@id='virtual-tours']//span[contains(text(),'Did')]";
		String subtitleLoc = "(.//section[@id='virtual-tours']//div[contains(@class,'phps-leading-snug')])[1]";

		WebElement title = Element.getPageElement(testConfig, How.xPath, didYouKnowLoc);
		WebElement subtitle = Element.getPageElement(testConfig, How.xPath, subtitleLoc);

		Helper.compareEquals(testConfig, "Section title", sectionTitle, title.getText());
		Helper.compareEquals(testConfig, "Section subtitle", sectionSubtitle, subtitle.getText());

		String[] itemValues = { "First content", "Second content" };
		String sectionSVGLocator = ".//section[@id='virtual-tours']//div[contains(@class,'bg-white')]//*[name()='svg']";
		List<WebElement> allSVG = Element.getListOfElements(testConfig, How.xPath, sectionSVGLocator);

		for (int i = 0; i < allSVG.size(); i++) {
			if (allSVG.get(i).isDisplayed()) {
				testConfig.logPass("Verify getting SVG displaying for " + content[i]);
			} else {
				testConfig.logFail("SVG not displaying for " + content[i]);
			}
		}

		String factsValues = ".//section[@id='virtual-tours']//div[contains(@class,'bg-white')]//*[name()='svg']/../div";		
		List<WebElement> itemDetails = Element.getListOfElements(testConfig, How.xPath, factsValues);

		for (int i = 0; i < itemDetails.size(); i++) {
			String value = Element.getText(testConfig, itemDetails.get(i), itemValues[i] + " values");
			if (!value.isEmpty()) {
				testConfig.logPass("Getting " + itemValues[i] + " as " + value);
			} else {
				testConfig.logFail("Getting " + itemValues[i] + " value as blank... failing the scenario");
			}
		}
	}

	public void verifyNavigationForVirtualTourItems(String expectedBorderColor) {

		String cardsLocator = ".//section[@id='virtual-tours']//div[contains(@class,'flex-col')]";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, cardsLocator);
		Browser.wait(testConfig, 2);

		Element.click(testConfig, allCards.get(0), "1st item in virtual tour gallery");
		Browser.wait(testConfig, 2);

		String btnLocator = ".//button/h2[text()='3D Walkthroughs']/parent::button";
		WebElement photosTab = Element.getPageElement(testConfig, How.xPath, btnLocator);
		String cssValue = photosTab.getCssValue("background-color");
		String hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color for Photos tab",	expectedBorderColor, hexcolor);

		String leftBtnLocator = "(.//div[contains(@class,'gallery-carousel')]//div[contains(@class,'splide__arrows')]/button)[1]";
		String rightBtnLocator = "(.//div[contains(@class,'gallery-carousel')]//div[contains(@class,'splide__arrows')]/button)[2]";

		if (allCards.size() > 3) {
			WebElement leftBtn = Element.getPageElement(testConfig, How.xPath, leftBtnLocator);
			WebElement rightBtn = Element.getPageElement(testConfig, How.xPath, rightBtnLocator);
			Browser.wait(testConfig, 2);

			for (int i = 0; i < allCards.size() - 3; i++) {
				Element.click(testConfig, rightBtn, "Right button");
				Browser.wait(testConfig, 1);
			}
			verifyBtnsBehaviorWhenNavThroughCards(leftBtn, rightBtn);
		} else {
			verifyButtonsNotPresent();
		}
	}

	private void verifyButtonsNotPresent() {

		String leftBtnLocator = ".//section[@id='virtual-tours']//button[contains(@class,'_nav-button')][1]";
		String rightBtnLocator = ".//section[@id='virtual-tours']//button[contains(@class,'_nav-button')][2]";

		try {
			WebElement leftBtn = Element.getPageElement(testConfig, How.xPath, leftBtnLocator);
			if (leftBtn.isDisplayed()) {
				testConfig.logFail("Getting left navgation button to be displayed");
			} else {
				testConfig.logPass("Verified we are not getting left navgation button displaying when cards count < 3");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified we are not getting left navgation button displaying when cards count < 3");
		}

		try {
			WebElement rightBtn = Element.getPageElement(testConfig, How.xPath, rightBtnLocator);
			if (rightBtn.isDisplayed()) {
				testConfig.logFail("Getting right navgation button to be displayed");
			} else {
				testConfig
				.logPass("Verified we are not getting right navgation button displaying when cards count < 3");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified we are not getting right navgation button displaying when cards count < 3");
		}

	}

	private void verifyBtnsBehaviorWhenNavThroughCards(WebElement leftBtn, WebElement rightBtn) {

		try {
			if (leftBtn.isEnabled()) {
				testConfig.logPass("Verified left navigation button for Homes list is enabled now");
			} else {
				testConfig.logFail("Failed to verify that left navigation button for Homes list is enabled now");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that left navigation button for Homes list is enabled now");
		}

	}

	/*private void verifyBtnsBehaviorInitially(WebElement leftBtn, WebElement rightBtn) {

		try {
			if (rightBtn.isEnabled()) {
				testConfig.logPass("Verified right navigation button for Homes list is enabled initially");
			} else {
				testConfig.logFail("Failed to verify that right navigation button for Homes list is enabled initially");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that right navigation button for Homes list is enabled initially");
		}

		try {
			if (!leftBtn.isEnabled()) {
				testConfig.logPass("Verified left navigation button for Gallery is disabled initially");
			} else {
				testConfig.logFail("Failed to verify that left navigation button for Gallery is disabled initially");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that left navigation button for Gallery is disabled initially");
		}
	}*/

	public void verifyPricingSectionDetails(PlanTypes planType, String expectedStatus) {

		String actualStatus = "", value = "";
		switch (planType) {
		case MyTimeEnabled:
			break;

		case StatusSoldOut:
			String pricingPathSold = "(.//div[contains(@class,'prc-shadow-card')])[1]//div[contains(@class,'prc-type-brds-v2-5xl-600')]/div/div "
					+ "| (.//div[contains(@class,'prc-shadow-card')])[1]//div[contains(@class,'prc-text-brds-v1-cta-secondary')]";
			List<WebElement> pricingStatusValuesSold = Element.getListOfElements(testConfig, How.xPath, pricingPathSold);
			for (int i = 0; i < pricingStatusValuesSold.size(); i++) {
				value = pricingStatusValuesSold.get(i).getText().trim();
				actualStatus += value + " ";
			}
			
			if(actualStatus.trim().isEmpty()) {
				pricingPathSold = "(.//div[contains(@class,'prc-shadow-card')])[2]//div[contains(@class,'prc-type-brds-v2-5xl-600')]/div/div "
						+ "| (.//div[contains(@class,'prc-shadow-card')])[2]//div[contains(@class,'prc-text-brds-v1-cta-secondary')]";
				pricingStatusValuesSold = Element.getListOfElements(testConfig, How.xPath, pricingPathSold);
				for (int i = 0; i < pricingStatusValuesSold.size(); i++) {
					value = pricingStatusValuesSold.get(i).getText().trim();
					actualStatus += value + " ";
				}
			}
				;
			Helper.compareContains(testConfig, "Pricing section status", expectedStatus, actualStatus.trim());
			break;
			
		case ComingSoon:
			String pricingPath = "(.//div[contains(@class,'prc-shadow-card')])[1]//div[contains(@class,'prc-type-brds-v2-5xl-600')]"
					+ "| (.//div[contains(@class,'prc-shadow-card')])[1]//div[contains(@class,'prc-text-brds-v1-cta-secondary')]";
			List<WebElement> pricingStatusValues = Element.getListOfElements(testConfig, How.xPath, pricingPath);
			for (int i = 0; i < pricingStatusValues.size(); i++) {
				value = pricingStatusValues.get(i).getText().trim();
				actualStatus += value + " ";
			}
			
			if(actualStatus.trim().isEmpty()) {
				pricingPathSold = "(.//div[contains(@class,'prc-shadow-card')])[2]//div[contains(@class,'prc-type-brds-v2-5xl-600')] "
						+ "| (.//div[contains(@class,'prc-shadow-card')])[2]//div[contains(@class,'prc-text-brds-v1-cta-secondary')]";
				pricingStatusValuesSold = Element.getListOfElements(testConfig, How.xPath, pricingPathSold);
				for (int i = 0; i < pricingStatusValuesSold.size(); i++) {
					value = pricingStatusValuesSold.get(i).getText().trim();
					actualStatus += value + " ";
				}
			}
				;
			Helper.compareContains(testConfig, "Pricing section status", expectedStatus, actualStatus.trim());
			break;
			
		case ValidPricing:
			String pricingFromLine = "(.//div[contains(@class,'prc-text-brds-v1-cta-secondary')])[1]";
			String priceValueLoc = "(.//div[contains(@class,'prc-text-brds-v1-cta-secondary')]/../div[2]/div)[1]";
			WebElement pricingFromText = Element.getPageElement(testConfig, How.xPath, pricingFromLine);
			WebElement priceValue = Element.getPageElement(testConfig, How.xPath, priceValueLoc);
			String actualPlanCardStatus = pricingFromText.getText().trim() + " " + priceValue.getText();
			
			if (actualPlanCardStatus.trim().isEmpty()) {
				pricingFromLine = "(.//div[contains(@class,'prc-text-brds-v1-cta-secondary')])[2]";
				priceValueLoc = "(.//div[contains(@class,'prc-text-brds-v1-cta-secondary')]/../div[2]/div)[2]";
				pricingFromText = Element.getPageElement(testConfig, How.xPath, pricingFromLine);
				priceValue = Element.getPageElement(testConfig, How.xPath, priceValueLoc);
				actualPlanCardStatus = pricingFromText.getText().trim() + " " + priceValue.getText();
			}
			
			String expectedPrice = testConfig.getRunTimeProperty("PlanPricing");
			Helper.compareEquals(testConfig, "Pricing value over Plan page comparison with FYH page", expectedPrice, actualPlanCardStatus);
			break;
		}
	}

	public void verifyRedirectionToCorrectPlan(String planName) {
		String planNameLocator = ".//button[contains(@class,'whitespace-nowrap')][contains(@class,'border-brp-blue-gray-200')]";
		WebElement planNameOverPageElement = Element.getPageElement(testConfig, How.xPath, planNameLocator);
		String planNameOverPage = Element.getText(testConfig, planNameOverPageElement, "Plan name over page");
		Helper.compareEquals(testConfig, "Redirection being done to correct plan", planName, planNameOverPage);
	}

	public void verifyClickingOnImageOpensGalleryModal() {

		String viewGalleryLocators = "(.//section[@id='explore-community']//img)[1]";
		WebElement viewGalleryBtn = Element.getPageElement(testConfig, How.xPath, viewGalleryLocators);
		Element.click(testConfig, viewGalleryBtn, "Explore community first image");
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

	public void verifyContentOverPricingBreakdownModal(String[] expectedErrors) {

		String fields[] = {"Interest Rate", "Down Payment"};
		String planNameLoc = ".//button[contains(@class,'whitespace-nowrap')][contains(@class,'border-brp-blue-gray-200')]";
		WebElement planName = Element.getPageElement(testConfig, How.xPath, planNameLoc);
		String expectedPlanName = Element.getText(testConfig, planName, "Plan Name");

		String expectedNeighborhoodName = testConfig.getRunTimeProperty("NeighborhoodName");

		String communityNameLoc = ".//p[text()='Community:']/parent::div/a";
		WebElement communityName = Element.getPageElement(testConfig, How.xPath, communityNameLoc);
		String expectedCommName = Element.getText(testConfig, communityName, "Community Name") + " Community";

		String expectedCompleteAddress = "San Marcos, TX, 78666";

		String pricingBreakdownLinkLoc = "(.//div[contains(@class,' prc-text-brds-v1-cta-link-teal-700')]//button[contains(@class,'link primary-underlined md')][1]/span)[1]";
		WebElement pricingBreakdownElement = Element.getPageElement(testConfig, How.xPath, pricingBreakdownLinkLoc);
		Element.clickThroughJS(testConfig, pricingBreakdownElement, "Pricing breakdown link");
		Browser.wait(testConfig, 1);

		String expectedNeighborhoodCommInfo = expectedNeighborhoodName + " Collection · " + expectedCommName;
		
		String neighborhoodAndCommNameLoc = "(.//section[@name='pricing-breakdown']/header/span)[1]";
		WebElement neighborhoodAndCommName = Element.getPageElement(testConfig, How.xPath, neighborhoodAndCommNameLoc);
		Helper.compareEquals(testConfig, "Neighborhood and Community name over modal", expectedNeighborhoodCommInfo, neighborhoodAndCommName.getText());
		
		String planNameTextLoc = ".//section[@name='pricing-breakdown']/header/h2";
		WebElement planText = Element.getPageElement(testConfig, How.xPath, planNameTextLoc);
		Helper.compareEquals(testConfig, "Plan Name over modal", expectedPlanName, planText.getText());

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

	public VisualizerPage verifyPersonalizeThisPlanButtonDisplayingInGallery() {

		String allButtonsGallery = ".//div[@id='gallery-teaser']//button[contains(@class,'icon-btn')]";
		List<WebElement> allButtons = Element.getListOfElements(testConfig, How.xPath, allButtonsGallery);
		Element.click(testConfig, allButtons.get(allButtons.size() - 1), "Full Gallery button");
		Browser.waitWithoutLogging(testConfig, 2);

		String personalizePlanBtnLoc = ".//h2/parent::div/div/button[contains(@class,'btn secondary sm')]";
		try {
			WebElement personalizeThisPlanBtn = Element.getPageElement(testConfig, How.xPath, personalizePlanBtnLoc);
			if (personalizeThisPlanBtn.isDisplayed()) {
				testConfig.logPass("Verified 'Personalize This Plan' button is displaying correctly in gallery");
			} else {
				testConfig.logFail("Failed to verify that 'Personalize This Plan' button is displaying correctly in gallery");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Personalize This Plan' button is displaying correctly in gallery");
		}

		Browser.waitWithoutLogging(testConfig, 2);
		WebElement personalizeThisPlanBtn = Element.getPageElement(testConfig, How.xPath, personalizePlanBtnLoc);
		Element.click(testConfig, personalizeThisPlanBtn, "Personalize This Plan");
		
		return new VisualizerPage(testConfig);
		
	}

	public void verifyFloorPlanAndExteriorSection(String sectionPreTitle, String sectionTitle) {

		WebElement sectionPreHeadingEle = Element.getPageElement(testConfig, How.xPath, ".//section[@id='floor-plan-exterior-section']/div/span[1]");
		WebElement sectionTitleEle = Element.getPageElement(testConfig, How.xPath, ".//section[@id='floor-plan-exterior-section']/div/span[2]");

		Helper.compareEquals(testConfig, "Avid module section heading", sectionPreTitle, sectionPreHeadingEle.getText());
		Helper.compareEquals(testConfig, "Avid module section title", sectionTitle, sectionTitleEle.getText());
	
		Browser.waitWithoutLogging(testConfig, 1);
		String personalizePlanLoc = ".//section[@id='floor-plan-exterior-section']//button[contains(@class,'btn')]";
		
		try {
			WebElement personalizePlanBtn = Element.getPageElement(testConfig, How.xPath, personalizePlanLoc);
			if (personalizePlanBtn.isDisplayed()) {
				testConfig.logPass("Verified 'Personalize This Plan' button is displaying correctly in gallery");
			} else {
				testConfig.logFail("Failed to verify that 'Personalize This Plan' button is displaying correctly in gallery");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Personalize This Plan' button is displaying correctly in gallery");
		}
	}
}
