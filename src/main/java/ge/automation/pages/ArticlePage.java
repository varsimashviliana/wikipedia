package ge.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ArticlePage extends BasePage {

    @FindBy(id = "firstHeading")
    private WebElement articleTitle;

    @FindBy(css = "#mw-content-text p")
    private List<WebElement> paragraphs;

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

    public int getParagraphCount() {
        return paragraphs.size();
    }
}
