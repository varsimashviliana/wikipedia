package ge.automation.tests.web;

import ge.automation.pages.CreateAccountPage;
import ge.automation.tests.BaseTest;
import org.testng.Assert;
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

}
