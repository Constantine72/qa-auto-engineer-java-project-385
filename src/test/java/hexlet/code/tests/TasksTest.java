package hexlet.code.tests;

import hexlet.code.pages.KanbanPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.TasksPage;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;
import java.util.List;

class TasksTest extends BaseTest {

    private static final int MAX_CARDS_NUMBER = 15;
    private static final int JOHN_CARDS_COUNT = 5;
    private static final int NUMBER_OF_CARDS_TO_REVIEW = 5;
    private static final int NUMBER_OF_DRAFT_CARDS = 3;

    @Test
    public void testCreateNewTask() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((getDriver()));
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
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((getDriver()));

        filterByStatus(tasksPage);

        filterByAssignee(tasksPage);

        filterByLabel(tasksPage);

        filterByAssigneeWithNoCards(tasksPage);

        filterByAssigneeAndStatus(tasksPage);

        changeAssignee(tasksPage);

        removeAllFilters(tasksPage);

        tasksPage.waitForCardsToLoad();
        String targetWorker12 = "alice@hotmail.com";

        tasksPage.filterByAssignee(targetWorker12);

        assertDoesNotThrow(
                () -> tasksPage.waitForCardsCount(2),
                "filter hasn't been applied"
        );

        tasksPage.waitForCardsToLoad();

        String myFilterName = "Alice_Custom_Filter";
        tasksPage.openSaveQueryModal();
        tasksPage.saveCurrentQueryAs(myFilterName);

        tasksPage.waitForCardsToLoad();

        tasksPage.clearAllFilters();

        tasksPage.waitForCardsCount(MAX_CARDS_NUMBER);

        tasksPage.applySavedQuery(myFilterName);

        tasksPage.waitForCardsCount(2);

        List<String> savedQueryCards = tasksPage.getVisibleStatusesInTable();
        assertEquals(2, savedQueryCards.size(), "number of cards is not 2");
        assertTrue(savedQueryCards.stream().allMatch(c -> c.contains("Task 8") || c.contains("Task 9")),
                "only Alice tasks should be displayed");

        deleteCustomQuery(tasksPage, myFilterName);

        aliceWithZeroTasks(tasksPage);

        tasksPage.waitForCardsToLoad();

        String targetWorker14 = "alice@hotmail.com";
        String targetStatus14 = "To Be Fixed";
        String targetLabel14 = " feature";

        tasksPage.filterByAssignee(targetWorker14);
        tasksPage.filterByStatus(targetStatus14);
        tasksPage.filterByLabel(targetLabel14);

        assertDoesNotThrow(
                () -> tasksPage.waitForCardsCount(1),
                "filter hasn't been applied"
        );

        List<String> visibleCardAlice = tasksPage.getVisibleStatusesInTable();
        assertEquals(1, visibleCardAlice.size(), "number of cards is not 1");
        assertTrue(visibleCardAlice.get(0).contains("Task 8"), "an improper task is returned");
    }

    public void aliceWithZeroTasks(TasksPage tasksPage) {
        String urlCombo13 = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();

        String targetWorker13 = "alice@hotmail.com";

        tasksPage.filterByAssignee(targetWorker13);

        tasksPage.waitForUrlToChange(urlCombo13);

        tasksPage.waitForCardsCount(2);

        tasksPage.waitForCardsToLoad();

        tasksPage.filterByStatus("Draft");

        tasksPage.waitForCardsCount(0);

        List<String> visibleCards = tasksPage.getVisibleStatusesInTable();

        assertTrue(visibleCards.isEmpty(), "The table should be empty");

        tasksPage.clearAllFilters();
    }

    public static void deleteCustomQuery(TasksPage tasksPage, String myFilterName) {
        tasksPage.deleteSavedQuery(myFilterName);
        boolean isFilterStillThere = tasksPage.isSavedQueryPresent(myFilterName);
        assertFalse(isFilterStillThere, myFilterName + " has not been deleted");

        tasksPage.clearAllFilters();
    }

    public void removeAllFilters(TasksPage tasksPage) {
        String urlBeforeClear = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();

        String targetWorker7 = "alice@hotmail.com";

        tasksPage.filterByAssignee(targetWorker7);

        tasksPage.waitForUrlToChange(urlBeforeClear);

        tasksPage.waitForCardsCount(2);

        tasksPage.clearAllFilters();

        tasksPage.waitForCardsCount(MAX_CARDS_NUMBER);

        List<String> allCards = tasksPage.getVisibleStatusesInTable();
        assertTrue(allCards.stream().anyMatch(c -> c.contains("Task 1")), "Task 1 is not shown");
        assertTrue(allCards.stream().anyMatch(c -> c.contains("Task 15")), "Task 1 is not shown");

        String urlCombo11 = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();
        String targetWorker11 = "alice@hotmail.com";

        tasksPage.filterByAssignee(targetWorker11);

        tasksPage.waitForUrlToChange(urlCombo11);

        tasksPage.waitForCardsCount(2);

        tasksPage.waitForCardsToLoad();

        tasksPage.filterByStatus("To Be Fixed");

        tasksPage.waitForCardsCount(1);

        tasksPage.removeStatusFilter();

        tasksPage.waitForCardsCount(2);

        List<String> aliceCardsAfterRemoval = tasksPage.getVisibleStatusesInTable();
        assertEquals(2, aliceCardsAfterRemoval.size(), "2 task2 should be on board");
        assertTrue(aliceCardsAfterRemoval.stream().allMatch(c -> c.contains("Task 8") || c.contains("Task 9")),
                "only Alice tasks should be displayed");

        tasksPage.clearAllFilters();
    }

    public void changeAssignee(TasksPage tasksPage) {
        String urlAlice = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();
        String targetWorker4 = "alice@hotmail.com";

        tasksPage.filterByAssignee(targetWorker4);

        tasksPage.waitForUrlToChange(urlAlice);

        tasksPage.waitForCardsCount(2);

        tasksPage.waitForCardsToLoad();

        String targetWorker5 = "john@google.com";
        tasksPage.filterByAssignee(targetWorker5);

        tasksPage.waitForCardWithTitle("Task 15");

        tasksPage.waitForCardsCount(JOHN_CARDS_COUNT);

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
    }

    public void filterByAssigneeAndStatus(TasksPage tasksPage) {
        String urlCombo15 = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();
        String targetWorker6 = "alice@hotmail.com";

        tasksPage.filterByAssignee(targetWorker6);

        tasksPage.waitForUrlToChange(urlCombo15);

        tasksPage.waitForCardsCount(2);

        tasksPage.waitForCardsToLoad();

        tasksPage.filterByStatus("To Be Fixed");

        tasksPage.waitForCardsCount(1);

        List<String> comboCardsNew = tasksPage.getVisibleStatusesInTable();
        assertEquals(1, comboCardsNew.size(), "1 task should be on board");
        assertTrue(comboCardsNew.get(0).contains("Task 8"), "combo hasn't returned task 8");

        tasksPage.clearAllFilters();
    }

    public  void filterByAssigneeWithNoCards(TasksPage tasksPage) {
        String urlBeforeAssignee2 = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();

        String targetWorker2 = "emily@example.com";

        tasksPage.filterByAssignee(targetWorker2);

        tasksPage.waitForUrlToChange(urlBeforeAssignee2);

        tasksPage.waitForCardsCount(0);

        List<String> emptyBoardCards = tasksPage.getVisibleStatusesInTable();
        assertTrue(emptyBoardCards.isEmpty(), "table should be empty");

        tasksPage.clearAllFilters();
    }

    public void filterByLabel(TasksPage tasksPage) {
        String urlBeforeLabel = tasksPage.getCurrentUrl();
        String targetLabel = "bug";

        tasksPage.waitForCardsToLoad();

        tasksPage.filterByLabel(targetLabel);

        tasksPage.waitForUrlToChange(urlBeforeLabel);

        tasksPage.waitForCardsCount(2);

        List<String> labelFilteredCards = tasksPage.getVisibleStatusesInTable();

        assertFalse(labelFilteredCards.isEmpty(), "error: bug filter is empty");

        assertEquals(2, labelFilteredCards.size(), "improper number of cards");

        assertTrue(labelFilteredCards.stream().anyMatch(c -> c.contains("Task 7")), "no task 7");
        assertTrue(labelFilteredCards.stream().anyMatch(c -> c.contains("Task 3")), "no task 3");

        boolean onlyBugTasks = labelFilteredCards.stream()
                .allMatch(c -> c.contains("Task 7") || c.contains("Task 3"));
        assertTrue(onlyBugTasks, "error: an improper task is shown");

        tasksPage.clearAllFilters();
    }

    public void filterByAssignee(TasksPage tasksPage) {
        String urlBeforeAssignee = tasksPage.getCurrentUrl();
        tasksPage.waitForCardsToLoad();

        String targetWorker = "alice@hotmail.com";

        tasksPage.waitForCardsToLoad();

        tasksPage.rememberOldCard();
        tasksPage.filterByAssignee(targetWorker);
        tasksPage.waitForOldCardToDisappear();

        tasksPage.waitForUrlToChange(urlBeforeAssignee);

        tasksPage.waitForCardsCount(2);

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
    }

    public void filterByStatus(TasksPage tasksPage) {
        int initialCardsCount = tasksPage.getTaskCardsCount();

        assertTrue(initialCardsCount > 0, "the table is blank");

        String urlBeforeFilter = tasksPage.getCurrentUrl();

        tasksPage.waitForCardsToLoad();

        String targetStatus = "Draft";
        tasksPage.filterByStatus(targetStatus);
        tasksPage.waitForUrlToBe(urlBeforeFilter);

        tasksPage.waitForCardsCount(NUMBER_OF_DRAFT_CARDS);

        List<String> statusFilteredCards = tasksPage.getVisibleStatusesInTable();

        assertFalse(statusFilteredCards.isEmpty(), "the table is empty");

        assertEquals(NUMBER_OF_DRAFT_CARDS, statusFilteredCards.size(), "error: incorrect numbers of cards");

        assertTrue(statusFilteredCards.stream().anyMatch(c -> c.contains("Task 11")), "no task 11");
        assertTrue(statusFilteredCards.stream().anyMatch(c -> c.contains("Task 5")), "no task 5");
        assertTrue(statusFilteredCards.stream().anyMatch(c -> c.contains("Task 6")), "no task 6");

        boolean onlyDraftTasks = statusFilteredCards.stream()
                .allMatch(c -> c.contains("Task 11") || c.contains("Task 5") || c.contains("Task 6"));
        assertTrue(onlyDraftTasks, "improper tasks are displayed");

        tasksPage.clearAllFilters();
    }

    @Test
    public void testEditTask() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((getDriver()));

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
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((getDriver()));

        String taskToMove = "Task 11";
        String newStatusId = "2";

        tasksPage.openTaskForEditing(taskToMove);

        tasksPage.changeTaskStatus(newStatusId);

        tasksPage.waitForTasksUpdate(NUMBER_OF_CARDS_TO_REVIEW);

        tasksPage.filterByStatus(newStatusId);

        tasksPage.waitForTasksUpdate(NUMBER_OF_CARDS_TO_REVIEW);

        boolean isCardMoved = tasksPage.isTaskVisible(taskToMove);

        Assertions.assertTrue(isCardMoved, "Card " + taskToMove + "' is not visible in the new status column");

        tasksPage.clearAllFilters();
    }

    @Test
    public  void testDeleteTask() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((getDriver()));

        String taskToDelete = "Task 5";

        int initialCount = tasksPage.getVisibleTasksCount();

        System.out.println(initialCount);

        tasksPage.openTaskForEditing(taskToDelete);

        tasksPage.clickDelete();

        int currentTasksCount = tasksPage.getVisibleTasksCount();

        assertEquals(initialCount - 1, currentTasksCount, "The number of tasks hasn't changed");

        assertTrue(tasksPage.isTaskGone(taskToDelete), "Error: a deleted task is still present");
    }

    @Test
    public void testShowTask() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((getDriver()));

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
    public  void testCreateTaskValidation() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String taskTitle = "SomeTask_" + uniqueId;
        String taskStatus = "2";
        String assigneeName = "1";

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((getDriver()));

        tasksPage.waitForListToLoad();

        int initialTasksCount = tasksPage.getInitialTasksCount();

        tasksPage.clickCreateTask();

        tasksPage.selectAssignee(assigneeName);
        tasksPage.selectStatus(taskStatus);
        tasksPage.clickSaveButtonForTasks();

        assertTrue(tasksPage.getCurrentUrl().contains("/create"), "Empty task has been created");

        assertTrue(tasksPage.isRequiredErrorDisplayed(), "Required is not displayed");

        tasksPage.refreshPage();

        try {
            tasksPage.fillTaskTitle(taskTitle);
        } catch (TimeoutException e) {
            fail("the form is not complete: title is missing");
        }
        tasksPage.selectStatus(taskStatus);

        tasksPage.clickSaveButtonForTasks();

        assertTrue(tasksPage.getCurrentUrl().contains("/create"), "task has been created w/o assignee");

        assertTrue(tasksPage.isRequiredErrorDisplayed(), "Required is not displayed");

        tasksPage.refreshPage();

        tasksPage.fillTaskTitle(taskTitle);
        tasksPage.selectAssignee(assigneeName);
        tasksPage.clickSaveButtonForTasks();

        assertTrue(tasksPage.getCurrentUrl().contains("/create"), "task has been created w/o status");

        assertTrue(tasksPage.isRequiredErrorDisplayed(), "Required is not displayed");

        kanbanPage.goToTasks();

        int finalTasksCount = tasksPage.getFinalTasksCount();

        assertEquals(initialTasksCount, finalTasksCount, "empty status has been saved");
    }

    @Test
    public  void testEditTaskWithoutTitle() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((getDriver()));

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

        tasksPage.clickSaveButtonForTasks();

        tasksPage.waitForSnackBar();

        assertFalse(tasksPage.getCurrentUrl().endsWith("/tasks"), "task w/o title was saved");

        assertTrue(tasksPage.isRequiredErrorDisplayed(), "no required message");
    }

    @Test
    public void testTasksFilterByStatusOnGrid() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.login("admin", "admin");

        KanbanPage kanbanPage = new KanbanPage(getDriver());
        kanbanPage.goToTasks();

        TasksPage tasksPage = new TasksPage((getDriver()));

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
