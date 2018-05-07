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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;

public class WikiTest {
    private static final Logger logger = LoggerFactory
            .getLogger(WikiTest.class);

    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";

    private static String browserType;
    private static String eusURL;
    private static String sutUrl;

    private WebDriver driver;

    @BeforeAll
    public static void setupClass() {

        browserType = System.getProperty("browser");

        System.out.println("Browser Type: " + browserType);

        eusURL = System.getenv("ET_EUS_API");
        if (eusURL == null) {
            
            if (browserType == null || browserType.equals(CHROME)) {
                ChromeDriverManager.getInstance().setup();
            } else {
                FirefoxDriverManager.getInstance().setup();
            }
        }

        String sutHost = System.getenv("ET_SUT_HOST");        
        if (sutHost == null) {
            sutUrl = System.getProperty("etSuUrl") != null ? System.getProperty("etSutUrl") : "https://www.wikipedia.org/";
        } else {
            sutUrl = "http://" + sutHost + ":8080/";
        }
        System.out.println("Webapp URL: " + sutUrl);
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
    public void testWiki() throws InterruptedException, MalformedURLException {
        String testName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        logger.info("##### Start test: {}", testName);
        this.setupTest(testName);
        driver.get(sutUrl);
        
        driver.findElement(By.id("searchInput")).sendKeys("Testing");
        driver.findElement(By.id("searchInput")).sendKeys(Keys.RETURN);
        
        Thread.sleep(2000);

    }

}
