package EXSquared.Brookfield;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.locators.RelativeLocator;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;
import Utils.Helper;

public class CareersPage extends BRPHelper {

	public enum CareersCards {
		WhyBrookfield, JoinUs, LifeAtBrookfield, TotalCompensation, InterviewPrep, OurHistory
	}
	
	@FindBy(xpath = ".//a[text()='Join our team!']")
	private WebElement careersTitle;

	public CareersPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		PageFactory.initElements(this.testConfig.driver, this);
//		try {
//			Helper.removeCookies(testConfig);
//			testConfig.driver.navigate().refresh();
//			WebElement acceptCookies = Element.getPageElement(testConfig, How.css,
//					"button#onetrust-accept-btn-handler");
//			Element.click(testConfig, acceptCookies, "One trust accept cookies button");
//			testConfig.putRunTimeProperty("CookieSetting", "Yes");
//		} catch (Exception e) {
//			testConfig.putRunTimeProperty("CookieSetting", "No");
//			testConfig.logComment("Accept cookie section not displayed");
//		}
		Browser.waitForPageLoad(testConfig, careersTitle);
	}

	public void verifyTilesContentDisplayingOnHover(HashMap<String, String> expectedData) {

		HashMap<String, String> pageContent = new HashMap<>();
		List<WebElement> headingValues = Element.getListOfElements(testConfig, How.xPath,
				".//div[contains(@class,'2xl:text-[3.75rem]')]");
		List<WebElement> contentAfterHover = Element.getListOfElements(testConfig, How.xPath,
				".//div[contains(@class,'group-hover:opacity-100')]");

		for (int i = 0; i < contentAfterHover.size(); i++) {
			pageContent.put(headingValues.get(i).getAttribute("innerText"),
					contentAfterHover.get(i).getAttribute("innerText").replace("\u00a0", "").trim());
		}

		Helper.compareEquals(testConfig, expectedData, pageContent);
	}

	public void verifyRedirectionForAllCards(CareersCards careerCards) {

		Browser.wait(testConfig, 5);
		String locator = "", expectedLink = "";
		switch (careerCards) {
		case WhyBrookfield:
			locator = "(.//div[contains(@class,'brp-page-container')]/a)[1]";
			expectedLink = homeurl + "careers/why-brookfield";
			break;

		case InterviewPrep:
			locator = "(.//div[contains(@class,'brp-page-container')]/a)[5]";
			expectedLink = homeurl + "careers/interview-prep";
			break;

		case JoinUs:
			locator = "(.//div[contains(@class,'brp-page-container')]/a)[2]";
			expectedLink = "https://brookfield.wd5.myworkdayjobs.com/brookfieldproperties";
			break;

		case LifeAtBrookfield:
			locator = "(.//div[contains(@class,'brp-page-container')]/a)[3]";
			expectedLink = homeurl + "careers/life-at-brookfield";
			break;

		case OurHistory:
			locator = "(.//div[contains(@class,'brp-page-container')]/a)[6]";
			expectedLink = "https://www.brookfieldresidential.com/about/history";
			break;

		case TotalCompensation:
			locator = "(.//div[contains(@class,'brp-page-container')]/a)[4]";
			expectedLink = homeurl + "careers/total-compensation";
			break;
		}
		
		By username = RelativeLocator.with(By.className("abc")).above(By.xpath(".//input"));
		WebElement card = Element.getPageElement(testConfig, How.xPath, locator);
		Element.click(testConfig, card, careerCards.toString() + " card");
		Browser.wait(testConfig, 6);
		verifyRedirectionBasedOnCard(expectedLink, careerCards);
	}

	private void verifyRedirectionBasedOnCard(String expectedLink, CareersCards careerCards) {

		switch (careerCards) {
		case InterviewPrep:
		case LifeAtBrookfield:
		case OurHistory:
		case TotalCompensation:
		case WhyBrookfield:
			String currentUrl = testConfig.driver.getCurrentUrl();
			Helper.compareEquals(testConfig, "Current page url after clicking on " + careerCards.toString() + " card",
					expectedLink, currentUrl);
			testConfig.driver.navigate().back();
			break;

		case JoinUs:
			String winHandleBefore = testConfig.driver.getWindowHandle();
			for (String winHandle : testConfig.driver.getWindowHandles()) {
				testConfig.driver.switchTo().window(winHandle);
			}
			testConfig.logComment("Switching to the new window opened...");
			String currentPageUrl = testConfig.driver.getCurrentUrl();
			Helper.compareContains(testConfig, "Current page url after clicking on " + careerCards.toString() + " card",
					expectedLink, currentPageUrl);
			testConfig.driver.close();
			testConfig.driver.switchTo().window(winHandleBefore);
			break;
		}
	}

	public void validateTopSectionDetails(String expectedTitle, String expectedDescription, String expectedLinkText, String expectedLink) {
		String headingLocator = "div.brp-page-container>h1";
		String descriptionLocator = "div.text-brp-blue-100>p:nth-child(1)";
		String linkLocator = "div.text-brp-blue-100>p>a";	
		
		WebElement headingText = Element.getPageElement(testConfig, How.css, headingLocator);
		WebElement descText = Element.getPageElement(testConfig, How.css, descriptionLocator);
		WebElement linkElement = Element.getPageElement(testConfig, How.css, linkLocator);
		
		Helper.compareContains(testConfig, "Title on the page", expectedTitle, headingText.getText());
		Helper.compareContains(testConfig, "Description on the page", expectedDescription, descText.getText());
		Helper.compareEquals(testConfig, "Text associated with the link on the page", expectedLinkText, linkElement.getText());
		Helper.compareContains(testConfig, "Link associated with " + linkElement.getText() + " on the page", expectedLink, linkElement.getAttribute("href"));
	}

	public void verifyHeroImageOverPage() {

		WebElement heroImage = Element.getPageElement(testConfig, How.xPath, ".//div/img[contains(@class,'object-contain')]");
		String imageSrc = Element.getAttribute(testConfig, heroImage, "src", "Hero image view");
		verifyURLAsPerDomain(imageSrc);

	}

}
