package ge.automation.pages;

import ge.automation.config.ConfigReader;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
        this.wait.ignoring(StaleElementReferenceException.class);

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

    protected WebElement waitUntilPresent(WebElement element) {
        wait.until(driver -> isPresent(element));
        return element;
    }

    protected WebElement revealElement(WebElement element) {
        waitUntilPresent(element);
        if (!element.isDisplayed()) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.opacity = '1';"
                            + "arguments[0].style.visibility = 'visible';", element);
        }
        return element;
    }

    protected boolean waitUntilUrlDoesNotContain(String part) {
        return wait.until(ExpectedConditions.not(ExpectedConditions.urlContains(part)));
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

    protected boolean isPresent(WebElement element) {
        try {
            return element.isEnabled();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected void selectByVisibleText(WebElement selectElement, String visibleText) {
        new Select(revealElement(selectElement)).selectByVisibleText(visibleText);
    }

    protected void selectByValue(WebElement selectElement, String value) {
        new Select(revealElement(selectElement)).selectByValue(value);
    }

    protected List<WebElement> getSelectOptions(WebElement selectElement) {
        return new Select(revealElement(selectElement)).getOptions();
    }

    protected String getSelectedOption(WebElement selectElement) {
        return optionText(new Select(revealElement(selectElement)).getFirstSelectedOption());
    }

    protected String readDomProperty(WebElement element, String property) {
        return wait.until(driver -> {
            try {
                String value = element.getDomProperty(property);
                return value == null ? "" : value;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    protected String optionText(WebElement option) {
        String text = option.getDomProperty("text");
        return text == null ? "" : text.trim();
    }

    protected void setCheckbox(WebElement checkbox, WebElement label, boolean checked) {
        waitUntilPresent(checkbox);
        if (checkbox.isSelected() != checked) {
            click(label);
        }
    }

    protected void hoverOver(WebElement element) {
        actions.moveToElement(waitUntilVisible(element)).perform();
    }

    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
