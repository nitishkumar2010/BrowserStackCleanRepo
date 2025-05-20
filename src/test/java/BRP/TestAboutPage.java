package BRP;

import java.util.HashMap;

import org.testng.annotations.Test;

import EXSquared.Brookfield.AboutPage;
import EXSquared.Brookfield.BRPHelper;
import EXSquared.Brookfield.AboutPage.AboutPageTabs;
import EXSquared.Brookfield.BRPHelper.ExpectedPage;
import Utils.Config;
import Utils.TestBase;
import Utils.TestDataReader;

public class TestAboutPage extends TestBase {

	@Test(timeOut = DEFAULT_TEST_TIMEOUT, description = "Verify content present over Leadership page", dataProvider = "GetTestConfig")
	public void verifyLeadershipSectionAboutPage(Config testConfig) {

		String sectionTitle = "Senior Leadership & Market Presidents";
		String sectionDescription = "When it comes to building great communities, "
				+ "experience is a key differentiator. That's why our team has "
				+ "some of the best, most seasoned people in the industry. Our "
				+ "management teams have an average of 20 years experience and "
				+ "are committed to building the best communities in their regions.";
		String sheetName = "AboutLeadershipStage";

		TestDataReader reader = testConfig.getCachedTestDataReaderObject(sheetName);
		HashMap<String, String> expectedData = new HashMap<>();
		for (int i = 1; i < reader.getRecordsNum(); i++) {
			String name = reader.GetData(i, "Name");
			String designation = reader.GetData(i, "Designation");
			expectedData.put(name, designation);
		}

		BRPHelper brpHelper = new BRPHelper(testConfig);
		brpHelper.aboutPage = (AboutPage) brpHelper.navigateToRequiredPage(ExpectedPage.AboutPage);
		brpHelper.aboutPage.navigateToRequiredTabContent(AboutPageTabs.Leadership);
		brpHelper.aboutPage.validateInvalidImages(testConfig);
		brpHelper.aboutPage.verifyLeadershipHistoryPageTopSection(sectionTitle, sectionDescription);
		brpHelper.aboutPage.verifyLeadershipContent(expectedData, reader);
	}

	@Test(timeOut = DEFAULT_TEST_TIMEOUT, description = "Verify different tabs and hero image present over about page", dataProvider = "GetTestConfig")
	public void verifyTabsAndHeroImageOverAboutPage(Config testConfig) {

		String[] expectedTabs = { "Leadership", "Distinction", "History", "Values", "Investor & Media Relations" };
		BRPHelper brpHelper = new BRPHelper(testConfig);
		brpHelper.aboutPage = (AboutPage) brpHelper.navigateToRequiredPage(ExpectedPage.AboutPage);
		brpHelper.aboutPage.verifyTabsPresentOverPage(expectedTabs);
	}

}
