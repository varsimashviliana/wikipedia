package ge.automation.pages;

import ge.automation.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ExportPage extends BasePage {

    @FindBy(css = "textarea[name='pages']")
    private WebElement pagesTextarea;

    @FindBy(css = "input[name='curonly']")
    private WebElement currentRevisionOnlyCheckbox;

    @FindBy(xpath = "//input[@name='curonly']/ancestor::div[contains(@class,'oo-ui-fieldLayout')][1]//label")
    private WebElement currentRevisionOnlyLabel;

    @FindBy(css = "input[name='templates']")
    private WebElement includeTemplatesCheckbox;

    @FindBy(xpath = "//input[@name='templates']/ancestor::div[contains(@class,'oo-ui-fieldLayout')][1]//label")
    private WebElement includeTemplatesLabel;

    @FindBy(css = "input[name='wpDownload']")
    private WebElement saveAsFileCheckbox;

    @FindBy(xpath = "//input[@name='wpDownload']/ancestor::div[contains(@class,'oo-ui-fieldLayout')][1]//label")
    private WebElement saveAsFileLabel;

    @FindBy(css = ".mw-htmlform-field-HTMLCheckField")
    private List<WebElement> checkboxFields;

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

    public int getCheckboxCount() {
        return checkboxFields.size();
    }

    public boolean isCurrentRevisionOnlySelected() {
        return currentRevisionOnlyCheckbox.isSelected();
    }

    public boolean isIncludeTemplatesSelected() {
        return includeTemplatesCheckbox.isSelected();
    }

    public boolean isSaveAsFileSelected() {
        return saveAsFileCheckbox.isSelected();
    }

    public ExportPage setCurrentRevisionOnly(boolean checked) {
        setCheckbox(currentRevisionOnlyCheckbox, currentRevisionOnlyLabel, checked);
        return this;
    }

    public ExportPage setIncludeTemplates(boolean checked) {
        setCheckbox(includeTemplatesCheckbox, includeTemplatesLabel, checked);
        return this;
    }

    public ExportPage setSaveAsFile(boolean checked) {
        setCheckbox(saveAsFileCheckbox, saveAsFileLabel, checked);
        return this;
    }

    public ExportPage setAllCheckboxes(boolean checked) {
        setCurrentRevisionOnly(checked);
        setIncludeTemplates(checked);
        setSaveAsFile(checked);
        return this;
    }

    public boolean areAllCheckboxesSelected() {
        return isCurrentRevisionOnlySelected()
                && isIncludeTemplatesSelected()
                && isSaveAsFileSelected();
    }

    public boolean areAllCheckboxesCleared() {
        return !isCurrentRevisionOnlySelected()
                && !isIncludeTemplatesSelected()
                && !isSaveAsFileSelected();
    }
}
