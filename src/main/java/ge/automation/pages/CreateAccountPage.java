package ge.automation.pages;

import ge.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * CreateAccountPage — ვიკიპედიის რეგისტრაციის გვერდი.
 *
 * <p><b>მნიშვნელოვანი შენიშვნა (გულახდილად):</b><br>
 * ამ გვერდზე ჩაშენებულია <b>hCaptcha</b> ("მე რობოტი არ ვარ" ტიპის დაცვა).
 * ეს ნიშნავს, რომ <u>რეალურ ანგარიშს ავტომატიზაცია ვერ შექმნის</u> —
 * CAPTCHA სწორედ ამისთვისაა შექმნილი და მისი გვერდის ავლა არც სწორია
 * და არც დასაშვები.</p>
 *
 * <p>ამიტომ ამ გვერდზე ვწერთ <b>ნეგატიურ / ვალიდაციის ტესტებს</b>:
 * ვამოწმებთ ფორმის ველების არსებობას, სავალდებულოობას (required),
 * მონაცემების შეყვანას და CAPTCHA-ს არსებობას.
 * ეს რეალურ QA პრაქტიკაში ჩვეულებრივი და სრულიად მისაღები მიდგომაა.</p>
 */
public class CreateAccountPage extends BasePage {

    /** მომხმარებლის სახელი. */
    @FindBy(id = "wpName2")
    private WebElement usernameInput;

    /** პაროლი. */
    @FindBy(id = "wpPassword2")
    private WebElement passwordInput;

    /** პაროლის გამეორება. */
    @FindBy(id = "wpRetype")
    private WebElement retypePasswordInput;

    /** ელფოსტა (არასავალდებულო ველი). */
    @FindBy(id = "wpEmail")
    private WebElement emailInput;

    /** "Create your account" ღილაკი. */
    @FindBy(id = "wpCreateaccount")
    private WebElement createAccountButton;

    /** რეგისტრაციის ფორმა. */
    @FindBy(id = "userlogin2")
    private WebElement createAccountForm;

    /** "username policy" ღილაკი — ინფორმაციული. */
    @FindBy(css = "button[type='button']")
    private WebElement usernamePolicyButton;

    public CreateAccountPage(WebDriver driver) {
        super(driver);
    }

    /** გვერდის გახსნა. */
    public CreateAccountPage open() {
        driver.get(ConfigReader.get("register.url"));
        return this;
    }

    @Override
    public boolean isPageOpened() {
        return isDisplayed(usernameInput)
                && isDisplayed(passwordInput)
                && isDisplayed(retypePasswordInput);
    }

    // ---------------------------------------------------------------
    //  ველების შევსება
    // ---------------------------------------------------------------

    public CreateAccountPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    public CreateAccountPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public CreateAccountPage enterRetypePassword(String password) {
        type(retypePasswordInput, password);
        return this;
    }

    public CreateAccountPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    /** ფორმის სრული შევსება ერთი მეთოდით (ღილაკს არ ვაჭერთ!). */
    public CreateAccountPage fillForm(String username, String password,
                                      String retypePassword, String email) {
        enterUsername(username);
        enterPassword(password);
        enterRetypePassword(retypePassword);
        enterEmail(email);
        return this;
    }

    // ---------------------------------------------------------------
    //  შემოწმებები
    // ---------------------------------------------------------------

    /** ყველა სავალდებულო ველი ჩანს თუ არა. */
    public boolean areAllFieldsDisplayed() {
        return isDisplayed(usernameInput)
                && isDisplayed(passwordInput)
                && isDisplayed(retypePasswordInput)
                && isDisplayed(emailInput)
                && isDisplayed(createAccountButton);
    }

    /**
     * დამხმარე მეთოდი — კითხულობს DOM property-ს და აბრუნებს true/false-ს.
     * getDomProperty() ყოველთვის ტექსტს აბრუნებს, ამიტომ გვჭირდება გარდაქმნა.
     */
    private boolean isPropertyTrue(WebElement element, String propertyName) {
        return "true".equalsIgnoreCase(element.getDomProperty(propertyName));
    }

    /** username ველი სავალდებულოა თუ არა (HTML5 required). */
    public boolean isUsernameRequired() {
        return isPropertyTrue(usernameInput, "required");
    }

    /** password ველი სავალდებულოა თუ არა. */
    public boolean isPasswordRequired() {
        return isPropertyTrue(passwordInput, "required");
    }

    /** პაროლის გამეორების ველი სავალდებულოა თუ არა. */
    public boolean isRetypeRequired() {
        return isPropertyTrue(retypePasswordInput, "required");
    }

    /** email ველი სავალდებულოა თუ არა (მოსალოდნელია false). */
    public boolean isEmailRequired() {
        return isPropertyTrue(emailInput, "required");
    }

    /** აბრუნებს username ველში ჩაწერილ მნიშვნელობას. */
    public String getUsernameValue() {
        return usernameInput.getDomProperty("value");
    }

    /** აბრუნებს email ველში ჩაწერილ მნიშვნელობას. */
    public String getEmailValue() {
        return emailInput.getDomProperty("value");
    }

    /** აბრუნებს პაროლის ველის ტიპს — უნდა იყოს "password" (რომ ტექსტი დაფარული იყოს). */
    public String getPasswordFieldType() {
        return passwordInput.getDomAttribute("type");
    }

    /** აბრუნებს email ველის ტიპს — უნდა იყოს "email". */
    public String getEmailFieldType() {
        return emailInput.getDomAttribute("type");
    }

    /**
     * ამოწმებს, არის თუ არა გვერდზე hCaptcha.
     * თუ არის — ესე იგი ავტომატური რეგისტრაცია შეუძლებელია (და ეს მოსალოდნელია).
     */
    public boolean isCaptchaPresent() {
        return !driver.findElements(
                By.cssSelector("[class*='h-captcha'], [data-hcaptcha-widget-id], iframe[src*='hcaptcha']")
        ).isEmpty();
    }

    /** "Create your account" ღილაკი ჩანს და აქტიურია თუ არა. */
    public boolean isSubmitButtonEnabled() {
        return createAccountButton.isDisplayed() && createAccountButton.isEnabled();
    }

    /** აბრუნებს ღილაკის ტექსტს. */
    public String getSubmitButtonText() {
        return getText(createAccountButton);
    }
}
