package ge.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SearchResultsPage — ძებნის შედეგების გვერდი.
 * მაგ: https://en.wikipedia.org/w/index.php?search=Selenium&fulltext=1
 *
 * <p>აქ არის <b>checkbox-ები</b> (Search in: Article, Talk, User...) —
 * დავალება სწორედ ასეთი მრავალფეროვანი ელემენტების გამოყენებას ითხოვს.</p>
 */
public class SearchResultsPage extends BasePage {

    /** შედეგების სია. */
    @FindBy(css = "ul.mw-search-results > li")
    private List<WebElement> resultItems;

    /** შედეგების სათაურები (ბმულები). */
    @FindBy(css = ".mw-search-result-heading a")
    private List<WebElement> resultTitles;

    /** ტექსტი "Results 1 – 20 of 2,972". */
    @FindBy(css = ".results-info")
    private WebElement resultsInfo;

    /** ძებნის ველი შედეგების გვერდზე. */
    @FindBy(css = "input[name='search']")
    private WebElement searchBox;

    /** checkbox: Article (ns0) — მთავარი სტატიები. */
    @FindBy(id = "mw-search-ns0")
    private WebElement articleNamespaceCheckbox;

    /** checkbox: Talk (ns1) — განხილვის გვერდები. */
    @FindBy(id = "mw-search-ns1")
    private WebElement talkNamespaceCheckbox;

    /** checkbox: User (ns2) — მომხმარებლის გვერდები. */
    @FindBy(id = "mw-search-ns2")
    private WebElement userNamespaceCheckbox;

    /** ყველა namespace checkbox ერთად. */
    @FindBy(css = "input[id^='mw-search-ns']")
    private List<WebElement> allNamespaceCheckboxes;

    /** "Search" ღილაკი შედეგების გვერდზე. */
    @FindBy(css = "button[type='submit']")
    private WebElement searchSubmitButton;

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageOpened() {
        return getCurrentUrl().contains("search") || !resultItems.isEmpty();
    }

    // ---------------------------------------------------------------
    //  შედეგები
    // ---------------------------------------------------------------

    /** რამდენი შედეგი მოიძებნა ამ გვერდზე. */
    public int getResultCount() {
        return resultItems.size();
    }

    /** აბრუნებს ყველა შედეგის სათაურს ტექსტად. */
    public List<String> getResultTitles() {
        return resultTitles.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /** აბრუნებს ტექსტს "Results 1 – 20 of 2,972". */
    public String getResultsInfoText() {
        return getText(resultsInfo);
    }

    /** ამოწმებს, შედეგებში მოიძებნება თუ არა მოცემული სიტყვა (რეგისტრის გარეშე). */
    public boolean anyResultContains(String keyword) {
        return getResultTitles().stream()
                .anyMatch(title -> title.toLowerCase().contains(keyword.toLowerCase()));
    }

    /** ხსნის N-ურ შედეგს (0-დან იწყება). */
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

    /** ამოწმებს, "შედეგი არ მოიძებნა" შეტყობინება ჩანს თუ არა. */
    public boolean isNoResultsMessageDisplayed() {
        return !driver.findElements(By.cssSelector(".mw-search-nonefound")).isEmpty();
    }

    // ---------------------------------------------------------------
    //  Checkbox-ები
    // ---------------------------------------------------------------

    /** სულ რამდენი namespace checkbox არის გვერდზე. */
    public int getNamespaceCheckboxCount() {
        return allNamespaceCheckboxes.size();
    }

    /** Article checkbox მონიშნულია თუ არა. */
    public boolean isArticleNamespaceSelected() {
        return articleNamespaceCheckbox.isSelected();
    }

    /** Talk checkbox მონიშნულია თუ არა. */
    public boolean isTalkNamespaceSelected() {
        return talkNamespaceCheckbox.isSelected();
    }

    /** Talk checkbox-ის მონიშვნა. */
    public SearchResultsPage checkTalkNamespace() {
        scrollToElement(talkNamespaceCheckbox);
        checkCheckbox(talkNamespaceCheckbox);
        return this;
    }

    /** Talk checkbox-იდან მონიშვნის მოხსნა. */
    public SearchResultsPage uncheckTalkNamespace() {
        scrollToElement(talkNamespaceCheckbox);
        uncheckCheckbox(talkNamespaceCheckbox);
        return this;
    }

    /** User checkbox-ის მონიშვნა. */
    public SearchResultsPage checkUserNamespace() {
        scrollToElement(userNamespaceCheckbox);
        checkCheckbox(userNamespaceCheckbox);
        return this;
    }

    /** User checkbox მონიშნულია თუ არა. */
    public boolean isUserNamespaceSelected() {
        return userNamespaceCheckbox.isSelected();
    }

    /** ახალი ძებნის გაშვება ამავე გვერდიდან. */
    public SearchResultsPage searchAgain(String query) {
        typeAndEnter(searchBox, query);
        return new SearchResultsPage(driver);
    }
}
