package hexlet.code.tests;

import hexlet.code.pages.LoginPage;
import hexlet.code.pages.StatusesPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;

import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusesTest extends BaseTest {

    @Test
    public void testCreateNewStatus() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String name = "In Progress " + uniqueId;
        String slug = "in-progress" + uniqueId;

        statusesPage.forceGoToStatuses();
        statusesPage.clickCreateStatus();

        try {
            statusesPage.fillAndSubmitStatusForm(name, slug);
        } catch (TimeoutException e) {
            fail("error while creating a status");
        }

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.isStatusInList(name), "Status has not been created");
    }

    @Test
    public  void testDefaultStatusesArePresent() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());

        statusesPage.forceGoToStatuses();

        Map<String, String> defaultStatuses = new LinkedHashMap<>();

        defaultStatuses.put("Draft", "draft");
        defaultStatuses.put("To Review", "to_review");
        defaultStatuses.put("To Be Fixed", "to_be_fixed");
        defaultStatuses.put("To Publish", "to_publish");
        defaultStatuses.put("Published", "published");

        for (Map.Entry<String, String> status : defaultStatuses.entrySet()) {
            String expectedName = status.getKey();
            String expectedSlug = status.getKey();

            assertTrue(statusesPage.isStatusRowCorrect(expectedName, expectedSlug),
                    "No name and slug found");
        }
    }

    @Test
    public void testStatusesListView() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.areHeaderDisplayed(), "Name and Slug headers are missing");

        assertTrue(statusesPage.isStatusRowCorrect("Draft", "draft"),
                "improper order");

        int rowsCount = statusesPage.getRowsCount();

        assertTrue(rowsCount > 0, "Statuses page is empty or data are not loaded");
    }

    @Test
    public  void testEditStatus() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());

        statusesPage.forceGoToStatuses();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String initialName = "ToEdit_" + uniqueId;
        String initialSlug = "to-edit" + uniqueId;

        statusesPage.clickCreateStatus();

        statusesPage.fillAndSubmitStatusForm(initialName, initialSlug);

        statusesPage.forceGoToStatuses();

        statusesPage.clickEditStatus(initialName);

        String updatedName = "Updated_" + uniqueId;
        String updatedSlug = "updated-" + uniqueId;

        statusesPage.fillAndSubmitEditForm(updatedName, updatedSlug);

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.isStatusRowCorrect(updatedName, updatedSlug), "changed status not found");

        boolean isOldNamePresent = statusesPage.isStatusPresent(initialName);

        assertFalse(isOldNamePresent, "old name '" + initialName + "' is still displayed)");

        statusesPage.clickEditStatus(updatedName);

        String actualName = statusesPage.getNameInputValue();
        String actualSlug = statusesPage.getSlugInputValue();

        assertEquals(updatedName, actualName);
        assertEquals(updatedSlug, actualSlug);
    }

    @Test
    public  void testDeleteStatus() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());

        statusesPage.forceGoToStatuses();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String nameToDelete = "DeleteMe_" + uniqueId;
        String slugToDelete = "delete-me" + uniqueId;

        statusesPage.clickCreateStatus();
        statusesPage.fillAndSubmitEditForm(nameToDelete, slugToDelete);

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.isStatusInList(nameToDelete));
        statusesPage.clickEditStatus(nameToDelete);

        statusesPage.clickDeleteButton();

        assertFalse(
                statusesPage.isStatusInList(nameToDelete), nameToDelete + "is still displayed"
        );
    }

    @Test
    public  void testDeleteAllStatuses() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());
        statusesPage.forceGoToStatuses();


        statusesPage.clickSelectAllUsersButton();

        statusesPage.clickDeleteAllUsersButton();

        assertTrue(statusesPage.isEmptyStateDisplayed(), "Empty state is not displayed");
    }

    @Test
    public void testShowStatus() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());
        statusesPage.forceGoToStatuses();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String statusName = "Status " + uniqueId;
        String slugName = "Slug " + uniqueId;

        statusesPage.clickCreateStatus();
        statusesPage.fillAndSubmitEditForm(statusName, slugName);

        statusesPage.forceGoToStatuses();

        statusesPage.clickEditStatus(statusName);

        statusesPage.clickUpperShowButton();

        assertTrue(statusesPage.isTextPresentOnViewPage(statusName), "No status name on Show page");

        statusesPage.clickUpperEditButton();

        assertFalse(statusesPage.getCurrentUrl().contains("/edit"));
    }

    @Test
    public  void testCreateStatusValidation() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());


        statusesPage.forceGoToStatuses();

        statusesPage.waitForListToLoad();

        int initialStatusesCount = statusesPage.getInitialStatusesCount();

        statusesPage.clickCreateStatus();

        statusesPage.fillNameField("Temp status name");
        statusesPage.clickSaveButtonForStatuses();

        assertTrue(statusesPage.getCurrentUrl().contains("/create"), "Empty slug has been created");

        assertTrue(statusesPage.isRequiredErrorDisplayed(), "Required is not displayed");

        statusesPage.clearNameField();

        statusesPage.fillSlugField("Temp slug name");
        statusesPage.clickSaveButtonForStatuses();

        assertTrue(statusesPage.getCurrentUrl().contains("/create"), "Empty slug has been created");

        assertTrue(statusesPage.isRequiredErrorDisplayed(), "Required is not displayed");

        statusesPage.forceGoToStatuses();

        statusesPage.waitForListToLoad();

        int finalStatusesCount = statusesPage.getFinalStatusesCount();

        assertEquals(initialStatusesCount, finalStatusesCount, "empty status has been saved");
    }

    @Test
    public void testEditStatusWithoutName() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());

        statusesPage.forceGoToStatuses();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String initialName = "ToEdit_" + uniqueId;
        String initialSlug = "to-edit" + uniqueId;

        statusesPage.clickCreateStatus();

        statusesPage.fillAndSubmitStatusForm(initialName, initialSlug);

        statusesPage.forceGoToStatuses();

        statusesPage.clickEditStatus(initialName);

        statusesPage.clearNameField();

        statusesPage.clickSaveButtonForStatuses();

        assertFalse(statusesPage.getCurrentUrl().endsWith("/statuses"), "status w/o name was saved");

        assertTrue(statusesPage.isRequiredErrorDisplayed(), "no required message");
    }

    @Test
    public  void testEditStatusWithoutSlug() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());

        statusesPage.forceGoToStatuses();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String initialName = "ToEdit_" + uniqueId;
        String initialSlug = "to-edit" + uniqueId;

        statusesPage.clickCreateStatus();

        statusesPage.fillAndSubmitStatusForm(initialName, initialSlug);

        statusesPage.forceGoToStatuses();

        statusesPage.clickEditStatus(initialName);

        statusesPage.clearSlugField();

        statusesPage.clickSaveButtonForStatuses();

        statusesPage.waitForSnackBar();

        assertFalse(statusesPage.getCurrentUrl().endsWith("/statuses"), "status w/o slug was saved");

        assertTrue(statusesPage.isRequiredErrorDisplayed(), "no required message");
    }

    @Test
    public  void testBulkDeleteStatus() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());

        statusesPage.forceGoToStatuses();

        int initialRowCount = statusesPage.getTableRowsCount();

        assertTrue(initialRowCount > 0, "Table is empty");

        statusesPage.selectFirstRowCheckbox();

        statusesPage.clickBulkDeleteButton();

        statusesPage.waitForSnackBar();

        int finalRowCount = statusesPage.getTableRowsCount();

        assertEquals(initialRowCount - 1, finalRowCount, "Rows count hasn't changed");
    }

    @Test
    public void testCancelStatusCheckboxSelection() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(getDriver());

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.getTableRowsCount() > 0, "Labels table is empty");

        statusesPage.selectFirstRowCheckbox();
        statusesPage.clickUnselectCrossButton();

        assertTrue(statusesPage.isSelectionTextHidden(), "1 item selected is still displayed");
    }
}
