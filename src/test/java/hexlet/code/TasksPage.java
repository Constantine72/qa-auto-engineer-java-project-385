package hexlet.code;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TasksPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private final By createTaskButton = By.xpath("//*[contains(text(), 'Create')]");
    private final By assigneeDropdown = By.xpath("//div[.//span[contains(text(), 'Assignee')]]//*[@role='combobox']");
    private final By titleInput = By.name("title");
    private final By titleInEditForm = By.xpath("//input[@name='title']");
    private final By statusDropdown = By.cssSelector("[class*='status_id'] div");
    private final By saveButton = By.xpath("//*[contains(text(), 'Save')]");
    private final By filterStatusDropdown = By.xpath("//div[@data-source='status_id']");
    private final By addFilterButton = By.xpath("//*[contains(text(), 'Add filter')]");
    private final By removeAllFiltersOption = By.xpath("//*[contains(text(), 'Remove all filters')]");
    private final By assigneeStatusDropdown = By.xpath("//div[@data-source='assignee_id']");
    private final By labelStatusDropdown = By.xpath("//div[@data-source='label_id']");
    private final By formStatusDropdown = By.cssSelector("[class*='status_id'] div");
    private final By deleteButton = By.xpath("//*[contains(text(), 'Delete')]");
    private final By contentInput = By.name("content");

    public TasksPage(WebDriver driver) {

        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void clickCreateTask() {
        wait.until(ExpectedConditions.elementToBeClickable(createTaskButton)).click();
    }

    public boolean isTaskFormDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(assigneeDropdown));
            wait.until(ExpectedConditions.presenceOfElementLocated(titleInput));
            wait.until(ExpectedConditions.visibilityOfElementLocated(statusDropdown));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void fillAndSubmitTaskForm(String title, String statusValue, String assigneeValue) {

        wait.until(ExpectedConditions.elementToBeClickable(titleInput)).sendKeys(title);

        selectDropdownOption(assigneeDropdown, assigneeValue);
        selectDropdownOption(statusDropdown, statusValue);


        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public boolean isTaskInColumn(String taskTitle, String columnName) {
        String taskInColumnXPath = String.format("//div[contains(@class, 'MuiBox-root') and contains(., '%s')]//div[contains(., '%s')]",
                columnName, taskTitle);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(taskInColumnXPath)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void selectDropdownOption(By dropdownLocator, String dataValue) {


        org.openqa.selenium.WebElement combobox = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
        combobox.click();


        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }

        String optionXPath = "//*[@role='option' and @data-value='" + dataValue + "']";

        org.openqa.selenium.WebElement option = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(optionXPath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);

    }

    public void forceGoToTasks() {
        By usersIcon = By.xpath("(//*[@data-testid='ViewListIcon'])[1]");

        wait.until(ExpectedConditions.elementToBeClickable(usersIcon)).click();
    }

    private final By taskCards = By.cssSelector(".RaList-content .MuiCard-root");

    public int getVisibleTasksCount() {
        return driver.findElements(taskCards).size();
    }

    public void applyStatusFilter(String statusName) {
        selectDropdownOption(filterStatusDropdown, statusName);
    }

    public void waitForTasksUpdate(int initialCount) {


        wait.until(d -> getVisibleTasksCount() != initialCount);
    }

    public void clearAllFilters() {
        driver.findElement(addFilterButton).click();

        WebElement removeBtn = wait.until(ExpectedConditions.elementToBeClickable(removeAllFiltersOption));
        removeBtn.click();
    }

    public void applyAssigneeFilter(String assigneeName) {
        selectDropdownOption(assigneeStatusDropdown, assigneeName);
    }

    public void applyLabelFilter(String labelName) {
        selectDropdownOption(labelStatusDropdown, labelName);
    }

    public void openTaskForEditing(String taskName) {
        String xpath = String.format("//*[contains(text(), '%s')]/ancestor::div[contains(@class, 'MuiCard-root')][1]//*[contains(text(), 'Edit')]", taskName);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    public void updateTaskName(String newName) {

        WebElement input = driver.findElement(titleInEditForm);

//        input.click();
//        input.sendKeys(Keys.CONTROL + "a");
//        input.sendKeys(Keys.BACK_SPACE);
//        input.sendKeys(newName);

        Actions actions = new Actions(driver);
        actions.click(input)
        .keyDown(Keys.CONTROL)
                .sendKeys("a")
                        .keyUp(Keys.CONTROL)
                                .sendKeys(Keys.BACK_SPACE)
                                        .sendKeys(newName)
                                                .perform();


        driver.findElement(saveButton).click();
    }

    public void changeTaskStatus(String statusId) {
        selectDropdownOption(formStatusDropdown, statusId);

        driver.findElement(By.xpath("//*[contains(text(), 'Save')]")).click();
    }

    public void clickDelete() {

        wait.until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
    }

    public boolean isAssigneeCorrectInDetails(String assigneeName) {
        try {
            String assigneeXPath = String.format("//*[contains(., '%s')]", assigneeName);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(assigneeXPath)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isColumnCorrectInDetails(String columnName) {
        try {
            String assigneeXPath = String.format("//*[contains(., '%s')]", columnName);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(assigneeXPath)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTaskCorrectInDetails(String taskTitle) {
        try {
            String assigneeXPath = String.format("//*[contains(., '%s')]", taskTitle);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(assigneeXPath)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getDescriptionInputValue() {
        return driver.findElement(By.name("content")).getAttribute("value");
    }

    public String getAssigneeDropdownValue() {
        return driver.findElement(assigneeDropdown).getText();
    }

    public void openTaskForViewing(String taskName) {

        String xpath = String.format("//*[contains(text(), '%s')]/ancestor::div[contains(@class, 'MuiCard-root')][1]//*[contains(text(), 'Show')]", taskName);

        By showIconLocator = By.xpath(xpath);

        WebElement showButton = wait.until(ExpectedConditions.elementToBeClickable(showIconLocator));

        showButton.click();
    }

    public boolean isTextPresentOnViewPage(String expectedText) {
        By textLocator = By.xpath("//span[contains(@class, 'MuiTypography-body2') and text()='" + expectedText + "']");

        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(textLocator));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;

        }
    }

    public void fillAndSubmitTaskForm(String title, String statusValue, String assigneeValue, String description) {


        wait.until(ExpectedConditions.elementToBeClickable(titleInput)).sendKeys(title);

        wait.until(ExpectedConditions.elementToBeClickable(contentInput)).sendKeys(description);

        selectDropdownOption(assigneeDropdown, assigneeValue);
        selectDropdownOption(statusDropdown, statusValue);


        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void selectAssignee(String assigneeValue) {

        selectDropdownOption(assigneeDropdown, assigneeValue);

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }
    public void selectStatus(String statusValue) {

        selectDropdownOption(statusDropdown, statusValue);

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }
    public void fillTaskTitle(String title) {

        wait.until(ExpectedConditions.elementToBeClickable(titleInput)).sendKeys(title);


        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }
    public void clickSaveButton() {

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }
    public boolean isRequiredErrorDisplayed() {
        By errorLocator = By.xpath("//*[contains(text(), 'Required')]");

        try {
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator));
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}




