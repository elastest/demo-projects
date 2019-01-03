package io.elastest.demo.web;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.gauge.ExecutionContext;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ElastestBaseTest {
    protected static final Logger logger = LoggerFactory
            .getLogger(WebAppTest.class);

    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";

    protected static String browserType;
    protected static String browserVersion;
    protected static String eusURL;
    protected static String sutUrl;

    protected static WebDriver driver;
    String currentTestScenarioName;

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
                logger.info("Using ElasTest EUS URL: {}", eusURL);

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

            // driver quit when all tests end
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    logger.info("Shutting down browser...");
                    driver.quit();
                }
            });
        }
    }

    public void beforeScenario(ExecutionContext context) {
        currentTestScenarioName = context.getCurrentScenario().getName();
        ((JavascriptExecutor) driver).executeScript(
                "'{\"elastestCommand\": \"startTest\", \"args\": {\"testName\": \""
                        + currentTestScenarioName + "\"} }'");

        logger.info("##### Start test: {}", currentTestScenarioName);
    }

    public void afterScenario(ExecutionContext context) {
        currentTestScenarioName = context.getCurrentScenario().getName();
        logger.info("##### Finish test: {}", currentTestScenarioName);
    }

}