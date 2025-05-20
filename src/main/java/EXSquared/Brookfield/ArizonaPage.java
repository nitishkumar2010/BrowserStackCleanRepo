package EXSquared.Brookfield;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Helper;
import Utils.Element.How;

public class ArizonaPage extends BRPHelper {

	@FindBy(xpath = ".//h2[text()='Phoenix, Arizona']")
	private WebElement phoenixHeading;

	public ArizonaPage(Config testConfig) {

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
		
		Browser.waitForPageLoad(testConfig, phoenixHeading);
	}

	public void verifyRedirectionToCorrectPage() {
		String currentURL = testConfig.driver.getCurrentUrl();
		Helper.compareEquals(testConfig, "Current page url for Arizona location", homeurl.concat("new-homes/arizona/phoenix-county"), currentURL);		
	}

}
