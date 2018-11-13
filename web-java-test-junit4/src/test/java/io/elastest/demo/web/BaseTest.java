package io.elastest.demo.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.net.MalformedURLException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {
    @Rule
    public TestName name = new TestName();

    protected static final Logger logger = LoggerFactory
            .getLogger(MultipleWebAppTests.class);

    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";

    protected static final String etMonitorMarkPrefix = "##elastest-monitor-mark:";

    protected static String browserType;
    protected static String browserVersion;
    protected static String eusURL;
    protected static String sutUrl;

    protected static WebDriver driver;

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

    public void teardown() {
        String testName = name.getMethodName();

        if (driver != null) {
            logger.info("Clearing Messages...");
            driver.findElement(By.id("clearSubmit")).click();

            logger.info("##### Finish test: {}", testName);
        }
    }

    @Test
    public void addMsgAndClear()
            throws InterruptedException, MalformedURLException {
        String testName = name.getMethodName();

        Thread.sleep(2000);

        String newTitle = "MessageTitle";
        String newBody = "MessageBody";

        this.addRow(testName, newTitle, newBody);

        Thread.sleep(2000);

        String title = driver.findElement(By.id("title")).getText();
        String body = driver.findElement(By.id("body")).getText();

        // Added
        logger.info("Checking Message...");
        assertEquals(newTitle, title);
        assertEquals(newBody, body);

        Thread.sleep(1000);

        int titleExist = driver.findElements(By.id("title")).size();
        int bodyExist = driver.findElements(By.id("body")).size();

        logger.info(etMonitorMarkPrefix + " id=action, value=Assert ("
                + testName + ")");
        assertNotEquals(0, titleExist);
        assertNotEquals(0, bodyExist);

        Thread.sleep(2000);

    }

    @Test
    public void findTitleAndBody()
            throws InterruptedException, MalformedURLException {
        String testName = name.getMethodName();

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
        assertEquals(newTitle, title);
        assertEquals(newBody, body);

        Thread.sleep(2000);

    }

    @Test
    public void checkTitleAndBodyNoEmpty()
            throws InterruptedException, MalformedURLException {
        String testName = name.getMethodName();

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
        assertNotEquals(newTitle, title);
        assertNotEquals(newBody, body);

        Thread.sleep(2000);

    }
}
