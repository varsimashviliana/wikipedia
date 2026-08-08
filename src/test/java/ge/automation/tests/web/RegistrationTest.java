package ge.automation.tests.web;

import ge.automation.pages.CreateAccountPage;
import ge.automation.tests.BaseTest;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationTest extends BaseTest {

    private CreateAccountPage registerPage;

    @BeforeMethod(alwaysRun = true)
    public void openRegistrationPage() {
        registerPage = new CreateAccountPage(driver).open();
    }

    @Test(priority = 1,
          groups = {"smoke", "registration"},
          description = "რეგისტრაციის ფორმა იხსნება ყველა საჭირო ველით")
    public void registrationFormOpensWithAllFields() {
        Assert.assertTrue(registerPage.isPageOpened(),
                "რეგისტრაციის ფორმა არ გაიხსნა");

        Assert.assertTrue(registerPage.areAllFieldsDisplayed(),
                "ფორმის ყველა ველი არ ჩანს "
                        + "(username, password, retype, email, submit ღილაკი)");

        System.out.println("      ღილაკი: '" + registerPage.getSubmitButtonText() + "'");
        Assert.assertTrue(registerPage.isSubmitButtonEnabled(),
                "'Create your account' ღილაკი აქტიური არ არის");
    }

    @Test(priority = 2,
          groups = {"smoke", "registration"},
          description = "სავალდებულო და არასავალდებულო ველების ვალიდაცია")
    public void requiredFieldsAreMarkedCorrectly() {
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(registerPage.isUsernameRequired(),
                "username სავალდებულო უნდა იყოს");
        softAssert.assertTrue(registerPage.isPasswordRequired(),
                "password სავალდებულო უნდა იყოს");
        softAssert.assertTrue(registerPage.isRetypeRequired(),
                "პაროლის გამეორება სავალდებულო უნდა იყოს");

        softAssert.assertFalse(registerPage.isEmailRequired(),
                "email არასავალდებულო უნდა იყოს");

        softAssert.assertAll();
        System.out.println("      required ველები სწორადაა მონიშნული ✓");
    }

    @Test(priority = 3,
          groups = {"smoke", "registration"},
          description = "პაროლის ველი დაფარულია, email ველს სწორი ტიპი აქვს")
    public void fieldTypesAreCorrect() {
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(registerPage.getPasswordFieldType(), "password",
                "პაროლის ველი type='password' უნდა იყოს, რომ ტექსტი დაიფაროს");

        softAssert.assertEquals(registerPage.getEmailFieldType(), "email",
                "email ველი type='email' უნდა იყოს");

        softAssert.assertAll();
        System.out.println("      ველების ტიპები სწორია ✓");
    }

    @Test(priority = 4,
          groups = {"regression", "registration"},
          description = "რეგისტრაციის გვერდი CAPTCHA-თი არის დაცული")
    public void registrationPageIsProtectedByCaptcha() {
        boolean captchaFound = registerPage.isCaptchaPresent();

        System.out.println("      CAPTCHA გვერდზე: " + (captchaFound ? "არის ✓" : "არ არის"));

        Assert.assertTrue(captchaFound,
                "რეგისტრაციის გვერდზე CAPTCHA ვერ მოიძებნა. "
                        + "ეს ან ვიკიპედიის ცვლილებაა, ან ლოკატორი მოძველდა.");
    }
}
