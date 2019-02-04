package io.elastest.proxytest;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
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

    @BeforeAll
    public static void setupClass() throws MalformedURLException {
        // If first time, init
        if (driver == null) {
            String sutHost = System.getenv("ET_SUT_HOST");
            String sutPort = System.getenv("ET_SUT_PORT");
            String sutProtocol = System.getenv("ET_SUT_PROTOCOL");

            String proxyUrl = System.getProperty("PROXY_URL");

            if (sutHost == null) {
                sutUrl = "https://elastest.io/docs/";
            } else {
                sutProtocol = sutProtocol != null ? sutProtocol : "http";

                sutUrl = sutProtocol + "://" + sutHost
                        + (sutPort != null ? ":" + sutPort : "");
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
                logger.info("Using ElasTest EUS URL: {}", eusURL);

                DesiredCapabilities caps;
                if (browserType == null || browserType.equals(CHROME)) {
                    caps = DesiredCapabilities.chrome();
                } else {
                    caps = DesiredCapabilities.firefox();
                }

                if (proxyUrl != null) {
                    Proxy proxy = new Proxy();
                    proxy.setHttpProxy(proxyUrl);
                    logger.info("Using proxy url: {}", proxyUrl);
                    caps.setCapability("proxy", proxy);
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
        logger.info("##### Finish test: {}", testName);
    }
}
