package ge.automation.tests.web;

import ge.automation.config.ConfigReader;
import ge.automation.pages.LoginPage;
import ge.automation.tests.BaseTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * ტესტი №4 — <b>ლოგინი</b> (დავალების მე-4 პუნქტი).
 *
 * <p>ფარავს: ფორმის შევსება, ნეგატიური ტესტირება, შეცდომის შეტყობინების
 * ვალიდაცია, checkbox, @DataProvider, SkipException.</p>
 *
 * <p><b>რატომ ნეგატიური ტესტები?</b><br>
 * პოზიტიური ლოგინისთვის რეალური ანგარიში გვჭირდება. ნეგატიური ტესტები
 * კი ანგარიშის გარეშე მუშაობს და <u>უფრო მნიშვნელოვანიც არის</u> —
 * ისინი ამოწმებენ, სისტემა სწორად იცავს თუ არა თავს არასწორი მონაცემებისგან.</p>
 */
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        loginPage = new LoginPage(driver).open();
    }

    /**
     * ტესტი 4.1 — ლოგინის გვერდი იხსნება და რედირექტი სწორად ხდება.
     *
     * <p>ვიკიპედია ლოგინს <code>auth.wikimedia.org</code>-ზე გადაისვრის.
     * ეს წინასწარ შევამოწმეთ საიტზე — ამის ცოდნის გარეშე ტესტი ჩავარდებოდა.</p>
     */
    @Test(priority = 1,
          groups = {"smoke", "login"},
          description = "ლოგინის გვერდი იხსნება და auth დომენზე გადაგვისვრის")
    public void loginPageOpensAndRedirectsToAuthDomain() {
        Assert.assertTrue(loginPage.isPageOpened(),
                "ლოგინის ფორმა არ ჩანს");

        String url = loginPage.getCurrentUrl();
        System.out.println("      URL: " + url);

        Assert.assertTrue(url.contains(ConfigReader.get("auth.domain")),
                "რედირექტი auth დომენზე არ მოხდა. URL: " + url);

        Assert.assertTrue(loginPage.isLoginFormDisplayed(),
                "ლოგინის ფორმა (form[name=userlogin]) ვერ მოიძებნა");
    }

    /**
     * ტესტი 4.2 — არასწორი მონაცემებით ლოგინი → შეცდომა.
     * <b>ეს არის მთავარი ნეგატიური ტესტი.</b>
     */
    @Test(priority = 2,
          groups = {"smoke", "login"},
          description = "არასწორი მომხმარებელი/პაროლი → შეცდომის შეტყობინება")
    public void loginWithInvalidCredentialsShowsError() {
        loginPage.loginWith(
                ConfigReader.get("invalid.username"),
                ConfigReader.get("invalid.password"));

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "არასწორი მონაცემებით შეცდომა არ გამოჩნდა");

        String error = loginPage.getErrorMessage();
        System.out.println("      შეცდომა: " + error);

        Assert.assertTrue(error.contains(ConfigReader.get("login.error.message")),
                "შეცდომის ტექსტი მოსალოდნელს არ ემთხვევა.\n"
                        + "  მოსალოდნელი: " + ConfigReader.get("login.error.message") + "\n"
                        + "  რეალური:     " + error);
    }

    /**
     * ტესტი 4.3 — ცარიელი ველებით ლოგინი.
     *
     * <p>ველები HTML5-ის <code>required</code>-ით არის დაცული,
     * ამიტომ ბრაუზერი ფორმას საერთოდ არ გააგზავნის და გვერდზე დავრჩებით.</p>
     */
    @Test(priority = 3,
          groups = {"smoke", "login"},
          description = "ცარიელი ველებით ლოგინი — ფორმა არ იგზავნება")
    public void loginWithEmptyFieldsIsBlocked() {
        String urlBefore = loginPage.getCurrentUrl();

        loginPage.clickLogin();   // ველების შევსების გარეშე

        // ფორმა არ უნდა გაგზავნილიყო → ისევ ლოგინის გვერდზე ვართ
        Assert.assertTrue(loginPage.isLoginFormDisplayed(),
                "ცარიელი ფორმა გაიგზავნა, რაც არ უნდა მომხდარიყო");

        System.out.println("      ფორმა დაბლოკილია ✓ (URL: " + loginPage.getCurrentUrl() + ")");
        Assert.assertEquals(loginPage.getUsernameValue(), "",
                "username ველი ცარიელი უნდა იყოს");
    }

    /**
     * ტესტი 4.4 — "Keep me logged in" checkbox.
     */
    @Test(priority = 4,
          groups = {"regression", "login"},
          description = "Remember-me checkbox მუშაობს")
    public void rememberMeCheckboxWorks() {
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertFalse(loginPage.isRememberMeSelected(),
                "checkbox ნაგულისხმევად მოხსნილი უნდა იყოს");

        loginPage.checkRememberMe();

        softAssert.assertTrue(loginPage.isRememberMeSelected(),
                "checkbox მონიშვნის შემდეგ მონიშნული უნდა იყოს");

        softAssert.assertAll();
        System.out.println("      Remember-me checkbox მუშაობს ✓");
    }

    /**
     * ტესტი 4.5 — არასწორი მონაცემების რამდენიმე ვარიანტი (@DataProvider).
     */
    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][]{
                {"NoSuchUser_aaa111", "wrongPassword1", "არარსებული მომხმარებელი"},
                {"NoSuchUser_bbb222", "12345",          "მოკლე პაროლი"},
                {"!!!invalid!!!",     "somePassword",   "დაუშვებელი სიმბოლოები სახელში"}
        };
    }

    @Test(priority = 5,
          groups = {"regression", "login"},
          dataProvider = "invalidCredentials",
          description = "არასწორი მონაცემების სხვადასხვა კომბინაცია")
    public void loginFailsForVariousInvalidInputs(String username, String password, String caseName) {
        System.out.println("      შემთხვევა: " + caseName);

        loginPage.loginWith(username, password);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "'" + caseName + "' — შეცდომა არ გამოჩნდა, თუმცა უნდა გამოჩენილიყო");
    }

    /**
     * ტესტი 4.6 — <b>პოზიტიური</b> ლოგინი.
     *
     * <p><b>სილაბუსი, მე-14 ლექცია: "ტესტის გამოტოვების შესაძლებლობა".</b><br>
     * ამ ტესტს რეალური ანგარიში სჭირდება. თუ config.properties-ში
     * <code>valid.username</code> და <code>valid.password</code> ცარიელია,
     * <code>SkipException</code>-ს ვისვრით — ტესტი <b>გამოტოვდება</b> (SKIPPED),
     * და არა ჩავარდება (FAILED).</p>
     *
     * <p>ეს სწორი მიდგომაა: ტესტი, რომელიც გარემოს გამო ვერ შესრულდება,
     * არ უნდა ჩაითვალოს შეცდომად.</p>
     */
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
