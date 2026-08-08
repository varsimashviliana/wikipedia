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

    @FindBy(id = "wpLoginAttempt")
    private WebElement loginButton;

    @FindBy(css = ".cdx-message--error")
    private WebElement errorMessage;

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

    public boolean isErrorDisplayed() {
        return !driver.findElements(By.cssSelector(".cdx-message--error")).isEmpty();
    }

    public boolean isCaptchaPresent() {
        return !driver.findElements(
                By.cssSelector("[class*='h-captcha'], [data-hcaptcha-widget-id], iframe[src*='hcaptcha']")
        ).isEmpty();
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }
}
