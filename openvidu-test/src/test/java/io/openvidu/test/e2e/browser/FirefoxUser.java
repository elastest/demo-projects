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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class FirefoxUser extends BrowserUser {

    public FirefoxUser(String userName, int timeOfWaitInSeconds,
            String testName) {
        super(userName, timeOfWaitInSeconds);

        FirefoxProfile profile = new FirefoxProfile();
        // This flag avoids granting the access to the camera
        profile.setPreference("media.navigator.permission.disabled", true);
        // This flag force to use fake user media (synthetic video of multiple
        // color)
        profile.setPreference("media.navigator.streams.fake", true);

        String eusApiURL = System.getenv("ET_EUS_API");

        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setVersion("57");
        capabilities.setCapability("acceptInsecureCerts", true);
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);

        if (eusApiURL == null) {
            this.driver = new FirefoxDriver(capabilities);
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
                throw new RuntimeException("Exception creaing eusApiURL", e);
            }
        }

        this.driver.manage().timeouts()
                .setScriptTimeout(this.timeOfWaitInSeconds, TimeUnit.SECONDS);

        this.configureDriver();
    }

}
