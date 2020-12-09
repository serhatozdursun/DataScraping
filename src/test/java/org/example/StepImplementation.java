package org.example;

import com.thoughtworks.gauge.Gauge;
import com.thoughtworks.gauge.Step;
import driver.Driver;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.WriteToTxt;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class StepImplementation {
    private WebElement element = null;
    private RemoteWebDriver driver;
    private ArrayList<String> foundElements = new ArrayList<>();

    public StepImplementation() {
        this.driver = Driver.webDriver;
    }

    @Step("Write stored web elements to a csv file")
    public void gotoGetStartedPage() throws InterruptedException {
        WriteToTxt writeToTxt = new WriteToTxt();
        String fileNameAndPath = System.getProperty("user.dir") + "/csvFile/storedData.csv";
        var writer = writeToTxt.createWriter(fileNameAndPath);

        if (foundElements.size() > 0) {
            writeToTxt.writeToTxt(writer, "Titles, Href");
            for (String line : foundElements) {
                writeToTxt.writeToTxt(writer, line);
            }
            writeToTxt.closeWriter(writer);
        }

    }

    @Step("Search, find and store <requested data> web elements")
    public void getWebElementsValues(String locatorValue) throws Exception {
        List<WebElement> elements = getElements(locatorValue);
        JavascriptExecutor js = driver;

        for (int i = 2; i < 5; i++) {
            int y = 100;

            for (WebElement elm : elements) {
                String href = elm.findElement(By.cssSelector(".link-detail")).getAttribute("href");
                String title = elm.findElement(By.cssSelector(".product-title")).getText();

                title = title.replace("\n", " ");
                System.out.println(title + " " + href);
                foundElements.add(title + ", " + href);
                js.executeScript("window.scrollTo(0," + y + ");");
                y += 100;
            }
            click(0, "//a[contains(@class,'s1pk8cwy') and contains(.,'" + i + "')]");
            js.executeScript("window.scrollTo(0," + y + ");");

            elements = getElements(locatorValue);

        }

    }

    @Step("Click <web element> if is  exists")
    public void clickElementIfExists(String locatorValue) throws Exception {
        Thread.sleep(2000);
        var elements = getElements(locatorValue);
        if (elements.size() > 0) {
            driver.navigate().refresh();
            Thread.sleep(2000);

        }
    }

    @Step("Open  web site")
    public void implementation1() {
        driver.manage().window().maximize();
        driver.get("https://www.cimri.com/");
        JavascriptExecutor js = driver;
        js.executeScript("window.scrollTo(0,1000);");
    }

    @Step("Write <text> on the <index>th <element>")
    public void writeText(String entryType, int index, String locatorValue) {
        try {
            locator(index, locatorValue).sendKeys(entryType);
        } catch (Exception e) {
            System.out.println("Text could not be written to the element!");
        }
    }

    @Step("Click on the <index>th <element>")
    public void click(int index, String locatorValue) {
        try {
            WebElement clickableElement = locator(index, locatorValue);
            JavascriptExecutor js = driver;
            js.executeScript("arguments[0].scrollIntoView(true);", clickableElement);
            clickableElement.click();
            js.executeScript("window.scrollTo(0,1000);");
        } catch (Exception e) {
            System.out.println("The element could not be clicked! " + e.getMessage());
        }
    }

    @Step("Pause the test for <seconds>")
    public void pauseTest(int second) throws InterruptedException {
        Thread.sleep((second * 1000));
    }


    @Step("Get the text of the <index>th <element> and compare it with <text>. are they the same?")
    public void getText(int index, String locatorValue, String text) throws Exception {
        String textOnPage = locator(index, locatorValue).getText();
        assertThat(text.equals(textOnPage));
    }

    private WebElement locator(int index, String locatorValue) throws Exception {
        element = null;
        List<WebElement> listObj;

        try {
            //Eğer ekranda birden fazla aynı element olduğu düşünülmüyorsa ya da index verilmemişse
            if (index == 0) {
                element = driver.findElement(setBy(locatorValue));
            } else {
                listObj = driver.findElements(setBy(locatorValue));
                element = listObj.get(index - 1);
            }
        } catch (NotFoundException ex) {
            System.out.println(locatorValue + " The element not found");
        } catch (ElementNotVisibleException visibleex) {
            System.out.println(locatorValue + " The element not visible on screen");
        } catch (ElementNotInteractableException interactex) {
            System.out.println(locatorValue + " The element not interactable");
        } catch (ElementNotSelectableException selectex) {
            System.out.println(locatorValue + " The element not selectable");
        } catch (StaleElementReferenceException referenceException) {
            System.out.println(locatorValue + "The element not accessible");
        } finally {
            if (!element.isEnabled()) {
                throw new Exception("The element not enabled");
            } else {
                return element;
            }
        }
    }

    private List<WebElement> getElements(String locatorValue) {
        return driver.findElements(setBy(locatorValue));
    }

    // preparing of the element locator
    private By setBy(String locatorValue) {
        By elementBy;
        String[] locatorValueArray = locatorValue.split("=");// id=xxx, css=xxx,link=xxx, //*
        Gauge.writeMessage("locator Identifier : " + locatorValueArray[0] + "");
        String locatorType = locatorValueArray[0];
        if (locatorValue.startsWith("/")) {
            elementBy = By.xpath(locatorValue);
        } else {
            switch (locatorType) {
                case "id":
                    elementBy = By.id(locatorValueArray[1]);
                    break;
                case "css":
                    elementBy = By.cssSelector(locatorValueArray[1]);
                    break;
                case "name":
                    elementBy = By.name(locatorValueArray[1]);
                    break;
                case "partial":
                    elementBy = By.partialLinkText(locatorValueArray[1]);
                    break;
                case "link":
                    elementBy = By.linkText(locatorValueArray[1]);
                    break;
                case "tag":
                    elementBy = By.tagName(locatorValueArray[1]);
                    break;
                case "class":
                    elementBy = By.className(locatorValueArray[1]);
                    break;
                case "dropDownLabel":
                    elementBy = By.xpath("//span[@class='label' and contains(.,'" + locatorValueArray[1] + "')]");
                    break;
                default:
                    elementBy = By.xpath("//*[" +
                            "contains(@id,'" + locatorValue + "') or " +
                            "contains(text(),'" + locatorValue + "') or " +
                            "contains(@value,'" + locatorValue + "') or " +
                            "contains(css,'" + locatorValue + "') or " +
                            "contains(@placeholder,'" + locatorValue + "') or " +
                            "contains(@class,'" + locatorValue + "') or " +
                            "contains(@href,'" + locatorValue + "')]");
                    break;
            }
        }
        return elementBy;
    }
}
