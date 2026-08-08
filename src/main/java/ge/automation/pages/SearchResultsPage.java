package ge.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

public class SearchResultsPage extends BasePage {

    @FindBy(css = "ul.mw-search-results > li")
    private List<WebElement> resultItems;

    @FindBy(css = ".mw-search-result-heading a")
    private List<WebElement> resultTitles;

    @FindBy(css = ".results-info")
    private WebElement resultsInfo;

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageOpened() {
        return getCurrentUrl().contains("search") || !resultItems.isEmpty();
    }

    public int getResultCount() {
        return resultItems.size();
    }

    public List<String> getResultTitles() {
        return resultTitles.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public String getResultsInfoText() {
        return getText(resultsInfo);
    }

    public boolean anyResultContains(String keyword) {
        return getResultTitles().stream()
                .anyMatch(title -> title.toLowerCase().contains(keyword.toLowerCase()));
    }

    public ArticlePage openResult(int index) {
        if (index < 0 || index >= resultTitles.size()) {
            throw new IndexOutOfBoundsException(
                    "შედეგი #" + index + " არ არსებობს. სულ არის: " + resultTitles.size());
        }
        WebElement link = resultTitles.get(index);
        scrollToElement(link);
        link.click();
        return new ArticlePage(driver);
    }

    public boolean isNoResultsMessageDisplayed() {
        return !driver.findElements(By.cssSelector(".mw-search-nonefound")).isEmpty();
    }
}
