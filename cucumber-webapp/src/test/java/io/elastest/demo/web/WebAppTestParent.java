package io.elastest.demo.web;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class WebAppTestParent {
    protected static final Logger logger = LoggerFactory
            .getLogger(MultipleWebAppTests.class);

    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";

    protected static final String etMonitorMarkPrefix = "##elastest-monitor-mark:";

    protected static String browserType;
    protected static String browserVersion;
    protected static String eusURL;
    protected static String sutUrl;

    protected WebDriver driver;

    // Test variables
    protected String newTitle;
    protected String newBody;
    protected String currentTestScenarioName;

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

    @Given("^app url$")
    public void app_url() throws Throwable {
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

            logger.info(etMonitorMarkPrefix
                    + " id=action, value=Start Browser Session for "
                    + currentTestScenarioName);
            driver = new RemoteWebDriver(new URL(eusURL), caps);
        }

        driver.get(sutUrl);
    }

    /* *************************************** */
    /* **** Check title and body no empty **** */
    /* *************************************** */

    @When("^i add an empty title and body$")
    public void i_add_an_empty_title_and_body() throws Throwable {
        Thread.sleep(2000);

        newTitle = "";
        newBody = "";

        this.addRow(currentTestScenarioName, newTitle, newBody);

        Thread.sleep(2000);
    }

    @Then("^row with empty title and body added$")
    public void row_with_empty_title_and_body_added() throws Throwable {

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

    @When("^i add a row with title and body$")
    public void i_add_a_row_with_title_and_body() throws Throwable {
        Thread.sleep(2000);

        newTitle = "MessageTitle";
        newBody = "MessageBody";

        this.addRow(currentTestScenarioName, newTitle, newBody);
        Thread.sleep(2000);
    }

    @Then("^row with the same title and body added$")
    public void row_with_the_same_title_and_body_added() throws Throwable {

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
