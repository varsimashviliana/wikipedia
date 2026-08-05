package ge.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * PortalPage — ვიკიპედიის მთავარი პორტალი: https://www.wikipedia.org
 *
 * <p>ეს გვერდი იმიტომ ავირჩიეთ, რომ აქ არის <b>ნამდვილი &lt;select&gt; dropdown</b>
 * 77 ენით — ზუსტად ის, რაზეც Selenium-ის <code>Select</code> კლასი მუშაობს.</p>
 *
 * <p><b>PageFactory:</b> @FindBy ანოტაცია ეუბნება Selenium-ს, სად ეძებოს ელემენტი.
 * ელემენტი რეალურად მაშინ იძებნება, როცა მას პირველად გამოვიყენებთ (lazy loading).</p>
 */
public class PortalPage extends BasePage {

    // ---------------------------------------------------------------
    //  ლოკატორები — სხვადასხვა სტრატეგიით (სილაბუსი, მე-12 ლექცია)
    // ---------------------------------------------------------------

    /** ძებნის ველი — id-ით ვპოულობთ (ყველაზე სწრაფი და საიმედო გზა). */
    @FindBy(id = "searchInput")
    private WebElement searchInput;

    /** ენების dropdown — ესაა ნამდვილი &lt;select&gt; ტეგი. */
    @FindBy(id = "searchLanguage")
    private WebElement languageDropdown;

    /** ძებნის ღილაკი — CSS სელექტორით. */
    @FindBy(css = "button.pure-button-primary-progress")
    private WebElement searchButton;

    /** English ენის ბლოკი — id-ით. */
    @FindBy(id = "js-link-box-en")
    private WebElement englishLink;

    /** ცენტრალური ლოგო — XPath-ით (რომ XPath-იც გამოვიყენოთ). */
    @FindBy(xpath = "//div[contains(@class,'central-featured-logo')]")
    private WebElement centralLogo;

    /** ყველა ენის ბლოკი — რამდენიმე ელემენტი ერთ სიაში. */
    @FindBy(css = "div.central-featured-lang")
    private List<WebElement> allLanguageBlocks;

    /**
     * კონსტრუქტორი — super(driver) ეძახის მშობელ კლასს,
     * რომელიც PageFactory-ს ინიციალიზაციას აკეთებს.
     * (სილაბუსი, მე-7 ლექცია: მემკვიდრეობა და კონსტრუქტორები)
     */
    public PortalPage(WebDriver driver) {
        super(driver);
    }

    // ---------------------------------------------------------------
    //  მოქმედებები — ტესტი მხოლოდ ამ მეთოდებს იძახებს
    // ---------------------------------------------------------------

    /**
     * მშობელი კლასის აბსტრაქტული მეთოდის იმპლემენტაცია.
     * (@Override — მეთოდის გადაფარვა, სილაბუსის მე-6 ლექცია)
     */
    @Override
    public boolean isPageOpened() {
        return isDisplayed(searchInput) && isDisplayed(languageDropdown);
    }

    /** ძებნის ველში ტექსტს აკრეფს და Enter-ს აჭერს. */
    public SearchResultsPage searchFor(String query) {
        typeAndEnter(searchInput, query);
        return new SearchResultsPage(driver);
    }

    /** ძებნა ღილაკზე დაჭერით (და არა Enter-ით). */
    public SearchResultsPage searchUsingButton(String query) {
        type(searchInput, query);
        click(searchButton);
        return new SearchResultsPage(driver);
    }

    /** ენის არჩევა dropdown-იდან, ჩანს ტექსტით (მაგ. "Deutsch"). */
    public PortalPage selectLanguageByText(String visibleText) {
        selectByVisibleText(languageDropdown, visibleText);
        return this;
    }

    /** ენის არჩევა dropdown-იდან, კოდით (მაგ. "ka" — ქართული). */
    public PortalPage selectLanguageByValue(String langCode) {
        selectByValue(languageDropdown, langCode);
        return this;
    }

    /** აბრუნებს ამჟამად არჩეულ ენას. */
    public String getSelectedLanguage() {
        return getSelectedOption(languageDropdown);
    }

    /** აბრუნებს dropdown-ში ენების რაოდენობას. */
    public int getLanguageCount() {
        return getSelectOptions(languageDropdown).size();
    }

    /** ამოწმებს, არის თუ არა კონკრეტული ენა სიაში. */
    public boolean hasLanguageOption(String visibleText) {
        return getSelectOptions(languageDropdown).stream()
                .anyMatch(option -> option.getText().trim().equalsIgnoreCase(visibleText));
    }

    /** მაუსს მიიტანს English ბლოკზე (Actions — hover). */
    public PortalPage hoverOverEnglish() {
        hoverOver(englishLink);
        return this;
    }

    /** English ვიკიპედიაზე გადასვლა. */
    public void goToEnglishWikipedia() {
        click(englishLink);
    }

    /** აბრუნებს მთავარ გვერდზე გამოტანილი ენების რაოდენობას. */
    public int getFeaturedLanguageCount() {
        return allLanguageBlocks.size();
    }

    /** ლოგო ჩანს თუ არა. */
    public boolean isLogoDisplayed() {
        return isDisplayed(centralLogo);
    }
}
