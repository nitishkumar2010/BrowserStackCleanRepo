package EXSquared.Brookfield;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Helper;
import Utils.Element.How;

public class SearchResultsPage extends BRPHelper {

	@FindBy(xpath = ".//div[contains(@class,'breadcrumb')]//a[text()='Search Results']")
	private WebElement searchResultBreadcrumb;

	public SearchResultsPage(Config testConfig) {

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
		Browser.waitForPageLoad(testConfig, searchResultBreadcrumb);
	}

	public void verifyResultListDisplaying(String searchedWord) {
		
		String locator = ".//div[@aria-labelledby='Search List']";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, locator);
		if(allCards.size() > 0) {
			testConfig.logPass("Getting total " + allCards.size() + " result list displaying for " + searchedWord + " searched keyword");
		} else {
			testConfig.logFail("Not getting search result list displaying for the searched location... failing the test");
		}
	}

	public void verifyNoMatchesFoundMsgDisplaying(String errorTitle, String errorSubtitle) {

		String errorTitleLocator = ".//h2[contains(@class,'text-blue-900')]";
		String errorSubtitleLocator = "div.mx-auto.text-base";

		WebElement errorTitleOnPage = Element.getPageElement(testConfig, How.xPath, errorTitleLocator);
		WebElement errorSubtitleOnPageElement = Element.getPageElement(testConfig, How.css, errorSubtitleLocator);

		Helper.compareEquals(testConfig, "Error title", errorTitle, errorTitleOnPage.getText().trim());
		Helper.compareEquals(testConfig, "Error subtitle", errorSubtitle, errorSubtitleOnPageElement.getText().trim());
	}

}
