package ge.automation.pages;

import ge.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * LoginPage — ვიკიპედიის ლოგინის გვერდი.
 *
 * <p><b>ყურადღება:</b> როცა შედიხარ <code>en.wikipedia.org/wiki/Special:UserLogin</code>-ზე,
 * ვიკიპედია გადაგისვრის <code>auth.wikimedia.org</code>-ზე. ეს ნორმალურია —
 * ავტორიზაცია ცალკე დომენზე ხდება. ტესტში ამიტომ URL-ს
 * <code>auth.wikimedia.org</code>-ზე ვამოწმებთ.</p>
 */
public class LoginPage extends BasePage {

    /** მომხმარებლის სახელის ველი. */
    @FindBy(id = "wpName1")
    private WebElement usernameInput;

    /** პაროლის ველი. */
    @FindBy(id = "wpPassword1")
    private WebElement passwordInput;

    /** "Keep me logged in" checkbox. */
    @FindBy(id = "wpRemember")
    private WebElement rememberMeCheckbox;

    /** "Log in" ღილაკი. */
    @FindBy(id = "wpLoginAttempt")
    private WebElement loginButton;

    /** შეცდომის შეტყობინება (არასწორი მონაცემების შემთხვევაში). */
    @FindBy(css = ".cdx-message--error")
    private WebElement errorMessage;

    /** ლოგინის ფორმა. */
    @FindBy(css = "form[name='userlogin']")
    private WebElement loginForm;

    /** "Join Wikipedia" ბმული — რეგისტრაციაზე გადასასვლელად. */
    @FindBy(xpath = "//a[contains(@href,'CreateAccount')]")
    private WebElement joinWikipediaLink;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** გვერდის გახსნა config-ში მითითებული URL-ით. */
    public LoginPage open() {
        driver.get(ConfigReader.get("login.url"));
        return this;
    }

    @Override
    public boolean isPageOpened() {
        return isDisplayed(usernameInput) && isDisplayed(passwordInput);
    }

    // ---------------------------------------------------------------
    //  მოქმედებები
    // ---------------------------------------------------------------

    /** მომხმარებლის სახელის შეყვანა. */
    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    /** პაროლის შეყვანა. */
    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    /** "Log in" ღილაკზე დაჭერა. */
    public LoginPage clickLogin() {
        click(loginButton);
        return this;
    }

    /**
     * სრული ლოგინის მცდელობა ერთი მეთოდით.
     * ეს ეწოდება "method chaining"-ის ალტერნატივა — ტესტში კოდი მოკლდება.
     */
    public LoginPage loginWith(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }

    /** "Keep me logged in" checkbox-ის მონიშვნა. */
    public LoginPage checkRememberMe() {
        checkCheckbox(rememberMeCheckbox);
        return this;
    }

    /** Remember-me მონიშნულია თუ არა. */
    public boolean isRememberMeSelected() {
        return rememberMeCheckbox.isSelected();
    }

    // ---------------------------------------------------------------
    //  შემოწმებები
    // ---------------------------------------------------------------

    /** შეცდომის შეტყობინება გამოჩნდა თუ არა. */
    public boolean isErrorDisplayed() {
        return !driver.findElements(By.cssSelector(".cdx-message--error")).isEmpty();
    }

    /** აბრუნებს შეცდომის ტექსტს. */
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    /** ლოგინის ფორმა ჩანს თუ არა. */
    public boolean isLoginFormDisplayed() {
        return isDisplayed(loginForm);
    }

    /** ამოწმებს, ველი "required" არის თუ არა (HTML5 ვალიდაცია). */
    public boolean isUsernameRequired() {
        return "true".equals(usernameInput.getDomProperty("required"));
    }

    /** აბრუნებს username ველში ჩაწერილ ტექსტს. */
    public String getUsernameValue() {
        return usernameInput.getDomProperty("value");
    }

    /** რეგისტრაციის გვერდზე გადასვლა. */
    public CreateAccountPage goToCreateAccount() {
        click(joinWikipediaLink);
        return new CreateAccountPage(driver);
    }
}
