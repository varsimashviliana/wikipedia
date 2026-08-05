package ge.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * ArticlePage — ვიკიპედიის სტატიის გვერდი.
 * მაგ: https://en.wikipedia.org/wiki/Selenium_(software)
 */
public class ArticlePage extends BasePage {

    /** სტატიის მთავარი სათაური. */
    @FindBy(id = "firstHeading")
    private WebElement articleTitle;

    /** სტატიის ძირითადი ტექსტი. */
    @FindBy(id = "mw-content-text")
    private WebElement articleBody;

    /** სტატიის ყველა აბზაცი. */
    @FindBy(css = "#mw-content-text p")
    private List<WebElement> paragraphs;

    /** გვერდის ჩანართები (Article / Talk). */
    @FindBy(css = "#p-associated-pages li")
    private List<WebElement> pageTabs;

    /** სარჩევის (Table of contents) ბლოკი. */
    @FindBy(css = "#vector-toc, .vector-toc")
    private WebElement tableOfContents;

    public ArticlePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageOpened() {
        return isDisplayed(articleTitle);
    }

    /** აბრუნებს სტატიის სათაურს. */
    public String getArticleTitle() {
        return getText(articleTitle);
    }

    /** აბრუნებს სტატიის ტექსტს. */
    public String getArticleBodyText() {
        return getText(articleBody);
    }

    /** რამდენი აბზაცია სტატიაში. */
    public int getParagraphCount() {
        return paragraphs.size();
    }

    /** ამოწმებს, სტატიაში გვხვდება თუ არა მოცემული სიტყვა. */
    public boolean bodyContains(String keyword) {
        return getArticleBodyText().toLowerCase().contains(keyword.toLowerCase());
    }

    /** სარჩევი ჩანს თუ არა. */
    public boolean isTableOfContentsDisplayed() {
        return isDisplayed(tableOfContents);
    }

    /** რამდენი ჩანართი აქვს გვერდს. */
    public int getTabCount() {
        return pageTabs.size();
    }
}
