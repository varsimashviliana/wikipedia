package ge.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ArticlePage extends BasePage {

    @FindBy(id = "firstHeading")
    private WebElement articleTitle;

    @FindBy(id = "mw-content-text")
    private WebElement articleBody;

    @FindBy(css = "#mw-content-text p")
    private List<WebElement> paragraphs;

    @FindBy(css = "#p-associated-pages li")
    private List<WebElement> pageTabs;

    @FindBy(css = "#vector-toc, .vector-toc")
    private WebElement tableOfContents;

    public ArticlePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageOpened() {
        return isDisplayed(articleTitle);
    }

    public String getArticleTitle() {
        return getText(articleTitle);
    }

    public String getArticleBodyText() {
        return getText(articleBody);
    }

    public int getParagraphCount() {
        return paragraphs.size();
    }

    public boolean bodyContains(String keyword) {
        return getArticleBodyText().toLowerCase().contains(keyword.toLowerCase());
    }

    public boolean isTableOfContentsDisplayed() {
        return isDisplayed(tableOfContents);
    }

    public int getTabCount() {
        return pageTabs.size();
    }
}
