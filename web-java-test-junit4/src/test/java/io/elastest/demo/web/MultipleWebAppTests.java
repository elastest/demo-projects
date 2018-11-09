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

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MultipleWebAppTests {
    private static final Logger logger = LoggerFactory
            .getLogger(MultipleWebAppTests.class);

    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";

    private static final String etMonitorMarkPrefix = "##elastest-monitor-mark:";

    private static String browserType;
    private static String browserVersion;
    private static String eusURL;
    private static String sutUrl;

    private WebDriver driver;

    @BeforeClass
    public static void setupClass() {

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

    public void setupTest(String testName) throws MalformedURLException {
        logger.info("##### Start test: {}", testName);

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

            caps.setCapability("testName", testName);

            logger.info(etMonitorMarkPrefix
                    + " id=action, value=Start Browser Session for "
                    + testName);
            driver = new RemoteWebDriver(new URL(eusURL), caps);
        }

        driver.get(sutUrl);
    }

    public void teardown(String testName) {
        testName = testName.replaceAll("\\(", "").replaceAll("\\)", "");

        if (driver != null) {
            logger.info("Clearing Messages...");
            driver.findElement(By.id("clearSubmit")).click();

            logger.info("##### Finish test: {}", testName);

            driver.quit();
        }
    }

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

    @Test
    public void addMsgAndClear()
            throws InterruptedException, MalformedURLException {
        String testName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        this.setupTest(testName);

        try {
            Thread.sleep(2000);

            String newTitle = "MessageTitle";
            String newBody = "MessageBody";

            this.addRow(testName, newTitle, newBody);

            Thread.sleep(2000);

            String title = driver.findElement(By.id("title")).getText();
            String body = driver.findElement(By.id("body")).getText();

            // Added
            logger.info("Checking Message...");
            assertThat(title, equalTo(newTitle));
            assertThat(body, equalTo(newBody));

            Thread.sleep(1000);

            int titleExist = driver.findElements(By.id("title")).size();
            int bodyExist = driver.findElements(By.id("body")).size();

            logger.info(etMonitorMarkPrefix + " id=action, value=Assert ("
                    + testName + ")");
            assertThat(titleExist, not(equalTo(0)));
            assertThat(bodyExist, not(equalTo(0)));

            Thread.sleep(2000);
        } finally {
            teardown(testName);
        }
    }

    @Test
    public void findTitleAndBody()
            throws InterruptedException, MalformedURLException {
        String testName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        this.setupTest(testName);

        try {
            Thread.sleep(2000);

            String newTitle = "MessageTitle";
            String newBody = "MessageBody";

            this.addRow(testName, newTitle, newBody);

            Thread.sleep(2000);

            String title = driver.findElement(By.id("title")).getText();
            String body = driver.findElement(By.id("body")).getText();

            logger.info("Checking Message...");
            logger.info(etMonitorMarkPrefix + " id=action, value=Assert ("
                    + testName + ")");
            assertThat(title, equalTo(newTitle));
            assertThat(body, equalTo(newBody));

            Thread.sleep(2000);
        } finally {
            teardown(testName);
        }
    }

    @Test
    public void checkTitleAndBodyNoEmpty()
            throws InterruptedException, MalformedURLException {
        String testName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        this.setupTest(testName);

        try {
            Thread.sleep(2000);

            String newTitle = "";
            String newBody = "";

            this.addRow(testName, newTitle, newBody);

            Thread.sleep(2000);

            String title = driver.findElement(By.id("title")).getText();
            String body = driver.findElement(By.id("body")).getText();

            logger.info("Checking Message...");
            logger.info(etMonitorMarkPrefix + " id=action, value=Assert ("
                    + testName + ")");
            assertThat(title, not(equalTo(newTitle)));
            assertThat(body, not(equalTo(newBody)));

            Thread.sleep(2000);
        } finally {
            teardown(testName);
        }
    }

}