package EXSquared.Brookfield;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Helper;
import Utils.Element.How;

public class BlogDetailPage extends BRPHelper {

	@FindBy(css = "h1.blog-header__title")
	private WebElement blogTitle;

	public BlogDetailPage(Config testConfig) {

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
		Browser.waitForPageLoad(testConfig, blogTitle);
	}

	public void verifyBlogDetailPageAppears(String expectedBlogTitle) {

		WebElement blogTitle = Element.getPageElement(testConfig, How.css, "h1.blog-header__title");
		String blogName = Element.getText(testConfig, blogTitle, "Blog title over page");

		if(blogName.equals(expectedBlogTitle)) {
			testConfig.logPass("Redirected to " + expectedBlogTitle + " blog article detail page correctly");
		} else {
			testConfig.logPass("Not able to redirected to '" + expectedBlogTitle + "' blog article detail page correctly... hence failing the test");
		}

	}

	public void verifyBreadcrumbDisplayingOverPage(List<String> expectedBreadcrumbs) {

		String allBreadcrumbsLocator = ".//nav[@aria-label='breadcrumbs']//a";
		List<WebElement> allBreadcrumbs = Element.getListOfElements(testConfig, How.xPath, allBreadcrumbsLocator);

		for (int i = 0; i < allBreadcrumbs.size(); i++) {
			if(i > 1) {
				if(allBreadcrumbs.get(i).getText().isEmpty()) {
					testConfig.logFail("Getting Breadcrumb item " + (i + 1) + " as blank... failing the scenario");
				} else {
					testConfig.logPass("Verified 'Breadcrumb item " + (i + 1) + "' as :-'" + allBreadcrumbs.get(i).getText() +"'");
				}
			} else {
				Helper.compareEquals(testConfig, "Breadcrumb item " + (i + 1), expectedBreadcrumbs.get(i),
						allBreadcrumbs.get(i).getText());
			}
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

	public void verifyDifferentSectionsOverPage() {

		String tagLabelLoc = ".//div[contains(@class,'rounded')]/span[contains(@class,'inline-block')]";
		WebElement tagLabelOverHeading = Element.getPageElement(testConfig, How.xPath, tagLabelLoc);
		String tagLabel = Element.getText(testConfig, tagLabelOverHeading, "Tag label over page"); 

		String titleLocator = "h1.blog-header__title";
		WebElement blogTitle = Element.getPageElement(testConfig, How.css, titleLocator);
		String blogName = Element.getText(testConfig, blogTitle, "Blog title over page"); 
		
		String blogArticleDateLoc = ".//h1[contains(@class,'blog-header__title')]/../div";
		WebElement blogArticleDate = Element.getPageElement(testConfig, How.xPath, blogArticleDateLoc);
		
		String blogArticleImageLoc = ".//div/div[not(contains(@class,'center'))]/img[contains(@class,'w-full')]";
		WebElement blogArticleImage = Element.getPageElement(testConfig, How.xPath, blogArticleImageLoc);

		if(!tagLabel.isEmpty()) {
			testConfig.logPass("Verified blog article tag label is displaying as " + blogName);
		} else {
			testConfig.logFail("Failed to verify that the blog article tag label is displaying over the page");
		}
		
		Browser.wait(testConfig, 1);
		
		if(!blogName.isEmpty()) {
			testConfig.logPass("Verified blog article heading is displaying as " + blogName);
		} else {
			testConfig.logFail("Failed to verify that the blog article heading is displaying over the page");
		}
		
		Browser.wait(testConfig, 1);
		
		if(blogArticleDate.isDisplayed()) {
			testConfig.logPass("Verified blog article date is displaying as " + blogArticleDate.getText());
		} else {
			testConfig.logFail("Failed to verify that blog article date is displaying over the page");
		}
		
		Browser.wait(testConfig, 1);
		
		String imageSrc = Element.getAttribute(testConfig, blogArticleImage, "src", " Blog article image");
		
		if(!imageSrc.isEmpty()) {
			testConfig.logPass("Getting image displaying over card with url as " + imageSrc);
		} else {
			testConfig.logFail("Getting image src displaying over card value as blank... failing the scenario");
		}
		
		verifyURLAsPerDomain(imageSrc);
	}

	public BlogPage verifyBackButtonFunctionality() {

		String backButtonLoc = ".//div[contains(@class,'back-button')]";
		WebElement backButton = Element.getPageElement(testConfig, How.xPath, backButtonLoc);
		Element.click(testConfig, backButton, "Back button");
		Helper.removeCookies(testConfig);
		return new BlogPage(testConfig);
	}

	public void verifyCardsAfterClickingViewMoreBtn(int initialCount, int viewMoreLoad) throws UnsupportedEncodingException {

		String allCardsLocator = ".//div[contains(@class,'flex-col')][contains(@class,'cursor-pointer')]";
		List<WebElement> cardsVisibleInitially = Element.getListOfElements(testConfig, How.xPath, allCardsLocator);
		
		Helper.compareEquals(testConfig, "Initial card count when page loaded", initialCount, cardsVisibleInitially.size());
		Browser.wait(testConfig, 2);
		
		String viewMoreBtnLocator = ".//button[contains(text(),'View More')]";
		WebElement viewMoreBtn = Element.getPageElement(testConfig, How.xPath, viewMoreBtnLocator);
		Element.click(testConfig, viewMoreBtn, "View More button");
		Browser.wait(testConfig, 6);
		
		List<WebElement> cardsVisibleNow = Element.getListOfElements(testConfig, How.xPath, allCardsLocator);
		Helper.compareEquals(testConfig, "Total cards count after clicking 'View More' button 1st time", initialCount + viewMoreLoad, cardsVisibleNow.size());
	
		Element.click(testConfig, viewMoreBtn, "View More button");
		Browser.wait(testConfig, 6);
		
		cardsVisibleNow = Element.getListOfElements(testConfig, How.xPath, allCardsLocator);
		Helper.compareEquals(testConfig, "Total cards count after clicking 'View More' button 2nd time", initialCount + (viewMoreLoad*2), cardsVisibleNow.size());
	
		WebElement viewAll = Element.getPageElement(testConfig, How.xPath, ".//button[contains(text(),'View All')]");

		if(viewAll.isDisplayed()) {
			testConfig.logPass("Verified now we are getting 'View All' button to be displayed");
		} else {
			testConfig.logFail("Failure to verify that now we are getting 'View All' button to be displayed");
		}
	}

	public BlogPage verifyNavigationForBlogHomeButton() {

		String blogHomeBtnLoc = ".//button[contains(@class,'btn secondary')]";
		WebElement blogHomeBtn = Element.getPageElement(testConfig, How.xPath, blogHomeBtnLoc);
		Element.click(testConfig, blogHomeBtn, "View all button");
		Helper.removeCookies(testConfig);
		return new BlogPage(testConfig);
	}

	public BlogDetailPage clickOnCardToNavigateToDetailPage() {
		
		String allCardsLocator = ".//div[contains(@class,'flex-col')]//h5";
		List<WebElement> cardsVisibleInitially = Element.getListOfElements(testConfig, How.xPath, allCardsLocator);
		String firstCardHeading = cardsVisibleInitially.get(1).getText();
		testConfig.putRunTimeProperty("BlogTitle", firstCardHeading);
		Element.click(testConfig, cardsVisibleInitially.get(1), "Card with title as '" + firstCardHeading + "'");
		return this;
	}
}
