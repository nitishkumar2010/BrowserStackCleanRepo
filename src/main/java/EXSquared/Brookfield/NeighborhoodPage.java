package EXSquared.Brookfield;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;
import Utils.Helper;

public class NeighborhoodPage extends BRPHelper {

	public enum NeighborhoodHeaders {
		Overview, PlanHomes, Map, Community, FAQs, FeatureSpotlight
	}

	public enum ResultsTab {
		Homes, Plans
	}

	@FindBy(xpath = ".//button[text()='Overview']")
	private WebElement neighborhoodOverviewTab;

	@FindBy(xpath = ".//button[text()='Schedule a Tour']")
	private WebElement scheduleATourBtn;

	@FindBy(xpath = ".//button[contains(text(),'Request Information')]")
	private WebElement requestInfoBtn;

	@FindBy(xpath = ".//div[contains(text(),'GET PRE-QUALIFIED')]")
	private WebElement getPreQualifiedBtn;

	@FindBy(xpath = ".//button[contains(@id,'pricing-and-availability')]")
	private WebElement homePlanList;

	public NeighborhoodPage(Config testConfig) {

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
		Browser.waitForPageLoad(testConfig, neighborhoodOverviewTab);
	}

	/**
	 * Zooming out of page on the basis of no of time one wants to
	 * 
	 * @param count
	 */
	public void zoomOutFromTheWebPage(int count) {
		Robot robot;
		try {
			robot = new Robot();
			testConfig.logComment("Performing zoom out operation");
			for (int i = 0; i < count; i++) {
				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_SUBTRACT);
				robot.keyRelease(KeyEvent.VK_SUBTRACT);
				robot.keyRelease(KeyEvent.VK_CONTROL);
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public PlanPage navigateToPlanPage() {
		WebElement plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[text()='Plans']");
		Element.click(testConfig, plansTab, "Plans tab");
		Browser.wait(testConfig, 2);

		//String locator = ".//section[@id='pricing-and-availability']//div[contains(@class,'_slide')]/..//span[@class='text-brp-blue-100']";
		String locator = ".//section[@id='pricing-and-availability']//*[contains(@class,'_slide')]//div[contains(@class,'card-title')]/span[not(contains(@class,'yellow'))]";
		List<WebElement> priceNamePlans = Element.getListOfElements(testConfig, How.xPath, locator);
		testConfig.putRunTimeProperty("PlanName", priceNamePlans.get(0).getText().trim());

		List<WebElement> planCards = Element.getListOfElements(testConfig, How.xPath,
				".//section[@id='pricing-and-availability']//*[contains(@class,'_slide')]");
		Element.click(testConfig, planCards.get(0), "Plan card to navigate to Plan Page");
		Browser.wait(testConfig, 2);

		Set<String> windowHandle = testConfig.driver.getWindowHandles();
		testConfig.driver.switchTo().window(getLastElement(windowHandle));

		testConfig.logComment("Switching to the new window opened...");
		
		return new PlanPage(testConfig);
	}

	public QMIPage navigateToQMIPage() {
		String finalVal = "";
		WebElement plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[text()='Homes']");
		Element.click(testConfig, plansTab, "Homes tab");
		Browser.wait(testConfig, 2);

		/*List<WebElement> planCards = Element.getListOfElements(testConfig, How.xPath,
				".//span[text()='Priced at']/../span[contains(text(),'$')]");
		 */
		
		String locator = ".//section[@id='pricing-and-availability']//div[contains(@class,'car-type-brds-v2-base-700')]//div[contains(text(),'$')]"
				+ "/ancestor::*[contains(@class,'_slide')]//div[contains(@class,'card-title')]/span";
		List<WebElement> homeCards = Element.getListOfElements(testConfig, How.xPath, locator);
		String qmiName = homeCards.get(0).getAttribute("innerText").trim();

		String[] array = qmiName.split(",");
		if(array.length == 2) {
			finalVal = array[0].concat(array[1]);
			if(finalVal.contains("Unit")) {
				int index = finalVal.indexOf("Unit ");
				finalVal = finalVal.substring(0, index + 5) + "#" + finalVal.substring(index + 5);
			}
		} else {
			finalVal = array[0];
		}
		
		testConfig.putRunTimeProperty("QMINameForHeading", qmiName);
		testConfig.putRunTimeProperty("QMINameForBreadcrumb", finalVal.trim());
		testConfig.putRunTimeProperty("QMINameForFYHCard", qmiName);

		Element.clickThroughJS(testConfig, homeCards.get(0), "QMI card to navigate to QMI Page");
		Browser.wait(testConfig, 2);

		Set<String> windowHandle = testConfig.driver.getWindowHandles();
		testConfig.driver.switchTo().window(getLastElement(windowHandle));

		testConfig.logComment("Switching to the new window opened...");
		
		return new QMIPage(testConfig);
	}

	public void verifyJumpAnchorTags(NeighborhoodHeaders neighborhoodHeaders) {

		String tabLocator = "";
		String sectionLocator = "";

		switch (neighborhoodHeaders) {
		case Overview:
			tabLocator = ".//button[text()='Overview']";
			sectionLocator = ".//section[contains(@class,'brp-section-about')]";
			break;

		case FAQs:
			tabLocator = ".//button[text()='FAQs']";
			sectionLocator = ".//section[contains(@class,'brp-section-faq')]";
			break;

		case Map:
			tabLocator = ".//button[text()='Map']";
			sectionLocator = ".//div[contains(@class,'circular-image')]/../../..";
			break;

		case PlanHomes:
			tabLocator = ".//button[contains(@id,'pricing-and-availability')]";
			sectionLocator = ".//section[contains(@class,'brp-section-homes-and-plans')]";
			break;

		case Community:
			tabLocator = ".//button[text()='Community']";
			sectionLocator = ".//section[contains(@class,'brp-section-explore')]";
			break;

		case FeatureSpotlight:
			tabLocator = ".//button[text()='Features Spotlight']";
			sectionLocator = ".//section[contains(@class,'brp-section-features')]";
			break;

		}

		try {
			WebElement tabElement = Element.getPageElement(testConfig, How.xPath, tabLocator);
			WebElement sectionElement = Element.getPageElement(testConfig, How.xPath, sectionLocator);

			Element.click(testConfig, tabElement, neighborhoodHeaders.toString() + " tab from Neighborhood nav header");
			Browser.wait(testConfig, 3);
			Helper.compareContains(testConfig,
					"Jump anchor tag associated with " + neighborhoodHeaders.toString() + " header tab",
					sectionElement.getAttribute("id"), tabElement.getAttribute("id"));
		} catch (Exception e) {
			testConfig.logComment("Not having " + neighborhoodHeaders.toString()
			+ " tab over Neighborhood nav header so skipping that case");
		}

	}

	public void verifyBreadcrumbDisplaying(List<String> expectedBreadcrumbs) {

		String allBreadcrumbsLocator = ".//div[contains(@class,'md:breadcrumbText')]//ol/li/a";
		List<WebElement> allBreadcrumbs = Element.getListOfElements(testConfig, How.xPath, allBreadcrumbsLocator);

		for (int i = 0; i < expectedBreadcrumbs.size(); i++) {
			Helper.compareEquals(testConfig, "Breadcrumb item " + (i + 1), expectedBreadcrumbs.get(i).toUpperCase(),
					allBreadcrumbs.get(i).getText());
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

	public void verifyNeighborhoodDescriptionExpandCollapse(String sectionTitle) {

		String titleLocator = ".//section[contains(@class,'brp-section-about')]//h2";
		WebElement titleOnPageElement = Element.getPageElement(testConfig, How.xPath, titleLocator);
		Helper.compareContains(testConfig, "Section title", sectionTitle, titleOnPageElement.getText());

		String contentLocator = "(.//section[contains(@class,'brp-section-about')])//div[contains(@class,'transition-all')]";
		String readMoreBtnLocator = ".//span[text()='Read More']";
		String readLessBtnLocator = ".//span[text()='Read Less']";

		WebElement originalContent = Element.getPageElement(testConfig, How.xPath, contentLocator);
		String initialContent = Element.getText(testConfig, originalContent,
				"Initial community description displaying");

		WebElement readMoreBtn = Element.getPageElement(testConfig, How.xPath, readMoreBtnLocator);
		Element.click(testConfig, readMoreBtn, "Read More button");

		Browser.wait(testConfig, 1);

		WebElement updatedContent = Element.getPageElement(testConfig, How.xPath, contentLocator);
		String newContent = Element.getText(testConfig, updatedContent, "Content after clicking 'Read More' button");

		if (initialContent.length() < newContent.length()) {
			testConfig.logPass("Initial content : '" + initialContent + "' being changed to '" + newContent
					+ "' after clicking 'Read More' button");
		} else {
			testConfig
			.logFail("Not getting content to be expanded by clicking 'Read More' button. Failing the test...");
		}

		WebElement readLessBtn = Element.getPageElement(testConfig, How.xPath, readLessBtnLocator);
		Element.click(testConfig, readLessBtn, "Read Less button");

		Browser.wait(testConfig, 1);

		WebElement updatedContentAgain = Element.getPageElement(testConfig, How.xPath, contentLocator);
		String latestContent = Element.getText(testConfig, updatedContentAgain,
				"Content after clicking 'Read More' button");

		if (initialContent.equals(latestContent)) {
			testConfig.logPass("Getting the content being collapsed as '" + initialContent
					+ "' correctly after clicking 'Read Less' button");
		} else {
			testConfig.logFail(
					"Not getting content to be collapsed correctly by clicking 'Read Less' button. Failing the test...");
		}
	}

	public void verifyDownloadBrochureButton(String expectedBrochure) {
		String downloadBrochureLocator = ".//span[text()='Download Brochure']/../..";
		WebElement brochureBtn = Element.getPageElement(testConfig, How.xPath, downloadBrochureLocator);
		Element.clickThroughJS(testConfig, brochureBtn, "Download brochure link");
		Browser.wait(testConfig, 30);

		File brochureDownloadedFile = Browser.lastFileModifiedWithDesiredName(testConfig, testConfig.downloadPath,
				"Brochure");
		Helper.compareContains(testConfig, "Downloaded Brochure File Name", expectedBrochure,
				brochureDownloadedFile.getName());
	}

	public void verifyHomeCardsNavigation(ResultsTab resultsTab) {

		String homeCardsCount = "";

		Element.click(testConfig, homePlanList, "Home Plan list");
		Browser.wait(testConfig, 2);

		switch (resultsTab) {
		case Homes:
			homeCardsCount = ".//div[contains(@class,'flex flex-col')]//button/span[text()='Homes']/../span[2]";
			break;

		case Plans:
			homeCardsCount = ".//div[contains(@class,'flex flex-col')]//button/span[text()='Plans']/../span[2]";
			break;
		}

		WebElement homesCount = Element.getPageElement(testConfig, How.xPath, homeCardsCount);
		Element.click(testConfig, homesCount, "Homes count");

		String cardsLocator = ".//section[@id='pricing-and-availability']//li/div[contains(@class,'card-container')]";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, cardsLocator);

		Helper.compareEquals(testConfig, "Total cards count matches with the count displaying on Homes header",
				homesCount.getText().replace("(", "").replace(")", "").trim(), String.valueOf(allCards.size()));
		Browser.wait(testConfig, 2);

		String leftBtnLocator = ".//section[@id='pricing-and-availability']//button[@aria-label='Previous slide']";
		String rightBtnLocator = ".//section[@id='pricing-and-availability']//button[@aria-label='Next slide']";

		if (allCards.size() > 2) {
			WebElement leftBtn = Element.getPageElement(testConfig, How.xPath, leftBtnLocator);
			WebElement rightBtn = Element.getPageElement(testConfig, How.xPath, rightBtnLocator);
			verifyBtnsBehaviorInitially(leftBtn, rightBtn);
			Browser.wait(testConfig, 2);

			for (int i = 0; i < allCards.size() - 2; i++) {
				Element.click(testConfig, rightBtn, "Right button");
				Browser.wait(testConfig, 1);
			}
			verifyBtnsBehaviorWhenNavThroughCards(leftBtn, rightBtn);
		} else {
			verifyButtonsNotPresent();
		}
	}

	private void verifyButtonsNotPresent() {

		String leftBtnLocator = ".//button[contains(@class,'left-0 z-20')]";
		String rightBtnLocator = ".//button[contains(@class,'right-0 z-20')]";

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

		try {
			if (!rightBtn.isEnabled()) {
				testConfig.logPass("Verified right navigation button for Homes list is disabled now");
			} else {
				testConfig.logFail("Failed to verify that right navigation button for Homes list is disabled now");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that right navigation button for Homes list is disabled now");
		}
	}

	private void verifyBtnsBehaviorInitially(WebElement leftBtn, WebElement rightBtn) {

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
	}

	public FYHPage verifyDetailsAndClickBrowseCommunityMap(String title, String subtitle) {

		String titleLocator = ".//section//h2[contains(text(),'Explore')]";
		String subtitleLocator = ".//section//h2[contains(text(),'Look')]";

		WebElement titleOnPageElement = Element.getPageElement(testConfig, How.xPath, titleLocator);
		WebElement subtitleOnPageElement = Element.getPageElement(testConfig, How.xPath, subtitleLocator);

		Helper.compareEquals(testConfig, "Section title", title, titleOnPageElement.getText());
		Helper.compareEquals(testConfig, "Section subtitle", subtitle, subtitleOnPageElement.getText());

		String imageLocator = ".//div[contains(@class,'circular-image')]/img";
		WebElement sitemapImage = Element.getPageElement(testConfig, How.xPath, imageLocator);

		String imageUrl = Element.getAttribute(testConfig, sitemapImage, "src", "Image Source");
		testConfig.logComment("Siteplan image source: " + homeurl + imageUrl);
		verifyURLAsPerDomain(imageUrl);

		String browseCommLocator = ".//div[contains(@class,'circular-image')]/a";
		WebElement browseCommMap = Element.getPageElement(testConfig, How.xPath, browseCommLocator);
		Element.click(testConfig, browseCommMap, "Browse Community Map link");

		/*for (String winHandle : testConfig.driver.getWindowHandles()) {
			testConfig.driver.switchTo().window(winHandle);
			getBase64UserNamePwdNetworkTab();
		}*/

		Set<String> windowHandle = testConfig.driver.getWindowHandles();
		testConfig.driver.switchTo().window(getLastElement(windowHandle));

		testConfig.logComment("Switching to the new window opened...");
		
		Helper.removeCookies(testConfig);
		testConfig.putRunTimeProperty("RedirectionValue", "yes");
		return new FYHPage(testConfig);
	}

	public CommunityPage verifyExploreTheCommunitySection(String sectionHeading, String communityName) {

		String exploreTextLocator = ".//section[contains(@class,'brp-section-explore')]//div[contains(text(),'Explore')]";
		WebElement sectionTitle = Element.getPageElement(testConfig, How.xPath, exploreTextLocator);

		Helper.compareEquals(testConfig, "Section heading", sectionHeading, sectionTitle.getText());

		String commNameLocator = ".//section[contains(@class,'brp-section-explore')]//div[contains(@class,'text-brp-blue-300')]";
		WebElement commNameElement = Element.getPageElement(testConfig, How.xPath, commNameLocator);

		Helper.compareEquals(testConfig, "Section heading", communityName, commNameElement.getText());

		String imageLocator = ".//section[contains(@class,'brp-section-explore')]/div/div/img";
		WebElement image = Element.getPageElement(testConfig, How.xPath, imageLocator);

		String imageSrc = image.getAttribute("src");
		verifyURLAsPerDomain(imageSrc);

		String learnMoreLinkLocator = ".//section[contains(@class,'brp-section-explore')]//*[text()='Learn More']";
		WebElement learnMoreLink = Element.getPageElement(testConfig, How.xPath, learnMoreLinkLocator);
		Element.click(testConfig, learnMoreLink, "Learn More link");
		

		return new CommunityPage(testConfig);
	}

	public NeighborhoodPage verifyOtherNeighborhoodSectionOverPage(String[] expectedSectionTitle, String sectionSubHeading,
			String[] otherNeighborhoods) {

		String subHeadingOverPage = ".//section[contains(@class,'brp-section-other-neighborhoods')]//h3";
		String titleOverPage = ".//section[contains(@class,'brp-section-other-neighborhoods')]//div[contains(@class,'text-white')]";
		String neighborhoodNamesLoc = "(.//section[contains(@class,'brp-section-other-neighborhoods')])[1]//div[contains(@class,'tracking-wide car-pt-1')]/span[1]";

		WebElement subHeadingPage = Element.getPageElement(testConfig, How.xPath, subHeadingOverPage);
		WebElement titlePage = Element.getPageElement(testConfig, How.xPath, titleOverPage);
		List<WebElement> neighborhoodNames = Element.getListOfElements(testConfig, How.xPath, neighborhoodNamesLoc);

		Helper.compareEquals(testConfig, "Section sub heading", sectionSubHeading, subHeadingPage.getText());

		List<String> sectionTitlesExpected = Arrays.asList(expectedSectionTitle);  
		if(sectionTitlesExpected.contains(titlePage.getText().trim())) {
			testConfig.logPass("Verified getting section heading as: '" + titlePage.getText() + "'");
		} else {
			testConfig.logPass("Failed to verify the section heading as it is displaying as: '" + titlePage.getText() + "'");
		}

		Helper.compareEquals(testConfig, "Neighborhood count over page", otherNeighborhoods.length,
				neighborhoodNames.size());

		Browser.wait(testConfig, 1);

		Element.click(testConfig, neighborhoodNames.get(0), neighborhoodNames.get(0).getText() + " Neighborhood card");
		
		return new NeighborhoodPage(testConfig);

	}

	public void verifyRedirectionToCorrectNeighborhood(String neighborhoodNameFromPlan) {

		String neighborhoodNameLocator = ".//button[contains(@class,'whitespace-nowrap')][contains(@class,'border-brp-blue-gray-200')]";
		WebElement neighborhoodName = Element.getPageElement(testConfig, How.xPath, neighborhoodNameLocator);
		String neighborhoodNameOverPage = Element.getText(testConfig, neighborhoodName, "Neighborhood name over page");
		Helper.compareEquals(testConfig, "Redirection being done to correct neighborhood", neighborhoodNameFromPlan, neighborhoodNameOverPage);
	}

	public PlanPage clickOnPlanHavingPrice() {

		WebElement plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[text()='Plans']");
		Element.click(testConfig, plansTab, "Plans tab");
		Browser.wait(testConfig, 2);
		String locator = ".//section[@id='pricing-and-availability']//div[contains(text(),'$')]/ancestor::div[contains(@class,'card-container')]"
				+ "//div[contains(@class,'card-title')]/span[2] | .//section[@id='pricing-and-availability']//div[contains(@class,'car-font-bold')]"
				+ "/ancestor::div[contains(@class,'card-container')]//div[contains(@class,'card-title')]/span[2]";
		List<WebElement> priceNamePlans = Element.getListOfElements(testConfig, How.xPath, locator);
		testConfig.putRunTimeProperty("PlanName", priceNamePlans.get(0).getAttribute("innerText").trim());

		List<WebElement> planCards = Element.getListOfElements(testConfig, How.xPath,
				".//section[@id='pricing-and-availability']//div[contains(text(),'$')] | .//section[@id='pricing-and-availability']//div[contains(@class,'car-font-bold')]");

		Element.click(testConfig, planCards.get(0), "Plan card to navigate to Plan Page");
		Browser.wait(testConfig, 2);
		
		Set<String> windowHandle = testConfig.driver.getWindowHandles();
		testConfig.driver.switchTo().window(getLastElement(windowHandle));

		testConfig.logComment("Switching to the new window opened...");
		
		return new PlanPage(testConfig);
	}

	public QMIPage clickOnQMIHavingPrice() {

		String locator = ".//section[@id='pricing-and-availability']//div[contains(@class,'car-type-brds-v2-base-700')]//div[contains(text(),'$')]"
				+ "/ancestor::*[contains(@class,'_slide')]//div[contains(@class,'card-title')]/span";
		List<WebElement> priceNameQMI = Element.getListOfElements(testConfig, How.xPath, locator);
		testConfig.putRunTimeProperty("QMIName", priceNameQMI.get(0).getText().trim());

		List<WebElement> qmiCards = Element.getListOfElements(testConfig, How.xPath,
				".//section[@id='pricing-and-availability']//div[contains(@class,'car-type-brds-v2-base-700')]//div[contains(text(),'$')]");

		Element.click(testConfig, qmiCards.get(0), "QMI card to navigate to QMI Page");
		Browser.wait(testConfig, 2);

		Set<String> windowHandle = testConfig.driver.getWindowHandles();
		testConfig.driver.switchTo().window(getLastElement(windowHandle));

		testConfig.logComment("Switching to the new window opened...");
		
		return new QMIPage(testConfig);
	}

	public Object clickOnQMIHavingSimilarHomes() {

		Object obj = null;
		String qmiPlanNameLocator = ".//span[contains(@class,'car-capitalize')][contains(text(),'Plan')]";
		List<WebElement> allQMIPlanName = Element.getListOfElements(testConfig, How.xPath, qmiPlanNameLocator);
		HashMap<String, Integer> qmiData = new HashMap<>();
		for (int i = 0; i < allQMIPlanName.size(); i++) {
			if(qmiData.containsKey(allQMIPlanName.get(i).getAttribute("innerText"))) {
				qmiData.put(allQMIPlanName.get(i).getAttribute("innerText"), qmiData.get(allQMIPlanName.get(i).getAttribute("innerText")) + 1);
				Element.click(testConfig, allQMIPlanName.get(i), "QMI card : " + (i + 1));

				Set<String> windowHandle = testConfig.driver.getWindowHandles();
				testConfig.driver.switchTo().window(getLastElement(windowHandle));

				testConfig.logComment("Switching to the new window opened...");
				
				return new QMIPage(testConfig);
			} else {
				qmiData.put(allQMIPlanName.get(i).getAttribute("innerText"), 1);
			}
		}
		
		return obj;
	}

	public void verifyNeighborhoodNameInMenuBarAndHeaderTag(String neighborhoodName, String community) {

		String navNeighborhoodNameLoc = ".//div[contains(@class,'shadow-subnav')]/div/button[contains(@class,'font-bold')]";
		WebElement navNeighborhoodName = Element.getPageElement(testConfig, How.xPath, navNeighborhoodNameLoc);
		Helper.compareEquals(testConfig, "Neighborhood name displaying along with sub nav menu", neighborhoodName.trim(), navNeighborhoodName.getText().trim());

		String locator = ".//*[contains(text(),'" + neighborhoodName + "')][contains(@class,'font-semibold')]/..";
		WebElement neighborhoodNameElement = Element.getPageElement(testConfig, How.xPath, locator);
		Helper.compareEquals(testConfig, "Tag associated with Neighborhood Name element", "h1", neighborhoodNameElement.getTagName());

		String communityName = ".//*[contains(text(),'" + neighborhoodName + "')][contains(@class,'font-semibold')]/parent::h1/..//h2";
		WebElement communityNameElement = Element.getPageElement(testConfig, How.xPath, communityName);
		Helper.compareContains(testConfig, "Neighborhood name displaying along with sub nav menu", community.trim(), communityNameElement.getText().trim());

		Helper.compareEquals(testConfig, "Tag associated with Community Name element", "h2", communityNameElement.getTagName());

	}

	public void verifySectionHeadingAndSubHeading(String title, String subtitle) {

		String titleLocator = ".//section[contains(@class,'brp-section-features')]//h2[1]";
		String subtitleLocator = ".//section[contains(@class,'brp-section-features')]//h2[2]";

		WebElement titleOnPageElement = Element.getPageElement(testConfig, How.xPath, titleLocator);
		WebElement subtitleOnPageElement = Element.getPageElement(testConfig, How.xPath, subtitleLocator);

		Helper.compareEquals(testConfig, "Section title", title, titleOnPageElement.getText());
		Helper.compareEquals(testConfig, "Section subtitle", subtitle, subtitleOnPageElement.getText());

	}

	public void verifyImagesDisplayingForTheSection() {

		String allImageLoc = ".//section[contains(@class,'brp-section-features')]//img";
		List<WebElement> allImages = Element.getListOfElements(testConfig, How.xPath, allImageLoc);

		for (int i = 0; i < allImages.size(); i++) {
			String imageSrc = allImages.get(i).getAttribute("src");
			verifyURLAsPerDomain(imageSrc);
		}
	}

	public void verifyTilesHeading(String[] spotlightTitles) {

		String allTilesHeadingLoc = ".//section[contains(@class,'brp-section-features')]//h4";
		List<WebElement> allTilesHeading = Element.getListOfElements(testConfig, How.xPath, allTilesHeadingLoc);

		for (int i = 0; i < allTilesHeading.size(); i++) {
			Helper.compareEquals(testConfig, "Tile " + ( i + 1) + " heading ", spotlightTitles[i], allTilesHeading.get(i).getAttribute("innerText"));
		}
	}

	public void verifyNeighborhoodsListedInPriceAscendingOrder() {

		String neighborhoodStartPriceLoc = ".//section[contains(@class,'brp-section-other-neighborhoods')]//div[contains(text(),'Starting price')][not(contains(@class,'hidden'))]/parent::div/div[2]";
		List<WebElement> neighborhoodPrices = Element.getListOfElements(testConfig, How.xPath, neighborhoodStartPriceLoc);
		ArrayList<Long> obtainedList = new ArrayList<>(); 

		if(neighborhoodPrices.size() > 1) {
			for (int i = 0; i < neighborhoodPrices.size(); i++) {
				WebElement priceValueEle = neighborhoodPrices.get(i);
				if(priceValueEle.getText().contains("$"))
				{
					String price = priceValueEle.getText().replace(",", "").replace("$", "").trim();
					obtainedList.add(Long.parseLong(price));
				}			
			}
			isPriceListSortedOrNot(obtainedList, false);
		} else {
			testConfig.logComment("Cannot verify the neighborhoods sorting order as neighborhoods count with prices are less than 2");
		}

	}

	public void verifyHeroImageDisplaying() {

		String heroImageLoc = ".//section[contains(@class,'brp-section-hero')]//img";
		WebElement heroImage = Element.getPageElement(testConfig, How.xPath, heroImageLoc);

		String imageSrc = heroImage.getAttribute("src");
		verifyURLAsPerDomain(imageSrc);
	}

	public void verifyGridSectionDetails() {

		String homeTypeLabelLoc = ".//div[contains(@class,'brp-page-grid')][not(contains(@class,'3xl:hidden'))]//div[contains(text(),'Home type:')]";
		String homeTypeValueLoc = ".//div[contains(@class,'brp-page-grid')][not(contains(@class,'3xl:hidden'))]//div[contains(text(),'Home type:')]/parent::div/div[2]";

		WebElement homeTypeLabel = Element.getPageElement(testConfig, How.xPath, homeTypeLabelLoc);
		WebElement homeTypeValue = Element.getPageElement(testConfig, How.xPath, homeTypeValueLoc);
		String value = homeTypeValue.getAttribute("innerText");
		String label = homeTypeLabel.getAttribute("innerText");

		if (!value.isEmpty()) {
			testConfig.logPass("Getting " + label.trim() + " as " + value);
		} else {
			testConfig.logFail("Getting " + label.trim() + " value as blank... failing the scenario");
		}

		try {
			String availableHomesLabelLoc = ".//div[contains(@class,'brp-page-grid')][not(contains(@class,'3xl:hidden'))]//div[contains(text(),'Available Homes')]";
			String availableHomesValueLoc = ".//div[contains(@class,'brp-page-grid')][not(contains(@class,'3xl:hidden'))]//div[contains(text(),'Available Homes')]/parent::div/div[2]";

			WebElement availableHomes = Element.getPageElement(testConfig, How.xPath, availableHomesLabelLoc);
			WebElement availableHomesValue = Element.getPageElement(testConfig, How.xPath, availableHomesValueLoc);
			String availableHomeValue = availableHomesValue.getAttribute("innerText");
			String availableHomeslabel = availableHomes.getAttribute("innerText");

			if (!availableHomeValue.isEmpty()) {
				testConfig.logPass("Getting " + availableHomeslabel.trim() + " as " + availableHomeValue);
			} else {
				testConfig.logFail("Getting " + availableHomeslabel.trim() + " value as blank... failing the scenario");
			}
		} catch (Exception e) {

		}

		try {
			String buildablePlansLabelLoc = ".//div[contains(@class,'brp-page-grid')][not(contains(@class,'3xl:hidden'))]//div[contains(text(),'Buildable Plans')]";
			String buildablePlansValueLoc = ".//div[contains(@class,'brp-page-grid')][not(contains(@class,'3xl:hidden'))]//div[contains(text(),'Buildable Plans')]/parent::div/div[2]";

			WebElement buildablePlans = Element.getPageElement(testConfig, How.xPath, buildablePlansLabelLoc);
			WebElement buildablePlansValue = Element.getPageElement(testConfig, How.xPath, buildablePlansValueLoc);
			String buildablePlanValue = buildablePlansValue.getAttribute("innerText");
			String buildablePlanlabel = buildablePlans.getAttribute("innerText");

			if (!buildablePlanValue.isEmpty()) {
				testConfig.logPass("Getting " + buildablePlanlabel.trim() + " as " + buildablePlanValue);
			} else {
				testConfig.logFail("Getting " + buildablePlanlabel.trim() + " value as blank... failing the scenario");
			}
		} catch (Exception e) {

		}
	}

	public void verifyHomeResultSectionDetails(String title, String subtitle, String description) {

		String spotlightHomePlanSectionImageLoc = ".//section[@id='pricing-and-availability']/div/img";
		WebElement spotlightHomePlanSectionImage = Element.getPageElement(testConfig, How.xPath, spotlightHomePlanSectionImageLoc);

		String imageSrc = spotlightHomePlanSectionImage.getAttribute("src");
		verifyURLAsPerDomain(imageSrc);

		String titleLocator = ".//section[@id='pricing-and-availability']//h2[1]";
		String subtitleLocator = ".//section[@id='pricing-and-availability']//h2[2]";
		String descriptionLocator = ".//section[@id='pricing-and-availability']//h2[3]";

		WebElement titleOnPageElement = Element.getPageElement(testConfig, How.xPath, titleLocator);
		WebElement subtitleOnPageElement = Element.getPageElement(testConfig, How.xPath, subtitleLocator);
		WebElement descriptionOnPageElement = Element.getPageElement(testConfig, How.xPath, descriptionLocator);

		Helper.compareEquals(testConfig, "Section title", title, titleOnPageElement.getText());
		Helper.compareEquals(testConfig, "Section subtitle", subtitle, subtitleOnPageElement.getText());
		Helper.compareEquals(testConfig, "Section description", description, descriptionOnPageElement.getText());


	}

	public void verifySortingOrderForCards(ResultsTab resultTab) {

		String homeCardsCount = "";

		Element.click(testConfig, homePlanList, "Home Plan list");
		Browser.wait(testConfig, 2);

		switch (resultTab) {
		case Homes:
			homeCardsCount = ".//div[contains(@class,'flex flex-col')]//button/span[text()='Homes']/../span[2]";
			break;

		case Plans:
			homeCardsCount = ".//div[contains(@class,'flex flex-col')]//button/span[text()='Plans']/../span[2]";
			break;
		}

		WebElement homesCount = Element.getPageElement(testConfig, How.xPath, homeCardsCount);
		Element.click(testConfig, homesCount, "Homes count");

		String priceValueLoc = "";
		switch (resultTab) {
		case Homes:
			priceValueLoc = ".//div[text()='Priced at ']/..//div[contains(text(),'$')]";
			break;

		case Plans:
			priceValueLoc = ".//div[text()='Priced from ']/..//div[contains(text(),'$')]";
			break;
		}

		ArrayList<Long> obtainedList = new ArrayList<>(); 
		List<WebElement> qmiCards = Element.getListOfElements(testConfig, How.xPath, priceValueLoc);

		for (int i = 0; i < qmiCards.size(); i++) {
			WebElement priceValueEle = qmiCards.get(i);
			if(priceValueEle.getAttribute("innerText").contains("$"))
			{
				String price = priceValueEle.getAttribute("innerText").replace(",", "").replace("$", "").trim();
				obtainedList.add(Long.parseLong(price));
			}			
		}
		isPriceListSortedOrNot(obtainedList, false);
	}

	public void verifyPlanCardsData() {

		String homeCardsCount = ".//div[contains(@class,'flex flex-col')]//button/span[text()='Plans']/../span[2]";

		Element.click(testConfig, homePlanList, "Home Plan list");
		Browser.wait(testConfig, 2);

		WebElement homesCount = Element.getPageElement(testConfig, How.xPath, homeCardsCount);
		Element.click(testConfig, homesCount, "Homes count");
		Browser.wait(testConfig, 2);

		String neighborhoodNameLoc = ".//div[contains(@class,'card-labels')]//span";
		List<WebElement> neighborhoodNameOverPlanCard = Element.getListOfElements(testConfig, How.xPath, neighborhoodNameLoc);

		for (int i = 0; i < neighborhoodNameOverPlanCard.size(); i++) {

			String planNameLoc = ".//div[contains(@class,'card-title')]/span[2]";
			List<WebElement> planNameOverCard = Element.getListOfElements(testConfig, How.xPath, planNameLoc);

			String homeTypeLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'specs-item')]/div/div/span)[1]";
			WebElement homeTypeList = Element.getPageElement(testConfig, How.xPath, homeTypeLoc.replace("count", String.valueOf(i + 1)));

			String sqFtRangeLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'specs-item')]/div/div/span)[2]";
			WebElement sqFtRange = Element.getPageElement(testConfig, How.xPath, sqFtRangeLoc.replace("count", String.valueOf(i + 1)));

			String bedroomValueLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'specs-item')]/div/div/span)[3]";
			WebElement bedroomValue = Element.getPageElement(testConfig, How.xPath, bedroomValueLoc.replace("count", String.valueOf(i + 1)));

			String bathroomValueLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'specs-item')]/div/div/span)[4]";
			WebElement bathroomValue = Element.getPageElement(testConfig, How.xPath, bathroomValueLoc.replace("count", String.valueOf(i + 1)));

			String pricingContent = ".//section[@id='pricing-and-availability']//div[contains(text(),'$')] | .//section[@id='pricing-and-availability']//div[contains(@class,'car-type-brds-v2-base-700')]";
			List<WebElement> pricing = Element.getListOfElements(testConfig, How.xPath, pricingContent);

			Browser.wait(testConfig, 3);
			testConfig.logComment("****************** Looking for data for card no " + ( i+ 1) + " ******************");
			if (!neighborhoodNameOverPlanCard.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Neighborhood name displaying over card as " + neighborhoodNameOverPlanCard.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Neighborhood name displaying over card value as blank... failing the scenario");
			}

			if (!planNameOverCard.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Plan name displaying over card as " + planNameOverCard.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Plan name displaying over card value as blank... failing the scenario");
			}

			if (!homeTypeList.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Home Type displaying over card as " + homeTypeList.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Home Type displaying over card value as blank... failing the scenario");
			}

			if (!sqFtRange.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Sq Ft Range displaying over card as " + sqFtRange.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Sq Ft Range displaying over card value as blank... failing the scenario");
			}

			if (!bedroomValue.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Bedroom value displaying over card as " + bedroomValue.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Bedroom value displaying over card value as blank... failing the scenario");
			}

			if (!bathroomValue.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Bathroom name displaying over card as " + bathroomValue.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Neighborhood name displaying over card value as blank... failing the scenario");
			}

			if (!pricing.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting pricing displaying over card as " + pricing.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting pricing displaying over card value as blank... failing the scenario");
			}
		}

	}

	public void verifyHomeCardsData() {

		String homeCardsCount = ".//div[contains(@class,'flex flex-col')]//button/span[text()='Homes']/../span[2]";
		Element.click(testConfig, homePlanList, "Home Plan list");
		Browser.wait(testConfig, 2);

		WebElement homesCount = Element.getPageElement(testConfig, How.xPath, homeCardsCount);
		Element.click(testConfig, homesCount, "Homes count");
		Browser.wait(testConfig, 2);

		String qmiNameLoc = ".//div[contains(@class,'card-title')]/span";
		List<WebElement> qmiNameOverCard = Element.getListOfElements(testConfig, How.xPath, qmiNameLoc);

		for (int i = 0; i < qmiNameOverCard.size(); i++) {

			Browser.wait(testConfig, 3);

			String neighborhoodNameLoc = "((.//div[contains(@class,'card-labels')])[count]//span[contains(@class,'car-capitalize')])[1]";
			WebElement neighborhoodNameOverQMICard = Element.getPageElement(testConfig, How.xPath, neighborhoodNameLoc.replace("count", String.valueOf(i + 1)));

			String planNameLoc = "((.//div[contains(@class,'card-labels')])[count]//span[contains(@class,'car-capitalize')])[2]";
			WebElement planNameOverCard = Element.getPageElement(testConfig, How.xPath, planNameLoc.replace("count", String.valueOf(i + 1)));

			String homeTypeLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'specs-item')]/div/div/span)[1]";
			WebElement homeTypeList = Element.getPageElement(testConfig, How.xPath, homeTypeLoc.replace("count", String.valueOf(i + 1)));

			String sqFtRangeLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'specs-item')]/div/div/span)[2]";
			WebElement sqFtRange = Element.getPageElement(testConfig, How.xPath, sqFtRangeLoc.replace("count", String.valueOf(i + 1)));

			String bedroomValueLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'specs-item')]/div/div/span)[3]";
			WebElement bedroomValue = Element.getPageElement(testConfig, How.xPath, bedroomValueLoc.replace("count", String.valueOf(i + 1)));

			String bathroomValueLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'specs-item')]/div/div/span)[4]";
			WebElement bathroomValue = Element.getPageElement(testConfig, How.xPath, bathroomValueLoc.replace("count", String.valueOf(i + 1)));

			String addressLoc = ".//div[contains(@class,'card-subtitle')]/span";
			List<WebElement> addressList = Element.getListOfElements(testConfig, How.xPath, addressLoc);

			//String pricingContent = ".//section[@id='pricing-and-availability']//div[contains(text(),'$')] | .//section[@id='pricing-and-availability']//div[contains(@class,'car-type-brds-v2-base-700')]";
			String pricingContent = ".//section[@id='pricing-and-availability']//div[contains(@class,'car-type-brds-v2-base-700')]";
			List<WebElement> pricing = Element.getListOfElements(testConfig, How.xPath, pricingContent);

			testConfig.logComment("****************** Looking for data for card no " + ( i+ 1) + " ******************");

			if (!neighborhoodNameOverQMICard.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Neighborhood name displaying over card as " + neighborhoodNameOverQMICard.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Neighborhood name displaying over card value as blank... failing the scenario");
			}

			if (planNameOverCard != null && !planNameOverCard.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Plan name displaying over card as " + planNameOverCard.getAttribute("innerText"));
			} else {
				if(!pricing.get(i).getAttribute("innerText").contains("$")) {
					testConfig.logPass("Getting Plan name displaying over card as blank beacuse the pricing status is " + pricing.get(i).getAttribute("innerText"));
				} else {
					testConfig.logFail("Getting Plan name displaying over card value as blank... failing the scenario");
				}
			}

			if (!qmiNameOverCard.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting QMI name displaying over card as " + qmiNameOverCard.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting QMI name displaying over card value as blank... failing the scenario");
			}

			if (!addressList.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Address displaying over card as " + addressList.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Address displaying over card value as blank... failing the scenario");
			}

			if (!homeTypeList.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Home Type displaying over card as " + homeTypeList.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Home Type displaying over card value as blank... failing the scenario");
			}

			if (!sqFtRange.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Sq Ft Range displaying over card as " + sqFtRange.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Sq Ft Range displaying over card value as blank... failing the scenario");
			}

			if (!bedroomValue.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Bedroom value displaying over card as " + bedroomValue.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Bedroom value displaying over card value as blank... failing the scenario");
			}

			if (!bathroomValue.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Bathroom name displaying over card as " + bathroomValue.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Neighborhood name displaying over card value as blank... failing the scenario");
			}

			if (!pricing.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting pricing displaying over card as " + pricing.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting pricing displaying over card value as blank... failing the scenario");
			}
		}
	}

	public void verifySectionTitle(String sectionTitle) {

		String faqSectionHeading = ".//section[@id='faq']//h2";
		WebElement faqHeading = Element.getPageElement(testConfig, How.xPath, faqSectionHeading);
		String faqHeadingValue = Element.getText(testConfig, faqHeading, "Neighborhood name over page");
		Helper.compareEquals(testConfig, "Redirection being done to correct neighborhood", sectionTitle, faqHeadingValue);

	}

	public void verifySVGsDisplayingForCards(ResultsTab resultsTab) {

		String homeCardsCount = "";
		//String homeTypeSvgLoc = "";
		String sqFtSvgLoc = "", bedroomSvgLoc = "", bathroomSvgLoc = "";

		Element.click(testConfig, homePlanList, "Home Plan list");
		Browser.wait(testConfig, 2);

		switch (resultsTab) {
		case Homes:
			homeCardsCount = ".//div[contains(@class,'flex flex-col')]//button/span[text()='Homes']/../span[2]";
			//homeTypeSvgLoc = ".//div[contains(@class,'whitespace-nowrap')]/parent::div/parent::a/div[2]/div[2]//*[name()='svg']";
			sqFtSvgLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'card-specs')]//div/span/*[name()='svg'])[2]";
			bedroomSvgLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'card-specs')]//div/span/*[name()='svg'])[3]";
			bathroomSvgLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'card-specs')]//div/span/*[name()='svg'])[4]";
			break;

		case Plans:
			homeCardsCount = ".//div[contains(@class,'flex flex-col')]//button/span[text()='Plans']/../span[2]";
			//homeTypeSvgLoc = ".//div[contains(@class,'whitespace-nowrap')]/parent::div/parent::a/div[3]/div//*[name()='svg']";
			sqFtSvgLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'card-specs')]//div/span/*[name()='svg'])[2]";
			bedroomSvgLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'card-specs')]//div/span/*[name()='svg'])[3]";
			bathroomSvgLoc = "((.//div[contains(@class,'card-container')])[count]//div[contains(@class,'card-specs')]//div/span/*[name()='svg'])[4]";
			break;
		}

		WebElement homesCount = Element.getPageElement(testConfig, How.xPath, homeCardsCount);
		Element.click(testConfig, homesCount, "Homes count");

		String cardsLocator = ".//section[@id='pricing-and-availability']//div[contains(@class,'transition-all')][contains(@class,'inline-block')]";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, cardsLocator);

		for (int i = 0; i < allCards.size(); i++) {

			testConfig.logComment("****************** Looking for SVG Data for card no " + (i + 1) + " ******************");

			//List<WebElement> homeTypeSvgList = Element.getListOfElements(testConfig, How.xPath, homeTypeSvgLoc);
			/*if (homeTypeSvgList.get(i).isDisplayed()) {
				testConfig.logPass("Verified SVG displaying correctly for Home Type for card no " + (i + 1));
			} else {
				testConfig.logFail("Failed to verify SVG displaying correctly for Home Type for card no " + (i + 1));
			}*/

			Browser.wait(testConfig, 1);
			WebElement sqFtSvgList = Element.getPageElement(testConfig, How.xPath, sqFtSvgLoc.replace("count", String.valueOf(i + 1)));
			if (sqFtSvgList.isDisplayed()) {
				testConfig.logPass("Verified SVG displaying correctly for Square Footage for card no " + (i + 1));
			} else {
				testConfig.logFail("Failed to verify SVG displaying correctly for Square Footage for card no " + (i + 1));
			}

			Browser.wait(testConfig, 1);
			WebElement bedroomSvgList = Element.getPageElement(testConfig, How.xPath, bedroomSvgLoc.replace("count", String.valueOf(i + 1)));
			if (bedroomSvgList.isDisplayed()) {
				testConfig.logPass("Verified SVG displaying correctly for Bedroom for card no " + (i + 1));
			} else {
				testConfig.logFail("Failed to verify SVG displaying correctly for Bedroom for card no " + (i + 1));
			}

			Browser.wait(testConfig, 1);
			WebElement bathroomSvgList = Element.getPageElement(testConfig, How.xPath, bathroomSvgLoc.replace("count", String.valueOf(i + 1)));
			if (bathroomSvgList.isDisplayed()) {
				testConfig.logPass("Verified SVG displaying correctly for Bathroom for card no " + (i + 1));
			} else {
				testConfig.logFail("Failed to verify SVG displaying correctly for Bathroom for card no " + (i + 1));
			}

			Browser.wait(testConfig, 1);
			String rightBtnLocator = ".//button[contains(@class,'right-0 z-20')]";
			WebElement rightBtn = Element.getPageElement(testConfig, How.xPath, rightBtnLocator);
			try {
				if(rightBtn.isEnabled()) {
					Element.click(testConfig, rightBtn, "Right navigational button");
				}
			} catch (Exception e) {
				testConfig.logComment("Reached to end of the list");
			}
			Browser.wait(testConfig, 1);
		}
	}

	public void verifySectionContent(String sectionTitle, String sectionSubHeading, String description) {

		String titleOverPage = ".//section[@id='contact-us']//h3[contains(@class,'text-sm')]";
		String subHeadingOverPage = ".//section[@id='contact-us']//h2[contains(@class,'text-3xl')]";
		String descriptionOverPage = ".//section[@id='contact-us']//div[contains(@class,'text-xl sdps-font-light')]";

		WebElement subHeadingPage = Element.getPageElement(testConfig, How.xPath, subHeadingOverPage);
		WebElement titlePage = Element.getPageElement(testConfig, How.xPath, titleOverPage);
		WebElement descriptionPage = Element.getPageElement(testConfig, How.xPath, descriptionOverPage);

		Helper.compareEquals(testConfig, "Section sub heading", sectionSubHeading, subHeadingPage.getText());
		Helper.compareEquals(testConfig, "Section heading", sectionTitle, titlePage.getText());
		Helper.compareEquals(testConfig, "Description over page", description, descriptionPage.getText());
	}

	public void verifyButtonsDisplaying() {

		try {
			WebElement scheduleATourBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='contact-us']//button[contains(@class,'btn primary')]");
			if (scheduleATourBtn.isDisplayed()) {
				testConfig.logPass("Getting Schedule A Tour button displaying correctly");
			} else {
				testConfig.logFail("Failed to verify that Schedule A Tour button is displaying correctly");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that Schedule A Tour button is displaying correctly");
		}

		try {
			WebElement requestInfoBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='contact-us']//button[contains(@class,'secondary lg')]");
			if (requestInfoBtn.isDisplayed()) {
				testConfig.logPass("Getting Request Information button displaying correctly");
			} else {
				testConfig.logFail("Failed to verify that Request Information button is displaying correctly");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that Request Information button is displaying correctly");
		}

		try {
			WebElement getPreQualifiedBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='contact-us']//button[contains(@class,'secondary md')]");
			if (getPreQualifiedBtn.isDisplayed()) {
				testConfig.logPass("Getting Get Prequalified button displaying correctly");
			} else {
				testConfig.logFail("Failed to verify that Get Prequalified button is displaying correctly");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that Get Prequalified button is displaying correctly");
		}

	}
}
