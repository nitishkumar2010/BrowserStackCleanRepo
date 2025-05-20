package EXSquared.Brookfield;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;
import Utils.Helper;

public class MyTimeTourPage extends BRPHelper {

	public enum MyTimeTabs {
		AllResults, MoveInReady, Models
	}

	@FindBy(xpath = ".//h1[text()='Find Homes to Tour Now']")
	private WebElement chooseLocationHeading;

	public MyTimeTourPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		PageFactory.initElements(this.testConfig.driver, this);
		Browser.waitForPageLoad(testConfig, chooseLocationHeading);
	}

	public void verifyTopSectionAndAccordianOverPage() throws UnsupportedEncodingException {

		String logoLocator = ".//img[@alt='myTime']";
		String sectionTitleLocator = ".//h2[contains(@class,'text-brds-v2-navy-80')]";
		String sectionDescriptionLocator = ".//div[contains(@class,'text-brds-v2-navy-80')][contains(@class,'text-xl')]";
		String seeHowItWorksLoc = ".//button[@onclick='handleDropdownClick()']/span";
		String videoIframe = "(.//iframe[@aria-label='myTime video'])[1]";

		WebElement logo = Element.getPageElement(testConfig, How.xPath, logoLocator);
		WebElement sectionTitle = Element.getPageElement(testConfig, How.xPath, sectionTitleLocator);
		WebElement sectionDescription = Element.getPageElement(testConfig, How.xPath, sectionDescriptionLocator);
		WebElement seeHowItWorks = Element.getPageElement(testConfig, How.xPath, seeHowItWorksLoc);
		WebElement video = Element.getPageElement(testConfig, How.xPath, videoIframe);

		if(sectionTitle.getText().isEmpty()) {
			testConfig.logFail("Not getting section title displaying.. failing the scenario");
		} else {
			testConfig.logPass("Verified section title is displaying as: " + sectionTitle.getText());
		}

		if(sectionDescription.getText().isEmpty()) {
			testConfig.logFail("Not getting section description displaying.. failing the scenario");
		} else {
			testConfig.logPass("Verified section description is displaying as: " + sectionDescription.getText());
		}

		if(seeHowItWorks.getText().isEmpty()) {
			testConfig.logFail("Not getting see how it works text displaying.. failing the scenario");
		} else {
			testConfig.logPass("Verified accordian section text is displaying as: " + seeHowItWorks.getText());
		}

		String logoUrl = Element.getAttribute(testConfig, logo, "src", "myTime logo");

		if (!logoUrl.isEmpty()) {
			testConfig.logPass("Getting logo image source displaying for the card as " + logoUrl);
			verifyURLAsPerDomain(logoUrl);
		} else {
			testConfig.logFail("Getting image source displaying for community as blank... failing the scenario");
		}

		String brightcoveUrl = Element.getAttribute(testConfig, video, "src", "myTime video");

		try {
			verifyURLStatus(brightcoveUrl);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		verifyAccordianSection(seeHowItWorks);
	}

	private void verifyAccordianSection(WebElement seeHowItWorks) {

		Element.click(testConfig, seeHowItWorks, "See How It Works link");
		Browser.wait(testConfig, 2);

		String allSubItemsHeadingLoc = ".//div[contains(@class,'bg-blue-50')]//div[contains(@class,'text-2xl')]";
		String allSubItemsDescriptionLoc = ".//div[contains(@class,'bg-blue-50')]//div[contains(@class,'text-2xl')]/parent::div//div[contains(@class,'leading-8')]";

		List<WebElement> allItems = Element.getListOfElements(testConfig, How.xPath, allSubItemsDescriptionLoc);

		for (int i = 0; i < allItems.size(); i++) {

			testConfig.logComment("****************** Looking for Data for Item No " + (i + 1) + " ******************");
			List<WebElement> allSubItemsHeading = Element.getListOfElements(testConfig, How.xPath, allSubItemsHeadingLoc);
			List<WebElement> allSubItemsDesc = Element.getListOfElements(testConfig, How.xPath, allSubItemsDescriptionLoc);

			if(allSubItemsHeading.get(i).getText().isEmpty()) {
				testConfig.logFail("Not getting Sub Item heading displaying.. failing the scenario");
			} else {
				testConfig.logPass("Verified Sub Item heading is displaying as: " + allSubItemsHeading.get(i).getText());
			}

			if(allSubItemsDesc.get(i).getText().isEmpty()) {
				testConfig.logFail("Not getting Sub Item description displaying.. failing the scenario");
			} else {
				testConfig.logPass("Verified Sub Item description is displaying as: " + allSubItemsDesc.get(i).getText());
			}
		}

		Element.click(testConfig, seeHowItWorks, "See How It Works link");
		verifyAccordianNotDisplaying();
	}

	private void verifyAccordianNotDisplaying() {

		String subItemsHeadingLoc = "(.//div[contains(@class,'bg-blue-50')]//div[contains(@class,'text-2xl')])[1]";

		try {
			WebElement subItemHeading = Element.getPageElement(testConfig, How.xPath, subItemsHeadingLoc);
			if(subItemHeading.isDisplayed()) {
				testConfig.logFail("Failed to verify that accordian is closed now");
			} else {
				testConfig.logPass("Verified accordian is closed now after clicking the 'See How It Works' link");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified accordian is closed now after clicking the 'See How It Works' link");
		}
	}
	
	public void setZoomLevel(int count) {
		String zoomInBtn = "button.mapboxgl-ctrl-zoom-in";
		WebElement zoomIn = Element.getPageElement(testConfig, How.css, zoomInBtn);

		for (int i = 0; i < count; i++) {
			Element.click(testConfig, zoomIn, "Zoom-in button click: " + (i + 1));
			Browser.waitWithoutLogging(testConfig, 2);
		}
	}

	public void verifyNoResultsState(MyTimeTabs myTimeTabs, String noResultsMessage, String trySearch) {
		
		String tabsLabelLocator = "";
		WebElement tabs = null;
		
		switch (myTimeTabs) {
		case AllResults:
			break;

		case Models:
			tabsLabelLocator = "(.//div[@role='tablist']/button/span)[3]";
			tabs = Element.getPageElement(testConfig, How.xPath, tabsLabelLocator);
			Element.click(testConfig, tabs, "Models tab");
			Browser.wait(testConfig, 2);
			break;
			
		case MoveInReady:
			tabsLabelLocator = "(.//div[@role='tablist']/button/span)[2]";
			tabs = Element.getPageElement(testConfig, How.xPath, tabsLabelLocator);
			Element.click(testConfig, tabs, "Move In Ready tab");
			Browser.wait(testConfig, 2);
			break;
		}
		
		String noResultsMsg = ".//h2[contains(text(),'No results')]";
		WebElement noResults = Element.getPageElement(testConfig, How.xPath, noResultsMsg);
		
		String trySearchingMsg = ".//div[contains(text(),'Try')]";
		WebElement trySearching = Element.getPageElement(testConfig, How.xPath, trySearchingMsg);
		
		Helper.compareEquals(testConfig, "Message: 1, when no results are found", noResultsMessage, noResults.getText());
		Helper.compareEquals(testConfig, "Message: 2, when no results are found", trySearch, trySearching.getText());
	}

	public void verifyCardsSortedDefaultCommAtoZ() {

		String locator = "";
		locator = ".//div[contains(@class,'grid')]/a//div[contains(@class,'bg-brp-blue-100')]/..//p";
		List<WebElement> allCommunityNames = Element.getListOfElements(testConfig, How.xPath, locator);

		ArrayList<String> obtainedCommList = new ArrayList<>();

		for (int i = 0; i < allCommunityNames.size(); i++) {
			String communityName = allCommunityNames.get(i).getAttribute("innerText");
			obtainedCommList.add(communityName);
		}

		if(obtainedCommList.isEmpty()) {
			testConfig.logFail("Getting the community name list as empty... hence failing the test");
		} else {
			isListSortedOrNot(obtainedCommList, false);
		}
	}

	public void verifyTabsDataDisplaying(String[] allTabsExpected) {

		String tabsLabelLocator = ".//div[@role='tablist']/button/span";
		String resultCount = ".//div[@role='tablist']/button/div/div";

		for (int i = 0; i < allTabsExpected.length; i++) {

			testConfig.logComment("****************** Looking for Result tab " + (i + 1) + " ******************");
			List<WebElement> allTabs = Element.getListOfElements(testConfig, How.xPath, tabsLabelLocator);
			List<WebElement> allResultsCount = Element.getListOfElements(testConfig, How.xPath, resultCount);

			Helper.compareEquals(testConfig, "Tab: " + (i + 1), allTabsExpected[i], allTabs.get(i).getAttribute("innerText").trim());

			if (!allResultsCount.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Count displaying for " + allTabsExpected[i] + " tab as " + allResultsCount.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Count displaying for " + allTabsExpected[i] + " tab as blank... failing the scenario");
			}
		}
	}

	public void verifyResultsUpdatingBasedOnTabSelected() {

		String tabsLabelLocator = ".//div[@role='tablist']/button/span";
		List<WebElement> allTabs = Element.getListOfElements(testConfig, How.xPath, tabsLabelLocator);

		String resultCount = ".//div[@role='tablist']/button/div/div";
		String totalCardsLoc = ".//div[contains(@class,'grid')]/a";

		for (int i = 0; i < allTabs.size(); i++) {
			WebElement tab = allTabs.get(i);
			Element.click(testConfig, tab, tab.getAttribute("innerText"));
			Browser.wait(testConfig, 2);
			List<WebElement> allResultsCount = Element.getListOfElements(testConfig, How.xPath, resultCount);
			List<WebElement> allCardsCount = Element.getListOfElements(testConfig, How.xPath, totalCardsLoc);
			Helper.compareEquals(testConfig, "Count displaying over tab with cards displaying in grid view",
					Integer.parseInt(allResultsCount.get(i).getAttribute("innerText")), allCardsCount.size());
			Browser.wait(testConfig, 2);
		}
	}

	public void verifyDefaultStateForResultTab(String selectedTabColor) {

		String resultTabLocator = "(.//div[@role='tablist']/button/span)[1]/..";
		WebElement resultTab = Element.getPageElement(testConfig, How.xPath, resultTabLocator);

		String resultTabCSSValue = resultTab.getCssValue("background-color");
		String resultTabRGBVal = resultTabCSSValue.substring(resultTabCSSValue.indexOf("rgb"));
		String resultTabHexcolor = Color.fromString(resultTabRGBVal).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Border color for Search Field when no value provided", selectedTabColor.toUpperCase(),
				resultTabHexcolor.toUpperCase());
	}

	public void verifyCardsAreClickableWithCorrectURL() throws UnsupportedEncodingException {

		String totalCardsLoc = ".//div[contains(@class,'grid')]/a";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, totalCardsLoc);

		String locator = "div.font-bold.text-brp-blue-100";
		List<WebElement> allPlanQMINames = Element.getListOfElements(testConfig, How.css, locator);

		for (int i = 0; i < allCards.size(); i++) {
			WebElement card = allCards.get(i);
			String planName = allPlanQMINames.get(i).getAttribute("innerText");
			if (planName.contains("Model Home:")) {
				planName = planName.replace("Model Home:", "").trim();
			}
			if(isClickable(testConfig, card)) {
				testConfig.logPass("Verified Card " + (i + 1) + " with Plan Name '" + planName + "' is clickable");
			} else {
				testConfig.logPass("Failed to verify that Card " + (i + 1) + " is clickable");
			}

			String cardLink = Element.getAttribute(testConfig, card, "href", "Home/Plan Card");

			if (!cardLink.isEmpty()) {
				testConfig.logPass("Getting link displaying for the card as " + cardLink);
			} else {
				testConfig.logFail("Getting link displaying for card as blank... failing the scenario");
			}
		}
	}

	public boolean isClickable(Config testConfig, WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(testConfig.driver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.elementToBeClickable(element));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void verifyScheduleTourCTADisplayingWithCorrectURL() throws UnsupportedEncodingException {

		String totalCardsLoc = ".//div[contains(@class,'grid')]/a//a";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, totalCardsLoc);

		String locator = "div.font-bold.text-brp-blue-100";
		List<WebElement> allPlanQMINames = Element.getListOfElements(testConfig, How.css, locator);

		for (int i = 0; i < allCards.size(); i++) {
			WebElement scheduleATourCard = allCards.get(i);
			String planName = allPlanQMINames.get(i).getAttribute("innerText");
			if (planName.contains("Model Home:")) {
				planName = planName.replace("Model Home:", "").trim();
			}
			if(scheduleATourCard.isDisplayed()) {
				testConfig.logPass("Verified Schedule A Tour link for Card " + (i + 1) + " with Plan Name '" + planName + "' is displaying");
			} else {
				testConfig.logFail("Failed to verify that Schedule A Tour link for Card " + (i + 1) + " is displaying");
			}

			String cardLink = Element.getAttribute(testConfig, scheduleATourCard, "href", "Schedule tour link for QMI/Plan Card");

			if (!cardLink.isEmpty()) {
				testConfig.logPass("Getting link displaying for the card as " + cardLink);
			} else {
				testConfig.logFail("Getting link displaying for card as blank... failing the scenario");
			}
		}
	}

	public void verifyDefaultCardAndMapPinCount() {

		int count = 0;

		String mapClusterMarkers = "div.relative.pin-container>div>p";
		List<WebElement> allClusters = Element.getListOfElements(testConfig, How.css, mapClusterMarkers);

		for (WebElement webElement : allClusters) {
			String value = webElement.getAttribute("innerText");
			count += Integer.valueOf(value);
		}

		String homesToTourTextLoc = ".//div[contains(text(),'in this area')]";
		WebElement homesToTourText = Element.getPageElement(testConfig, How.xPath, homesToTourTextLoc);

		Helper.compareEquals(testConfig, "myTime Homes to tour text with map for default view", String.valueOf(count) + " Homes to Tour in this area:", homesToTourText.getText());

	}

	public void clickOnMapPinAndVerifyCardCountWithMapPinCount() {

		Random rd = new Random();

		String mapClusterMarkers = "div.relative.pin-container>div>p";
		List<WebElement> allClusters = Element.getListOfElements(testConfig, How.css, mapClusterMarkers);
		int location = rd.nextInt(allClusters.size());

		Element.clickThroughJS(testConfig, allClusters.get(location), "Map Pin at position: " + location);
		Browser.waitWithoutLogging(testConfig, 5);
		verifyDefaultCardAndMapPinCount();
	}

}
