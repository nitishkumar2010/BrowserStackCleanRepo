package BRP;

import org.testng.annotations.Test;

import EXSquared.Brookfield.AboutPage;
import EXSquared.Brookfield.BRPHelper;
import EXSquared.Brookfield.BRPHelper.ExpectedPage;
import Utils.Config;
import Utils.TestBase;

public class TestAboutPage extends TestBase {

	@Test(timeOut = DEFAULT_TEST_TIMEOUT, description = "Verify different tabs and hero image present over about page", dataProvider = "GetTestConfig")
	public void verifyTabsAndHeroImageOverAboutPage(Config testConfig) {

		String[] expectedTabs = { "Leadership", "Distinction", "History", "Values", "Investor & Media Relations" };
		BRPHelper brpHelper = new BRPHelper(testConfig);
		brpHelper.aboutPage = (AboutPage) brpHelper.navigateToRequiredPage(ExpectedPage.AboutPage);
		brpHelper.aboutPage.verifyTabsPresentOverPage(expectedTabs);
	}

}
