package ge.automation.pages;

import ge.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;

    protected BasePage(WebDriver driver) {
        this.driver = driver;

        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait")));

        this.actions = new Actions(driver);

        PageFactory.initElements(driver, this);
    }

    public abstract boolean isPageOpened();

    protected WebElement waitUntilVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitUntilClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement fluentWaitFor(By locator) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(ConfigReader.getInt("fluent.wait.timeout")))
                .pollingEvery(Duration.ofSeconds(ConfigReader.getInt("fluent.wait.polling")))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        return fluentWait.until(d -> d.findElement(locator));
    }

    protected boolean waitUntilUrlContains(String part) {
        return wait.until(ExpectedConditions.urlContains(part));
    }

    protected void click(WebElement element) {
        waitUntilClickable(element).click();
    }

    protected void type(WebElement element, String text) {
        WebElement el = waitUntilVisible(element);
        el.clear();
        el.sendKeys(text);
    }

    protected void typeAndEnter(WebElement element, String text) {
        WebElement el = waitUntilVisible(element);
        el.clear();
        el.sendKeys(text);
        el.sendKeys(Keys.ENTER);
    }

    protected String getText(WebElement element) {
        return waitUntilVisible(element).getText().trim();
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected void selectByVisibleText(WebElement selectElement, String visibleText) {
        new Select(waitUntilVisible(selectElement)).selectByVisibleText(visibleText);
    }

    protected void selectByValue(WebElement selectElement, String value) {
        new Select(waitUntilVisible(selectElement)).selectByValue(value);
    }

    protected List<WebElement> getSelectOptions(WebElement selectElement) {
        return new Select(waitUntilVisible(selectElement)).getOptions();
    }

    protected String getSelectedOption(WebElement selectElement) {
        return new Select(waitUntilVisible(selectElement))
                .getFirstSelectedOption().getText().trim();
    }

    protected void checkCheckbox(WebElement checkbox) {
        WebElement el = waitUntilClickable(checkbox);
        if (!el.isSelected()) {
            el.click();
        }
    }

    protected void uncheckCheckbox(WebElement checkbox) {
        WebElement el = waitUntilClickable(checkbox);
        if (el.isSelected()) {
            el.click();
        }
    }

    protected void hoverOver(WebElement element) {
        actions.moveToElement(waitUntilVisible(element)).perform();
    }

    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
