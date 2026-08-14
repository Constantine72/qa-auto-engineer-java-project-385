package hexlet.code.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Duration;

public final class TasksPage extends BasePage {

    private final By createTaskButton = By.xpath("//*[contains(text(), 'Create')]");
    private final By assigneeDropdown = By.xpath(
            "//div[.//span[contains(text(), 'Assignee')]]//*[@role='combobox']");
    private final By titleInput = By.name("title");
    private final By titleInEditForm = By.xpath("//input[@name='title']");
    private final By statusDropdown = By.cssSelector("[class*='status_id'] div");
    private final By saveButton = By.xpath("//*[contains(text(), 'Save')]");
    private final By addFilterButton = By.xpath("//*[contains(text(), 'Add filter')]");
    private final By removeAllFiltersOption = By.xpath("//*[contains(text(), 'Remove all filters')]");
    private final By saveCurrentQueryButton = By.xpath("//*[contains(text(), 'Save current query...')]");
    private final By formStatusDropdown = By.cssSelector("[class*='status_id'] div");
    private final By deleteButton = By.xpath("//*[contains(text(), 'Delete')]");
    private final By contentInput = By.name("content");
    private final By cardLocator = By.cssSelector(".MuiCard-root");
    private static final int MINIMAL_SLEEP = 500;
    private static final int MIN_WAIT_TIME_SECS = 5;
    private static final int MAX_WAIT_DURATION = 5000;

    public TasksPage(WebDriver driver) {

        super(driver);
    }

    public void clickCreateTask() {
        getWait().until(ExpectedConditions.elementToBeClickable(createTaskButton)).click();
    }

    public boolean isTaskFormDisplayed() {
        try {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(assigneeDropdown));
            getWait().until(ExpectedConditions.presenceOfElementLocated(titleInput));
            getWait().until(ExpectedConditions.visibilityOfElementLocated(statusDropdown));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void fillAndSubmitTaskForm(String title, String statusValue, String assigneeValue) {

        getWait().until(ExpectedConditions.elementToBeClickable(titleInput)).sendKeys(title);

        selectDropdownOption(assigneeDropdown, assigneeValue);
        selectDropdownOption(statusDropdown, statusValue);

        WebElement btn = getWait().until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", btn);
    }

    public boolean isTaskInColumn(String taskTitle, String columnName) {
        String taskInColumnXPath = String.format("//div[contains(@class, "
                        +
                        "'MuiBox-root') and contains(., '%s')]//div[contains(., '%s')]",
                columnName, taskTitle);
        try {
            getWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath(taskInColumnXPath)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void selectDropdownOption(By dropdownLocator, String dataValue) {
        org.openqa.selenium.WebElement combobox = getWait().until(ExpectedConditions.
                elementToBeClickable(dropdownLocator));
        combobox.click();

//        try {
//            Thread.sleep(MINIMAL_SLEEP);
//        } catch (InterruptedException ignored) {
//        }

        String optionXPath = "//*[@role='option' and @data-value='" + dataValue + "']";

        org.openqa.selenium.WebElement option = getWait().until(ExpectedConditions
                .presenceOfElementLocated(By.xpath(optionXPath)));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", option);

    }

    public void forceGoToTasks() {
        By usersIcon = By.xpath("(//*[@data-testid='ViewListIcon'])[1]");

        getWait().until(ExpectedConditions.elementToBeClickable(usersIcon)).click();
    }

    private final By taskCards = By.cssSelector(".RaList-content .MuiCard-root");

    public int getVisibleTasksCount() {
        return getDriver().findElements(taskCards).size();
    }

    public void waitForTasksUpdate(int initialCount) {


        getWait().until(d -> getVisibleTasksCount() != initialCount);
    }

    public void clearAllFilters() {
        getDriver().findElement(addFilterButton).click();

        WebElement removeBtn = getWait().until(ExpectedConditions.elementToBeClickable(removeAllFiltersOption));
        removeBtn.click();
    }

    public void openTaskForEditing(String taskName) {
        String xpath = String.format("//*[contains(text(),"
                +
                " '%s')]/ancestor::div[contains(@class, 'MuiCard-root')][1]//*[contains(text(), 'Edit')]", taskName);

        getWait().until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    public void updateTaskName(String newName) {

        WebElement input = getDriver().findElement(titleInEditForm);

        Actions actions = new Actions(getDriver());
        actions.click(input)
                .keyDown(Keys.CONTROL)
                .sendKeys("a")
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.BACK_SPACE)
                .sendKeys(newName)
                .perform();
        getDriver().findElement(saveButton).click();
    }

    public void changeTaskStatus(String statusId) {
        selectDropdownOption(formStatusDropdown, statusId);

        getDriver().findElement(By.xpath("//*[contains(text(), 'Save')]")).click();
    }

    public void clickDelete() {

        getWait().until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
    }

    public boolean isAssigneeCorrectInDetails(String assigneeName) {
        try {
            String assigneeXPath = String.format("//*[contains(., '%s')]", assigneeName);
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(MIN_WAIT_TIME_SECS));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(assigneeXPath)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isColumnCorrectInDetails(String columnName) {
        try {
            String assigneeXPath = String.format("//*[contains(., '%s')]", columnName);
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(MIN_WAIT_TIME_SECS));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(assigneeXPath)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTaskCorrectInDetails(String taskTitle) {
        try {
            String assigneeXPath = String.format("//*[contains(., '%s')]", taskTitle);
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(MIN_WAIT_TIME_SECS));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(assigneeXPath)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getDescriptionInputValue() {
        return getDriver().findElement(By.name("content")).getAttribute("value");
    }

    public String getAssigneeDropdownValue() {
        return getDriver().findElement(assigneeDropdown).getText();
    }

    public void openTaskForViewing(String taskName) {

        String xpath = String.format("//*[contains(text(), '%s')]/ancestor::div[contains(@class,"
                +
                " 'MuiCard-root')][1]//*[contains(text(), 'Show')]", taskName);
        By showIconLocator = By.xpath(xpath);
        WebElement showButton = getWait().until(ExpectedConditions.elementToBeClickable(showIconLocator));
        showButton.click();
    }

    public boolean isTextPresentOnViewPage(String expectedText) {
        By textLocator = By.xpath("//span[contains(@class,"
                +
                " 'MuiTypography-body2') and text()='" + expectedText + "']");
        try {
            WebElement element = getWait().until(ExpectedConditions.visibilityOfElementLocated(textLocator));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void fillAndSubmitTaskForm(String title, String statusValue, String assigneeValue,
                                      String description) {

        getWait().until(ExpectedConditions.elementToBeClickable(titleInput)).sendKeys(title);
        getWait().until(ExpectedConditions.elementToBeClickable(contentInput)).sendKeys(description);

        selectDropdownOption(assigneeDropdown, assigneeValue);
        selectDropdownOption(statusDropdown, statusValue);

        WebElement btn = getWait().until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", btn);
    }

    public void selectAssignee(String assigneeValue) {
        selectDropdownOption(assigneeDropdown, assigneeValue);
        WebElement btn = getWait().until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", btn);
    }

    public void selectStatus(String statusValue) {
        selectDropdownOption(statusDropdown, statusValue);

        WebElement btn = getWait().until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", btn);
    }

    public void fillTaskTitle(String title) {
        getWait().until(ExpectedConditions.elementToBeClickable(titleInput)).sendKeys(title);

        WebElement btn = getWait().until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", btn);
    }

    public void clickSaveButtonForTasks() {
        WebElement btn = getWait().until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", btn);
    }

    public void clearTitleField() {
        By titleLocator = By.name("title");
        WebElement titleInputInField = getWait().until(ExpectedConditions.elementToBeClickable(titleLocator));
        titleInputInField.click();
        titleInputInField.sendKeys(Keys.END);
        titleInputInField.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        titleInputInField.sendKeys(Keys.BACK_SPACE);
    }

    public void filterByStatus(String statusName) {
        By filterDropdownLocator = By.cssSelector("[class*='status_id'] div");
        WebElement dropdown = getWait().until(ExpectedConditions.elementToBeClickable(filterDropdownLocator));
        dropdown.click();

        By menuListLocator = By.cssSelector(".MuiMenu-list, [role='listbox']");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(menuListLocator));

        By optionByText = By.xpath("//li[contains(., '" + statusName + "')]");
        By optionValue = By.xpath("//li[@data-value='" + statusName + "']");

        try {
            getWait().until(ExpectedConditions.presenceOfElementLocated(optionByText));
            getWait().until(ExpectedConditions.elementToBeClickable(optionByText)).click();
        } catch (Exception e) {
            getWait().until(ExpectedConditions.elementToBeClickable(optionValue)).click();
        }
    }

    public void filterByAssignee(String assigneeName) {
        By filterDropdownLocator = By.cssSelector("[class*='assignee_id'] div");
        WebElement dropdown = getWait().until(ExpectedConditions.elementToBeClickable(filterDropdownLocator));
        dropdown.click();

        By menuListLocator = By.cssSelector(".MuiMenu-list, [role='listbox']");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(menuListLocator));

        By optionByText = By.xpath("//li[contains(., '" + assigneeName + "')]");

        By optionValue = By.xpath("//li[@data-value='" + assigneeName + "']");

//        try {
//            Thread.sleep(MAX_WAIT_DURATION);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            getWait().until(ExpectedConditions.presenceOfElementLocated(optionByText));
            getWait().until(ExpectedConditions.elementToBeClickable(optionByText)).click();
        } catch (Exception e) {
            getWait().until(ExpectedConditions.elementToBeClickable(optionValue)).click();
        }
    }

    public void filterByLabel(String labelName) {
        By filterDropdownLocator = By.cssSelector("[class*='label_id'] div");
        WebElement dropdown = getWait().until(ExpectedConditions.elementToBeClickable(filterDropdownLocator));
        dropdown.click();

        By menuListLocator = By.cssSelector(".MuiMenu-list, [role='listbox']");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(menuListLocator));

        By optionByText = By.xpath("//li[contains(., '" + labelName + "')]");
        By optionValue = By.xpath("//li[@data-value='" + labelName + "']");

//        try {
//            Thread.sleep(MAX_WAIT_DURATION);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            getWait().until(ExpectedConditions.presenceOfElementLocated(optionByText));
            getWait().until(ExpectedConditions.elementToBeClickable(optionByText)).click();
        } catch (Exception e) {
            getWait().until(ExpectedConditions.elementToBeClickable(optionValue)).click();
        }
    }

    public List<String> getVisibleStatusesInTable() {

        try {
            return getDriver().findElements(cardLocator).stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
            return List.of();
        }
    }

    public void waitForCardsCount(int expectedCount) {
        getWait().until(ExpectedConditions.numberOfElementsToBe(cardLocator, expectedCount));
    }

    public int getTaskCardsCount() {
        try {
            getWait().until(ExpectedConditions.presenceOfElementLocated(cardLocator));
            return getDriver().findElements(cardLocator).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void removeStatusFilter() {
        By filterDropdownLocator = By.cssSelector("[class*='status_id'] div");
        WebElement dropdown = getWait().until(ExpectedConditions.elementToBeClickable(filterDropdownLocator));
        dropdown.click();

        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='listbox']")));

        WebElement emptyOption = getWait().until(ExpectedConditions.
                elementToBeClickable(By.cssSelector("li[data-value='']")));
        emptyOption.click();

        getWait().until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[role='listbox']")));
    }

    public void openSaveQueryModal() {
        getDriver().findElement(addFilterButton).click();

        getDriver().findElement(saveCurrentQueryButton).click();
    }

    public void saveCurrentQueryAs(String queryName) {
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id("form-dialog-title")));

        WebElement nameInput = getWait().until(ExpectedConditions.elementToBeClickable(By.id("name")));

        nameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        nameInput.sendKeys(Keys.BACK_SPACE);
        nameInput.sendKeys(queryName);

        WebElement saveBtn = getDriver().findElement(By.xpath("//button[contains(., 'Save')]"));
        saveBtn.click();

        getWait().until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//h2[contains(.,"
                +
                " 'Save current query as')]")));
    }

    public void applySavedQuery(String queryName) {

        getDriver().findElement(addFilterButton).click();

        WebElement savedQueryTab = getWait().until(ExpectedConditions.
                elementToBeClickable(By.xpath("//*[contains(text(), '" + queryName + "')]")));
        savedQueryTab.click();
    }

    public void deleteSavedQuery(String queryName) {
        getDriver().findElement(addFilterButton).click();

        String xpath = String.format("//li[contains(., 'Remove query') and contains(., '%s')]", queryName);
        WebElement removeOption = getWait().until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

        removeOption.click();

        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),"
                +
                " 'Remove saved query?')]")));

        WebElement confirmBtn = getWait().until(ExpectedConditions.
                elementToBeClickable(By.xpath("//button[contains(., 'Confirm')]")));
        confirmBtn.click();

        getWait().until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[contains(text(),"
                +
                " 'Remove saved query?')]")));
    }

    public boolean isSavedQueryPresent(String queryName) {
        getDriver().findElement(addFilterButton).click();

        boolean isPresent = false;
        try {
            WebDriverWait shortWait = new WebDriverWait(getDriver(), java.time.Duration.ofSeconds(2));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[contains(., '"
                    +
                    queryName + "')]")));
            isPresent = true;
        } catch (org.openqa.selenium.TimeoutException e) {
            isPresent = false;
        } finally {
            getDriver().findElement(By.tagName("body")).click();
        }
        return isPresent;
    }

    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }

    public void waitForCardsToLoad() {
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".MuiCard-root")));
    }

    public void waitForUrlToBe(String expectedUrl) {
        getWait().until(ExpectedConditions.urlToBe(expectedUrl));
    }

    public void waitForUrlToChange(String oldUrl) {
        getWait().until(ExpectedConditions.not(ExpectedConditions.urlToBe(oldUrl)));
    }

    private WebElement oldCard;

    public void rememberOldCard() {
        oldCard = getDriver().findElement(By.cssSelector(".MuiCard-root"));
    }

    public void waitForOldCardToDisappear() {
        getWait().until(ExpectedConditions.stalenessOf(oldCard));
    }

    private By getCardLocatorByTitle(String taskTitle) {
        return By.xpath("//div[contains(@class, 'RaList-content')]//*[text()='"
                +
                taskTitle + "']");
    }

    public void waitForCardWithTitle(String taskTitle) {
        By dynamicLocator = getCardLocatorByTitle(taskTitle);
        getWait().until(ExpectedConditions.presenceOfElementLocated(dynamicLocator));
    }

    public boolean isCardPresent(String taskTitle) {
        By dynamicLocator = getCardLocatorByTitle(taskTitle);
        return !getDriver().findElements(dynamicLocator).isEmpty();
    }

    public void waitForNewCardToAppear(String updatedName) {
        By newCardLocator = By.xpath("//div[contains(@class, 'RaList-content')]//*[text()='"
                +
                updatedName + "']");
        getWait().until(ExpectedConditions.presenceOfElementLocated(newCardLocator));
    }

    public boolean isNewCardDisplayed(String updatedName) {
        By newCardLocator = By.xpath("//div[contains(@class, 'RaList-content')]//*[text()='"
                +
                updatedName + "']");
        WebElement newCard = getWait().until(ExpectedConditions.visibilityOfElementLocated(newCardLocator));
        return newCard.isDisplayed();
    }

    public boolean areOldCardsEmpty(String taskTitle) {
        By oldCardLocator = By.xpath("//div[contains(@class, 'RaList-content')]//*[text()='"
                +
                taskTitle + "']");
        return getDriver().findElements(oldCardLocator).isEmpty();
    }

    public boolean isTaskGone(String taskToDelete) {
        By deleteCardLocator = By.xpath("//div[contains(@class, 'MuiCard-root')]//*[text()='"
                +
                taskToDelete + "']");
        return getDriver().findElements(deleteCardLocator).isEmpty();
    }

    public void waitForListToLoad() {
        getWait().until(ExpectedConditions.or(ExpectedConditions.
                        presenceOfElementLocated(By.cssSelector(".MuiCard-root")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".RaList-content"))));
    }

    public int getInitialTasksCount() {
        return getDriver().findElements(By.cssSelector(".MuiTableRow-root")).size();
    }

    public int getFinalTasksCount() {
        return getDriver().findElements(By.cssSelector(".MuiTableRow-root")).size();
    }

    public void waitForSnackBar() {
        getWait().until(ExpectedConditions.
                visibilityOfElementLocated(By.cssSelector(".MuiAlert-root, .MuiSnackbar-root")));
    }

    public boolean isTaskVisible(String taskTitle) {
        By taskLocator = By.xpath("//*[text()='" + taskTitle + "']");
        try {
            WebElement task = getWait().until(ExpectedConditions.visibilityOfElementLocated(taskLocator));
            return task.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }
    public void refreshPage() {
        getDriver().navigate().refresh();
    }
}




