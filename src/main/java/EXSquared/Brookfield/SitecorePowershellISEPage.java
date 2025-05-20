package EXSquared.Brookfield;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;

public class SitecorePowershellISEPage extends BRPHelper {

	@FindBy(xpath = ".//a[@class='scRibbonToolbarLargeComboButtonTop'][@title='Clear the editor and start creating a new script. (Alt+N)']")
	private WebElement newEditorWindow;

	@FindBy(xpath = ".//a[@class='scRibbonToolbarLargeComboButtonTop'][@title='Execute the script as a background job. (Ctrl+E)']")
	private WebElement executeScript;

	public SitecorePowershellISEPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		PageFactory.initElements(this.testConfig.driver, this);
		Browser.waitForPageLoad(testConfig, newEditorWindow);

	}

	public void executeScriptAndExtractResults(String scriptContent) {

		String contentBox = "div.ace_layer.ace_text-layer";
		WebElement contentTextArea = Element.getPageElement(testConfig, How.css, contentBox);
		Browser.wait(testConfig, 2);
		Actions action = new Actions(testConfig.driver);
		action.moveToElement(contentTextArea).perform();
		action.moveToElement(contentTextArea).click().perform();

		action.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).build().perform();
		action.sendKeys(Keys.BACK_SPACE).build().perform();

		StringSelection stringSelection = new StringSelection(scriptContent.toString());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		Browser.wait(testConfig, 2);
		action.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).build().perform();

		Element.click(testConfig, executeScript, "Execute script");
		Browser.wait(testConfig, 2);

		WebElement iframeElement = Element.getPageElement(testConfig, How.id, "jqueryModalDialogsFrame");
		System.out.println(iframeElement.getAttribute("id"));
		testConfig.driver.switchTo().frame(iframeElement);

		WebElement inneriframeElement = Element.getPageElement(testConfig, How.id, "scContentIframeId0");
		System.out.println(inneriframeElement.getAttribute("id"));
		testConfig.driver.switchTo().frame(inneriframeElement);

		String passwordVal = testConfig.getRunTimeProperty("SitecorePwd");
		WebElement passwordField = Element.getPageElement(testConfig, How.xPath, ".//input[@id='PasswordBox']");
		Element.enterData(testConfig, passwordField, passwordVal, "Password field value");

		WebElement okBtn = Element.getPageElement(testConfig, How.xPath, ".//button[@id='OKButton']");
		Element.click(testConfig, okBtn, "Ok button");
		Browser.wait(testConfig, 2);

		testConfig.driver.switchTo().defaultContent();

		String url = "";
		List<WebElement> resultsList = Element.getListOfElements(testConfig, How.xPath,
				".//pre[@id='ScriptResultCode']/span");
		for (int i = 0; i < resultsList.size(); i++) {
			testConfig.logComment(resultsList.get(i).getText());
			String value = resultsList.get(i).getText();
			url = value.substring(value.indexOf("Url:") + 4, value.indexOf(", Home Type"));
		}
		
		Browser.navigateToURL(testConfig, homeurl);
		testConfig.putRunTimeProperty("QMIUrl", homeurl + url.trim());
	}

	public QMIPage navigateToQMIPage(String url) {
	
		Browser.navigateToURL(testConfig, url);
		return new QMIPage(testConfig);
	}

}
