package ge.automation.tests.web;

import ge.automation.config.ConfigReader;
import ge.automation.pages.ArticlePage;
import ge.automation.pages.PortalPage;
import ge.automation.pages.SearchResultsPage;
import ge.automation.tests.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {

    @Test(priority = 1,
          groups = {"smoke", "search"},
          description = "პორტალიდან ძებნა Enter-ით და შედეგის შემოწმება")
    public void searchFromPortalUsingEnter() {
        PortalPage portal = new PortalPage(driver);
        driver.get(ConfigReader.get("portal.url"));

        Assert.assertTrue(portal.isPageOpened(),
                "პორტალის გვერდი არ გაიხსნა");

        portal.searchFor("Selenium");

        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("wikipedia.org"),
                "ძებნის შემდეგ ვიკიპედიაზე არ გადავედით. URL: " + url);

        String title = driver.getTitle();
        Assert.assertTrue(title.toLowerCase().contains("selenium"),
                "გვერდის სათაურში 'Selenium' არ არის. სათაური: " + title);
    }

    @Test(priority = 2,
          groups = {"smoke", "search"},
          description = "სრული ძებნა და შედეგების სიის ვალიდაცია")
    public void fullTextSearchReturnsResults() {
        openSearchResults("Automation testing");

        SearchResultsPage results = new SearchResultsPage(driver);

        int count = results.getResultCount();
        Assert.assertTrue(count > 0,
                "შედეგები საერთოდ არ მოიძებნა");

        System.out.println("      მოიძებნა " + count + " შედეგი");
        System.out.println("      ინფო: " + results.getResultsInfoText());

        Assert.assertTrue(results.anyResultContains("automation"),
                "არცერთი შედეგი არ შეიცავს სიტყვას 'automation'. "
                        + "ნაპოვნი სათაურები: " + results.getResultTitles());
    }

    @Test(priority = 3,
          groups = {"regression", "search"},
          description = "ძებნის შედეგიდან სტატიის გახსნა")
    public void openArticleFromSearchResults() {
        openSearchResults("Selenium software");

        SearchResultsPage results = new SearchResultsPage(driver);
        Assert.assertTrue(results.getResultCount() > 0, "შედეგები ცარიელია");

        ArticlePage article = results.openResult(0);

        Assert.assertTrue(article.isPageOpened(),
                "სტატიის გვერდი არ გაიხსნა");

        String articleTitle = article.getArticleTitle();
        System.out.println("      გაიხსნა სტატია: " + articleTitle);

        Assert.assertFalse(articleTitle.isBlank(),
                "სტატიის სათაური ცარიელია");
        Assert.assertTrue(article.getParagraphCount() > 0,
                "სტატიაში აბზაცები არ არის");
    }

    @Test(priority = 4,
          groups = {"regression", "search"},
          description = "არარსებული სიტყვის ძებნა — შედეგი არ უნდა იყოს")
    public void searchForNonExistentTermShowsNoResults() {
        openSearchResults("zzxqwlkjhgfdsapoiuytrewq12345");

        SearchResultsPage results = new SearchResultsPage(driver);

        boolean noResults = results.isNoResultsMessageDisplayed()
                || results.getResultCount() == 0;

        Assert.assertTrue(noResults,
                "არარსებულ სიტყვაზე შედეგები დაბრუნდა, რაც არ უნდა მომხდარიყო");
    }

    @DataProvider(name = "searchTerms")
    public Object[][] searchTerms() {
        return new Object[][]{
                {"Java"},
                {"Python"},
                {"JavaScript"}
        };
    }

    @Test(priority = 5,
          groups = {"regression", "search"},
          dataProvider = "searchTerms",
          description = "სხვადასხვა სიტყვის ძებნა DataProvider-ით")
    public void searchDifferentTerms(String term) {
        openSearchResults(term);

        SearchResultsPage results = new SearchResultsPage(driver);

        Assert.assertTrue(results.getResultCount() > 0,
                "'" + term + "'-ზე შედეგი არ მოიძებნა");

        System.out.println("      '" + term + "' → " + results.getResultCount() + " შედეგი");
    }

    private void openSearchResults(String query) {
        String url = ConfigReader.get("wiki.url")
                + "/w/index.php?search=" + query.replace(" ", "+")
                + "&title=Special:Search&fulltext=1&ns0=1";
        driver.get(url);
    }
}
