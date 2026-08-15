package hexlet.code.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

public final class StatusesPage extends BasePage {

    private final By createStatusButton = By.xpath("//*[contains(text(), 'Create')]");
    private final By nameField = By.cssSelector("input[name='name']");
    private final By slugField = By.cssSelector("input[name='slug']");
    private final By saveButton = By.xpath("//*[contains(text(), 'Save')]");
    private final By nameHeader = By.xpath("//*[contains(text(), 'Name')]");
    private final By slugHeader = By.xpath("//*[contains(text(), 'Slug')]");
    private final By tableRows = By.cssSelector("table tbody tr");
    private final By deleteButton = By.xpath("//*[contains(text(), 'Delete')]");
    private final By selectAllCheckbox = By.className("PrivateSwitchBase-input");
    private final By deleteAllStatusesButton = By.xpath("//*[contains(text(), 'Delete')]");
    private static final int MAX_WAIT_DURATION = 5000;
    private static final int MINIMAL_SLEEP = 500;

    public StatusesPage(WebDriver driver) {
        super(driver);
    }

    public void clickCreateStatus() {
        getWait().until(ExpectedConditions.elementToBeClickable(createStatusButton)).click();
    }

    public void fillAndSubmitStatusForm(String name, String slug) {

        getWait().until(ExpectedConditions.elementToBeClickable(nameField)).sendKeys(name);
        getWait().until(ExpectedConditions.elementToBeClickable(slugField)).sendKeys(slug);
        getWait().until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    public boolean isStatusInList(String expectedStatus) {
        try {
            By statusesListContainer = By.className("list-page");
            getWait().until(ExpectedConditions.visibilityOfElementLocated(statusesListContainer));

            By statusesCard = By.xpath("//*[contains(text(), '" + expectedStatus + "')]");

            getWait().until(ExpectedConditions.visibilityOf(getDriver().findElement(statusesCard)));

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void forceGoToStatuses() {
        By usersIcon = By.xpath("(//*[@data-testid='ViewListIcon'])[4]");

        getWait().until(ExpectedConditions.elementToBeClickable(usersIcon)).click();
    }

    public boolean areHeaderDisplayed() {
        try {
            getWait().until(ExpectedConditions.presenceOfElementLocated(nameHeader));
            getWait().until(ExpectedConditions.presenceOfElementLocated(slugHeader));
            return true;

        } catch (Exception e) {
            return false;

        }
    }

    public boolean isStatusRowCorrect(String expectedName, String expectedSlug) {
        String complexRowXPath = "//tr[contains(., '" + expectedName + "') and contains(., '" + expectedSlug + "')]";

        try {
            getWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath(complexRowXPath)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getRowsCount() {
        try {
            getWait().until(webDriver -> webDriver.findElements(tableRows).size() > 0);
            return getDriver().findElements(tableRows).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickEditStatus(String statusName) {
        String rowXPath = "//*[contains(text(), '" + statusName + "')]/ancestor::tr";
        WebElement row = getWait().until(ExpectedConditions.elementToBeClickable(By.xpath(rowXPath)));

        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", row);
    }

    public void fillAndSubmitEditForm(String newName, String newSlug) {
        clearAndType(nameField, newName);
        clearAndType(slugField, newSlug);

        WebElement btn = getWait().until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", btn);
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
        getWait().until(ExpectedConditions.elementToBeClickable(deleteAllStatusesButton)).click();

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

            if (pageSource.contains("No Task statuses yet.") && pageSource.contains("Do you want to add one?")) {
                return true;
            }
            try {
                Thread.sleep(MINIMAL_SLEEP);
            } catch (InterruptedException ignored) {
            }
        }
        return false;
    }

    public boolean isTextPresentOnViewPage(String expectedText) {
        By textLocator = By.xpath("//span[contains(@class, 'MuiTypography-body2') and text()='"
                +
                expectedText + "']");
        try {
            WebElement element = getWait().until(ExpectedConditions.visibilityOfElementLocated(textLocator));
            return element.isDisplayed();
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

    public void fillNameField(String name) {
        By nameLocator = By.name("name");
        WebElement nameInput = getWait().until(ExpectedConditions.elementToBeClickable(nameLocator));
        nameInput.sendKeys(name);
    }

    public void fillSlugField(String slug) {
        By slugLocator = By.name("slug");
        WebElement nameInput = getWait().until(ExpectedConditions.elementToBeClickable(slugLocator));
        nameInput.sendKeys(slug);
    }

    public void clearNameField() {
        By nameLocator = By.name("name");
        WebElement nameInput = getWait().until(ExpectedConditions.elementToBeClickable(nameLocator));

        nameInput.click();

        nameInput.sendKeys(Keys.END);
        nameInput.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));

        nameInput.sendKeys(Keys.BACK_SPACE);
    }

    public void clickSaveButtonForStatuses() {

        WebElement btn = getWait().until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", btn);
    }

    public void clearSlugField() {
        By slugLocator = By.name("slug");
        WebElement slugInput = getWait().until(ExpectedConditions.elementToBeClickable(slugLocator));
        slugInput.click();
        slugInput.sendKeys(Keys.END);
        slugInput.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        slugInput.sendKeys(Keys.BACK_SPACE);
    }
    public String getNameInputValue() {
        By nameInputLocator = By.cssSelector("input[name='name']");
        WebElement editNameInput = getWait().until(ExpectedConditions.visibilityOfElementLocated(nameInputLocator));
        return editNameInput.getAttribute("value");
    }
    public String getSlugInputValue() {
        By slugInputLocator = By.cssSelector("input[name='slug']");
        WebElement editSlugInput = getWait().until(ExpectedConditions.visibilityOfElementLocated(slugInputLocator));
        return editSlugInput.getAttribute("value");
    }
    public void waitForListToLoad() {
        getWait().until(ExpectedConditions.or(ExpectedConditions.
                        presenceOfElementLocated(By.cssSelector(".MuiTableRow-root")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".RaList-noResults"))));
    }
    public int getInitialStatusesCount() {
        return getDriver().findElements(By.cssSelector(".MuiTableRow-root")).size();
    }
    public int getFinalStatusesCount() {
        return
                getDriver().findElements(By.cssSelector(".MuiTableRow-root")).size();
    }
    public void waitForSnackBar() {
        getWait().until(ExpectedConditions.
                visibilityOfElementLocated(By.cssSelector(".MuiAlert-root, .MuiSnackbar-root")));
    }
    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }
    public boolean isStatusPresent(String oldXPath) {
        By statusLocator = By.xpath("//*[contains(., '" + oldXPath + "')]");
        return !getDriver().findElements(statusLocator).isEmpty();
    }
}
