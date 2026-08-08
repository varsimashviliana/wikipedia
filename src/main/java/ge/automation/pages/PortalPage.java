package ge.automation.pages;

import ge.automation.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class PortalPage extends BasePage {

    @FindBy(id = "searchInput")
    private WebElement searchInput;

    @FindBy(id = "searchLanguage")
    private WebElement languageDropdown;

    @FindBy(id = "js-link-box-en")
    private WebElement englishLink;

    @FindBy(xpath = "//img[contains(@class,'central-featured-logo')]")
    private WebElement centralLogo;

    @FindBy(css = "div.central-featured-lang")
    private List<WebElement> allLanguageBlocks;

    public PortalPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageOpened() {
        return isDisplayed(searchInput) && isPresent(languageDropdown);
    }

    public SearchResultsPage searchFor(String query) {
        typeAndEnter(searchInput, query);
        waitUntilUrlDoesNotContain(ConfigReader.get("portal.url"));
        return new SearchResultsPage(driver);
    }

    public PortalPage selectLanguageByText(String visibleText) {
        selectByVisibleText(languageDropdown, visibleText);
        return this;
    }

    public PortalPage selectLanguageByValue(String langCode) {
        selectByValue(languageDropdown, langCode);
        return this;
    }

    public String getSelectedLanguage() {
        return getSelectedOption(languageDropdown);
    }

    public int getLanguageCount() {
        return getSelectOptions(languageDropdown).size();
    }

    public boolean hasLanguageOption(String visibleText) {
        return getSelectOptions(languageDropdown).stream()
                .anyMatch(option -> optionText(option).equalsIgnoreCase(visibleText));
    }

    public PortalPage hoverOverEnglish() {
        hoverOver(englishLink);
        return this;
    }

    public int getFeaturedLanguageCount() {
        return allLanguageBlocks.size();
    }

    public boolean isLogoDisplayed() {
        return isDisplayed(centralLogo);
    }
}
