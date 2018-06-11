package io.elastest.sendPostTest;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class SendPostTest extends TestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(SendPostTest.class);

    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";

    private static String browserType;
    private static String eusURL;
    private static String sutUrl;
    private static String endpointUrl;

    private static Long sleep = (long) 3000;

    private WebDriver driver;

    @BeforeAll
    public static void setupClass() throws Exception {

        browserType = System.getProperty("browser");

        System.out.println("Browser Type: " + browserType);

        sleep = Long.parseLong(System.getenv("SLEEP"));
        eusURL = System.getenv("ET_EUS_API");
        endpointUrl = System.getenv("ET_MON_LSHTTPS_API");
        if (endpointUrl == null) {
            throw new Exception("No endpoint url received");
        }
        if (eusURL == null) {

            if (browserType == null || browserType.equals(CHROME)) {
                ChromeDriverManager.getInstance().setup();
            } else {
                FirefoxDriverManager.getInstance().setup();
            }
        }

        String sutHost = System.getenv("ET_SUT_HOST");
        if (sutHost == null) {
            sutUrl = "http://localhost:4200/#/";
        } else {
            sutUrl = "http://" + sutHost + ":4200/#/";
        }

        sutUrl += "?url=" + endpointUrl;

        String exec = System.getenv("ET_MON_EXEC");
        if (exec != null) {
            sutUrl += "&exec=" + exec;
        }

        System.out.println("App URL: " + sutUrl);
    }

    // @BeforeEach
    public void setupTest(String testName) throws MalformedURLException {
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
            caps.setCapability("browserId", testName);

            driver = new RemoteWebDriver(new URL(eusURL), caps);
        }
    }

    @AfterEach
    public void teardown(TestInfo info) {
        String testName = info.getDisplayName();
        testName = testName.replaceAll("\\(", "").replaceAll("\\)", "");

        if (driver != null) {
            logger.info("Clearing Messages...");
            driver.findElement(By.id("clearSubmit")).click();

            logger.info("##### Finish test: {}", testName);

            driver.quit();
        }
    }

    @Test
    public void openApp()
            throws InterruptedException, MalformedURLException {
        String testName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        logger.info("##### Start test: {}", testName);
        this.setupTest(testName);

        driver.get(sutUrl);

        Thread.sleep(sleep);
    }

}
