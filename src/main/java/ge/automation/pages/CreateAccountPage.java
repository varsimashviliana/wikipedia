package ge.automation.pages;

import ge.automation.config.ConfigReader;
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

    public boolean isSubmitButtonEnabled() {
        return createAccountButton.isDisplayed() && createAccountButton.isEnabled();
    }

    public String getSubmitButtonText() {
        return getText(createAccountButton);
    }
}
