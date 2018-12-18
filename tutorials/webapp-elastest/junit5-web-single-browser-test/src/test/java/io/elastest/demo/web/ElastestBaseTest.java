package io.elastest.demo.web;

import static org.openqa.selenium.OutputType.BASE64;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ElastestBaseTest {
    protected static final Logger logger = LoggerFactory
            .getLogger(ElastestBaseTest.class);

    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";

    protected static String browserType;
    protected static String browserVersion;
    protected static String eusURL;
    protected static String sutUrl;

    protected static WebDriver driver;
    protected static Boolean screenshotActivated;

    @BeforeAll
    public static void setupClass() throws MalformedURLException {
        // If first time, init
        if (driver == null) {
            String sutHost = System.getenv("ET_SUT_HOST");
            String sutPort = System.getenv("ET_SUT_PORT");
            String sutProtocol = System.getenv("ET_SUT_PROTOCOL");
            String a = System.getenv("SCREENSHOT_ACTIVATED");
            logger.debug("System.getenv(\"SCREENSHOT_ACTIVATED\") {}", a);
            screenshotActivated = Boolean
                    .getBoolean(System.getenv("SCREENSHOT_ACTIVATED"));

            if (sutHost == null) {
                sutUrl = "http://localhost:8080/";
            } else {
                sutPort = sutPort != null ? sutPort : "8080";
                sutProtocol = sutProtocol != null ? sutProtocol : "http";

                sutUrl = sutProtocol + "://" + sutHost + ":" + sutPort;
            }
            logger.info("Webapp URL: " + sutUrl);

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
                driver = new RemoteWebDriver(new URL(eusURL), caps);
            }

            // driver quit when all tests end
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    logger.info("Shutting down browser...");
                    driver.quit();
                }
            });
        }
    }

    @BeforeEach
    public void setupTest(TestInfo info) throws MalformedURLException {
        String testName = info.getTestMethod().get().getName();
        ((JavascriptExecutor) driver).executeScript(
                "'{\"elastestCommand\": \"startTest\", \"args\": {\"testName\": \""
                        + testName + "\"} }'");

        logger.info("##### Start test: {}", testName);

        driver.get(sutUrl);
    }

    @AfterEach
    public void teardown(TestInfo info) {
        String testName = info.getTestMethod().get().getName();
        logBase64Screenshot(driver);
        logger.info("##### Finish test: {}", testName);
    }

    public void logBase64Screenshot(WebDriver driver) {
        boolean activated = screenshotActivated != null && screenshotActivated;
        logger.info("Cheking if screenshots are activated: {}", activated);
        if (activated) {
            try {
                String screenshotBase64 = ((TakesScreenshot) driver)
                        .getScreenshotAs(BASE64);
                logger.info("Screenshot (in Base64) at the end of {} "
                        + "(copy&paste this string as URL in browser to watch it):\r\n"
                        + "data:image/png;base64,{}", screenshotBase64);
            } catch (Exception e) {
                logger.trace("Exception getting screenshot in Base64", e);
            }
        }
    }
}
