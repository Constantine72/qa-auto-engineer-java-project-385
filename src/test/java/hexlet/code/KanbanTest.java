package hexlet.code;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import java.time.Duration;
import java.util.Map;
import java.util.LinkedHashMap;


import static org.junit.jupiter.api.Assertions.*;

public class KanbanTest {
    private WebDriver driver;
    private String baseurl;

    @BeforeEach
    public void setUp() {
        baseurl = System.getenv("APP_BASE_URL");
        if (baseurl == null || baseurl.trim().isEmpty()) {
            baseurl = "http://localhost:5173";
        }

        //new code for testing headless mode in CI 36-40 and 45
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");


        driver = new ChromeDriver(options);

        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
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
        Assertions.assertTrue(actualUsernameError.contains("Required"), "No error message displayed");

        String actualPasswordError = loginPage.getPasswordErrorMessage();
        Assertions.assertTrue(actualPasswordError.contains("Required"), "No error message displayed");
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

        assertTrue(usersPage.isUserInList(testFirstName, testLastName, testEmail), "Created user " + testFirstName + "not found");
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

        assertTrue(usersPage.isUserInList(originalFirstName, originalLastName, originalEmail), "User for edit has not been created");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        usersPage.clickEditUser(originalFirstName);

        assertEquals(originalFirstName, usersPage.getFirstNameValue(), "the name does not coincide");
        assertEquals(originalEmail, usersPage.getEmailValue(), "email does not coincide");

        usersPage.fillEmailOnly("qweqweqwe");

        usersPage.clickSaveButton();

        assertTrue(usersPage.isEmailValidationErrorDisplayed(), "No error message displayed");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String newFirstName = "NewFirstName_" + uniqueId;
        String newLastName = "NewLastName_" + uniqueId;
        String newEmail = "new" + uniqueId + "@example.com";

        usersPage.fillAndSubmitUserForm(newEmail, newFirstName, newLastName);

        assertTrue(usersPage.isUserInList(newFirstName, newLastName, newEmail));
        assertFalse(usersPage.isUserInList(originalFirstName, originalLastName, originalEmail), "old userName is still on the list");

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

        assertTrue(usersPage.isUserInList(userToDeleteFirstName, userToDeleteLastName, originalEmail), "User for edit has not been created");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        usersPage.clickEditUser(userToDeleteFirstName);

        usersPage.clickDeleteButton();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        usersPage.forceGoToUsers();

        assertFalse(usersPage.isUserInList(userToDeleteFirstName, userToDeleteLastName, originalEmail), "Error: the user to delete is still there");
    }

    @Test
    public void testDeleteAllUsers() {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));


        usersPage.clickSelectAllUsersButton();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }

        usersPage.clickDeleteAllUsersButton();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }

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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

            assertTrue(statusesPage.isStatusRowCorrect(expectedName, expectedSlug), "No name and slug found");
        }
    }

    @Test
    public void testStatusesListView() {


        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.areHeaderDisplayed(), "Name and Slug headers are missing");

        assertTrue(statusesPage.isStatusRowCorrect("Draft", "draft"), "improper order");

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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        statusesPage.forceGoToStatuses();

        statusesPage.clickEditStatus(initialName);

        String updatedName = "Updated_" + uniqueId;
        String updatedSlug = "updated-" + uniqueId;

        statusesPage.fillAndSubmitEditForm(updatedName, updatedSlug);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.isStatusRowCorrect(updatedName, updatedSlug), "changed status not found");

        String oldXPath = "//*[contains(., '" + initialName + "')]";
        boolean isOldNamePresent = false;

        try {
            isOldNamePresent = driver.findElement(By.xpath(oldXPath)).isDisplayed();
        } catch (Exception ignored) {
        }

        assertFalse(isOldNamePresent, "old name '" + initialName + "' is still displayed)");
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

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.isStatusInList(nameToDelete));
        statusesPage.clickEditStatus(nameToDelete);

        statusesPage.clickDeleteButton();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }


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

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }

        statusesPage.clickDeleteAllUsersButton();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }

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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        labelsPage.forceGoToLabels();

        labelsPage.clickEditLabel(initialName);

        String updatedName = "Updated_" + uniqueId;


        labelsPage.fillAndSubmitEditForm(updatedName);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        labelsPage.forceGoToLabels();

        assertTrue(labelsPage.isLabelInList(updatedName), "changed label not found");

        String oldXPath = "//*[contains(., '" + initialName + "')]";
        boolean isOldNamePresent = false;

        try {
            isOldNamePresent = driver.findElement(By.xpath(oldXPath)).isDisplayed();
        } catch (Exception ignored) {
        }

        assertFalse(isOldNamePresent, "old name '" + initialName + "' is still displayed)");
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

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }

        labelsPage.forceGoToLabels();

        assertTrue(labelsPage.isLabelInList(labelToDelete));
        labelsPage.clickEditLabel(labelToDelete);

        labelsPage.clickDeleteButton();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

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

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

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

        int totalTasks = tasksPage.getVisibleTasksCount();

        Assertions.assertEquals(totalTasks, 15, "Expected 15 cards, but loaded" + totalTasks);

        tasksPage.applyStatusFilter("2");

        tasksPage.waitForTasksUpdate(15);

        int draftTasks = tasksPage.getVisibleTasksCount();
        Assertions.assertTrue(draftTasks < 15, "Filter failed");
        Assertions.assertTrue(draftTasks > 0, "No cards left");


        tasksPage.clearAllFilters();

        tasksPage.waitForTasksUpdate(draftTasks);

        int resetTasks = tasksPage.getVisibleTasksCount();
        Assertions.assertEquals(resetTasks, 15, "After filter the amount of cards is not 15");

        tasksPage.applyAssigneeFilter("2");

        tasksPage.waitForTasksUpdate(15);

        int assigneeTasks = tasksPage.getVisibleTasksCount();
        Assertions.assertTrue(assigneeTasks < 15, "improper number of tasks");
        Assertions.assertTrue(assigneeTasks > 0, "no cards displayed");

        tasksPage.clearAllFilters();
        tasksPage.waitForTasksUpdate(assigneeTasks);

        Assertions.assertEquals(tasksPage.getVisibleTasksCount(), 15, "The number of cards is not 15");


        tasksPage.applyLabelFilter("2");
        tasksPage.waitForTasksUpdate(15);

        int labelTasks = tasksPage.getVisibleTasksCount();
        Assertions.assertTrue(labelTasks < 15, "filter has not been applied");
        Assertions.assertTrue(labelTasks > 0, "no cards displayed");

        tasksPage.clearAllFilters();
        tasksPage.waitForTasksUpdate(labelTasks);

        Assertions.assertEquals(tasksPage.getVisibleTasksCount(), 15, "not 15 cardsz`");


    }

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

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        tasksPage.forceGoToTasks();


        tasksPage.openTaskForEditing(taskTitle);


        tasksPage.updateTaskName(updatedName);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        By newCardLocator = By.xpath("//div[contains(@class, 'RaList-content')]//*[text()='" +
                updatedName + "']");

        By oldCardLocator = By.xpath("//div[contains(@class, 'RaList-content')]//*[text()='" +
                taskTitle + "']");

        List<WebElement> oldcards = driver.findElements(oldCardLocator);


        WebElement newCard = wait.until(ExpectedConditions.presenceOfElementLocated(newCardLocator));


        Assertions.assertTrue(newCard.isDisplayed(), "changes haven't been applied: new card name is missing");
        Assertions.assertTrue(oldcards.isEmpty(), "old task " + taskTitle + " is still displayed");

        tasksPage.openTaskForEditing(updatedName);

        String actualDescription = tasksPage.getDescriptionInputValue();
        String actualAssignee = tasksPage.getAssigneeDropdownValue();

        Assertions.assertEquals(expectedDescription, actualDescription, "the description is missing");
        Assertions.assertEquals(expectedAssignee, actualAssignee, "the description is missing");

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

        tasksPage.selectDropdownOption(By.xpath("//div[@data-source='status_id']"), newStatusId);

        tasksPage.waitForTasksUpdate(5);

        By moveCardLocator = By.xpath("//div[contains(@class, 'MuiCard-root')]//*[text()='" + taskToMove +
                "']");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement moveCard = wait.until(ExpectedConditions.presenceOfElementLocated(moveCardLocator));

        Assertions.assertTrue(moveCard.isDisplayed(), "The task hasn't been moved to another status");

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

        tasksPage.openTaskForEditing(taskToDelete);

        tasksPage.clickDelete();

        tasksPage.waitForTasksUpdate(5);

        int currentTasksCount = tasksPage.getVisibleTasksCount();

        Assertions.assertEquals(currentTasksCount, 14, "The number of tasks hasn't changed");

        By deleteCardLocator = By.xpath("//div[contains(@class, 'MuiCard-root')]//*[text()='" +
                taskToDelete + "']");

        boolean istaskGone = driver.findElements(deleteCardLocator).isEmpty();

        Assertions.assertTrue(istaskGone, "Error: a deleted task is still present");
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

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        tasksPage.forceGoToTasks();

        tasksPage.openTaskForViewing(taskTitle);

        Assertions.assertTrue(tasksPage.isTextPresentOnViewPage(taskTitle), "the task " + taskTitle + " is not displayed");

        Assertions.assertTrue(tasksPage.isTextPresentOnViewPage(taskTitle), "the task description " + taskDesc + " is not displayed");
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

        Assertions.assertTrue(usersPage.isTextPresentOnViewPage(testFirstName), "Username is not displayed");

        Assertions.assertTrue(usersPage.isTextPresentOnViewPage(testEmail), "Email is not displayed");

        Assertions.assertTrue(usersPage.isTextPresentOnViewPage(testLastName), "Last is not displayed");

        usersPage.clickUpperEditButton();

        Assertions.assertFalse(driver.getCurrentUrl().contains("/show"), "Show page is still displayed");
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

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }

        statusesPage.forceGoToStatuses();

        statusesPage.clickEditStatus(statusName);

        statusesPage.clickUpperShowButton();

        Assertions.assertTrue(statusesPage.isTextPresentOnViewPage(statusName), "No status name on Show page");

        statusesPage.clickUpperEditButton();

        Assertions.assertFalse(driver.getCurrentUrl().contains("/edit"));
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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        labelsPage.forceGoToLabels();

        labelsPage.clickEditLabel(labelName);

        labelsPage.clickUpperShowButton();

        Assertions.assertTrue(labelsPage.isTextPresentOnViewPage(labelName), "Labelname is not displayed on Show page");

        labelsPage.clickUpperEditButton();

        Assertions.assertFalse(driver.getCurrentUrl().contains("/edit"));




    }
}
