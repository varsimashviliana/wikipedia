package ge.automation.tests.web;

import ge.automation.pages.ExportPage;
import ge.automation.tests.BaseTest;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CheckboxTest extends BaseTest {

    private ExportPage exportPage;

    @BeforeMethod(alwaysRun = true)
    public void openExportPage() {
        exportPage = new ExportPage(driver).open();
    }

    @Test(priority = 1,
          groups = {"smoke", "checkbox"},
          description = "checkbox-ები არსებობს და საწყისი მდგომარეობა სწორია")
    public void checkboxesArePresentWithCorrectDefaults() {
        Assert.assertTrue(exportPage.isPageOpened(), "ექსპორტის გვერდი არ გაიხსნა");

        int checkboxCount = exportPage.getCheckboxCount();
        System.out.println("      ნაპოვნია " + checkboxCount + " checkbox");
        Assert.assertEquals(checkboxCount, 3,
                "checkbox-ების რაოდენობა მოსალოდნელს არ ემთხვევა");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(exportPage.isCurrentRevisionOnlySelected(),
                "'Include only the current revision' ნაგულისხმევად მონიშნული უნდა იყოს");
        softAssert.assertFalse(exportPage.isIncludeTemplatesSelected(),
                "'Include templates' ნაგულისხმევად მოხსნილი უნდა იყოს");
        softAssert.assertTrue(exportPage.isSaveAsFileSelected(),
                "'Save as file' ნაგულისხმევად მონიშნული უნდა იყოს");
        softAssert.assertAll();
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

    @Test(priority = 3,
          groups = {"regression", "checkbox"},
          description = "ერთი checkbox-ის შეცვლა დანარჩენებზე არ მოქმედებს")
    public void togglingOneCheckboxDoesNotAffectOthers() {
        exportPage.setIncludeTemplates(true);
        exportPage.setSaveAsFile(false);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(exportPage.isIncludeTemplatesSelected(),
                "'Include templates' არ მოინიშნა");
        softAssert.assertFalse(exportPage.isSaveAsFileSelected(),
                "'Save as file' არ მოიხსნა");
        softAssert.assertTrue(exportPage.isCurrentRevisionOnlySelected(),
                "'Include only the current revision' არ უნდა შეცვლილიყო");
        softAssert.assertAll();
    }

    @Test(priority = 4,
          groups = {"regression", "checkbox"},
          description = "ყველა checkbox-ის ერთდროული მონიშვნა და მოხსნა")
    public void canSelectAndClearAllCheckboxes() {
        exportPage.setAllCheckboxes(true);
        Assert.assertTrue(exportPage.areAllCheckboxesSelected(),
                "სამივე checkbox მონიშნული უნდა ყოფილიყო");
        System.out.println("      სამივე checkbox მოინიშნა ✓");

        exportPage.setAllCheckboxes(false);
        Assert.assertTrue(exportPage.areAllCheckboxesCleared(),
                "სამივე checkbox მოხსნილი უნდა ყოფილიყო");
        System.out.println("      სამივე checkbox მოიხსნა ✓");
    }
}
