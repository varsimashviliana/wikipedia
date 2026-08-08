package ge.automation.pages;

import ge.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CreateAccountPage extends BasePage {

    @FindBy(id = "wpName2")
    private WebElement usernameInput;

    @FindBy(id = "wpPassword2")
    private WebElement passwordInput;

    @FindBy(id = "wpRetype")
    private WebElement retypePasswordInput;

    @FindBy(id = "wpEmail")
    private WebElement emailInput;

    @FindBy(id = "wpCreateaccount")
    private WebElement createAccountButton;

    public CreateAccountPage(WebDriver driver) {
        super(driver);
    }

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

    public boolean areAllFieldsDisplayed() {
        return isDisplayed(usernameInput)
                && isDisplayed(passwordInput)
                && isDisplayed(retypePasswordInput)
                && isDisplayed(emailInput)
                && isDisplayed(createAccountButton);
    }

    private boolean isPropertyTrue(WebElement element, String propertyName) {
        return "true".equalsIgnoreCase(element.getDomProperty(propertyName));
    }

    public boolean isUsernameRequired() {
        return isPropertyTrue(usernameInput, "required");
    }

    public boolean isPasswordRequired() {
        return isPropertyTrue(passwordInput, "required");
    }

    public boolean isRetypeRequired() {
        return isPropertyTrue(retypePasswordInput, "required");
    }

    public boolean isEmailRequired() {
        return isPropertyTrue(emailInput, "required");
    }

    public String getPasswordFieldType() {
        return passwordInput.getDomAttribute("type");
    }

    public String getEmailFieldType() {
        return emailInput.getDomAttribute("type");
    }

    public boolean isCaptchaPresent() {
        return !driver.findElements(
                By.cssSelector("[class*='h-captcha'], [data-hcaptcha-widget-id], iframe[src*='hcaptcha']")
        ).isEmpty();
    }

    public boolean isSubmitButtonEnabled() {
        return createAccountButton.isDisplayed() && createAccountButton.isEnabled();
    }

    public String getSubmitButtonText() {
        return getText(createAccountButton);
    }
}
