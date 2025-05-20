package EXSquared.Brookfield;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;

public class VisualizerPage extends BRPHelper {

	@FindBy(xpath = ".//div[contains(@class,'heading')]/div[contains(@class,'title')]")
	private WebElement brookfieldResidentialTitle;

	public VisualizerPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		PageFactory.initElements(this.testConfig.driver, this);
		Browser.waitForPageLoad(testConfig, brookfieldResidentialTitle);
	}

	
}
