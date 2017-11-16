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
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.ChromeDriverManager;

public class WebAppTest {

	private static String eusURL;
	private static String sutURL;

	private WebDriver driver;

	@BeforeAll
	public static void setupAllTests() {

		String sutHost = System.getenv("ET_SUT_HOST");
		if (sutHost == null) {
			sutURL = "http://localhost:8080/";
		} else {
			sutURL = "http://" + sutHost + ":8080/";
		}
		System.out.println("App url: " + sutURL);
		
		eusURL = System.getenv("ET_EUS_API");
		if (eusURL == null) {
			ChromeDriverManager.getInstance().setup();
		}
	}

	@BeforeEach
	public void setupTest() throws MalformedURLException {

		ChromeOptions options = new ChromeOptions();
		
		options.addArguments("start-maximized");
		
		if (eusURL == null) {
			driver = new ChromeDriver(options);
		} else {			
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setBrowserName("chrome");
			caps.setCapability(ChromeOptions.CAPABILITY, options);					
			driver = new RemoteWebDriver(new URL(eusURL), caps);
		}
	}

	@AfterEach
	public void teardown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	public void test() throws InterruptedException {

		driver.get(sutURL);

		Thread.sleep(3000);

		String newTitle = "MessageTitle";
		String newBody = "MessageBody";

		driver.findElement(By.id("title-input")).sendKeys(newTitle);
		driver.findElement(By.id("body-input")).sendKeys(newBody);

		Thread.sleep(3000);

		driver.findElement(By.id("submit")).click();

		Thread.sleep(3000);

		String title = driver.findElement(By.id("title")).getText();
		String body = driver.findElement(By.id("body")).getText();

		assertThat(title, equalTo(newTitle));
		assertThat(body, equalTo(newBody));

		Thread.sleep(3000);
	}

}
