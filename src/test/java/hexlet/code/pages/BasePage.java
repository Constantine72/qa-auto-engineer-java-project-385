package hexlet.code.pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.ByteArrayInputStream;
import java.time.Duration;

public class BasePage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final int TIMEOUT_SECONDS = 20;

    public BasePage(WebDriver webDriver) {
        this.driver = webDriver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
    }

    protected final WebDriver getDriver() {
        return driver;
    }
    protected final WebDriverWait getWait() {
        return wait;
    }

    public final int getTableRowsCount() {
        By rowsLocator = By.xpath("//tbody/tr");
        try {
            getWait().until(ExpectedConditions.presenceOfElementLocated(rowsLocator));
        } catch (Exception e) {
            return 0;
        }
        return getDriver().findElements(rowsLocator).size();
    }

    public final void selectFirstRowCheckbox() {

        By firstRowCheckbox = By.cssSelector("tbody .PrivateSwitchBase-input");

        WebElement checkbox = getWait().until(ExpectedConditions.presenceOfElementLocated(firstRowCheckbox));

        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", checkbox);
    }

    public final void clickBulkDeleteButton() {
        By bulkDeleteLocator = By.xpath("//button[@aria-label='Delete']");
        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(bulkDeleteLocator));
        deleteButton.click();
    }

    public final boolean isRequiredErrorDisplayed() {
        By errorLocator = By.xpath("//*[contains(text(), 'Required')]");

        try {
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator));
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public final void clickUnselectCrossButton() {
        By unselectLocator = By.xpath("//button[@aria-label='Unselect']");

        WebElement unselectBtn = wait.until(ExpectedConditions.elementToBeClickable(unselectLocator));
        unselectBtn.click();
    }

    public final boolean isSelectionTextHidden() {
        By selectionTextLocator = By.xpath("//*[contains(text(), '1 item selected')]");

        try {

            return wait.until(ExpectedConditions.invisibilityOfElementLocated(selectionTextLocator));
        } catch (Exception e) {
            return false;
        }
    }

    public final void changeRowsPerPage(String value) {
        By dropdownLocator = By.xpath("//div[contains(@class, 'MuiTablePagination-select')]");
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
        dropdown.click();

        By optionLocator = By.xpath("//li[@data-value='" + value + "']");
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
        option.click();
    }

    public final boolean isNextPageButtonEnabled() {
        By nextButtonLocator = By.xpath("//button[@aria-label='Go to next page']");
        try {
            WebElement nextBtn = wait.until(ExpectedConditions.presenceOfElementLocated(nextButtonLocator));
            return nextBtn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public final void clickNextPageButton() {
        By nextButtonLocator = By.xpath("//button[@aria-label='Go to next page']");
        WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(nextButtonLocator));
        nextBtn.click();
    }

    public final void clickPreviousPageButton() {
        By nextButtonLocator = By.xpath("//button[@aria-label='Go to previous page']");
        WebElement prevBtn = wait.until(ExpectedConditions.elementToBeClickable(nextButtonLocator));
        prevBtn.click();
    }
    protected final void takesScreenshot() {
        Allure.addAttachment(
                "Screenshot on failure",
                new ByteArrayInputStream(((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES)));
    }
}
