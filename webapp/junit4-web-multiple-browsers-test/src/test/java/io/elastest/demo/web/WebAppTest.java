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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.net.MalformedURLException;

import org.junit.Test;
import org.openqa.selenium.By;

public class WebAppTest extends ElastestBaseTest {

    @Test
    public void addMsgAndClear()
            throws InterruptedException, MalformedURLException {
        Thread.sleep(2000);

        String newTitle = "MessageTitle";
        String newBody = "MessageBody";

        this.addRow(newTitle, newBody);

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

        assertNotEquals(0, titleExist);
        assertNotEquals(0, bodyExist);

        Thread.sleep(2000);

    }

    @Test
    public void findTitleAndBody()
            throws InterruptedException, MalformedURLException {
        Thread.sleep(2000);

        String newTitle = "MessageTitle";
        String newBody = "MessageBody";

        this.addRow(newTitle, newBody);

        Thread.sleep(2000);

        String title = driver.findElement(By.id("title")).getText();
        String body = driver.findElement(By.id("body")).getText();

        logger.info("Checking Message...");

        assertEquals(newTitle, title);
        assertEquals(newBody, body);

        Thread.sleep(2000);

    }

    @Test
    public void checkTitleAndBodyNoEmpty()
            throws InterruptedException, MalformedURLException {
        Thread.sleep(2000);

        String newTitle = "";
        String newBody = "";

        this.addRow(newTitle, newBody);

        Thread.sleep(2000);

        String title = driver.findElement(By.id("title")).getText();
        String body = driver.findElement(By.id("body")).getText();

        logger.info("Checking Message...");

        assertNotEquals(newTitle, title);
        assertNotEquals(newBody, body);

        Thread.sleep(2000);

    }

    public void addRow(String newTitle, String newBody)
            throws InterruptedException {
        driver.findElement(By.id("title-input")).sendKeys(newTitle);
        driver.findElement(By.id("body-input")).sendKeys(newBody);

        Thread.sleep(2000);

        logger.info("Adding Message...");

        driver.findElement(By.id("submit")).click();
    }

}
