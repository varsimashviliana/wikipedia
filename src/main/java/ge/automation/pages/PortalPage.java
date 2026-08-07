package ge.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class PortalPage extends BasePage {

    @FindBy(id = "searchInput")
    private WebElement searchInput;

    @FindBy(id = "searchLanguage")
    private WebElement languageDropdown;

    @FindBy(css = "button.pure-button-primary-progress")
    private WebElement searchButton;

    @FindBy(id = "js-link-box-en")
    private WebElement englishLink;

    @FindBy(xpath = "//div[contains(@class,'central-featured-logo')]")
    private WebElement centralLogo;

    @FindBy(css = "div.central-featured-lang")
    private List<WebElement> allLanguageBlocks;

    public PortalPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageOpened() {
        return isDisplayed(searchInput) && isDisplayed(languageDropdown);
    }

    public SearchResultsPage searchFor(String query) {
        typeAndEnter(searchInput, query);
        return new SearchResultsPage(driver);
    }

    public SearchResultsPage searchUsingButton(String query) {
        type(searchInput, query);
        click(searchButton);
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
                .anyMatch(option -> option.getText().trim().equalsIgnoreCase(visibleText));
    }

    public PortalPage hoverOverEnglish() {
        hoverOver(englishLink);
        return this;
    }

    public void goToEnglishWikipedia() {
        click(englishLink);
    }

    public int getFeaturedLanguageCount() {
        return allLanguageBlocks.size();
    }

    public boolean isLogoDisplayed() {
        return isDisplayed(centralLogo);
    }
}
