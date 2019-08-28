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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.net.MalformedURLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

// Uses a browser for each test
public class ExampleOrgTest extends ElastestBaseTest {

    @Test
    public void findTitleAndBody(TestInfo info)
            throws InterruptedException, MalformedURLException {
        String h1TitleExpected = "Example Domain";
        String exampleDomainTitleXPath = "//*/h1[contains(., '"
                + h1TitleExpected + "')]";

        logger.info("Waiting for Title loaded...");
        WebDriverWait waitService = new WebDriverWait(driver, 25);
        By elementAvailable = By.xpath(exampleDomainTitleXPath);

        waitService.until(presenceOfElementLocated(elementAvailable));

        String h1TitleReceived = driver.findElement(elementAvailable).getText();

        Thread.sleep(1500);

        logger.info(
                "Checking If Received Title ({}) is equals to Expected ({})...",
                h1TitleReceived, h1TitleExpected);
        try {
            assertEquals(h1TitleExpected, h1TitleReceived);
        } finally {
            Thread.sleep(1200);
        }
    }

}
