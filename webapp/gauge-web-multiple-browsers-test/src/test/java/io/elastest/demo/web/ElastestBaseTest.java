package io.elastest.demo.web;

import org.openqa.selenium.WebDriver;
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

    protected WebDriver driver;
    protected String currentTestScenarioName;
    
    
    public void beforeScenario(ExecutionContext context) {
        currentTestScenarioName = context.getCurrentScenario().getName();

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
            } else {
                WebDriverManager.firefoxdriver().setup();
            }
        }

        logger.info("##### Start test: {}", currentTestScenarioName);
    }

    public void afterScenario(ExecutionContext context) {
        if (driver != null) {
            driver.quit();
        }

        currentTestScenarioName = context.getCurrentScenario().getName();
        logger.info("##### Finish test: {}", currentTestScenarioName);
    }
}