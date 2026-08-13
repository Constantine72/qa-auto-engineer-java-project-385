package hexlet.code.tests;

import hexlet.code.pages.KanbanPage;
import hexlet.code.pages.LabelsPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.StatusesPage;
import hexlet.code.pages.TasksPage;
import hexlet.code.pages.UsersPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;
import java.time.Duration;
import java.util.Map;
import java.util.LinkedHashMap;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import static org.junit.jupiter.api.Assertions.*;
import hexlet.code.utils.WebDriverFactory;

public final class KanbanTest {
    private WebDriver driver;
    private String baseurl;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        baseurl = System.getenv("APP_BASE_URL");
        if (baseurl == null || baseurl.trim().isEmpty()) {
            baseurl = "http://localhost:5173";
        }

        driver = new WebDriverFactory().createDriver();

        wait = new WebDriverWait(driver, Duration.ofSeconds(7));

        driver.get(baseurl);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testSuccessfulLogin() {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        assertTrue(kanbanPage.isWelcomeTitleDisplayed(), "The page is not loaded");
    }

    @Test
    public void testLoginWithEmptyFields() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("", "");

        String actualUsernameError = loginPage.getUsernameErrorMessage();
        assertTrue(actualUsernameError.contains("Required"), "No error message displayed");

        String actualPasswordError = loginPage.getPasswordErrorMessage();
        assertTrue(actualPasswordError.contains("Required"), "No error message displayed");
    }

    @Test
    public void testLoginWithEmptyFieldsConsecutively() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("qwe", "");

        assertTrue(loginPage.getCurrentUrl().contains("/login"), "Enter happened with password");

        assertTrue(loginPage.isRequiredErrorDisplayed());

        loginPage.refreshPage();

        loginPage.login("", "ASD");

        assertTrue(loginPage.getCurrentUrl().contains("/login"), "Enter happened with password");

        assertTrue(loginPage.isRequiredErrorDisplayed());
    }

    @Test
    public void testSuccessfulLogout() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        assertTrue(kanbanPage.isWelcomeTitleDisplayed(), "The page is not loaded");

        kanbanPage.clickLogout();

        assertTrue(loginPage.isUsernameFieldDisplayed(), "No login page after logout");
    }

    @Test
    public void testCreateNewUser() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String testEmail = "test@mail.com" + uniqueId;
        String testFirstName = "test" + uniqueId;
        String testLastName = "user" + uniqueId;

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));
        usersPage.clickCreateUser();

        assertTrue(usersPage.isUserFormDisplayed(), "User form hasn't opened");

        usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);

        kanbanPage.goToUsers();

        assertTrue(usersPage.isUserInList(testFirstName, testLastName, testEmail), "Created user "
                + testFirstName + "not found");
    }

    @Test
    public void testUserListLoadingAndFields() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

        assertTrue(usersPage.isUserTableLoaded(), "The table has not loaded");

        assertTrue(usersPage.areKeyFieldsDisplayed(), "Fields are missing");
    }

    @Test
    public void testEditUserAndValidation() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

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

        usersPage.clickSaveButton();

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
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

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
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

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
    public void testDeleteAllUsers() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

        usersPage.clickSelectAllUsersButton();

        usersPage.clickDeleteAllUsersButton();

        assertTrue(usersPage.isEmptyStateDisplayed(), "Empty state is not displayed");
    }

    @Test
    public void testCreateNewStatus() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String name = "In Progress " + uniqueId;
        String slug = "in-progress" + uniqueId;

        statusesPage.forceGoToStatuses();
        statusesPage.clickCreateStatus();
        statusesPage.fillAndSubmitStatusForm(name, slug);

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.isStatusInList(name), "Status has not been created");
    }

    @Test
    public void testDefaultStatusesArePresent() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);

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
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.areHeaderDisplayed(), "Name and Slug headers are missing");

        assertTrue(statusesPage.isStatusRowCorrect("Draft", "draft"),
                "improper order");

        int rowsCount = statusesPage.getRowsCount();

        assertTrue(rowsCount > 0, "Statuses page is empty or data are not loaded");
    }

    @Test
    public void testEditStatus() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);

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
    public void testDeleteStatus() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);

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
    public void testDeleteAllStatuses() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);
        statusesPage.forceGoToStatuses();


        statusesPage.clickSelectAllUsersButton();

        statusesPage.clickDeleteAllUsersButton();

        assertTrue(statusesPage.isEmptyStateDisplayed(), "Empty state is not displayed");
    }

    @Test
    public void testCreateNewLabel() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(driver);

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
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(driver);

        labelsPage.forceGoToLabels();

        assertTrue(labelsPage.areHeaderDisplayed(), "Name is missing");

        int rowsCount = labelsPage.getRowsCount();

        assertTrue(rowsCount > 0, "Labels page is empty or data are not loaded");
    }


    @Test
    public void testEditLabel() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(driver);

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
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(driver);

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
    public void testCreateNewTask() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((driver));
        tasksPage.clickCreateTask();

        assertTrue(tasksPage.isTaskFormDisplayed(), "Task form hasn't opened");

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String taskTitle = "SomeTask_" + uniqueId;
        String taskStatus = "2";
        String taskValue = "1";
        String targetColumn = "To Review";
        String assigneeName = "john@google.com";

        tasksPage.fillAndSubmitTaskForm(taskTitle, taskStatus, taskValue);

        tasksPage.forceGoToTasks();

        assertTrue(tasksPage.isTaskInColumn(taskTitle, targetColumn),
                "task '" + taskTitle + "' is not found in column '" + targetColumn + "'");

        tasksPage.openTaskForEditing(taskTitle);
        assertTrue(tasksPage.isAssigneeCorrectInDetails(assigneeName), "assignee hasn't been saved");
        assertTrue(tasksPage.isTaskCorrectInDetails(taskTitle), "assignee hasn't been saved");
        assertTrue(tasksPage.isColumnCorrectInDetails(targetColumn), "column name hasn't been saved");
    }

    @Test
    public void testTaskViewingAndFiltering() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((driver));
        //=======================status============================
        int initialCardsCount = tasksPage.getTaskCardsCount();

        assertTrue(initialCardsCount > 0, "the table is blank");

        String urlBeforeFilter = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();

        String targetStatus = "Draft";
        tasksPage.filterByStatus(targetStatus);
        tasksPage.waitForUrlToBe(urlBeforeFilter);

        try {
            tasksPage.waitForCardsCount(3);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot",
                    new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        List<String> statusFilteredCards = tasksPage.getVisibleStatusesInTable();

        assertFalse(statusFilteredCards.isEmpty(), "the table is empty");

        assertEquals(3, statusFilteredCards.size(), "error: incorrect numbers of cards");

        assertTrue(statusFilteredCards.stream().anyMatch(c -> c.contains("Task 11")), "no task 11");
        assertTrue(statusFilteredCards.stream().anyMatch(c -> c.contains("Task 5")), "no task 5");
        assertTrue(statusFilteredCards.stream().anyMatch(c -> c.contains("Task 6")), "no task 6");

        boolean onlyDraftTasks = statusFilteredCards.stream()
                .allMatch(c -> c.contains("Task 11") || c.contains("Task 5") || c.contains("Task 6"));
        assertTrue(onlyDraftTasks, "improper tasks are displayed");

        tasksPage.clearAllFilters();
        //============================================================

        //==========================Assignee===================================
        String urlBeforeAssignee = tasksPage.getCurrentUrl();
        tasksPage.waitForCardsToLoad();

        String targetWorker = "alice@hotmail.com";

        tasksPage.waitForCardsToLoad();

        tasksPage.rememberOldCard();
        tasksPage.filterByAssignee(targetWorker);
        tasksPage.waitForOldCardToDisappear();

        tasksPage.waitForUrlToChange(urlBeforeAssignee);

        try {
            tasksPage.waitForCardsCount(2);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        tasksPage.waitForOldCardToDisappear();
        List<String> assigneeFilteredCards = tasksPage.getVisibleStatusesInTable();

        assertFalse(assigneeFilteredCards.isEmpty(), "table is empty");

        assertEquals(2, assigneeFilteredCards.size(), "improper number of tasks");

        assertTrue(assigneeFilteredCards.stream().anyMatch(c -> c.contains("Task 8")), "no task 8");
        assertTrue(assigneeFilteredCards.stream().anyMatch(c -> c.contains("Task 9")), "no task 9");

        boolean onlyAliceTasks = assigneeFilteredCards.stream()
                .allMatch(c -> c.contains("Task 8") || c.contains("Task 9"));
        assertTrue(onlyAliceTasks, "error: improper tasks are displayed");

        tasksPage.clearAllFilters();
        //============================================================

        //=================================Label=============================
        String urlBeforeLabel = tasksPage.getCurrentUrl();
        String targetLabel = "bug";

        tasksPage.waitForCardsToLoad();

        tasksPage.filterByLabel(targetLabel);

        tasksPage.waitForUrlToChange(urlBeforeLabel);

        try {
            tasksPage.waitForCardsCount(2);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        List<String> labelFilteredCards = tasksPage.getVisibleStatusesInTable();

        assertFalse(labelFilteredCards.isEmpty(), "error: bug filter is empty");

        assertEquals(2, labelFilteredCards.size(), "improper number of cards");

        assertTrue(labelFilteredCards.stream().anyMatch(c -> c.contains("Task 7")), "no task 7");
        assertTrue(labelFilteredCards.stream().anyMatch(c -> c.contains("Task 3")), "no task 3");

        boolean onlyBugTasks = labelFilteredCards.stream()
                .allMatch(c -> c.contains("Task 7") || c.contains("Task 3"));
        assertTrue(onlyBugTasks, "error: an improper task is shown");

        tasksPage.clearAllFilters();
        //=========================================================================

        //===============================AssigneeWithNoCards===================================
        String urlBeforeAssignee2 = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();

        String targetWorker2 = "emily@example.com";

        tasksPage.filterByAssignee(targetWorker2);

        tasksPage.waitForUrlToChange(urlBeforeAssignee2);
        try {
            tasksPage.waitForCardsCount(0);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail("no cards should be displayed");
        }
        List<String> emptyBoardCards = tasksPage.getVisibleStatusesInTable();
        assertTrue(emptyBoardCards.isEmpty(), "table should be empty");

        tasksPage.clearAllFilters();
        //==============================================================================

        //================================Assignee+Status===============================
        String urlCombo15 = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();
        String targetWorker6 = "alice@hotmail.com";

        tasksPage.filterByAssignee(targetWorker6);

        tasksPage.waitForUrlToChange(urlCombo15);

        try {
            tasksPage.waitForCardsCount(2);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        tasksPage.waitForCardsToLoad();

        tasksPage.filterByStatus("To Be Fixed");

        try {
            tasksPage.waitForCardsCount(1);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        List<String> comboCardsNew = tasksPage.getVisibleStatusesInTable();
        assertEquals(1, comboCardsNew.size(), "1 task should be on board");
        assertTrue(comboCardsNew.get(0).contains("Task 8"), "combo hasn't returned task 8");

        tasksPage.clearAllFilters();

        //=================================================================================

        //=================================ChangeAssignee================================

        String urlAlice = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();
        String targetWorker4 = "alice@hotmail.com";

        tasksPage.filterByAssignee(targetWorker4);

        tasksPage.waitForUrlToChange(urlAlice);

        try {
            tasksPage.waitForCardsCount(2);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        tasksPage.waitForCardsToLoad();

        String targetWorker5 = "john@google.com";
        tasksPage.filterByAssignee(targetWorker5);

        try {
            tasksPage.waitForCardsCount(5);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        List<String> johnCards = tasksPage.getVisibleStatusesInTable();
        assertTrue(johnCards.stream().anyMatch(c -> c.contains("Task 15")), "task 15 is not displayed");

        boolean hasOnlyJohnTasks = johnCards.stream()
                .allMatch(c -> c.contains("Task 11")
                        ||
                        c.contains("Task 2")
                        ||
                        c.contains("Task 1")
                        ||
                        c.contains("Task 15")
                        ||
                        c.contains("Task 5"));

        assertTrue(hasOnlyJohnTasks, "improper tasks are shown");

        tasksPage.clearAllFilters();
        //===========================================================================

        //===============================RemoveAllFilters===========================

        String urlBeforeClear = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();

        String targetWorker7 = "alice@hotmail.com";

        tasksPage.filterByAssignee(targetWorker7);

        tasksPage.waitForUrlToChange(urlBeforeClear);

        try {
            tasksPage.waitForCardsCount(2);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        tasksPage.clearAllFilters();

        try {
            tasksPage.waitForCardsCount(15);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail("15 tasks should be displayed");
        }

        List<String> allCards = tasksPage.getVisibleStatusesInTable();
        assertTrue(allCards.stream().anyMatch(c -> c.contains("Task 1")), "Task 1 is not shown");
        assertTrue(allCards.stream().anyMatch(c -> c.contains("Task 15")), "Task 1 is not shown");
        //===============================================================================

        //===========================RemoveStatusFromAssigneeAndStatus=====================
        String urlCombo11 = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();
        String targetWorker11 = "alice@hotmail.com";

        try {
            tasksPage.filterByAssignee(targetWorker11);
        } catch (TimeoutException e) {

            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail("filter is broken");
        }

        tasksPage.waitForUrlToChange(urlCombo11);

        try {
            tasksPage.waitForCardsCount(2);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        tasksPage.waitForCardsToLoad();

        tasksPage.filterByStatus("To Be Fixed");

        try {
            tasksPage.waitForCardsCount(1);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        tasksPage.removeStatusFilter();

        try {
            tasksPage.waitForCardsCount(2);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been changed");
        }

        List<String> aliceCardsAfterRemoval = tasksPage.getVisibleStatusesInTable();
        assertEquals(2, aliceCardsAfterRemoval.size(), "2 task2 should be on board");
        assertTrue(aliceCardsAfterRemoval.stream().allMatch(c -> c.contains("Task 8") || c.contains("Task 9")),
                "only Alice tasks should be displayed");

        tasksPage.clearAllFilters();

        //=================================================================================

        //======================================SaveFilterAlice=================================
        tasksPage.waitForCardsToLoad();
        String targetWorker12 = "alice@hotmail.com";

        tasksPage.filterByAssignee(targetWorker12);

        try {
            tasksPage.waitForCardsCount(2);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        tasksPage.waitForCardsToLoad();

        String myFilterName = "Alice_Custom_Filter";
        tasksPage.openSaveQueryModal();
        tasksPage.saveCurrentQueryAs(myFilterName);

        tasksPage.waitForCardsToLoad();

        tasksPage.clearAllFilters();

        tasksPage.waitForCardsCount(15);

        tasksPage.applySavedQuery(myFilterName);

        try {
            tasksPage.waitForCardsCount(2);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail("custom filter hasn't been applied");
        }

        List<String> savedQueryCards = tasksPage.getVisibleStatusesInTable();
        assertEquals(2, savedQueryCards.size(), "number of cards is not 2");
        assertTrue(savedQueryCards.stream().allMatch(c -> c.contains("Task 8") || c.contains("Task 9")),
                "only Alice tasks should be displayed");
        //=================================================================================

        //================================DeleteCustomQuery================================
        tasksPage.deleteSavedQuery(myFilterName);
        boolean isFilterStillThere = tasksPage.isSavedQueryPresent(myFilterName);
        assertFalse(isFilterStillThere, myFilterName + " has not been deleted");

        tasksPage.clearAllFilters();
        //=================================================================================

        //==========================================AliceWithZeroTasks==========================
        String urlCombo13 = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();

        String targetWorker13 = "alice@hotmail.com";

        tasksPage.filterByAssignee(targetWorker13);

        tasksPage.waitForUrlToChange(urlCombo13);

        try {
            tasksPage.waitForCardsCount(2);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        tasksPage.waitForCardsToLoad();

        tasksPage.filterByStatus("Draft");

        try {
            tasksPage.waitForCardsCount(0);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        List<String> visibleCards = tasksPage.getVisibleStatusesInTable();

        assertTrue(visibleCards.isEmpty(), "The table should be empty");

        tasksPage.clearAllFilters();
        //======================================================================================

        //=============================ThreeFilters========================================

        tasksPage.waitForCardsToLoad();

        String targetWorker14 = "alice@hotmail.com";
        String targetStatus14 = "To Be Fixed";
        String targetLabel14 = " feature";

        tasksPage.filterByAssignee(targetWorker14);
        tasksPage.filterByStatus(targetStatus14);
        tasksPage.filterByLabel(targetLabel14);

        try {
            tasksPage.waitForCardsCount(1);
        } catch (TimeoutException e) {
            Allure.addAttachment("Screenshot", new ByteArrayInputStream(((TakesScreenshot)
                    driver).getScreenshotAs(OutputType.BYTES)));
            fail(" filter hasn't been applied");
        }

        List<String> visibleCardAlice = tasksPage.getVisibleStatusesInTable();
        assertEquals(1, visibleCardAlice.size(), "number of cards is not 1");
        assertTrue(visibleCardAlice.get(0).contains("Task 8"), "an improper task is returned");
    }
    //=================================================================================

    @Test
    public void testEditTask() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((driver));

        String updatedName = "new task" + System.currentTimeMillis();

        String expectedDescription = "Description of task 15";
        String expectedAssignee = "john@google.com";

        tasksPage.clickCreateTask();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String taskTitle = "SomeTask_" + uniqueId;
        String taskStatus = "2";
        String taskValue = "1";

        tasksPage.fillAndSubmitTaskForm(taskTitle, taskStatus, taskValue, expectedDescription);

        tasksPage.forceGoToTasks();

        tasksPage.openTaskForEditing(taskTitle);

        tasksPage.updateTaskName(updatedName);

        tasksPage.waitForCardWithTitle(updatedName);

        assertTrue(tasksPage.isCardPresent(updatedName));

        tasksPage.waitForNewCardToAppear(updatedName);

        assertTrue(tasksPage.isNewCardDisplayed(updatedName));

        assertTrue(tasksPage.areOldCardsEmpty(taskTitle));

        tasksPage.openTaskForEditing(updatedName);

        String actualDescription = tasksPage.getDescriptionInputValue();
        String actualAssignee = tasksPage.getAssigneeDropdownValue();

        assertEquals(expectedDescription, actualDescription, "the description is missing");
        assertEquals(expectedAssignee, actualAssignee, "the description is missing");
    }

    @Test
    public void testMoveTaskToAnotherStatus() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((driver));

        String taskToMove = "Task 11";
        String newStatusId = "2";

        tasksPage.openTaskForEditing(taskToMove);

        tasksPage.changeTaskStatus(newStatusId);

        tasksPage.waitForTasksUpdate(5);

        tasksPage.filterByStatus(newStatusId);

        tasksPage.waitForTasksUpdate(5);

        boolean isCardMoved = tasksPage.isTaskVisible(taskToMove);

        Assertions.assertTrue(isCardMoved, "Card " + taskToMove + "' is not visible in the new status column");

        tasksPage.clearAllFilters();
    }

    @Test
    public void testDeleteTask() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((driver));

        String taskToDelete = "Task 5";

        int initialCount = tasksPage.getVisibleTasksCount();

        tasksPage.openTaskForEditing(taskToDelete);

        tasksPage.clickDelete();

        tasksPage.waitForTasksUpdate(5);

        int currentTasksCount = tasksPage.getVisibleTasksCount();

        assertEquals(initialCount - 1, currentTasksCount, "The number of tasks hasn't changed");

        assertTrue(tasksPage.isTaskGone(taskToDelete), "Error: a deleted task is still present");
    }

    @Test
    public void testShowTask() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((driver));

        tasksPage.clickCreateTask();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String taskTitle = "test task " + uniqueId;
        String taskDesc = "DescriptionFor " + uniqueId;
        String taskStatus = "2";
        String taskValue = "1";

        tasksPage.fillAndSubmitTaskForm(taskTitle, taskStatus, taskValue, taskDesc);

        tasksPage.forceGoToTasks();

        tasksPage.openTaskForViewing(taskTitle);

        assertTrue(tasksPage.isTextPresentOnViewPage(taskTitle), "the task "
                + taskTitle + " is not displayed");

        assertTrue(tasksPage.isTextPresentOnViewPage(taskDesc), "the task description "
                + taskDesc + " is not displayed");
    }

    @Test
    public void testShowUser() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

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
    public void testShowStatus() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);
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
    public void testShowLabel() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String labelName = "Label " + uniqueId;

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(driver);

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
    public void testCreateUserValidation() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

        int initialUsersCount = usersPage.getUsersCount();


        usersPage.clickCreateUser();

        usersPage.fillLastNameField("Smith");
        usersPage.fillEmailField("newemail@test.com");
        usersPage.clickSaveButton();

        assertTrue(usersPage.getCurrentUrl().contains("/create"), "empty firstName was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is missing");

        usersPage.clearLastNameField();
        usersPage.fillFirstNameField("John");

        usersPage.clickSaveButton();

        assertTrue(usersPage.getCurrentUrl().contains("/create"), "empty firstName was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is missing");

        usersPage.fillLastNameField("Smith");
        usersPage.clearEmailField();
        usersPage.clickSaveButton();

        assertTrue(usersPage.getCurrentUrl().contains("/create"), "empty firstName was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is missing");

        String badEmail = "notAnEmail";

        usersPage.fillEmailField(badEmail);
        usersPage.clickSaveButton();

        assertTrue(usersPage.getCurrentUrl().contains("/create"), "empty firstName was saved");

        assertTrue(usersPage.isInvalidEmailErrorDisplayed(), "Incorrect email error is not shown");

        usersPage.forceGoToUsers();

        int finalUsersCount = usersPage.getFinalUsersCount();
        assertEquals(initialUsersCount, finalUsersCount, "improper user has been created");

        boolean isUserCreatedAnyway = usersPage.isTextPresentOnPage(badEmail);
        assertFalse(isUserCreatedAnyway, "improper user has been saved");
    }

    @Test
    public void testCreateLabelValidation() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(driver);

        labelsPage.forceGoToLabels();

        int initialLabelsCount = labelsPage.getInitialLabelsCount();

        labelsPage.clickCreateLabel();
        labelsPage.triggerValidationOnNameField();
        labelsPage.clickSaveButton();

        assertTrue(labelsPage.getCurrentUrl().contains("/create"), "Empty label has been created");

        assertTrue(labelsPage.isRequiredErrorDisplayed(), "No required error is displayed");

        labelsPage.forceGoToLabels();

        labelsPage.waitForListToLoad();

        int finalLabelsCount = labelsPage.getFinalLabelsCount();

        assertEquals(initialLabelsCount, finalLabelsCount, "empty label has been saved");
    }

    @Test
    public void testCreateStatusValidation() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);


        statusesPage.forceGoToStatuses();

        statusesPage.waitForListToLoad();

        int initialStatusesCount = statusesPage.getInitialStatusesCount();

        statusesPage.clickCreateStatus();

        statusesPage.fillNameField("Temp status name");
        statusesPage.clickSaveButton();

        assertTrue(statusesPage.getCurrentUrl().contains("/create"), "Empty slug has been created");

        assertTrue(statusesPage.isRequiredErrorDisplayed(), "Required is not displayed");

        statusesPage.clearNameField();

        statusesPage.fillSlugField("Temp slug name");
        statusesPage.clickSaveButton();

        assertTrue(statusesPage.getCurrentUrl().contains("/create"), "Empty slug has been created");

        assertTrue(statusesPage.isRequiredErrorDisplayed(), "Required is not displayed");

        statusesPage.forceGoToStatuses();

        statusesPage.waitForListToLoad();

        int finalStatusesCount = statusesPage.getFinalStatusesCount();

        assertEquals(initialStatusesCount, finalStatusesCount, "empty status has been saved");
    }

    @Test
    public void testCreateTaskValidation() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String taskTitle = "SomeTask_" + uniqueId;
        String taskStatus = "2";
        String assigneeName = "1";

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((driver));

        tasksPage.waitForListToLoad();

        int initialTasksCount = tasksPage.getInitialTasksCount();

        tasksPage.clickCreateTask();

        tasksPage.selectAssignee(assigneeName);
        tasksPage.selectStatus(taskStatus);
        tasksPage.clickSaveButton();

        assertTrue(tasksPage.getCurrentUrl().contains("/create"), "Empty task has been created");

        assertTrue(tasksPage.isRequiredErrorDisplayed(), "Required is not displayed");

        tasksPage.refreshPage();

        tasksPage.fillTaskTitle(taskTitle);
        tasksPage.selectStatus(taskStatus);

        tasksPage.clickSaveButton();

        assertTrue(tasksPage.getCurrentUrl().contains("/create"), "task has been created w/o assignee");

        assertTrue(tasksPage.isRequiredErrorDisplayed(), "Required is not displayed");

        tasksPage.refreshPage();

        tasksPage.fillTaskTitle(taskTitle);
        tasksPage.selectAssignee(assigneeName);
        tasksPage.clickSaveButton();

        assertTrue(tasksPage.getCurrentUrl().contains("/create"), "task has been created w/o status");

        assertTrue(tasksPage.isRequiredErrorDisplayed(), "Required is not displayed");

        kanbanPage.goToTasks();

        int finalTasksCount = tasksPage.getFinalTasksCount();

        assertEquals(initialTasksCount, finalTasksCount, "empty status has been saved");
    }

    @Test
    public void testEditUserValidationWithoutFirstName() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String testFirstName = "John " + uniqueId;
        String testLastName = "Smith " + uniqueId;
        String testEmail = "user" + uniqueId + "@test.com";

        usersPage.clickCreateUser();

        usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);

        kanbanPage.goToUsers();

        usersPage.clickEditUser(testFirstName);

        usersPage.clearFirstNameField();

        usersPage.clickSaveButton();

        usersPage.waitForSnackBar();

        assertFalse(usersPage.getCurrentUrl().endsWith("/users"), "user w/o first name was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is not displayed");
    }

    @Test
    public void testEditUserValidationWithoutLastName() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String testFirstName = "John " + uniqueId;
        String testLastName = "Smith " + uniqueId;
        String testEmail = "user" + uniqueId + "@test.com";

        usersPage.clickCreateUser();

        usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);

        kanbanPage.goToUsers();

        usersPage.clickEditUser(testFirstName);

        usersPage.clearLastNameField();

        usersPage.clickSaveButton();

        assertFalse(usersPage.getCurrentUrl().endsWith("/users"), "user w/o last name was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is not displayed");

    }

    @Test
    public void testEditUserValidationWithoutEmail() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String testFirstName = "John " + uniqueId;
        String testLastName = "Smith " + uniqueId;
        String testEmail = "user" + uniqueId + "@test.com";

        usersPage.clickCreateUser();

        usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);

        kanbanPage.goToUsers();

        usersPage.clickEditUser(testFirstName);

        usersPage.clearEmailField();

        usersPage.clickSaveButton();

        assertFalse(usersPage.getCurrentUrl().endsWith("/users"), "user w/o last name was saved");

        assertTrue(usersPage.isRequiredErrorDisplayed(), "Required is not displayed");
    }

    @Test
    public void testEditUserValidationWithIncorrectEmail() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

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

        usersPage.clickSaveButton();

        assertFalse(usersPage.getCurrentUrl().endsWith("/users"), "user w/o last name was saved");

        assertTrue(usersPage.isInvalidEmailErrorDisplayed(), "no error message for improper email");
    }

    @Test
    public void testEditTaskWithoutTitle() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((driver));

        String expectedDescription = "Description of task 15";

        tasksPage.clickCreateTask();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String taskTitle = "SomeTask_" + uniqueId;
        String taskStatus = "2";
        String taskValue = "1";

        tasksPage.fillAndSubmitTaskForm(taskTitle, taskStatus, taskValue, expectedDescription);

        tasksPage.forceGoToTasks();

        tasksPage.openTaskForEditing(taskTitle);

        tasksPage.clearTitleField();

        tasksPage.clickSaveButton();

        tasksPage.waitForSnackBar();

        assertFalse(tasksPage.getCurrentUrl().endsWith("/tasks"), "task w/o title was saved");

        assertTrue(tasksPage.isRequiredErrorDisplayed(), "no required message");
    }

    @Test
    public void testEditLabelWithoutName() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String labelName = "Label " + uniqueId;

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(driver);

        labelsPage.forceGoToLabels();

        labelsPage.clickCreateLabel();

        labelsPage.fillAndSubmitLabelForm(labelName);

        labelsPage.forceGoToLabels();

        labelsPage.clickEditLabel(labelName);

        labelsPage.clearLabelField();

        labelsPage.clickSaveButton();

        labelsPage.waitForSnackBar();

        assertFalse(labelsPage.getCurrentUrl().endsWith("/labels"), "label w/o title was saved");

        assertTrue(labelsPage.isRequiredErrorDisplayed(), "no required message");
    }

    @Test
    public void testEditStatusWithoutName() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);

        statusesPage.forceGoToStatuses();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String initialName = "ToEdit_" + uniqueId;
        String initialSlug = "to-edit" + uniqueId;

        statusesPage.clickCreateStatus();

        statusesPage.fillAndSubmitStatusForm(initialName, initialSlug);

        statusesPage.forceGoToStatuses();

        statusesPage.clickEditStatus(initialName);

        statusesPage.clearNameField();

        statusesPage.clickSaveButton();

        assertFalse(statusesPage.getCurrentUrl().endsWith("/statuses"), "status w/o name was saved");

        assertTrue(statusesPage.isRequiredErrorDisplayed(), "no required message");
    }

    @Test
    public void testEditStatusWithoutSlug() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);

        statusesPage.forceGoToStatuses();

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String initialName = "ToEdit_" + uniqueId;
        String initialSlug = "to-edit" + uniqueId;

        statusesPage.clickCreateStatus();

        statusesPage.fillAndSubmitStatusForm(initialName, initialSlug);

        statusesPage.forceGoToStatuses();

        statusesPage.clickEditStatus(initialName);

        statusesPage.clearSlugField();

        statusesPage.clickSaveButton();

        statusesPage.waitForSnackBar();

        assertFalse(statusesPage.getCurrentUrl().endsWith("/statuses"), "status w/o slug was saved");

        assertTrue(statusesPage.isRequiredErrorDisplayed(), "no required message");
    }

    @Test
    public void testBulkDeleteUser() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

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
    public void testBulkDeleteLabel() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(driver);

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
    public void testBulkDeleteStatus() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);

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
    public void testCancelLabelCheckboxSelection() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        LabelsPage labelsPage = new LabelsPage(driver);

        labelsPage.forceGoToLabels();

        assertTrue(labelsPage.getTableRowsCount() > 0, "Labels table is empty");

        labelsPage.selectFirstRowCheckbox();
        labelsPage.clickUnselectCrossButton();

        assertTrue(labelsPage.isSelectionTextHidden(), "1 item selected is still displayed");
    }

    @Test
    public void testCancelUserCheckboxSelection() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

        assertTrue(usersPage.getTableRowsCount() > 0, "Labels table is empty");

        usersPage.selectFirstRowCheckbox();
        usersPage.clickUnselectCrossButton();

        assertTrue(usersPage.isSelectionTextHidden(), "1 item selected is still displayed");
    }

    @Test
    public void testCancelStatusCheckboxSelection() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.getTableRowsCount() > 0, "Labels table is empty");

        statusesPage.selectFirstRowCheckbox();
        statusesPage.clickUnselectCrossButton();

        assertTrue(statusesPage.isSelectionTextHidden(), "1 item selected is still displayed");
    }

    @Test
    public void testPaginationFullFlow() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));

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
    public void testTasksFilterByStatusOnGrid() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((driver));

        int cardsBefore = tasksPage.getTaskCardsCount();

        assertTrue(cardsBefore > 0, "no cards on board");

        String urlBeforeFilter = tasksPage.getCurrentUrl();

        String targetStatus = "Draft";
        tasksPage.filterByStatus(targetStatus);

        tasksPage.waitForUrlToChange(urlBeforeFilter);

        List<String> cardsTexts = tasksPage.getVisibleStatusesInTable();

        assertFalse(cardsTexts.isEmpty(), "table is empty: there's no " + targetStatus + "'");

        for (String cardText : cardsTexts) {
            if (cardText.contains("Published") || cardText.contains("To Publish") || cardText.contains("To Be Fixed")
                    || cardText.contains("To Review")) {
                fail("There's an extra card " + cardText);
            }
        }
    }
}
