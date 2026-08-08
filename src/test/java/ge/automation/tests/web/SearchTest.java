package ge.automation.tests.web;

import ge.automation.config.ConfigReader;
import ge.automation.pages.ArticlePage;
import ge.automation.pages.PortalPage;
import ge.automation.pages.SearchResultsPage;
import ge.automation.tests.BaseTest;
import org.testng.Assert;
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

        portal.searchFor("Tbilisi");

        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("wikipedia.org"),
                "ძებნის შემდეგ ვიკიპედიაზე არ გადავედით. URL: " + url);

        String title = driver.getTitle();
        Assert.assertTrue(title.toLowerCase().contains("tbilisi"),
                "გვერდის სათაურში 'Tbilisi' არ არის. სათაური: " + title);
    }

    @Test(priority = 3,
          groups = {"regression", "search"},
          description = "ძებნის შედეგიდან სტატიის გახსნა")
    public void openArticleFromSearchResults() {
        openSearchResults("Selenium (software)");

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
        openSearchResults("Ana Varsimashvili QaAutomation99887");

        SearchResultsPage results = new SearchResultsPage(driver);

        boolean noResults = results.isNoResultsMessageDisplayed()
                || results.getResultCount() == 0;

        Assert.assertTrue(noResults,
                "არარსებულ სიტყვაზე შედეგები დაბრუნდა, რაც არ უნდა მომხდარიყო");
    }

    private void openSearchResults(String query) {
        String url = ConfigReader.get("wiki.url")
                + "/w/index.php?search=" + query.replace(" ", "+")
                + "&title=Special:Search&fulltext=1&ns0=1";
        driver.get(url);
    }
}
