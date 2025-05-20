package EXSquared.Brookfield;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import Utils.Browser;
import Utils.Config;
import Utils.Element;
import Utils.Element.How;
import Utils.Helper;

public class FYHPage extends BRPHelper {

	private static final String WORD_SEPARATORS = " .-_/()";

	public enum CommunitySectionView {
		Expanded, Collapsed
	}

	public enum FilterTypes {
		PriceRange, HomeType, BedsAndBaths, SquareFootage, Stories, GarageParking, BuildStatus
	}

	public enum SortOption {
		PriceLowToHigh, PriceHighToLow, HomeSizeLowToHigh, HomeSizeHighToLow
	}

	public enum PlanTypes {
		ComingSoon, StatusSoldOut, ValidPricing, MyTimeEnabled
	}

	public enum ResultTab {
		Communities, Homes, Plans
	}

	@FindBy(xpath = ".//*[contains(text(),'near search area')]")
	private WebElement confirmationText;

	@FindBy(xpath = "(.//section[contains(@class,'fyh-experience')]//span[text()='Price Range'])[1]")
	private WebElement priceRangeBtn;

	@FindBy(xpath = ".//div[contains(@class,'mapboxgl-map')]")
	private WebElement mapbox;

	public FYHPage(Config testConfig) {

		super(testConfig);
		this.testConfig = testConfig;
		PageFactory.initElements(this.testConfig.driver, this);

		if(testConfig.getRunTimeProperty("RedirectionValue") != null && testConfig.getRunTimeProperty("RedirectionValue").equals("yes"))
		{
			try {
				Helper.removeCookies(testConfig);
				testConfig.driver.navigate().refresh();
				Browser.waitWithoutLogging(testConfig, 5);
				WebElement acceptCookies = Element.getPageElement(testConfig, How.css,
						"button#onetrust-accept-btn-handler");
				Element.click(testConfig, acceptCookies, "One trust accept cookies button");
				testConfig.putRunTimeProperty("CookieSetting", "Yes");
			} catch (Exception e) {
				testConfig.putRunTimeProperty("CookieSetting", "No");
				testConfig.logComment("Accept cookie section not displayed");
			}
		}
		Browser.waitForPageLoad(testConfig, mapbox);
	}

	public CommunityPage navigateToCommunityPage(String searchedProduct) {

		Browser.wait(testConfig, 10);
		/*
		 * try { String promoCancelBtn =
		 * ".//div[contains(@class,'promo-flex')]/div/button/div"; WebElement cancelBtn
		 * = Element.getPageElement(testConfig, How.xPath, promoCancelBtn);
		 * Element.click(testConfig, cancelBtn, "Cancel button"); } catch (Exception e)
		 * { testConfig.logComment("No promo section displayed"); }
		 */

		String communityName = testConfig.getRunTimeProperty("CommunityToSelectOverFYH");
		if (!searchedProduct.equals("Community")) {
			List<WebElement> communityMapLinks = Element.getListOfElements(testConfig, How.xPath,
					".//h3[contains(text(),'" + communityName + "')]//ancestor::div[contains(@class,'flex-wrap info')]//button/span");
			if (isClickable(testConfig, communityMapLinks.get(0))) {
				Element.clickThroughJS(testConfig, communityMapLinks.get(0),
						"View Community Map link for Community item");
			} else {
				Element.clickThroughJS(testConfig, communityMapLinks.get(1),
						"View Community Map link for Community item");
			}
		}

		Browser.wait(testConfig, 5);
		try {
			String communityNameLoc = ".//div[contains(@class,'transition-all')]/h2";
			WebElement commName = Element.getPageElement(testConfig, How.xPath, communityNameLoc);
			Element.click(testConfig, commName, "Community Name Section");
			Browser.wait(testConfig, 2);
		} catch (Exception e) {
			String promoCancelBtn = ".//div[contains(@class,'promo-flex')]/div/button/div";
			WebElement cancelBtn = Element.getPageElement(testConfig, How.xPath, promoCancelBtn);
			Element.click(testConfig, cancelBtn, "Cancel button");
			Browser.wait(testConfig, 2);
			String communityNameLoc = ".//div[contains(@class,'transition-all')]/h2";
			WebElement commName = Element.getPageElement(testConfig, How.xPath, communityNameLoc);
			Element.click(testConfig, commName, "Community Name Section");
			Browser.wait(testConfig, 2);
		}


		WebElement learnMoreLink = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'community-header')]//div/a");
		try {
			Element.click(testConfig, learnMoreLink, "View Community Page link");
		} catch (Exception e) {
			Element.clickThroughJS(testConfig, learnMoreLink, "View Community Page link");
		}

		Browser.wait(testConfig, 2);

		Set<String> windowHandle = testConfig.driver.getWindowHandles();
		testConfig.driver.switchTo().window(getLastElement(windowHandle));
		//getBase64UserNamePwdNetworkTab();

		testConfig.logComment("Switching to the new window opened...");

		return new CommunityPage(testConfig);
	}

	public FYHPage navigateToCommunityMapPage(String searchedProduct) {

		Browser.wait(testConfig, 10);
		/*
		 * try { String promoCancelBtn =
		 * ".//div[contains(@class,'promo-flex')]/div/button/div"; WebElement cancelBtn
		 * = Element.getPageElement(testConfig, How.xPath, promoCancelBtn);
		 * Element.click(testConfig, cancelBtn, "Cancel button"); } catch (Exception e)
		 * { testConfig.logComment("No promo section displayed"); }
		 */

		String communityName = testConfig.getRunTimeProperty("CommunityToSelectOverFYH");

		String scrollRequired = testConfig.getRunTimeProperty("scrollRequired");
		if (scrollRequired!= null && scrollRequired.equals("yes")) {
			testConfig.logComment("Scrolling to the end of lazy load page");
			JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
			Long windowHeight = (Long) js.executeScript(
					"return document.getElementsByClassName('sidebar-scroll-container')[0].offsetHeight");
			int numberOfPixelsToDragTheScrollbarDown = 2500, i = 0;
			while (i < windowHeight) {
				js.executeScript("document.getElementsByClassName('sidebar-scroll-container')[0].scrollTo( " + i + ", "
						+ (i + numberOfPixelsToDragTheScrollbarDown) + ")");
				i += numberOfPixelsToDragTheScrollbarDown;
				Browser.waitWithoutLogging(testConfig, 1);
				if (Element.IsElementDisplayed(testConfig,
						Element.getPageElement(testConfig, How.xPath, ".//h3[contains(text(),'" + communityName
								+ "')]//ancestor::div[contains(@class,'flex-wrap info')]//button/span"))) {
					break;
				}
			}
		}

		if (!searchedProduct.equals("Community")) {
			List<WebElement> communityMapLinks = Element.getListOfElements(testConfig, How.xPath,
					".//h3[contains(text(),'" + communityName + "')]//ancestor::div[contains(@class,'flex-wrap info')]//button/span");
			if (isClickable(testConfig, communityMapLinks.get(0))) {
				Element.clickThroughJS(testConfig, communityMapLinks.get(0),
						"View Community Map link for Community item");
			} else {
				Element.clickThroughJS(testConfig, communityMapLinks.get(1),
						"View Community Map link for Community item");
			}
		}

		Browser.wait(testConfig, 5);
		return this;
	}

	public boolean isClickable(Config testConfig, WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(testConfig.driver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.elementToBeClickable(element));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public PlanPage navigateToPlanPage() {

		List<WebElement> communityMapLinks = Element.getListOfElements(testConfig, How.xPath,
				".//div[contains(@id,'card')]//button[text()='Community Map']");
		if (isClickable(testConfig, communityMapLinks.get(0))) {
			Element.clickThroughJS(testConfig, communityMapLinks.get(0), "View Community Map link for Community item");
		} else {
			Element.clickThroughJS(testConfig, communityMapLinks.get(1), "View Community Map link for Community item");
		}
		Browser.wait(testConfig, 2);

		List<WebElement> homesCheckbox = Element.getListOfElements(testConfig, How.xPath,
				".//div[contains(text(),'Homes')]/../button");
		if (isClickable(testConfig, homesCheckbox.get(0))) {
			Element.click(testConfig, homesCheckbox.get(0), "Homes Checkbox to get all plans cards");
		} else {
			Element.click(testConfig, homesCheckbox.get(1), "Homes Checkbox to get all plans cards");
		}

		List<WebElement> planCards = Element.getListOfElements(testConfig, How.xPath,
				".//div[contains(@id,'siteplan_card')]//span[contains(text(),'$')]");
		Element.click(testConfig, planCards.get(0), "Plan card to open card in map");
		Browser.wait(testConfig, 2);

		WebElement viewDetails = Element.getPageElement(testConfig, How.xPath, ".//a[text()='View Details']");
		Element.click(testConfig, viewDetails, "View details link to navigate to Plan Page");

		return new PlanPage(testConfig);
	}

	public void verifyFYHPageSearchedLocation(String location) {

		if(location.contains(", CA")) {
			location = location.replace(", CA", ", California");
		}

		Browser.wait(testConfig, 5);
		WebElement searchBar = Element.getPageElement(testConfig, How.xPath, ".//input[contains(@class,'fyh-input')][@aria-labelledby='Search']");
		Helper.compareContains(testConfig, "Searched location", location, searchBar.getAttribute("value"));

	}

	public void verifyNeighborhoodDisplayingSelected(String neighborhoodName) {

		Browser.wait(testConfig, 5);
		String learnMoreTextLoc = "a.primary-underlined";
		WebElement learnMoreText = Element.getPageElement(testConfig, How.css, learnMoreTextLoc);

		Helper.compareContains(testConfig, "Learn More text", neighborhoodName, learnMoreText.getText());

	}

	public void verifyCorrectPlanQMIIsHighlighted(String planQMIName, String expectedBorderColor, String productType) {

		Browser.wait(testConfig, 5);

		String nameLocator = "", highlightedPlanQMIName = "";
		if(productType.equals("Plan")) {
			nameLocator = "(.//div[contains(@class,'card-title')])[1]/div/span[2]";
			List<WebElement> cards = Element.getListOfElements(testConfig, How.xPath, nameLocator);
			highlightedPlanQMIName = cards.get(0).getAttribute("innerText");		
		} else {
			nameLocator = "(.//*[contains(@class,'card-container')])[1]//div[contains(@class,'card-titles')]/span[1]";
			List<WebElement> data = Element.getListOfElements(testConfig, How.xPath, nameLocator);
			//String qmiName = data.get(0).getAttribute("innerText").trim();
			String qmiFullName = data.get(0).getAttribute("innerText").trim();
			//highlightedPlanQMIName = qmiFullName.split(",")[1].trim().concat(",").concat(qmiFullName.split(",")[2]);
			/*String firstQMIName = qmiName.split(",")[0];
			String cityName = "San Marcos";
			highlightedPlanQMIName += data.get(0).getText() + " " + firstQMIName.replace(cityName, "").trim();*/
			highlightedPlanQMIName = qmiFullName;
		}

		Helper.compareEquals(testConfig, "Redirection being done to correct plan/qmi", planQMIName.trim(), highlightedPlanQMIName.trim());

		Browser.wait(testConfig, 2);
		String cardLocator = "(.//*[contains(@class,'card-container')])[1]";
		WebElement highlighedCard = Element.getPageElement(testConfig, How.xPath, cardLocator);

		String cssValue = highlighedCard.getCssValue("border");
		String rgbVal = cssValue.substring(cssValue.indexOf("rgb"));
		String hexcolor = Color.fromString(rgbVal).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color tab", expectedBorderColor.toUpperCase(),
				hexcolor.toUpperCase());

	}

	public void verifyPlanNameOnCardFlyout(String planName) {
		String planNameLocator = ".//span[@class='text-brp-orange-100']/../span[2]";
		WebElement plans = Element.getPageElement(testConfig, How.xPath, planNameLocator);

		String flyoutPlanName = plans.getAttribute("innerText");
		Helper.compareEquals(testConfig, "Name on card flyout displaying", planName, flyoutPlanName);
	}


	public void verifyQMINameOnCardFlyout(String qmiName) {

		String finalVal = "";
		String[] array = qmiName.split(",");
		if(array.length == 2) {
			finalVal = array[0].concat(",").concat(array[1]);
		} else {
			finalVal = array[0];
		}

		String qmiNameLocator = "(.//div[contains(@class,'w-full h-full')]//div[contains(@class,'type-brds-v2-lg-700')])/span[1]";
		WebElement qmi = Element.getPageElement(testConfig, How.xPath, qmiNameLocator);

		String flyoutQMIName = Element.getText(testConfig, qmi, "QMI Name over map card flyout");
		Helper.compareEquals(testConfig, "Name on card flyout displaying", finalVal.trim(), flyoutQMIName.trim());
	}

	public PlanPage navigateToRequiredTypePlan(PlanTypes planTypes, String expectedStatus) {

		checkForPromo();

		String plansLocator = ".//*[text()='floor plans']/parent::button";
		WebElement plansTab = Element.getPageElement(testConfig, How.xPath, plansLocator);
		Element.click(testConfig, plansTab, "Plans tab");
		Browser.wait(testConfig, 2);

		switch (planTypes) {

		case MyTimeEnabled:
			String myTimeEnabledLoc = "(.//div[contains(@class,'tour-the-model')])[1]";
			WebElement myTimeEnabledPlan = Element.getPageElement(testConfig, How.xPath, myTimeEnabledLoc);
			Element.click(testConfig, myTimeEnabledPlan, "my Time Enabled plan");
			Browser.wait(testConfig, 1);
			break;

		case StatusSoldOut:
			Browser.waitWithoutLogging(testConfig, 10);
			Helper.scrollOnLazyLoadingFYHCommResultView(testConfig, true);
			String statusSoldOutLoc = "(.//div[contains(@class,'car-text-brds-v1-grayscale-blue-medium-ft')]/div/div[text()='Sold Out'])[1]";
			WebElement statusSoldOutPlan = Element.getPageElement(testConfig, How.xPath, statusSoldOutLoc);
			String actualPlanCardStatus = statusSoldOutPlan.getText().trim();
			Helper.compareEquals(testConfig, "Status for the Plan Card", expectedStatus, actualPlanCardStatus);
			Browser.wait(testConfig, 3);
			Element.clickThroughJS(testConfig, statusSoldOutPlan, "Status Sold Out plan");
			Browser.wait(testConfig, 1);
			break;

		case ComingSoon:
			Browser.waitWithoutLogging(testConfig, 10);
			Helper.scrollOnLazyLoadingFYHCommResultView(testConfig, true);
			String tempSoldOutLoc = "(.//div[contains(@class,'car-text-brds-v1-grayscale-blue-medium-ft')]/div/div[text()='Coming Soon'])[1]";
			WebElement tempSoldOutPlan = Element.getPageElement(testConfig, How.xPath, tempSoldOutLoc);
			String actualPlanStatus = tempSoldOutPlan.getText().trim();
			Helper.compareEquals(testConfig, "Status for the Plan Card", expectedStatus, actualPlanStatus);
			Browser.wait(testConfig, 3);
			Element.click(testConfig, tempSoldOutPlan, "Coming Soon plan");
			Browser.wait(testConfig, 1);
			break;

		case ValidPricing:
			String pricingCardLoc = "(.//div[contains(@class,'car-text-brds-v1-grayscale-blue-medium-ft')][contains(text(),'Priced from')])[1]";
			String priceValueLoc = "(.//div[contains(@class,'car-text-brds-v1-grayscale-blue-medium-ft')][contains(text(),'Priced from')])[1]/../div[2]";
			WebElement pricingCard = Element.getPageElement(testConfig, How.xPath, pricingCardLoc);
			WebElement priceValue = Element.getPageElement(testConfig, How.xPath, priceValueLoc);
			String actualPlanPrice = pricingCard.getText().trim() + " " + priceValue.getText();
			testConfig.putRunTimeProperty("PlanPricing", actualPlanPrice);
			Helper.compareContains(testConfig, "Pricing for the Plan Card", expectedStatus, actualPlanPrice);
			Element.click(testConfig, pricingCard, "Pricing plan card");
			Browser.wait(testConfig, 1);
			break;
		}

		String viewDetailsLoc = ".//div[contains(@class,'w-full h-full')]//a[contains(@class,'btn secondary')]";
		WebElement viewDetailBtn = Element.getPageElement(testConfig, How.xPath, viewDetailsLoc);
		Element.clickThroughJS(testConfig, viewDetailBtn, "View Details button over Plan card");

		Set<String> windowHandle = testConfig.driver.getWindowHandles();
		testConfig.driver.switchTo().window(getLastElement(windowHandle));

		testConfig.logComment("Switching to the new window opened...");

		return new PlanPage(testConfig);
	}

	public void verifyCommunitiesTextForLocation(String locationText) {

		Helper.compareContains(testConfig, "Communities text before results", locationText, confirmationText.getText());
		testConfig.putRunTimeProperty("InitialCommCount", confirmationText.getText());

		String cardsLocator = ".//div[contains(@class,'item-container')]";
		List<WebElement> totalCards = Element.getListOfElements(testConfig, How.xPath, cardsLocator);
		testConfig.logComment("Total cards displaying initially as " + totalCards.size());
	}

	public void verifyFilterValues(FilterTypes pricerange) {

		String locator = "", minPriceLoc = "", maxPriceLoc = "", minSqFtLoc = "", maxSqFtLoc = "";
		WebElement filterBtn = null, minPriceSection = null, maxPriceSection = null, minSqFtSection = null, maxSqFtSection = null;
		List<WebElement> minPriceRange = null, maxPriceRange = null, minSqFtRange = null, maxSqFtRange = null;
		switch (pricerange) {
		case PriceRange:
			locator = ".//button//span[text()='Price Range']";
			filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
			Element.click(testConfig, filterBtn, "Price range filter");
			Browser.wait(testConfig, 2);
			minPriceLoc = ".//div[contains(@class,'panel')]//label[text()='Min:']//parent::div/div/button";
			minPriceSection = Element.getPageElement(testConfig, How.xPath, minPriceLoc);
			Element.click(testConfig, minPriceSection, "Min Price dropdown");
			String minPriceValues = ".//label[text()='Min:']/parent::div/div/div/ul/li";
			minPriceRange = Element.getListOfElements(testConfig, How.xPath, minPriceValues);
			if(minPriceRange.size() > 0) {
				testConfig.logPass("Getting total min price range values as " + minPriceRange.size());
				testConfig.logComment("The values are: ");
				for (int i = 0; i < minPriceRange.size(); i++) {
					testConfig.logComment(minPriceRange.get(i).getText());
				}
			} else {
				testConfig.logFail("Not getting min price range values displaying in the dropdown...");
			}

			Browser.wait(testConfig, 2);
			maxPriceLoc = ".//div[contains(@class,'panel')]//label[text()='Max:']//parent::div/div/button";
			maxPriceSection = Element.getPageElement(testConfig, How.xPath, maxPriceLoc);
			Element.click(testConfig, maxPriceSection, "Max Price dropdown");
			String maxPriceValues = ".//label[text()='Max:']/parent::div/div/div/ul/li";
			maxPriceRange = Element.getListOfElements(testConfig, How.xPath, maxPriceValues);
			if(maxPriceRange.size() > 0) {
				testConfig.logPass("Getting total max price range values as " + maxPriceRange.size());
				testConfig.logComment("The values are: ");
				for (int i = 0; i < maxPriceRange.size(); i++) {
					testConfig.logComment(maxPriceRange.get(i).getText());
				}
			} else {
				testConfig.logFail("Not getting max price range values displaying in the dropdown...");
			}
			Element.click(testConfig, maxPriceSection, "Max Price dropdown");
			break;

		case HomeType:
			locator = ".//button//span[text()='Home Type']";
			filterBtn = Element.getPageElement(testConfig, How.xPath, locator);
			Element.click(testConfig, filterBtn, "Home type filter button");
			String homeTypesLocator = ".//div[contains(@class,'panel')]//h4[text()='Home Type']//ancestor::fieldset/div/button";
			List<WebElement> allHomeTypes = Element.getListOfElements(testConfig, How.xPath, homeTypesLocator);
			if(allHomeTypes.size() > 0) {
				testConfig.logPass("Getting total Home Types values as " + allHomeTypes.size());
				testConfig.logComment("The values are: ");
				for (int i = 0; i < allHomeTypes.size(); i++) {
					testConfig.logComment(allHomeTypes.get(i).getText());
				}
			} else {
				testConfig.logFail("Not getting Home Types values displaying in the dropdown...");
			}
			break;

		case BedsAndBaths:
			locator = ".//button//span[text()='Beds & Baths']";
			filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
			Element.click(testConfig, filterBtn, "Beds & Baths range filter");
			Browser.wait(testConfig, 2);

			String bedPlusLocator = ".//button[@data-testid='plus-button-beds']";
			WebElement bedPlus = Element.getPageElement(testConfig, How.xPath, bedPlusLocator);

			String bedLabelLocator = ".//span[@data-testid='label-beds']";
			WebElement bedLabel = Element.getPageElement(testConfig, How.xPath, bedLabelLocator);
			testConfig.logComment("Current bed label value is " + bedLabel.getText());

			while(!bedPlus.getAttribute("class").contains("disabled")) {
				Element.click(testConfig, bedPlus, "Incrementing bed count by 1 as button is enabled");
				Browser.waitWithoutLogging(testConfig, 1);
				bedPlus = Element.getPageElement(testConfig, How.xPath, bedPlusLocator);
				bedLabel = Element.getPageElement(testConfig, How.xPath, bedLabelLocator);
				testConfig.logComment("Current bed label value is " + bedLabel.getText());
			}

			String bathPlusLocator = ".//button[@data-testid='plus-button-baths']";
			WebElement bathPlus = Element.getPageElement(testConfig, How.xPath, bathPlusLocator);

			String bathLabelLocator = ".//span[@data-testid='label-baths']";
			WebElement bathLabel = Element.getPageElement(testConfig, How.xPath, bathLabelLocator);
			testConfig.logComment("Current bath label value is " + bathLabel.getText());

			while(!bathPlus.getAttribute("class").contains("disabled")) {
				Element.click(testConfig, bathPlus, "Increment bath count by 1 as button is enabled");
				Browser.waitWithoutLogging(testConfig, 1);
				bathLabel = Element.getPageElement(testConfig, How.xPath, bathLabelLocator);
				bathPlus = Element.getPageElement(testConfig, How.xPath, bathPlusLocator);
				testConfig.logComment("Current bath label value is " + bathLabel.getText());
			}

			break;

		case SquareFootage:
			locator = ".//button//div[contains(text(),'More Filters')]";
			filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
			Element.click(testConfig, filterBtn, "More filter");
			Browser.wait(testConfig, 2);
			minSqFtLoc = ".//div[contains(@class,'panel')]//label[text()='Min Square Feet']//parent::div/div/button";
			minSqFtSection = Element.getPageElement(testConfig, How.xPath, minSqFtLoc);
			Element.click(testConfig, minSqFtSection, "Min Sq Ft dropdown");
			String minSquareFootageValues = ".//label[text()='Min Square Feet']/parent::div/div/div/ul/li";
			minSqFtRange = Element.getListOfElements(testConfig, How.xPath, minSquareFootageValues);
			if(minSqFtRange.size() > 0) {
				testConfig.logPass("Getting total min square footage range values as " + minSqFtRange.size());
				testConfig.logComment("The values are: ");
				for (int i = 0; i < minSqFtRange.size(); i++) {
					testConfig.logComment(minSqFtRange.get(i).getText());
				}
			} else {
				testConfig.logFail("Not getting min square footage range values displaying in the dropdown...");
			}

			Browser.wait(testConfig, 2);
			maxSqFtLoc = ".//div[contains(@class,'panel')]//label[text()='Max Square Feet']//parent::div/div/button";
			maxSqFtSection = Element.getPageElement(testConfig, How.xPath, maxSqFtLoc);
			Element.click(testConfig, maxSqFtSection, "Max Price dropdown");
			String maxSqFtValues = ".//label[text()='Max Square Feet']/parent::div/div/div/ul/li";
			maxSqFtRange = Element.getListOfElements(testConfig, How.xPath, maxSqFtValues);
			if(maxSqFtRange.size() > 0) {
				testConfig.logPass("Getting total max square footage range values as " + maxSqFtRange.size());
				testConfig.logComment("The values are: ");
				for (int i = 0; i < maxSqFtRange.size(); i++) {
					testConfig.logComment(maxSqFtRange.get(i).getText());
				}
			} else {
				testConfig.logFail("Not getting max square footage range values displaying in the dropdown...");
			}
			Element.click(testConfig, filterBtn, "More filter");
			break;

		case GarageParking:
			locator = ".//button//div[contains(text(),'More Filters')]";
			filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
			Element.click(testConfig, filterBtn, "More filter");
			Browser.wait(testConfig, 2);

			String garagePlusLocator = ".//button[@data-testid='plus-button-garage/parking']";
			WebElement garagePlus = Element.getPageElement(testConfig, How.xPath, garagePlusLocator);

			String garageLabelLocator = ".//span[@data-testid='label-garage/parking']";
			WebElement garageLabel = Element.getPageElement(testConfig, How.xPath, garageLabelLocator);
			testConfig.logComment("Current garage label value is " + garageLabel.getText());

			while(!garagePlus.getAttribute("class").contains("disabled")) {
				Element.click(testConfig, garagePlus, "Incrementing garage count by 1 as button is enabled");
				Browser.waitWithoutLogging(testConfig, 1);
				garageLabel = Element.getPageElement(testConfig, How.xPath, garageLabelLocator);
				garagePlus = Element.getPageElement(testConfig, How.xPath, garagePlusLocator);
				testConfig.logComment("Current garage label value is " + garageLabel.getText());
			}

			Element.click(testConfig, filterBtn, "More filter");
			break;

		case Stories:
			locator = ".//button//div[contains(text(),'More Filters')]";
			filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
			Element.click(testConfig, filterBtn, "More filter");
			Browser.wait(testConfig, 2);

			String storiesPlusLocator = ".//button[@data-testid='plus-button-garage/parking']";
			WebElement storiesPlus = Element.getPageElement(testConfig, How.xPath, storiesPlusLocator);

			String storiesLabelLocator = ".//span[@data-testid='label-garage/parking']";
			WebElement storiesLabel = Element.getPageElement(testConfig, How.xPath, storiesLabelLocator);
			testConfig.logComment("Current stories label value is " + storiesLabel.getText());

			while(!storiesPlus.getAttribute("class").contains("disabled")) {
				Element.click(testConfig, storiesPlus, "Incrementing stories count by 1 as button is enabled");
				Browser.waitWithoutLogging(testConfig, 1);
				storiesLabel = Element.getPageElement(testConfig, How.xPath, storiesLabelLocator);
				storiesPlus = Element.getPageElement(testConfig, How.xPath, storiesPlusLocator);
				testConfig.logComment("Current stories label value is " + storiesLabel.getText());
			}
			Element.click(testConfig, filterBtn, "More filter");
			break;

		case BuildStatus:
			locator = ".//button//div[contains(text(),'More Filters')]";
			filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
			Element.click(testConfig, filterBtn, "More filter");
			Browser.wait(testConfig, 2);

			locator = ".//button//span[text()='Home Type']";
			filterBtn = Element.getPageElement(testConfig, How.xPath, locator);
			Element.click(testConfig, filterBtn, "Home type filter button");

			String buildStatusLocator = ".//span[text()='Availability']/parent::div/fieldset//button[not(contains(@class,'disabled'))]/div";
			List<WebElement> allBuildStatus = Element.getListOfElements(testConfig, How.xPath, buildStatusLocator);
			if(allBuildStatus.size() > 0) {
				testConfig.logPass("Getting total Build Status values as " + allBuildStatus.size());
			} else {
				testConfig.logFail("Not getting Build Status values displaying");
			}
			break;
		}
	}

	public void applyFilterRangeAndVerifyResultsUpdation(String locationText) {

		applyPriceFilter();

		WebElement doneBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'panel')]//button[contains(@class,'btn dark')]");
		Element.click(testConfig, doneBtn, "Done button to apply the filter");
		Browser.wait(testConfig, 3);

		Helper.compareContains(testConfig, "Communities text after applying filters results", locationText, confirmationText.getText());
		testConfig.putRunTimeProperty("FinalCommCount", confirmationText.getText());

		String cardsLocator = ".//div[contains(@class,'item-container')]";
		List<WebElement> totalCards = Element.getListOfElements(testConfig, How.xPath, cardsLocator);
		testConfig.logComment("Total cards displaying after applying filter as " + totalCards.size());
	}

	public void applyPriceFilter() {

		Browser.wait(testConfig, 3);
		String locator = ".//button//span[text()='Price Range']";
		WebElement filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
		Element.click(testConfig, filterBtn, "Price range filter");
		Browser.wait(testConfig, 2);

		String minPriceLoc = ".//div[contains(@class,'panel')]//label[text()='Min:']//parent::div/div/button";
		WebElement minPriceSection = Element.getPageElement(testConfig, How.xPath, minPriceLoc);
		Element.click(testConfig, minPriceSection, "Min Price dropdown");
		String minPriceValues = ".//label[text()='Min:']/parent::div/div/div/ul/li";
		List<WebElement> minPriceRange = Element.getListOfElements(testConfig, How.xPath, minPriceValues);
		if(minPriceRange.size() > 0) {
			testConfig.logPass("Getting total min price range values as " + minPriceRange.size());
			testConfig.logComment("The values are: ");
			for (int i = 0; i < minPriceRange.size(); i++) {
				if(i == 6) {
					String minValue = minPriceRange.get(i).getText();
					Element.click(testConfig, minPriceRange.get(i), minValue + " option");
					testConfig.putRunTimeProperty("PriceMinValue", minValue.replaceAll("[$,]", ""));
					break;
				}
			}
		} else {
			testConfig.logFail("Not getting min price range values displaying in the dropdown...");
		}

		Browser.wait(testConfig, 2);
		String maxPriceLoc = ".//div[contains(@class,'panel')]//label[text()='Max:']//parent::div/div/button";
		WebElement maxPriceSection = Element.getPageElement(testConfig, How.xPath, maxPriceLoc);
		Element.click(testConfig, maxPriceSection, "Max Price dropdown");
		String maxPriceValues = ".//label[text()='Max:']/parent::div/div/div/ul/li";
		List<WebElement> maxPriceRange = Element.getListOfElements(testConfig, How.xPath, maxPriceValues);
		if (maxPriceRange.size() > 0) {
			testConfig.logPass("Getting total max price range values as " + maxPriceRange.size());
			testConfig.logComment("The values are: ");
			for (int i = 0; i < maxPriceRange.size(); i++) {
				if (i == 6) {
					String maxValue = maxPriceRange.get(i).getText();
					Element.click(testConfig, maxPriceRange.get(i), maxValue + " option");
					testConfig.putRunTimeProperty("PriceMaxValue", maxValue.replaceAll("[$,]", ""));
					break;
				}
			}
		} else {
			testConfig.logFail("Not getting max price range values displaying in the dropdown...");
		}

	}

	public void verifyResultsUpdatedCorrectly() {

		String initialCommCount = testConfig.getRunTimeProperty("InitialCommCount");
		String finalCommCount = testConfig.getRunTimeProperty("FinalCommCount");

		if(initialCommCount.equals(finalCommCount)) {
			testConfig.logFail("Results does not updated after applying price filter");
		} else {
			testConfig.logPass("Results updated correctly after applying price filter from '" + initialCommCount + "' to '" + finalCommCount + "'");
		}
	}

	public void applyHomeTypesFilterAndVerifyResultsUpdation(String locationText, String expectedColor) {

		applyHomeTypeFilter(expectedColor);

		Browser.wait(testConfig, 3);
		WebElement doneBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'panel')]//button[contains(@class,'btn dark')]");
		Element.click(testConfig, doneBtn, "Done button to apply the filter");

		Helper.compareContains(testConfig, "Communities text after applying filters results", locationText, confirmationText.getText());
		testConfig.putRunTimeProperty("FinalCommCount", confirmationText.getText());

		String cardsLocator = ".//div[contains(@class,'item-container')]";
		List<WebElement> totalCards = Element.getListOfElements(testConfig, How.xPath, cardsLocator);
		testConfig.logComment("Total cards displaying after applying filter as " + totalCards.size());
	}

	private void applyHomeTypeFilter(String expectedBorderColor) {

		String locator = ".//button//span[text()='Home Type']";
		WebElement filterBtn = Element.getPageElement(testConfig, How.xPath, locator);
		Element.click(testConfig, filterBtn, "Home type filter button");

		Browser.wait(testConfig, 1);

		String homeTypesLocator = ".//div[contains(@class,'panel')]//h4[text()='Home Type']//ancestor::fieldset/div/button[not(contains(@class,'disabled'))]";
		List<WebElement> allHomeTypes = Element.getListOfElements(testConfig, How.xPath, homeTypesLocator);
		if(allHomeTypes.size() > 0) {
			testConfig.logPass("Getting total Home Types values as " + allHomeTypes.size());
			testConfig.logComment("The values are: ");
			for (int i = 0; i < allHomeTypes.size(); i++) {
				if(allHomeTypes.get(i).isEnabled()) {
					Element.click(testConfig, allHomeTypes.get(i), allHomeTypes.get(i).getText() + " option");
					Browser.wait(testConfig, 2);
					String cssValue = allHomeTypes.get(i).getCssValue("border-color");
					String hexcolor = Color.fromString(cssValue).asHex();
					Helper.compareEquals(testConfig, "Border color for " + allHomeTypes.get(i).getText() + " tab", expectedBorderColor, hexcolor);
					break;
				}
			}
		} else {
			testConfig.logFail("Not getting Home Types values displaying in the dropdown...");
		}
	}

	public void applyBedBathFilterAndVerifyResultsUpdation(String locationText) {

		applyBedBathFilter();

		Browser.wait(testConfig, 3);
		WebElement doneBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'panel')]//button[contains(@class,'btn dark')]");
		Element.clickThroughJS(testConfig, doneBtn, "Done button to apply Bed Bath filter");

		Helper.compareContains(testConfig, "Communities text after applying filters results", locationText, confirmationText.getText());
		testConfig.putRunTimeProperty("FinalCommCount", confirmationText.getText());

		String cardsLocator = ".//div[contains(@class,'item-container')]";
		List<WebElement> totalCards = Element.getListOfElements(testConfig, How.xPath, cardsLocator);
		testConfig.logComment("Total cards displaying after applying filter as " + totalCards.size());

	}

	private void applyBedBathFilter() {

		String locator = ".//button//span[text()='Beds & Baths']";
		WebElement filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
		Element.click(testConfig, filterBtn, "Beds & Baths range filter");
		Browser.wait(testConfig, 2);

		String bedPlusLocator = ".//button[@data-testid='plus-button-beds']";
		WebElement bedPlus = Element.getPageElement(testConfig, How.xPath, bedPlusLocator);

		String bedLabelLocator = ".//span[@data-testid='label-beds']";
		WebElement bedLabel = Element.getPageElement(testConfig, How.xPath, bedLabelLocator);

		while(!bedLabel.getText().equals("4+")) {
			testConfig.logComment("Current bed label value is " + bedLabel.getText() + " so incrementing the same to make it 4+");
			Element.click(testConfig, bedPlus, "Incrementing bed count by 1");
			Browser.waitWithoutLogging(testConfig, 1);
			bedLabel = Element.getPageElement(testConfig, How.xPath, bedLabelLocator);
		}

		String bathPlusLocator = ".//button[@data-testid='plus-button-baths']";
		WebElement bathPlus = Element.getPageElement(testConfig, How.xPath, bathPlusLocator);

		String bathLabelLocator = ".//span[@data-testid='label-baths']";
		WebElement bathLabel = Element.getPageElement(testConfig, How.xPath, bathLabelLocator);

		while(!bathLabel.getText().equals("4+")) {
			testConfig.logComment("Current bath label value is " + bathLabel.getText() + " so incrementing the same to make it 4+");
			Element.click(testConfig, bathPlus, "Increment bath count by 1");
			Browser.waitWithoutLogging(testConfig, 1);
			bathLabel = Element.getPageElement(testConfig, How.xPath, bathLabelLocator);
		}
	}

	public void verifySearchHistoryDisplaySearchLocation(String location) {

		Browser.wait(testConfig, 5);
		WebElement searchInput = Element.getPageElement(testConfig, How.xPath, ".//input[contains(@class,'fyh-input')][@aria-labelledby='Search']");
		Element.clickThroughJS(testConfig, searchInput, "Search input field");
		Browser.wait(testConfig, 2);

		WebElement clearSearch = Element.getPageElement(testConfig, How.xPath, ".//button[@aria-labelledby='ClearSearch']");
		Element.click(testConfig, clearSearch, "Clear search input field");
		Browser.wait(testConfig, 2);

		WebElement searchHistory = Element.getPageElement(testConfig, How.xPath, ".//div[contains(text(),'Recent Searches')]/parent::div//div[contains(@class,'srch-items-center')]");
		Helper.compareContains(testConfig, "Location present under Recent Searches", location, searchHistory.getText());

	}

	public AboutPage navigateToAboutPage() {

		WebElement menuBar = Element.getPageElement(testConfig, How.xPath, ".//button[contains(@class,'hover:nav-bg-brds-v1-grayscale')]");
		Element.click(testConfig, menuBar, "Header top menu");
		Browser.wait(testConfig, 2);

		WebElement contactUs = Element.getPageElement(testConfig, How.xPath, ".//h3[text()='Company']/..//a[@aria-label='About']");
		Element.click(testConfig, contactUs, "About link from Header");
		Helper.removeCookies(testConfig);
		testConfig.putRunTimeProperty("RedirectionValue", "yes");
		return new AboutPage(testConfig);
	}

	public void applySquareFootageFilterAndVerifyResultsUpdation(String locationText) {

		applySquareFootageFilter();

		Browser.wait(testConfig, 3);
		WebElement doneBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'panel')]//button[contains(@class,'btn dark')]");
		Element.click(testConfig, doneBtn, "Done button to apply the filter");

		Helper.compareContains(testConfig, "Communities text after applying filters results", locationText, confirmationText.getText());
		testConfig.putRunTimeProperty("FinalCommCount", confirmationText.getText());

		String cardsLocator = ".//div[contains(@class,'item-container')]";
		List<WebElement> totalCards = Element.getListOfElements(testConfig, How.xPath, cardsLocator);
		testConfig.logComment("Total cards displaying after applying filter as " + totalCards.size());
	}

	public void applySquareFootageFilter() {

		String locator = "", minSqFtLoc = "", maxSqFtLoc = "";
		WebElement filterBtn = null, minSqFtSection = null, maxSqFtSection = null;
		List<WebElement> minSqFtRange = null, maxSqFtRange = null;

		locator = ".//button//div[contains(text(),'More Filters')]";
		filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
		Element.click(testConfig, filterBtn, "More filter");
		Browser.wait(testConfig, 4);

		minSqFtLoc = ".//div[contains(@class,'panel')]//label[text()='Min Square Feet']//parent::div/div/button";
		minSqFtSection = Element.getPageElement(testConfig, How.xPath, minSqFtLoc);
		Element.click(testConfig, minSqFtSection, "Min Sq Ft dropdown");
		String minSquareFootageValues = ".//label[text()='Min Square Feet']/parent::div/div/div/ul/li";
		minSqFtRange = Element.getListOfElements(testConfig, How.xPath, minSquareFootageValues);
		if(minSqFtRange.size() > 0) {
			testConfig.logPass("Getting total min square footage range values as " + minSqFtRange.size());
			testConfig.logComment("The values are: ");
			for (int i = 0; i < minSqFtRange.size(); i++) {
				if (i == 2) {
					String minSqFt = minSqFtRange.get(i).getText();
					Element.click(testConfig, minSqFtRange.get(i), minSqFt + " option");
					testConfig.putRunTimeProperty("SquareFootageMinValue", minSqFt.replaceAll("[$, sqft]", ""));
					break;
				}
			}
		} else {
			testConfig.logFail("Not getting min square footage range values displaying in the dropdown...");
		}

		Browser.wait(testConfig, 4);
		maxSqFtLoc = ".//div[contains(@class,'panel')]//label[text()='Max Square Feet']//parent::div/div/button";
		maxSqFtSection = Element.getPageElement(testConfig, How.xPath, maxSqFtLoc);
		Element.click(testConfig, maxSqFtSection, "Max Price dropdown");
		String maxSqFtValues = ".//label[text()='Max Square Feet']/parent::div/div/div/ul/li";
		maxSqFtRange = Element.getListOfElements(testConfig, How.xPath, maxSqFtValues);
		if(maxSqFtRange.size() > 0) {
			testConfig.logPass("Getting total max square footage range values as " + maxSqFtRange.size());
			testConfig.logComment("The values are: ");
			for (int i = 0; i < maxSqFtRange.size(); i++) {
				if (i == 1) {
					String maxSqFt = maxSqFtRange.get(i).getText();
					Element.click(testConfig, maxSqFtRange.get(i), maxSqFt + " option");
					testConfig.putRunTimeProperty("SquareFootageMaxValue", maxSqFt.replaceAll("[$, sqft]", ""));
					break;
				}
			}
		} else {
			testConfig.logFail("Not getting max square footage range values displaying in the dropdown...");
		}
	}

	public void applyBuildStatusAndVerifyResultsUpdation(String locationText) {

		String locator = ".//button//div[contains(text(),'More Filters')]";
		WebElement filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
		Element.click(testConfig, filterBtn, "More filter");
		Browser.wait(testConfig, 2);

		String buildStatusLocator = ".//span[text()='Availability']/parent::div/fieldset//button[not(contains(@class,'disabled'))]/div";
		List<WebElement> allBuildStatus = Element.getListOfElements(testConfig, How.xPath, buildStatusLocator);
		if(allBuildStatus.size() > 0) {
			testConfig.logPass("Getting total Build Status values as " + allBuildStatus.size());
			testConfig.logComment("The values are: ");
			for (int i = 0; i < allBuildStatus.size(); i++) {
				testConfig.logComment(allBuildStatus.get(i).getText());
				if (i == (allBuildStatus.size() / 2)) {
					Element.click(testConfig, allBuildStatus.get(i), allBuildStatus.get(i).getText() + " option");
					break;
				}
			}
		} else {
			testConfig.logFail("Not getting Build Status values displaying");
		}

		Browser.wait(testConfig, 3);
		WebElement doneBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'panel')]//button[contains(@class,'btn dark')]");
		Element.click(testConfig, doneBtn, "Done button to apply the filter");

		Helper.compareContains(testConfig, "Communities text after applying filters results", locationText, confirmationText.getText());
		testConfig.putRunTimeProperty("FinalCommCount", confirmationText.getText());

		String cardsLocator = ".//div[contains(@class,'item-container')]";
		List<WebElement> totalCards = Element.getListOfElements(testConfig, How.xPath, cardsLocator);
		testConfig.logComment("Total cards displaying after applying filter as " + totalCards.size());

	}

	public void applySortOptionAndVerifyResult(SortOption sortOption) {

		Browser.waitWithoutLogging(testConfig, 2);
		checkForPromo();

		int x = 0;
		Browser.wait(testConfig, 4);
		String sortValue = "";
		ArrayList<Long> obtainedList = new ArrayList<>(); 
		String btnLocator = "(.//span[contains(text(),'Sort:')]/../button)[1]";

		WebDriverWait wait = new WebDriverWait(testConfig.driver, Duration.ofSeconds(15)); 
		WebElement sortByBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(btnLocator)));
		Element.click(testConfig, sortByBtn, "Sort by section");
		List<WebElement> allOptions = Element.getListOfElements(testConfig, How.xPath, ".//div[contains(@class,'dropdown-content')]/li");

		switch (sortOption) {
		case PriceHighToLow:
			sortValue = "Price high to low";
			break;

		case PriceLowToHigh:
			sortValue = "Price low to high";
			break;

		default:
			break;
		}

		for (int i = 0; i < allOptions.size(); i++) {
			String value = allOptions.get(i).getText();
			if (value.equalsIgnoreCase(sortValue)) {
				x++;
				Element.click(testConfig, allOptions.get(i), value + " sort option");
				Browser.wait(testConfig, 8);
				break;
			}

			if (x > 0) {
				testConfig.logFail("No such option available for sort");
			}
		}

		List<WebElement> allPrices = Element.getListOfElements(testConfig, How.xPath, ".//div[contains(@class,'car-items-end')]/div[contains(@class,'700')]//div[contains(text(),'$')]");
		obtainedList = prepareListForPriceSortingVerification(allPrices, "", obtainedList);

		switch (sortOption) {
		case PriceHighToLow:
			isPriceListSortedOrNot(obtainedList, true);
			break;

		case PriceLowToHigh:
			isPriceListSortedOrNot(obtainedList, false);
			break;

		default:
			break;
		}
	}

	/**
	 * Method to prepare the data in case of price by removing $, comma(,) from the string
	 * @param elementList
	 * @param priceValue
	 * @param obtainedList
	 * @return 
	 */
	public ArrayList<Long> prepareListForPriceSortingVerification(List<WebElement> elementList, String priceValue, ArrayList<Long> obtainedList){

		for (int i = 0; i < elementList.size(); i++) {
			WebElement priceValueEle = elementList.get(i);
			if(priceValueEle.getText().contains("$"))
			{
				String price = priceValueEle.getText().replace(",", "").replace("$", "").trim();
				obtainedList.add(Long.parseLong(price));
			}
		}
		return obtainedList;
	}

	public void applyHomeSizeSortOptionAndVerifyResult(SortOption sortOption) {

		checkForPromo();

		int x = 0;
		Browser.wait(testConfig, 4);
		String sortValue = "";
		ArrayList<Integer> obtainedList = new ArrayList<>(); 
		String btnLocator = "(.//span[contains(text(),'Sort:')]/../button)[1]";

		WebDriverWait wait = new WebDriverWait(testConfig.driver, Duration.ofSeconds(15)); 
		WebElement sortByBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(btnLocator)));
		Element.click(testConfig, sortByBtn, "Sort by section");
		List<WebElement> allOptions = Element.getListOfElements(testConfig, How.xPath, ".//div[contains(@class,'dropdown-content')]/li");

		switch (sortOption) {
		case HomeSizeLowToHigh:
			sortValue = "Home Size low to high";
			break;

		case HomeSizeHighToLow:
			sortValue = "Home Size high to low";
			break;

		default:
			break;
		}

		for (int i = 0; i < allOptions.size(); i++) {
			String value = allOptions.get(i).getText();
			if (value.equalsIgnoreCase(sortValue)) {
				x++;
				Element.click(testConfig, allOptions.get(i), value + " sort option");
				Browser.wait(testConfig, 10);
				break;
			}

			if (x > 0) {
				testConfig.logFail("No such option available for sort");
			}
		}

		String homeSize = "";
		String locator = ".//div[contains(@class,'car-text-brds')][contains(text(),'Priced')]/ancestor::*[contains(@class,'card-container')]//span[contains(text(),'ft²')]";
		List<WebElement> allHomeSizes = Element.getListOfElements(testConfig, How.xPath, locator);
		for (int i = 0; i < (allHomeSizes.size() / 2); i++) {
			WebElement homeSizeEle = allHomeSizes.get(i);
			if(homeSizeEle.getText().contains("-"))
			{
				homeSize = homeSizeEle.getText().split("-")[1].replace(",", "").replace("ft²", "").trim();
			} else {
				homeSize = homeSizeEle.getText().split(" ")[0].replace(",", "").trim();
			}
			obtainedList.add(Integer.parseInt(homeSize));
		}

		switch (sortOption) {
		case HomeSizeLowToHigh:
			isHomeSizeListSortedOrNot(obtainedList, false);
			break;

		case HomeSizeHighToLow:
			isHomeSizeListSortedOrNot(obtainedList, true);
			break;

		default:
			break;
		}
	}

	/**
	 * Method to verify whether the provided list is sorted or not
	 * @param obtainedList
	 * @param reverse :- true in case of verification needs to be done in case of decreasing list(reverse order verification) else false
	 */
	public void isHomeSizeListSortedOrNot(ArrayList<Integer> obtainedList, Boolean reverse){

		ArrayList<Integer> sortedList = new ArrayList<>();   
		for(Integer s:obtainedList){
			sortedList.add(s);
		}
		Collections.sort(sortedList);

		if(reverse)
			Collections.reverse(sortedList);

		testConfig.logComment("Actual Obtained List from page is : " + Arrays.toString(obtainedList.toArray()));
		testConfig.logComment("Created Sorted List from Obtained list is : " + Arrays.toString(sortedList.toArray()));

		Helper.compareTrue(testConfig, "Sorting is working fine on the page", sortedList.equals(obtainedList));

	}

	public void applyCommNameSortOptionAndVerifyResult(String sortValue, String tab, String areaCode) {

		String valueLocator = ".//h3[contains(@class,'community-name')]";
		String areaLocator = ".//div[contains(@class,'flex-wrap info')]/div/h5";

		if(tab.equals("Homes")) {
			WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]");
			Element.click(testConfig, homesTab, "Homes tab");
			Browser.wait(testConfig, 5);
			valueLocator = ".//div[contains(@class,'card-header')]//span[contains(@class,'car-capitalize')]";
			areaLocator = ".//div[contains(@class,'card-titles')]/span[contains(@class,'card-subtitle')]";

		}

		applySortOption(sortValue);

		List<WebElement> allCommunityNames = Element.getListOfElements(testConfig, How.xPath, valueLocator);

		List<WebElement> cardArea = Element.getListOfElements(testConfig, How.xPath, areaLocator);
		ArrayList<String> obtainedRegionPriceList = new ArrayList<>();
		ArrayList<String> obtainedNearAreaPriceList = new ArrayList<>();

		ArrayList<String> obtainedList = new ArrayList<>();
		for (int i = 0; i < (allCommunityNames.size()); i++) {
			String communityName = allCommunityNames.get(i).getText();
			if (communityName.substring(0, 4).equals("The ")) {
				communityName = communityName.substring(4, communityName.length()).trim();
			}
			obtainedList.add(communityName);
		}

		for (int i = 0; i < cardArea.size(); i++) {
			if (cardArea.get(i).getAttribute("innerText").contains(areaCode)) {
				obtainedRegionPriceList.add(obtainedList.get(i));
			} else {
				obtainedNearAreaPriceList.add(obtainedList.get(i));
			}
		}

		testConfig.logComment("--------------- Starting verification for sorting of Cards for Searched location based on " + sortValue + " ---------------");
		if (sortValue.equals("Community (A-Z)")) {
			isListSortedOrNot(obtainedRegionPriceList, false);
		} else {
			isListSortedOrNot(obtainedRegionPriceList, true);
		}

		testConfig.logComment("--------------- Starting verification for sorting of Cards for Side Areas based on " + sortValue + " ---------------");
		if (sortValue.equals("Community (A-Z)")) {
			isListSortedOrNot(obtainedNearAreaPriceList, false);
		} else {
			isListSortedOrNot(obtainedNearAreaPriceList, true);
		}

	}

	private void applySortOption(String sortValue) {
		int x = 0;
		Browser.wait(testConfig, 4);
		String btnLocator = "(.//span[contains(text(),'Sort:')]/../button)[1]";

		WebDriverWait wait = new WebDriverWait(testConfig.driver, Duration.ofSeconds(10)); 
		WebElement sortByBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(btnLocator)));
		Element.click(testConfig, sortByBtn, "Sort by section");
		List<WebElement> allOptions = Element.getListOfElements(testConfig, How.xPath, ".//div[contains(@class,'dropdown-content')]/li");

		for (int i = 0; i < allOptions.size(); i++) {
			String value = allOptions.get(i).getText();
			if (value.equalsIgnoreCase(sortValue)) {
				x++;
				Element.click(testConfig, allOptions.get(i), value + " sort option");
				Browser.wait(testConfig, 10);
				break;
			}

			if (x > 0) {
				testConfig.logFail("No such option available for sort");
			}
		}
	}

	public void applyPriceSortOptionAndVerifyResult(String sortValue, String areaCode) {

		WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]");
		Element.click(testConfig, homesTab, "Homes tab");
		Browser.wait(testConfig, 5);

		applySortOption(sortValue);

		Helper.scrollOnLazyLoadingFYHWideSearchResultView(testConfig, false);

		//String valueLocator = ".//div[contains(text(),'Priced')]/parent::div/div/span";
		String valueLocator = ".//div[contains(@class,'car-type-brds')][contains(@class,'car-text-brds-v1-grayscale-blue-medium-ft')]/div/div[contains(text(),'$')]";
		List<WebElement> allPrices = Element.getListOfElements(testConfig, How.xPath, valueLocator);

		String areaLocator = ".//div[contains(@class,'car-type-brds')]/div/div[contains(text(),'$')]/ancestor::*[contains(@class,'card-container')]//"
				+ "div[contains(@class,'card-titles')]/span[contains(@class,'card-subtitle')]";

		List<WebElement> cardArea = Element.getListOfElements(testConfig, How.xPath, areaLocator);
		ArrayList<WebElement> obtainedRegionPriceList = new ArrayList<>();
		ArrayList<WebElement> obtainedNearAreaPriceList = new ArrayList<>();

		for (int i = 0; i < cardArea.size(); i++) {
			if (cardArea.get(i).getAttribute("innerText").contains(areaCode)) {
				obtainedRegionPriceList.add(allPrices.get(i));
			} else {
				obtainedNearAreaPriceList.add(allPrices.get(i));
			}
		}

		ArrayList<Long> areaObtainedList = new ArrayList<>();
		prepareListForPriceSortingVerification(obtainedRegionPriceList, "", areaObtainedList);
		testConfig.logComment("--------------- Starting verification for sorting of Cards for Searched location based on " + sortValue + " ---------------");

		switch (sortValue) {
		case "Price high to low":
			isPriceListSortedOrNot(areaObtainedList, true);
			break;

		case "Price low to high":
			isPriceListSortedOrNot(areaObtainedList, false);
			break;
		}

		ArrayList<Long> sideAreasObtainedList = new ArrayList<>(); 
		prepareListForPriceSortingVerification(obtainedNearAreaPriceList, "", sideAreasObtainedList);
		testConfig.logComment("--------------- Starting verification for sorting of Cards for Side Areas based on " + sortValue + " ---------------");

		switch (sortValue) {
		case "Price high to low":
			isPriceListSortedOrNot(sideAreasObtainedList, true);
			break;

		case "Price low to high":
			isPriceListSortedOrNot(sideAreasObtainedList, false);
			break;
		}

	}

	public void applyHomeSizeSortOptionAndVerifyResultHomesTab(String sortValue, String areaCode) {

		WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//div[@id='scrolling']//span[contains(text(),'Homes (')]");
		Element.click(testConfig, homesTab, "Homes tab");
		Browser.wait(testConfig, 5);

		applySortOption(sortValue);

		Helper.scrollOnLazyLoadingFYHWideSearchResultView(testConfig, false);

		List<WebElement> allHomeSizes = Element.getListOfElements(testConfig, How.xPath, ".//span[contains(text(),'ft²')]");
		String areaLocator = ".//div[contains(@class,'card-content')]/div[2]/div[2]/span[contains(@class,'text-xs')]";

		List<WebElement> cardArea = Element.getListOfElements(testConfig, How.xPath, areaLocator);
		ArrayList<WebElement> obtainedRegionHomeSizeList = new ArrayList<>();
		ArrayList<WebElement> obtainedNearAreaHomeSizeList = new ArrayList<>();

		for (int i = 0; i < cardArea.size() / 2; i++) {
			if (cardArea.get(i).getAttribute("innerText").contains(areaCode)) {
				obtainedRegionHomeSizeList.add(allHomeSizes.get(i));
			} else {
				obtainedNearAreaHomeSizeList.add(allHomeSizes.get(i));
			}
		}

		ArrayList<Integer> areaObtainedList = new ArrayList<>();
		testConfig.logComment("--------------- Starting verification for sorting of Cards for Searched location based on " + sortValue + " ---------------");

		String homeSize = "";
		for (int i = 0; i < (obtainedRegionHomeSizeList.size() / 2); i++) {
			WebElement homeSizeEle = obtainedRegionHomeSizeList.get(i);
			if (homeSizeEle.getText().contains("-")) {
				homeSize = homeSizeEle.getText().split("-")[0].replace(",", "").replace("ft²", "").trim();
			} else {
				homeSize = homeSizeEle.getText().split(" ")[0].replace(",", "").trim();
			}
			areaObtainedList.add(Integer.parseInt(homeSize));
		}
		isHomeSizeListSortedOrNot(areaObtainedList, true);

		ArrayList<Integer> sideAreasObtainedList = new ArrayList<>(); 
		testConfig.logComment("--------------- Starting verification for sorting of Cards for Side Areas based on " + sortValue + " ---------------");

		String sideAreashomeSize = "";
		for (int i = 0; i < (obtainedNearAreaHomeSizeList.size() / 2); i++) {
			WebElement homeSizeEle = obtainedNearAreaHomeSizeList.get(i);
			if (homeSizeEle.getText().contains("-")) {
				sideAreashomeSize = homeSizeEle.getText().split("-")[0].replace(",", "").replace("ft²", "").trim();
			} else {
				sideAreashomeSize = homeSizeEle.getText().split(" ")[0].replace(",", "").trim();
			}
			sideAreasObtainedList.add(Integer.parseInt(sideAreashomeSize));
		}
		isHomeSizeListSortedOrNot(sideAreasObtainedList, true);
	}

	public PlanPage navigateToPlanHavingCommMap() {

		checkForPromo();

		String plansLocator = ".//*[text()='floor plans']/parent::button";
		WebElement plansTab = Element.getPageElement(testConfig, How.xPath, plansLocator);
		Element.click(testConfig, plansTab, "Plans tab");

		Browser.wait(testConfig, 2);
		String floorPlanLotLocator = ".//span[contains(text(),'Lot')][contains(@class,'car-uppercase')]";
		String floorPlanNameLocator = ".//span[contains(text(),'Lot')][contains(@class,'car-uppercase')]/ancestor::*[contains(@class,'card-container')]//span[contains(@class,'brand-yellow')]/../span[2]";
		//String floorPlanNameLocator = ".//div[contains(text(),'Lot')][not(contains(@class,'tracking'))]/../..//span/span";

		Browser.wait(testConfig, 2);
		List<WebElement> planCards = Element.getListOfElements(testConfig, How.xPath, floorPlanLotLocator);
		List<WebElement> planNames = Element.getListOfElements(testConfig, How.xPath, floorPlanNameLocator);

		String planName = planNames.get(0).getAttribute("innerText").trim();
		testConfig.putRunTimeProperty("PlanName", planName);
		Browser.wait(testConfig, 2);
		planCards = Element.getListOfElements(testConfig, How.xPath, floorPlanLotLocator);
		Element.click(testConfig, planCards.get(0), "Plan card having community map associated");
		Browser.wait(testConfig, 2);
		String viewDetails = ".//div[contains(@class,'w-full h-full')]//a[contains(@class,'btn secondary')]";
		WebElement viewDetailsBtn = Element.getPageElement(testConfig, How.xPath, viewDetails);
		Element.clickThroughJS(testConfig, viewDetailsBtn, "View Details button");

		Browser.wait(testConfig, 2);
		/*for (String winHandle : testConfig.driver.getWindowHandles()) {
			testConfig.driver.switchTo().window(winHandle);
			getBase64UserNamePwdNetworkTab();
			Browser.wait(testConfig, 1);
		}*/
		Set<String> windowHandle = testConfig.driver.getWindowHandles();
		testConfig.driver.switchTo().window(getLastElement(windowHandle));

		testConfig.logComment("Switching to the new window opened...");

		return new PlanPage(testConfig);
	}

	public void verifyStayUpdatedFormSuccessfulSubmission() {

		String sendMeUpdates = "(.//.//div[@id='scrolling']//button[text()='Send Me Updates'])[1]";
		WebElement sendMeUpdatesBtn = Element.getPageElement(testConfig, How.xPath, sendMeUpdates);
		Element.click(testConfig, sendMeUpdatesBtn, "Send me updates button");

		Browser.wait(testConfig, 2);
		String firstNameLocator = ".//section[@id='stay-updated']//input[@data-sc-field-name='First Name'][@placeholder='Your First Name']";
		String lastNameLocator = ".//section[@id='stay-updated']//input[@data-sc-field-name='Last Name'][@placeholder='Your Last Name']";
		String emailLocator = ".//section[@id='stay-updated']//input[@data-sc-field-name='Email Address'][@placeholder='email@domain.com']";
		String phoneLocator = ".//section[@id='stay-updated']//input[@data-sc-field-name='Phone Number'][@placeholder='Your Phone Number']";

		WebElement firstName = Element.getPageElement(testConfig, How.xPath, firstNameLocator);
		WebElement lastName = Element.getPageElement(testConfig, How.xPath, lastNameLocator);
		WebElement emailAddress = Element.getPageElement(testConfig, How.xPath, emailLocator);
		WebElement phone = Element.getPageElement(testConfig, How.xPath, phoneLocator);

		String firstNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddressValue = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		Element.enterData(testConfig, firstName, firstNameValue, "First Name field value");
		Element.enterData(testConfig, lastName, lastNameValue, "Last Name field value");
		Element.enterData(testConfig, emailAddress, emailAddressValue, "Email Address field value");
		Element.enterData(testConfig, phone, String.valueOf(phoneNo), "Phone Number field value");

		String consent = ".//section[@class='stay-updated']//input[@data-sc-field-name='Terms and Conditions']";
		WebElement consentField = Element.getPageElement(testConfig, How.xPath, consent);
		Element.clickThroughJS(testConfig, consentField, "Consent checkbox");

		Browser.wait(testConfig, 2);

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath, ".//section[@class='stay-updated']//button[@value='Submit'][not(contains(@class,'footer'))]");
		Element.clickThroughJS(testConfig, submitBtn, "Submit button");
		Browser.wait(testConfig, 2);

		verifySuccessMessageRequestInfoForm(firstNameValue);
	}

	private void verifySuccessMessageRequestInfoForm(String firstNameValue) {

		String[] additionalMsg = {"We have successfully received your message. A Brookfield Residential "
				+ "team member will be responding to assist you in finding your dream home", 
				"You may close this window.", "In the meantime, explore"};

		WebElement messageSent = Element.getPageElement(testConfig, How.xPath, ".//div[@title='Message Sent!']/span");
		WebElement thankYouMsg = Element.getPageElement(testConfig, How.xPath, ".//h2[contains(@class,'line-clamp-2')]");
		List<WebElement> additionalContent = Element.getListOfElements(testConfig, How.xPath, ".//div[contains(@class,'text-brp-blue-100')]/p");

		Helper.compareEquals(testConfig, "Title over success screen", "Message Sent!", messageSent.getAttribute("innerText").trim());
		Helper.compareEquals(testConfig, "Thank you message over success screen", "Thank you " + firstNameValue, thankYouMsg.getText().trim());

		int i = 0;
		for (Iterator<WebElement> iterator = additionalContent.iterator(); iterator.hasNext();) {
			WebElement webElement = (WebElement) iterator.next();
			Helper.compareContains(testConfig, "Additional content " + ++i, additionalMsg[i-1], webElement.getAttribute("innerText"));
		}

	}

	public void verifyErrorMessagesStayUpdatedForm(String[] expectedErrorMsgs) {

		String sendMeUpdates = "(.//.//div[@id='scrolling']//button[text()='Send Me Updates'])[1]";
		WebElement sendMeUpdatesBtn = Element.getPageElement(testConfig, How.xPath, sendMeUpdates);
		Element.click(testConfig, sendMeUpdatesBtn, "Send me updates button");

		Browser.wait(testConfig, 2);

		String fields[] = {"First Name", "Last Name", "Email", "Consent"};

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath, ".//section[@class='stay-updated']//button[@value='Submit'][not(contains(@class,'footer'))]");
		Element.click(testConfig, submitBtn, "Submit button");
		Browser.wait(testConfig, 2);

		String errorLoc = ".//span[contains(@class,'field-validation-error')]";
		List<WebElement> allErrors = Element.getListOfElements(testConfig, How.xPath, errorLoc);

		for (int i = 0; i < allErrors.size(); i++) {
			WebElement errorMsg = allErrors.get(i);
			Helper.compareEquals(testConfig, "Error message for " + fields[i] +  " field", expectedErrorMsgs[i], errorMsg.getAttribute("innerText").trim());
		}

	}

	public void verifyFieldsEditableBehavior() {

		String sendMeUpdates = "(.//.//div[@id='scrolling']//button[text()='Send Me Updates'])[1]";
		WebElement sendMeUpdatesBtn = Element.getPageElement(testConfig, How.xPath, sendMeUpdates);
		Element.click(testConfig, sendMeUpdatesBtn, "Send me updates button");

		Browser.wait(testConfig, 2);

		String firstNameLocator = ".//section[@id='stay-updated']//input[@data-sc-field-name='First Name'][@placeholder='Your First Name']";
		String lastNameLocator = ".//section[@id='stay-updated']//input[@data-sc-field-name='Last Name'][@placeholder='Your Last Name']";
		String emailLocator = ".//section[@id='stay-updated']//input[@data-sc-field-name='Email Address'][@placeholder='email@domain.com']";
		String phoneLocator = ".//section[@id='stay-updated']//input[@data-sc-field-name='Phone Number'][@placeholder='Your Phone Number']";

		String firstNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastNameValue = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddressValue = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		WebElement firstName = Element.getPageElement(testConfig, How.xPath, firstNameLocator);
		WebElement lastName = Element.getPageElement(testConfig, How.xPath, lastNameLocator);
		WebElement emailAddress = Element.getPageElement(testConfig, How.xPath, emailLocator);
		WebElement phone = Element.getPageElement(testConfig, How.xPath, phoneLocator);

		Element.enterData(testConfig, firstName, firstNameValue, "First Name field value");
		Element.enterData(testConfig, lastName, lastNameValue, "Last Name field value");
		Element.enterData(testConfig, emailAddress, emailAddressValue, "Email Address field value");
		Element.enterData(testConfig, phone, String.valueOf(phoneNo), "Phone Number field value");

		Helper.compareEquals(testConfig, "Text entered for First Name field", firstNameValue,
				firstName.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Last Name field", lastNameValue,
				lastName.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Email Address field", emailAddressValue,
				emailAddress.getAttribute("value"));
		Helper.compareEquals(testConfig, "Text entered for Phone Number field", String.valueOf(phoneNo),
				phone.getAttribute("value"));

		Browser.wait(testConfig, 2);
	}

	public void verifyErrorForLastNameField(String expectedErrorMessage) {

		String lastNameLocator = ".//section[@id='stay-updated']//input[@data-sc-field-name='Last Name'][@placeholder='Your Last Name']";
		WebElement lastName = Element.getPageElement(testConfig, How.xPath, lastNameLocator);
		String lastNameValue = Helper.generateRandomAlphabetsString(1);
		Element.enterDataAfterClick(testConfig, lastName, lastNameValue, "Last Name field value");

		String consent = ".//section[@class='stay-updated']//input[@data-sc-field-name='Terms and Conditions']";
		WebElement consentField = Element.getPageElement(testConfig, How.xPath, consent);
		Element.clickThroughJS(testConfig, consentField, "Consent checkbox");

		Browser.wait(testConfig, 2);

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath, ".//section[@class='stay-updated']//button[@value='Submit'][not(contains(@class,'footer'))]");
		Element.clickThroughJS(testConfig, submitBtn, "Submit button");
		Browser.wait(testConfig, 2);

		String errorLoc = ".//span[contains(@class,'field-validation-error')]";
		List<WebElement> allErrors = Element.getListOfElements(testConfig, How.xPath, errorLoc);

		for (int i = 0; i < allErrors.size(); i++) {
			WebElement errorMsg = allErrors.get(i);
			Helper.compareEquals(testConfig, "Error message for Last Name field", expectedErrorMessage, errorMsg.getAttribute("innerText").trim());
		}
	}

	public void verifyRealStateAgentCheckboxSelection() {

		String yesCheckboxLocator = ".//section[@class='stay-updated']//input[@data-sc-field-name='Agent Opt-In'][@value='true']";
		String noCheckboxLocator = ".//section[@class='stay-updated']//input[@data-sc-field-name='Agent Opt-In'][@value='false']";

		WebElement yesCheckbox = Element.getPageElement(testConfig, How.xPath, yesCheckboxLocator);
		WebElement noCheckbox = Element.getPageElement(testConfig, How.xPath, noCheckboxLocator);

		verifyCheckboxSelected(yesCheckbox, "Yes");
		verifyCheckboxNotSelected(noCheckbox, "No");
		verifyCheckboxSelected(noCheckbox, "No");
		verifyCheckboxNotSelected(yesCheckbox, "Yes");
	}

	private void verifyCheckboxNotSelected(WebElement checkbox, String checkboxName) {

		try {
			if(!checkbox.isSelected()) {
				testConfig.logPass("Verified that the other checkbox i.e. " + checkboxName + " checkbox, is displaying as not selected");
			} else {
				testConfig.logFail("Failed to verify that the other checkbox i.e. " + checkboxName + " checkbox, is displaying as not selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that the other checkbox i.e. " + checkboxName + " checkbox, is displaying as not selected");
		}
	}

	private void verifyCheckboxSelected(WebElement checkbox, String checkboxName) {

		Element.click(testConfig, checkbox, checkboxName + " checkbox");
		Browser.wait(testConfig, 2);

		try {
			if(checkbox.isSelected()) {
				testConfig.logPass("Verified that after clicking " + checkboxName + " checkbox, the checkbox is displaying as selected");
			} else {
				testConfig.logFail("Failed to verify that on clicking " + checkboxName + " checkbox, the checkbox is displaying as selected");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that on clicking " + checkboxName + " checkbox, the checkbox is displaying as selected");
		}

	}

	public void applyPriceFilterRangeAndVerifyClearFilter() {

		applyPriceFilter();

		String clearFiltersLoc = ".//button[contains(@class,'ftr-border-solid')]//*[name()='svg']";
		List<WebElement> clearFilter = Element.getListOfElements(testConfig, How.xPath, clearFiltersLoc);
		Element.click(testConfig, clearFilter.get(0), "Clear Filters link for price filter");

		Browser.wait(testConfig, 2);

		String locator = ".//button//span[text()='Price Range']";
		WebElement filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
		Element.click(testConfig, filterBtn, "Price range filter");
		Browser.wait(testConfig, 2);

		String minPriceSelectedValueLoc = ".//div[contains(@class,'panel')]//label[text()='Min:']//parent::div/div/button/span[1]";
		WebElement minPriceSelectedValue = Element.getPageElement(testConfig, How.xPath, minPriceSelectedValueLoc);
		Helper.compareEquals(testConfig, "Selected value in Min Price dropdown", "No Min", minPriceSelectedValue.getText());

		String maxPriceSelectedValueLoc = ".//div[contains(@class,'panel')]//label[text()='Max:']//parent::div/div/button/span[1]";
		WebElement maxPriceSelectedValue = Element.getPageElement(testConfig, How.xPath, maxPriceSelectedValueLoc);
		Helper.compareEquals(testConfig, "Selected value in Min Price dropdown", "No Max", maxPriceSelectedValue.getText());

		WebElement doneBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'panel')]//button[contains(@class,'btn dark')]");
		Element.click(testConfig, doneBtn, "Done button to apply the filter");
		Browser.wait(testConfig, 3);

	}

	public void applyHomeTypeFilterAndVerifyClearFilter(String expectedColor) {

		applyHomeTypeFilter(expectedColor);

		String clearFiltersLoc = ".//button[contains(@class,'ftr-border-solid')]//*[name()='svg']";
		List<WebElement> clearFilter = Element.getListOfElements(testConfig, How.xPath, clearFiltersLoc);
		Element.click(testConfig, clearFilter.get(0), "Clear Filters link for price filter");

		Browser.wait(testConfig, 2);

		String locator = ".//button//span[text()='Home Type']";
		WebElement filterBtn = Element.getPageElement(testConfig, How.xPath, locator);
		Element.click(testConfig, filterBtn, "Home type filter button");
		Browser.wait(testConfig, 2);

		WebElement doneBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'panel')]//button[contains(@class,'btn dark')]");
		Element.click(testConfig, doneBtn, "Done button to apply the filter");
		Browser.wait(testConfig, 3);
	}

	public void applyToogleFunctionalityForHomesCommunitiesTab(String expectedBorderColor, String defaultColor) {

		WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]/..");
		WebElement communitiesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'communities')]/..");
		WebElement plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'floor plans')]/..");
		Element.click(testConfig, homesTab, "Homes tab");
		Browser.wait(testConfig, 1);

		String cssValue = homesTab.getCssValue("background-color");
		String hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color for Homes tab", expectedBorderColor, hexcolor);

		cssValue = communitiesTab.getCssValue("background-color");
		hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color for Communities tab", defaultColor, hexcolor);

		cssValue = plansTab.getCssValue("background-color");
		hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color for Plans tab", defaultColor, hexcolor);

		homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]/..");
		communitiesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'communities')]/..");
		plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'floor plans')]/..");
		Element.click(testConfig, communitiesTab, "Communities tab");
		Browser.wait(testConfig, 1);

		cssValue = communitiesTab.getCssValue("background-color");
		hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color for Communities tab", expectedBorderColor, hexcolor);

		cssValue = homesTab.getCssValue("background-color");
		hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color for Homes tab", defaultColor, hexcolor);

		cssValue = plansTab.getCssValue("background-color");
		hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color for Plans tab", defaultColor, hexcolor);

		homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]/..");
		communitiesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'communities')]/..");
		plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'floor plans')]/..");
		Element.click(testConfig, plansTab, "Plans tab");
		Browser.wait(testConfig, 1);

		cssValue = plansTab.getCssValue("background-color");
		hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color for Plans tab", expectedBorderColor, hexcolor);

		cssValue = homesTab.getCssValue("background-color");
		hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color for Homes tab", defaultColor, hexcolor);

		cssValue = communitiesTab.getCssValue("background-color");
		hexcolor = Color.fromString(cssValue).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Background color for Communities tab", defaultColor, hexcolor);
	}

	public void applyBedBathFilterAndVerifyClearFilter() {

		applyBedBathFilter();

		String clearFiltersLoc = ".//button[contains(@class,'ftr-border-solid')]//*[name()='svg']";
		List<WebElement> clearFilter = Element.getListOfElements(testConfig, How.xPath, clearFiltersLoc);
		Element.click(testConfig, clearFilter.get(0), "Clear Filters link for price filter");

		Browser.wait(testConfig, 2);

		String locator = ".//button//span[text()='Beds & Baths']";
		WebElement filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
		Element.click(testConfig, filterBtn, "Beds & Baths range filter");
		Browser.wait(testConfig, 2);

		String bedsSelectedValueLoc = ".//span[@data-testid='label-beds']";
		WebElement bedsSelectedValue = Element.getPageElement(testConfig, How.xPath, bedsSelectedValueLoc);
		if(bedsSelectedValue.getText().equals("Any")) {
			testConfig.logPass("Verified 'Any' option displaying selected for Beds filter");
		} else {
			testConfig.logFail("Failed to verify that 'Any' option is displaying selected for Beds filter");
		}

		String bathsSelectedValueLoc = ".//span[@data-testid='label-baths']";
		WebElement bathsSelectedValue = Element.getPageElement(testConfig, How.xPath, bathsSelectedValueLoc);
		if(bathsSelectedValue.getText().equals("Any")) {
			testConfig.logPass("Verified 'Any' option displaying selected for Baths filter");
		} else {
			testConfig.logFail("Failed to verify that 'Any' option is displaying selected for Baths filter");
		}

		WebElement doneBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'panel')]//button[contains(@class,'btn dark')]");
		Element.click(testConfig, doneBtn, "Done button to apply the filter");
		Browser.wait(testConfig, 3);		

	}

	public void applySquareFootageFilterAndVerifyClearFilter() {

		applySquareFootageFilter();

		WebElement doneBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'panel')]//button[contains(@class,'btn dark')]");
		Element.click(testConfig, doneBtn, "Done button to close the modal");
		Browser.wait(testConfig, 3);

		String locator = ".//button//div[contains(text(),'More Filters')]";
		WebElement filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
		Element.click(testConfig, filterBtn, "More filter");
		Browser.wait(testConfig, 2);

		String clearFiltersLoc = ".//div[@data-headlessui-state='open']//button[contains(@class,'link dark-underlined')]/span";
		WebElement clearFilter = Element.getPageElement(testConfig, How.xPath, clearFiltersLoc);
		Element.click(testConfig, clearFilter, "Clear Filters link for Square Footage filter");

		Browser.wait(testConfig, 2);

		locator = ".//button//div[contains(text(),'More Filters')]";
		filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
		Element.click(testConfig, filterBtn, "More filter");
		Browser.wait(testConfig, 2);

		String minPriceSelectedValueLoc = ".//div[contains(@class,'panel')]//label[text()='Min Square Feet']//parent::div/div/button";
		WebElement minPriceSelectedValue = Element.getPageElement(testConfig, How.xPath, minPriceSelectedValueLoc);
		Helper.compareEquals(testConfig, "Selected value in Min Price dropdown", "No Min", minPriceSelectedValue.getText());

		String maxPriceSelectedValueLoc = ".//div[contains(@class,'panel')]//label[text()='Max Square Feet']//parent::div/div/button";
		WebElement maxPriceSelectedValue = Element.getPageElement(testConfig, How.xPath, maxPriceSelectedValueLoc);
		Helper.compareEquals(testConfig, "Selected value in Min Price dropdown", "No Max", maxPriceSelectedValue.getText());

		doneBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'panel')]//button[contains(@class,'btn dark')]");
		Element.click(testConfig, doneBtn, "Done button to close the modal");
		Browser.wait(testConfig, 3);
	}

	public void verifyCheckboxesNotSelectedByDefault() {

		Browser.wait(testConfig, 2);

		String locator = ".//button//div[contains(text(),'More Filters')]";
		WebElement filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
		Element.click(testConfig, filterBtn, "More filter");
		Browser.wait(testConfig, 2);

		String walkthroughAvailableButton = ".//button/div[text()='3D Walkthrough available']/parent::button";

		try {
			WebElement walkthroughAvailableBtn = Element.getPageElement(testConfig, How.xPath, walkthroughAvailableButton);
			if(!walkthroughAvailableBtn.getAttribute("class").contains("selected")) {
				testConfig.logPass("Verify Virtual Tour Available button is not selected by default");
			} else {
				testConfig.logFail("Failed to verify Virtual Tour Available checkbox is not selected by default");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify Virtual Tour Available checkbox is not selected by default");
		}

		String mytimeTourAvailableInput = "(.//span[contains(text(),'tour available')]/ancestor::div[contains(@class,'ftr-gap-3')]//button)[2]";

		try {
			WebElement mytimeTourAvailableCheckbox = Element.getPageElement(testConfig, How.xPath, mytimeTourAvailableInput);
			if(!mytimeTourAvailableCheckbox.getAttribute("class").contains("selected")) {
				testConfig.logPass("Verify Mytime Tour Available checkbox is not selected by default");
			} else {
				testConfig.logFail("Failed to verify Mytime Tour Available checkbox is not selected by default");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify Mytime Tour Available checkbox is not selected by default");
		}
	}

	public void verifyDefaultSortingOptionAndCardCount(ResultTab resultTab, String defaultSort) {

		switch (resultTab) {
		case Communities:
			/*Helper.scrollOnLazyLoadingFYHWideSearchResultView(testConfig, true);
			totalCardsLoc = ".//div[contains(@id,'card')]";
			tabText = ".//button/span[contains(text(),'communities')]";
			totalCards = Element.getListOfElements(testConfig, How.xPath, totalCardsLoc);
			WebElement communityTab = Element.getPageElement(testConfig, How.xPath, tabText);
			Helper.compareEquals(testConfig, "Total community cards as",
					Integer.parseInt(communityTab.getText().substring(communityTab.getText().indexOf('(') + 1, communityTab.getText().indexOf(')'))),
					totalCards.size());
			 */
			break;

		case Homes:
			WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]");
			Element.click(testConfig, homesTab, "Homes tab");
			//Helper.scrollOnLazyLoadingFYHWideSearchResultView(testConfig, false);
			/*totalCardsLoc = ".//div[contains(@id,'home')]";
			totalCards = Element.getListOfElements(testConfig, How.xPath, totalCardsLoc);
			Helper.compareEquals(testConfig, "Total home cards as",
					Integer.parseInt(homesTab.getText().substring(homesTab.getText().indexOf('(') + 1, homesTab.getText().indexOf(')'))),
					totalCards.size());*/
			break;

		case Plans:
			WebElement plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'floor plans')]");
			Element.click(testConfig, plansTab, "Plans tab");
			break;
		}

		Browser.wait(testConfig, 5);
		String defaultSortBtnLocator = "(.//span[contains(text(),'Sort:')]/../button)[1]/div";
		WebDriverWait wait = new WebDriverWait(testConfig.driver, Duration.ofSeconds(10)); 
		WebElement sortByBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(defaultSortBtnLocator)));
		Helper.compareEquals(testConfig, "Default sort by option for " + resultTab.toString() + " tab", defaultSort, sortByBtn.getText());

	}

	public void verifyDataForCommunityCards() {

		Browser.wait(testConfig, 15);
		String cardsAddressLoc = ".//div[contains(@id,'card')]//h5";
		String cardsCommunityName = ".//div[contains(@id,'card')]//h3";
		String cardsAmenitiesLoc = "(.//div[contains(@id,'card')])[number]//span[contains(@class,'amenity-text')]";
		String imageLoc = "(.//div[contains(@id,'card')])//div[contains(@class,'image-section')]/div/img";

		String totalCardsLoc = ".//div[contains(@id,'card')]";
		List<WebElement> totalCards = Element.getListOfElements(testConfig, How.xPath, totalCardsLoc);

		for (int i = 0; i < totalCards.size(); i++) {
			testConfig.logComment("****************** Looking for Community Card Data for card no " + (i + 1) + " ******************");

			List<WebElement> cardsAddress = Element.getListOfElements(testConfig, How.xPath, cardsAddressLoc);
			if (!cardsAddress.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Address displaying over card as " + cardsAddress.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Address displaying over card value as blank... failing the scenario");
			}

			List<WebElement> communityName = Element.getListOfElements(testConfig, How.xPath, cardsCommunityName);
			if (!communityName.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Community name displaying over card as " + communityName.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Community name displaying over card value as blank... failing the scenario");
			}

			List<WebElement> allAmenities = Element.getListOfElements(testConfig, How.xPath, cardsAmenitiesLoc.replace("number", String.valueOf(i + 1)));
			if(allAmenities.size() == 0) {
				testConfig.logFail("No amenities displaying for the community card.. failing the scenario");
			} else {
				for (Iterator<WebElement> iterator = allAmenities.iterator(); iterator.hasNext();) {
					WebElement webElement = (WebElement) iterator.next();
					testConfig.logPass("Amenity for Community Card " + (i + 1) + " : " + webElement.getText());					
				}
			}

			List<WebElement> images = Element.getListOfElements(testConfig, How.xPath, imageLoc);
			if(!images.get(i).getAttribute("src").isEmpty()) {
				testConfig.logPass("Getting image displaying over card with url as " + images.get(i).getAttribute("src"));
			} else {
				testConfig.logFail("Getting image src displaying over card value as blank... failing the scenario");
			}
		}
	}

	public CommunityPage verifyNavigationToLearnMoreCommunityPage() {

		String learnMoreLoc = "(.//div[contains(@id,'card')])//div[contains(@class,'hidden')]/a[contains(text(),'Learn More')]";
		List<WebElement> learnMoreComm = Element.getListOfElements(testConfig, How.xPath, learnMoreLoc);

		String communityNameLoc = "(.//div[contains(@id,'card')])//div[contains(@class,'hidden')]/a[contains(text(),'Learn More')]/../..//h3";
		List<WebElement> communityNames = Element.getListOfElements(testConfig, How.xPath, communityNameLoc);
		testConfig.putRunTimeProperty("CommunityNameFYH", communityNames.get(0).getText().replace("Community", "").trim());
		Element.clickThroughJS(testConfig, learnMoreComm.get(0), communityNames.get(0).getText());

		return new CommunityPage(testConfig);
	}

	public void verifyNeighborhoodPillBehavior(String expectedBorderColor, String expectedDefaultColor) throws UnsupportedEncodingException {

		Browser.wait(testConfig, 10);

		String sitemapNeighborhood = ".//div[not(contains(@class,'hidden'))]/div/button[contains(@class,'btn primary')]//ancestor::div[contains(@id,'card')]//h4[contains(@class,'title')]";
		List<WebElement> allNeighborhoodsName = Element.getListOfElements(testConfig, How.xPath, sitemapNeighborhood);

		String neighborhoodName = allNeighborhoodsName.get(0).getText();
		testConfig.putRunTimeProperty("NeighborhoodNameFYH", neighborhoodName);

		Element.clickThroughJS(testConfig, allNeighborhoodsName.get(0), neighborhoodName);

		Browser.wait(testConfig, 5);
		String learnMoreTextLoc = "a.primary-underlined";
		WebElement learnMoreText = Element.getPageElement(testConfig, How.css, learnMoreTextLoc);

		Helper.compareContains(testConfig, "Learn More text", neighborhoodName, learnMoreText.getText());

		String learnMoreLinkLoc = "a.primary-underlined";
		WebElement learnMoreAnchor = Element.getPageElement(testConfig, How.css, learnMoreLinkLoc);
		String learnMoreLink = Element.getAttribute(testConfig, learnMoreAnchor, "href", "Learn More link");

		verifyURLAsPerDomain(learnMoreLink);

		Browser.wait(testConfig, 2);

		checkForPromo();

		Browser.waitWithoutLogging(testConfig, 2);
		String clearFilterBtn = ".//button[contains(@class,'rounded-full')][contains(@class,'block')]//p[text()='CLEAR FILTER']";
		try {
			WebElement clearFilter = Element.getPageElement(testConfig, How.xPath, clearFilterBtn);
			if(clearFilter.isDisplayed()) {
				testConfig.logPass("Verified getting clear filter link displaying for the selected neighborhood");
			} else {
				testConfig.logFail("Failed to verify that getting clear filter link displaying for the selected neighborhood");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that getting clear filter link displaying for the selected neighborhood");
		}

		String selectedNeighborhoodLoc = ".//p[text()='CLEAR FILTER']/ancestor::button[contains(@class,'primary')]";
		WebElement selectedNeighborhood = Element.getPageElement(testConfig, How.xPath, selectedNeighborhoodLoc);

		String selectedNeighborhoodNameLoc = ".//p[text()='CLEAR FILTER']/ancestor::button[contains(@class,'primary')]//p[contains(@class,'bold')]";
		WebElement selectedNeighborhoodName = Element.getPageElement(testConfig, How.xPath, selectedNeighborhoodNameLoc);

		String name = selectedNeighborhoodName.getText();
		String requiredName = name.substring(0, name.lastIndexOf(" "));

		String cssValue = selectedNeighborhood.getCssValue("border-color");
		String rgbVal = cssValue.substring(cssValue.indexOf("rgb"));
		String hexcolor = Color.fromString(rgbVal).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "Border color for the selected neighborhood pill", expectedBorderColor.toUpperCase(),
				hexcolor.toUpperCase());

		Browser.wait(testConfig, 2);
		WebElement clearFilter = Element.getPageElement(testConfig, How.xPath, clearFilterBtn);
		Element.click(testConfig, clearFilter, "Clear Filter Button");

		Actions action = new Actions(testConfig.driver);
		action.moveToElement(mapbox).build().perform();
		Browser.wait(testConfig, 2);

		Browser.wait(testConfig, 2);
		String neighborhoodItem = ".//p[contains(text(),'" + requiredName + "')]/../../..";
		selectedNeighborhood = Element.getPageElement(testConfig, How.xPath, neighborhoodItem);

		cssValue = selectedNeighborhood.getCssValue("border-color");
		rgbVal = cssValue.substring(cssValue.indexOf("rgb"));
		hexcolor = Color.fromString(rgbVal).asHex(); // converted Into HexFormat
		Helper.compareEquals(testConfig, "After clear filter, Border color for the neighborhood pill changes to", expectedDefaultColor.toUpperCase(),
				hexcolor.toUpperCase());

	}

	public void verifyDataForHomeCards() throws UnsupportedEncodingException {

		WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[text()='quick move-in']");
		Element.click(testConfig, homesTab, "Homes tab");
		Browser.wait(testConfig, 5);

		String cardImage = "(.//img[contains(@class,'card-image-content')])[number]";
		String commNameLoc = ".//div[contains(@class,'card-header')]//span[contains(@class,'car-capitalize')]";
		String neighborhoodNameLoc = ".//div[contains(@class,'card-header')]//parent::div//div[contains(@class,'car-h-full')][1]/span[contains(@class,'car-capitalize')]";
		String itemNameLoc = ".//div[contains(@class,'card-titles')]/span[contains(@class,'card-title')]";
		String itemAddressLoc = ".//div[contains(@class,'card-titles')]/span[contains(@class,'card-subtitle')]";
		String homeTypeLoc = "((.//div[contains(@class,'specs-container')])[number]//span[contains(@class,'car-type-brds-v2-xs-500')])[1]";
		String squareFootageLoc = "((.//div[contains(@class,'specs-container')])[number]//span[contains(@class,'car-type-brds-v2-xs-500')])[2]";
		String bedroomLoc = "((.//div[contains(@class,'specs-container')])[number]//span[contains(@class,'car-type-brds-v2-xs-500')])[3]";
		String bathroomLoc = "((.//div[contains(@class,'specs-container')])[number]//span[contains(@class,'car-type-brds-v2-xs-500')])[4]";
		String pricingLoc = ".//div[contains(@class,'car-text-brds-v1-grayscale-blue-medium-ft')]//div[contains(text(),'$')]";

		String allCardsLoc = ".//*[contains(@class,'card-container')]";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, allCardsLoc);

		Helper.scrollOnLazyLoadingPage(testConfig, true);

		for (int i = 0; i < allCards.size(); i++) {

			testConfig.logComment("****************** Looking for Community Card Data for card no " + (i + 1) + " ******************");

			WebElement cardImageAndThumb = Element.getPageElement(testConfig, How.xPath, cardImage.replace("number", String.valueOf(i + 1)));
			String largeImageSrc = Element.getAttribute(testConfig, cardImageAndThumb, "src", "Large image");
			if (!largeImageSrc.isEmpty()) {
				testConfig.logPass("Getting Larger image source displaying for the card as " + largeImageSrc);
				verifyURLAsPerDomain(largeImageSrc);
			} else {
				testConfig.logFail("Getting Larger image source displaying over card as blank... failing the scenario");
			}

			List<WebElement> commName = Element.getListOfElements(testConfig, How.xPath, commNameLoc);
			if (!commName.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Community Name displaying over card as " + commName.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Community Name displaying over card value as blank... failing the scenario");
			}

			List<WebElement> neighborhoodName = Element.getListOfElements(testConfig, How.xPath, neighborhoodNameLoc);
			if (!neighborhoodName.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Neighborhood Name displaying over card as " + neighborhoodName.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Neighborhood Name displaying over card value as blank... failing the scenario");
			}

			List<WebElement> itemName = Element.getListOfElements(testConfig, How.xPath, itemNameLoc);
			if(!itemName.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Item Name displaying over card as " + itemName.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Item Name displaying over card value as blank... failing the scenario");
			}

			List<WebElement> itemAddress = Element.getListOfElements(testConfig, How.xPath, itemAddressLoc);
			if(!itemAddress.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Item Address displaying over card as " + itemAddress.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Item Address displaying over card value as blank... failing the scenario");
			}

			WebElement homeType = Element.getPageElement(testConfig, How.xPath, homeTypeLoc.replace("number", String.valueOf(i + 1)));
			if(!homeType.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Home Type displaying over card as " + homeType.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Home Type displaying over card value as blank... failing the scenario");
			}

			WebElement squareFootage = Element.getPageElement(testConfig, How.xPath, squareFootageLoc.replace("number", String.valueOf(i + 1)));
			if(!squareFootage.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Square Footage displaying over card as " + squareFootage.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Square Footage displaying over card value as blank... failing the scenario");
			}

			WebElement bedroom = Element.getPageElement(testConfig, How.xPath, bedroomLoc.replace("number", String.valueOf(i + 1)));
			if(!bedroom.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Bedroom displaying over card as " + squareFootage.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Bedroom Footage displaying over card value as blank... failing the scenario");
			}

			WebElement bathroom = Element.getPageElement(testConfig, How.xPath, bathroomLoc.replace("number", String.valueOf(i + 1)));
			if(!bathroom.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Bathroom displaying over card as " + bathroom.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Bathroom Footage displaying over card value as blank... failing the scenario");
			}

			List<WebElement> pricing = Element.getListOfElements(testConfig, How.xPath, pricingLoc);
			if(!pricing.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Pricing displaying over card as " + pricing.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Pricing displaying over card value as blank... failing the scenario");
			}
		}
	}

	public void verifyBreadcrumbRedirectionToMarketFYHPage(String marketName) throws UnsupportedEncodingException {

		try {
			WebElement acceptCookies = Element.getPageElement(testConfig, How.css,
					"button#onetrust-accept-btn-handler");
			Element.click(testConfig, acceptCookies, "One trust accept cookies button");
			testConfig.putRunTimeProperty("CookieSetting", "Yes");
		} catch (Exception e) {
			testConfig.putRunTimeProperty("CookieSetting", "No");
			testConfig.logComment("Accept cookie section not displayed");
		}

		boolean flag = false;
		String breadcrumbItemsLoc = ".//nav[@aria-label='breadcrumbs']//a/span[@class='label nav-capitalize']";
		List<WebElement> allCrumbs = Element.getListOfElements(testConfig, How.xPath, breadcrumbItemsLoc);

		for (Iterator<WebElement> iterator = allCrumbs.iterator(); iterator.hasNext();) {
			WebElement breadcrumbItem = (WebElement) iterator.next();
			if(breadcrumbItem.getText().equals(marketName)) {
				Element.clickThroughJS(testConfig, breadcrumbItem, breadcrumbItem.getText() + " breadcrumb item");
				break;
			}
		}

		Browser.wait(testConfig, 10);
		String marketNameLoc = ".//div[contains(@class,'space')]/h2";

		Helper.removeCookies(testConfig);
		testConfig.driver.navigate().refresh();
		Browser.waitWithoutLogging(testConfig, 5);
		WebElement marketNameOnNewView = Element.getPageElement(testConfig, How.xPath, marketNameLoc);
		Helper.compareEquals(testConfig, "Market Name on new view", marketName, marketNameOnNewView.getText());

		try {
			WebElement acceptCookies = Element.getPageElement(testConfig, How.css,
					"button#onetrust-accept-btn-handler");
			Element.click(testConfig, acceptCookies, "One trust accept cookies button");
			testConfig.putRunTimeProperty("CookieSetting", "Yes");
		} catch (Exception e) {
			testConfig.putRunTimeProperty("CookieSetting", "No");
			testConfig.logComment("Accept cookie section not displayed");
		}

		String breadcrumbsLocOnNewView = ".//div[contains(@class,'space')]/a";
		List<WebElement> breadcrumbsOnNewView = Element.getListOfElements(testConfig, How.xPath, breadcrumbsLocOnNewView);
		if(breadcrumbsOnNewView.size() == 0) {
			testConfig.logFail("No breadcrumbs are displaying over the screen... hence failing the scenario");
		} else {
			for (Iterator<WebElement> iterator = breadcrumbsOnNewView.iterator(); iterator.hasNext();) {
				WebElement breadcrumbNewView = (WebElement) iterator.next();
				if(breadcrumbNewView.getText().trim().equals(marketName.toUpperCase())) {
					flag = true;
					break;
				}
			}
			if(flag) {
				testConfig.logPass("Correct breadcrumbs are displaying over the view");
				for (int i = 0; i < breadcrumbsOnNewView.size(); i++) {
					testConfig.logComment("Breadcrumb item : " + (i + 1) + " : " + breadcrumbsOnNewView.get(i).getText());				
				}
			} else {
				testConfig.logFail("Breadcrumbs are displaying but those are not correct... hence failing the scenario");
			}
		}

		for (Iterator<WebElement> iterator = breadcrumbsOnNewView.iterator(); iterator.hasNext();) {
			WebElement breadcrumbNewView = (WebElement) iterator.next();
			String link = breadcrumbNewView.getAttribute("href");
			verifyURLAsPerDomain(link);
		}
	}

	public void verifyRedirectionOnFindYourHomeLink() {

		try {
			WebElement acceptCookies = Element.getPageElement(testConfig, How.css,
					"button#onetrust-accept-btn-handler");
			Element.click(testConfig, acceptCookies, "One trust accept cookies button");
			testConfig.putRunTimeProperty("CookieSetting", "Yes");
		} catch (Exception e) {
			testConfig.putRunTimeProperty("CookieSetting", "No");
			testConfig.logComment("Accept cookie section not displayed");
		}

		Browser.wait(testConfig, 4);
		WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]/../div/div");
		WebElement communityTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'communities')]/../div/div");

		String homesTabLabel = Element.getText(testConfig, homesTab, "Homes tab content");
		String communitiesTabLabel = Element.getText(testConfig, communityTab, "Communities tab content");

		int locationHomeCount = Integer.parseInt(homesTabLabel);
		int locationCommunityCount = Integer.parseInt(communitiesTabLabel);

		String breadcrumbsLocOnNewView = ".//div[contains(@class,'space')]/a";
		List<WebElement> breadcrumbsOnNewView = Element.getListOfElements(testConfig, How.xPath, breadcrumbsLocOnNewView);
		for (Iterator<WebElement> iterator = breadcrumbsOnNewView.iterator(); iterator.hasNext();) {
			WebElement breadcrumbItem = (WebElement) iterator.next();
			if(breadcrumbItem.getText().contains("FIND YOUR HOME")) {
				Element.click(testConfig, breadcrumbItem, breadcrumbItem.getText() + " breadcrumb item");
				break;
			}
		}

		Browser.wait(testConfig, 5);
		Helper.removeCookies(testConfig);
		testConfig.driver.navigate().refresh();
		Browser.waitWithoutLogging(testConfig, 15);

		WebElement homesTabNewView = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]/../div/div");
		WebElement communityTabNewView = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'communities')]/../div/div");

		String homesTabLabelNewView = Element.getText(testConfig, homesTabNewView, "Homes tab content");
		String communitiesTabLabelNewView = Element.getText(testConfig, communityTabNewView, "Communities tab content");

		int locationHomeCountNewView = Integer.parseInt(homesTabLabelNewView);
		int locationCommunityCountNewView = Integer.parseInt(communitiesTabLabelNewView);

		if(locationHomeCountNewView > locationHomeCount) {
			testConfig.logPass("Verified redirection happened correctly and the home count being updated to " + locationHomeCountNewView + " from " + locationHomeCount);
		} else {
			testConfig.logFail("Failed to verify the redirection happened correctly as home count downgrade to " + locationHomeCountNewView + " from " + locationHomeCount);
		}

		if(locationCommunityCountNewView > locationCommunityCount) {
			testConfig.logPass("Verified redirection happened correctly and the community count being updated to " + locationCommunityCountNewView + " from " + locationCommunityCount);
		} else {
			testConfig.logFail("Failed to verify the redirection happened correctly as community count downgrade to " + locationCommunityCountNewView + " from " + locationCommunityCount);
		}
	}

	public void verifyCommunityMarkersOverMap() {

		Browser.wait(testConfig, 10);

		Helper.scrollOnLazyLoadingFYHWideSearchResultView(testConfig, false);
		String allCommCardsLoc = ".//div[contains(@class,'item-container')]//button[contains(@class,'btn primary md')]/ancestor::div//h3[contains(@class,'community-name')]";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, allCommCardsLoc);

		for (Iterator<WebElement> iterator = allCards.iterator(); iterator.hasNext();) {
			WebElement communityCards = (WebElement) iterator.next();
			Actions action = new Actions(testConfig.driver);
			action.moveToElement(communityCards).build().perform();
			Browser.wait(testConfig, 2);
		}

		allCommCardsLoc = ".//div[contains(@class,'item-container')]//button[contains(@class,'btn primary md')]/ancestor::div//h3[contains(@class,'community-name')]";
		allCards = Element.getListOfElements(testConfig, How.xPath, allCommCardsLoc);

		WebElement communityTabNewView = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'communities')]");
		String communitiesTabLabelNewView = Element.getText(testConfig, communityTabNewView, "Communities tab content");
		int locationCommunityCountNewView = Integer.parseInt(communitiesTabLabelNewView.replace("Communities (", "").replace(")", ""));

		String allCommMarkersLoc = ".//div[contains(@id,'community-marker')][contains(@style,'z-index: 1')]";
		List<WebElement> allCommMarkers = Element.getListOfElements(testConfig, How.xPath, allCommMarkersLoc);

		Helper.compareEquals(testConfig, "Total communities markers displaying over the map", locationCommunityCountNewView, allCommMarkers.size());

	}

	public void verifyLegendSectionDisplayingOverMap() {

		checkForPromo();

		String legendsSection = ".//div[contains(@class,'button-shadow')]/button";
		WebElement legendCollapsed = Element.getPageElement(testConfig, How.xPath, legendsSection);
		Element.click(testConfig, legendCollapsed, "Legend section");
		Browser.waitWithoutLogging(testConfig, 2);

		String legendsLoc = ".//div[contains(@class,'lgn-grid')]/span/span";
		List<WebElement> allLegends = Element.getListOfElements(testConfig, How.xPath, legendsLoc);

		if(allLegends.size() == 0) {
			testConfig.logFail("No legends are displaying over map in 2D Community view... failing the test");
		} else {
			for (int i = 0; i < allLegends.size(); i++) {
				testConfig.logPass("Getting Legend " + (i + 1) + " as: " + allLegends.get(i).getAttribute("innerText"));				
			}
		}
	}

	public void verifyZoomInOutButtonsAreDisplaying() {

		Browser.wait(testConfig, 10);
		String zoomInBtnLoc = ".//button[@class='mapboxgl-ctrl-zoom-in']";
		String zoomOutBtnLoc = ".//button[@class='mapboxgl-ctrl-zoom-out']";

		try {
			WebElement zoomInBtn = Element.getPageElement(testConfig, How.xPath, zoomInBtnLoc);
			if(zoomInBtn.isDisplayed()) {
				testConfig.logPass("Verified Zoom in button is displaying correctly");
			} else {
				testConfig.logFail("Failed to verify Zoom in button is displaying correctly");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify Zoom in button is displaying correctly");
		}

		try {
			WebElement zoomOutBtn = Element.getPageElement(testConfig, How.xPath, zoomOutBtnLoc);
			if(zoomOutBtn.isDisplayed()) {
				testConfig.logPass("Verified Zoom out button is displaying correctly");
			} else {
				testConfig.logFail("Failed to verify Zoom out button is displaying correctly");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify Zoom out button is displaying correctly");
		}

		try {
			WebElement zoomInBtn = Element.getPageElement(testConfig, How.xPath, zoomInBtnLoc);
			if(isClickable(testConfig, zoomInBtn)) {
				testConfig.logPass("Verified the zoom in button is clickable");
				Element.click(testConfig, zoomInBtn, "Zoom in button");
				Browser.wait(testConfig, 2);
			} else {
				testConfig.logFail("Failed to verify that the zoom in button is clickable");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that the zoom in button is clickable");
		}

		try {
			WebElement zoomOutBtn = Element.getPageElement(testConfig, How.xPath, zoomOutBtnLoc);
			if(isClickable(testConfig, zoomOutBtn)) {
				testConfig.logPass("Verified the zoom out button is clickable");
				Element.click(testConfig, zoomOutBtn, "Zoom out button");
				Browser.wait(testConfig, 2);
			} else {
				testConfig.logFail("Failed to verify that the zoom out button is clickable");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that the zoom out button is clickable");
		}
	}

	public void clickOnAmenityIconAndVerifyCardOpens(String amenityTitleOverCard, String salesCenterTitle) {

		checkForPromo();

		String amenityLocator = ".//div[contains(@class,'amenity-marker-container')]//div[contains(@class,'amenity-marker')]";
		List<WebElement> amentityPin = Element.getListOfElements(testConfig, How.xPath, amenityLocator);

		for (int i = 0; i < amentityPin.size(); i++) {
			try {
				if(isClickable(testConfig, amentityPin.get(i))) {
					Element.click(testConfig, amentityPin.get(i), "Amenity over map");
					break;
				}
			} catch (Exception e) {
				continue;
			}

		}

		Browser.wait(testConfig, 2);

		String amenityTitleLoc = ".//div[contains(@class,'shadow-pois')]//div[contains(@class,'relative')]/p[1]";
		WebElement amenityTitle = Element.getPageElement(testConfig, How.xPath, amenityTitleLoc);

		if(amenityTitle.getAttribute("innerText").trim().equals(amenityTitleOverCard) || amenityTitle.getAttribute("innerText").trim().equals(salesCenterTitle)) {
			testConfig.logPass("Title over card as " + amenityTitle.getAttribute("innerText").trim());
		} else {
			testConfig.logFail("Incorrect title displaying over the map");
		}

		String amenityNameLoc = ".//div[contains(@class,'shadow-pois')]//p[2]";
		WebElement amenityName = Element.getPageElement(testConfig, How.xPath, amenityNameLoc);

		if(amenityName.getAttribute("innerText").isEmpty()) {
			testConfig.logFail("Getting amenity name as blank... failing the test");
		} else {
			testConfig.logPass("Verified getting the amenity name as " + amenityName.getAttribute("innerText"));
		}

		String keyFeaturesLoc = ".//div[contains(@class,'shadow-pois')]//ul/li";
		List<WebElement> keyFeatures = Element.getListOfElements(testConfig, How.xPath, keyFeaturesLoc);

		if(keyFeatures.size() == 0) {
			testConfig.logComment("Not getting any key feature displaying with the amenity... skipping the verification");
		} else {
			for (int i = 0; i < keyFeatures.size(); i++) {
				testConfig.logPass("Verified the amenity key feature: " + (i + 1) + " as " + keyFeatures.get(i).getAttribute("innerText"));
			}
		}
	}

	public void verifyCollapsedCommunitySection() {

		checkForPromo();

		String divLoc = ".//div[contains(@class,'community-header')]//h2/../button";
		WebElement displayingSection = Element.getPageElement(testConfig, How.xPath, divLoc);

		if(displayingSection.getAttribute("aria-label").contains("Expand Header")) {
			testConfig.logPass("Verified the section is collapsed by default");
		} else {
			testConfig.logFail("Failed to verify that the section is collapsed by default");
		}

		Browser.wait(testConfig, 2);
		String commNameLoc = ".//h2[contains(@class,'text-white')][contains(@class,'line-clamp')]/../button";
		WebElement commName = Element.getPageElement(testConfig, How.xPath, commNameLoc);
		Element.click(testConfig, commName, "Community Name");

		Browser.wait(testConfig, 2);
		displayingSection = Element.getPageElement(testConfig, How.xPath, divLoc);

		if(displayingSection.getAttribute("aria-label").contains("Collapse Header")) {
			testConfig.logPass("Verified the section is displaying now after clicking the community name");
		} else {
			testConfig.logFail("Failed to verify that the section is displaying after clicking the community name");
		}

	}

	public void verifyCTAsAreDisplaying(CommunitySectionView communitySectionView) {

		switch (communitySectionView) {
		case Collapsed:
			checkForPromo();
			break;

		case Expanded:
			String commNameLoc = ".//h2[contains(@class,'text-white')][contains(@class,'line-clamp')]/../button";
			WebElement commName = Element.getPageElement(testConfig, How.xPath, commNameLoc);
			Element.click(testConfig, commName, "Community Name");
			Browser.wait(testConfig, 2);
			break;
		}

		try {
			String requestInfoLocator = ".//div[contains(@class,'community-header')]//div/a/parent::div/button";
			WebElement requestInfo = Element.getPageElement(testConfig, How.xPath, requestInfoLocator);
			if(requestInfo.isDisplayed()) {
				testConfig.logPass("Verified Request Information button is displaying correctly");
			} else {
				testConfig.logFail("Failed to verify that Request Information button is displaying correctly");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that Request Information button is displaying correctly");
		}

		try {
			String viewCommPageLocator = ".//div[contains(@class,'community-header')]//div/a";
			WebElement viewCommPage = Element.getPageElement(testConfig, How.xPath, viewCommPageLocator);
			if(viewCommPage.isDisplayed()) {
				testConfig.logPass("Verified View Community Page CTA is displaying correctly");
			} else {
				testConfig.logFail("Failed to verify that View Community Page CTA is displaying correctly");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that View Community Page CTA is displaying correctly");
		}
	}

	public void verifyCommunitySectionDetails(String text) throws UnsupportedEncodingException {

		String commNameLoc = ".//h2[contains(@class,'text-white')][contains(@class,'line-clamp')]";
		WebElement commName = Element.getPageElement(testConfig, How.xPath, commNameLoc);
		Helper.compareEquals(testConfig, "Community Name", text, commName.getAttribute("innerText"));

		String imageLoc = ".//div[contains(@class,'community-header')]//img";
		WebElement imageElement = Element.getPageElement(testConfig, How.xPath, imageLoc);
		String imageSrc = Element.getAttribute(testConfig, imageElement, "src", "Image source");

		verifyURLAsPerDomain(imageSrc);

		Browser.wait(testConfig, 2);
		String breadcrumbItemsLoc = ".//nav[@aria-label='breadcrumbs']//a/span[@class='label nav-capitalize']";
		List<WebElement> allCrumbs = Element.getListOfElements(testConfig, How.xPath, breadcrumbItemsLoc);

		if(allCrumbs.size() == 0) {
			testConfig.logFail("Not getting any breadcrumb data displaying.. failing the scenario");
		} else {
			for (int i = 0; i < allCrumbs.size(); i++) {
				testConfig.logPass("Verified breadcrumb " + (i + 1) + " displaying as: " + allCrumbs.get(i).getText());
				/*
				 * String titleCase = convertToTitleCase(allCrumbs.get(i).getText());
				 * if(allCrumbs.get(i).getText() == titleCase) {
				 * testConfig.logPass("Verified breadcrumb value: " + titleCase +
				 * " is displaying in Title case"); } else {
				 * testConfig.logFail("Failed to verify that breadcrumb value: " + titleCase +
				 * " is displaying in Title case"); }
				 */
			}
		}

		Browser.wait(testConfig, 2);
		String headlineTextLoc = ".//div[contains(@class,'community-header')]//div[contains(@class,'transition-all')]/p";
		WebElement headlineText = Element.getPageElement(testConfig, How.xPath, headlineTextLoc);

		if(headlineText.getText().isEmpty()) {
			testConfig.logFail("Not getting headline text displaying.. failing the scenario");
		} else {
			testConfig.logPass("Verified headline text is displaying as: " + headlineText.getText());
		}

		String homeTypeDataLoc = "(.//div[contains(@class,'community-header')]//div[contains(@class,'type-brds-v2-sm-400')])[1]/span";
		List<WebElement> homeTypeData = Element.getListOfElements(testConfig, How.xPath, homeTypeDataLoc);

		if(homeTypeData.size() == 0) {
			testConfig.logFail("Not getting any home type data displaying.. failing the scenario");
		} else {
			for (int i = 0; i < homeTypeData.size(); i++) {
				testConfig.logPass("Verified Home Type data " + (i + 1) + " displaying as: " + homeTypeData.get(i).getText());
			}
		}

		Browser.wait(testConfig, 2);
		String amenitiesLoc = "(.//div[contains(@class,'community-header')]//div[contains(@class,'type-brds-v2-sm-400')])[2]/span";
		WebElement amenities = Element.getPageElement(testConfig, How.xPath, amenitiesLoc);

		if(amenities.getText().isEmpty()) {
			testConfig.logFail("Not getting amenities text displaying.. failing the scenario");
		} else {
			testConfig.logPass("Verified amenities text is displaying as: " + amenities.getText());
		}

	}

	public void applyAnotherSearchAndVerifyBackButton(String text, String expectedSearch) {

		Browser.wait(testConfig, 5);

		checkForPromo();

		try {
			String backBtnLoc = "//div[contains(@class,'community-header')]/div/button";
			WebElement backBtn = Element.getPageElement(testConfig, How.xPath, backBtnLoc);
			if(backBtn.isDisplayed()) {
				testConfig.logPass("Verified that initially back button is displaying");
			} else {
				testConfig.logFail("Failed to verify that back button is displaying when searching first community");
			}
		} catch (Exception e) {
			testConfig.logPass("Verified that now no back button is displaying");
		}

		Browser.wait(testConfig, 5);
		WebElement searchInput = Element.getPageElement(testConfig, How.xPath, ".//input[contains(@class,'fyh-input')][@aria-labelledby='Search']");
		Element.click(testConfig, searchInput, "Search input field");
		Browser.wait(testConfig, 2);

		WebElement clearSearch = Element.getPageElement(testConfig, How.xPath, ".//button[@aria-labelledby='ClearSearch']");
		Element.click(testConfig, clearSearch, "Clear search input field");
		Browser.wait(testConfig, 2);

		WebElement inputSearch = Element.getPageElement(testConfig, How.css, "input[class*='fyh-input'][aria-labelledby='Search']");
		Element.enterData(testConfig, inputSearch, text.substring(0, text.length() / 2), "Search value");
		Browser.wait(testConfig, 2);
		Element.enterDataWithoutClear(testConfig, inputSearch, text.substring((text.length() / 2), text.length()),
				"Search value");
		Browser.wait(testConfig, 4);

		List<WebElement> suggestionList = Element.getListOfElements(testConfig, How.xPath,
				".//li[contains(@class,'srch-items')]/div[2]");
		if(suggestionList.size() == 0) {
			testConfig.driver.navigate().refresh();
			inputSearch = Element.getPageElement(testConfig, How.css,
					".//input[contains(@class,'fyh-input')][@aria-labelledby='Search']");
			Element.enterDataAfterClick(testConfig, inputSearch, text.substring(0, text.length() / 2), "Search value");
			Browser.wait(testConfig, 2);
			Element.enterDataWithoutClear(testConfig, inputSearch, text.substring((text.length() / 2), text.length()),
					"Search value");
			Browser.wait(testConfig, 4);

			suggestionList = Element.getListOfElements(testConfig, How.xPath,
					".//li[contains(@class,'srch-items')]/div[2]");
		}

		for (WebElement webElement : suggestionList) {
			if (expectedSearch.equals(webElement.getText())) {
				Element.click(testConfig, webElement, webElement.getText() + " suggestion");
				break;
			}
		}

		Browser.waitWithoutLogging(testConfig, 5);

		WebElement communitiesTab = Element.getPageElement(testConfig, How.xPath, ".//div[@role='tablist']/button/span[text()='communities']");
		Element.click(testConfig, communitiesTab, "Communities Tab");
		Browser.waitWithoutLogging(testConfig, 3);

		String communityName = "Kissing Tree";

		List<WebElement> communityMapLinks = Element.getListOfElements(testConfig, How.xPath,
				".//h3[contains(text(),'" + communityName + "')]//ancestor::div[contains(@class,'flex-wrap info')]//button/span");
		if (isClickable(testConfig, communityMapLinks.get(0))) {
			Element.clickThroughJS(testConfig, communityMapLinks.get(0),
					"View Community Map link for Community item");
		} else {
			Element.clickThroughJS(testConfig, communityMapLinks.get(1),
					"View Community Map link for Community item");
		}

		Browser.wait(testConfig, 5);

		try {
			String backBtnLoc = "//div[contains(@class,'community-header')]/div/button";
			WebElement backBtn = Element.getPageElement(testConfig, How.xPath, backBtnLoc);
			if(backBtn.isDisplayed()) {
				testConfig.logPass("Getting back button displaying now");
			} else {
				testConfig.logFail("Failed to verify that back button is displaying now after searching another community");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that back button is displaying now after searching another community");
		}

	}

	public void verifyHomesAndPlansCheckboxAndCount() {

		String labels[] = {"Homes", "Plans"};
		String checkboxBtnLocator = ".//button[contains(@class,'border-brp-blue-200')]";
		String homePlanTextLocator = ".//button[contains(@class,'border-brp-blue-200')]/parent::div/div[contains(@class,'font-light')]";
		String countLocator = ".//button[contains(@class,'border-brp-blue-200')]/parent::div/div[contains(@class,'font-light')]/span";

		List<WebElement> checkboxBtns = Element.getListOfElements(testConfig, How.xPath, checkboxBtnLocator);
		List<WebElement> homePlanTextContent = Element.getListOfElements(testConfig, How.xPath, homePlanTextLocator);
		@SuppressWarnings("unused")
		List<WebElement> countContent = Element.getListOfElements(testConfig, How.xPath, countLocator);

		for (int i = 0; i < checkboxBtns.size(); i++) {
			try {
				WebElement checkbox = checkboxBtns.get(i);
				if(checkbox.isDisplayed()) {
					testConfig.logPass("Verified checkbox for " + labels[i] + " is displaying as expected");
				} else {
					testConfig.logFail("Failed to verify checkbox for " + labels[i] + " is displaying as expected");
				}
			} catch (Exception e) {
				testConfig.logFail("Failed to verify checkbox for " + labels[i] + " is displaying as expected");
			}
		}

		for (int i = 0; i < homePlanTextContent.size(); i++) {
			WebElement homePlanContent = homePlanTextContent.get(i);

			if (!homePlanContent.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Verified content for " + labels[i] + " is displaying as "
						+ homePlanContent.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting content for " + labels[i] + " is displaying as blank... failing the case");
			}
		}
	}

	public void verifyNeighborhoodPillsView() throws UnsupportedEncodingException {

		String imageLocator = ".//button[contains(@class,'rounded-full')][contains(@class,'border-brp-blue-gray-200')]/div/img";
		String neighborhoodNameLocator = ".//button[contains(@class,'rounded-full')][contains(@class,'border-brp-blue-gray-200')]//div/p[contains(@class,'font-bold')]";
		String homeTypeLocator = ".//button[contains(@class,'rounded-full')][contains(@class,'border-brp-blue-gray-200')]//div[contains(@class,'items-center')]/p";

		String neighborhoodPillsLoc = ".//button[contains(@class,'rounded-full')][contains(@class,'border-brp-blue-gray-200')]";
		List<WebElement> allNeighborhoodPills = Element.getListOfElements(testConfig, How.xPath, neighborhoodPillsLoc);

		for (int i = 0; i < allNeighborhoodPills.size(); i++) {

			List<WebElement> allImages = Element.getListOfElements(testConfig, How.xPath, imageLocator);
			String thumbnailImageSrc = Element.getAttribute(testConfig, allImages.get(i), "src", "Thumbnail image");

			if (!thumbnailImageSrc.isEmpty()) {
				verifyURLAsPerDomain(thumbnailImageSrc);
			} else {
				testConfig.logFail("Getting Larger image source displaying over card as blank... failing the scenario");
			}

			List<WebElement> allNeighborhoodNames = Element.getListOfElements(testConfig, How.xPath, neighborhoodNameLocator);
			if(!allNeighborhoodNames.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Neighborhood Name displaying over pill " + (i + 1) + " as " + allNeighborhoodNames.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Neighborhood Name displaying over pill as blank... failing the scenario");
			}

			List<WebElement> allHomeTypes = Element.getListOfElements(testConfig, How.xPath, homeTypeLocator);
			if(!allHomeTypes.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Home Type displaying over pill " + (i + 1) + " as " + allHomeTypes.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Home Type displaying over card value as blank... failing the scenario");
			}
		}
	}

	public void verifyRightLeftNavigationForNeighborhoodPills() {

		checkForPromo();
		String leftButtonNavigation = "(.//button[contains(@class,'text-brp-blue-100')])[1]";
		String rightButtonNavigation = "(.//button[contains(@class,'text-brp-blue-100')])[2]";

		String neighborhoodPillsLoc = ".//button[contains(@class,'rounded-full')][contains(@class,'border-brp-blue-gray-200')]";
		List<WebElement> allNeighborhoodPills = Element.getListOfElements(testConfig, How.xPath, neighborhoodPillsLoc);

		if(allNeighborhoodPills.size() > 2) {
			verifyRightNavigationalBtnIsClickable(leftButtonNavigation, rightButtonNavigation);
			WebElement rightButton = Element.getPageElement(testConfig, How.xPath, rightButtonNavigation);
			for (int i = 2; i <= allNeighborhoodPills.size(); i++) {
				Element.click(testConfig, rightButton, "Right button");
				Browser.wait(testConfig, 1);
			}
			verifyLeftNavigationalBtnIsClickable(leftButtonNavigation, rightButtonNavigation);
		}
	}

	private void verifyLeftNavigationalBtnIsClickable(String leftButtonNavigation, String rightButtonNavigation) {

		WebElement leftButton = Element.getPageElement(testConfig, How.xPath, leftButtonNavigation);
		if(isClickable(testConfig, leftButton)) {
			testConfig.logPass("Verified that the left button is clickable now");
		} else {
			testConfig.logFail("Failed to verify that the left button is clickable");
		}
	}

	private void verifyRightNavigationalBtnIsClickable(String leftButtonNavigation, String rightButtonNavigation) {

		WebElement rightButton = Element.getPageElement(testConfig, How.xPath, rightButtonNavigation);
		if(isClickable(testConfig, rightButton)) {
			testConfig.logPass("Verified that the right button is clickable now");
		} else {
			testConfig.logFail("Failed to verify that the right button is clickable");
		}
	}

	public void selectQMICardAndVerifyCardDetails() {

		checkForPromo();

		String qmiNeighborhoodNameLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced at')])[1]/ancestor::*[contains(@class,'card-container')]//span[contains(@class,'car-whitespace-nowrap')])[1]";
		WebElement qmiNeighborhoodNameElement = Element.getPageElement(testConfig, How.xPath, qmiNeighborhoodNameLoc);
		String qmiNeighborhoodName = Element.getAttribute(testConfig, qmiNeighborhoodNameElement, "innerText", "Neighborhood for the QMI card");

		String qmiNameLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced at')])[1]/ancestor::*[contains(@class,'card-container')]//span[contains(@class,'card-title')])";
		WebElement qmiNameElement = Element.getPageElement(testConfig, How.xPath, qmiNameLoc);
		String qmiFullName = Element.getAttribute(testConfig, qmiNameElement, "innerText", "QMI name over the QMI card");

		String qmiName = "";
		String[] array = qmiFullName.split(",");
		if(array.length == 2) {
			qmiName = array[0].concat(",").concat(array[1]);
		} else {
			qmiName = array[0];
		}

		String qmiAddressLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced at')])[1]/ancestor::*[contains(@class,'card-container')]//span[contains(@class,'card-subtitle')])";
		WebElement qmiAddressElement = Element.getPageElement(testConfig, How.xPath, qmiAddressLoc);
		String qmiAddress = Element.getAttribute(testConfig, qmiAddressElement, "innerText", "Address over the QMI card");

		/*String qmiHomeTypeLoc = "((.//div[contains(@class,'font-regular')][contains(text(),'Priced at')])[1]/ancestor::div[contains(@class,'card-content')]//div[not(contains(@class,'items-baseline'))]/span[contains(@class,'text-sm')])[1]";
		WebElement qmiHomeTypeElement = Element.getPageElement(testConfig, How.xPath, qmiHomeTypeLoc);
		String qmiHomeType = Element.getAttribute(testConfig, qmiHomeTypeElement, "innerText", "Home Type over the QMI card");
		 */
		String qmiSquareFootageLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced at')])[1]/ancestor::*[contains(@class,'card-container')]//div[contains(@class,'specs-container')]//span[contains(@class,'car-type-brds')])[2]";
		WebElement qmiSquareFootageElement = Element.getPageElement(testConfig, How.xPath, qmiSquareFootageLoc);
		String qmiSquareFootage = Element.getAttribute(testConfig, qmiSquareFootageElement, "innerText", "Square Footage over the QMI card");

		String qmiBedroomLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced at')])[1]/ancestor::*[contains(@class,'card-container')]//div[contains(@class,'specs-container')]//span[contains(@class,'car-type-brds')])[3]";
		WebElement qmiBedroomElement = Element.getPageElement(testConfig, How.xPath, qmiBedroomLoc);
		String qmiBedroom = Element.getAttribute(testConfig, qmiBedroomElement, "innerText", "Bedroom over the QMI card");

		String qmiBathroomLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced at')])[1]/ancestor::*[contains(@class,'card-container')]//div[contains(@class,'specs-container')]//span[contains(@class,'car-type-brds')])[4]";
		WebElement qmiBathroomElement = Element.getPageElement(testConfig, How.xPath, qmiBathroomLoc);
		String qmiBathroom = Element.getAttribute(testConfig, qmiBathroomElement, "innerText", "Bathroom over the QMI card");

		String qmiPriceLoc = "(.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced at')])[1]/parent::div/div[2]";
		WebElement qmiPriceElement = Element.getPageElement(testConfig, How.xPath, qmiPriceLoc);
		String qmiPrice = Element.getAttribute(testConfig, qmiPriceElement, "innerText", "Price over the QMI card");

		Browser.wait(testConfig, 3);
		try {
			Element.clickThroughJS(testConfig, qmiPriceElement, "QMI card");
		} catch (Exception e) {
			Element.click(testConfig, qmiPriceElement, "QMI card");
		}
		Browser.wait(testConfig, 2);

		String qmiNameLocator = ".//div[contains(@class,'w-full h-full')]//div[contains(@class,'type-brds-v2-lg-700')]/span[1]";
		WebElement qmi = Element.getPageElement(testConfig, How.xPath, qmiNameLocator);
		String flyoutQMIName = Element.getText(testConfig, qmi, "QMI Name over map card flyout");

		// changed to below - String mapFlyoutNeighborhoodNameLoc = ".//div[contains(@class,'w-full h-full')]//div[contains(@class,'text-brp-blue-400')]/span[1]";
		String mapFlyoutNeighborhoodNameLoc = ".//div[contains(@class,'w-full h-full')]//div/div[contains(@class,'justify-start space-x-1')]/span[1]";
		WebElement mapFlyoutNeighborhoodNameElement = Element.getPageElement(testConfig, How.xPath, mapFlyoutNeighborhoodNameLoc);
		String mapFlyoutNeighborhoodName = Element.getAttribute(testConfig, mapFlyoutNeighborhoodNameElement, "innerText", "Neighborhood name over the QMI card over map flyout");

		String mapFlyoutQmiAddressLocator = ".//div[contains(@class,'w-full h-full')]//div[contains(@class,'type-brds-v2-lg-700')]/span[2]";
		WebElement mapFlyoutQmiAddress = Element.getPageElement(testConfig, How.xPath, mapFlyoutQmiAddressLocator);
		String flyoutQMIAddress = Element.getAttribute(testConfig, mapFlyoutQmiAddress, "innerText", "QMI Address over map card flyout");

		String mapFlyoutQmiSquareFootageLocator = "(.//div[contains(@class,'w-full h-full')]//*[contains(@class,'space-x-1')])[2]/span";
		WebElement mapFlyoutQmiSquareFootage = Element.getPageElement(testConfig, How.xPath, mapFlyoutQmiSquareFootageLocator);
		String flyoutQMISquareFootage = Element.getAttribute(testConfig, mapFlyoutQmiSquareFootage, "innerText", "QMI Square Footage over map card flyout");
		int value = flyoutQMISquareFootage.lastIndexOf(" ");
		String finalFlyOutSqFtValue = flyoutQMISquareFootage.substring(0, value).concat(flyoutQMISquareFootage.substring(value + 1, flyoutQMISquareFootage.length()));

		String mapFlyoutQmiBedroomLocator = "(.//div[contains(@class,'w-full h-full')]//*[contains(@class,'space-x-1')])[3]/div";
		WebElement mapFlyoutQmiBedroom = Element.getPageElement(testConfig, How.xPath, mapFlyoutQmiBedroomLocator);
		String flyoutQMIBedroom = Element.getAttribute(testConfig, mapFlyoutQmiBedroom, "innerText", "QMI Bedroom over map card flyout");

		String mapFlyoutQmiBathroomLocator = "(.//div[contains(@class,'w-full h-full')]//*[contains(@class,'space-x-1')])[4]";
		WebElement mapFlyoutQmiBathroom = Element.getPageElement(testConfig, How.xPath, mapFlyoutQmiBathroomLocator);
		String flyoutQMIBathroom = Element.getAttribute(testConfig, mapFlyoutQmiBathroom, "innerText", "QMI Bathroom over map card flyout");

		String mapFlyoutQmiPriceLocator = ".//div[contains(@class,'w-full h-full')]//p[contains(@class,'type-brds-v2-lg-700')]";
		WebElement mapFlyoutQmiPrice = Element.getPageElement(testConfig, How.xPath, mapFlyoutQmiPriceLocator);
		String flyoutQMIPrice = Element.getAttribute(testConfig, mapFlyoutQmiPrice, "innerText", "QMI Price over map card flyout");

		Helper.compareEquals(testConfig, "QMI name over left trail card and map card flyout", qmiName.trim(), flyoutQMIName.trim());

		if(qmiNeighborhoodName.toLowerCase().contains(mapFlyoutNeighborhoodName.toLowerCase())) {
			testConfig.logPass("Verified 'QMI Neighborhood name over left trail card and map card flyout' as :-'" + mapFlyoutNeighborhoodName + "'");
		} else {
			testConfig.logFail("Expected 'QMI Neighborhood name over left trail card and map card flyout' was :-'" + qmiNeighborhoodName + "'. But actual is '" + mapFlyoutNeighborhoodName + "'");
		}

		Helper.compareEquals(testConfig, "QMI Address over left trail card and map card flyout", qmiAddress.trim(), flyoutQMIAddress.trim());
		Helper.compareEquals(testConfig, "QMI Square Footage over left trail card and map card flyout", qmiSquareFootage.trim().replace(",", ""), finalFlyOutSqFtValue.trim().replace(",", ""));
		Helper.compareEquals(testConfig, "QMI Bedroom over left trail card and map card flyout", qmiBedroom.replace("Bed","").trim(), flyoutQMIBedroom.trim());
		Helper.compareEquals(testConfig, "QMI Bathroom over left trail card and map card flyout", qmiBathroom.replace("Bath","").trim(), flyoutQMIBathroom.trim());
		Helper.compareEquals(testConfig, "QMI Price over left trail card and map card flyout", qmiPrice.trim(),	flyoutQMIPrice.replace(" ", "").trim());

	}

	public void selectPlanCardAndVerifyCardDetails() {

		checkForPromo();

		WebElement plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[text()='floor plans']");
		Element.click(testConfig, plansTab, "Plans tab");
		Browser.wait(testConfig, 5);

		String planLocator = "(.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced from')])[1]";
		WebElement planCard = Element.getPageElement(testConfig, How.xPath, planLocator);

		String planNeighborhoodNameLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced from')])[1]/ancestor::*[contains(@class,'card-container')]//span[contains(@class,'car-whitespace-nowrap')])[1]";
		WebElement planNeighborhoodNameElement = Element.getPageElement(testConfig, How.xPath, planNeighborhoodNameLoc);
		String planNeighborhoodName = Element.getAttribute(testConfig, planNeighborhoodNameElement, "innerText", "Neighborhood for the Plan card");

		String floorPlanLabelLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced from')])[1]/ancestor::*[contains(@class,'card-container')]//span[contains(@class,'car-whitespace-nowrap')])[2]";
		WebElement floorPlanLabelElement = Element.getPageElement(testConfig, How.xPath, floorPlanLabelLoc);
		String floorPlanLabel = Element.getAttribute(testConfig, floorPlanLabelElement, "innerText", "Floor Plan label for the Plan card");

		String planNameLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced from')])[1]/ancestor::*[contains(@class,'card-container')]//span[contains(@class,'car-whitespace-nowrap')])[3]";
		WebElement planNameElement = Element.getPageElement(testConfig, How.xPath, planNameLoc);
		String planName = Element.getAttribute(testConfig, planNameElement, "innerText", "Plan name over the Plan card");

		String planSquareFootageLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced from')])[1]/ancestor::*[contains(@class,'card-container')]//div[contains(@class,'specs-container')]//span[contains(@class,'car-type-brds')])[2]";
		WebElement planSquareFootageElement = Element.getPageElement(testConfig, How.xPath, planSquareFootageLoc);
		String planSquareFootage = Element.getAttribute(testConfig, planSquareFootageElement, "innerText", "Square Footage over the Plan card");

		String temp = planSquareFootage.split("ft")[0].replace(",", "").replace(" ", "");
		String temp2 = planSquareFootage.substring(planSquareFootage.indexOf("ft"), planSquareFootage.length());
		String formattedPlanSqFt = temp.concat(" ").concat(temp2);

		String planBedroomLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced from')])[1]/ancestor::*[contains(@class,'card-container')]//div[contains(@class,'specs-container')]//span[contains(@class,'car-type-brds')])[3]";
		WebElement planBedroomElement = Element.getPageElement(testConfig, How.xPath, planBedroomLoc);
		String planBedroom = Element.getAttribute(testConfig, planBedroomElement, "innerText", "Bedroom over the Plan card");

		String planBathroomLoc = "((.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced from')])[1]/ancestor::*[contains(@class,'card-container')]//div[contains(@class,'specs-container')]//span[contains(@class,'car-type-brds')])[4]";
		WebElement planBathroomElement = Element.getPageElement(testConfig, How.xPath, planBathroomLoc);
		String planBathroom = Element.getAttribute(testConfig, planBathroomElement, "innerText", "Bathroom over the Plan card");

		String planPriceLoc = "(.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced from')])[1]/parent::div/div[2]";
		WebElement planPriceElement = Element.getPageElement(testConfig, How.xPath, planPriceLoc);
		String planPrice = Element.getAttribute(testConfig, planPriceElement, "innerText", "Price over the Plan card");

		Browser.wait(testConfig, 5);
		try {
			Element.clickThroughJS(testConfig, planCard, "Plan card");
		} catch (Exception e) {
			Element.click(testConfig, planCard, "Plan card");
		}
		Browser.wait(testConfig, 2);

		String mapFlyoutNeighborhoodNameLoc = ".//div[contains(@class,'w-full h-full')]//div[contains(@class,'type-brds-v2-xs-500')]/span[1]";
		WebElement mapFlyoutNeighborhoodNameElement = Element.getPageElement(testConfig, How.xPath, mapFlyoutNeighborhoodNameLoc);
		String mapFlyoutNeighborhoodName = Element.getAttribute(testConfig, mapFlyoutNeighborhoodNameElement, "innerText", "Neighborhood name over the Plan card over map flyout");

		String mapFlyoutFloorPlanLabelLoc = ".//div[contains(@class,'w-full h-full')]//span[@class='text-brp-orange-100']/../span[1]";
		WebElement mapFlyoutPlanFloorElement = Element.getPageElement(testConfig, How.xPath, mapFlyoutFloorPlanLabelLoc);
		String mapFlyoutPlanLabel = Element.getAttribute(testConfig, mapFlyoutPlanFloorElement, "innerText", "Plan name over the Plan card over map flyout");

		String mapFlyoutPlanNameLocator = ".//div[contains(@class,'w-full h-full')]//span[@class='text-brp-orange-100']/../span[2]";
		WebElement mapFlyoutPlanName = Element.getPageElement(testConfig, How.xPath, mapFlyoutPlanNameLocator);
		String flyoutPlanName = Element.getAttribute(testConfig, mapFlyoutPlanName, "innerText", "Plan Name over map card flyout");

		String mapFlyoutPlanSquareFootageLocator = "(.//div[contains(@class,'w-full h-full')]//div[contains(@class,'text-xs')]//*[contains(@class,'space-x-1')])[1]/span";
		WebElement mapFlyoutQmiSquareFootage = Element.getPageElement(testConfig, How.xPath, mapFlyoutPlanSquareFootageLocator);
		String flyoutPlanSquareFootage = Element.getAttribute(testConfig, mapFlyoutQmiSquareFootage, "innerText", "Plan Square Footage over map card flyout");
		int value = flyoutPlanSquareFootage.lastIndexOf(" ");
		String finalFlyOutSqFtValue = flyoutPlanSquareFootage.substring(0, value).concat(flyoutPlanSquareFootage.substring(value + 1, flyoutPlanSquareFootage.length()));

		String mapFlyoutPlanBedroomLocator = "(.//div[contains(@class,'w-full h-full')]//div[contains(@class,'justify-start')]/*[contains(@class,'space-x-1')])[2]/div";
		WebElement mapFlyoutPlanBedroom = Element.getPageElement(testConfig, How.xPath, mapFlyoutPlanBedroomLocator);
		String flyoutPlanBedroom = Element.getAttribute(testConfig, mapFlyoutPlanBedroom, "innerText", "Plan Bedroom over map card flyout");

		Browser.wait(testConfig, 2);
		String mapFlyoutPlanBathroomLocator = "(.//div[contains(@class,'w-full h-full')]//div[contains(@class,'justify-start')]/*[contains(@class,'space-x-1')])[3]/div/div[1]";
		WebElement mapFlyoutPlanBathroom = Element.getPageElement(testConfig, How.xPath, mapFlyoutPlanBathroomLocator);
		String flyoutPlanBathroom = Element.getAttribute(testConfig, mapFlyoutPlanBathroom, "innerText", "Plan Bathroom over map card flyout");

		String mapFlyoutPlanPriceLocator = ".//div[contains(@class,'w-full h-full')]//span[contains(text(),'Priced from')]/ancestor::div[contains(@class,'flex-col')]/span[2]";
		WebElement mapFlyoutPlanPrice = Element.getPageElement(testConfig, How.xPath, mapFlyoutPlanPriceLocator);
		String flyoutPlanPrice = Element.getAttribute(testConfig, mapFlyoutPlanPrice, "innerText", "Plan Price over map card flyout");

		Helper.compareEquals(testConfig, "Plan name over left trail card and map card flyout", planName.trim(), flyoutPlanName.trim());
		Helper.compareContains(testConfig, "Plan Neighborhood name over left trail card and map card flyout", mapFlyoutNeighborhoodName.trim(), planNeighborhoodName.toUpperCase().trim());
		Helper.compareEquals(testConfig, "Plan Floor Plan label over left trail card and map card flyout", floorPlanLabel.trim(), mapFlyoutPlanLabel.trim());
		Helper.compareEquals(testConfig, "Plan Square Footage over left trail card and map card flyout", formattedPlanSqFt, finalFlyOutSqFtValue.trim());
		Helper.compareEquals(testConfig, "Plan Bedroom over left trail card and map card flyout", planBedroom.replace("Bed","").trim(), flyoutPlanBedroom.trim());
		Helper.compareEquals(testConfig, "Plan Bathroom over left trail card and map card flyout", planBathroom.replace("Bath","").trim(), flyoutPlanBathroom.trim());
		Helper.compareEquals(testConfig, "Plan Price over left trail card and map card flyout", planPrice.trim(), flyoutPlanPrice.trim());

	}

	public void checkForPromo() {
		try {
			String promoCancelBtn = ".//div[contains(@class,'promo-flex')]/div/button/div";
			WebElement cancelBtn = Element.getPageElement(testConfig, How.xPath, promoCancelBtn);
			Element.click(testConfig, cancelBtn, "Cancel button");
		} catch (Exception e) {
			testConfig.logComment("No promo section displayed");
		}
	}

	public void submitFormOverQMICardFlyout(String leadForm) {

		checkForPromo();

		String qmiLocator = "(.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced at')])[1]";
		WebElement qmiCard = Element.getPageElement(testConfig, How.xPath, qmiLocator);
		Browser.wait(testConfig, 3);
		Element.clickThroughJS(testConfig, qmiCard, "QMI card");
		Browser.wait(testConfig, 2);

		String requestInfoBtnLocator = ".//button[contains(text(),'Request Info')]";
		WebElement requestInfoBtn = Element.getPageElement(testConfig, How.xPath, requestInfoBtnLocator);
		if(requestInfoBtn == null) {
			requestInfoBtnLocator = ".//div[contains(@class,'w-full h-full')]//button[contains(@class,'primary')]/span/..";
			requestInfoBtn = Element.getPageElement(testConfig, How.xPath, requestInfoBtnLocator);
			Element.clickThroughJS(testConfig, requestInfoBtn, "Take a Tour button over QMI card flyout");
			verifyFormSubmission(DifferentFormTypes.OnsiteTour, leadForm);
		} else {
			Element.clickThroughJS(testConfig, requestInfoBtn, "Request Info button over QMI card flyout");
			submitFormAndVerifyMsg(leadForm, "");
		}

	}

	public void submitFormOverPlanCardFlyout(DifferentFormTypes formTypes, String leadForm) {

		checkForPromo();

		WebElement plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[text()='floor plans']");
		Element.click(testConfig, plansTab, "Plans tab");
		Browser.wait(testConfig, 5);

		String planLocator = "(.//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced from')])[1]";
		WebElement planCard = Element.getPageElement(testConfig, How.xPath, planLocator);
		Browser.wait(testConfig, 3);
		try {
			Element.click(testConfig, planCard, "Plan Card");
		} catch (Exception e) {
			Element.clickThroughJS(testConfig, planCard, "Plan card");
		}
		Browser.wait(testConfig, 2);

		String takeATourBtnLocator = ".//div[contains(@class,'w-full h-full')]//button[contains(@class,'btn primary')]";
		WebElement takeATourBtn = Element.getPageElement(testConfig, How.xPath, takeATourBtnLocator);
		if(takeATourBtn == null) {
			takeATourBtnLocator = ".//button[contains(text(),'Request Info')]";
			takeATourBtn = Element.getPageElement(testConfig, How.xPath, takeATourBtnLocator);
			Element.clickThroughJS(testConfig, takeATourBtn, "Request Information button over Plan card flyout");
			submitFormAndVerifyMsg(leadForm, "");
		} else {
			Element.clickThroughJS(testConfig, takeATourBtn, "Take a Tour button over Plan card flyout");
			verifyFormSubmission(formTypes, leadForm);
		}

	}


	public void verifyFormSubmission(DifferentFormTypes differentFormTypes, String leadForm) {

		switch (differentFormTypes) {
		case OnsiteTour:
			WebElement onsiteTour = Element.getPageElement(testConfig, How.xPath,
					".//span[text()='Onsite Tour with our Team']");
			Element.click(testConfig, onsiteTour, "Onsite Tour with our Team form");
			break;

		case RequestInformation:
			WebElement reqInfoBtn = Element.getPageElement(testConfig, How.xPath,
					".//span[text()='Request Info']");
			Element.click(testConfig, reqInfoBtn, "Onsite Tour with our Team form");
			break;

		case VideoChat:
			WebElement videoChat = Element.getPageElement(testConfig, How.xPath,
					".//span[text()='Video Chat Tour with our Team']");
			Element.click(testConfig, videoChat, "Video Chat Tour with our Team form");
			break;
		}

		Browser.waitWithoutLogging(testConfig, 2);

		String daysToVisit[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
		String timeOfDay[] = { "Morning", "Afternoon", "Evening" };

		Random rd = new Random();
		String preferredDay = daysToVisit[rd.nextInt(daysToVisit.length)];
		String preferredTime = timeOfDay[rd.nextInt(timeOfDay.length)];

		String firstName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String lastName = "Test_" + Helper.generateRandomAlphabetsString(10);
		String emailAddress = "Test_" + Helper.generateRandomAlphabetsString(5) + "@gmail.com";
		Long phoneNo = Helper.generateRandomNumber(10);

		WebElement firstNameField = null, lastNameField = null, emailAddressField = null, phoneField = null,
				preferredDayToVisitField = null, preferredTimeOfDayField = null, consentCheckbox = null,
				submitBtn = null;

		switch (differentFormTypes) {
		case OnsiteTour:
			String[] additionalMsg1 = {"We have successfully received your message. "
					+ "A team member will reach out to you to confirm your tour details "
					+ "as soon as possible. Be sure to check your inbox for a confirmation email.",
			"You may close this window." };

			firstNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//input[@placeholder='Your First Name']");
			lastNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//input[@placeholder='Your Last Name']");
			emailAddressField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//input[@placeholder='email@domain.com']");
			phoneField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//input[@placeholder='(XXX) XXX-XXXX']");
			preferredDayToVisitField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//select[@data-sc-field-name='Preferred Day']");
			preferredTimeOfDayField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//select[@data-sc-field-name='Preferred Time']");

			Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
			Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
			Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");
			Element.enterData(testConfig, phoneField, String.valueOf(phoneNo), "Phone Number field value");

			Element.selectVisibleText(testConfig, preferredDayToVisitField, preferredDay,
					"Preferred Day to Visit field value");
			Element.selectVisibleText(testConfig, preferredTimeOfDayField, preferredTime,
					"Preferred Time Of Day field value");

			consentCheckbox = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//input[@data-sc-field-name='Terms and Conditions']");
			Element.click(testConfig, consentCheckbox, "Consent checkbox");

			submitBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='onsite-tour']//button[@value='Submit']");
			Element.click(testConfig, submitBtn, "Submit button");
			Browser.wait(testConfig, 2);
			verifySuccessMessageRequestInfoForm(firstName, additionalMsg1);
			createExcelFileAndWriteEmailAddress(emailAddress, leadForm);
			break;

		case RequestInformation:
			String[] additionalMsg2 = {"We have successfully received your message. "
					+ "A team member will reach out to assist you as soon as possible. "
					+ "Be sure to check your inbox for a confirmation email.",
			"You may close this window." };

			firstNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//input[@placeholder='Your First Name']");
			lastNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//input[@placeholder='Your Last Name']");
			emailAddressField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//input[@placeholder='email@domain.com']");
			phoneField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//input[@placeholder='(XXX) XXX-XXXX']");

			Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
			Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
			Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");
			Element.enterData(testConfig, phoneField, String.valueOf(phoneNo), "Phone Number field value");

			consentCheckbox = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//input[@data-sc-field-name='Terms and Conditions']");
			Element.click(testConfig, consentCheckbox, "Consent checkbox");

			submitBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='request-information']//button[@value='Submit']");
			Element.click(testConfig, submitBtn, "Submit button");
			Browser.wait(testConfig, 2);
			verifySuccessMessageRequestInfoForm(firstName, additionalMsg2);
			createExcelFileAndWriteEmailAddress(emailAddress, leadForm);
			break;

		case VideoChat:
			String[] additionalMsg3 = {"We have successfully received your message. "
					+ "A team member will reach out to you to confirm your tour details "
					+ "as soon as possible. Be sure to check your inbox for a confirmation email.",
			"You may close this window." };

			firstNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//input[@placeholder='Your First Name']");
			lastNameField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//input[@placeholder='Your Last Name']");
			emailAddressField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//input[@placeholder='email@domain.com']");
			phoneField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//input[@placeholder='(XXX) XXX-XXXX']");
			preferredDayToVisitField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//select[@data-sc-field-name='Preferred Day']");
			preferredTimeOfDayField = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//select[@data-sc-field-name='Preferred Time']");

			Element.enterData(testConfig, firstNameField, firstName, "First Name field value");
			Element.enterData(testConfig, lastNameField, lastName, "Last Name field value");
			Element.enterData(testConfig, emailAddressField, emailAddress, "Email Address field value");
			Element.enterData(testConfig, phoneField, String.valueOf(phoneNo), "Phone Number field value");

			Element.selectVisibleText(testConfig, preferredDayToVisitField, preferredDay,
					"Preferred Day to Visit field value");
			Element.selectVisibleText(testConfig, preferredTimeOfDayField, preferredTime,
					"Preferred Time Of Day field value");

			consentCheckbox = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//input[@data-sc-field-name='Terms and Conditions']");
			Element.click(testConfig, consentCheckbox, "Consent checkbox");

			submitBtn = Element.getPageElement(testConfig, How.xPath,
					".//section[@id='video-chat-tour']//button[@value='Submit']");
			Element.click(testConfig, submitBtn, "Submit button");
			Browser.wait(testConfig, 2);
			verifySuccessMessageRequestInfoForm(firstName, additionalMsg3);
			createExcelFileAndWriteEmailAddress(emailAddress, leadForm);
			break;
		}
	}

	public void verifyDataForPlanCards() throws UnsupportedEncodingException {

		WebElement plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[text()='floor plans']");
		Element.click(testConfig, plansTab, "Plans tab");
		Browser.wait(testConfig, 5);

		String cardImageAndThumbLoc = "(.//div[contains(@class,'card-body')])[number]//img";
		String commNameLoc = ".//div[contains(@class,'card-header')]//span[contains(@class,'car-capitalize')]";
		String neighborhoodNameLoc = ".//div[contains(@class,'card-header')]//parent::div//div[contains(@class,'car-h-full')][1]/span[contains(@class,'car-capitalize')]";
		String itemNameLoc = ".//div[contains(@class,'card-title')]/span[2]";
		String homeTypeLoc = "((.//div[contains(@class,'specs-container')])[number]//span[contains(@class,'car-type-brds-v2-xs-500')])[1]";
		String squareFootageLoc = "((.//div[contains(@class,'specs-container')])[number]//span[contains(@class,'car-type-brds-v2-xs-500')])[2]";
		String bedroomLoc = "((.//div[contains(@class,'specs-container')])[number]//span[contains(@class,'car-type-brds-v2-xs-500')])[3]";
		String bathroomLoc = "((.//div[contains(@class,'specs-container')])[number]//span[contains(@class,'car-type-brds-v2-xs-500')])[4]";
		String pricingLoc = ".//div[contains(@class,'car-text-brds')]//div[contains(text(),'$')] | .//div[contains(@class,'car-text-brds')]//div[contains(text(),'Coming')]";

		String allCardsLoc = ".//*[contains(@class,'card-container')]";
		List<WebElement> allCards = Element.getListOfElements(testConfig, How.xPath, allCardsLoc);

		Helper.scrollOnLazyLoadingPage(testConfig, true);

		for (int i = 0; i < allCards.size() - 1; i++) {

			testConfig.logComment("****************** Looking for Community Card Data for card no " + (i + 1) + " ******************");

			List<WebElement> cardImageAndThumb = Element.getListOfElements(testConfig, How.xPath, cardImageAndThumbLoc.replace("number", String.valueOf(i + 1)));
			if (cardImageAndThumb.size() == 2) {
				String largeImageSrc = Element.getAttribute(testConfig, cardImageAndThumb.get(0), "src", "Large image");
				String thumbImageSrc = Element.getAttribute(testConfig, cardImageAndThumb.get(1), "src", "Thumbnail image");

				if (!largeImageSrc.isEmpty()) {
					testConfig.logPass("Getting Larger image source displaying for the card as " + largeImageSrc);
					verifyURLAsPerDomain(largeImageSrc);
				} else {
					testConfig.logFail("Getting Larger image source displaying over card as blank... failing the scenario");
				}

				if (!thumbImageSrc.isEmpty()) {
					testConfig.logPass("Getting Thumbnail image source displaying for the card as " + largeImageSrc);
					verifyURLAsPerDomain(thumbImageSrc);
				} else {
					testConfig.logFail("Getting Thumbnail image source displaying over card as blank... failing the scenario");
				}

			} else {
				String largeImageSrc = Element.getAttribute(testConfig, cardImageAndThumb.get(0), "src", "Large image");
				if (!largeImageSrc.isEmpty()) {
					testConfig.logPass("Getting Larger image source displaying for the card as " + largeImageSrc);
					verifyURLAsPerDomain(largeImageSrc);
				} else {
					testConfig.logFail("Getting Larger image source displaying over card as blank... failing the scenario");
				}
			}

			List<WebElement> commName = Element.getListOfElements(testConfig, How.xPath, commNameLoc);
			if (!commName.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Community Name displaying over card as " + commName.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Community Name displaying over card value as blank... failing the scenario");
			}

			List<WebElement> neighborhoodName = Element.getListOfElements(testConfig, How.xPath, neighborhoodNameLoc);
			if (!neighborhoodName.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Neighborhood Name displaying over card as " + neighborhoodName.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Neighborhood Name displaying over card value as blank... failing the scenario");
			}

			List<WebElement> itemName = Element.getListOfElements(testConfig, How.xPath, itemNameLoc);
			if(!itemName.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Item Name displaying over card as " + itemName.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Item Name displaying over card value as blank... failing the scenario");
			}

			WebElement homeType = Element.getPageElement(testConfig, How.xPath, homeTypeLoc.replace("number", String.valueOf(i + 1)));
			if(!homeType.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Home Type displaying over card as " + homeType.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Home Type displaying over card value as blank... failing the scenario");
			}

			WebElement squareFootage = Element.getPageElement(testConfig, How.xPath, squareFootageLoc.replace("number", String.valueOf(i + 1)));
			if(!squareFootage.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Square Footage displaying over card as " + squareFootage.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Square Footage displaying over card value as blank... failing the scenario");
			}

			WebElement bedroom = Element.getPageElement(testConfig, How.xPath, bedroomLoc.replace("number", String.valueOf(i + 1)));
			if(!bedroom.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Bedroom displaying over card as " + squareFootage.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Bedroom Footage displaying over card value as blank... failing the scenario");
			}

			WebElement bathroom = Element.getPageElement(testConfig, How.xPath, bathroomLoc.replace("number", String.valueOf(i + 1)));
			if(!bathroom.getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Bathroom displaying over card as " + bathroom.getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Bathroom Footage displaying over card value as blank... failing the scenario");
			}

			List<WebElement> pricing = Element.getListOfElements(testConfig, How.xPath, pricingLoc);
			if(!pricing.get(i).getAttribute("innerText").isEmpty()) {
				testConfig.logPass("Getting Pricing displaying over card as " + pricing.get(i).getAttribute("innerText"));
			} else {
				testConfig.logFail("Getting Pricing displaying over card value as blank... failing the scenario");
			}
		}
	}

	public void accessRequestInformationModal(Boolean checkForm) {

		checkForPromo();

		String requestInfoLocator = ".//div[contains(@class,'community-header')]//div/a/parent::div/button";
		WebElement requestInfo = Element.getPageElement(testConfig, How.xPath, requestInfoLocator);
		Element.click(testConfig, requestInfo, "Request Information Form button present in Community section");
		Browser.wait(testConfig, 5);

		if (checkForm) {
			try {
				String requestInfoModal = ".//div[contains(@class,'scrollbars')]/../parent::div[contains(@class,'transition')]";
				WebElement requestInfoModalElement = Element.getPageElement(testConfig, How.xPath, requestInfoModal);
				if (requestInfoModalElement.isDisplayed()) {
					testConfig.logPass("Verified Request Information modal opens correctly");
				} else {
					testConfig.logFail("Failed to verify that Request Information modal opens correctly");
				}
			} catch (Exception e) {
				testConfig.logFail("Failed to verify that Request Information modal opens correctly");
			}
		}
	}

	public static String toSentenceCase(final String s) {
		final StringBuilder sb = new StringBuilder(s);
		return toSentenceCase(sb).toString();
	}

	private static StringBuilder toSentenceCase(final StringBuilder sb) {
		boolean capitalizeNext = true;
		for (int i = 0; i < sb.length(); i++) {
			final char c = sb.charAt(i);
			if (c == '.') {
				capitalizeNext = true;
			} else if (capitalizeNext && !isSeparator(c)) {
				sb.setCharAt(i, Character.toTitleCase(c));
				capitalizeNext = false;
			} else if (!Character.isLowerCase(c)) {
				sb.setCharAt(i, Character.toLowerCase(c));
			}
		}
		return sb;
	}

	private static boolean isSeparator(char c) {
		return WORD_SEPARATORS.indexOf(c) >= 0;
	}

	public String convertToTitleCase(String message) {

		char[] charArray = message.toCharArray();
		boolean foundSpace = true;
		for (int i = 0; i < charArray.length; i++) {
			if (Character.isLetter(charArray[i])) {
				if (foundSpace) {
					charArray[i] = Character.toUpperCase(charArray[i]);
					foundSpace = false;
				}
			} else {
				foundSpace = true;
			}
		}
		message = String.valueOf(charArray);
		return message;
	}

	public void verifyTooltipStylingPlanCard() {

		checkForPromo();

		String plansLocator = ".//*[text()='floor plans']/parent::button";
		WebElement plansTab = Element.getPageElement(testConfig, How.xPath, plansLocator);
		Element.click(testConfig, plansTab, "Plans tab");
		Browser.wait(testConfig, 2);

		String bathroomLoc = "((.//div[contains(@class,'specs-container')])[number]//span[contains(@class,'car-type-brds-v2-xs-500')])[4]";
		String totalCardsLocator = ".//div[contains(@class,'specs-container')]";
		List<WebElement> totalCards = Element.getListOfElements(testConfig, How.xPath, totalCardsLocator);

		for (int i = 0; i < totalCards.size(); i++) {
			WebElement cardBathTooltip = Element.getPageElement(testConfig, How.xPath, bathroomLoc.replace("number", String.valueOf(i + 1)));
			String borderStyle = cardBathTooltip.getCssValue("border-style");
			String textAlignProperty = cardBathTooltip.getCssValue("text-align");

			Helper.compareEquals(testConfig, "Border style for the bath tooltip for Plan card : " + (i+1), "dashed", borderStyle);
			Helper.compareEquals(testConfig, "Text-align property for the bath tooltip for Plan card : " + (i+1), "center", textAlignProperty);
		}

	}

	public void verifyUnitAndLotAppearanceForQMICards(String expectedCardText, String expectedFlyoutCardText) {

		checkForPromo();

		String condoNeighborhoodLocator = ".//p[contains(text(),'Filter by')]/parent::div//p[text()='Condos']";
		WebElement condoNeighborhood = Element.getPageElement(testConfig, How.xPath, condoNeighborhoodLocator);
		Element.click(testConfig, condoNeighborhood, "Condo neighborhood tile");
		Browser.wait(testConfig, 2);

		checkForPromo();

		String condoCardLoc = "(.//*[contains(@class,'card-container')]//div[contains(@class,'specs-container')]"
				+ "//span[contains(@class,'car-type-brds')][text()='Condo'])[1]";
		WebElement CondoCards = Element.getPageElement(testConfig, How.xPath, condoCardLoc);
		Element.click(testConfig, CondoCards, "First condo card encountered");
		Browser.wait(testConfig, 5);

		String lotNumLoc = "(.//*[contains(@class,'card-container')]//div[contains(@class,'specs-container')]"
				+ "//span[contains(@class,'car-type-brds')][text()='Condo']/ancestor::*[contains(@class,'card-container')]"
				+ "//span[contains(@class,'available')]/../span[1])[1]";
		WebElement lotNum = Element.getPageElement(testConfig, How.xPath, lotNumLoc);

		String unitNumberFlyoutCardLoc = ".//div[contains(@class,'w-full h-full')]//div[contains(@class,'text-brp-blue-100')]/span[contains(@style,'white')]";
		WebElement unitNumberFlyoutCard = Element.getPageElement(testConfig, How.xPath, unitNumberFlyoutCardLoc);

		Helper.compareContains(testConfig, "Detail displaying over card in the left trail", expectedCardText, lotNum.getText());
		Helper.compareContains(testConfig, "Detail displaying over card flyout on the map", expectedFlyoutCardText, unitNumberFlyoutCard.getText());

	}

	public void verifySearchByMapIncreasesCommunityCountAsPrevious() {

		Browser.wait(testConfig, 4);

		WebElement inputSearch = Element.getPageElement(testConfig, How.css,
				"input[class*='fyh-input'][aria-labelledby='Search']");
		Element.click(testConfig, inputSearch, "Search input box");
		Browser.wait(testConfig, 2);

		WebElement clearButton = Element.getPageElement(testConfig, How.css, "button[aria-labelledby='ClearSearch']");
		Element.click(testConfig, clearButton, "Clear search value");
		Browser.wait(testConfig, 2);

		WebElement searchByMapCTA = Element.getPageElement(testConfig, How.css, "li[aria-labelledby='Search by map']");
		Element.clickThroughJS(testConfig, searchByMapCTA, "Search By Map CTA");
		Browser.wait(testConfig, 20);

		WebElement homesTabNewView = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]/../div/div");
		WebElement communityTabNewView = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'communities')]/../div/div");

		String homesTabLabelNewView = Element.getText(testConfig, homesTabNewView, "Homes tab content");
		String communitiesTabLabelNewView = Element.getText(testConfig, communityTabNewView, "Communities tab content");

		int locationHomeCountNewView = Integer.parseInt(homesTabLabelNewView);
		int locationCommunityCountNewView = Integer.parseInt(communitiesTabLabelNewView);

		if(locationHomeCountNewView == Integer.parseInt(testConfig.getRunTimeProperty("HomeCount"))) {
			testConfig.logPass("Verified redirection happened correctly and the home count remain same as " + locationHomeCountNewView);
		} else {
			testConfig.logFail("Failed to verify the redirection happened correctly as home count decreased/increased to " + locationHomeCountNewView + " from " + Integer.parseInt(testConfig.getRunTimeProperty("HomeCount")));
		}

		if(locationCommunityCountNewView == Integer.parseInt(testConfig.getRunTimeProperty("CommunitiesCount"))) {
			testConfig.logPass("Verified redirection happened correctly and the community count remain same as " + locationCommunityCountNewView);
		} else {
			testConfig.logFail("Failed to verify the redirection happened correctly as community count decreased/increased to " + locationCommunityCountNewView + " from " + Integer.parseInt(testConfig.getRunTimeProperty("CommunitiesCount")));
		}
	}

	public void getCommunitiesHomesCount(String location) {

		Browser.wait(testConfig, 15);

		WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]/../div/div");
		WebElement communityTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'communities')]/../div/div");

		String homesTabLabel = Element.getText(testConfig, homesTab, "Homes tab content");
		String communitiesTabLabel = Element.getText(testConfig, communityTab, "Communities tab content");

		int locationHomeCount = Integer.parseInt(homesTabLabel);
		int locationCommunityCount = Integer.parseInt(communitiesTabLabel);

		testConfig.putRunTimeProperty("CommunitiesCount", locationCommunityCount);
		testConfig.putRunTimeProperty("HomeCount", locationHomeCount);

		WebElement inputSearch = Element.getPageElement(testConfig, How.css,
				"input[class*='fyh-input'][aria-labelledby='Search']");
		Element.click(testConfig, inputSearch, "Search input box");
		Browser.wait(testConfig, 2);

		WebElement clearButton = Element.getPageElement(testConfig, How.css, "button[aria-labelledby='ClearSearch']");
		Element.click(testConfig, clearButton, "Clear search value");
		Browser.wait(testConfig, 5);

		List<WebElement> allLocations = Element.getListOfElements(testConfig, How.css,
				"li.srch-items-center div.srch-flex-grow");

		for (int i = 0; i < allLocations.size(); i++) {
			if(allLocations.get(i).getText().equals(location)) {
				Element.click(testConfig, allLocations.get(i), allLocations.get(i).getText() + " suggested state location");
				break;
			}
		}

		Browser.wait(testConfig, 3);
		WebElement submitButton = Element.getPageElement(testConfig, How.xPath,
				".//input[contains(@class,'fyh-input')]/../../button[contains(@id,'combobox')]");
		Element.click(testConfig, submitButton, "Search input box");
		Browser.wait(testConfig, 10);

		WebElement homesTabNewView = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]/../div/div");
		WebElement communityTabNewView = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'communities')]/../div/div");

		String homesTabLabelNewView = Element.getText(testConfig, homesTabNewView, "Homes tab content");
		String communitiesTabLabelNewView = Element.getText(testConfig, communityTabNewView, "Communities tab content");

		int locationHomeCountNewView = Integer.parseInt(homesTabLabelNewView);
		int locationCommunityCountNewView = Integer.parseInt(communitiesTabLabelNewView);

		if(locationHomeCountNewView < locationHomeCount) {
			testConfig.logPass("Verified redirection happened correctly and the home count being updated to " + locationHomeCountNewView + " from " + locationHomeCount);
		} else {
			testConfig.logFail("Failed to verify the redirection happened correctly as home count increased/remained same to " + locationHomeCountNewView + " from " + locationHomeCount);
		}

		if(locationCommunityCountNewView < locationCommunityCount) {
			testConfig.logPass("Verified redirection happened correctly and the community count being updated to " + locationCommunityCountNewView + " from " + locationCommunityCount);
		} else {
			testConfig.logFail("Failed to verify the redirection happened correctly as community count increased/remained same to " + locationCommunityCountNewView + " from " + locationCommunityCount);
		}
	}

	public void verifyErrorMessageForContactOurTeamForm(String expectedRegion, String[] expectedErrorMsgs) {

		String fields[] = {"First Name", "Last Name", "Email", "Consent"};
		selectRegionAndCommunity(expectedRegion);

		Browser.wait(testConfig, 2);

		//Helper.scrollOnContactUsSendAMessageModal(testConfig, false);

		WebElement submitBtn = Element.getPageElement(testConfig, How.xPath, ".//button[@value='Submit'][not(contains(@class,'footer'))]");
		Element.click(testConfig, submitBtn, "Submit button");
		Browser.wait(testConfig, 2);

		String errorLoc = ".//span[contains(@class,'field-validation-error')]";
		List<WebElement> allErrors = Element.getListOfElements(testConfig, How.xPath, errorLoc);

		for (int i = 0; i < allErrors.size(); i++) {
			WebElement errorMsg = allErrors.get(i);
			Helper.compareEquals(testConfig, "Error message for " + fields[i] +  " field", expectedErrorMsgs[i], errorMsg.getAttribute("innerText").trim());
		}
	}

	private void selectRegionAndCommunity(String expectedRegion) {

		String btnLocator = ".//div[@class='flex h-14']//div/button[contains(@class,'btn')]";
		WebElement contactBtn = Element.getPageElement(testConfig, How.xPath, btnLocator);
		Element.click(testConfig, contactBtn, "Contact Our Team button");
		Browser.wait(testConfig, 2);

		try {
			WebElement modal = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'mdl-relative')]");
			if(!modal.isDisplayed()) {
				testConfig.logFail("Failed to verify that modal opened after clicking 'Contact Our Team' CTA");
			} else {
				testConfig.logPass("Verified that modal opened after clicking 'Contact Our Team' CTA");
			}
		} catch (Exception e) {
			testConfig.logFail("Failed to verify that modal opened after clicking 'Contact Our Team' CTA");
		}

		Browser.wait(testConfig, 2);
		String selectRegion = ".//p[contains(text(),'Region')]/parent::div//p[text()='Select a Region']";
		WebElement selectRgnDropdown = Element.getPageElement(testConfig, How.xPath, selectRegion);
		Element.click(testConfig, selectRgnDropdown, "Select Region Dropdown");
		Browser.wait(testConfig, 5);

		boolean flag = false;
		String regionListLocator = ".//ul[@role='listbox']//li/span";
		//Helper.scrollOnContactUsModal(testConfig, false);
		List<WebElement> allRegions = Element.getListOfElements(testConfig, How.xPath, regionListLocator);
		if(allRegions.size() < 1) {
			testConfig.logFail("Region list not populating... hence marking the test case as failed");
		} else {
			for (Iterator<WebElement> iterator = allRegions.iterator(); iterator.hasNext();) {
				WebElement region = (WebElement) iterator.next();
				if(region.getText().equals(expectedRegion)) {
					flag = true;
					Element.click(testConfig, region, region.getText() + " region");
					Browser.wait(testConfig, 2);
					break;
				}
			}
		}

		if(!flag) {
			testConfig.logFail("Expected region not displaying in the dropdown... hence marking the test as failed");
		} else {
			String selectCommunity = ".//p[contains(text(),'Community')]/parent::div//p[contains(text(),'Select a Community')]";
			WebElement selectCommunityDropdown = Element.getPageElement(testConfig, How.xPath, selectCommunity);
			Element.click(testConfig, selectCommunityDropdown, "Select Region Dropdown");
			Browser.wait(testConfig, 2);

			String communityListLocator = ".//ul[@role='listbox']//li";
			List<WebElement> allCommunities = Element.getListOfElements(testConfig, How.xPath, communityListLocator);
			testConfig.putRunTimeProperty("CommunityName", allCommunities.get(0).getText());
			Element.click(testConfig, allCommunities.get(0), allCommunities.get(0).getText() + " community");
			Browser.wait(testConfig, 2);
		}
	}

	public void applyAvailabilityFilter(String availabilityFilter) {

		String locator = ".//button//div[contains(text(),'More Filters')]";
		WebElement filterBtn = Element.getPageElement(testConfig, How.xPath, locator); 
		Element.click(testConfig, filterBtn, "More filter");
		Browser.wait(testConfig, 2);

		String buildStatusLocator = ".//span[text()='Availability']/parent::div/fieldset//button[not(contains(@class,'disabled'))]/div";
		List<WebElement> allBuildStatus = Element.getListOfElements(testConfig, How.xPath, buildStatusLocator);
		if(allBuildStatus.size() > 0) {
			testConfig.logPass("Getting total Build Status values as " + allBuildStatus.size());
			testConfig.logComment("The values are: ");
			for (int i = 0; i < allBuildStatus.size(); i++) {
				if (availabilityFilter.equalsIgnoreCase(allBuildStatus.get(i).getText())) {
					Element.click(testConfig, allBuildStatus.get(i), allBuildStatus.get(i).getText() + " option");
					break;
				}
			}
		} else {
			testConfig.logFail("Not getting the expected availability values displaying");
		}

		Browser.wait(testConfig, 3);
		WebElement doneBtn = Element.getPageElement(testConfig, How.xPath, ".//div[contains(@class,'panel')]//button[contains(@class,'btn dark')]");
		Element.click(testConfig, doneBtn, "Done button to apply the filter");

	}

	public void verifyQMICardsHaveMoveInNowLabel(String expectedLabel) {

		WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]");
		Element.click(testConfig, homesTab, "Homes tab");
		Browser.wait(testConfig, 5);

		String priceLocator = ".//div[contains(@class,'car-text-brds-v1-grayscale-blue-medium-ft')]/div";
		String moveInNowLabel = ".//div[contains(@class,'car-text-brds-v1-grayscale-blue-medium-ft')]/div/ancestor::div[contains(@class,'car-item-bottom')]//div[contains(@class,'card-badge build-status')]";

		List<WebElement> priceValue = Element.getListOfElements(testConfig, How.xPath, priceLocator);
		List<WebElement> allLabels = Element.getListOfElements(testConfig, How.xPath, moveInNowLabel);

		if (allLabels.size() < 1) {
			testConfig.logFail("Correct results are not displayed... failing the test case");
		} else {
			for (int i = 0; i < priceValue.size(); i++) {
				if (!priceValue.get(i).getText().equals("SOLD")) {
					Helper.compareEquals(testConfig, "Availability label for " + (i + 1) + " card ", expectedLabel, allLabels.get(i).getText());
				}
			}
		}
	}

	public void verifyFloorPlansCountIsNotZero() {

		WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'floor plans')]");
		Element.click(testConfig, homesTab, "Floor Plans tab");
		Browser.wait(testConfig, 5);

		String floorPlanCountLoc = ".//button/span[contains(text(),'floor plans')]/../div/div";
		String allCardLoc = "button.card-container";

		WebElement floorPlanCount = Element.getPageElement(testConfig, How.xPath, floorPlanCountLoc);

		if(!(Integer.parseInt(floorPlanCount.getText()) == 0)) {
			testConfig.logPass("Verified that we are getting the count displaying with the tab as " + Integer.parseInt(floorPlanCount.getText()));
		} else {
			testConfig.logFail("We are getting the count displaying with the tab as zero .. failing the test case");
		}

		try {
			List<WebElement> allCards = Element.getListOfElements(testConfig, How.css, allCardLoc);
			if(allCards.size() != 0) {
				testConfig.logPass("Verified that we are getting the cards count as " + Integer.parseInt(floorPlanCount.getText()));
			} else {
				testConfig.logFail("We are getting the cards count as zero .. failing the test case");
			}
		} catch (Exception e) {
			testConfig.logFail("We are getting the cards count as zero .. failing the test case");
		}

	}

	public void verifyResultCardsSatisfiesPriceFilter(ResultTab resultTab) {

		Browser.waitWithoutLogging(testConfig, 5);
		String priceLocator = "";
		int minValueSelected = Integer.parseInt(testConfig.getRunTimeProperty("PriceMinValue"));
		int maxValueSelected = Integer.parseInt(testConfig.getRunTimeProperty("PriceMaxValue"));

		switch (resultTab) {
		case Homes:
			priceLocator = ".//div[contains(@class,'grayscale-blue-medium')][contains(text(),'Priced at')]/parent::div/div[2]";
			WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]");
			Element.clickThroughJS(testConfig, homesTab, "Homes tab");
			break;

		case Plans:
			priceLocator = ".//div[contains(@class,'car-text-brds-v1-grayscale-blue-medium-ft')]//div[contains(text(),'$')]";
			WebElement plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'floor plans')]");
			Element.clickThroughJS(testConfig, plansTab, "Plans tab");
			break;

		default:
			break;
		}

		Browser.waitWithoutLogging(testConfig, 5);
		List<WebElement> qmiPriceElement = Element.getListOfElements(testConfig, How.xPath, priceLocator);

		ArrayList<Long> obtainedList = new ArrayList<>(); 
		for (int i = 0; i < qmiPriceElement.size(); i++) {
			WebElement priceValueEle = qmiPriceElement.get(i);
			if(priceValueEle.getText().contains("$"))
			{
				String price = priceValueEle.getText().replaceAll("[$,]", "").trim();
				obtainedList.add(Long.parseLong(price));
			}
		}

		ArrayList<Long> sortedList = new ArrayList<>();
		for (Long s : obtainedList) {
			sortedList.add(s);
		}
		Collections.sort(sortedList);
		testConfig.logComment("Obtained Price List from cards over the page : " + Arrays.toString(obtainedList.toArray()));

		if(sortedList.get(0) >= minValueSelected) {
			testConfig.logPass("Verified the minimum price for QMI cards lies within the range " + minValueSelected
					+ " and " + maxValueSelected + " as the minimum value is " + sortedList.get(0));
		} else {
			testConfig.logFail("Failed to verify the minimum price for QMI cards lies within the range " + minValueSelected
					+ " and " + maxValueSelected + " as the minimum value is " + sortedList.get(0));
		}
		
		if(sortedList.get(sortedList.size() - 1) <= maxValueSelected) {
			testConfig.logPass("Verified the maximum price for QMI cards lies within the range " + minValueSelected
					+ " and " + maxValueSelected + " as the maximum value is " + sortedList.get(sortedList.size() - 1));
		} else {
			testConfig.logFail("Failed to verify the maximum price for QMI cards lies within the range " + minValueSelected
					+ " and " + maxValueSelected + " as the maximum value is " + sortedList.get(sortedList.size() - 1));
		}
	}

	public void verifyResultCardsSatisfiesSquareFootageFilter(ResultTab resultTab) {
			
		Browser.waitWithoutLogging(testConfig, 5);
		String squareFootageLocator = "";
		int minSquareFootageSelected = Integer.parseInt(testConfig.getRunTimeProperty("SquareFootageMinValue"));
		int maxSquareFootageSelected = Integer.parseInt(testConfig.getRunTimeProperty("SquareFootageMaxValue"));
		String type = "";
		
		switch (resultTab) {
		case Homes:
			type = "QMI";
			squareFootageLocator = ".//div[contains(@class,'specs-container')]//span[contains(@class,'car-type-brds-v2-xs-500')][contains(text(),'ft')]";
			WebElement homesTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'quick move-in')]");
			Element.clickThroughJS(testConfig, homesTab, "Homes tab");
			break;

		case Plans:
			type = "Plans";
			squareFootageLocator = ".//div[contains(@class,'specs-container')]//span[contains(@class,'car-type-brds-v2-xs-500')][contains(text(),'ft')]";
			WebElement plansTab = Element.getPageElement(testConfig, How.xPath, ".//button/span[contains(text(),'floor plans')]");
			Element.clickThroughJS(testConfig, plansTab, "Plans tab");
			break;

		default:
			break;
		}
		
		Browser.waitWithoutLogging(testConfig, 5);
		List<WebElement> allHomeSizes = Element.getListOfElements(testConfig, How.xPath, squareFootageLocator);

		String homeSize = "";
		ArrayList<Integer> obtainedList = new ArrayList<>(); 
		for (int i = 0; i < (allHomeSizes.size() / 2); i++) {
			WebElement homeSizeEle = allHomeSizes.get(i);
			if(homeSizeEle.getText().contains("-"))
			{
				homeSize = homeSizeEle.getText().split("-")[0].replaceAll("[, ft²]", "").trim();
			} else {
				homeSize = homeSizeEle.getText().replaceAll("[, ft²]", "").trim();
			}
			obtainedList.add(Integer.parseInt(homeSize));
		}

		ArrayList<Integer> sortedList = new ArrayList<>();
		for (Integer s : obtainedList) {
			sortedList.add(s);
		}
		Collections.sort(sortedList);
		testConfig.logComment("Obtained Square footage List from cards over the page : " + Arrays.toString(obtainedList.toArray()));

		if(sortedList.get(0) >= minSquareFootageSelected) {
			testConfig.logPass("Verified the minimum square footage for " + type + " cards lies within the range " + minSquareFootageSelected
					+ " and " + maxSquareFootageSelected + " as the minimum value is " + sortedList.get(0));
		} else {
			testConfig.logFail("Failed to verify the minimum square footage for " + type + " cards lies within the range " + minSquareFootageSelected
					+ " and " + maxSquareFootageSelected + " as the minimum value is " + sortedList.get(0));
		}
		
		if(sortedList.get(sortedList.size() - 1) <= maxSquareFootageSelected) {
			testConfig.logPass("Verified the maximum square footage for " + type + " cards lies within the range " + minSquareFootageSelected
					+ " and " + maxSquareFootageSelected + " as the maximum value is " + sortedList.get(sortedList.size() - 1));
		} else {
			testConfig.logFail("Failed to verify the maximum square footage for " + type + " cards lies within the range " + minSquareFootageSelected
					+ " and " + maxSquareFootageSelected + " as the maximum value is " + sortedList.get(sortedList.size() - 1));
		}
	}

}