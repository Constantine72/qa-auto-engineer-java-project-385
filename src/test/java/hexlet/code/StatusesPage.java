package hexlet.code;


import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.security.PublicKey;
import java.time.Duration;


public class StatusesPage extends BasePage {

//    private WebDriver driver;
//    private WebDriverWait wait;

    private final By createStatusButton = By.xpath("//*[contains(text(), 'Create')]");
    private final By nameField = By.cssSelector("input[name='name']");
    private final By slugField = By.cssSelector("input[name='slug']");
    private final By saveButton = By.xpath("//*[contains(text(), 'Save')]");
    private final By statusesListContainer = By.className("list-page");
    private final By nameHeader = By.xpath("//*[contains(text(), 'Name')]");
    private final By slugHeader = By.xpath("//*[contains(text(), 'Slug')]");

//    private final By tableRows = By.cssSelector("MuiTableBody-root datagrid-body RaDatagrid-tbody");

    private final By tableRows = By.cssSelector("table tbody tr");

    private final By nameInput = By.name("//*[contains(text(), 'Name')]");
    private final By deleteButton = By.xpath("//*[contains(text(), 'Delete')]");
    private final By selectAllCheckbox = By.className("PrivateSwitchBase-input");
    private final By noTaskStatusesYetMessage = By.xpath("//*[contains(text(), 'No Task statuses yet.");
    private final By doYouWantToAddOneMessage = By.xpath("//*[contains(text(), 'Do you want to add one?'");
    private final By deleteAllStatusesButton = By.xpath("//*[contains(text(), 'Delete')]");


    public StatusesPage(WebDriver driver) {

        super(driver);
    }


    public void clickCreateStatus() {
        wait.until(ExpectedConditions.elementToBeClickable(createStatusButton)).click();
    }

    public boolean isUserFormDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void fillAndSubmitStatusForm(String name, String slug) {

        wait.until(ExpectedConditions.elementToBeClickable(nameField)).sendKeys(name);
        wait.until(ExpectedConditions.elementToBeClickable(slugField)).sendKeys(slug);

        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isStatusInList(String expectedStatus) {
        try {
            By statusesListContainer = By.className("list-page");
            wait.until(ExpectedConditions.visibilityOfElementLocated(statusesListContainer));

            By statusesCard = By.xpath("//*[contains(text(), '" + expectedStatus + "')]");

            wait.until(ExpectedConditions.visibilityOf(driver.findElement(statusesCard)));

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void forceGoToStatuses() {
        By usersIcon = By.xpath("(//*[@data-testid='ViewListIcon'])[4]");

        wait.until(ExpectedConditions.elementToBeClickable(usersIcon)).click();
    }

    public boolean areHeaderDisplayed() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(nameHeader));
            wait.until(ExpectedConditions.presenceOfElementLocated(slugHeader));
            return true;

        } catch (Exception e) {
            return false;

        }
    }

    public boolean isStatusRowCorrect(String expectedName, String expectedSlug) {
        String complexRowXPath = "//tr[contains(., '" + expectedName + "') and contains(., '" + expectedSlug + "')]";

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(complexRowXPath)));

            return true;

        } catch (Exception e) {
            return false;

        }
    }

    public int getRowsCount() {
        try {
            wait.until(webDriver -> webDriver.findElements(tableRows).size() > 0);
            return driver.findElements(tableRows).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickEditStatus(String statusName) {
        String rowXPath = "//*[contains(text(), '" + statusName + "')]/ancestor::tr";
        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(rowXPath)));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", row);
    }

    public void fillAndSubmitEditForm(String newName, String newSlug) {
        clearAndType(nameField, newName);
        clearAndType(slugField, newSlug);

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    private void clearAndType(By fieldLocator, String text) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(fieldLocator));

        input.click();


        new Actions(driver)
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
                .sendKeys(Keys.BACK_SPACE)
                .sendKeys(text)
                .perform();
    }

    public void clickDeleteButton() {
        wait.until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
    }

    public void clickDeleteAllUsersButton() {
        wait.until(ExpectedConditions.elementToBeClickable(deleteAllStatusesButton)).click();


    }

    public void clickSelectAllUsersButton() {
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(selectAllCheckbox));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", checkbox);
    }

    public boolean isEmptyStateDisplayed() {

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < 5000) {
            String pageSource = driver.getPageSource();

            if (pageSource.contains("No Task statuses yet.") && pageSource.contains("Do you want to add one?")) {
                return true;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
        }
        return false;
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

    public void clickUpperShowButton() {
        By showButtonLocator = By.xpath("//a[contains(@href, '/show')]");
        WebElement showButton = wait.until(ExpectedConditions.elementToBeClickable(showButtonLocator));
        showButton.click();
    }

    public void clickUpperEditButton() {
        By editButtonLocator = By.xpath("//a[contains(@class, 'MuiButton-root') and (contains(text(), 'Edit'))]");
        WebElement showButton = wait.until(ExpectedConditions.elementToBeClickable(editButtonLocator));
        showButton.click();
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

    public void fillNameField(String name) {
        By nameLocator = By.name("name");
        WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(nameLocator));
        nameInput.sendKeys(name);
    }

    public void fillSlugField(String slug) {
        By slugLocator = By.name("slug");
        WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(slugLocator));
        nameInput.sendKeys(slug);
    }

    public void clearNameField() {
        By nameLocator = By.name("name");
        WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(nameLocator));
        nameInput.click();
        nameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        nameInput.sendKeys(Keys.BACK_SPACE);
    }

    public void clickSaveButton() {

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void clearSlugField() {
        By slugLocator = By.name("slug");
        WebElement slugInput = wait.until(ExpectedConditions.elementToBeClickable(slugLocator));
        slugInput.click();
        slugInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        slugInput.sendKeys(Keys.BACK_SPACE);
    }
}
