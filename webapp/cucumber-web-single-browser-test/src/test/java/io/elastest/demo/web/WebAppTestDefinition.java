/*
 * (C) Copyright 2017-2019 ElasTest (http://elastest.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

// With a browser for all test
public class WebAppTestDefinition {
    private static final Logger logger = LoggerFactory
            .getLogger(WebAppTestDefinition.class);

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
    @Before("@firstScenario")
    public void beforeFeature() throws MalformedURLException {
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

            driver = new RemoteWebDriver(new URL(eusURL), caps);
        }

    }

    @Before
    public void beforeScenario(Scenario scenario) {
        currentTestScenarioName = scenario.getName();
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript(
                    "'<<##et => {\"command\": \"startTest\", \"args\": {\"testName\": \""
                            + currentTestScenarioName + "\"} }>>'");
        }
        logger.info("##### Start test: {}", currentTestScenarioName);
    }

    @After
    public void afterScenario(Scenario scenario) {
        currentTestScenarioName = scenario.getName();
        if (driver != null) {
            logger.info("Clearing Messages...");
            driver.findElement(By.id("clearSubmit")).click();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        logger.info("##### Finish test: {}", currentTestScenarioName);
    }

    // Hack because @AfterClass cannot be used
    @After("@lastScenario")
    public void afterFeature() {
        if (driver != null) {
            driver.quit();
        }
    }

    /* ************************ */
    /* ******** Common ******** */
    /* ************************ */

    @Given("^app url$")
    public void app_url() throws Throwable {
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

        assertThat(title, equalTo(newTitle));
        assertThat(body, equalTo(newBody));

        Thread.sleep(1000);
    }

    public void addRow(String testName, String newTitle, String newBody)
            throws InterruptedException {
        driver.findElement(By.id("title-input")).sendKeys(newTitle);
        driver.findElement(By.id("body-input")).sendKeys(newBody);

        Thread.sleep(2000);

        logger.info("Adding Message...");

        driver.findElement(By.id("submit")).click();
    }

}
