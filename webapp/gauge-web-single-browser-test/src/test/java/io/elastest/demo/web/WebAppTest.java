package io.elastest.demo.web;

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
    @BeforeSpec(tags = { "unique" })
    public void beforeFeature() throws MalformedURLException {
        // If first time, init
        if (driver == null) {
            String sutHost = System.getenv("ET_SUT_HOST");
            String sutPort = System.getenv("ET_SUT_PORT");
            String sutProtocol = System.getenv("ET_SUT_PROTOCOL");

            if (sutHost == null) {
                sutUrl = "http://localhost:8080/";
            } else {
                sutPort = sutPort != null ? sutPort : "8080";
                sutProtocol = sutProtocol != null ? sutProtocol : "http";

                sutUrl = sutProtocol + "://" + sutHost + ":" + sutPort;
            }
            logger.info("Webapp URL: {}", sutUrl);

            browserType = System.getProperty("browser");
            logger.info("Browser Type: {}", browserType);
            eusURL = System.getenv("ET_EUS_API");

            if (eusURL == null) {
                if (browserType == null || browserType.equals(CHROME)) {
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver();
                } else {
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver();
                }
            } else {
                DesiredCapabilities caps;
                if (browserType == null || browserType.equals(CHROME)) {
                    caps = DesiredCapabilities.chrome();
                } else {
                    caps = DesiredCapabilities.firefox();
                }

                browserVersion = System.getProperty("browserVersion");
                if (browserVersion != null) {
                    logger.info("Browser Version: {}", browserVersion);
                    caps.setVersion(browserVersion);
                }

                caps.setCapability("testName", currentTestScenarioName);

                driver = new RemoteWebDriver(new URL(eusURL), caps);
            }

        }

        // driver quit when all tests end
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("Shutting down browser...");
                driver.quit();
            }
        });

    }

    @BeforeScenario(tags = { "unique" })
    public void beforeScenario(ExecutionContext context) {
        currentTestScenarioName = context.getCurrentScenario().getName();
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript(
                    "'<<##et => {\"command\": \"startTest\", \"args\": {\"testName\": \""
                            + currentTestScenarioName + "\"} }>>'");
        }
        logger.info("##### Start test: {}", currentTestScenarioName);
    }

    @AfterScenario(tags = { "unique" })
    public void afterScenario(ExecutionContext context) {
        currentTestScenarioName = context.getCurrentScenario().getName();
        logger.info("##### Finish test: {}", currentTestScenarioName);
    }

    // Hack because @AfterClass cannot be used
    @AfterSpec(tags = { "unique" })
    public void afterFeature() {
        if (driver != null) {
            driver.quit();
        }
    }

    /* ************************ */
    /* ******** Common ******** */
    /* ************************ */

    @Step("Navigate to app url")
    public void navigateToAppUrl() throws MalformedURLException {
        driver.get(sutUrl);
    }

    /* *************************************** */
    /* **** Check title and body no empty **** */
    /* *************************************** */

    @Step("Add an empty title and body")
    public void addAnEmptyTitleAndBody() throws InterruptedException {
        Thread.sleep(2000);

        newTitle = "";
        newBody = "";

        this.addRow(newTitle, newBody);

        Thread.sleep(2000);
    }

    @Step("Check that row with empty title and body has been added")
    public void checkThatRowWithEmptyTitleAndBodyHasBeenAdded()
            throws InterruptedException {

        String title = driver.findElement(By.id("title")).getText();
        String body = driver.findElement(By.id("body")).getText();

        logger.info("Checking Message...");

        try {
            assertThat(title, not(equalTo(newTitle)));
            assertThat(body, not(equalTo(newBody)));
        } finally {
            Thread.sleep(2000);
            clearRows();
        }
    }

    /* *************************************** */
    /* ********* Find title and body ********* */
    /* *************************************** */

    @Step("Add a row with title and body")
    public void addARowWithTitleAndBody() throws InterruptedException {
        Thread.sleep(2000);

        newTitle = "MessageTitle";
        newBody = "MessageBody";

        this.addRow(newTitle, newBody);
        Thread.sleep(2000);
    }

    @Step("Check that row with the same title and body has been added")
    public void CheckThatRowWithTheSameTitleAndBodyHasBeenAdded()
            throws InterruptedException {

        String title = driver.findElement(By.id("title")).getText();
        String body = driver.findElement(By.id("body")).getText();

        // Added
        logger.info("Checking Message...");

        try {
            assertThat(title, equalTo(newTitle));
            assertThat(body, equalTo(newBody));
        } finally {
            Thread.sleep(2000);
            clearRows();
        }
    }

    /* ********************* */
    /* *** Other methods *** */
    /* ********************* */

    public void addRow(String newTitle, String newBody)
            throws InterruptedException {
        driver.findElement(By.id("title-input")).sendKeys(newTitle);
        driver.findElement(By.id("body-input")).sendKeys(newBody);

        Thread.sleep(2000);

        logger.info("Adding Message...");

        driver.findElement(By.id("submit")).click();
    }

    public void clearRows() throws InterruptedException {
        logger.info("Clearing Messages...");
        driver.findElement(By.id("clearSubmit")).click();
        Thread.sleep(1000);
    }
}
