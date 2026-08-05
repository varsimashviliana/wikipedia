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

/**
 * BasePage — ყველა გვერდის მშობელი კლასი (Page Object Model-ის საფუძველი).
 *
 * <p><b>რატომ abstract?</b><br>
 * "BasePage" თავისთავად არცერთი რეალური გვერდი არ არის — ის მხოლოდ საერთო
 * ქცევას ინახავს. ამიტომ მისი ობიექტის შექმნა აზრს მოკლებულია და
 * <code>abstract</code>-ით ვკრძალავთ. (სილაბუსი, მე-8 ლექცია: Abstract Classes)</p>
 *
 * <p><b>რა არის აქ:</b>
 * <ul>
 *   <li>PageFactory-ის ინიციალიზაცია — @FindBy ანოტაციები რომ იმუშაოს</li>
 *   <li>Explicit Wait (WebDriverWait)</li>
 *   <li>Fluent Wait (FluentWait)</li>
 *   <li>Actions — მაუსის და კლავიატურის მოქმედებები</li>
 *   <li>საერთო დამხმარე მეთოდები (click, type, getText...)</li>
 * </ul>
 */
public abstract class BasePage {

    /** protected — შვილობილ კლასებს ხელი მიუწვდებათ, გარეთ კი დაცულია (ინკაფსულაცია). */
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;

    /**
     * კონსტრუქტორი. (სილაბუსი, მე-7 ლექცია: კონსტრუქტორები)
     *
     * @param driver ბრაუზერი, რომელზეც ეს გვერდი მუშაობს
     */
    protected BasePage(WebDriver driver) {
        this.driver = driver;

        // --- Explicit Wait ---
        // ელოდება კონკრეტულ პირობას (მაგ. "ელემენტი დაკლიკვადი გახდა")
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait")));

        // --- Actions — მაუსი და კლავიატურა (სილაბუსი, მე-13 ლექცია) ---
        this.actions = new Actions(driver);

        // --- PageFactory ---
        // ეს ხაზი "აცოცხლებს" ყველა @FindBy ველს შვილობილ კლასში.
        // მის გარეშე ყველა WebElement null იქნება.
        PageFactory.initElements(driver, this);
    }

    // =================================================================
    //  აბსტრაქტული მეთოდი — ყოველი გვერდი თავისებურად პასუხობს
    //  (სილაბუსი, მე-8 ლექცია: Polymorphism)
    // =================================================================

    /**
     * ამოწმებს, ნამდვილად გაიხსნა თუ არა ეს გვერდი.
     * ყოველი შვილობილი კლასი თავის ლოგიკას წერს.
     */
    public abstract boolean isPageOpened();

    // =================================================================
    //  ლოდინის მეთოდები — სამივე ტიპი (სილაბუსი, მე-13 ლექცია)
    // =================================================================

    /** Explicit Wait: ელოდება სანამ ელემენტი გამოჩნდება. */
    protected WebElement waitUntilVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /** Explicit Wait: ელოდება სანამ ელემენტი დაკლიკვადი გახდება. */
    protected WebElement waitUntilClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Fluent Wait — უფრო მოქნილი ლოდინი.
     * განსხვავება Explicit-თან: აქ თვითონ ვირჩევთ რამდენად ხშირად შემოწმდეს
     * და რომელი გამონაკლისები იგნორირდეს.
     */
    protected WebElement fluentWaitFor(By locator) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(ConfigReader.getInt("fluent.wait.timeout")))
                .pollingEvery(Duration.ofSeconds(ConfigReader.getInt("fluent.wait.polling")))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        return fluentWait.until(d -> d.findElement(locator));
    }

    /** ელოდება სანამ URL მითითებულ ტექსტს შეიცავს. */
    protected boolean waitUntilUrlContains(String part) {
        return wait.until(ExpectedConditions.urlContains(part));
    }

    // =================================================================
    //  საერთო მოქმედებები
    // =================================================================

    /** უსაფრთხო კლიკი — ჯერ დაელოდება, მერე დააჭერს. */
    protected void click(WebElement element) {
        waitUntilClickable(element).click();
    }

    /** ველის გასუფთავება და ტექსტის აკრეფა. */
    protected void type(WebElement element, String text) {
        WebElement el = waitUntilVisible(element);
        el.clear();
        el.sendKeys(text);
    }

    /** ტექსტის აკრეფა და Enter-ის დაჭერა (კლავიატურის მოქმედება). */
    protected void typeAndEnter(WebElement element, String text) {
        WebElement el = waitUntilVisible(element);
        el.clear();
        el.sendKeys(text);
        el.sendKeys(Keys.ENTER);
    }

    /** ელემენტის ტექსტის წაკითხვა. */
    protected String getText(WebElement element) {
        return waitUntilVisible(element).getText().trim();
    }

    /** ამოწმებს ელემენტი ჩანს თუ არა (თუ საერთოდ არ არსებობს — false, გამონაკლისის გარეშე). */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Dropdown-იდან არჩევა ჩანს ტექსტით.
     * Select კლასი მხოლოდ ნამდვილ &lt;select&gt; ტეგზე მუშაობს.
     */
    protected void selectByVisibleText(WebElement selectElement, String visibleText) {
        new Select(waitUntilVisible(selectElement)).selectByVisibleText(visibleText);
    }

    /** Dropdown-იდან არჩევა value ატრიბუტით. */
    protected void selectByValue(WebElement selectElement, String value) {
        new Select(waitUntilVisible(selectElement)).selectByValue(value);
    }

    /** აბრუნებს dropdown-ის ყველა ოფციას. */
    protected List<WebElement> getSelectOptions(WebElement selectElement) {
        return new Select(waitUntilVisible(selectElement)).getOptions();
    }

    /** აბრუნებს ამჟამად არჩეულ ოფციას. */
    protected String getSelectedOption(WebElement selectElement) {
        return new Select(waitUntilVisible(selectElement))
                .getFirstSelectedOption().getText().trim();
    }

    /**
     * Checkbox-ის მონიშვნა — მხოლოდ იმ შემთხვევაში, თუ ჯერ არ არის მონიშნული.
     * (თუ უკვე მონიშნულს დავაჭერთ, მოვხსნით — ეს ხშირი შეცდომაა)
     */
    protected void checkCheckbox(WebElement checkbox) {
        WebElement el = waitUntilClickable(checkbox);
        if (!el.isSelected()) {
            el.click();
        }
    }

    /** Checkbox-იდან მონიშვნის მოხსნა. */
    protected void uncheckCheckbox(WebElement checkbox) {
        WebElement el = waitUntilClickable(checkbox);
        if (el.isSelected()) {
            el.click();
        }
    }

    /** მაუსის მიტანა ელემენტზე (hover) — Actions კლასი. */
    protected void hoverOver(WebElement element) {
        actions.moveToElement(waitUntilVisible(element)).perform();
    }

    /** გვერდის ჩამოქაჩვა ელემენტამდე — JavaScript-ით. */
    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    /** აბრუნებს გვერდის სათაურს (ბრაუზერის ჩანართზე რაც წერია). */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /** აბრუნებს მიმდინარე URL-ს. */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
