package hexlet.code;

import org.openqa.selenium.*;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public int getTableRowsCount() {
        By rowsLocator = By.xpath("//tbody/tr");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(rowsLocator));
        } catch (Exception e) {
            return 0;
        }
        return driver.findElements(rowsLocator).size();
    }

    public void selectFirstRowCheckbox() {

        By firstRowCheckbox = By.cssSelector("tbody .PrivateSwitchBase-input");

        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(firstRowCheckbox));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
    }

    public void clickBulkDeleteButton() {
        By bulkDeleteLocator = By.xpath("//button[@aria-label='Delete']");
        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(bulkDeleteLocator));
        deleteButton.click();
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

    public void clickSaveButton() {
        By saveButtonLocator = By.xpath("//*[contains(text(), 'Save')]");
    }

    public void clickUnselectCrossButton() {
        By unselectLocator = By.xpath("//button[@aria-label='Unselect']");

        WebElement unselectBtn = wait.until(ExpectedConditions.elementToBeClickable(unselectLocator));
        unselectBtn.click();
    }

    public boolean isSelectionTextHidden() {
        By selectionTextLocator = By.xpath("//*[contains(text(), '1 item selected')]");

        try {

            return wait.until(ExpectedConditions.invisibilityOfElementLocated(selectionTextLocator));
        } catch (Exception e) {
            return false;
        }
    }

    public void changeRowsPerPage(String value) {
        By dropdownLocator = By.xpath("//div[contains(@class, 'MuiTablePagination-select')]");
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
        dropdown.click();

        By optionLocator = By.xpath("//li[@data-value='" + value + "']");
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
        option.click();
    }

    public boolean isNextPageButtonEnabled() {
        By nextButtonLocator = By.xpath("//button[@aria-label='Go to next page']");
        try {
            WebElement nextBtn = wait.until(ExpectedConditions.presenceOfElementLocated(nextButtonLocator));
            return nextBtn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickNextPageButton() {
        By nextButtonLocator = By.xpath("//button[@aria-label='Go to next page']");
        WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(nextButtonLocator));
        nextBtn.click();
    }

    public void clickPreviousPageButton() {
        By nextButtonLocator = By.xpath("//button[@aria-label='Go to previous page']");
        WebElement prevBtn = wait.until(ExpectedConditions.elementToBeClickable(nextButtonLocator));
        prevBtn.click();
    }
}
