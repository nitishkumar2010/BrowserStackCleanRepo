package EXSquared.Brookfield;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;

public class SitecoreLaunchpadPage extends BRPHelper {

	@FindBy(xpath = ".//span[text()='Sitecore Experience Platform']")
	private WebElement launchpad;

	public SitecoreLaunchpadPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		PageFactory.initElements(this.testConfig.driver, this);
		Browser.waitForPageLoad(testConfig, launchpad);

	}
	
	public SitecorePowershellISEPage navigateToPowershellISE() {
		
		String locator = ".//img[@alt='PowerShell ISE']/parent::span/parent::a";
		WebElement powershellTile = Element.getPageElement(testConfig, How.xPath, locator);
		Element.click(testConfig, powershellTile, "Powershell ISE tile");
		return new SitecorePowershellISEPage(testConfig);
	}

}
