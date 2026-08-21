package hexlet.code.tests;

import hexlet.code.pages.KanbanPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.UsersPage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsersTest extends BaseTest {

    @Test
    void testCreateNewUser() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String testEmail = "test@mail.com" + uniqueId;
        String testFirstName = "test" + uniqueId;
        String testLastName = "user" + uniqueId;

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));
        usersPage.clickCreateUser();

        assertTrue(usersPage.isUserFormDisplayed(), "User form hasn't opened");

        usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);

        kanbanPage.goToUsers();

        assertTrue(usersPage.isUserInList(testFirstName, testLastName, testEmail), "Created user "
                + testFirstName + "not found");
    }

    @Test
    void testUserListLoadingAndFields() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        assertTrue(usersPage.isUserTableLoaded(), "The table has not loaded");

        assertTrue(usersPage.areKeyFieldsDisplayed(), "Fields are missing");
    }

    @Test
    void testEditUserAndValidation() {
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
    void testEditFormPopulatedDataCorrectly() {
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
    void testDeleteUser() {
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

        usersPage.clickEditUser(userToDeleteFirstName);

        usersPage.clickDeleteButton();

        usersPage.forceGoToUsers();

        assertFalse(usersPage.isUserInList(userToDeleteFirstName, userToDeleteLastName, originalEmail),
                "Error: the user to delete is still there");
    }

    @Test
    void testDeleteAllUsers() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        usersPage.clickSelectAllUsersButton();

        usersPage.clickDeleteAllUsersButton();

        assertTrue(usersPage.isEmptyStateDisplayed(), "Empty state is not displayed");
    }
    @Test
    void testShowUser() {
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

        usersPage.clickUpperShowButton();

        assertTrue(usersPage.isTextPresentOnViewPage(testFirstName), "Username is not displayed");

        assertTrue(usersPage.isTextPresentOnViewPage(testEmail), "Email is not displayed");

        assertTrue(usersPage.isTextPresentOnViewPage(testLastName), "Last is not displayed");

        usersPage.clickUpperEditButton();

        assertFalse(usersPage.getCurrentUrl().contains("/show"), "Show page is still displayed");
    }
    @Test
    void testCreateUserValidation() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        int initialUsersCount = usersPage.getUsersCount();


        usersPage.clickCreateUser();

        usersPage.fillLastNameField("Smith");
        usersPage.fillEmailField("newemail@test.com");
        usersPage.clickSaveButtonForUsers();

        assertTrue(usersPage.getCurrentUrl().contains("/create"), "empty firstName was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is missing");

        usersPage.clearLastNameField();
        usersPage.fillFirstNameField("John");

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
    void testEditUserValidationWithoutFirstName() {
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
    void testEditUserValidationWithoutLastName() {
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
    void testEditUserValidationWithoutEmail() {
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
    void testEditUserValidationWithIncorrectEmail() {
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
    void testBulkDeleteUser() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((getDriver()));

        int initialRowCount = usersPage.getTableRowsCount();
        assertTrue(initialRowCount > 0, "The table is empty");
        System.out.println(initialRowCount);
        usersPage.selectFirstRowCheckbox();

        usersPage.clickBulkDeleteButton();

        usersPage.waitForSnackBar();

        int finalRowsCount = usersPage.getTableRowsCount();

        assertEquals(initialRowCount - 1, finalRowsCount, "Rows count hasn't changed");
    }
    @Test
    void testCancelUserCheckboxSelection() {
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
    void testPaginationFullFlow() {
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
}
