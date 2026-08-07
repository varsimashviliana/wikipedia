package ge.automation.pages;

import ge.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(id = "wpName1")
    private WebElement usernameInput;

    @FindBy(id = "wpPassword1")
    private WebElement passwordInput;

    @FindBy(id = "wpRemember")
    private WebElement rememberMeCheckbox;

    @FindBy(id = "wpLoginAttempt")
    private WebElement loginButton;

    @FindBy(css = ".cdx-message--error")
    private WebElement errorMessage;

    @FindBy(css = "form[name='userlogin']")
    private WebElement loginForm;

    @FindBy(xpath = "//a[contains(@href,'CreateAccount')]")
    private WebElement joinWikipediaLink;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(ConfigReader.get("login.url"));
        return this;
    }

    @Override
    public boolean isPageOpened() {
        return isDisplayed(usernameInput) && isDisplayed(passwordInput);
    }

    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public LoginPage clickLogin() {
        click(loginButton);
        return this;
    }

    public LoginPage loginWith(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }

    public LoginPage checkRememberMe() {
        checkCheckbox(rememberMeCheckbox);
        return this;
    }

    public boolean isRememberMeSelected() {
        return rememberMeCheckbox.isSelected();
    }

    public boolean isErrorDisplayed() {
        return !driver.findElements(By.cssSelector(".cdx-message--error")).isEmpty();
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isLoginFormDisplayed() {
        return isDisplayed(loginForm);
    }

    public boolean isUsernameRequired() {
        return "true".equals(usernameInput.getDomProperty("required"));
    }

    public String getUsernameValue() {
        return usernameInput.getDomProperty("value");
    }

    public CreateAccountPage goToCreateAccount() {
        click(joinWikipediaLink);
        return new CreateAccountPage(driver);
    }
}
