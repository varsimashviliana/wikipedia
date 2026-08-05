package ge.automation.tests.web;

import ge.automation.config.ConfigReader;
import ge.automation.pages.PortalPage;
import ge.automation.tests.BaseTest;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * ტესტი №2 — ენების <b>dropdown</b> (&lt;select&gt; ელემენტი).
 *
 * <p>დავალება ითხოვდა საიტს, სადაც სხვადასხვა ტიპის ელემენტებია —
 * dropdown, checkbox და ა.შ. ვიკიპედიის პორტალზე არის ნამდვილი
 * <code>&lt;select&gt;</code> 70+ ენით.</p>
 *
 * <p>ფარავს: Selenium-ის <code>Select</code> კლასი, SoftAssert, Actions (hover).</p>
 */
public class LanguageDropdownTest extends BaseTest {

    private PortalPage portal;

    /**
     * ამ კლასის ყოველი ტესტის წინ პორტალს ვხსნით.
     *
     * <p>ყურადღება: ეს @BeforeMethod <b>მშობლის</b> @BeforeMethod-ის შემდეგ სრულდება.
     * ანუ ჯერ ბრაუზერი იხსნება (BaseTest), მერე გვერდი (აქ).</p>
     */
    @BeforeMethod(alwaysRun = true)
    public void openPortal() {
        driver.get(ConfigReader.get("portal.url"));
        portal = new PortalPage(driver);
    }

    /**
     * ტესტი 2.1 — dropdown არსებობს და ენები აქვს.
     */
    @Test(priority = 1,
          groups = {"smoke", "dropdown"},
          description = "ენების dropdown იტვირთება და შეიცავს ბევრ ენას")
    public void languageDropdownIsPopulated() {
        Assert.assertTrue(portal.isPageOpened(), "პორტალი არ გაიხსნა");

        int languageCount = portal.getLanguageCount();
        System.out.println("      dropdown-ში არის " + languageCount + " ენა");

        // 50-ზე მეტს ვამოწმებთ და არა ზუსტ რიცხვს —
        // ვიკიპედიამ შეიძლება ენა დაამატოს და ტესტი უმიზეზოდ ჩავარდეს
        Assert.assertTrue(languageCount > 50,
                "dropdown-ში ძალიან ცოტა ენაა: " + languageCount);
    }

    /**
     * ტესტი 2.2 — ენის არჩევა ჩანს ტექსტით.
     *
     * <p><b>SoftAssert</b> — ჩვეულებრივი Assert პირველივე შეცდომაზე ჩერდება.
     * SoftAssert კი ყველა შემოწმებას ბოლომდე მიიყვანს და მერე ერთად აჩვენებს,
     * რომელი ჩავარდა. სასარგებლოა, როცა რამდენიმე რამის შემოწმება გვინდა.
     * <u>მთავარია ბოლოს assertAll() არ დაგვავიწყდეს</u> — მის გარეშე
     * SoftAssert-ის შეცდომები არ დაფიქსირდება!</p>
     */
    @Test(priority = 2,
          groups = {"smoke", "dropdown"},
          description = "ენის არჩევა dropdown-იდან ტექსტით და კოდით")
    public void selectLanguageFromDropdown() {
        SoftAssert softAssert = new SoftAssert();

        // --- არჩევა ჩანს ტექსტით ---
        portal.selectLanguageByText("Deutsch");
        String selected = portal.getSelectedLanguage();
        System.out.println("      არჩეულია: " + selected);
        softAssert.assertEquals(selected, "Deutsch",
                "Deutsch-ის არჩევა ვერ მოხერხდა");

        // --- არჩევა კოდით (value ატრიბუტით) ---
        portal.selectLanguageByValue("fr");
        String selectedByValue = portal.getSelectedLanguage();
        System.out.println("      არჩეულია: " + selectedByValue);
        softAssert.assertEquals(selectedByValue, "Français",
                "ფრანგულის არჩევა კოდით ვერ მოხერხდა");

        // --- კონკრეტული ენების არსებობა ---
        softAssert.assertTrue(portal.hasLanguageOption("English"),
                "სიაში English არ არის");
        softAssert.assertTrue(portal.hasLanguageOption("Español"),
                "სიაში Español არ არის");

        // აუცილებელია! ამის გარეშე ზემოთა შემოწმებები არ დაფიქსირდება
        softAssert.assertAll();
    }

    /**
     * ტესტი 2.3 — ძებნა არჩეულ ენაზე.
     * dropdown-ში გერმანულს ვირჩევთ და ვამოწმებთ, რომ გერმანულ ვიკიპედიაზე გადავდივართ.
     */
    @Test(priority = 3,
          groups = {"regression", "dropdown"},
          description = "dropdown-ით ენის არჩევა და ამ ენაზე ძებნა")
    public void searchInSelectedLanguage() {
        portal.selectLanguageByValue("de");
        portal.searchFor("Automatisierung");

        String url = driver.getCurrentUrl();
        System.out.println("      გადავედით: " + url);

        Assert.assertTrue(url.contains("de.wikipedia.org"),
                "გერმანულ ვიკიპედიაზე არ გადავედით. URL: " + url);
    }

    /**
     * ტესტი 2.4 — მაუსის მოქმედება (hover) Actions კლასით.
     * სილაბუსი, მე-13 ლექცია: "Mouse და Keyboard მოქმედებები".
     */
    @Test(priority = 4,
          groups = {"regression", "dropdown"},
          description = "მაუსის მიტანა ენის ბლოკზე (Actions)")
    public void hoverOverLanguageBlock() {
        Assert.assertTrue(portal.isLogoDisplayed(), "ლოგო არ ჩანს");

        portal.hoverOverEnglish();

        int featured = portal.getFeaturedLanguageCount();
        System.out.println("      მთავარ გვერდზე " + featured + " ენაა გამოტანილი");

        Assert.assertTrue(featured >= 5,
                "მთავარ გვერდზე ძალიან ცოტა ენაა: " + featured);
    }
}
