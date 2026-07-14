package hexlet.code;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.security.PublicKey;
import java.time.Duration;

public class LabelsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private final By createLabelButton = By.xpath("//*[contains(text(), 'Create')]");
    private final By nameField = By.cssSelector("input[name='name']");
    private final By saveButton = By.xpath("//*[contains(text(), 'Save')]");
    private final By nameHeader = By.xpath("//*[contains(text(), 'Name')]");
    private final By tableRows = By.xpath("//tbody/tr");
    private final By deleteButton = By.xpath("//*[contains(text(), 'Delete')]");

    public LabelsPage(WebDriver driver) {

        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateLabel() {
        wait.until(ExpectedConditions.elementToBeClickable(createLabelButton)).click();
    }

    public boolean isLabelFormDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void fillAndSubmitLabelForm(String labelName) {
        wait.until(ExpectedConditions.elementToBeClickable(nameField)).sendKeys(labelName);

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public boolean isLabelInList(String expectedStatus) {
        try {
            By labelsListContainer = By.className("list-page");
            wait.until(ExpectedConditions.visibilityOfElementLocated(labelsListContainer));

            By statusesCard = By.xpath("//*[contains(text(), '" + expectedStatus + "')]");

            wait.until(ExpectedConditions.visibilityOf(driver.findElement(statusesCard)));

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void forceGoToLabels() {
        By usersIcon = By.xpath("(//*[@data-testid='ViewListIcon'])[3]");

        wait.until(ExpectedConditions.elementToBeClickable(usersIcon)).click();
    }

    public boolean areHeaderDisplayed() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(nameHeader));
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

    public void clickEditLabel(String labelName) {
        String rowXPath = "//*[contains(text(), '" + labelName + "')]/ancestor::tr";
        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(rowXPath)));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", row);
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

    public void fillAndSubmitEditForm(String newName) {
        clearAndType(nameField, newName);

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public boolean isLabelsRowCorrect(String expectedLabelName) {
        String complexRowXPath = "//tr[contains(., '" + expectedLabelName + "')";

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(complexRowXPath)));

            return true;

        } catch (Exception e) {
            return false;

        }
    }

    public void clickDeleteButton() {
        wait.until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
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
}