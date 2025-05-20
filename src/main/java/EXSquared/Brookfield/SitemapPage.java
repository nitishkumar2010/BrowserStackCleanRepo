package EXSquared.Brookfield;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;

public class SitemapPage extends BRPHelper {

	@FindBy(xpath = ".//*[contains(text(),'This XML')]")
	private WebElement xmlTitle;
	
	public SitemapPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		String sitemapurl = testConfig.getRunTimeProperty("SitemapPage");
		Browser.navigateToURL(testConfig, sitemapurl);

		PageFactory.initElements(this.testConfig.driver, this);
		Browser.waitForPageLoad(testConfig, xmlTitle);
	}

	public void getAllContentInFile(String file) {

		Browser.wait(testConfig, 5);
		WebElement content = Element.getPageElement(testConfig, How.tagName, "body");
		String allContent = content.getText();
		allContent = allContent.replace("This XML file does not appear to have any style information associated with it. The document tree is shown below.", "");
		modifyFile(file, allContent);

	}
	
	private void modifyFile(String filePath, String newContent) {
		File fileToBeModified = new File(filePath);
		String oldContent = "";
		BufferedReader reader = null;
		FileWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(fileToBeModified));
			String line = reader.readLine();
			while (line != null) {
				oldContent = oldContent + line + System.lineSeparator();
				line = reader.readLine();
			}
			writer = new FileWriter(fileToBeModified);
			writer.write(newContent);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
