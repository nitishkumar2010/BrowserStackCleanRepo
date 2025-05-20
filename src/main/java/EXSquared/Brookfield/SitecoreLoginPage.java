package EXSquared.Brookfield;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;

public class SitecoreLoginPage extends BRPHelper {

	@FindBy(css = "#UserName")
	private WebElement username;

	public SitecoreLoginPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;

		String homeurl = testConfig.getRunTimeProperty("BrookfieldSitecoreLoginPage");
		Browser.navigateToURL(testConfig, homeurl);
		PageFactory.initElements(this.testConfig.driver, this);
		Browser.waitForPageLoad(testConfig, username);

	}
	
	public SitecoreLaunchpadPage navigateToLaunchpad() {
		
		WebElement username = Element.getPageElement(testConfig, How.css, "input#UserName");
		String usernameVal = testConfig.getRunTimeProperty("SitecoreUser");
		Element.enterDataAfterClick(testConfig, username, usernameVal, "Username field value");
		
		Browser.wait(testConfig, 1);
		WebElement pwd = Element.getPageElement(testConfig, How.css, "input#Password");
		String passwordVal = testConfig.getRunTimeProperty("SitecorePwd");
		Element.enterDataAfterClick(testConfig, pwd, passwordVal, "Password field value");
		
		Browser.wait(testConfig, 1);
		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath, ".//input[@name='LogInBtn']");
		Element.click(testConfig, submitBtn, "Log In button");
		
		return new SitecoreLaunchpadPage(testConfig);
	}

}
