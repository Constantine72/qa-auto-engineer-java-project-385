package hexlet.code.tests;

import hexlet.code.pages.KanbanPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.UsersPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UsersTest extends BaseTest {

    private static final int MIN_WAIT_TIME_SECS = 5;

    @Test
    public void testCreateNewUser() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String testEmail = "test@mail.com" + uniqueId;
        String testFirstName = "test" + uniqueId;
        String testLastName = "user" + uniqueId;

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        assertTrue(usersPage.getCurrentUrl().contains("/users"), "app is not displayed");

        try {
            usersPage.clickCreateUser();
            assertTrue(usersPage.isUserFormDisplayed(), "User form hasn't opened");
            usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);
        } catch (TimeoutException e) {
            fail("app is not displayed");
        }

        String visibleText = getDriver().findElement(By.tagName("body")).getText().toLowerCase();

        assertTrue(visibleText.contains("first name"), "first name placeholder is missing");
        assertTrue(visibleText.contains("last name"), "last name placeholder is missing");
        assertTrue(visibleText.contains("email"), "email placeholder is missing");

        usersPage.waitForSnackBar();

        kanbanPage.goToUsers();

        assertTrue(usersPage.isUserInList(testFirstName, testLastName, testEmail), "Created user "
                + testFirstName + "not found");
    }

    @Test
    public void testUserListLoadingAndFields() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        try {
            assertTrue(usersPage.getUsersCount() > 0, "users table is empty");
        } catch (TimeoutException e) {
            fail("app is not displayed");
        }

        WebElement dateCell = getDriver().findElement(By.cssSelector("tbody .column-createdAt"));

        String dateText = dateCell.getText();


        assertTrue(!dateText.contains("T") && dateText.contains(","), "date field is broken");

        try {
            assertTrue(usersPage.isUserTableLoaded(), "The table has not loaded");
            assertTrue(usersPage.areKeyFieldsDisplayed(), "Fields are missing");
        } catch (TimeoutException e) {
            fail("the app is not displayed");
        }
        assertTrue(getDriver().getPageSource().contains("Users"), "Header Users is missing");
    }

    @Test
    public void testEditUserAndValidation() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String originalFirstName = "OldFirstName_" + uniqueId;
        String originalLastName = "OldLastName_" + uniqueId;
        String originalEmail = "old" + uniqueId + "@example.com";

        usersPage.clickCreateUser();
        usersPage.fillAndSubmitUserForm(originalEmail, originalFirstName, originalLastName);

        usersPage.waitForSnackBar();

        usersPage.forceGoToUsers();

        assertTrue(usersPage.isUserInList(originalFirstName, originalLastName, originalEmail),
                "User for edit has not been created");

        try {
            usersPage.clickEditUser(originalFirstName);
        } catch (TimeoutException e) {
            fail("the user is not displayed");
        }

        String visibleText = getDriver().findElement(By.tagName("body")).getText().toLowerCase();

        assertTrue(visibleText.contains("first name"), "first name placeholder is missing");
        assertTrue(visibleText.contains("last name"), "last name placeholder is missing");
        assertTrue(visibleText.contains("email"), "email placeholder is missing");

        assertEquals(originalFirstName, usersPage.getFirstNameValue(), "the name does not coincide");
        assertEquals(originalEmail, usersPage.getEmailValue(), "email does not coincide");

        usersPage.fillEmailOnly("qweqweqwe");

        usersPage.clickSaveButtonForUsers();

        assertTrue(usersPage.isEmailValidationErrorDisplayed(), "No error message displayed");

        String newFirstName = "NewFirstName_" + uniqueId;
        String newLastName = "NewLastName_" + uniqueId;
        String newEmail = "new" + uniqueId + "@example.com";

        usersPage.fillAndSubmitUserForm(newEmail, newFirstName, newLastName);

        assertTrue(usersPage.isUserInList(newFirstName, newLastName, newEmail));
        assertFalse(usersPage.isUserInList(originalFirstName, originalLastName, originalEmail),
                "old userName is still on the list");
    }

    @Test
    public void testEditFormPopulatedDataCorrectly() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String originalFirstName = "OldFirstName_" + uniqueId;
        String originalLastName = "OldLastName_" + uniqueId;
        String originalEmail = "old" + uniqueId + "@example.com";

        usersPage.clickCreateUser();
        usersPage.fillAndSubmitUserForm(originalEmail, originalFirstName, originalLastName);

        usersPage.forceGoToUsers();

        assertTrue(usersPage.isUserInList(originalFirstName, originalLastName, originalEmail),
                "User for edit has not been created");

        usersPage.clickEditUser(originalFirstName);

        String actualFirstName = usersPage.getFirstNameInputValue();
        String actualLastName = usersPage.getLastNameInputValue();
        String actualEmail = usersPage.getEmailInputValue();

        assertEquals(actualFirstName, originalFirstName);
        assertEquals(actualLastName, originalLastName);
        assertEquals(actualEmail, originalEmail);
    }

    @Test
    public void testDeleteUser() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String userToDeleteFirstName = "DeleteFirstme_" + uniqueId;
        String userToDeleteLastName = "DeleteLastme_" + uniqueId;
        String originalEmail = "delete" + uniqueId + "@example.com";

        usersPage.clickCreateUser();
        usersPage.fillAndSubmitUserForm(originalEmail, userToDeleteFirstName, userToDeleteLastName);

        usersPage.forceGoToUsers();

        assertTrue(usersPage.isUserInList(userToDeleteFirstName, userToDeleteLastName, originalEmail),
                "User for edit has not been created");

        try {
            usersPage.clickEditUser(userToDeleteFirstName);
        } catch (TimeoutException e) {
            fail("the user is not displayed");
        }

        usersPage.clickDeleteButton();

        usersPage.forceGoToUsers();

        assertFalse(usersPage.isUserInList(userToDeleteFirstName, userToDeleteLastName, originalEmail),
                "Error: the user to delete is still there");
    }

    @Test
    public void testDeleteAllUsers() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        assertTrue(usersPage.getTableRowsCount() > 0, "The table is empty");

        usersPage.clickSelectAllUsersButton();

        usersPage.clickDeleteAllUsersButton();

        assertTrue(usersPage.isEmptyStateDisplayed(), "Empty state is not displayed");
    }

    @Test
    public void testShowUser() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String testFirstName = "John " + uniqueId;
        String testLastName = "Smith " + uniqueId;
        String testEmail = "user" + uniqueId + "@test.com";

        usersPage.clickCreateUser();

        usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);

        kanbanPage.goToUsers();

        try {
            usersPage.clickEditUser(testFirstName);
        } catch (TimeoutException e) {
            fail("the user is not displayed");
        }

        usersPage.clickUpperShowButton();

        assertTrue(usersPage.isTextPresentOnViewPage(testFirstName), "Username is not displayed");

        assertTrue(usersPage.isTextPresentOnViewPage(testEmail), "Email is not displayed");

        assertTrue(usersPage.isTextPresentOnViewPage(testLastName), "Last is not displayed");

        String visibleText = getDriver().findElement(By.tagName("body")).getText().toLowerCase();

        assertTrue(visibleText.contains("id"), "no id");
        assertTrue(visibleText.contains("email"), "no email");
        assertTrue(visibleText.contains("first name"), "no first name");
        assertTrue(visibleText.contains("last name"), "no last name");
        assertTrue(visibleText.contains("created at"), "no created at");

        usersPage.clickUpperEditButton();

        assertFalse(usersPage.getCurrentUrl().contains("/show"), "Show page is still displayed");
    }

    @Test
    public void testCreateUserValidation() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        int initialUsersCount = usersPage.getUsersCount();


        usersPage.clickCreateUser();

        try {
            usersPage.fillLastNameField("Smith");
        } catch (TimeoutException e) {
            fail("the form is not displayed");
        }
        usersPage.fillEmailField("newemail@test.com");
        usersPage.clickSaveButtonForUsers();

        assertTrue(usersPage.getCurrentUrl().contains("/create"), "empty firstName was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is missing");

        usersPage.clearLastNameField();

        try {
            usersPage.fillFirstNameField("John");
        } catch (TimeoutException e) {
            fail("the form is not displayed");
        }
        usersPage.clickSaveButtonForUsers();

        assertTrue(usersPage.getCurrentUrl().contains("/create"), "empty firstName was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is missing");

        usersPage.fillLastNameField("Smith");
        usersPage.clearEmailField();
        usersPage.clickSaveButtonForUsers();

        assertTrue(usersPage.getCurrentUrl().contains("/create"), "empty firstName was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is missing");

        String badEmail = "notAnEmail";

        usersPage.fillEmailField(badEmail);
        usersPage.clickSaveButtonForUsers();

        assertTrue(usersPage.getCurrentUrl().contains("/create"), "empty firstName was saved");

        assertTrue(usersPage.isInvalidEmailErrorDisplayed(), "Incorrect email error is not shown");

        usersPage.forceGoToUsers();

        int finalUsersCount = usersPage.getFinalUsersCount();
        assertEquals(initialUsersCount, finalUsersCount, "improper user has been created");

        boolean isUserCreatedAnyway = usersPage.isTextPresentOnPage(badEmail);
        assertFalse(isUserCreatedAnyway, "improper user has been saved");
    }

    @Test
    public void testEditUserValidationWithoutFirstName() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String testFirstName = "John " + uniqueId;
        String testLastName = "Smith " + uniqueId;
        String testEmail = "user" + uniqueId + "@test.com";

        usersPage.clickCreateUser();

        usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);

        kanbanPage.goToUsers();

        usersPage.clickEditUser(testFirstName);

        usersPage.clearFirstNameField();

        usersPage.clickSaveButtonForUsers();

        usersPage.waitForSnackBar();

        assertFalse(usersPage.getCurrentUrl().endsWith("/users"), "user w/o first name was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is not displayed");
    }

    @Test
    public void testEditUserValidationWithoutLastName() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String testFirstName = "John " + uniqueId;
        String testLastName = "Smith " + uniqueId;
        String testEmail = "user" + uniqueId + "@test.com";

        usersPage.clickCreateUser();

        usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);

        kanbanPage.goToUsers();

        usersPage.clickEditUser(testFirstName);

        usersPage.clearLastNameField();

        usersPage.clickSaveButtonForUsers();

        assertFalse(usersPage.getCurrentUrl().endsWith("/users"), "user w/o last name was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is not displayed");

    }

    @Test
    public void testEditUserValidationWithoutEmail() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String testFirstName = "John " + uniqueId;
        String testLastName = "Smith " + uniqueId;
        String testEmail = "user" + uniqueId + "@test.com";

        usersPage.clickCreateUser();

        usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);

        kanbanPage.goToUsers();

        usersPage.clickEditUser(testFirstName);

        usersPage.clearEmailField();

        usersPage.clickSaveButtonForUsers();

        assertFalse(usersPage.getCurrentUrl().endsWith("/users"), "user w/o last name was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is not displayed");
    }

    @Test
    public void testEditUserValidationWithIncorrectEmail() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String testFirstName = "John " + uniqueId;
        String testLastName = "Smith " + uniqueId;
        String testEmail = "user" + uniqueId + "@test.com";
        String testIncorrectEmail = "user" + uniqueId + "test.com";

        usersPage.clickCreateUser();

        usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);

        kanbanPage.goToUsers();

        usersPage.clickEditUser(testFirstName);

        usersPage.clearEmailField();

        usersPage.fillEmailField(testIncorrectEmail);

        usersPage.clickSaveButtonForUsers();

        assertFalse(usersPage.getCurrentUrl().endsWith("/users"), "user w/o last name was saved");

        assertTrue(usersPage.isInvalidEmailErrorDisplayed(), "no error message for improper email");
    }

    @Test
    public void testBulkDeleteUser() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        int initialRowCount = usersPage.getTableRowsCount();
        assertTrue(initialRowCount > 0, "The table is empty");

        String targetEmail = usersPage.getEmailFromFirstRow();
        System.out.println(initialRowCount);
        System.out.println(targetEmail);

        try {
            usersPage.selectFirstRowCheckbox();
            usersPage.clickBulkDeleteButton();
        } catch (TimeoutException e) {
            fail("the button is not displayed");
        }
        usersPage.waitForSnackBar();

        int finalRowsCount = usersPage.getTableRowsCount();

        assertEquals(initialRowCount - 1, finalRowsCount, "Rows count hasn't changed");

        assertFalse(usersPage.isTextPresentOnPage(targetEmail), targetEmail + " is still displayed");
    }

    @Test
    public void testCancelUserCheckboxSelection() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        assertTrue(usersPage.getTableRowsCount() > 0, "Labels table is empty");

        usersPage.selectFirstRowCheckbox();
        usersPage.clickUnselectCrossButton();

        assertTrue(usersPage.isSelectionTextHidden(), "1 item selected is still displayed");
    }

    @Test
    public void testPaginationFullFlow() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        assertTrue(usersPage.getTableRowsCount() > 0, "the table is empty");

        usersPage.changeRowsPerPage("5");

        usersPage.waitForPaginationTextOneToFive();

        assertTrue(usersPage.isNextPageButtonEnabled(), "pagination arrow right is not clickable");

        usersPage.clickNextPageButton();

        usersPage.waitForPaginationTextSixToMore();

        String urlPage2 = usersPage.getCurrentUrl();

        assertTrue(urlPage2.contains("page=2") || (urlPage2.contains("page%22%3A2")),
                "next page hasn't been opened");

        usersPage.clickPreviousPageButton();

        usersPage.waitForPaginationTextOneToFive();

        String finalUrl = usersPage.getCurrentUrl();

        assertTrue(finalUrl.contains("page=1") || finalUrl.contains("page%22%3A1"),
                "page 1 hasn't been opened");
    }

    @Test
    public void testExportUsers() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));
        usersPage.clickExportButtonForUsers();

        assertTrue(usersPage.isUserTableLoaded(), "The app has crashed after clicking Export");
    }

    @Test
    public void testUsersTableSorting() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        assertTrue(usersPage.getTableRowsCount() > 0, "the table is empty");

        String urlBeforeSort = usersPage.getCurrentUrl();

        String emailBefore = usersPage.getEmailFromFirstRow();

        usersPage.clickEmailColumnHeader();

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(MIN_WAIT_TIME_SECS));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(urlBeforeSort)));
        String urlAfterSort = usersPage.getCurrentUrl();

        String emailAfter = usersPage.getEmailFromFirstRow();

        assertNotEquals(urlBeforeSort, urlAfterSort, "Sorting does not work");

        assertNotEquals(emailBefore, emailAfter, "sorting is broken");
    }

    @Test
    public void testSaveButtonDisabledOnEmptyForm() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        usersPage.clickCreateUser();

        assertFalse(usersPage.isSaveButtonEnabled(), "Save button is enabled");
    }
}
