package io.elastest.demo.web;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ElastestBaseTest {
    @Rule
    public TestName name = new TestName();

    protected static final Logger logger = LoggerFactory
            .getLogger(ElastestBaseTest.class);

    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";

    protected static String browserType;
    protected static String browserVersion;
    protected static String eusURL;
    protected static String sutUrl;

    protected WebDriver driver;

    @BeforeClass
    public static void setupClass() {
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
        logger.info("Webapp URL: " + sutUrl);

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
    }

    @Before
    public void beforeEach() throws MalformedURLException {
        String testName = name.getMethodName();
        logger.info("##### Start test: {}", testName);

        browserVersion = System.getProperty("browserVersion");

        if (eusURL == null) {
            if (browserType == null || browserType.equals(CHROME)) {
                driver = new ChromeDriver();
            } else {
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

            if (browserVersion != null) {
                logger.info("Browser Version: {}", browserVersion);
                caps.setVersion(browserVersion);
            }

            caps.setCapability("testName", testName);

            driver = new RemoteWebDriver(new URL(eusURL), caps);
        }

        driver.get(sutUrl);
    }

    @After
    public void afterEach() {
        if (driver != null) {
            driver.quit();
        }

        String testName = name.getMethodName();
        logger.info("##### Finish test: {}", testName);
    }

}
