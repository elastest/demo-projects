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
package io.elastest.demo.proxytest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ProxyTest extends ElastestBaseTest {

    @Test
    public void testProxy(TestInfo info)
            throws InterruptedException, MalformedURLException {

        WebElement ip = driver.findElement(By.xpath(
                "//div/table[1]/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[1]/td[2]"));

        boolean throughProxy = ip != null && ip.getText() != null
                && ip.getText().equals(proxyUrl);

        try {
            assertTrue(throughProxy);
        } finally {
            Thread.sleep(2000);
        }
    }

}
