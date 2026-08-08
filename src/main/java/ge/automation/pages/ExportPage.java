package ge.automation.pages;

import ge.automation.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ExportPage extends BasePage {

    @FindBy(css = "textarea[name='pages']")
    private WebElement pagesTextarea;

    @FindBy(css = "input[name='curonly']")
    private WebElement currentRevisionOnlyCheckbox;

    @FindBy(css = "input[name='templates']")
    private WebElement includeTemplatesCheckbox;

    @FindBy(xpath = "//input[@name='templates']/ancestor::div[contains(@class,'oo-ui-fieldLayout')][1]//label")
    private WebElement includeTemplatesLabel;

    public ExportPage(WebDriver driver) {
        super(driver);
    }

    public ExportPage open() {
        driver.get(ConfigReader.get("export.url"));
        return this;
    }

    @Override
    public boolean isPageOpened() {
        return isDisplayed(pagesTextarea) && isPresent(currentRevisionOnlyCheckbox);
    }

    public boolean isIncludeTemplatesSelected() {
        return includeTemplatesCheckbox.isSelected();
    }

    public ExportPage setIncludeTemplates(boolean checked) {
        setCheckbox(includeTemplatesCheckbox, includeTemplatesLabel, checked);
        return this;
    }
}
