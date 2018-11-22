package io.elastest.demo.web;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;

public class ElastestBaseTest {
    protected static final Logger logger = LoggerFactory
            .getLogger(WebAppTestDefinition.class);

    protected String currentTestScenarioName;
    protected static String eusURL;
    protected static String sutUrl;

    protected WebDriver driver;

    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";

    protected static String browserType;
    protected static String browserVersion;

    @Before
    public void beforeScenario(Scenario scenario) {
        currentTestScenarioName = scenario.getName();

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

    @After
    public void afterScenario(Scenario scenario) {
        if (driver != null) {
            driver.quit();
        }

        currentTestScenarioName = scenario.getName();
        logger.info("##### Finish test: {}", currentTestScenarioName);
    }

}
