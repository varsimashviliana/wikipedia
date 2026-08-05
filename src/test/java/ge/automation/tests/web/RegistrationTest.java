package ge.automation.tests.web;

import ge.automation.pages.CreateAccountPage;
import ge.automation.pages.LoginPage;
import ge.automation.tests.BaseTest;
import ge.automation.utils.RandomDataGenerator;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * ტესტი №5 — <b>რეგისტრაცია</b> (დავალების მე-4 პუნქტი).
 *
 * <p><b>მნიშვნელოვანი და გულახდილი შენიშვნა:</b><br>
 * ვიკიპედიის რეგისტრაციის გვერდზე ჩაშენებულია <b>hCaptcha</b>.
 * ეს ნიშნავს, რომ ავტომატიზაციას <u>რეალური ანგარიშის შექმნა ფიზიკურად არ შეუძლია</u> —
 * CAPTCHA ზუსტად ამისთვის არსებობს და მისი გვერდის ავლა არასწორია.</p>
 *
 * <p>ამიტომ აქ ვწერთ იმას, რაც <b>რეალურად შესაძლებელი და სასარგებლოა</b>:
 * ფორმის ვალიდაციის ტესტებს. QA-ს პრაქტიკაში ეს სრულიად ნორმალური მიდგომაა —
 * ვამოწმებთ ყველაფერს CAPTCHA-მდე.</p>
 *
 * <p>ფარავს: ფორმის ელემენტების ვალიდაცია, HTML5 required, ველების ტიპები,
 * დინამიური მონაცემების გენერაცია, ნავიგაცია გვერდებს შორის.</p>
 */
public class RegistrationTest extends BaseTest {

    private CreateAccountPage registerPage;

    @BeforeMethod(alwaysRun = true)
    public void openRegistrationPage() {
        registerPage = new CreateAccountPage(driver).open();
    }

    /**
     * ტესტი 5.1 — რეგისტრაციის ფორმა იხსნება და ყველა ველი ადგილზეა.
     */
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

    /**
     * ტესტი 5.2 — სავალდებულო ველების შემოწმება (HTML5 <code>required</code>).
     *
     * <p>ვიკიპედიაზე username, password და retype სავალდებულოა,
     * email კი — <b>არა</b>. ეს რეალურად შევამოწმეთ საიტზე.</p>
     */
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

        // email არასავალდებულოა — ვიკიპედია მას არ ითხოვს
        softAssert.assertFalse(registerPage.isEmailRequired(),
                "email არასავალდებულო უნდა იყოს");

        softAssert.assertAll();
        System.out.println("      required ველები სწორადაა მონიშნული ✓");
    }

    /**
     * ტესტი 5.3 — ველების ტიპები.
     * პაროლის ველი <code>type="password"</code> უნდა იყოს, რომ ტექსტი დაიფაროს.
     * ეს <b>უსაფრთხოების</b> შემოწმებაა.
     */
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

    /**
     * ტესტი 5.4 — ფორმის შევსება <b>დინამიური მონაცემებით</b>.
     *
     * <p><b>სილაბუსი, მე-19 ლექცია:</b> "დინამიური მონაცემების გენერაცია".<br>
     * ყოველ გაშვებაზე ახალი, უნიკალური მონაცემები გენერირდება.</p>
     *
     * <p>ღილაკს <u>განზრახ არ ვაჭერთ</u> — CAPTCHA-ს გამო რეგისტრაცია
     * მაინც ვერ დასრულდება, და უაზრო მოთხოვნებს ვიკიპედიას არ ვუგზავნით.</p>
     */
    @Test(priority = 4,
          groups = {"regression", "registration"},
          description = "ფორმის შევსება უნიკალური დინამიური მონაცემებით")
    public void canFillFormWithDynamicData() {
        String username = RandomDataGenerator.username();
        String password = RandomDataGenerator.password();
        String email = RandomDataGenerator.email();

        System.out.println("      გენერირებული მომხმარებელი: " + username);
        System.out.println("      გენერირებული ელფოსტა:      " + email);

        registerPage.fillForm(username, password, password, email);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(registerPage.getUsernameValue(), username,
                "username ველში ტექსტი არასწორად ჩაიწერა");
        softAssert.assertEquals(registerPage.getEmailValue(), email,
                "email ველში ტექსტი არასწორად ჩაიწერა");
        softAssert.assertAll();

        System.out.println("      ფორმა შევსებულია ✓ (ღილაკს განზრახ არ ვაჭერთ)");
    }

    /**
     * ტესტი 5.5 — CAPTCHA-ს არსებობის დოკუმენტირება.
     *
     * <p>ეს ტესტი <b>ადასტურებს</b>, რომ გვერდზე CAPTCHA არის.
     * ანუ ის არა მხოლოდ არ არის პრობლემა — არამედ თვითონაა
     * ღირებული შემოწმება: თუ ვიკიპედიამ CAPTCHA მოხსნა,
     * ეს ტესტი ჩავარდება და ჩვენ ამას მაშინვე გავიგებთ.</p>
     */
    @Test(priority = 5,
          groups = {"regression", "registration"},
          description = "რეგისტრაციის გვერდი CAPTCHA-თი არის დაცული")
    public void registrationPageIsProtectedByCaptcha() {
        boolean captchaFound = registerPage.isCaptchaPresent();

        System.out.println("      CAPTCHA გვერდზე: " + (captchaFound ? "არის ✓" : "არ არის"));

        Assert.assertTrue(captchaFound,
                "რეგისტრაციის გვერდზე CAPTCHA ვერ მოიძებნა. "
                        + "ეს ან ვიკიპედიის ცვლილებაა, ან ლოკატორი მოძველდა.");

        System.out.println("      → ამიტომ ავტომატური რეგისტრაცია შეუძლებელია (მოსალოდნელია)");
    }

    /**
     * ტესტი 5.6 — ნავიგაცია ლოგინიდან რეგისტრაციაზე.
     * ამოწმებს, რომ ორი გვერდი ერთმანეთთანაა დაკავშირებული.
     */
    @Test(priority = 6,
          groups = {"regression", "registration"},
          description = "ლოგინის გვერდიდან რეგისტრაციაზე გადასვლა")
    public void canNavigateFromLoginToRegistration() {
        LoginPage loginPage = new LoginPage(driver).open();
        Assert.assertTrue(loginPage.isPageOpened(), "ლოგინის გვერდი არ გაიხსნა");

        CreateAccountPage createAccount = loginPage.goToCreateAccount();

        Assert.assertTrue(createAccount.isPageOpened(),
                "'Join Wikipedia'-ზე დაჭერის შემდეგ რეგისტრაციის ფორმა არ გაიხსნა");

        System.out.println("      ლოგინი → რეგისტრაცია ნავიგაცია მუშაობს ✓");
    }
}
