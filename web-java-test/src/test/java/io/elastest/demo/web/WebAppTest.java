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

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

// Uses a browser for all test
public class WebAppTest extends BaseTest {

    @BeforeAll
    public static void setupClass() throws MalformedURLException {

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
            driver = new RemoteWebDriver(new URL(eusURL), caps);
        }
    }

    @BeforeEach
    public void setupTest(TestInfo info) throws MalformedURLException {
        String testName = info.getTestMethod().get().getName();
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript(
                    "<<##et => {\"command\": \"startTest\", \"args\": {\"testName\": \""
                            + testName + "\"} }>>");
        }

        logger.info("##### Start test: {}", testName);
        logger.info(etMonitorMarkPrefix
                + " id=action, value=Start Browser Session for " + testName);

        driver.get(sutUrl);
    }

    @AfterEach
    public void teardown(TestInfo info) {
        String testName = info.getTestMethod().get().getName();
        testName = testName.replaceAll("\\(", "").replaceAll("\\)", "");

        if (driver != null) {
            logger.info("Clearing Messages...");
            driver.findElement(By.id("clearSubmit")).click();

            logger.info("##### Finish test: {}", testName);
        }
    }

    @AfterAll
    public static void afterAll() {
        if (driver != null) {
            driver.quit();
        }
    }

}
