/*
 * (C) Copyright 2017 OpenVidu (http://openvidu.io/)
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

package io.openvidu.test.e2e.browser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.core.io.ClassPathResource;

public class ChromeUser extends BrowserUser {

    public ChromeUser(String userName, int timeOfWaitInSeconds,
            String testName) {
        super(userName, timeOfWaitInSeconds);

        ChromeOptions options = new ChromeOptions();
        // This flag avoids to grant the user media
        options.addArguments("--use-fake-ui-for-media-stream");
        // This flag fakes user media with synthetic video
        options.addArguments("--use-fake-device-for-media-stream");
        // This flag selects the entire screen as video source when screen
        // sharing
        options.addArguments(
                "--auto-select-desktop-capture-source=Entire screen");
        // Disable CORS
        options.addArguments("--test-type");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
//        options.addArguments("--auto-open-devtools-for-tabs");

        try {
            // Add Screen Sharing extension
            options.addExtensions(
                    new ClassPathResource("ScreenCapturing.crx").getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String eusApiURL = System.getenv("ET_EUS_API");

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        if (eusApiURL == null) {
            this.driver = new ChromeDriver(capabilities);
        } else {
            try {

                String browserVersion = System.getProperty("browserVersion");
                if (browserVersion != null) {
                    capabilities.setVersion(browserVersion);
                }
                capabilities.setCapability("testName", testName);
                this.driver = new RemoteWebDriver(new URL(eusApiURL),
                        capabilities);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Exception creating eusApiURL", e);
            }
        }

        this.driver.manage().timeouts()
                .setScriptTimeout(this.timeOfWaitInSeconds, TimeUnit.SECONDS);

        this.configureDriver();
    }

}
