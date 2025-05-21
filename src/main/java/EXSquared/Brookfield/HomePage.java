package EXSquared.Brookfield;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;
import Utils.Helper;

public class HomePage extends BRPHelper {

	public enum CookieSettingsTabs {
		YourPrivacy, StrictlyNecessaryCookies, PerformanceCookies, FunctionalCookies, TargetingCookies
	}
	
	public enum WhereWeBuildLocation {
		UnitedStates, Canada
	}

	@FindBy(xpath = ".//h1[contains(text(),'Find')]")
	private WebElement newHomeSearchText;

	@FindBy(css = "header>div>a>img")
	private WebElement logoImage;

	@FindBy(css = "header>div>a")
	private WebElement logoURL;

	@FindBy(css = "p.sitewide-banner-para")
	private WebElement htmlBanner;

	@FindBy(xpath = ".//section//div[@aria-roledescription='carousel']//div[contains(@class,'text-brds-v2-navy-80')]")
	private WebElement promotionsText;

	@FindBy(xpath = ".//button[contains(text(),'Where we build')]")
	private WebElement whereWeBuildBtn;

	@FindBy(xpath = ".//button[contains(@class,'hover:nav-bg-brds-v1-grayscale-blue-dark-ft')]")
	private WebElement menuBar;

	@FindBy(xpath = ".//section//div[@aria-roledescription='carousel']//a[contains(@class,'promo')]")
	private WebElement promotionsCTA;

	@FindBy(xpath = ".//section//div[@aria-roledescription='carousel']//img[contains(@class,'promo')]")
	private WebElement promotionsImage;

	@FindBy(css = "section div[class*='xl:col-span-4'] h3")
	private WebElement bestPlacesHeading;
	
	@FindBy(css = "div[class*='mig-grid'] h3")
	private WebElement BRPDiffHeading;

	@FindBy(css = "section div[class*='xl:col-span-4']>div>p")
	private WebElement bestPlacesSubHeading;

	@FindBy(css = "section div[class='slider'] img")
	private List<WebElement> sliderAllImages;

	@FindBy(css = "section div[class='slider']>div")
	private List<WebElement> sliderImagesParentNode;

	@FindBy(css = "section ul[aria-label='Select a slide to show'] li[class*='progress-bar'] button")
	private List<WebElement> sliderAllProgressBars;

	@FindBy(css = "section div[class*='xl:col-span-4'] span[class*='inline'] button")
	private WebElement whereWeBuildButton;
	
	@FindBy(css = "div[class*='mig-grid'] ul li img")
	private List<WebElement> BRPDiffImages;
	
	@FindBy(css = "div[class*='mig-grid'] ul li h4")
	private List<WebElement> BRPDiffGridHeadings;
	
	@FindBy(css = "div[class*='mig-grid'] ul li div")
	private List<WebElement> BRPDiffGridDescriptions;
	
	@FindBy(css = "div[class*='mig-grid'] a")
	private WebElement BRPDiffLearnMoreCTA;

	@FindBy(css = "div[class*='mig-grid'] ul li")
	private List<WebElement> BRPDiffGridData;
	
	@FindBy(css = "div[class*='dmp-text'] h3")
	private WebElement topLocationSectionHeading;
	
	@FindBy(css = "div[class*='dmp-text'] p")
	private WebElement topLocationSectionSubHeading;
	
	@FindBy(css = "div[class*='dmp-map-container'] ul>li[class*='dmp-region']>a")
	private List<WebElement> regionRedirectionLink;

	@FindBy(css = "div[class*='dmp-map-container'] ul>li[class*='dmp-region']")
	private List<WebElement> regionNames;
	
	@FindBy(css = "div.brp-page-container.gap-8 h3")
	private WebElement blogSectionHeading;
	
	@FindBy(css = "div.brp-page-container.gap-8 div>ul>li img")
	private List<WebElement> blogSectionCardImages;
	
	@FindBy(css = "div.brp-page-container.gap-8 div>ul>li span")
	private List<WebElement> blogCardCategoryLabel;
	
	@FindBy(css = "div.brp-page-container.gap-8 div>ul>li h5")
	private List<WebElement> blogCardHeading;
	
	@FindBy(css = "div.brp-page-container.gap-8 div.items-center a")
	private WebElement exploreAllResourcesCTA;
	
	@FindBy(css = "header>h6")
	private WebElement getInTouchSubHeader;

	@FindBy(css = "header>h3")
	private WebElement getInTouchHeader;
	
	@FindBy(css = "li.profile-img>img")
	private List<WebElement> getInTouchProfiles;
	
	@FindBy(css = "div.bg-cover.bg-center")
	private WebElement getInTouchBGImage;
	
	@FindBy(xpath = ".//div[@class='brp-page-container']/div/a")
	private WebElement getInTouchCTA;
	
	
	public HomePage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;

		String homeurl = testConfig.getRunTimeProperty("BrookfieldHomePage");
		Browser.navigateToURL(testConfig, homeurl);

		/*if (!(testConfig.getRunTimeProperty("Environment") == "BRPProd")) {
			try {
				Runtime.getRuntime().exec(System.getProperty("user.dir") + "\\Parameters\\HandleAuthentication.exe");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/

		try {
			Browser.waitWithoutLogging(testConfig, 2);
			WebElement acceptCookies = Element.getPageElement(testConfig, How.css, "button#onetrust-accept-btn-handler");
			Element.click(testConfig, acceptCookies, "One trust accept cookies button");
			testConfig.putRunTimeProperty("CookieSetting", "Yes");
		} catch (Exception e) {
			testConfig.putRunTimeProperty("CookieSetting", "No");
			testConfig.logComment("Accept cookie section not displayed");
		}

		PageFactory.initElements(this.testConfig.driver, this);
		Browser.waitForPageLoad(testConfig, newHomeSearchText);

	}

	public void verifyLocationsWhereBrookfieldBuilds(String[] expectedLocs) {

		WebElement inputSearch = Element.getPageElement(testConfig, How.css,
				"input[class*='fyh-input'][aria-labelledby='Search']");
		Element.click(testConfig, inputSearch, "Search input box");
		Browser.wait(testConfig, 2);

		List<WebElement> allLocations = Element.getListOfElements(testConfig, How.css,
				"li.srch-items-center div.srch-flex-grow");
		//Helper.compareEquals(testConfig, "Expected count of the location", expectedLocs.length, allLocations.size());
		
		// Making this as Failed intentionally
		Helper.compareEquals(testConfig, "Expected count of the location", expectedLocs.length, allLocations.size() - 1);

		for (int i = 0; i < expectedLocs.length; i++) {
			Helper.compareEquals(testConfig, "Location " + (i + 1) + " as ", expectedLocs[i],
					allLocations.get(i).getText());
		}

	}

	public ContactUsPage clickOnContactUsLink() {

		Browser.wait(testConfig, 2);
		Element.clickThroughJS(testConfig, menuBar, "Header top nav menu bar");
		Browser.wait(testConfig, 2);

		WebElement contactUs = Element.getPageElement(testConfig, How.xPath, ".//a[@aria-label='Contact']");
		Element.clickThroughJS(testConfig, contactUs, "Contact Us link from Header");
		Helper.removeCookies(testConfig);
		return new ContactUsPage(testConfig);
	}

	public AboutPage clickOnAboutLink() {

		Browser.wait(testConfig, 2);
		Element.clickThroughJS(testConfig, menuBar, "Header top nav menu bar");
		Browser.wait(testConfig, 2);

		WebElement aboutBtn = Element.getPageElement(testConfig, How.xPath, ".//a[@aria-label='About']");
		Element.clickThroughJS(testConfig, aboutBtn, "About link from Header");
		Helper.removeCookies(testConfig);
		return new AboutPage(testConfig);
	}

	public Object clickOnCareersLink() {

		Browser.wait(testConfig, 2);
		Element.clickThroughJS(testConfig, menuBar, "Header top nav menu bar");
		Browser.wait(testConfig, 2);

		Object obj = null;
		WebElement contactUs = Element.getPageElement(testConfig, How.xPath, ".//div/ul/li/a[@aria-label='Careers']");
		Element.clickThroughJS(testConfig, contactUs, "Careers link from Header");

		try {
			obj = new CareersPage(testConfig);
		} catch (Exception e) {
			//getBase64UserNamePwdNetworkTab();
			Browser.navigateToURL(testConfig, homeurl + "careers");
			obj = new CareersPage(testConfig);
		}
		return obj;
	}

	public Object performSearch(String text, String expectedSearch, String abc) {

		Browser.wait(testConfig, 2);
		Object obj = null;
		WebElement inputSearch = Element.getPageElement(testConfig, How.css,
				"input[class*='fyh-input'][aria-labelledby='Search']");
		try {
			Element.enterDataAfterClick(testConfig, inputSearch, text.substring(0, text.length() / 2), "Search value");
		} catch (Exception e) {
			try {
				WebElement acceptCookies = Element.getPageElement(testConfig, How.css, "button#onetrust-accept-btn-handler");
				Element.click(testConfig, acceptCookies, "One trust accept cookies button");
				testConfig.putRunTimeProperty("CookieSetting", "Yes");
				Element.enterDataAfterClick(testConfig, inputSearch, text.substring(0, text.length() / 2), "Search value");
			} catch (Exception ex) {
				testConfig.putRunTimeProperty("CookieSetting", "No");
				testConfig.logComment("Accept cookie section not displayed");
				Element.enterDataAfterClick(testConfig, inputSearch, text.substring(0, text.length() / 2), "Search value");
			}
		}

		Browser.wait(testConfig, 2);
		Element.enterDataWithoutClear(testConfig, inputSearch, text.substring((text.length() / 2), text.length()),
				"Search value");
		Browser.wait(testConfig, 7);

		List<WebElement> suggestionList = Element.getListOfElements(testConfig, How.xPath,
				".//li[contains(@class,'srch-items')]/div[2]");

		if(suggestionList.size() == 0) {
			testConfig.driver.navigate().refresh();
			inputSearch = Element.getPageElement(testConfig, How.css,
					"input[class*='fyh-input'][aria-labelledby='Search']");
			Element.enterDataAfterClick(testConfig, inputSearch, text.substring(0, text.length() / 2), "Search value");
			Browser.wait(testConfig, 2);
			Element.enterDataWithoutClear(testConfig, inputSearch, text.substring((text.length() / 2), text.length()),
					"Search value");
			Browser.wait(testConfig, 4);

			suggestionList = Element.getListOfElements(testConfig, How.xPath,
					".//li[contains(@class,'srch-items')]/div[2]");
		}

		Browser.wait(testConfig, 2);
		for (WebElement webElement : suggestionList) {
			if (expectedSearch.equals(webElement.getText())) {
				Element.click(testConfig, webElement, webElement.getText() + " suggestion");
				break;
			}
		}

		if(abc.equals("Plan")) {
			obj = new PlanPage(testConfig);
		} else if (abc.equals("Community")) {
			obj = new CommunityPage(testConfig);
		} else {
			obj = new FYHPage(testConfig);
		}
		return obj;
	}

	public void verifyDisclaimerContent(String[] disclaimer) {

		Browser.waitWithoutLogging(testConfig, 3);
		List<WebElement> copyrightContent = Element.getListOfElements(testConfig, How.xPath,
				".//div[contains(@class,'footer__disclaimer')]/p");

		Helper.compareEquals(testConfig, "First section disclaimer content", disclaimer[0], copyrightContent.get(0).getText());
		Helper.compareEquals(testConfig, "Second section disclaimer content", disclaimer[1], copyrightContent.get(1).getText());
		Helper.compareContains(testConfig, "Third section disclaimer content", disclaimer[2], copyrightContent.get(2).getText());

	}

	public void verifyLogoImageAndURL() {

		String imageSrc = Element.getAttribute(testConfig, logoImage, "src", "Image source");
		String logoUrl = Element.getAttribute(testConfig, logoURL, "href", "Anchor link");

		Helper.compareEquals(testConfig, "URL associated with logo", homeurl, logoUrl);
		verifyURLAsPerDomain(imageSrc);

	}

	public void verifyCompanyLinksDisplayingOverHeader(Map<String, String> expectedLinks) {

		Element.click(testConfig, menuBar, "Header top nav menu bar");
		Browser.wait(testConfig, 2);
		Map<String, String> linksOverPage = new HashMap<String, String>();
		List<WebElement> headerLinks = Element.getListOfElements(testConfig, How.xPath, ".//div[@aria-label='Company']//ul/li/a");
		for (Iterator<WebElement> iterator = headerLinks.iterator(); iterator.hasNext();) {
			WebElement content = (WebElement) iterator.next();
			linksOverPage.put(content.getText(), content.getAttribute("href").replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", ""));
		}

		if(areEqual(expectedLinks, linksOverPage)) {
			testConfig.logPass("Verified header section displays as " + linksOverPage.toString());
		} else {
			testConfig.logFail("Not getting expected header section displays over page " + linksOverPage.toString());
			testConfig.logComment("Expected section : " + expectedLinks.toString());
			testConfig.logComment("Actual section displaying over the page : " + linksOverPage.toString());
		}

		for (Iterator<WebElement> iterator = headerLinks.iterator(); iterator.hasNext();) {
			WebElement content = (WebElement) iterator.next();
			String contentUrl = content.getAttribute("href").replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", "");
			verifyURLAsPerDomain(contentUrl);

		}
	}

	private boolean areEqual(Map<String, String> first, Map<String, String> second) {
		if (first.size() != second.size()) {
			return false;
		}

		return first.entrySet().stream()
				.allMatch(e -> e.getValue().equals(second.get(e.getKey())));
	}

	public void verifyNewsAndBlogLinksDisplayingOverHeader(Map<String, String> expectedLinks) {

		Browser.waitWithoutLogging(testConfig, 2);
		Element.clickThroughJS(testConfig, menuBar, "Header top nav menu bar");
		Browser.wait(testConfig, 2);

		Map<String, String> linksOverPage = new HashMap<String, String>();
		List<WebElement> headerLinks = Element.getListOfElements(testConfig, How.xPath, ".//div[@aria-label='News & Blog']//ul/li/a");
		for (Iterator<WebElement> iterator = headerLinks.iterator(); iterator.hasNext();) {
			WebElement content = (WebElement) iterator.next();
			linksOverPage.put(content.getText(), content.getAttribute("href").replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", ""));
		}

		if(areEqual(expectedLinks, linksOverPage)) {
			testConfig.logPass("Verified header section displays as " + linksOverPage.toString());
		} else {
			testConfig.logFail("Not getting expected header section displays over page " + linksOverPage.toString());
			testConfig.logComment("Expected section : " + expectedLinks.toString());
			testConfig.logComment("Actual section displaying over the page : " + linksOverPage.toString());
		}

		for (Iterator<WebElement> iterator = headerLinks.iterator(); iterator.hasNext();) {
			WebElement content = (WebElement) iterator.next();
			String contentUrl = content.getAttribute("href").replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", "");
			verifyURLAsPerDomain(contentUrl);

		}
	}

	public void verifyHTMLBanner(String expectedText, String expectedUrl) {

		String bannerContent = Element.getText(testConfig, htmlBanner, "Sitewide HTML Banner");
		Helper.compareEquals(testConfig, "Banner content", expectedText, bannerContent);

		WebElement ourCommitment = Element.getPageElement(testConfig, How.css, "p.sitewide-banner-para a");
		String ourCommitmentLink = Element.getAttribute(testConfig, ourCommitment, "href", "Our commitment");
		Helper.compareContains(testConfig, "Link for our commitment", expectedUrl, ourCommitmentLink);
		verifyBannerNotDisplayingAfterClosing();

	}

	private void verifyBannerNotDisplayingAfterClosing() {

		WebElement cancelBtn = Element.getPageElement(testConfig, How.xPath, ".//p[contains(@class,'banner')]/../button");
		Element.clickThroughJS(testConfig, cancelBtn, "Cancel button over Banner section");
		Browser.wait(testConfig, 2);

		try {
			WebElement section = Element.getPageElement(testConfig, How.xPath, ".//p[contains(@class,'banner')]/../..");
			if(section.isDisplayed()) {
				testConfig.logFail("Still getting element to be displayed");
			} else {
				testConfig.logPass("Verified that now html banner is not displaying");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified that now html banner is not displaying");
		}

	}

	public FYHPage clickOnLocationAndHitSearch(String location) {

		WebElement inputSearch = Element.getPageElement(testConfig, How.css,
				"input[class*='fyh-input'][aria-labelledby='Search']");
		Element.clickThroughJS(testConfig, inputSearch, "Search input box");
		Browser.wait(testConfig, 2);

		List<WebElement> allLocations = Element.getListOfElements(testConfig, How.css,
				"li.srch-items-center div.srch-flex-grow");

		for (int i = 0; i < allLocations.size(); i++) {
			if(allLocations.get(i).getText().equals(location)) {
				Element.click(testConfig, allLocations.get(i), allLocations.get(i).getText() + " suggested state location");
				break;
			}
		}

		Browser.wait(testConfig, 3);
		WebElement submitButton = Element.getPageElement(testConfig, How.xPath,
				".//input[contains(@class,'fyh-input')]/../../button[contains(@id,'combobox')]");
		Element.click(testConfig, submitButton, "Search input box");
		Helper.removeCookies(testConfig);
		return new FYHPage(testConfig);
	}

	public FYHPage clickOnLocationAndSearchSecondLevelLocation(String location, String secondLevelLocation) {

		WebElement inputSearch = Element.getPageElement(testConfig, How.css,
				"input[class*='fyh-input'][aria-labelledby='Search']");
		Element.click(testConfig, inputSearch, "Search input box");
		Browser.wait(testConfig, 2);

		List<WebElement> allLocations = Element.getListOfElements(testConfig, How.css,
				"li.srch-items-center div.srch-flex-grow");

		for (int i = 0; i < allLocations.size(); i++) {
			if(allLocations.get(i).getText().equals(location)) {
				Element.click(testConfig, allLocations.get(i), allLocations.get(i).getText() + " suggested state location");
				break;
			}
		}

		Browser.wait(testConfig, 2);

		List<WebElement> secondLevelValues = Element.getListOfElements(testConfig, How.css, "li div.srch-flex-grow");
		for (int i = 0; i < secondLevelValues.size(); i++) {
			if(secondLevelValues.get(i).getText().equals(secondLevelLocation)) {
				Element.click(testConfig, secondLevelValues.get(i), secondLevelValues.get(i).getText() + " second level location");
				break;
			}
		}
		Helper.removeCookies(testConfig);
		testConfig.putRunTimeProperty("RedirectionValue", "yes");
		return new FYHPage(testConfig);
	}

	public EmpowerPage verifyEmpowerVideoBehavior(String expectedTitleForPlayBtn, String expectedPauseBtn, String expectedEmpowerText) {

		WebElement empowerVideoFrame = Element.getPageElement(testConfig, How.xPath, ".//iframe[@allow='encrypted-media']");
		String brightcoveUrl = Element.getAttribute(testConfig, empowerVideoFrame, "src", "Empower video");

		try {
			verifyURLStatus(brightcoveUrl);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String 	textLocator = ".//p[contains(@class,'leading-[1.467] text-brp-blue-100')]";
		WebElement allTextEmpower = Element.getPageElement(testConfig, How.xPath, textLocator);
		Helper.compareContains(testConfig, "Text associated with empower section", expectedEmpowerText, allTextEmpower.getText());

		WebElement tellMeMoreLink = Element.getPageElement(testConfig, How.xPath, ".//a[contains(@href,'empower')]");
		Element.click(testConfig, tellMeMoreLink, "Tell me more link");

		return new EmpowerPage(testConfig);
	}


	public void verifyBlogSection(String expectedTitle) throws UnsupportedEncodingException {

		String sectionTitleLocator = ".//div[contains(@class,'brp-page-container')]/h3[contains(@class,'text-brp-blue-300')]";
		WebElement titleText = Element.getPageElement(testConfig, How.xPath, sectionTitleLocator);
		Helper.compareEquals(testConfig, "Subtitle for Join Our Team section", expectedTitle, titleText.getText());

		WebElement viewMore = Element.getPageElement(testConfig, How.xPath, ".//button[contains(text(),'View More')]");

		if(viewMore.isDisplayed()) {
			testConfig.logPass("Verified initially we are getting 'View More' button to be displayed");
		} else {
			testConfig.logFail("Failure to verify that initially we are getting 'View More' button to be displayed");
		}

		clickOnLinkToLoadAllBlogs();

		WebElement viewAll = Element.getPageElement(testConfig, How.xPath, ".//div/a[contains(text(),'View All')]");

		if(viewAll.isDisplayed()) {
			testConfig.logPass("Verified now we are getting 'View All' button to be displayed");
		} else {
			testConfig.logFail("Failure to verify that now we are getting 'View All' button to be displayed");
		}

		String viewAllLink = Element.getAttribute(testConfig, viewAll, "href", "View all link");
		verifyURLAsPerDomain(viewAllLink);

		Helper.compareEquals(testConfig, "Link associated with View All", homeurl + "news-and-blog", viewAll.getAttribute("href"));

		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, ".//div[contains(@class,'ease-in-out')]/a[contains(@class,'post')]");
		Helper.compareEquals(testConfig, "All blog cards count displaying over page", 9, allCards.size());

	}

	private void clickOnLinkToLoadAllBlogs() {

		WebElement viewMore = Element.getPageElement(testConfig, How.xPath, ".//button[contains(text(),'View More')]");
		Element.click(testConfig, viewMore, "View More button 1st time");
		Browser.wait(testConfig, 2);
		Element.click(testConfig, viewMore, "View More button 2nd time");
		Browser.wait(testConfig, 2);
	}

	public CareersPage verifyJoinOurTeamSection(String expectedSubTitle, String expectedTitle) throws UnsupportedEncodingException {

		WebElement subtitleText = Element.getPageElement(testConfig, How.xPath, ".//section[@class='xl:brp-page-container']//h6");
		WebElement titleText = Element.getPageElement(testConfig, How.xPath, ".//section[@class='xl:brp-page-container']//h5");

		Helper.compareEquals(testConfig, "Subtitle for Join Our Team section", expectedSubTitle, subtitleText.getText());
		Helper.compareEquals(testConfig, "Title for Join Our Team section", expectedTitle, titleText.getText());

		String backgroundImageLoc = ".//section[@class='xl:brp-page-container']/div[contains(@class,'bg-no-repeat')]";
		WebElement backgroundImage = Element.getPageElement(testConfig, How.xPath, backgroundImageLoc);
		String htmlContent = Element.getAttribute(testConfig, backgroundImage, "style", "Style html for the image section");
		String imageUrl = htmlContent.substring(htmlContent.indexOf("(") + 2, htmlContent.indexOf(")") - 1);
		verifyURLAsPerDomain(imageUrl);

		Browser.wait(testConfig, 2);

		WebElement joinOurTeamLink = Element.getPageElement(testConfig, How.xPath, ".//a[contains(text(),'join our team')]");
		Helper.compareEquals(testConfig, "Link associated with Join Our Team button", homeurl + "careers", joinOurTeamLink.getAttribute("href"));
		verifyURLAsPerDomain(joinOurTeamLink.getAttribute("href"));
		Element.click(testConfig, joinOurTeamLink, "Join Our Team link");

		Helper.removeCookies(testConfig);
		return new CareersPage(testConfig);
	}

	public MyTimeTourPage verifymyTimeTourSection(String expectedMyTimeSectionTitle) throws UnsupportedEncodingException {

		WebElement sectionTitle = Element.getPageElement(testConfig, How.xPath, ".//h3[contains(text(),'Tour new')]");
		Helper.compareContains(testConfig, "Title for My Time Tour section", expectedMyTimeSectionTitle, sectionTitle.getText().trim());

		String imageLoc = ".//h3[contains(text(),'Tour new')]/ancestor::section//img";
		WebElement image = Element.getPageElement(testConfig, How.xPath, imageLoc);
		String imageUrl = image.getAttribute("src");
		verifyURLAsPerDomain(imageUrl);

		String descriptionLocator = ".//h3[contains(text(),'Tour new')]/ancestor::section//p";
		WebElement description = Element.getPageElement(testConfig, How.xPath, descriptionLocator);

		if(description.getAttribute("innerText").isEmpty()) {
			testConfig.logFail("Not getting description text displaying for the myTime section");
		} else {
			testConfig.logPass("Verified the description text is displaying as " + description.getAttribute("innerText"));
		}

		WebElement scheduleATourLink = Element.getPageElement(testConfig, How.xPath, ".//a[contains(text(),'Schedule a tour')]");
		Helper.compareEquals(testConfig, "Link associated with Schedule a tour button", homeurl + "mytime", scheduleATourLink.getAttribute("href"));

		Element.click(testConfig, scheduleATourLink, "Schedule A Tour button");

		return new MyTimeTourPage(testConfig);
	}

	public FYHPage selectCurrentLocation() {

		WebElement inputSearch = Element.getPageElement(testConfig, How.css,
				"input[class*='fyh-input'][aria-labelledby='Search']");
		Element.click(testConfig, inputSearch, "Search input box");
		Browser.wait(testConfig, 1);
		WebElement currentLocation = null;
		try {
			currentLocation = Element.getPageElement(testConfig, How.xPath, ".//*[contains(text(),'Current Location')][@aria-labelledby='Current Location']");
			if(currentLocation.isDisplayed()) {
				testConfig.logPass("Verified Current Location link is displaying in the dropdown");
			} else {
				testConfig.logFail("Failed to verify that Current Location link is displaying in the dropdown");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that Current Location link is displaying in the dropdown");
		}

		currentLocation = Element.getPageElement(testConfig, How.xPath, ".//*[contains(text(),'Current Location')][@aria-labelledby='Current Location']");
		Element.click(testConfig, currentLocation, "Current location option");
		Helper.removeCookies(testConfig);
		return new FYHPage(testConfig);
	}

	public BlogPage clickOnBlogLink() {
		Browser.wait(testConfig, 2);
		Element.clickThroughJS(testConfig, menuBar, "Header top nav menu bar");
		Browser.wait(testConfig, 2);

		WebElement blogFooterLink = Element.getPageElement(testConfig, How.xPath, ".//a[@aria-label='All Topics']");
		Element.clickThroughJS(testConfig, blogFooterLink, "Blog link from Footer section");
		Helper.removeCookies(testConfig);
		return new BlogPage(testConfig);
	}

	public void verifyFooterLinks(String[] allLinkTexts) {

		String allLinksLocator = "a.nav-text-brds-v2-navy-80";
		List<WebElement> allLinks = Element.getListOfElements(testConfig, How.css, allLinksLocator);

		for (int i = 0; i < allLinks.size(); i++) {
			if(allLinkTexts[i].contains("Settings")) {
				Helper.compareContains(testConfig, "Link " + (i+1), "Settings", allLinks.get(i).getText());
			} else {
				Helper.compareEquals(testConfig, "Link " + (i+1), allLinkTexts[i], allLinks.get(i).getText());
			}
			String linkValue = allLinks.get(i).getAttribute("href");
			if(!linkValue.contains("javascript")) {
				try {
					verifyURLStatus(linkValue);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void verifyDoNotSeePersonalInfoModal() {

		String locator = ".//a[contains(@class,'nav-text-brds-v2-navy-80')][contains(@class,'sdk-show-settings')]";
		WebElement cookieSettingsLink = Element.getPageElement(testConfig, How.xPath, locator);
		Element.click(testConfig, cookieSettingsLink, "Cookie Settings link");
		Browser.wait(testConfig, 2);

		String modalLocator = ".//div[@id='onetrust-pc-sdk']";
		try {
			WebElement oneTruestModal = Element.getPageElement(testConfig, How.xPath, modalLocator);
			if(oneTruestModal.isDisplayed()) {
				testConfig.logPass("Verified that clicking Cookie Settings link, it opens the one trust modal successfully");
			} else {
				testConfig.logFail("Failed to verify that clicking Cookie Settings link, it opens the one trust modal successfully");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that clicking Cookie Settings link, it opens the one trust modal successfully");
		}
	}

	public void verifyTabsOverCookieSettingModal(CookieSettingsTabs cookieSettingsTabs) {

		Browser.waitWithoutLogging(testConfig, 2);
		String tabLocator = "", contentLocator = "";
		WebElement content = null;
		switch (cookieSettingsTabs) {
		case StrictlyNecessaryCookies:
			tabLocator = ".//li[contains(@class,'ot-cat-item')]/div/h3[text()='Strictly Necessary Cookies']";
			break;

		case YourPrivacy:
			tabLocator = ".//li[@role='presentation']/div/h3[text()='Your Privacy']";
			break;

		case PerformanceCookies:
			tabLocator = ".//li[contains(@class,'ot-cat-item')]/div/h3[text()='Performance Cookies']";
			break;

		case FunctionalCookies:
			tabLocator = ".//li[contains(@class,'ot-cat-item')]/div/h3[text()='Functional Cookies']";
			break;

		case TargetingCookies:
			tabLocator = ".//li[contains(@class,'ot-cat-item')]/div/h3[text()='Targeting Cookies']";
			break;
		}

		WebElement tab = Element.getPageElement(testConfig, How.xPath, tabLocator);
		Element.clickThroughJS(testConfig, tab, cookieSettingsTabs.toString() + " tab");
		Browser.waitWithoutLogging(testConfig, 2);

		switch (cookieSettingsTabs) {
		case PerformanceCookies:
			contentLocator = "(.//div[@class='ot-grp-hdr1']/h4)[2]";
			content = Element.getPageElement(testConfig, How.xPath, contentLocator);
			Helper.compareEquals(testConfig, "Heading over content for the " + cookieSettingsTabs.toString(), "Performance Cookies", content.getText());
			break;

		case StrictlyNecessaryCookies:
			contentLocator = "(.//div[@class='ot-grp-hdr1']/h4)[1]";
			content = Element.getPageElement(testConfig, How.xPath, contentLocator);
			Helper.compareEquals(testConfig, "Heading over content for the " + cookieSettingsTabs.toString(), "Strictly Necessary Cookies", content.getText());
			break;

		case YourPrivacy:
			contentLocator = ".//div[@id='ot-tab-desc']/h4";
			content = Element.getPageElement(testConfig, How.xPath, contentLocator);
			Helper.compareEquals(testConfig, "Heading over content for the " + cookieSettingsTabs.toString(), "Your Privacy", content.getText());
			break;

		case FunctionalCookies:
			contentLocator = "(.//div[@class='ot-grp-hdr1']/h4)[3]";
			content = Element.getPageElement(testConfig, How.xPath, contentLocator);
			Helper.compareEquals(testConfig, "Heading over content for the " + cookieSettingsTabs.toString(), "Functional Cookies", content.getText());
			break;

		case TargetingCookies:
			contentLocator = "(.//div[@class='ot-grp-hdr1']/h4)[4]";
			content = Element.getPageElement(testConfig, How.xPath, contentLocator);
			Helper.compareEquals(testConfig, "Heading over content for the " + cookieSettingsTabs.toString(), "Targeting Cookies", content.getText());
			break;
		}

	}

	public SearchResultsPage searchForKeywordAllOverSite(String keyword) {

		WebElement inputSearch = Element.getPageElement(testConfig, How.css,
				"input[class*='fyh-input'][aria-labelledby='Search']");
		Element.enterDataAfterClick(testConfig, inputSearch, keyword, "Search value");
		Browser.wait(testConfig, 2);

		String locator = ".//div[contains(text(),'Search the full site')]";
		WebElement siteSearchOpt = Element.getPageElement(testConfig, How.xPath, locator);
		Element.click(testConfig, siteSearchOpt, "Search the full site for this phrase option");

		return new SearchResultsPage(testConfig);
	}

	public void verifyHeroImageAndHeadingDisplaying(String expectedHeading) {

		String headingLoc = "(.//h1[contains(@class,'hps-block')])[1]";
		WebElement sectionTitle = Element.getPageElement(testConfig, How.xPath, headingLoc);
		Helper.compareContains(testConfig, "Page Heading", expectedHeading.toLowerCase(), sectionTitle.getText().toLowerCase().trim());

		String backgroundImageLoc = ".//div[contains(@class,'hps-bg-cover')]";
		WebElement backgroundImage = Element.getPageElement(testConfig, How.xPath, backgroundImageLoc);
		String styleProperty = backgroundImage.getAttribute("style");
		String imageUrl = styleProperty.substring(styleProperty.indexOf("(") + 2, styleProperty.indexOf(")") - 1);
		verifyURLAsPerDomain(imageUrl);
	}

	public void verifySearchModuleSearchFieldPlaceholderAndSearchBtn(String expectedPlaceholder) {

		String searchFieldLoc = "input[class*='fyh-input'][aria-labelledby='Search']";
		WebElement searchField = Element.getPageElement(testConfig, How.css, searchFieldLoc);
		Helper.compareContains(testConfig, "Placeholder for Search Field", expectedPlaceholder, searchField.getAttribute("aria-label").trim());


		String searchBtnLocator = ".//button[@aria-labelledby='Search']";
		WebElement searchBtn = Element.getPageElement(testConfig, How.xPath, searchBtnLocator);
		try {
			if(searchBtn.isDisplayed()) {
				testConfig.logPass("Verified Search Button is displaying for the new home search module");
			} else {
				testConfig.logFail("Failed to verify that Search Button is displaying for the new home search module");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that Search Button is displaying for the new home search module");
		}
	}

	public void verifyPlaceholderUpdatesOnClickingInputField(String expectedUpdatedPlaceholder) {

		String searchFieldLoc = "input[class*='fyh-input'][aria-labelledby='Search']";
		WebElement searchField = Element.getPageElement(testConfig, How.css, searchFieldLoc);
		Element.click(testConfig, searchField, "Search Field");
		Browser.wait(testConfig, 2);
		Helper.compareContains(testConfig, "Placeholder after clicking on Search field", expectedUpdatedPlaceholder, searchField.getAttribute("placeholder").trim());
	}

	public void verifySearchFieldTurnsRed(String expectedBorderColor) {

		String fieldLocator = ".//div[contains(@class,'rounded-full')][contains(@class,'bg-white')]";

		String searchBtnLocator = ".//button[@aria-labelledby='Search']";
		WebElement searchBtn = Element.getPageElement(testConfig, How.xPath, searchBtnLocator);
		Element.click(testConfig, searchBtn, "Search button");
		Browser.wait(testConfig, 2);

		WebElement searchField = Element.getPageElement(testConfig, How.xPath, fieldLocator);
		String cssValue = searchField.getCssValue("border-color");
		String rgbVal = cssValue.substring(cssValue.indexOf("rgb"));
		String hexcolor = Color.fromString(rgbVal).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Border color for Search Field when no value provided", expectedBorderColor.toUpperCase(),
				hexcolor.toUpperCase());

	}

	public BlogDetailPage clickOnBlogCard() {

		String cardsLoc = ".//div[contains(@class,'ease-in-out')]/a[contains(@class,'post')]";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, cardsLoc);

		String cardHeadingLoc = ".//div[contains(@class,'ease-in-out')]/a[contains(@class,'post')]//h5";
		List<WebElement> cardHeading = Element.getListOfElements(testConfig, How.xPath, cardHeadingLoc);

		String firstCardHeading = cardHeading.get(0).getText();	
		testConfig.putRunTimeProperty("BlogCardHeading", firstCardHeading);
		Element.click(testConfig, allCards.get(0), "Blog card having title as " + firstCardHeading);
		Helper.removeCookies(testConfig);
		return new BlogDetailPage(testConfig);
	}

	public void verifyExploreCommunitySection() throws UnsupportedEncodingException {

		String headingTopLoc = ".//div[contains(@class,'px-4')]/div/h4[contains(@class,'font-light')]";
		String headingBelowLoc = ".//div[contains(@class,'px-4')]/div/h3[contains(@class,'font-semibold')]";
		String commDescLoc = ".//div[contains(@class,'lg:text-brp-blue-100')]/p";
		String exploreCommBtnLoc = ".//div[contains(@class,'lg:text-brp-blue-100')]/../a[contains(@class,'rounded-full')]";
		String imageLoc = ".//div[contains(@class,'px-4')]/div/h4[contains(@class,'font-light')]/ancestor::section//img";

		String navigationDotsLoc = ".//div[contains(@class,'bottom-0')]/button";
		List<WebElement> allNavDots = Element.getListOfElements(testConfig, How.xPath, navigationDotsLoc);

		for (int i = 0; i < allNavDots.size(); i++) {

			Element.click(testConfig, allNavDots.get(i), "Navigation dot: " + (i + 1));
			Browser.wait(testConfig, 2);
			testConfig.logComment("****************** Looking for Community Data for Item No " + (i + 1) + " ******************");

			WebElement headingTop = Element.getPageElement(testConfig, How.xPath, headingTopLoc);
			WebElement headingbelow = Element.getPageElement(testConfig, How.xPath, headingBelowLoc);
			WebElement commDesc = Element.getPageElement(testConfig, How.xPath, commDescLoc);
			List<WebElement> allImages = Element.getListOfElements(testConfig, How.xPath, imageLoc);

			if(commDesc == null) {
				commDesc = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'lg:text-brp-blue-100')]");
			}

			if(headingTop.getText().isEmpty()) {
				testConfig.logFail("Not getting top heading displaying.. failing the scenario");
			} else {
				testConfig.logPass("Verified top heading is displaying as: " + headingTop.getText());
			}

			if(headingbelow.getText().isEmpty()) {
				testConfig.logFail("Not getting sub heading displaying.. failing the scenario");
			} else {
				testConfig.logPass("Verified sub heading is displaying as: " + headingbelow.getText());
			}

			if(commDesc.getText().isEmpty()) {
				testConfig.logFail("Not getting Community description displaying.. failing the scenario");
			} else {
				testConfig.logPass("Verified Community description is displaying as: " + commDesc.getText());
			}

			String imageSrc = Element.getAttribute(testConfig, allImages.get(i), "src", "Community image");

			if (!imageSrc.isEmpty()) {
				testConfig.logPass("Getting image source displaying for the card as " + imageSrc);
				verifyURLAsPerDomain(imageSrc);
			} else {
				testConfig.logFail("Getting image source displaying for community as blank... failing the scenario");
			}

			try {
				WebElement exploreCommBtn = Element.getPageElement(testConfig, How.xPath, exploreCommBtnLoc);
				if(exploreCommBtn.isDisplayed()) {
					testConfig.logPass("Verified explore community button is displaying for the community : " + exploreCommBtn.getAttribute("innerText").trim());
				} else {
					testConfig.logFail("Failed to verify that button is displaying for community : " + (i + 1));
				}
				String communityLink = Element.getAttribute(testConfig, exploreCommBtn, "href", "Explore community button"); 
				verifyURLAsPerDomain(communityLink);
			} catch (Exception e) {
				testConfig.logFail("Failed to verify that button is displaying for community : " + (i + 1));
			}

		}
	}

	public MyTimeTourPage clickOnScheduleATourLink() {

		String scheduleATourLinkLoc = ".//a[contains(@href,'/mytime')]";
		WebElement scheduleATourLink = Element.getPageElement(testConfig, How.xPath, scheduleATourLinkLoc);
		Element.click(testConfig, scheduleATourLink, "Schedule A Tour button");

		return new MyTimeTourPage(testConfig);

	}

	public MyTimeTourPage navigateToMyTimePageDirectly() {

		testConfig.driver.navigate().to(homeurl + "mytime");
		return new MyTimeTourPage(testConfig);

	}


	public void verifySearchButtonBackground(String expectedBackgroundColor) {

		String searchBtnLocator = ".//button[@aria-labelledby='Search']";
		WebElement searchBtn = Element.getPageElement(testConfig, How.xPath, searchBtnLocator);
		String cssValue = searchBtn.getCssValue("background-color");
		String rgbVal = cssValue.substring(cssValue.indexOf("rgb"));
		String hexcolor = Color.fromString(rgbVal).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Border color for Search Field when no value provided", expectedBackgroundColor.toUpperCase(),
				hexcolor.toUpperCase());

	}

	public void verifyCategoryDisplayingSelected() {

		List<WebElement> headerLinks = Element.getListOfElements(testConfig, How.xPath, ".//div[@aria-label='News & Blog']//ul/li/a");
		WebElement content = headerLinks.get(0);
		String label = content.getText();
		Element.click(testConfig, content, label + " link from navigation menu");

		verifyCategorySelectedDefault(label);

		headerLinks = Element.getListOfElements(testConfig, How.xPath, ".//div[@aria-label='News & Blog']//ul/li/a");
		content = headerLinks.get(1);
		label = content.getText();
		Element.click(testConfig, content, label + " link from navigation menu");

		verifyCategorySelectedDefault(label);

		headerLinks = Element.getListOfElements(testConfig, How.xPath, ".//div[@aria-label='News & Blog']//ul/li/a");
		content = headerLinks.get(2);
		label = content.getText();
		Element.click(testConfig, content, label + " link from navigation menu");

		if(label.equals("All Topics")) {
			verifyCategorySelectedDefault("All Categories");
		}

	}

	//	private void verifyCategorySelectedDefault(String label1, String label2) {
	//
	//		String allCategoriesLoc = ".//button[@id='dropdownMenuButton1'][contains(text(),'All Categories')]";
	//		WebElement allCategoriesDrop = Element.getPageElement(testConfig, How.xPath, allCategoriesLoc);
	//		Element.click(testConfig, allCategoriesDrop, "All Categories dropdown");
	//		Browser.wait(testConfig, 2);
	//
	//		String allCategoriesLocator = ".//button[contains(text(),'All Categories')]/..//li//span";
	//		List<WebElement> allCategories = Element.getListOfElements(testConfig, How.xPath, allCategoriesLocator);
	//
	//		String allCategoriesInputLoc = ".//button[contains(text(),'All Categories')]/..//li//span/../input";
	//		List<WebElement> allCategoriesInput = Element.getListOfElements(testConfig, How.xPath, allCategoriesInputLoc);
	//
	//		for (int i = 0; i < allCategories.size(); i++) {
	//			if(allCategories.get(i).getText().equals(label1) || allCategories.get(i).getText().equals(label2)) {
	//				if(allCategoriesInput.get(i).isSelected()) {
	//					testConfig.logPass("Verified " + allCategories.get(i).getText() + " is selected by default");
	//				} else {
	//					testConfig.logFail("Failed to verify " + allCategories.get(i).getText() + " is selected by default");
	//				}
	//			} else {
	//				if(!allCategoriesInput.get(i).isSelected()) {
	//					testConfig.logPass("Verified " + allCategories.get(i).getText() + " is not selected by default");
	//				} else {
	//					testConfig.logFail("Failed to verify " + allCategories.get(i).getText() + " is not selected by default");
	//				}
	//			}
	//		}
	//
	//		testConfig.driver.navigate().back();
	//		Browser.wait(testConfig, 2);
	//
	//		WebElement menuBar = Element.getPageElement(testConfig, How.xPath, ".//button[contains(@class,'hover:nav-bg-brds-v1-grayscale-blue-dark-ft')]");
	//		Element.click(testConfig, menuBar, "Header top nav menu bar");
	//		Browser.wait(testConfig, 2);
	//	}


	private void verifyCategorySelectedDefault(String label) {

		String selectedCategory = ".//p[contains(text(),'Category')]/parent::div/button/div/p";
		WebElement allCategoriesDrop = Element.getPageElement(testConfig, How.xPath, selectedCategory);
		String category = Element.getText(testConfig, allCategoriesDrop, "Selected Category Value");
		Browser.wait(testConfig, 2);

		if(label.equals("Home Buyer Resources")) {
			label = "Homebuyer Resources";
		}
		Helper.compareEquals(testConfig, "Selected Category from News and Blog section header links", label, category);
		testConfig.driver.navigate().back();
		Browser.wait(testConfig, 2);

		WebElement menuBar = Element.getPageElement(testConfig, How.xPath, ".//button[contains(@class,'hover:nav-bg-brds-v1-grayscale-blue-dark-ft')]");
		Element.clickThroughJS(testConfig, menuBar, "Header top nav menu bar");
		Browser.wait(testConfig, 2);
	}

	public void verifyFutureOfHomeSectionDisplaying(Map<String, String> expectedLinks, String expectedHeading) {

		Browser.waitWithoutLogging(testConfig, 2);
		Element.clickThroughJS(testConfig, menuBar, "Header top nav menu bar");
		Browser.wait(testConfig, 2);

		String logoLocator = ".//a[@aria-label='go to home page']";
		String logoImageLink = ".//a[@aria-label='go to home page']/img";

		WebElement logo = Element.getPageElement(testConfig, How.xPath, logoLocator);
		WebElement logoImage = Element.getPageElement(testConfig, How.xPath, logoImageLink);

		String imageSrc = Element.getAttribute(testConfig, logoImage, "src", "Image source");
		String logoUrl = Element.getAttribute(testConfig, logo, "href", "Anchor link");

		Helper.compareEquals(testConfig, "URL associated with logo", homeurl, logoUrl);

		verifyURLAsPerDomain(imageSrc);

		String headingLoc = ".//h3[contains(@aria-label,'Future')]";
		WebElement heading = Element.getPageElement(testConfig, How.xPath, headingLoc);
		Helper.compareEquals(testConfig, "Heading above list items", expectedHeading, heading.getText());

		String cancelBtnLocator = "button.transparent>div.iconContainer";
		WebElement cancelBtn = Element.getPageElement(testConfig, How.css, cancelBtnLocator);
		if(cancelBtn.isDisplayed()) {
			testConfig.logPass("Verified cancel button is present over the header nav menu modal");
		} else {
			testConfig.logFail("Failed to verify that the cancel button is present over the header nav menu modal");
		}

		Map<String, String> linksOverPage = new HashMap<String, String>();
		List<WebElement> headerLinks = Element.getListOfElements(testConfig, How.xPath, ".//figure[@role='listitem']//a");
		for (Iterator<WebElement> iterator = headerLinks.iterator(); iterator.hasNext();) {
			WebElement content = (WebElement) iterator.next();
			linksOverPage.put(content.getText(), content.getAttribute("href").replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", ""));
		}

		if(areEqual(expectedLinks, linksOverPage)) {
			testConfig.logPass("Verified header section displays as " + linksOverPage.toString());
		} else {
			testConfig.logFail("Not getting expected header section displays over page " + linksOverPage.toString());
			testConfig.logComment("Expected section : " + expectedLinks.toString());
			testConfig.logComment("Actual section displaying over the page : " + linksOverPage.toString());
		}

		for (Iterator<WebElement> iterator = headerLinks.iterator(); iterator.hasNext();) {
			WebElement content = (WebElement) iterator.next();
			String contentUrl = content.getAttribute("href").replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", "");
			verifyURLAsPerDomain(contentUrl);
		}
	}

	public void verifyWhereWeBuildSection(String expectedTitle, String expectedSubTitle, String[] countriesLoc) {

		Element.click(testConfig, whereWeBuildBtn, "Where we build button in top header");
		Browser.wait(testConfig, 2);

		String titleLoc = ".//h3[@aria-label='Where We Build menu']";
		WebElement titleOnModal = Element.getPageElement(testConfig, How.xPath, titleLoc);

		String subtitleLoc = ".//span[@id='wwb-description']";
		WebElement subtitleOnModal = Element.getPageElement(testConfig, How.xPath, subtitleLoc);

		Helper.compareEquals(testConfig, "Title over Where we build modal", expectedTitle, titleOnModal.getText().trim());
		Helper.compareEquals(testConfig, "Sub-title over Where we build modal", expectedSubTitle, subtitleOnModal.getText().trim());

		Browser.waitWithoutLogging(testConfig, 2);

		String countriesTabLoc = ".//div[@role='tablist']/button/div/span";
		List<WebElement> countriesTab = Element.getListOfElements(testConfig, How.xPath, countriesTabLoc);

		for (int i = 0; i < countriesTab.size(); i++) {
			Helper.compareEquals(testConfig, "Country " + (i + 1) + " displaying", countriesLoc[i], countriesTab.get(i).getText());			
		}

	}

	public void verifyDefaultCountryAndCloseButton() {

		Browser.waitWithoutLogging(testConfig, 2);
		String countriesTabLoc = ".//div[@role='tablist']/button";
		List<WebElement> countriesTabButton = Element.getListOfElements(testConfig, How.xPath, countriesTabLoc);

		String countriesTabLabelLoc = ".//div[@role='tablist']/button/div/span";
		List<WebElement> countriesTabLabel = Element.getListOfElements(testConfig, How.xPath, countriesTabLabelLoc);

		if(countriesTabButton.get(0).getAttribute("data-headlessui-state").equals("selected")) {
			testConfig.logPass("Verified " + countriesTabLabel.get(0).getText() + " is displaying selected by default");
		} else {
			testConfig.logFail("Failed to verify that " + countriesTabLabel.get(0).getText() + " is displaying selected by default");
		}

		if(!countriesTabButton.get(1).getAttribute("data-headlessui-state").equals("selected")) {
			testConfig.logPass("Verified " + countriesTabLabel.get(0).getText() + " is not displaying selected by default");
		} else {
			testConfig.logFail("Failed to verify that " + countriesTabLabel.get(0).getText() + " is not displaying selected by default");
		}

		String cancelBtnLocator = "button.transparent>div.iconContainer";
		WebElement cancelBtn = Element.getPageElement(testConfig, How.css, cancelBtnLocator);
		if(cancelBtn.isDisplayed()) {
			testConfig.logPass("Verified cancel button is present over the 'Where We Build' modal");
		} else {
			testConfig.logFail("Failed to verify that the cancel button is present over the 'Where We Build' modal");
		}
	}

	public void verifyStateAndCountyListed() {

		String countriesTabLabelLoc = ".//div[@role='tablist']/button/div/span";
		List<WebElement> countriesTabLabel = Element.getListOfElements(testConfig, How.xPath, countriesTabLabelLoc);

		String countriesTabLoc = ".//div[@role='tablist']/button";
		List<WebElement> countriesTabButton = Element.getListOfElements(testConfig, How.xPath, countriesTabLoc);

		for (int k = 0; k < countriesTabButton.size(); k++) {
			Element.click(testConfig, countriesTabButton.get(k), countriesTabLabel.get(k).getText() + " country tab");

			Browser.waitWithoutLogging(testConfig, 2);

			String stateNamesLocator = ".//h4[contains(@class,'nav-text-brds-v1-brand-blue')]";
			List<WebElement> stateNameElements = Element.getListOfElements(testConfig, How.xPath, stateNamesLocator);

			String countiesLocPrefix = ".//h4[contains(@class,'nav-text-brds-v1-brand-blue')][text()='";
			String countiesLocSuffix = "']/../div/button[@id='card']//span[contains(@class,'base-400')]";

			for (int i = 0; i < stateNameElements.size(); i++) {
				String stateName = stateNameElements.get(i).getText();
				if(!stateName.isEmpty()) {
					testConfig.logPass("Getting State " + (i + 1) + " as " + stateName);
					List<WebElement> counties = Element.getListOfElements(testConfig, How.xPath, countiesLocPrefix + stateName + countiesLocSuffix);
					testConfig.logComment("Listing different counties displaying under " + stateName + " state");
					for (int j = 0; j < counties.size(); j++) {
						testConfig.logPass("Getting County " + (j + 1) + " under " + stateName + " as " + counties.get(j).getText());					
					}
				} else {
					testConfig.logFail("Getting State " + (i + 1) + " as blank");
				}
			}
		}
	}

	public FYHPage performSearchByMapAction() {

		WebElement inputSearch = Element.getPageElement(testConfig, How.css,
				"input[class*='fyh-input'][aria-labelledby='Search']");
		Element.clickThroughJS(testConfig, inputSearch, "Search input box");
		Browser.wait(testConfig, 2);

		WebElement searchByMapCTA = Element.getPageElement(testConfig, How.css, "li[aria-labelledby='Search by map']");
		Element.clickThroughJS(testConfig, searchByMapCTA, "Search By Map CTA");
		Helper.removeCookies(testConfig);
		testConfig.putRunTimeProperty("RedirectionValue", "yes");
		return new FYHPage(testConfig);
	}

	public void verifyPromotionsSection(String promotionText, String expectedUrl) {
		String promotionContent = Element.getText(testConfig, promotionsText, "Promotions section text");
		Helper.compareEquals(testConfig, "Promotions section content", promotionText, promotionContent);

		verifyImageDisplayingCorrectly(expectedUrl);
	}

	private void verifyImageDisplayingCorrectly(String expectedUrl) {

		String imageSrc = Element.getAttribute(testConfig, promotionsImage, "src", "Promotions Image");
		String CTAUrl = Element.getAttribute(testConfig, promotionsCTA, "href", "Promotions Section CTA");

		Helper.compareContains(testConfig, "URL associated with the CTA", expectedUrl, CTAUrl);
		verifyURLAsPerDomain(imageSrc);
		verifyURLAsPerDomain(CTAUrl);
	}

	public void verifyBestPlacesCarouselBehaviorAndContent(String sectionHeading, String sectionSubTitle) {

		String sectionHeadingContent = Element.getText(testConfig, bestPlacesHeading, "Promotions section text");
		Helper.compareEquals(testConfig, "Section Heading content", sectionHeading, sectionHeadingContent);

		String sectionSubHeadingContent = Element.getText(testConfig, bestPlacesSubHeading, "Promotions section text");
		Helper.compareEquals(testConfig, "Section Sub-heading content", sectionSubTitle, sectionSubHeadingContent);

		verifyCarouselBehavior();
	}

	private void verifyCarouselBehavior() {

		((JavascriptExecutor) testConfig.driver).executeScript("arguments[0].scrollIntoView(true);", sliderAllProgressBars.get(sliderAllProgressBars.size() - 1));

		Helper.compareEquals(testConfig, "Image count with the progress bar count" , sliderAllImages.size(), sliderAllProgressBars.size());

		for (int i = 0; i < sliderAllProgressBars.size(); i++) {
			Element.clickThroughJS(testConfig, sliderAllProgressBars.get(i), "Progress bar item : " + (i + 1));
			Browser.waitWithoutLogging(testConfig, 2);

			List<WebElement> imageSection = Element.getListOfElements(testConfig, How.css, "section div[class='slider'] img");
			List<WebElement> imageParentSection = Element.getListOfElements(testConfig, How.css, "section div[class='slider']>div");

			for (int j = 0; j < imageParentSection.size(); j++) {
				if(i == j) {
					Helper.compareTrue(testConfig, "Carousel Item : " + (j + 1) + " is displaying selected in the panel", imageParentSection.get(j).getAttribute("area-selected").equals("true"));
				} else {
					Helper.compareTrue(testConfig, "Carousel Item : " + (j + 1) + " is not displaying selected in the panel", imageParentSection.get(j).getAttribute("area-selected").equals("false"));
				}				
			}
			testConfig.logComment("Getting the image src which is displaying in the panel as : " + imageSection.get(i).getAttribute("src"));
			verifyURLAsPerDomain(imageSection.get(i).getAttribute("src"));
		}

	}

	public void verifyWhereWeBuildCTAOpensModal() {

		Element.click(testConfig, whereWeBuildBtn, "Where We Build CTA");
		Browser.waitWithoutLogging(testConfig, 2);
		
		String modalLocator = "div[class*='nav-pointer-events-none'] h3";
		WebElement whereWeBuildModalText = Element.getPageElement(testConfig, How.css, modalLocator);

		if(whereWeBuildModalText.isDisplayed()) {
			testConfig.logPass("Verified the modal opens correctly when clicking on the 'Where We Build' CTA");
		} else {
			testConfig.logFail("Failed to verify that the modal opens correctly when clicking on the 'Where We Build' CTA");
		}

		String closeButton = "div[class*='nav-pointer-events-none'] button[class*='transparent']";
		WebElement closeBtn = Element.getPageElement(testConfig, How.css, closeButton);
		Element.click(testConfig, closeBtn, "Close button over the modal");
		Browser.waitWithoutLogging(testConfig, 2);
		
		try {
			whereWeBuildModalText = Element.getPageElement(testConfig, How.css, modalLocator);

			if(whereWeBuildModalText.isDisplayed()) {
				testConfig.logFail("Failed to verify that the modal closes correctly when clicking on the close button");
			} else {
				testConfig.logPass("Verified the modal closes correctly when clicking on the close button");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified the modal closes correctly when clicking on the close button");
		}
	}

	public void verifyBRPDifferenceSectionContent(String sectionHeading, String expectedUrl) {

		String sectionHeadingContent = Element.getText(testConfig, BRPDiffHeading, "Brookfield Residential Diff section text");
		Helper.compareEquals(testConfig, "Section Heading content", sectionHeading, sectionHeadingContent);
		
		verifyGridContent();
		
		String CTAUrl = Element.getAttribute(testConfig, BRPDiffLearnMoreCTA, "href", "Brookfield Residential Diff section CTA");
		Helper.compareContains(testConfig, "URL associated with the CTA", expectedUrl, CTAUrl);
		verifyURLAsPerDomain(CTAUrl);
		
	}

	private void verifyGridContent() {
		
		for (int i = 0; i < BRPDiffGridData.size(); i++) {
			
			testConfig.logComment("****************** Looking for Data for Grid no " + (i + 1) + " ******************");

			String imageSrc = Element.getAttribute(testConfig, BRPDiffImages.get(i), "src", "Grid image");
			if (!imageSrc.isEmpty()) {
				testConfig.logPass("Getting image source displaying for the card as " + imageSrc);
				verifyURLAsPerDomain(imageSrc);
			} else {
				testConfig.logFail("Getting image source displaying over card as blank... failing the scenario");
			}
			
			if (!BRPDiffGridHeadings.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Grid Heading displaying over card as " + BRPDiffGridHeadings.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Grid Heading displaying over card value as blank... failing the scenario");
			}
			
			if (!BRPDiffGridDescriptions.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Grid Description displaying over card as " + BRPDiffGridDescriptions.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Grid Description displaying over card value as blank... failing the scenario");
			}
		}
		
	}

	public void verifyTopLocationsSection(String sectionHeading, String sectionSubHeading) {

		String sectionHeadingContent = Element.getText(testConfig, topLocationSectionHeading, "Top Locations section heading");
		Helper.compareEquals(testConfig, "Section Heading content", sectionHeading, sectionHeadingContent);
		
		String sectionSubHeadingContent = Element.getText(testConfig, topLocationSectionSubHeading, "Top Locations section sub-heading");
		Helper.compareEquals(testConfig, "Section Sub-heading content", sectionSubHeading, sectionSubHeadingContent);
		
		verifyMapSection();
	}

	private void verifyMapSection() {
		String regionName = "", regionUrl = "";
		for (int i = 0; i < regionRedirectionLink.size(); i++) {
						
			testConfig.logComment("****************** Looking for Data for Region: " + (i + 1) + " ******************");
			
			regionName = Element.getAttribute(testConfig, regionNames.get(i), "class", "Region Name");
			regionName = regionName.split(" ")[1];
			
			regionUrl = regionRedirectionLink.get(i).getAttribute("href");
			
			testConfig.logComment(regionName + " : " + regionUrl); 
			
			if (!regionName.isEmpty()) {
				testConfig.logPass("Getting Region Name as " + regionName);
			} else {
				testConfig.logFail("Getting Region Name as blank... failing the scenario");
			}
			
			if (!regionName.isEmpty()) {
				testConfig.logPass("Getting Region URL value as " + regionUrl);
				verifyURLAsPerDomain(regionUrl);
			} else {
				testConfig.logFail("Getting Region URL value as blank... failing the scenario");
			}
		}
	}

	public void verifyBlogHomeBuyerSection(String sectionHeading, String expectedURL) {

		String sectionHeadingContent = Element.getText(testConfig, blogSectionHeading, "Blog section heading");
		Helper.compareEquals(testConfig, "Blog Section Heading content", sectionHeading, sectionHeadingContent);
		
		String CTALink = Element.getAttribute(testConfig, exploreAllResourcesCTA, "href", "Explore All Resources CTA");
		Helper.compareContains(testConfig, "Blog Home Buyer Section CTA Link", expectedURL, CTALink);
		
		verifyCardSection();
	}

	private void verifyCardSection() {
		
		for (int i = 0; i < blogSectionCardImages.size(); i++) {
			
			testConfig.logComment("****************** Looking for Card Data for Blog Card no " + (i + 1) + " ******************");

			String imageSrc = Element.getAttribute(testConfig, blogSectionCardImages.get(i), "src", "Blog Card Image");
			if (!imageSrc.isEmpty()) {
				testConfig.logPass("Getting image source displaying for the card as " + imageSrc);
				verifyURLAsPerDomain(imageSrc);
			} else {
				testConfig.logFail("Getting image source displaying over card as blank... failing the scenario");
			}
			
			if (blogCardCategoryLabel.get(i).getAttribute("innerText").trim().equals("Homebuyer Resources")) {
				testConfig.logPass("Getting the blog category displayed as " + blogCardCategoryLabel.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting the blog category displayed as " + blogCardCategoryLabel.get(i).getAttribute("innerText") + "which is incorrect... failing the scenario");
			}
			
			if (!blogCardHeading.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting heading displaying over blog card as " + blogCardHeading.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting heading displaying over blog card value as blank... failing the scenario");
			}
		}
	}

	public void verifyGetInTouchImageCTADetails(String sectionPreHeading, String expectedHeading, String expectedURL, int expectedSalesProfilesCount) {

		String sectionSubHeadingContent = Element.getText(testConfig, getInTouchSubHeader, "Get In Touch section sub heading");
		Helper.compareEquals(testConfig, "Get In Touch section sub heading content", sectionPreHeading, sectionSubHeadingContent);
		
		String sectionHeadingContent = Element.getText(testConfig, getInTouchHeader, "Get In Touch section heading");
		Helper.compareContains(testConfig, "Get In Touch section heading content", expectedHeading, sectionHeadingContent);
		
		verifySalesProfilesIcons(expectedSalesProfilesCount);
		verifyBackgroundImage();
		verifyGetInTouchCTA(expectedURL);
	}

	private void verifyGetInTouchCTA(String expectedURL) {
		String CTALink = Element.getAttribute(testConfig, getInTouchCTA, "href", "Get In Touch CTA");
		Helper.compareContains(testConfig, "Get in touch CTA", expectedURL, CTALink);
	}

	private void verifyBackgroundImage() {
		
		testConfig.logComment("****************** Looking for the Background Image Data for the section ******************");
		
		String imageStyle = Element.getAttribute(testConfig, getInTouchBGImage, "style", "Get In Touch Section Background Main Image");
		String imageSrc = homeurl.concat(imageStyle.substring(imageStyle.indexOf("(") + 2, imageStyle.indexOf(")") - 1));
		if (!imageSrc.isEmpty()) {
			testConfig.logPass("Getting Get In Touch Section Background Main Image source displaying as " + imageSrc);
			verifyURLAsPerDomain(imageSrc);
		} else {
			testConfig.logFail("Getting Get In Touch Section Background Main Image source as blank... failing the scenario");
		}
	}

	private void verifySalesProfilesIcons(int expectedSalesProfilesCount) {
		
		String imageSrc = "";
		Helper.compareEquals(testConfig, "Profiles Count present in the section", expectedSalesProfilesCount, getInTouchProfiles.size());
		
		for (int i = 0; i < getInTouchProfiles.size(); i++) {
			testConfig.logComment("****************** Looking for the Profile Image Data for Profile: " + (i + 1) + " ******************");
			
			imageSrc = Element.getAttribute(testConfig, getInTouchProfiles.get(i), "src", "Get In Touch Profile Image");
			if (!imageSrc.isEmpty()) {
				testConfig.logPass("Getting image source displaying for the Profile as " + imageSrc);
				verifyURLAsPerDomain(imageSrc);
			} else {
				testConfig.logFail("Getting image source displaying for the Profile as blank... failing the scenario");
			}
		}
		
	}

	public void verifyWhereWeBuildViewOpenedFromHeaderMenu(String sectionHeading, String sectionSubHeading) {

		Element.click(testConfig, whereWeBuildBtn, "Where We Build button");
		Browser.waitWithoutLogging(testConfig, 2);
		
		String whereWeBuildHeadingLoc = ".//h3[contains(text(),'Where We Build')]";
		WebElement whereWeBuildHeading = Element.getPageElement(testConfig, How.xPath, whereWeBuildHeadingLoc);
		Helper.compareEquals(testConfig, "Heading displaying in the overlay modal", sectionHeading, whereWeBuildHeading.getText());

		String whereWeBuildSubHeadingLoc = ".//h3[contains(text(),'Where We Build')]/../span";
		WebElement whereWeBuildSubHeading = Element.getPageElement(testConfig, How.xPath, whereWeBuildSubHeadingLoc);
		Helper.compareEquals(testConfig, "Heading displaying in the overlay modal", sectionSubHeading, whereWeBuildSubHeading.getText());

		verifyUSALocationSelectedByDefault();
		verifyStatesAndCountyLocations(WhereWeBuildLocation.UnitedStates);
		verifyStatesAndCountyLocations(WhereWeBuildLocation.Canada);
		
	}

	private void verifyUSALocationSelectedByDefault() {
		
		String countryLocator = ".//span[text()='United States']/ancestor::button";
		WebElement country = Element.getPageElement(testConfig, How.xPath, countryLocator);

		if(Element.getAttribute(testConfig, country, "aria-selected", "United States button").equals("true")) {
			testConfig.logPass("Verified that the United States button is selected by default");
		} else {
			testConfig.logFail("Failed to verify that the United States button is selected by default");
		}
		
	}

	private void verifyStatesAndCountyLocations(WhereWeBuildLocation location) {
		
		switch (location) {
		case UnitedStates:
			break;

		case Canada:
			String countryLocator = ".//span[text()='Canada']/ancestor::button";
			WebElement country = Element.getPageElement(testConfig, How.xPath, countryLocator);
			Element.click(testConfig, country, "Canada location button");
			Browser.waitWithoutLogging(testConfig, 5);
			break;
		}
		
		String allCardsLocation = "button#card";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.css, allCardsLocation);
		
		String allCardsImageLocation = "button#card>img";
		String allCardsCountyLocation = "button#card div.nav-items-start>span";
		String allCardsCitiesLocation = "button#card div.cities";

		List<WebElement> allCardsImage = Element.getListOfElements(testConfig, How.css, allCardsImageLocation);
		List<WebElement> allCardsCounty = Element.getListOfElements(testConfig, How.css, allCardsCountyLocation);
		List<WebElement> allCardsCities = Element.getListOfElements(testConfig, How.css, allCardsCitiesLocation);
		String imageSrc = "", regionName = "", cities = "";

		for (int i = 0; i < allCards.size(); i++) {
			testConfig.logComment("****************** Looking for Card Data for Card no " + (i + 1) + " ******************");
			
			regionName = allCardsCounty.get(i).getText();
			if (!regionName.isEmpty()) {
				testConfig.logPass("Getting Region Name as " + regionName);
			} else {
				testConfig.logFail("Getting Region Name as blank... failing the scenario");
			}
			
			imageSrc = Element.getAttribute(testConfig, allCardsImage.get(i), "src", "Card Image");
			if (!imageSrc.isEmpty()) {
				testConfig.logPass("Getting image source displaying for the card as " + imageSrc);
				verifyURLAsPerDomain(imageSrc);
			} else {
				testConfig.logFail("Getting image source displaying for the card as blank... failing the scenario");
			}
			
			cities = allCardsCities.get(i).getAttribute("innerText");
			if (!regionName.isEmpty()) {
				testConfig.logPass("Getting Cities associated with " + regionName + " as " + cities);
			} else {
				testConfig.logFail("Getting Cities associated with " + regionName + " as blank... failing the scenario");
			}
		}
		
	}
}
