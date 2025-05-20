package EXSquared.Brookfield;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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
import Utils.TestDataReader;

public class AboutPage extends BRPHelper {

	public enum AboutPageTabs {
		Leadership, Distinction, History, Values, InvestorMediaRelations
	}

	@FindBy(xpath = ".//h1[contains(text(),'Get to Know Us')]")
	private WebElement aboutTitle;

	public AboutPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		PageFactory.initElements(this.testConfig.driver, this);

		if(testConfig.getRunTimeProperty("RedirectionValue") != null && testConfig.getRunTimeProperty("RedirectionValue").equals("yes"))
		{
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
		}

		Browser.waitForPageLoad(testConfig, aboutTitle);
	}

	public void verifyTabsPresentOverPage(String[] expectedTabs) {

		List<WebElement> allTabsPage = Element.getListOfElements(testConfig, How.css, ".brp-page-container>a");
		for (int i = 0; i < expectedTabs.length; i++) {
			Helper.compareEquals(testConfig, "Tab " + (i + 1) + " over About Page", expectedTabs[i],
					allTabsPage.get(i).getText());
			if(isClickable(testConfig, allTabsPage.get(i))) {
				testConfig.logPass("Verified " + allTabsPage.get(i).getText() + " tab is clickable");
			} else {
				testConfig.logFail("Failed to verify that " + allTabsPage.get(i).getText() + " tab is clickable");
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

	public void navigateToRequiredTabContent(AboutPageTabs aboutPageTabs) {

		Browser.wait(testConfig, 5);
		WebElement requiredTab = null;

		switch (aboutPageTabs) {
		case Distinction:
			requiredTab = Element.getPageElement(testConfig, How.xPath, ".//a[text()='Distinction']");
			break;

		case History:
			requiredTab = Element.getPageElement(testConfig, How.xPath, ".//a[text()='History']");
			break;

		case InvestorMediaRelations:
			requiredTab = Element.getPageElement(testConfig, How.xPath,
					".//a[text()='Investor & Media Relations']");
			break;

		case Leadership:
			requiredTab = Element.getPageElement(testConfig, How.xPath, ".//a[text()='Leadership']");
			break;

		case Values:
			requiredTab = Element.getPageElement(testConfig, How.xPath, ".//a[text()='Values']");
			break;
		}

		Element.click(testConfig, requiredTab, aboutPageTabs.toString() + " tab");
		Browser.wait(testConfig, 5);

	}

	public void verifyLeadershipContent(HashMap<String, String> expectedData, TestDataReader reader) {

		HashMap<String, String> pageContent = new HashMap<>();
		List<WebElement> allLeaders = Element.getListOfElements(testConfig, How.css, "div.relative.flex>div.group");
		Helper.compareEquals(testConfig, "All leader tiles count", expectedData.size(), allLeaders.size());
		Browser.wait(testConfig, 1);

		WebElement firstCard = Element.getPageElement(testConfig, How.xPath,
				"(.//div[contains(@class,'relative flex')]/div[contains(@class,'group')])[1]");

		Actions action = new Actions(testConfig.driver);
		action.moveToElement(firstCard).build().perform();
		Browser.wait(testConfig, 2);

		WebElement readMore = Element.getPageElement(testConfig, How.xPath,
				"(.//button[contains(text(),'Read More')])[1]");
		Element.click(testConfig, readMore, "Read More button");
		Browser.wait(testConfig, 4);

		WebElement nextBtn = Element.getPageElement(testConfig, How.xPath, ".//button[contains(@class,'xl:right-10')]");
		for (int i = 0; i < allLeaders.size(); i++) {
			Element.click(testConfig, nextBtn, "Next button");
			Browser.wait(testConfig, 1);
		}

		Browser.waitWithoutLogging(testConfig, 5);
		List<WebElement> allNames = Element.getListOfElements(testConfig, How.xPath,
				".//h2[contains(@class,'md:text-3xl')]");
		List<WebElement> allDesignations = Element.getListOfElements(testConfig, How.xPath,
				".//div[contains(@class,'text-xs md:text-base')]");

		for (int i = 0; i < allDesignations.size(); i++) {
			pageContent.put(allNames.get(i).getAttribute("innerText"),
					allDesignations.get(i).getAttribute("innerText"));
		}

		//String name = reader.GetData(reader.getRecordsNum() - 1, "Name");
		//String designation = reader.GetData(reader.getRecordsNum() - 1, "Designation");

		//Helper.compareEquals(testConfig, "Name over last card", name, allNames.get(allNames.size() - 1).getText());
		//Helper.compareEquals(testConfig, "Designation over last card", designation, pageContent.get(allNames.get(allNames.size() - 1).getText()));

		WebElement prevBtn = Element.getPageElement(testConfig, How.xPath, ".//button[contains(@class,'lg:left-5')]");
		for (int i = 0; i < allLeaders.size(); i++) {
			Element.click(testConfig, prevBtn, "Prev button");
			Browser.wait(testConfig, 1);
		}

		String name = reader.GetData(1, "Name");
		String designation = reader.GetData(1, "Designation");

		Helper.compareEquals(testConfig, "Name over first card", name, allNames.get(0).getAttribute("innerText"));
		Helper.compareEquals(testConfig, "Designation over first card", designation,pageContent.get(allNames.get(0).getAttribute("innerText")));

		Helper.compareEquals(testConfig, expectedData, pageContent);

	}

	public void verifyCancelButtonFunctionality() {

		WebElement firstCard = Element.getPageElement(testConfig, How.xPath,
				"(.//div[contains(@class,'relative flex')]/div[contains(@class,'group')])[1]");

		Actions action = new Actions(testConfig.driver);
		action.moveToElement(firstCard).build().perform();
		Browser.wait(testConfig, 2);

		WebElement readMore = Element.getPageElement(testConfig, How.xPath,
				"(.//button[contains(text(),'Read More')])[1]");
		Element.click(testConfig, readMore, "Read More button");
		Browser.wait(testConfig, 4);

		try {
			WebElement modal = Element.getPageElement(testConfig, How.css, ".ease-in.slider.overflow-hidden");
			if (modal.isDisplayed()) {
				testConfig.logPass("Verified Leadership modal opens correctly as expected");
			} else {
				testConfig.logFail("Fail to verify that Leadership modal opens correctly as expected");
			}
		} catch (Exception e) {
			testConfig.logFail("Fail to verify that Leadership modal opens correctly as expected");
		}
		Browser.wait(testConfig, 1);

		WebElement closeBtn = Element.getPageElement(testConfig, How.id, "leadershipCloseButton");
		Element.click(testConfig, closeBtn, "Close button");
		Browser.wait(testConfig, 1);

		try {
			WebElement modal = Element.getPageElement(testConfig, How.css, ".ease-in.slider.overflow-hidden");
			if (!modal.isDisplayed()) {
				testConfig.logPass("Verified Leadership modal closes correctly as expected");
			} else {
				testConfig.logFail("Fail to verify that Leadership modal closes correctly as expected");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified Leadership modal closes correctly as expected");
		}

	}

	public EmpowerPage verifyEmpowerSectionDetails() {

		WebElement title = Element.getPageElement(testConfig, How.xPath, ".//div[text()='Empower']");
		WebElement subtitle = Element.getPageElement(testConfig, How.xPath,
				".//div[contains(text(),'future of home')]");
		WebElement learnMoreLink = Element.getPageElement(testConfig, How.xPath, ".//a[text()=' Learn More ']");

		Helper.compareEquals(testConfig, "Section title", "Empower", title.getText());
		Helper.compareEquals(testConfig, "Section subtitle", "The future of home", subtitle.getText());

		Element.click(testConfig, learnMoreLink, "Learn More link");


		return new EmpowerPage(testConfig);
	}

	public void verifyHistoryInformationDetails(HashMap<String, String> expectedData) {

		HashMap<String, String> pageContent = new HashMap<>();

		WebElement nextBtn = Element.getPageElement(testConfig, How.xPath, ".//button[contains(@class,'lg:right-20')]");
		for (int i = 0; i < expectedData.size(); i++) {
			Element.click(testConfig, nextBtn, "Next button");
			Browser.wait(testConfig, 1);
		}

		List<WebElement> yearValues = Element.getListOfElements(testConfig, How.xPath,
				".//span[contains(@class,'lg:-ml-12')]");
		List<WebElement> eventsDetails = Element.getListOfElements(testConfig, How.xPath,
				".//span[contains(@class,'lg:-ml-12')]/../../..//div[contains(@class,'tracking-wider')]");

		for (int i = 0; i < eventsDetails.size(); i++) {
			pageContent.put(yearValues.get(i).getAttribute("innerText"),
					eventsDetails.get(i).getAttribute("innerText"));
		}

		WebElement prevBtn = Element.getPageElement(testConfig, How.xPath, ".//button[contains(@class,'lg:left-20')]");
		for (int i = 0; i < expectedData.size(); i++) {
			Element.click(testConfig, prevBtn, "Prev button");
			Browser.wait(testConfig, 1);
		}

		Helper.compareEquals(testConfig, expectedData, pageContent);

	}

	public void verifyValuesPageTitleSection(String expectedTitle, String expectedDescription) {

		WebElement title = Element.getPageElement(testConfig, How.xPath, ".//h1");
		WebElement description = Element.getPageElement(testConfig, How.xPath,
				".//div[contains(@class,'leading-relaxed')]");

		Helper.compareEquals(testConfig, "Section Title over page", expectedTitle, title.getText());
		Helper.compareEquals(testConfig, "Section Description over page", expectedDescription, description.getText());

	}

	public void verifyTitlesForItems(String[] expectedTitles) {

		String titlesLocator = ".//div[contains(@class,'text-brp-blue-300')][contains(@class,'mb-1')]";
		List<WebElement> sectionTitles = Element.getListOfElements(testConfig, How.xPath, titlesLocator);

		for (int i = 0; i < expectedTitles.length; i++) {
			Helper.compareEquals(testConfig, "Title for item " + (i + 1), expectedTitles[i],
					sectionTitles.get(i).getText());
		}
	}

	public void verifyLearnMoreLink(String expectedBuiltOnValuesLink) {

		String learnMoreLocator = ".//a[text()=' Learn More ']";
		WebElement learnMoreBtn = Element.getPageElement(testConfig, How.xPath, learnMoreLocator);

		Helper.compareContains(testConfig, "URL associated with Learn More link", expectedBuiltOnValuesLink,
				learnMoreBtn.getAttribute("href"));
		Browser.wait(testConfig, 1);

		try {
			verifyURLStatus(learnMoreBtn.getAttribute("href"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void verifyFinancialDisclosureSection(String expectedTitle, String reportHeading) {

		WebElement sectionTitle = Element.getPageElement(testConfig, How.xPath, ".//h1");
		String reportTitleLocator = ".//button[contains(@id,'headlessui-disclosure-button')]/h2";
		WebElement sectionHeading = Element.getPageElement(testConfig, How.xPath, reportTitleLocator);

		Helper.compareEquals(testConfig, "Section Title over page", expectedTitle, sectionTitle.getText());
		Helper.compareEquals(testConfig, "Report Title over page", reportHeading, sectionHeading.getText());

	}

	public void verifyOtherDocumentsSectionTitle(String[] expectedOtherDocuments) {

		List<WebElement> otherDocumentsTitle = Element.getListOfElements(testConfig, How.xPath,
				".//button[contains(@id,'headlessui-disclosure-button')]/h3");
		for (int i = 0; i < expectedOtherDocuments.length; i++) {
			Helper.compareEquals(testConfig, "Title for Other document section " + (i + 1), expectedOtherDocuments[i],
					otherDocumentsTitle.get(i).getText());
		}
	}

	public void verifyAllAvailablePDFLinks() {

		try {
			Set<String> linkedHashSet = new LinkedHashSet<>();
			List<WebElement> allPDFLinks = Element.getListOfElements(testConfig, How.xPath,
					".//a[contains(@href,'pdf')]");
			for (WebElement anchorTagElement : allPDFLinks) {
				String url = anchorTagElement.getAttribute("href");
				linkedHashSet.add(url);
			}

			testConfig.logComment("Total no. of PDF links found are " + linkedHashSet.size());

			for (String urlObtained : linkedHashSet) {
				verifyURLStatus(urlObtained);
			}
		} catch (Exception e) {
			testConfig.logComment(e.getMessage());
		}
	}

	public PressReleasePage verifyPressReleaseSectionFunctionality(String sectionHeading) {

		WebElement sectionTitle = Element.getPageElement(testConfig, How.css, "h2.leading-tight");
		Helper.compareEquals(testConfig, "Section Title over page", sectionHeading, sectionTitle.getText());

		WebElement viewMore = Element.getPageElement(testConfig, How.xPath, ".//button[text()='View More']");

		if (viewMore.isDisplayed()) {
			testConfig.logPass("Verified initially we are getting 'View More' button to be displayed");
		} else {
			testConfig.logFail("Failure to verify that initially we are getting 'View More' button to be displayed");
		}

		clickOnLinkToLoadAllCards();
		Browser.wait(testConfig, 2);

		WebElement viewAll = Element.getPageElement(testConfig, How.xPath, ".//div[contains(text(),'View All')]");

		if (viewAll.isDisplayed()) {
			testConfig.logPass("Verified now we are getting 'View All' button to be displayed");
		} else {
			testConfig.logFail("Failure to verify that now we are getting 'View All' button to be displayed");
		}

		WebElement viewAllLink = Element.getPageElement(testConfig, How.xPath,
				".//div[contains(text(),'View All')]/..");
		Helper.compareEquals(testConfig, "Link associated with View All",
				homeurl + "press-releases-events-and-webcasts-landing", viewAllLink.getAttribute("href"));

		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath,
				".//a[contains(@class,'bg-white')][contains(@class,'flex-col')]");
		Helper.compareEquals(testConfig, "All press release cards count displaying over page", 12, allCards.size());

		Element.click(testConfig, viewAll, "View all button");

		return new PressReleasePage(testConfig);
	}

	private void clickOnLinkToLoadAllCards() {

		WebElement viewMore = Element.getPageElement(testConfig, How.xPath, ".//button[text()='View More']");
		Element.click(testConfig, viewMore, "View More button 1st time");
		Browser.wait(testConfig, 2);
		Element.click(testConfig, viewMore, "View More button 2nd time");

	}

	public void verifyClearSearchHistory() {
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		js.executeScript("window.scrollTo(document.body.scrollTop,0)");
		Browser.waitWithoutLogging(testConfig, 2);
		
		WebElement searchBar = Element.getPageElement(testConfig, How.xPath, ".//input[contains(@class,'fyh-input')][@aria-labelledby='Search']");
		Element.click(testConfig, searchBar, "Search input field");
		Browser.wait(testConfig, 2);

		//		WebElement searchHistory = Element.getPageElement(testConfig, How.xPath, ".//div[contains(text(),'Recent Searches')]/parent::div//div[contains(@class,'srch-items-center')]");
		//		Actions action = new Actions(testConfig.driver);
		//		action.moveToElement(searchHistory).build().perform();

		//		JavascriptExecutor je = (JavascriptExecutor) testConfig.driver;
		//		je.executeScript("arguments[0].scrollIntoView(true);", searchHistory);

		WebElement clearHistoryBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'srch')]//span[contains(text(),'Clear history') or contains(text(),'Clear History')]");
		Element.clickThroughJS(testConfig, clearHistoryBtn, "Clear history button");
		Browser.wait(testConfig, 2);

		searchBar = Element.getPageElement(testConfig, How.xPath, ".//input[contains(@class,'fyh-input')][@aria-labelledby='Search']");
		Element.click(testConfig, searchBar, "Search input field");
		Browser.wait(testConfig, 2);

		try {
			WebElement searchHistoryResult = Element.getPageElement(testConfig, How.xPath,
					".//div[contains(text(),'Recent Searches')]/parent::div//div[contains(@class,'srch-items-center')]");
			if (searchHistoryResult.isDisplayed()) {
				testConfig.logFail("Still getting search history displaying... failing the test");
			} else {
				testConfig.logPass("Verified that now we are not getting search history visible under Search header");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified that now we are not getting search history visible under Search header");
		}

	}

	public void verifyHeroImageOverPage() {

		String backgroundImageLoc = ".//main//div[contains(@class,'bg-white')]/parent::div/img";
		WebElement heroImage = Element.getPageElement(testConfig, How.xPath, backgroundImageLoc);
		String imageSrc = Element.getAttribute(testConfig, heroImage, "src", "Hero image view");
		verifyURLAsPerDomain(imageSrc);

	}

	public void verifySectionDetails(String expectedTitle, String expectedDescription) {

		WebElement title = Element.getPageElement(testConfig, How.xPath, ".//h1");
		WebElement description = Element.getPageElement(testConfig, How.xPath,
				".//div[contains(@class,'leading-relaxed')][contains(@class,'text-brp-blue-100')]");

		Helper.compareContains(testConfig, "Section Title over page", expectedTitle, title.getText());
		Helper.compareContains(testConfig, "Section Description over page", expectedDescription, description.getText());

	}

	public void verifyLeadershipHistoryPageTopSection(String sectionTitle, String sectionDescription) {

		WebElement title = Element.getPageElement(testConfig, How.xPath, ".//h1");
		WebElement description = Element.getPageElement(testConfig, How.xPath, ".//h1/parent::div/div");

		Helper.compareEquals(testConfig, "Title for top section over Leadership page", sectionTitle, title.getText());
		Helper.compareEquals(testConfig, "Description for top section over Leadership page", sectionDescription, description.getText());
	}

	public void verifyDistinctionPageTopSection(String sectionTitle, String sectionDescription) {

		WebElement title = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'brp-page-container')]/h1");
		WebElement description = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'brp-page-container')]/h1/parent::div/div[1]");

		Helper.compareEquals(testConfig, "Title for top section over Distinction page", sectionTitle, title.getText());
		Helper.compareEquals(testConfig, "Description for top section over Distinction page", sectionDescription, description.getText());
	}

	public PressReleaseArticlePage navigateToPressReleaseArticlePage(String sectionHeading) {

		String headingLocForFirstArticle = "(.//h2[contains(text(),'Press Release')]/parent::div//a/h4)[1]";
		WebElement headingtext = Element.getPageElement(testConfig, How.xPath, headingLocForFirstArticle);

		String heading = Element.getText(testConfig, headingtext, "Heading for first press release article");
		testConfig.putRunTimeProperty("ArticleHeading", heading);

		Element.click(testConfig, headingtext, "Press release article 1st tile");

		return new PressReleaseArticlePage(testConfig);
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

	public void verifyTabsAreSelected(String expectedBorderColor, AboutPageTabs aboutPageTabs) {

		Browser.wait(testConfig, 2);
		WebElement requiredTab = null;
		String locator = "";
		switch (aboutPageTabs) {
		case Distinction:
			locator = ".//a[text()='Distinction']";
			break;

		case History:
			locator = ".//a[text()='History']";
			break;

		case InvestorMediaRelations:
			locator = ".//a[contains(@class,'pb-5')][text()='Investor & Media Relations']";
			break;

		case Leadership:
			locator = ".//a[text()='Leadership']";
			break;

		case Values:
			locator = ".//a[text()='Values']";
			break;
		}

		requiredTab = Element.getPageElement(testConfig, How.xPath, locator);
		Element.click(testConfig, requiredTab, aboutPageTabs.toString() + " tab");
		Browser.wait(testConfig, 4);

		WebElement selectedTab = Element.getPageElement(testConfig, How.xPath, locator);
		String cssValue = selectedTab.getCssValue("border-color");
		String hexcolor = Color.fromString(cssValue).asHex();
		Helper.compareEquals(testConfig, "Border color for " + aboutPageTabs.toString() + " tab", expectedBorderColor, hexcolor);
	}
}
