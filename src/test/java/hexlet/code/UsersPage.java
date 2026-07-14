package hexlet.code;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.security.PublicKey;
import java.time.Duration;

public class UsersPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By createUserButton = By.xpath("//*[contains(text(), 'Create')]");

    private final By emailField = By.cssSelector("input[name='email']");
    private final By firstNameField = By.cssSelector("input[name='firstName']");
    private final By lastNameField = By.cssSelector("input[name='lastName']");
    private final By saveButton = By.xpath("//*[contains(text(), 'Save')]");
    private final By deleteButton = By.xpath("//*[contains(text(), 'Delete')]");
    private final By deleteAllUsersButton = By.xpath("//*[contains(text(), 'Delete')]");
    private final By showButton = By.xpath("//*[contains(text(), 'SHOW')]");
    private final By userCreateForm = By.className("create-page");
    private final By userListContainer = By.className("list-page");
    private final By userRow = By.className("MuiTableRow-root");
    private final By emailErrorMessage = By.xpath("//*[contains(text(), 'Incorrect email format')]");
    private final By selectAllCheckbox = By.className("PrivateSwitchBase-input");
    private final By noUsersYetMessage = By.xpath("//*[contains(text(), 'No Users yet.'");
    private final By doYouWantToAddOneMessage = By.xpath("//*[contains(text(), 'Do you want to add one?'");

    public UsersPage(WebDriver driver) {

        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateUser() {
        wait.until(ExpectedConditions.elementToBeClickable(createUserButton)).click();
    }


    public boolean isUserFormDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField));
            wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameField));
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
            wait.until(ExpectedConditions.visibilityOfElementLocated(saveButton));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void fillAndSubmitUserForm(String email, String firstName, String lastName) {

        clearAndType(emailField, email);
        clearAndType(firstNameField, firstName);
        clearAndType(lastNameField, lastName);

        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isUserInList(String expectedFirstName, String expectedLastName, String expectedEmail) {
        try {
            By userListContainer = By.className("list-page");
            wait.until(ExpectedConditions.visibilityOfElementLocated(userListContainer));



           // By userCard = By.xpath("//*[contains(text(), '" + expectedFirstName + "')]");

            //creating a mix of firstname, lastname and email
            String xpathQuery = String.format(
                    "//*[contains(., '%s') and contains(., '%s')]", expectedFirstName, expectedLastName, expectedEmail);
            By userCard = By.xpath(xpathQuery);



            wait.until(ExpectedConditions.visibilityOf(driver.findElement(userCard)));

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUserTableLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(userListContainer));

            wait.until(ExpectedConditions.presenceOfElementLocated(userRow));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areKeyFieldsDisplayed() {
        try {
            String tableText = wait.until(ExpectedConditions.visibilityOfElementLocated(userListContainer))
                    .getText();

            boolean hasId = tableText.contains("Id");
            boolean hasEmail = tableText.contains("Email");
            boolean hasFirstName = tableText.contains("First name");
            boolean hasLastName = tableText.contains("Last name");
            boolean hasCreatedAt = tableText.contains("Created at");

            return hasId && hasEmail && hasFirstName && hasLastName && hasCreatedAt;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickEditUser(String expectedName) {
        By userRowToEdit = By.xpath("//*[contains(text(), '" + expectedName + "')]");

        wait.until(ExpectedConditions.elementToBeClickable(userRowToEdit)).click();
    }

    public boolean isEmailValidationErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(emailErrorMessage));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getFirstNameValue() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField)).getAttribute("value");
    }

    public String getEmailValue() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(emailField)).getAttribute("value");
    }

    public void fillEmailOnly(String email) {
        clearAndType(emailField, email);
    }

    public void clickSaveButton() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    public void forceGoToUsers() {
        By usersIcon = By.xpath("(//*[@data-testid='ViewListIcon'])[2]");

        wait.until(ExpectedConditions.elementToBeClickable(usersIcon)).click();
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
        wait.until(ExpectedConditions.elementToBeClickable(deleteAllUsersButton)).click();


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

            if (pageSource.contains("No Users yet.") && pageSource.contains("Do you want to add one?")) {
                return true;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
        }
        return false;
    }
    public boolean isStatusPresent(String statusName) {
        String rowXPath = "//*[contains(text(), '" + statusName + "')]";

        try {
            WebDriverWait shortWait =  new WebDriverWait(driver, Duration.ofSeconds(2));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(rowXPath)));
            return true;
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
    public boolean isTextPresentOnViewPage(String expectedText) {
        By textLocator = By.xpath("//span[contains(@class, 'MuiTypography-body2') and text()='" + expectedText + "']");
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(textLocator));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}


