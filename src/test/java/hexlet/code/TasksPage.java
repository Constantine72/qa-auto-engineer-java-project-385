package hexlet.code;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.json.JsonOutput;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.security.PublicKey;
import java.time.Duration;

public class TasksPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private final By createTaskButton = By.xpath("//*[contains(text(), 'Create')]");
    //private final By assigneeDropdown = By.xpath("//div[.//span[contains(text(), 'Assignee')]]//*[@role='combobox']");
    private final By titleInput = By.name("title");
    private final By statusDropdown = By.partialLinkText("Statu");
    private final By saveButton = By.xpath("//*[contains(text(), 'Save')]");

    public TasksPage(WebDriver driver) {

        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateTask() {
        wait.until(ExpectedConditions.elementToBeClickable(createTaskButton)).click();
    }

    public boolean isTaskFormDisplayed() {
        try {
            //wait.until(ExpectedConditions.visibilityOfElementLocated(assigneeDropdown));
            wait.until(ExpectedConditions.presenceOfElementLocated(titleInput));
           wait.until(ExpectedConditions.visibilityOfElementLocated(statusDropdown));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void fillAndSubmitTaskForm(String title, String statusValue, String assigneeValue) {
        wait.until(ExpectedConditions.elementToBeClickable(titleInput)).sendKeys(title);

       // selectDropdownOption(assigneeDropdown, assigneeValue);
       selectDropdownOption(statusDropdown, statusValue);

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public boolean isTaskInColumn(String taskTitle, String columnName) {
        String taskInColumnXPath = "//div[contains(@class, 'column') and contains(., '" +
                columnName + "')]//*[contains(text(), '" + taskTitle + "')]";
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



        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        String optionXPath = "//*[@role='option' and @data-value='" + dataValue + "']";

        org.openqa.selenium.WebElement option = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(optionXPath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);

    }
    public void forceGoToTasks() {
        By usersIcon = By.xpath("(//*[@data-testid='ViewListIcon'])[3]");

        wait.until(ExpectedConditions.elementToBeClickable(usersIcon)).click();
    }
}
