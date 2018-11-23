package io.elastest.demo.web;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import com.thoughtworks.gauge.ExecutionContext;
import com.thoughtworks.gauge.Step;

public class WebAppTest extends ElastestBaseTest {
    String newTitle;
    String newBody;

    @BeforeScenario()
    public void beforeScenario(ExecutionContext context) {
        super.beforeScenario(context);
    }

    @AfterScenario()
    public void afterScenario(ExecutionContext context) {
        super.afterScenario(context);
    }

    /* ************************ */
    /* ******** Common ******** */
    /* ************************ */
    @Step("Navigate to app url")
    public void navigateToAppUrl() throws MalformedURLException {
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

        driver.get(sutUrl);
    }

    /* *************************************** */
    /* **** Check title and body no empty **** */
    /* *************************************** */

    @Step("Add an empty title and body")
    public void addAnEmptyTitleAndBody() throws InterruptedException {
        Thread.sleep(2000);

        newTitle = "";
        newBody = "";

        this.addRow(newTitle, newBody);

        Thread.sleep(2000);
    }

    @Step("Check that row with empty title and body has been added")
    public void checkThatRowWithEmptyTitleAndBodyHasBeenAdded()
            throws InterruptedException {

        String title = driver.findElement(By.id("title")).getText();
        String body = driver.findElement(By.id("body")).getText();

        logger.info("Checking Message...");

        try {
            assertThat(title, not(equalTo(newTitle)));
            assertThat(body, not(equalTo(newBody)));
        } finally {
            Thread.sleep(2000);
            clearRows();
        }
    }

    /* *************************************** */
    /* ********* Find title and body ********* */
    /* *************************************** */

    @Step("Add a row with title and body")
    public void addARowWithTitleAndBody() throws InterruptedException {
        Thread.sleep(2000);

        newTitle = "MessageTitle";
        newBody = "MessageBody";

        this.addRow(newTitle, newBody);
        Thread.sleep(2000);
    }

    @Step("Check that row with the same title and body has been added")
    public void CheckThatRowWithTheSameTitleAndBodyHasBeenAdded()
            throws InterruptedException {

        String title = driver.findElement(By.id("title")).getText();
        String body = driver.findElement(By.id("body")).getText();

        // Added
        logger.info("Checking Message...");

        try {
            assertThat(title, equalTo(newTitle));
            assertThat(body, equalTo(newBody));
        } finally {
            Thread.sleep(2000);
            clearRows();
        }
    }

    /* ********************** */
    /* *** Common methods *** */
    /* ********************** */

    public void addRow(String newTitle, String newBody)
            throws InterruptedException {
        driver.findElement(By.id("title-input")).sendKeys(newTitle);
        driver.findElement(By.id("body-input")).sendKeys(newBody);

        Thread.sleep(2000);

        logger.info("Adding Message...");

        driver.findElement(By.id("submit")).click();
    }

    public void clearRows() throws InterruptedException {
        logger.info("Clearing Messages...");
        driver.findElement(By.id("clearSubmit")).click();
        Thread.sleep(1000);
    }
}