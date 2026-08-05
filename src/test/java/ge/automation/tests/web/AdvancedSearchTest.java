package ge.automation.tests.web;

import ge.automation.config.ConfigReader;
import ge.automation.pages.SearchResultsPage;
import ge.automation.tests.BaseTest;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * ტესტი №3 — გაფართოებული ძებნა და <b>checkbox-ები</b>.
 *
 * <p>ძებნის შედეგების გვერდზე არის ~28 checkbox ("Search in: Article, Talk, User...").
 * ესენი განსაზღვრავს, სად მოძებნოს ვიკიპედიამ.</p>
 *
 * <p>ფარავს: checkbox-ის მონიშვნა/მოხსნა, isSelected(), SoftAssert, scroll.</p>
 */
public class AdvancedSearchTest extends BaseTest {

    private SearchResultsPage results;

    @BeforeMethod(alwaysRun = true)
    public void openAdvancedSearch() {
        // profile=advanced — აჩვენე checkbox-ები
        String url = ConfigReader.get("wiki.url")
                + "/w/index.php?search=Selenium&title=Special:Search"
                + "&profile=advanced&fulltext=1&ns0=1";
        driver.get(url);
        results = new SearchResultsPage(driver);
    }

    /**
     * ტესტი 3.1 — checkbox-ები არსებობს და საწყისი მდგომარეობა სწორია.
     */
    @Test(priority = 1,
          groups = {"smoke", "checkbox"},
          description = "namespace checkbox-ები არსებობს, Article ნაგულისხმევად მონიშნულია")
    public void checkboxesArePresentWithCorrectDefaults() {
        int checkboxCount = results.getNamespaceCheckboxCount();
        System.out.println("      ნაპოვნია " + checkboxCount + " checkbox");

        Assert.assertTrue(checkboxCount > 10,
                "checkbox-ები ვერ მოიძებნა. ნაპოვნია: " + checkboxCount);

        SoftAssert softAssert = new SoftAssert();

        // Article (ns0) ნაგულისხმევად მონიშნული უნდა იყოს — ჩვენ ns0=1 გადავეცით URL-ში
        softAssert.assertTrue(results.isArticleNamespaceSelected(),
                "Article checkbox მონიშნული უნდა იყოს");

        // Talk და User ნაგულისხმევად მოხსნილი უნდა იყოს
        softAssert.assertFalse(results.isTalkNamespaceSelected(),
                "Talk checkbox მოხსნილი უნდა იყოს");
        softAssert.assertFalse(results.isUserNamespaceSelected(),
                "User checkbox მოხსნილი უნდა იყოს");

        softAssert.assertAll();
    }

    /**
     * ტესტი 3.2 — checkbox-ის მონიშვნა და მოხსნა.
     */
    @Test(priority = 2,
          groups = {"smoke", "checkbox"},
          description = "checkbox-ის მონიშვნა და მონიშვნის მოხსნა")
    public void canCheckAndUncheckCheckbox() {
        // --- მონიშვნა ---
        results.checkTalkNamespace();
        Assert.assertTrue(results.isTalkNamespaceSelected(),
                "Talk checkbox მონიშვნის შემდეგ მონიშნული არ არის");
        System.out.println("      Talk checkbox მოინიშნა ✓");

        // --- მოხსნა ---
        results.uncheckTalkNamespace();
        Assert.assertFalse(results.isTalkNamespaceSelected(),
                "Talk checkbox მოხსნის შემდეგ ისევ მონიშნულია");
        System.out.println("      Talk checkbox მოიხსნა ✓");
    }

    /**
     * ტესტი 3.3 — რამდენიმე checkbox ერთდროულად.
     */
    @Test(priority = 3,
          groups = {"regression", "checkbox"},
          description = "რამდენიმე checkbox-ის ერთდროული მონიშვნა")
    public void canCheckMultipleCheckboxes() {
        results.checkTalkNamespace();
        results.checkUserNamespace();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(results.isTalkNamespaceSelected(), "Talk არ მოინიშნა");
        softAssert.assertTrue(results.isUserNamespaceSelected(), "User არ მოინიშნა");
        softAssert.assertTrue(results.isArticleNamespaceSelected(),
                "Article-ს მონიშვნა არ უნდა მოხსნილიყო");
        softAssert.assertAll();

        System.out.println("      3 checkbox ერთდროულად მონიშნულია ✓");
    }

    /**
     * ტესტი 3.4 — ძებნის შედეგები checkbox-ის მდგომარეობის მიუხედავად არსებობს.
     */
    @Test(priority = 4,
          groups = {"regression", "checkbox"},
          description = "ძებნის შედეგები გვერდზე ჩანს")
    public void searchResultsAreDisplayed() {
        int count = results.getResultCount();
        System.out.println("      შედეგები: " + count);
        System.out.println("      " + results.getResultsInfoText());

        Assert.assertTrue(count > 0, "შედეგები არ ჩანს");
        Assert.assertTrue(results.anyResultContains("selenium"),
                "შედეგებში 'Selenium' არ არის");
    }
}
