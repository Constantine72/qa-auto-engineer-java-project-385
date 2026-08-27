package hexlet.code.tests;

import hexlet.code.pages.LabelsPage;
import hexlet.code.pages.LoginPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LabelsTest extends BaseTest {

    private static final int MIN_WAIT_TIME_SECS = 5;

    @Test
    public void testCreateNewLabel() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(getDriver());

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String labelName = "Feature_ " + uniqueId;

        labelsPage.forceGoToLabels();
        labelsPage.clickCreateLabel();
        labelsPage.fillAndSubmitLabelForm(labelName);

        labelsPage.forceGoToLabels();

        assertTrue(labelsPage.isLabelInList(labelName), "Label " + labelName + "has not been created");
    }

    @Test
    public void testLabelsListView() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(getDriver());

        labelsPage.forceGoToLabels();

        assertTrue(labelsPage.areHeaderDisplayed(), "Name is missing");

        int rowsCount = labelsPage.getRowsCount();

        assertTrue(rowsCount > 0, "Labels page is empty or data are not loaded");
    }


    @Test
    public void testEditLabel() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(getDriver());

        labelsPage.forceGoToLabels();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String initialName = "ToEdit_" + uniqueId;

        labelsPage.clickCreateLabel();

        labelsPage.fillAndSubmitLabelForm(initialName);

        labelsPage.forceGoToLabels();

        labelsPage.clickEditLabel(initialName);

        String updatedName = "Updated_" + uniqueId;

        labelsPage.fillAndSubmitEditForm(updatedName);

        labelsPage.forceGoToLabels();

        assertTrue(labelsPage.isLabelInList(updatedName), "changed label not found");

        boolean isOldNamePresent = labelsPage.isTextPresentOnPage(initialName);

        assertFalse(isOldNamePresent, "old name '" + initialName + "' is still displayed)");

        labelsPage.clickEditLabel(updatedName);

        String actualName = labelsPage.getNameInputValue();

        assertEquals(updatedName, actualName);
    }

    @Test
    public void testDeleteLabel() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(getDriver());

        labelsPage.forceGoToLabels();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String labelToDelete = "DeleteMe_" + uniqueId;

        labelsPage.clickCreateLabel();
        labelsPage.fillAndSubmitEditForm(labelToDelete);

        labelsPage.forceGoToLabels();

        assertTrue(labelsPage.isLabelInList(labelToDelete));
        labelsPage.clickEditLabel(labelToDelete);

        labelsPage.clickDeleteButton();

        assertFalse(
                labelsPage.isLabelInList(labelToDelete), labelToDelete + "is still displayed"
        );
    }

    @Test
    public void testShowLabel() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String labelName = "Label " + uniqueId;

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(getDriver());

        labelsPage.forceGoToLabels();

        labelsPage.clickCreateLabel();

        labelsPage.fillAndSubmitLabelForm(labelName);

        labelsPage.forceGoToLabels();

        labelsPage.clickEditLabel(labelName);

        labelsPage.clickUpperShowButton();

        assertTrue(labelsPage.isTextPresentOnViewPage(labelName), "Labelname is not displayed on Show page");

        labelsPage.clickUpperEditButton();

        assertFalse(labelsPage.getCurrentUrl().contains("/edit"));
    }

    @Test
    public void testCreateLabelValidation() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(getDriver());

        labelsPage.forceGoToLabels();

        int initialLabelsCount = labelsPage.getInitialLabelsCount();

        labelsPage.clickCreateLabel();
        labelsPage.triggerValidationOnNameField();
        labelsPage.clickSaveButtonForLabels();

        assertTrue(labelsPage.getCurrentUrl().contains("/create"), "Empty label has been created");

        assertTrue(labelsPage.isRequiredErrorDisplayed(), "No required error is displayed");

        labelsPage.forceGoToLabels();

        labelsPage.waitForListToLoad();

        int finalLabelsCount = labelsPage.getFinalLabelsCount();

        assertEquals(initialLabelsCount, finalLabelsCount, "empty label has been saved");
    }

    @Test
    public void testEditLabelWithoutName() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String labelName = "Label " + uniqueId;

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(getDriver());

        labelsPage.forceGoToLabels();

        labelsPage.clickCreateLabel();

        labelsPage.fillAndSubmitLabelForm(labelName);

        labelsPage.forceGoToLabels();

        labelsPage.clickEditLabel(labelName);

        labelsPage.clearLabelField();

        labelsPage.clickSaveButtonForLabels();

        labelsPage.waitForSnackBar();

        assertFalse(labelsPage.getCurrentUrl().endsWith("/labels"), "label w/o title was saved");

        assertTrue(labelsPage.isRequiredErrorDisplayed(), "no required message");
    }

    @Test
    public void testBulkDeleteLabel() {
        LoginPage loginPage = new LoginPage(getDriver());

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(MIN_WAIT_TIME_SECS));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));

        assertTrue(getDriver().findElements(By.name("username")).size() > 0,
                "application has not loaded: username field is not found");

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(getDriver());

        labelsPage.forceGoToLabels();

        int initialRowCount = labelsPage.getRowsCount();
        assertTrue(initialRowCount > 0, "Labels table is empty");

        labelsPage.selectFirstRowCheckbox();
        labelsPage.clickBulkDeleteButton();

        int finalRowCount = labelsPage.getTableRowsCount();
        labelsPage.waitForSnackBar();

        assertEquals(initialRowCount - 1, finalRowCount, "The row count hasn't changed");
    }

    @Test
    public void testCancelLabelCheckboxSelection() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(getDriver());

        labelsPage.forceGoToLabels();

        assertTrue(labelsPage.getTableRowsCount() > 0, "Labels table is empty");

        labelsPage.selectFirstRowCheckbox();
        labelsPage.clickUnselectCrossButton();

        assertTrue(labelsPage.isSelectionTextHidden(), "1 item selected is still displayed");
    }
}
