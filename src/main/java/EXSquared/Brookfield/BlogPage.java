package EXSquared.Brookfield;

import java.util.ArrayList;
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

public class BlogPage extends BRPHelper {

	@FindBy(xpath = ".//h1[text()='News and Blog']")
	private WebElement newsAndBlogHeading;

	@FindBy(xpath = ".//p[contains(text(),'Region')]//parent::div/button")
	private WebElement allRegionsDropdown;

	@FindBy(xpath = ".//p[contains(text(),'Category')]//parent::div/button")
	private WebElement allCategoriesDropdown;

	public BlogPage(Config testConfig) {

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
		Browser.waitForPageLoad(testConfig, newsAndBlogHeading);
	}

	public void verifyAllCategoriesDropdownValues(List<String> expectedAllCategories) {

		List<String> allCategoriesOverPage = new ArrayList<>();
		Element.click(testConfig, allCategoriesDropdown, "All Categories dropdown");
		Browser.wait(testConfig, 2);

		String allCategoriesLocator = ".//p[contains(text(),'Category')]//parent::div/ul/li//span";
		List<WebElement> allCategories = Element.getListOfElements(testConfig, How.xPath, allCategoriesLocator);

		for (Iterator<WebElement> iterator = allCategories.iterator(); iterator.hasNext();) {
			WebElement webElement = (WebElement) iterator.next();
			allCategoriesOverPage.add(webElement.getText());
		}

		if(expectedAllCategories.equals(allCategoriesOverPage)) {
			testConfig.logPass("Verified that correct categories are displaying over page as " + allCategoriesOverPage.toString());
		} else {
			testConfig.logFail("Failed to verify that correct categories are displaying over page");
			testConfig.logComment("Expected categories : " + expectedAllCategories.toString());
			testConfig.logComment("Actual categories : " + allCategoriesOverPage.toString());
		}

	}

	public void selectRegionAndCategory(String region, String category) {

		Element.click(testConfig, allRegionsDropdown, "All regions dropdown");
		Browser.wait(testConfig, 2);

		String allRegionLocator = ".//p[contains(text(),'Region')]//parent::div/ul/li//span";
		List<WebElement> allRegions = Element.getListOfElements(testConfig, How.xPath, allRegionLocator);

		for (int i = 0; i < allRegions.size(); i++) {
			if(region.equals(allRegions.get(i).getAttribute("innerText"))) {
				Element.click(testConfig, allRegions.get(i), region + " value");
				break;
			}
		}

		Browser.wait(testConfig, 2);
		Element.click(testConfig, allCategoriesDropdown, "All Categories dropdown");
		Browser.wait(testConfig, 2);

		String allCategoriesLocator = ".//p[contains(text(),'Category')]//parent::div/ul/li//span";
		List<WebElement> allCategories = Element.getListOfElements(testConfig, How.xPath, allCategoriesLocator);

		for (int i = 0; i < allCategories.size(); i++) {
			if(category.equals(allCategories.get(i).getAttribute("innerText"))) {
				Element.click(testConfig, allCategories.get(i), category + " value");
				break;
			}
		}
		Browser.wait(testConfig, 2);
		Element.click(testConfig, allCategoriesDropdown, "All Categories dropdown");

	}

	public void verifyBtnEnabledWhenSelectingAndClearingValues() {

		String clearBtnLocator = ".//button[contains(text(),'CLEAR FILTERS')]";
		WebElement clearBtn = Element.getPageElement(testConfig, How.xPath, clearBtnLocator);

		if(!clearBtn.isEnabled()) {
			testConfig.logFail("Getting clear filter button as enabled even after selecting values... hence failing the test");
		} else {
			testConfig.logPass("Verified clear filter button is enabled after selecting region and category as expected");
		}

		Browser.wait(testConfig, 2);
		Element.click(testConfig, clearBtn, "Clear button");
		Browser.wait(testConfig, 2);

		clearBtn = Element.getPageElement(testConfig, How.xPath, clearBtnLocator);

		if(clearBtn.isEnabled()) {
			testConfig.logFail("Getting clear filter button as enabled after clearing filter... hence failing the test");
		} else {
			testConfig.logPass("Verified clear filter button is disabled after clearing filter as expected");
		}
	}

	public void verifySelectedRegionCategories(String[] regions, String[] categories) {

		Element.click(testConfig, allRegionsDropdown, "All regions dropdown");
		Browser.wait(testConfig, 2);

		String allRegionLocator = ".//button[text()=' All Regions ']/..//li//span";
		List<WebElement> allRegions = Element.getListOfElements(testConfig, How.xPath, allRegionLocator);

		String checkboxInputRegion = ".//button[text()=' All Regions ']/..//li//span/../input";
		List<WebElement> checkboxRegionVal = Element.getListOfElements(testConfig, How.xPath, checkboxInputRegion);

		for (int i = 0; i < checkboxRegionVal.size(); i++) {
			if(allRegions.get(i).getAttribute("innerText").equals(regions[0]) ||  allRegions.get(i).getAttribute("innerText").equals(regions[1])) {
				if(checkboxRegionVal.get(i).isSelected()) {
					testConfig.logPass("Verified that " + allRegions.get(i).getAttribute("innerText") + " is selected");
				} else {
					testConfig.logFail("Failed to verify that " + allRegions.get(i).getAttribute("innerText") + " is selected");
				}
			} else {
				if(!checkboxRegionVal.get(i).isSelected()) {
					testConfig.logPass("Verified that " + allRegions.get(i).getAttribute("innerText") + " is not selected");
				} else {
					testConfig.logFail("Failed to verify that " + allRegions.get(i).getAttribute("innerText") + " is not selected");
				}
			}
		}

		Element.click(testConfig, allCategoriesDropdown, "All Categories dropdown");
		Browser.wait(testConfig, 2);

		String allCategoriesLocator = ".//button[contains(text(),'All Categories')]/..//li//span";
		List<WebElement> allCategories = Element.getListOfElements(testConfig, How.xPath, allCategoriesLocator);

		String checkboxInputCategory = ".//button[contains(text(),'All Categories')]/..//li//span/../input";
		List<WebElement> checkboxCategoryVal = Element.getListOfElements(testConfig, How.xPath, checkboxInputCategory);

		for (int i = 0; i < checkboxCategoryVal.size(); i++) {
			if(allCategories.get(i).getAttribute("innerText").equals(categories[0]) ||  allCategories.get(i).getAttribute("innerText").equals(categories[1])) {
				if(checkboxCategoryVal.get(i).isSelected()) {
					testConfig.logPass("Verified that " + allCategories.get(i).getAttribute("innerText") + " is selected");
				} else {
					testConfig.logFail("Failed to verify that " + allCategories.get(i).getAttribute("innerText") + " is selected");
				}
			} else {
				if(!checkboxCategoryVal.get(i).isSelected()) {
					testConfig.logPass("Verified that " + allCategories.get(i).getAttribute("innerText") + " is not selected");
				} else {
					testConfig.logFail("Failed to verify that " + allCategories.get(i).getAttribute("innerText") + " is not selected");
				}
			}
		}	
	}

	public void verifySelectingValuesUpdatesResult(String region, String category) {

		String firstCardHeadingLoc = "(.//div[contains(@class,'flex-col')]//h5)[2]";
		WebElement firstCardHeading = Element.getPageElement(testConfig, How.xPath, firstCardHeadingLoc);

		String titleBeforeFilter = firstCardHeading.getText();
		testConfig.logComment("Second card title : " + titleBeforeFilter);

		selectRegionAndCategory(region, category);

		WebElement secondCardHeadingAfterFilter = Element.getPageElement(testConfig, How.xPath, firstCardHeadingLoc);
		String titleAfterFilter = secondCardHeadingAfterFilter.getText();
		testConfig.logComment(
				"Second card title after selecting " + region + " and " + category + " : " + titleAfterFilter);

		if (titleBeforeFilter.equals(titleAfterFilter)) {
			testConfig.logFail("Cards not updated as second card heading is still same as '" + titleBeforeFilter + "'");
		} else {
			testConfig.logPass("Cards updated correctly as second card heading is updated from '" + titleBeforeFilter
					+ "' to '" + titleAfterFilter + "'");
		}
	}

	public void verifyCardsAfterClickingViewMoreBtn(int initialCount, int viewMoreLoad) {

		String allCardsLocator = ".//div[contains(@class,'flex-col')]//h5";
		List<WebElement> cardsVisibleInitially = Element.getListOfElements(testConfig, How.xPath, allCardsLocator);
		
		Helper.compareEquals(testConfig, "Initial card count when page loaded", initialCount, cardsVisibleInitially.size());
		Browser.wait(testConfig, 2);
		
		String viewMoreBtnLocator = ".//button[contains(@class,'btn secondary')]";
		WebElement viewMoreBtn = Element.getPageElement(testConfig, How.xPath, viewMoreBtnLocator);
		Element.click(testConfig, viewMoreBtn, "View More button");
		Browser.wait(testConfig, 12);
		
		List<WebElement> cardsVisibleNow = Element.getListOfElements(testConfig, How.xPath, allCardsLocator);
		Helper.compareEquals(testConfig, "Total cards count after clicking 'View More' button 1st time", initialCount + viewMoreLoad, cardsVisibleNow.size());
	
		Element.click(testConfig, viewMoreBtn, "View More button");
		Browser.wait(testConfig, 12);
		
		cardsVisibleNow = Element.getListOfElements(testConfig, How.xPath, allCardsLocator);
		Helper.compareEquals(testConfig, "Total cards count after clicking 'View More' button 2nd time", initialCount + (viewMoreLoad*2), cardsVisibleNow.size());
	
	}

	public BlogDetailPage clickOnCardToNavigateToDetailPage() {
		
		String allCardsLocator = ".//div[contains(@class,'flex-col')]//h5";
		List<WebElement> cardsVisibleInitially = Element.getListOfElements(testConfig, How.xPath, allCardsLocator);
		String firstCardHeading = cardsVisibleInitially.get(0).getText();
		testConfig.putRunTimeProperty("BlogTitle", firstCardHeading);
		Element.click(testConfig, cardsVisibleInitially.get(0), "Card with title as '" + firstCardHeading + "'");
		Helper.removeCookies(testConfig);
		return new BlogDetailPage(testConfig);
	}

	public void verifyHeroSection(String expectedHeading) {

		String locator = ".//h1[contains(@class,'font-bold')]";
		WebElement heading = Element.getPageElement(testConfig, How.xPath, locator);
		Helper.compareEquals(testConfig, "Page heading", expectedHeading, heading.getText());
	}

	public void verifyDifferentSectionsDisplayingOverPage() {

		if(allRegionsDropdown.isDisplayed()) {
			testConfig.logPass("Verified that All Regions field is displaying correctly");
		} else {
			testConfig.logFail("Failed to verify that All Regions field is displaying over the page");
		}
		Browser.wait(testConfig, 1);
		
		if(allCategoriesDropdown.isDisplayed()) {
			testConfig.logPass("Verified that All Categories field is displaying correctly");
		} else {
			testConfig.logFail("Failed to verify that All Categories field is displaying over the page");
		}
		Browser.wait(testConfig, 1);
	}

	public void verifyRegionDisableOnCategorySelection(String[] categories) {
		
		Element.click(testConfig, allCategoriesDropdown, "All Categories dropdown");
		Browser.wait(testConfig, 2);
		
		for (int i = 0; i < categories.length; i++) {
			String allCategoriesLocator = ".//p[contains(text(),'Category')]//parent::div/ul/li//span";
			List<WebElement> allCategories = Element.getListOfElements(testConfig, How.xPath, allCategoriesLocator);
			for (int j = 0; j < categories.length; j++) {
				if(categories[i].equals(allCategories.get(j).getText())) {
					Element.click(testConfig, allCategories.get(j), categories[i] + " category value");
					Browser.waitWithoutLogging(testConfig, 3);
					verifyFewRegionsGetDisable(categories[i]);
					break;
				}	
			}
		}
	}

	private void verifyFewRegionsGetDisable(String category) {
		
		int enableCount = 0;
		
		Element.click(testConfig, allRegionsDropdown, "All regions dropdown");
		Browser.wait(testConfig, 2);

		String allRegionNameLocator = ".//p[contains(text(),'Region')]//parent::div/ul/li//span";
		String allRegionLiLocator = ".//p[contains(text(),'Region')]//parent::div/ul/li";
		List<WebElement> allRegions = Element.getListOfElements(testConfig, How.xPath, allRegionNameLocator);
		List<WebElement> allRegionsLiLocatorValue = Element.getListOfElements(testConfig, How.xPath, allRegionLiLocator);

		for (int i = 0; i < allRegions.size(); i++) {
			if (allRegionsLiLocatorValue.get(i).getAttribute("aria-disabled") != null && allRegionsLiLocatorValue.get(i).getAttribute("aria-disabled").equals("true")) {
				testConfig.logPass("Getting " + allRegions.get(i).getText() + " disabled for " + category + " category");
			} else {
				enableCount++;
				testConfig.logPass("Getting " + allRegions.get(i).getText() + " enabled for " + category + " category");
			}
		}
		
		if(enableCount == 0) {
			testConfig.logFail("No region is displaying enabled in case of selecting " + category + " category");
		}
		
		
		Element.click(testConfig, allCategoriesDropdown, "All Categories dropdown");
		Browser.wait(testConfig, 2);
	}
}
