package ge.automation.tests.web;

import ge.automation.config.ConfigReader;
import ge.automation.pages.LoginPage;
import ge.automation.tests.BaseTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        loginPage = new LoginPage(driver).open();
    }

    @Test(priority = 2,
          groups = {"smoke", "login"},
          description = "არასწორი მომხმარებელი/პაროლი → შეცდომის შეტყობინება")
    public void loginWithInvalidCredentialsShowsError() {
        loginPage.loginWith(
                ConfigReader.get("invalid.username"),
                ConfigReader.get("invalid.password"));

        skipIfCaptchaBlocked();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "არასწორი მონაცემებით შეცდომა არ გამოჩნდა");

        String error = loginPage.getErrorMessage();
        System.out.println("      შეცდომა: " + error);

        Assert.assertTrue(error.contains(ConfigReader.get("login.error.message")),
                "შეცდომის ტექსტი მოსალოდნელს არ ემთხვევა.\n"
                        + "  მოსალოდნელი: " + ConfigReader.get("login.error.message") + "\n"
                        + "  რეალური:     " + error);
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][]{
                {"AnaVarsimashviliQA1001", "WrongPassword2026!", "არარსებული მომხმარებელი"},
                {"AnaVarsimashviliQA1002", "12345",              "მოკლე პაროლი"},
                {"!!!AnaVarsimashvili!!!", "WrongPassword2026!", "დაუშვებელი სიმბოლოები სახელში"}
        };
    }

    @Test(priority = 5,
          groups = {"regression", "login"},
          dataProvider = "invalidCredentials",
          description = "არასწორი მონაცემების სხვადასხვა კომბინაცია")
    public void loginFailsForVariousInvalidInputs(String username, String password, String caseName) {
        System.out.println("      შემთხვევა: " + caseName);

        loginPage.loginWith(username, password);

        skipIfCaptchaBlocked();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "'" + caseName + "' — შეცდომა არ გამოჩნდა, თუმცა უნდა გამოჩენილიყო");
    }

    private void skipIfCaptchaBlocked() {
        if (loginPage.isCaptchaPresent()) {
            throw new SkipException(
                    "გამოტოვებულია: ვიკიპედიამ hCaptcha გამოიტანა, ამიტომ ლოგინი ვერ დამუშავდა "
                            + "და შეცდომის შეტყობინება ვერ გამოჩნდა. ეს ანტი-ბოტ დაცვაა, რომელიც "
                            + "ერთი IP-დან განმეორებითი წარუმატებელი ლოგინების შემდეგ ირთვება. "
                            + "ცოტა ხნის შემდეგ ხელახლა გაშვება დაეხმარება.");
        }
    }

    @Test(priority = 6,
          groups = {"regression", "login"},
          description = "პოზიტიური ლოგინი — გამოტოვდება თუ მონაცემები არ არის")
    public void loginWithValidCredentials() {
        if (!ConfigReader.has("valid.username") || !ConfigReader.has("valid.password")) {
            throw new SkipException(
                    "გამოტოვებულია: config.properties-ში valid.username / valid.password "
                            + "არ არის შევსებული. თუ გაქვს ვიკიპედიის ანგარიში, "
                            + "შეავსე ისინი და ეს ტესტი გაეშვება.");
        }

        loginPage.loginWith(
                ConfigReader.get("valid.username"),
                ConfigReader.get("valid.password"));

        Assert.assertFalse(loginPage.isErrorDisplayed(),
                "სწორი მონაცემებით ლოგინისას შეცდომა გამოჩნდა: "
                        + (loginPage.isErrorDisplayed() ? loginPage.getErrorMessage() : ""));

        System.out.println("      ლოგინი წარმატებულია ✓");
    }
}
