package EXSquared.Brookfield;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;
import Utils.Helper;

public class CommunityPage extends BRPHelper {

	public enum CommunityHeaders {
		Overview, NeighborhoodList, Map, FAQs
	}

	@FindBy(xpath = ".//button[text()='Overview']")
	private WebElement communityOverviewTab;

	@FindBy(xpath = ".//button[contains(@id,'neighborhood-results')]")
	private WebElement neighborhoodResultsTab;

	@FindBy(xpath = ".//button[text()='Map']")
	private WebElement mapHeaderTab;

	@FindBy(xpath = ".//button[text()='FAQs']")
	private WebElement faqHeaderTab;

	@FindBy(xpath = ".//button[text()='Schedule a Tour']")
	private WebElement scheduleATourBtn;

	@FindBy(xpath = ".//button[text()=' Request Information ']")
	private WebElement requestInfoBtn;

	@FindBy(xpath = ".//div[text()=' GET PRE-QUALIFIED ']")
	private WebElement getPreQualifiedBtn;

	public CommunityPage(Config testConfig) {

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
			testConfig.putRunTimeProperty("CookieSetting", "No");
			testConfig.logComment("Accept cookie section not displayed");
		}
		Browser.waitForPageLoad(testConfig, communityOverviewTab);
	}

	public NeighborhoodPage navigateToNeighborhoodPage() {

		String neighborhoodName = "";
		
//		String neighborhoodNameLoc = "(.//section[@id='neighborhood-results']//a//div[contains(@class,'tracking-wide car-pt-1')])[1]/span";
		String neighborhoodNameLoc = "(.//section[@id='neighborhood-results']//a[contains(text(),'Learn More')]//ancestor::li//div[contains(@class,'tracking-wide car-pt-1')])[1]/span";
		List<WebElement> name = Element.getListOfElements(testConfig, How.xPath, neighborhoodNameLoc);

		for (Iterator<WebElement> iterator = name.iterator(); iterator.hasNext();) {
			WebElement webElement = (WebElement) iterator.next();
			neighborhoodName += webElement.getAttribute("innerText") + " ";
		}

		testConfig.putRunTimeProperty("NeighborhoodName", neighborhoodName.trim());

		List<WebElement> neighborhoodCards = Element.getListOfElements(testConfig, How.xPath,
				".//section[@id='neighborhood-results']//a[contains(text(),'Learn More')]");
		if (neighborhoodCards.size() == 0) {
			testConfig.logFail("No neighborhood found for the community.. hence failing the test case");
		} else {
			Element.clickThroughJS(testConfig, neighborhoodCards.get(0), "Neighborhood link over Community page");
		}
		
		return new NeighborhoodPage(testConfig);
	}

	public void verifyJumpAnchorTags(CommunityHeaders communityHeaders) {

		String tabLocator = "";
		String sectionLocator = "";

		switch (communityHeaders) {
		case Overview:
			tabLocator = ".//button[text()='Overview']";
			sectionLocator = "(.//div[contains(@class,'md:breadcrumbText')])[2]/../../../..";
			break;

		case FAQs:
			tabLocator = ".//button[text()='FAQs']";
			sectionLocator = ".//section[contains(@class,'brp-section-faq')]";
			break;

		case Map:
			tabLocator = ".//button[text()='Map']";
			sectionLocator = ".//div[contains(@class,'circular-image')]/../../..";
			break;

		case NeighborhoodList:
			tabLocator = ".//button[contains(@id,'neighborhood-results')]";
			sectionLocator = ".//section[contains(@class,'brp-section-homes-and-plans')]";
			break;
		}

		try {
			WebElement tabElement = Element.getPageElement(testConfig, How.xPath, tabLocator);
			WebElement sectionElement = Element.getPageElement(testConfig, How.xPath, sectionLocator);

			Element.click(testConfig, tabElement, communityHeaders.toString() + " tab from community nav header");
			Browser.wait(testConfig, 3);
			Helper.compareContains(testConfig,
					"Jump anchor tag associated with " + communityHeaders.toString() + " header tab",
					sectionElement.getAttribute("id"), tabElement.getAttribute("id"));
		} catch (Exception e) {
			testConfig.logComment("Not having " + communityHeaders.toString() + " tab over community nav header so skipping that case");
		}
	}



	public void verifyBreadcrumbDisplaying(String[] expectedBreadcrumbArr) {

		String allBreadcrumbsLocator = ".//section[@class='bg-brp-blue-200']//div[contains(@class,'md:breadcrumbText')]//a";
		List<WebElement> allBreadcrumbs = Element.getListOfElements(testConfig, How.xPath, allBreadcrumbsLocator);

		for (int i = 0; i < expectedBreadcrumbArr.length; i++) {
			Helper.compareEquals(testConfig, "Breadcrumb item " + (i + 1), expectedBreadcrumbArr[i].toUpperCase(),
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

	public void verifyCommunityDescriptionExpandCollapse() {

		String contentLocator = "(.//section[@id='community-description'])//div[contains(@class,'transition-all')]";
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

	public FYHPage verifyDetailsAndClickBrowseCommunityMap(String title, String subtitle) {

		String titleLocator = ".//section//h2[contains(text(),'Map')]";
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

	public NeighborhoodPage navigateToRequiredNeighborhoodPage(String neighborhoodName) {

		Browser.wait(testConfig, 2);
		Element.click(testConfig, neighborhoodResultsTab, "Neighborhood Results tab");
		Browser.wait(testConfig, 2);
		String neighborhoodNameLoc = ".//section[@id='neighborhood-results']//div[contains(@class,'tracking-wide car-pt-1')]/span[1]";
		List<WebElement> name = Element.getListOfElements(testConfig, How.xPath, neighborhoodNameLoc);
		
		for (Iterator<WebElement> iterator = name.iterator(); iterator.hasNext();) {
			WebElement webElement = (WebElement) iterator.next();
			if (neighborhoodName.equals(webElement.getAttribute("innerText").trim())) {
				Element.click(testConfig, webElement, neighborhoodName + " neighborhood card");
				break;
			}
		}
		testConfig.putRunTimeProperty("NeighborhoodName", neighborhoodName);
		
		return new NeighborhoodPage(testConfig);

	}

	public void verifyRedirectionToCorrectCommunity(String communityNameFromPlan) {

		String commNameLocator = ".//button[contains(@class,'whitespace-nowrap')][contains(@class,'border-brp-blue-gray-200')]";
		WebElement communityName = Element.getPageElement(testConfig, How.xPath, commNameLocator);
		String communityNameOverPage = Element.getText(testConfig, communityName, "Community name over page");
		Helper.compareEquals(testConfig, "Redirection being done to correct community", communityNameFromPlan, communityNameOverPage.trim());
	}

	public void verifyCommunityNameHeaderTag(String commName) {

		String locator = ".//*[text()='" + commName + "'][contains(@class,'font-semibold')]";
		WebElement commNameElement = Element.getPageElement(testConfig, How.xPath, locator);
		Helper.compareEquals(testConfig, "Tag associated with Community Name element", "h1", commNameElement.getTagName());

	}

	public void verifyHomeTypeInformationDisplaying() {

		String homeTypeLabel = ".//h5[text()='Home Types:']";
		try {
			WebElement homeType = Element.getPageElement(testConfig, How.xPath, homeTypeLabel);
			if (!homeType.isDisplayed()) {
				testConfig.logFail("Not getting Home Type section displaying for the community");
			} else {
				testConfig.logPass("Verified Home Type section displaying for the community");
				String homeTypeValues = ".//h5[text()='Home Types:']/parent::div//ul/li/span[1]";
				List<WebElement> allHomeType = Element.getListOfElements(testConfig, How.xPath, homeTypeValues);
				if(allHomeType.size() == 0) {
					testConfig.logFail("No Home Type information is displaying for the community");
				} else {
					testConfig.logPass("Verified Home Type information is displaying for the community as below :");
					for (int i = 0; i < allHomeType.size(); i++) {
						if(!allHomeType.get(i).getText().isEmpty()) {
							testConfig.logPass("Home Type information item " + ( i + 1) + " : " + allHomeType.get(i).getText());
						} else {
							testConfig.logFail("Getting Home Type information item " + ( i + 1) + " as empty");
						}
					}
				}
			}
		} catch (Exception e) {
			testConfig.logFail("Not getting Home Type section displaying for the community");
		}

	}

	public void verifyAmenitiesInformationDisplaying() {

		String amenitiesLabel = ".//h5[contains(text(),'Amenities:')]";
		try {
			WebElement amenities = Element.getPageElement(testConfig, How.xPath, amenitiesLabel);
			if (!amenities.isDisplayed()) {
				testConfig.logFail("Not getting Amenities section displaying for the community");
			} else {
				testConfig.logPass("Verified Amenities section displaying for the community");
				String amenitiesValues = ".//h5[contains(text(),'Amenities:')]/parent::div/ul/li";
				List<WebElement> amenitiesType = Element.getListOfElements(testConfig, How.xPath, amenitiesValues);
				if(amenitiesType.size() == 0) {
					testConfig.logFail("No Amenities information is displaying for the community");
				} else {
					testConfig.logPass("Verified Amenities information is displaying for the community as below :");
					for (int i = 0; i < amenitiesType.size(); i++) {
						testConfig.logComment("Amenities information item : " + ( i + 1) + " as " + amenitiesType.get(i).getText());
					}
				}
			}
		} catch (Exception e) {
			testConfig.logFail("Not getting Amenities section displaying for the community");
		}

	}

	public void verifyAvidRatingModule(String expectedUrl, String expectedAvidReview) {

		String learnMoreLinkLocator = ".//a[contains(@href,'" + expectedUrl + "')]";
		WebElement learnMoreLinkLink = Element.getPageElement(testConfig, How.xPath, learnMoreLinkLocator);
		Helper.compareContains(testConfig, "Link associated with View Report button", expectedUrl, learnMoreLinkLink.getAttribute("href"));	

		String avidTextValue = ".//div[contains(text(),'Customer Reviews')]";
		WebElement avidText = Element.getPageElement(testConfig, How.xPath, avidTextValue);
		Helper.compareContains(testConfig, "Link associated with View Report button", expectedAvidReview, avidText.getText());	

		try {
			verifyURLStatus(learnMoreLinkLink.getAttribute("href"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void verifyImageDisplayingCorrectly() {

		String imageLocator = ".//section[@class='bg-brp-blue-200']/div[1]/div/img";
		WebElement image = Element.getPageElement(testConfig, How.xPath, imageLocator);
		
		String imageSrc = image.getAttribute("src");
		verifyURLAsPerDomain(imageSrc);
	}

	public void verifyCloseToEverythingSection(String expectedHeading) {

		String sectionLocator = ".//section[@id='community-features']";
				
		try {
			WebElement sectionElement = Element.getPageElement(testConfig, How.xPath, sectionLocator);
			if (sectionElement.isDisplayed()) {
				testConfig.logPass("Verified 'Close To Everything' section is displaying correctly");
			} else {
				testConfig.logFail("Failed to verify that 'Close To Everything' section is displaying correctly");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that 'Close To Everything' section is displaying correctly");
		}
		
		String titleHeading = ".//section[@id='community-features']//h2[1]";
		WebElement title = Element.getPageElement(testConfig, How.xPath, titleHeading);
		Helper.compareEquals(testConfig, "Section Heading", expectedHeading, title.getText().trim());
		
		String allAmenitiesLocator = ".//section[@id='community-features']//div[2]/ul/li/div";
		List<WebElement> allAmenities = Element.getListOfElements(testConfig, How.xPath, allAmenitiesLocator);
		
		if(allAmenities.size() == 0) {
			testConfig.logFail("No amenities displaying under 'Close To Everything' section");
		} else {
			testConfig.logPass("Amenities are correctly displaying under 'Close To Everything' as below:");
			for (int i = 0; i < allAmenities.size(); i++) {
				testConfig.logComment(allAmenities.get(i).getText());
			}
		}
	}

	public void verifyPhotosTabHighlighted(String expectedBorderColor) {

		String btnLocator = ".//button/h2[text()='Photos']/parent::button";
		WebElement photosTab = Element.getPageElement(testConfig, How.xPath, btnLocator);
		String cssValue = photosTab.getCssValue("background-color");
		String hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color for Photos tab",	expectedBorderColor, hexcolor);
		
	}

	public FYHPage clickOnStateBreadcrumbItem(String stateName) {

		String allBreadcrumbsLocator = ".//section[@class='bg-brp-blue-200']//div[contains(@class,'md:breadcrumbText')]//a";
		List<WebElement> allBreadcrumbs = Element.getListOfElements(testConfig, How.xPath, allBreadcrumbsLocator);

		for (Iterator<WebElement> iterator = allBreadcrumbs.iterator(); iterator.hasNext();) {
			WebElement breadcrumbItem = (WebElement) iterator.next();
			if(breadcrumbItem.getText().contains("FIND YOUR HOME")) {
				Element.click(testConfig, breadcrumbItem, breadcrumbItem.getText() + " breadcrumb item");
				break;
			}
		}
		Browser.wait(testConfig, 2);
		Helper.removeCookies(testConfig);
		testConfig.putRunTimeProperty("RedirectionValue", "yes");
		return new FYHPage(testConfig);
	}

	public FYHPage verifyBrowseCommMapLinkFunctionality(String expectedBackgroundColor) {

		String buttonLocator = ".//section[@id='neighborhood-results']//h2/parent::div//a[contains(@class,'lg')]";
		WebElement browseCommMap = Element.getPageElement(testConfig, How.xPath, buttonLocator);
		
		String cssValue = browseCommMap.getCssValue("background-color");
		String rgbVal = cssValue.substring(cssValue.indexOf("rgb"));
		String hexcolor = Color.fromString(rgbVal).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Border color for Search Field when no value provided", expectedBackgroundColor.toUpperCase(),
				hexcolor.toUpperCase());
		
		Element.click(testConfig, browseCommMap, "Browse community map");
		Helper.removeCookies(testConfig);
		return new FYHPage(testConfig);
	}

}
