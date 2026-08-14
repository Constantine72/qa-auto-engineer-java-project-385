package hexlet.code.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;

public final class UsersPage extends BasePage {

    private final By createUserButton = By.xpath("//*[contains(text(), 'Create')]");
    private final By emailField = By.cssSelector("input[name='email']");
    private final By firstNameField = By.cssSelector("input[name='firstName']");
    private final By lastNameField = By.cssSelector("input[name='lastName']");
    private final By saveButton = By.xpath("//*[contains(text(), 'Save')]");
    private final By deleteButton = By.xpath("//*[contains(text(), 'Delete')]");
    private final By deleteAllUsersButton = By.xpath("//*[contains(text(), 'Delete')]");
    private final By userListContainer = By.className("list-page");
    private final By userRow = By.className("MuiTableRow-root");
    private final By emailErrorMessage = By.xpath("//*[contains(text(), 'Incorrect email format')]");
    private final By selectAllCheckbox = By.className("PrivateSwitchBase-input");
    private static final int MINIMAL_SLEEP = 500;
    private static final int MAX_WAIT_DURATION = 5000;

    public UsersPage(WebDriver driver) {

        super(driver);
    }

    public void clickCreateUser() {
        getWait().until(ExpectedConditions.elementToBeClickable(createUserButton)).click();
    }

    public boolean isUserFormDisplayed() {
        try {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(firstNameField));
            getWait().until(ExpectedConditions.visibilityOfElementLocated(lastNameField));
            getWait().until(ExpectedConditions.visibilityOfElementLocated(emailField));
            getWait().until(ExpectedConditions.visibilityOfElementLocated(saveButton));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void fillAndSubmitUserForm(String email, String firstName, String lastName) {
        clearAndType(emailField, email);
        clearAndType(firstNameField, firstName);
        clearAndType(lastNameField, lastName);

        getWait().until(ExpectedConditions.elementToBeClickable(saveButton)).click();

//        try {
//            Thread.sleep(MINIMAL_SLEEP);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public boolean isUserInList(String expectedFirstName, String expectedLastName, String expectedEmail) {
        try {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(userListContainer));

            String xpathQuery = String.format(
                    "//*[contains(., '%s') and contains(., '%s')]", expectedFirstName, expectedLastName, expectedEmail);
            By userCard = By.xpath(xpathQuery);

            getWait().until(ExpectedConditions.visibilityOf(getDriver().findElement(userCard)));

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUserTableLoaded() {
        try {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(userListContainer));

            getWait().until(ExpectedConditions.presenceOfElementLocated(userRow));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areKeyFieldsDisplayed() {
        try {
            String tableText = getWait().until(ExpectedConditions.visibilityOfElementLocated(userListContainer))
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

        getWait().until(ExpectedConditions.elementToBeClickable(userRowToEdit)).click();
    }

    public boolean isEmailValidationErrorDisplayed() {
        try {
            getWait().until(ExpectedConditions.presenceOfElementLocated(emailErrorMessage));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getFirstNameValue() {
        return getWait().until(ExpectedConditions.
                visibilityOfElementLocated(firstNameField)).getAttribute("value");
    }

    public String getEmailValue() {
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(emailField)).getAttribute("value");
    }

    public void fillEmailOnly(String email) {
        clearAndType(emailField, email);
    }

    public void clickSaveButtonForUsers() {
        getWait().until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    public void forceGoToUsers() {
        By usersIcon = By.xpath("(//*[@data-testid='ViewListIcon'])[2]");

        getWait().until(ExpectedConditions.elementToBeClickable(usersIcon)).click();
    }

    private void clearAndType(By fieldLocator, String text) {
        WebElement input = getWait().until(ExpectedConditions.elementToBeClickable(fieldLocator));

        input.click();


        new Actions(getDriver())
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
                .sendKeys(Keys.BACK_SPACE)
                .sendKeys(text)
                .perform();
    }

    public void clickDeleteButton() {
        getWait().until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
    }

    public void clickDeleteAllUsersButton() {
        getWait().until(ExpectedConditions.elementToBeClickable(deleteAllUsersButton)).click();
    }

    public void clickSelectAllUsersButton() {
        WebElement checkbox = getWait().until(ExpectedConditions.presenceOfElementLocated(selectAllCheckbox));
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].click();", checkbox);
    }

    public boolean isEmptyStateDisplayed() {

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < MAX_WAIT_DURATION) {
            String pageSource = getDriver().getPageSource();

            if (pageSource.contains("No Users yet.") && pageSource.contains("Do you want to add one?")) {
                return true;
            }
//            try {
//                Thread.sleep(MINIMAL_SLEEP);
//            } catch (InterruptedException ignored) {
//            }
        }
        return false;
    }

    public void clickUpperShowButton() {
        By showButtonLocator = By.xpath("//a[contains(@href, '/show')]");
        WebElement showButton = getWait().until(ExpectedConditions.elementToBeClickable(showButtonLocator));
        showButton.click();
    }

    public void clickUpperEditButton() {
        By editButtonLocator =
                By.xpath("//a[contains(@class, 'MuiButton-root') and (contains(text(), 'Edit'))]");
        WebElement showButton = getWait().until(ExpectedConditions.elementToBeClickable(editButtonLocator));
        showButton.click();
    }

    public boolean isTextPresentOnViewPage(String expectedText) {
        By textLocator = By.xpath("//span[contains(@class, 'MuiTypography-body2') and text()='"
                +
                expectedText + "']");
        try {
            WebElement element =
                    getWait().until(ExpectedConditions.visibilityOfElementLocated(textLocator));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clearFirstNameField() {
        By locator = By.name("firstName");
        WebElement input = getWait().until(ExpectedConditions.elementToBeClickable(locator));
        input.click();
        input.sendKeys(Keys.END);
        input.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        input.sendKeys(Keys.BACK_SPACE);
    }

    public void clearLastNameField() {
        By locator = By.name("lastName");
        WebElement input = getWait().until(ExpectedConditions.elementToBeClickable(locator));
        input.click();
        input.sendKeys(Keys.END);
        input.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        input.sendKeys(Keys.BACK_SPACE);
    }

    public void clearEmailField() {
        By locator = By.name("email");
        WebElement input = getWait().until(ExpectedConditions.elementToBeClickable(locator));
        input.click();
        input.sendKeys(Keys.END);
        input.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        input.sendKeys(Keys.BACK_SPACE);
    }

    public boolean isInvalidEmailErrorDisplayed() {
        By errorLocator = By.xpath("//*[contains(text(), 'Incorrect email format')]");

        try {
            WebElement errorMessage = getWait().until(ExpectedConditions.visibilityOfElementLocated(errorLocator));
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void fillFirstNameField(String firstName) {
        By nameLocator = By.name("firstName");
        WebElement nameInput = getWait().until(ExpectedConditions.elementToBeClickable(nameLocator));
        nameInput.sendKeys(firstName);
    }

    public void fillLastNameField(String lastName) {
        By nameLocator = By.name("lastName");
        WebElement nameInput = getWait().until(ExpectedConditions.elementToBeClickable(nameLocator));
        nameInput.sendKeys(lastName);
    }

    public void fillEmailField(String email) {
        By nameLocator = By.name("email");
        WebElement nameInput = getWait().until(ExpectedConditions.elementToBeClickable(nameLocator));
        nameInput.sendKeys(email);
    }

    public String getFirstNameInputValue() {
        By firstNameInputLocator = By.cssSelector("input[name='firstName']");
        WebElement editFirstNameInput = getWait().until(ExpectedConditions.
                visibilityOfElementLocated(firstNameInputLocator));
        return editFirstNameInput.getAttribute("value");
    }

    public String getLastNameInputValue() {
        By firstNameInputLocator = By.cssSelector("input[name='lastName']");
        WebElement editLastNameInput =
                getWait().until(ExpectedConditions.visibilityOfElementLocated(firstNameInputLocator));
        return editLastNameInput.getAttribute("value");
    }

    public String getEmailInputValue() {
        By emailInputLocator = By.cssSelector("input[name='email']");
        WebElement editFirstNameInput =
                getWait().until(ExpectedConditions.visibilityOfElementLocated(emailInputLocator));
        return editFirstNameInput.getAttribute("value");
    }
    public int getUsersCount() {
        return getDriver().findElements(By.cssSelector(".MuiTableRow-root")).size();
    }
    public int getFinalUsersCount() {
        return getDriver().findElements(By.cssSelector(".MuiTableRow-root")).size();
    }
    public void waitForSnackBar() {
        getWait().until(ExpectedConditions.
                visibilityOfElementLocated(By.cssSelector(".MuiAlert-root, .MuiSnackbar-root")));
    }
    public void waitForPaginationTextOneToFive() {
        By paginationTextLocator = By.xpath("//p[contains(@class, 'MuiTablePagination-displayedRows')]");
        getWait().until(ExpectedConditions.textToBePresentInElementLocated(paginationTextLocator, "1-5"));
    }
    public void waitForPaginationTextSixToMore() {
        By paginationTextLocator = By.xpath("//p[contains(@class, 'MuiTablePagination-displayedRows')]");
        getWait().until(ExpectedConditions.textToBePresentInElementLocated(paginationTextLocator, "6-"));
    }
    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }
    public boolean isTextPresentOnPage(String text) {
        return getDriver().getPageSource().contains(text);
    }
}

