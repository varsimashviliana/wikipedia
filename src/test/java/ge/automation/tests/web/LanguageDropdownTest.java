package ge.automation.tests.web;

import ge.automation.config.ConfigReader;
import ge.automation.pages.PortalPage;
import ge.automation.tests.BaseTest;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LanguageDropdownTest extends BaseTest {

    private PortalPage portal;

    @BeforeMethod(alwaysRun = true)
    public void openPortal() {
        driver.get(ConfigReader.get("portal.url"));
        portal = new PortalPage(driver);
    }

    @Test(priority = 2,
          groups = {"smoke", "dropdown"},
          description = "ენის არჩევა dropdown-იდან ტექსტით და კოდით")
    public void selectLanguageFromDropdown() {
        SoftAssert softAssert = new SoftAssert();

        portal.selectLanguageByText("Deutsch");
        String selected = portal.getSelectedLanguage();
        System.out.println("      არჩეულია: " + selected);
        softAssert.assertEquals(selected, "Deutsch",
                "Deutsch-ის არჩევა ვერ მოხერხდა");

        portal.selectLanguageByValue("fr");
        String selectedByValue = portal.getSelectedLanguage();
        System.out.println("      არჩეულია: " + selectedByValue);
        softAssert.assertEquals(selectedByValue, "Français",
                "ფრანგულის არჩევა კოდით ვერ მოხერხდა");

        softAssert.assertTrue(portal.hasLanguageOption("English"),
                "სიაში English არ არის");
        softAssert.assertTrue(portal.hasLanguageOption("Español"),
                "სიაში Español არ არის");

        softAssert.assertAll();
    }

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
