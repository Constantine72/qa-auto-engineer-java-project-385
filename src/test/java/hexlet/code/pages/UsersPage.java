package hexlet.code.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;

import java.util.List;

public final class UsersPage extends BasePage {

    private final By createUserButton = By.xpath("//*[contains(text(), 'Create')]");
    private final By emailField = By.cssSelector("input[name='email']");
    private final By firstNameField = By.cssSelector("input[name='firstName']");
    private final By lastNameField = By.cssSelector("input[name='lastName']");
    private final By saveButton = By.xpath("//*[contains(text(), 'Save')]");
    private final By exportButton = By.xpath("//*[contains(text(), 'Export')]");
    private final By deleteButton = By.xpath("//*[contains(text(), 'Delete')]");
    private final By deleteAllUsersButton = By.xpath("//*[contains(text(), 'Delete')]");
    private final By userListContainer = By.className("list-page");
    private final By userRow = By.className("MuiTableRow-root");
    private final By emailErrorMessage = By.xpath("//*[contains(text(), 'Incorrect email format')]");
    private final By selectAllCheckbox = By.className("PrivateSwitchBase-input");

    public UsersPage(WebDriver driver) {

        super(driver);
    }

    public void clickCreateUser() {
        waitForClickableStrict(createUserButton).click();
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
    }

    public boolean isUserInList(String expectedFirstName, String expectedLastName, String expectedEmail) {
        try {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(userListContainer));

            List<WebElement> rows = getDriver().findElements(By.cssSelector("tbody .MuiTableRow-root"));

            for (WebElement row : rows) {
                try {
                    String fn = row.findElement(By.cssSelector(".column-firstName")).getText();
                    String ln = row.findElement(By.cssSelector(".column-lastName")).getText();
                    String em = row.findElement(By.cssSelector(".column-email")).getText();

                    if (fn.contains(expectedFirstName) && ln.contains(expectedLastName) && em.contains(expectedEmail)) {
                        return true;
                    }
                } catch (Exception e) {
                }
            }
            return false;
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
            getWait().until(ExpectedConditions.visibilityOfElementLocated(userListContainer));

            String theadText = getDriver().findElement(By.tagName("thead")).getText();

            boolean hasId = theadText.contains("Id");
            boolean hasEmail = theadText.contains("Email");
            boolean hasFirstName = theadText.contains("First name");
            boolean hasLastName = theadText.contains("Last name");
            boolean hasCreatedAt = theadText.contains("Created at");

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

        WebElement btn = getWait().until(ExpectedConditions.visibilityOfElementLocated(saveButton));
        checkElementNotDisabled(btn, "Save Button");
        btn.click();

    }

    public void clickExportButtonForUsers() {
        getWait().until(ExpectedConditions.elementToBeClickable(exportButton)).click();
    }

    public void forceGoToUsers() {
        By usersIcon = By.xpath("(//*[@data-testid='ViewListIcon'])[2]");

        getWait().until(ExpectedConditions.elementToBeClickable(usersIcon)).click();
    }

    private void clearAndType(By fieldLocator, String text) {

        WebElement input = getWait().until(ExpectedConditions.presenceOfElementLocated(fieldLocator));
        input.click();

        new Actions(getDriver())
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
                .sendKeys(Keys.BACK_SPACE)
                .sendKeys(text)
                .perform();

        if (!input.getAttribute("value").contains(text)) {
            throw new AssertionError("field is disabled");
        }

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
        try {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(By.
                    xpath("//*[contains(text(), 'No Users yet')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
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
        checkElementNotDisabled(input, "First Name Field");
        input.click();
        input.sendKeys(Keys.END);
        input.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        input.sendKeys(Keys.BACK_SPACE);

    }

    public void clearLastNameField() {
        By locator = By.name("lastName");
        WebElement input = getWait().until(ExpectedConditions.elementToBeClickable(locator));
        checkElementNotDisabled(input, "Last Name Field");
        input.click();
        input.sendKeys(Keys.END);
        input.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        input.sendKeys(Keys.BACK_SPACE);

    }

    public void clearEmailField() {
        By locator = By.name("email");
        WebElement input = getWait().until(ExpectedConditions.elementToBeClickable(locator));
        checkElementNotDisabled(input, "Email Field");
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
        checkElementNotDisabled(nameInput, "First Name Field");
        nameInput.sendKeys(firstName);
    }

    public void fillLastNameField(String lastName) {
        By nameLocator = By.name("lastName");
        WebElement nameInput = getWait().until(ExpectedConditions.elementToBeClickable(nameLocator));
        checkElementNotDisabled(nameInput, "Last Name Field");
        nameInput.sendKeys(lastName);
    }

    public void fillEmailField(String email) {
        By nameLocator = By.name("email");
        WebElement nameInput = getWait().until(ExpectedConditions.elementToBeClickable(nameLocator));
        checkElementNotDisabled(nameInput, "Email Field");
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
        List<WebElement> elements = getDriver().findElements(By.
                xpath("//*[contains(text(), '" + text + "')]"));
        return elements.stream().anyMatch(WebElement::isDisplayed);
    }

    public void clickEmailColumnHeader() {
        WebElement emailHeader = getDriver().findElement(By.cssSelector("[data-field='email']"));
        emailHeader.click();
    }

    public String getEmailFromFirstRow() {
        WebElement firstRowEmailCell = getDriver().findElement(By.cssSelector("tbody .column-email"));
        return firstRowEmailCell.getText().trim();
    }

    public boolean isSaveButtonEnabled() {
        WebElement saveBtn = getDriver().findElement(By.xpath("//*[contains(text(), 'Save')]"));

        return saveBtn.isEnabled();
    }

    private WebElement waitForClickableStrict(By locator) {
        try {
            return getWait().until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            throw new AssertionError("the element is unavailable!" + locator);
        }
    }

    private void checkElementNotDisabled(WebElement element, String elementName) {
        if (!element.isEnabled()) {
            throw new AssertionError(elementName + " is disabled");
        }
        if (element.getAttribute("disabled") != null
                ||
                "true".equals(element.getAttribute("aria-disabled"))) {
            throw new AssertionError(elementName + " has disabled attribute");
        }
        String classes = element.getAttribute("class");
        if (classes != null && (classes.contains("disabled") || classes.contains("Disabled"))) {
            throw new AssertionError(elementName + " has disabled css class");
        }
    }
    public boolean isEmailFieldEnabled() {
        WebElement emailInput = getWait().until(ExpectedConditions.presenceOfElementLocated(By.name("email")));
        return emailInput.isEnabled() && emailInput.getAttribute("disabled") == null;
    }
}

