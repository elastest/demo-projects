package io.elastest.demo.web.unique;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.AfterSpec;
import com.thoughtworks.gauge.BeforeScenario;
import com.thoughtworks.gauge.BeforeSpec;
import com.thoughtworks.gauge.ExecutionContext;
import com.thoughtworks.gauge.Step;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WebAppTest {
    private static final Logger logger = LoggerFactory
            .getLogger(WebAppTest.class);

    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";

    private static final String etMonitorMarkPrefix = "##elastest-monitor-mark:";

    private static String browserType;
    private static String browserVersion;
    private static String eusURL;
    private static String sutUrl;

    private static WebDriver driver;

    // Test variables
    String newTitle;
    String newBody;
    String currentTestScenarioName;

    // Hack because @BeforeClass cannot be used
    @BeforeSpec(tags = {"unique"})
    public void beforeFeature() throws MalformedURLException {
        browserType = System.getProperty("browser");
        logger.info("Browser Type: {}", browserType);

        eusURL = System.getenv("ET_EUS_API");
        if (eusURL == null) {

            if (browserType == null || browserType.equals(CHROME)) {
                WebDriverManager.chromedriver().setup();
            } else {
                WebDriverManager.firefoxdriver().setup();
            }
        }

        String sutHost = System.getenv("ET_SUT_HOST");
        if (sutHost == null) {
            sutUrl = "http://localhost:8080/";
        } else {
            sutUrl = "http://" + sutHost + ":8080/";
        }
        logger.info("Webapp URL: {}", sutUrl);

        browserVersion = System.getProperty("browserVersion");

        if (eusURL == null) {
            if (browserType == null || browserType.equals(CHROME)) {
                driver = new ChromeDriver();
            } else {
                driver = new FirefoxDriver();
            }
        } else {
            DesiredCapabilities caps;
            if (browserType == null || browserType.equals(CHROME)) {
                caps = DesiredCapabilities.chrome();
            } else {
                caps = DesiredCapabilities.firefox();
            }

            if (browserVersion != null) {
                logger.info("Browser Version: {}", browserVersion);
                caps.setVersion(browserVersion);
            }

            caps.setCapability("testName", currentTestScenarioName);

            driver = new RemoteWebDriver(new URL(eusURL), caps);
        }

    }

    @BeforeScenario(tags = {"unique"})
    public void beforeScenario(ExecutionContext context) {
        currentTestScenarioName = context.getCurrentScenario().getName();
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript(
                    "'<<##et => {\"command\": \"startTest\", \"args\": {\"testName\": \""
                            + currentTestScenarioName + "\"} }>>'");
        }
        logger.info("##### Start test: {}", currentTestScenarioName);
    }

    @AfterScenario(tags = {"unique"})
    public void afterScenario(ExecutionContext context) {
        currentTestScenarioName = context.getCurrentScenario().getName();

        if (driver != null) {
            logger.info("Clearing Messages...");
            driver.findElement(By.id("clearSubmit")).click();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            logger.info("##### Finish test: {}", currentTestScenarioName);
        }
    }

    // Hack because @AfterClass cannot be used
    @AfterSpec(tags = {"unique"})
    public void afterFeature() {
        if (driver != null) {
            driver.quit();
        }
    }

    public void addRow(String testName, String newTitle, String newBody)
            throws InterruptedException {
        driver.findElement(By.id("title-input")).sendKeys(newTitle);
        driver.findElement(By.id("body-input")).sendKeys(newBody);

        Thread.sleep(2000);

        logger.info("Adding Message...");
        logger.info(etMonitorMarkPrefix + " id=action, value=Submit ("
                + testName + ")");
        driver.findElement(By.id("submit")).click();
    }

    /* ************************ */
    /* ******** Common ******** */
    /* ************************ */

    @Step("Navigate to app url 2")
    public void navigateToAppUrl() throws MalformedURLException {
        driver.get(sutUrl);
    }

    /* *************************************** */
    /* **** Check title and body no empty **** */
    /* *************************************** */

    @Step("Add an empty title and body 2")
    public void addAnEmptyTitleAndBody() throws InterruptedException {
        Thread.sleep(2000);

        newTitle = "";
        newBody = "";

        this.addRow(currentTestScenarioName, newTitle, newBody);

        Thread.sleep(2000);
    }

    @Step("Check that row with empty title and body has been added 2")
    public void checkThatRowWithEmptyTitleAndBodyHasBeenAdded()
            throws InterruptedException {

        String title = driver.findElement(By.id("title")).getText();
        String body = driver.findElement(By.id("body")).getText();

        logger.info("Checking Message...");
        logger.info(etMonitorMarkPrefix + " id=action, value=Assert ("
                + currentTestScenarioName + ")");
        assertThat(title, not(equalTo(newTitle)));
        assertThat(body, not(equalTo(newBody)));

        Thread.sleep(2000);
    }

    /* *************************************** */
    /* ********* Find title and body ********* */
    /* *************************************** */

    @Step("Add a row with title and body 2")
    public void addARowWithTitleAndBody() throws InterruptedException {
        Thread.sleep(2000);

        newTitle = "MessageTitle";
        newBody = "MessageBody";

        this.addRow(currentTestScenarioName, newTitle, newBody);
        Thread.sleep(2000);
    }

    @Step("Check that row with the same title and body has been added 2")
    public void CheckThatRowWithTheSameTitleAndBodyHasBeenAdded()
            throws InterruptedException {

        String title = driver.findElement(By.id("title")).getText();
        String body = driver.findElement(By.id("body")).getText();

        // Added
        logger.info("Checking Message...");

        logger.info(etMonitorMarkPrefix + " id=action, value=Assert ("
                + currentTestScenarioName + ")");
        assertThat(title, equalTo(newTitle));
        assertThat(body, equalTo(newBody));

        Thread.sleep(1000);
    }
}
