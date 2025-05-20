package BRP;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import EXSquared.Brookfield.BRPHelper;
import EXSquared.Brookfield.BRPHelper.ExpectedPage;
import EXSquared.Brookfield.BlogPage;
import Utils.Config;
import Utils.TestBase;

public class TestBlogArticlePage extends TestBase {

	@Test(timeOut = DEFAULT_TEST_TIMEOUT, description = "Verify breadcrumbs displaying over the page", dataProvider = "GetTestConfig")
	public void verifyBreadcrumbsDisplayingOverPage(Config testConfig) {

		String expectedBreadcrumbArr[] = { "Home", "News & Blog" };
		List<String> expectedBreadcrumbs = new ArrayList<>();
		for (int i = 0; i < expectedBreadcrumbArr.length; i++) {
			expectedBreadcrumbs.add(expectedBreadcrumbArr[i]);
		}

		BRPHelper brpHelper = new BRPHelper(testConfig);
		brpHelper.blogPage = (BlogPage) brpHelper.navigateToRequiredPage(ExpectedPage.BlogPage);
		brpHelper.blogDetailPage = brpHelper.blogPage.clickOnCardToNavigateToDetailPage();
		brpHelper.blogDetailPage.verifyBreadcrumbDisplayingOverPage(expectedBreadcrumbs);
	}
	
	@Test(timeOut = DEFAULT_TEST_TIMEOUT, description = "Verify Different sections displaying over the page", dataProvider = "GetTestConfig")
	public void verifyDifferentSectionsDisplaying(Config testConfig) {

		BRPHelper brpHelper = new BRPHelper(testConfig);
		brpHelper.blogPage = (BlogPage) brpHelper.navigateToRequiredPage(ExpectedPage.BlogPage);
		brpHelper.blogDetailPage = brpHelper.blogPage.clickOnCardToNavigateToDetailPage();
		brpHelper.blogDetailPage.verifyDifferentSectionsOverPage();
	}

}
