package hexlet.code;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

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
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            //driver.quit();
        }
    }

    @Test
    public void testSuccessfulLogin() {
        driver.get(baseurl);
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        assertTrue(kanbanPage.isWelcomeTitleDisplayed(), "The page is not loaded");
    }

    @Test
    public void testSuccessfulLogout() {
        driver.get(baseurl);
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        assertTrue(kanbanPage.isWelcomeTitleDisplayed(), "The page is not loaded");

        kanbanPage.clickLogout();

        assertTrue(loginPage.isUsernameFieldDisplayed(), "No login page after logout");
    }

    @Test
    public void testCreateNewUser() {
        String testEmail = "test@mail.com";
        String testFirstName = "test";
        String testLastName = "user";


        driver.get(baseurl);
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToUsers();

        UsersPage usersPage = new UsersPage((driver));
        usersPage.clickCreateUser();

        assertTrue(usersPage.isUserFormDisplayed(), "User form hasn't opened");

        usersPage.fillAndSubmitUserForm(testEmail, testFirstName, testLastName);

        kanbanPage.goToUsers();

        assertTrue(usersPage.isUserInList(testFirstName), "Created user " + testFirstName + "not found");
    }

    @Test
    public void testUserListLoadingAndFields() {

        driver.get(baseurl);
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
        driver.get(baseurl);
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

        assertTrue(usersPage.isUserInList(originalFirstName), "User for edit has not been created");

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

        assertTrue(usersPage.isUserInList(newFirstName));
        assertFalse(usersPage.isUserInList(originalFirstName), "old userName is still on the list");

    }

    @Test
    public void testDeleteUser() {
        driver.get(baseurl);
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

        assertTrue(usersPage.isUserInList(userToDeleteFirstName), "User for edit has not been created");

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

        assertFalse(usersPage.isUserInList(userToDeleteFirstName), "Error: the user to delete is still there");
    }

    @Test
    public void testDeleteAllUsers() {
        driver.get(baseurl);
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
        driver.get(baseurl);
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

        assertTrue(statusesPage.isStatusInList(name), "User for edit has not been created");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testDefaultStatusesArePresent() {
        driver.get(baseurl);
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

        driver.get(baseurl);
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        StatusesPage statusesPage = new StatusesPage(driver);

        statusesPage.forceGoToStatuses();

        assertTrue(statusesPage.areHeaderDisplayed(), "Name and Slug headers are missing");

        assertTrue(statusesPage.isStatusRowCorrect("Draft", "draft"), "improper order");

        int rowsCount = statusesPage.getRowsCount();
        assertTrue(rowsCount > 0, "Labels page is empty or data are not loaded");
    }

    @Test
    public void testEditStatus() {
        driver.get(baseurl);
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

        driver.get(baseurl);
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
        driver.get(baseurl);
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
        driver.get(baseurl);
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

        driver.get(baseurl);
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
        driver.get(baseurl);
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

        driver.get(baseurl);
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


        driver.get(baseurl);
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(driver);
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((driver));
        tasksPage.clickCreateTask();

        assertTrue(tasksPage.isTaskFormDisplayed(), "Task form hasn't opened");

        String uniqueId = String.valueOf(System.currentTimeMillis());
        String taskTitle = "SomeTask_" + uniqueId;

        String targetColumn = "Draft";
        String assignee = "michael@example.com";

        tasksPage.fillAndSubmitTaskForm("test task", "8", "1");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        tasksPage.forceGoToTasks();

        assertTrue(tasksPage.isTaskInColumn(taskTitle, targetColumn),
                "task '" + taskTitle + "' is not found in column '" + targetColumn + "'");
    }
}


