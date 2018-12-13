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

package io.openvidu.test.e2e;

import static java.lang.System.getProperty;
import static java.lang.invoke.MethodHandles.lookup;
import static org.openqa.selenium.OutputType.BASE64;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

import io.github.bonigarcia.SeleniumExtension;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.Session;
import io.openvidu.test.e2e.browser.BrowserUser;
import io.openvidu.test.e2e.browser.ChromeUser;
import io.openvidu.test.e2e.browser.FirefoxUser;

/**
 * E2E tests for openvidu-testapp.
 *
 * @author Pablo Fuente (pablo.fuente@urjc.es)
 * @since 1.1.1
 */
@Tag("e2e")
@DisplayName("E2E tests for OpenVidu TestApp")
@ExtendWith(SeleniumExtension.class)
public class OpenViduTestAppE2eTest {

    static String OPENVIDU_SECRET = "MY_SECRET";
    static String OPENVIDU_URL = "https://localhost:4443/";
    static String APP_URL = "https://localhost:4443/";
    static Exception ex = null;

    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";

    private static String BROWSER;

    final static Logger log = getLogger(lookup().lookupClass());

    BrowserUser user;

    @BeforeAll()
    static void setupAll() {

        if (System.getenv("ET_EUS_API") == null) {
            // Outside ElasTest
            ChromeDriverManager.getInstance().setup();
            FirefoxDriverManager.getInstance().setup();
        }

        String appURL = System.getenv("ET_SUT_HOST");
        if (appURL != null) {
            String port = System.getenv("ET_SUT_PORT");
            if (port == null) {
                port = "4443";
            }

            String protocol = System.getenv("ET_SUT_PROTOCOL");
            if (protocol == null) {
                protocol = "https";
            }

            appURL = protocol + "://" + appURL + ":" + port;
            APP_URL = appURL;
            OPENVIDU_URL = "https://" + appURL + ":4443";
        }

        String appUrl = getProperty("app.url");
        if (appUrl != null) {
            APP_URL = appUrl;
        }

        log.info("Using URL {} to connect to openvidu-testapp", APP_URL);

        String openviduUrl = getProperty("openvidu.url");
        if (openviduUrl != null) {
            OPENVIDU_URL = openviduUrl;
        }
        log.info("Using URL {} to connect to openvidu-server", OPENVIDU_URL);

        String openvidusecret = getProperty("openvidu.secret");
        if (openvidusecret != null) {
            OPENVIDU_SECRET = openvidusecret;
        }
        log.info("Using secret {} to connect to openvidu-server",
                OPENVIDU_SECRET);

        BROWSER = System.getenv("BROWSER");

        if ((BROWSER == null) || (!BROWSER.equals(FIREFOX))) {
            BROWSER = CHROME;
        }
    }

    void setupBrowser(String browser, TestInfo testInfo, int secondsOfWait) {

        switch (browser) {
        case "chrome":
            this.user = new ChromeUser("TestUser", secondsOfWait,
                    testInfo.getTestMethod().get().getName());
            break;
        case "firefox":
            this.user = new FirefoxUser("TestUser", secondsOfWait,
                    testInfo.getTestMethod().get().getName());
            break;
        default:
            this.user = new ChromeUser("TestUser", secondsOfWait,
                    testInfo.getTestMethod().get().getName());
        }

        user.getDriver().get(APP_URL);

        sleep(5000);

        WebElement urlInput = user.getDriver()
                .findElement(By.id("openvidu-url"));
        urlInput.clear();
        urlInput.sendKeys(OPENVIDU_URL);
        WebElement secretInput = user.getDriver()
                .findElement(By.id("openvidu-secret"));
        secretInput.clear();
        secretInput.sendKeys(OPENVIDU_SECRET);

        user.getEventManager().startPolling();
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
    }

    @AfterEach
    void dispose(TestInfo info) {
        try {
            user.dispose();
        } finally {
            log.info("##### Finish test: "
                    + info.getTestMethod().get().getName());
        }
    }

    @Test
    // @DisplayName("One2One Chrome [Video + Audio]")
    void oneToOneVideoAudioSessionChrome(TestInfo info) throws Exception {
        log.info("##### Start test: " + info.getTestMethod().get().getName());
        setupBrowser(BROWSER, info, 50);

        log.info("One2One Chrome [Video + Audio]");

        user.getDriver().findElement(By.id("auto-join-checkbox")).click();
        user.getDriver().findElement(By.id("one2one-btn")).click();

        user.getEventManager().waitUntilNumberOfEvent("streamPlaying", 4);

        log.info("Waiting for video recording");
        Thread.sleep(10000);

        try {
            System.out.println(getBase64Screenshot(user));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(user.getEventManager().assertMediaTracks(
                user.getDriver().findElements(By.tagName("video")), true,
                true));

        user.getDriver().findElement(By.id("remove-user-btn")).click();

        user.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 1);
        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);

        user.getDriver().findElement(By.id("remove-user-btn")).click();

        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 2);

        user.dispose();
    }

    /*
     * @Test
     * 
     * @DisplayName("One2One Firefox [Video + Audio]") void
     * oneToOneVideoAudioSessionFirefox(TestInfo info) throws Exception {
     * log.info("##### Start test: " + info.getTestMethod().get().getName());
     * 
     * setupBrowser("firefox", info, 50);
     * log.info("One2One Firefox [Video + Audio]");
     * 
     * user.getDriver().findElement(By.id("auto-join-checkbox")).click();
     * user.getDriver().findElement(By.id("one2one-btn")).click();
     * 
     * user.getEventManager().waitUntilNumberOfEvent("videoPlaying", 4);
     * 
     * try { System.out.println(getBase64Screenshot(user)); } catch (Exception
     * e) { e.printStackTrace(); }
     * 
     * Assert.assertTrue(user.getEventManager().assertMediaTracks(user.getDriver
     * ().findElements(By.tagName("video")), true, true));
     * 
     * user.getDriver().findElement(By.id("remove-user-btn")).click();
     * 
     * user.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 1);
     * user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);
     * 
     * user.getDriver().findElement(By.id("remove-user-btn")).click();
     * 
     * user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 2);
     * 
     * user.dispose(); }
     */

    @Test
    // @DisplayName("One2One [Audio]")
    @Disabled
    void oneToOneAudioSession(TestInfo info) throws Exception {
        log.info("##### Start test: " + info.getTestMethod().get().getName());

        setupBrowser("chrome", info, 50);

        log.info("One2One [Audio]");

        user.getDriver().findElement(By.id("one2one-btn")).click();

        List<WebElement> l1 = user.getDriver()
                .findElements(By.className("send-video-checkbox"));
        for (WebElement el : l1) {
            el.click();
        }

        List<WebElement> l2 = user.getDriver()
                .findElements(By.className("join-btn"));
        for (WebElement el : l2) {
            el.click();
        }

        user.getEventManager().waitUntilNumberOfEvent("connectionCreated", 4);
        user.getEventManager().waitUntilNumberOfEvent("accessAllowed", 2);
        user.getEventManager().waitUntilNumberOfEvent("videoElementCreated", 4);
        user.getEventManager().waitUntilNumberOfEvent("streamCreated", 1);
        user.getEventManager().waitUntilNumberOfEvent("videoPlaying", 4);

        try {
            System.out.println(getBase64Screenshot(user));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(user.getEventManager().assertMediaTracks(
                user.getDriver().findElements(By.tagName("video")), true,
                false));

        user.getDriver().findElement(By.id("remove-user-btn")).click();

        user.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 1);
        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);

        user.getDriver().findElement(By.id("remove-user-btn")).click();

        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 2);
    }

    @Test
    // @DisplayName("One2One [Video]")
    @Disabled
    void oneToOneVideoSession(TestInfo info) throws Exception {
        log.info("##### Start test: " + info.getTestMethod().get().getName());

        setupBrowser("chrome", info, 50);

        log.info("One2One [Video]");

        user.getDriver().findElement(By.id("one2one-btn")).click();

        List<WebElement> l1 = user.getDriver()
                .findElements(By.className("send-audio-checkbox"));
        for (WebElement el : l1) {
            el.click();
        }

        List<WebElement> l2 = user.getDriver()
                .findElements(By.className("join-btn"));
        for (WebElement el : l2) {
            el.click();
        }

        user.getEventManager().waitUntilNumberOfEvent("connectionCreated", 4);
        user.getEventManager().waitUntilNumberOfEvent("accessAllowed", 2);
        user.getEventManager().waitUntilNumberOfEvent("videoElementCreated", 4);
        user.getEventManager().waitUntilNumberOfEvent("streamCreated", 1);
        user.getEventManager().waitUntilNumberOfEvent("videoPlaying", 4);

        try {
            System.out.println(getBase64Screenshot(user));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(user.getEventManager().assertMediaTracks(
                user.getDriver().findElements(By.tagName("video")), false,
                true));

        user.getDriver().findElement(By.id("remove-user-btn")).click();

        user.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 1);
        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);

        user.getDriver().findElement(By.id("remove-user-btn")).click();

        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 2);
    }

    @Test
    // @DisplayName("One2Many [Video + Audio]")
    @Disabled
    void oneToManyVideoAudioSession(TestInfo info) throws Exception {
        log.info("##### Start test: " + info.getTestMethod().get().getName());

        setupBrowser("chrome", info, 50);

        log.info("One2Many [Video + Audio]");

        user.getDriver().findElement(By.id("auto-join-checkbox")).click();
        user.getDriver().findElement(By.id("one2many-btn")).click();

        user.getEventManager().waitUntilNumberOfEvent("videoPlaying", 4);

        try {
            System.out.println(getBase64Screenshot(user));
        } catch (Exception e) {
            e.printStackTrace();
        }

        user.getEventManager().assertMediaTracks(
                user.getDriver().findElements(By.tagName("video")), true, true);

        user.getDriver().findElements(By.className(("leave-btn"))).get(0)
                .click();

        user.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 3);
    }

    @Test
    // @DisplayName("Unique user remote subscription [Video + Audio]")
    @Disabled
    void oneRemoteSubscription(TestInfo info) throws Exception {
        log.info("##### Start test: " + info.getTestMethod().get().getName());

        setupBrowser("chrome", info, 50);
        log.info("Unique user remote subscription [Video + Audio]");

        user.getDriver().findElement(By.id("add-user-btn")).click();
        user.getDriver().findElement(By.className("subscribe-remote-check"))
                .click();
        user.getDriver().findElement(By.className("join-btn")).click();

        user.getEventManager().waitUntilNumberOfEvent("connectionCreated", 1);
        user.getEventManager().waitUntilNumberOfEvent("accessAllowed", 1);
        user.getEventManager().waitUntilNumberOfEvent("videoElementCreated", 1);
        user.getEventManager().waitUntilNumberOfEvent("remoteVideoPlaying", 1);

        try {
            System.out.println(getBase64Screenshot(user));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(user.getEventManager().assertMediaTracks(
                user.getDriver().findElements(By.tagName("video")), true,
                true));

        user.getDriver().findElement(By.className(("leave-btn"))).click();

        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);
    }

    @Test
    // @DisplayName("Unique user remote subscription [ScreenShare + Audio]")
    @Disabled
    void oneRemoteSubscriptionScreen(TestInfo info) throws Exception {
        log.info("##### Start test: " + info.getTestMethod().get().getName());

        setupBrowser("chrome", info, 50);
        log.info("Unique user remote subscription [ScreenShare + Audio]");

        user.getDriver().findElement(By.id("add-user-btn")).click();
        user.getDriver().findElement(By.className("screen-radio")).click();
        user.getDriver().findElement(By.className("subscribe-remote-check"))
                .click();
        user.getDriver().findElement(By.className("join-btn")).click();

        user.getEventManager().waitUntilNumberOfEvent("connectionCreated", 1);
        user.getEventManager().waitUntilNumberOfEvent("accessAllowed", 1);
        user.getEventManager().waitUntilNumberOfEvent("videoElementCreated", 1);
        user.getEventManager().waitUntilNumberOfEvent("remoteVideoPlaying", 1);

        try {
            System.out.println(getBase64Screenshot(user));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(user.getEventManager().assertMediaTracks(
                user.getDriver().findElements(By.tagName("video")), true,
                true));

        user.getDriver().findElement(By.className(("leave-btn"))).click();

        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);
    }

    @Test
    // @DisplayName("Many2Many [Video + Audio]")
    @Disabled
    void manyToManyVideoAudioSession(TestInfo info) throws Exception {
        log.info("##### Start test: " + info.getTestMethod().get().getName());

        setupBrowser("chrome", info, 50);
        log.info("Many2Many [Video + Audio]");

        WebElement addUser = user.getDriver()
                .findElement(By.id("add-user-btn"));
        for (int i = 0; i < 4; i++) {
            addUser.click();
        }

        List<WebElement> l = user.getDriver()
                .findElements(By.className("join-btn"));
        for (WebElement el : l) {
            el.sendKeys(Keys.ENTER);
        }

        user.getEventManager().waitUntilNumberOfEvent("connectionCreated", 16);
        user.getEventManager().waitUntilNumberOfEvent("accessAllowed", 4);
        user.getEventManager().waitUntilNumberOfEvent("videoElementCreated",
                16);
        user.getEventManager().waitUntilNumberOfEvent("streamCreated", 6);
        user.getEventManager().waitUntilNumberOfEvent("videoPlaying", 16);

        try {
            System.out.println(getBase64Screenshot(user));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(user.getEventManager().assertMediaTracks(
                user.getDriver().findElements(By.tagName("video")), true,
                true));

        user.getDriver().findElement(By.id(("remove-user-btn")))
                .sendKeys(Keys.ENTER);
        user.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 3);
        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);

        user.getDriver().findElement(By.id(("remove-user-btn")))
                .sendKeys(Keys.ENTER);
        user.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 4);
        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 2);

        user.getDriver().findElement(By.id(("remove-user-btn")))
                .sendKeys(Keys.ENTER);
        user.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 5);
        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 3);

        user.getDriver().findElement(By.id(("remove-user-btn")))
                .sendKeys(Keys.ENTER);
        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 4);
    }

    @Test
    // @DisplayName("Secure Test")
    @Disabled
    void secureTest(TestInfo info) throws Exception {
        log.info("##### Start test: " + info.getTestMethod().get().getName());

        setupBrowser("chrome", info, 50);
        log.info("Secure Test");

        WebElement addUser = user.getDriver()
                .findElement(By.id("add-user-btn"));
        for (int i = 0; i < 4; i++) {
            addUser.click();
        }

        OpenVidu OV = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
        Session session = OV.createSession();
        String sessionId = session.getSessionId();

        List<WebElement> l1 = user.getDriver()
                .findElements(By.className("secure-session-checkbox"));
        for (WebElement el : l1) {
            el.click();
        }

        List<WebElement> l2 = user.getDriver()
                .findElements(By.className("sessionIdInput"));
        for (WebElement el : l2) {
            el.sendKeys(sessionId);
        }

        List<WebElement> l3 = user.getDriver()
                .findElements(By.className("tokenInput"));
        for (WebElement el : l3) {
            String token = session.generateToken();
            el.sendKeys(token);
        }

        List<WebElement> l4 = user.getDriver()
                .findElements(By.className("join-btn"));
        for (WebElement el : l4) {
            el.sendKeys(Keys.ENTER);
        }

        user.getEventManager().waitUntilNumberOfEvent("connectionCreated", 16);
        user.getEventManager().waitUntilNumberOfEvent("accessAllowed", 4);
        user.getEventManager().waitUntilNumberOfEvent("videoElementCreated",
                16);
        user.getEventManager().waitUntilNumberOfEvent("streamCreated", 6);
        user.getEventManager().waitUntilNumberOfEvent("videoPlaying", 16);

        try {
            System.out.println(getBase64Screenshot(user));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(user.getEventManager().assertMediaTracks(
                user.getDriver().findElements(By.tagName("video")), true,
                true));

        user.getDriver().findElement(By.id(("remove-user-btn")))
                .sendKeys(Keys.ENTER);
        user.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 3);
        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);

        user.getDriver().findElement(By.id(("remove-user-btn")))
                .sendKeys(Keys.ENTER);
        user.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 4);
        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 2);

        user.getDriver().findElement(By.id(("remove-user-btn")))
                .sendKeys(Keys.ENTER);
        user.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 5);
        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 3);

        user.getDriver().findElement(By.id(("remove-user-btn")))
                .sendKeys(Keys.ENTER);
        user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 4);
    }

    /*
     * @Test
     * 
     * @DisplayName("Cross-Browser test") void crossBrowserTest(TestInfo info)
     * throws Exception { log.info("##### Start test: " +
     * info.getTestMethod().get().getName());
     * 
     * setupBrowser("chrome", info, 50);
     * 
     * log.info("Cross-Browser test");
     * 
     * Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler()
     * { public void uncaughtException(Thread th, Throwable ex) {
     * System.out.println("Uncaught exception: " + ex); synchronized (lock) {
     * OpenViduTestAppE2eTest.ex = new Exception(ex); } } };
     * 
     * Thread t = new Thread(() -> { BrowserUser user2 = new
     * FirefoxUser("TestUser", 30); user2.getDriver().get(APP_URL); WebElement
     * urlInput = user2.getDriver().findElement(By.id("openvidu-url"));
     * urlInput.clear(); urlInput.sendKeys(OPENVIDU_URL); WebElement secretInput
     * = user2.getDriver().findElement(By.id("openvidu-secret"));
     * secretInput.clear(); secretInput.sendKeys(OPENVIDU_SECRET);
     * 
     * user2.getEventManager().startPolling();
     * 
     * user2.getDriver().findElement(By.id("add-user-btn")).click();
     * user2.getDriver().findElement(By.className("join-btn")).click(); try {
     * user2.getEventManager().waitUntilNumberOfEvent("videoPlaying", 2);
     * Assert.assertTrue(user2.getEventManager()
     * .assertMediaTracks(user2.getDriver().findElements(By.tagName("video")),
     * true, true));
     * user2.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 1);
     * user2.getDriver().findElement(By.id("remove-user-btn")).click();
     * user2.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);
     * } catch (Exception e) { e.printStackTrace();
     * Thread.currentThread().interrupt(); } user2.dispose(); });
     * t.setUncaughtExceptionHandler(h); t.start();
     * 
     * user.getDriver().findElement(By.id("add-user-btn")).click();
     * user.getDriver().findElement(By.className("join-btn")).click();
     * 
     * user.getEventManager().waitUntilNumberOfEvent("videoPlaying", 2);
     * 
     * try { System.out.println(getBase64Screenshot(user)); } catch (Exception
     * e) { e.printStackTrace(); }
     * 
     * Assert.assertTrue(user.getEventManager().assertMediaTracks(user.getDriver
     * ().findElements(By.tagName("video")), true, true));
     * 
     * user.getDriver().findElement(By.id("remove-user-btn")).click();
     * 
     * user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);
     * 
     * t.join();
     * 
     * synchronized (lock) { if (OpenViduTestAppE2eTest.ex != null) { throw
     * OpenViduTestAppE2eTest.ex; } } }
     */

    private String getBase64Screenshot(BrowserUser user) throws Exception {
        String screenshotBase64 = ((TakesScreenshot) user.getDriver())
                .getScreenshotAs(BASE64);
        return "data:image/png;base64," + screenshotBase64;
    }

}
