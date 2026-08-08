package ge.automation.tests.web;

import ge.automation.pages.ExportPage;
import ge.automation.tests.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CheckboxTest extends BaseTest {

    private ExportPage exportPage;

    @BeforeMethod(alwaysRun = true)
    public void openExportPage() {
        exportPage = new ExportPage(driver).open();
    }

    @Test(priority = 2,
          groups = {"smoke", "checkbox"},
          description = "checkbox-ის მონიშვნა და მონიშვნის მოხსნა")
    public void canCheckAndUncheckCheckbox() {
        exportPage.setIncludeTemplates(true);
        Assert.assertTrue(exportPage.isIncludeTemplatesSelected(),
                "checkbox მონიშვნის შემდეგ მონიშნული არ არის");
        System.out.println("      checkbox მოინიშნა ✓");

        exportPage.setIncludeTemplates(false);
        Assert.assertFalse(exportPage.isIncludeTemplatesSelected(),
                "checkbox მოხსნის შემდეგ ისევ მონიშნულია");
        System.out.println("      checkbox მოიხსნა ✓");
    }

}
