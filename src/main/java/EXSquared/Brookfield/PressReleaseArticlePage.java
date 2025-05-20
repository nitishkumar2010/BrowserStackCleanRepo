package EXSquared.Brookfield;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Helper;
import Utils.Element.How;

public class PressReleaseArticlePage extends BRPHelper {

	String homeurl = testConfig.getRunTimeProperty("BrookfieldHomePage").replace("brp:@Brookfield1@", "").replace("ex2:@Brookfield1@", "");
	
	@FindBy(css = "h3.font-thin.uppercase")
	private WebElement articleHeading;

	@FindBy(xpath = ".//h3[contains(@class,'uppercase')]/parent::div/div")
	private WebElement timeField;

	public PressReleaseArticlePage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		PageFactory.initElements(this.testConfig.driver, this);
		try {
			Helper.removeCookies(testConfig);
			testConfig.driver.navigate().refresh();
			WebElement acceptCookies = Element.getPageElement(testConfig, How.css,
					"button#onetrust-accept-btn-handler");
			Element.click(testConfig, acceptCookies, "One trust accept cookies button");
			testConfig.putRunTimeProperty("CookieSetting", "Yes");
		} catch (Exception e) {
			testConfig.putRunTimeProperty("CookieSetting", "No");
			testConfig.logComment("Accept cookie section not displayed");
		}
		Browser.waitForPageLoad(testConfig, articleHeading);
	}

	public void verifyTitleForArticle() {

		Helper.compareEquals(testConfig, "Press release article heading",
				testConfig.getRunTimeProperty("ArticleHeading").toUpperCase(), articleHeading.getText());
	}

}
