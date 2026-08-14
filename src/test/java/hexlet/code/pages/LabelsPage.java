package hexlet.code.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;


public final class LabelsPage extends BasePage {

    private final By createLabelButton = By.xpath("//*[contains(text(), 'Create')]");
    private final By nameField = By.cssSelector("input[name='name']");
    private final By saveButton = By.xpath("//*[contains(text(), 'Save')]");
    private final By nameHeader = By.xpath("//*[contains(text(), 'Name')]");
    private final By tableRows = By.xpath("//tbody/tr");
    private final By deleteButton = By.xpath("//*[contains(text(), 'Delete')]");

    public LabelsPage(WebDriver driver) {
        super(driver);
    }

    public void clickCreateLabel() {
        getWait().until(ExpectedConditions.elementToBeClickable(createLabelButton)).click();
    }

    public void fillAndSubmitLabelForm(String labelName) {
        getWait().until(ExpectedConditions.elementToBeClickable(nameField)).sendKeys(labelName);

        WebElement btn = getWait().until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", btn);
    }

    public boolean isLabelInList(String expectedStatus) {
        try {
            By labelsListContainer = By.className("list-page");
            getWait().until(ExpectedConditions.visibilityOfElementLocated(labelsListContainer));

            By statusesCard = By.xpath("//*[contains(text(), '" + expectedStatus + "')]");

            getWait().until(ExpectedConditions.visibilityOf(getDriver().findElement(statusesCard)));

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void forceGoToLabels() {
        By usersIcon = By.xpath("(//*[@data-testid='ViewListIcon'])[3]");

        getWait().until(ExpectedConditions.elementToBeClickable(usersIcon)).click();
    }

    public boolean areHeaderDisplayed() {
        try {
            getWait().until(ExpectedConditions.presenceOfElementLocated(nameHeader));
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

    public void clickEditLabel(String labelName) {
        String rowXPath = "//*[contains(text(), '" + labelName + "')]/ancestor::tr";
        WebElement row = getWait().until(ExpectedConditions.elementToBeClickable(By.xpath(rowXPath)));

        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", row);
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

    public void fillAndSubmitEditForm(String newName) {
        clearAndType(nameField, newName);

        WebElement btn = getWait().until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", btn);
    }

    public void clickDeleteButton() {
        getWait().until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
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

    public void triggerValidationOnNameField() {
        By nameLocator = By.name("name");
        WebElement nameInput = getWait().until(ExpectedConditions.elementToBeClickable(nameLocator));

        nameInput.click();
        nameInput.sendKeys("a");
        nameInput.sendKeys(Keys.BACK_SPACE);
    }

    public void clickSaveButtonForLabels() {

        WebElement btn = getWait().until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", btn);
    }

    public void clearLabelField() {
        By labelLocator = By.name("name");
        WebElement labelInput = getWait().until(ExpectedConditions.elementToBeClickable(labelLocator));
        labelInput.click();
        labelInput.sendKeys(Keys.END);
        labelInput.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        labelInput.sendKeys(Keys.BACK_SPACE);
    }

    public String getNameInputValue() {
        By nameInputLocator = By.cssSelector("input[name='name']");
        WebElement editNameInput = getWait().until(ExpectedConditions.visibilityOfElementLocated(nameInputLocator));
        return editNameInput.getAttribute("value");
    }
    public int getInitialLabelsCount() {
        return getDriver().findElements(By.cssSelector(".MuiTableRow-root")).size();
    }
    public int getFinalLabelsCount() {
        return getDriver().findElements(By.cssSelector(".MuiTableRow-root")).size();
    }
    public void waitForListToLoad() {
        getWait().until(ExpectedConditions.or(ExpectedConditions.
                        presenceOfElementLocated(By.cssSelector(".MuiTableRow-root")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".RaList-noResults"))));
    }
    public void waitForSnackBar() {
        getWait().until(ExpectedConditions.
                visibilityOfElementLocated(By.cssSelector(".MuiAlert-root, .MuiSnackbar-root")));
    }
    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }
    public boolean isTextPresentOnPage(String text) {
        By textLocator = By.xpath("//*[contains(., '" + text + "')]");
        return !getDriver().findElements(textLocator).isEmpty();
    }
}
