package io.elastest.sendPostTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;

/**
 * Unit test for simple App.
 */
public class SendPostTest {
    private static final Logger logger = LoggerFactory
            .getLogger(SendPostTest.class);

    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";

    private static String browserType;
    private static String eusURL;
    private static String sutUrl;
    private static String endpointUrl = "http://localhost:37000/logstash/";

    private static Long sleep = (long) 3000;

    private WebDriver driver;

    @BeforeAll
    public static void setupClass() throws Exception {

        browserType = System.getProperty("browser");

        System.out.println("Browser Type: " + browserType);

        if (System.getenv("SLEEP") != null) {
            sleep = Long.parseLong(System.getenv("SLEEP"));
        }
        eusURL = System.getenv("ET_EUS_API");
        endpointUrl = System.getenv("ET_MON_LSHTTPS_API");
        if (endpointUrl == null) {
            String msg = "No endpoint url received";
            logger.error("Error: {}", msg);
            throw new Exception(msg);
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

        sutUrl += "?url=" + URLEncoder.encode(endpointUrl, "UTF-8");
        ;

        String exec = System.getenv("ET_MON_EXEC");
        if (exec != null) {
            sutUrl += "&exec=" + exec;
        }

        System.out.println("App URL: " + sutUrl);
    }

    // @BeforeEach
    public void setupTest(String testName) throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("start-maximized");

        if (eusURL == null) {
            driver = new ChromeDriver(options);
        } else {
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setBrowserName("chrome");
            caps.setCapability(ChromeOptions.CAPABILITY, options);
            caps.setCapability("browserId", testName);
            driver = new RemoteWebDriver(new URL(eusURL), caps);
        }
    }

    @AfterEach
    public void teardown(TestInfo info) {
        String testName = info.getDisplayName();
        testName = testName.replaceAll("\\(", "").replaceAll("\\)", "");

        if (driver != null) {
            logger.info("##### Finish test: {}", testName);

            driver.quit();
        }
    }

    @Test
    public void testOpenApp()
            throws InterruptedException, MalformedURLException {
        String testName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        logger.info("##### Start test: {}", testName);
        this.setupTest(testName);

        driver.get(sutUrl);

        Thread.sleep(sleep);
    }

}
