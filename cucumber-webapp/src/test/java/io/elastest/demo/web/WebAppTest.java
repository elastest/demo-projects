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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;

// With a browser for each test
public class WebAppTest {
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

    @BeforeClass
    public void beforeFeature() {
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
        if (sutHost == null) {
            sutUrl = "http://localhost:8080/";
        } else {
            sutUrl = "http://" + sutHost + ":8080/";
        }
        System.out.println("Webapp URL: " + sutUrl);
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        currentTestScenarioName = scenario.getName();
        logger.info("##### Start test: {}", currentTestScenarioName);
    }

    @After
    public void afterScenario(Scenario scenario) {
        currentTestScenarioName = scenario.getName();
        // testName = testName.replaceAll("\\(", "").replaceAll("\\)", "");

        if (driver != null) {
            logger.info("Clearing Messages...");
            driver.findElement(By.id("clearSubmit")).click();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            logger.info("##### Finish test: {}", currentTestScenarioName);
        }
    }

    @AfterClass
    public void afterFeature() {
        if (driver != null) {
            driver.quit();
        }
    }
}
