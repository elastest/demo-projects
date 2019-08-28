package io.elastest.demo.web;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ElastestBaseTest {
    protected static final Logger logger = LoggerFactory
            .getLogger(ElastestBaseTest.class);

    protected static boolean USE_AWS_BROWSER = false;

    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";

    protected static String browserType;
    protected static String browserVersion;
    protected static String eusURL;
    protected static String sutUrl;

    protected WebDriver driver;

    protected static JsonObject awsConfig;

    @BeforeAll
    public static void setupClass() {
        Boolean useAWSBrowser = Boolean
                .valueOf(System.getenv("USE_AWS_BROWSER"));

        if (useAWSBrowser != null) {
            USE_AWS_BROWSER = useAWSBrowser;
        }

        if (USE_AWS_BROWSER) {
            initAwsConfig();
        }

        String sutHost = System.getenv("ET_SUT_HOST");
        String sutPort = System.getenv("ET_SUT_PORT");
        String sutProtocol = System.getenv("ET_SUT_PROTOCOL");

        if (sutHost == null) {
            sutUrl = "http://localhost:8080/";
        } else {
            sutProtocol = sutProtocol != null ? sutProtocol : "http";
            sutUrl = sutProtocol + "://" + sutHost;
            if (sutPort != null && !"".equals(sutPort)) {
                sutUrl += ":" + sutPort;
            }
        }
        logger.info("Sut URL: " + sutUrl);

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
    }

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setupTest(TestInfo info)
            throws JsonParseException, JsonMappingException, IOException {
        String testName = info.getTestMethod().get().getName();
        logger.info("##### Start test: {}", testName);

        if (eusURL == null) {
            if (browserType == null || browserType.equals(CHROME)) {
                driver = new ChromeDriver();
            } else {
                driver = new FirefoxDriver();
            }
        } else {
            logger.info("Using ElasTest EUS URL: {}", eusURL);

            DesiredCapabilities caps;
            if (browserType == null || browserType.equals(CHROME)) {
                caps = DesiredCapabilities.chrome();
            } else {
                caps = DesiredCapabilities.firefox();
            }

            browserVersion = System.getProperty("browserVersion");
            if (browserVersion != null) {
                logger.info("Browser Version: {}", browserVersion);
                caps.setVersion(browserVersion);
            }

            caps.setCapability("testName", testName);

            if (USE_AWS_BROWSER) {
                // AWS capabilities for browsers
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> awsConfigMap = mapper
                        .readValue(awsConfig.toString(), Map.class);

                caps.setCapability("awsConfig", awsConfigMap);
            }

            driver = new RemoteWebDriver(new URL(eusURL), caps);
        }

        driver.get(sutUrl);
    }

    @AfterEach
    public void teardown(TestInfo info) {
        if (driver != null) {
            driver.quit();
        }

        String testName = info.getTestMethod().get().getName();
        logger.info("##### Finish test: {}", testName);
    }

    public static void initAwsConfig() {
        /* *************************************** */
        /* *********** AWS config init *********** */
        /* *************************************** */

        // Aws Config
        String region = System.getenv("AWS_REGION");
        String secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        String sshUser = System.getenv("AWS_SSH_USER");
        String sshPrivateKey = System.getenv("AWS_SSH_PRIVATE_KEY");

        // Instances config
        String awsAmiId = System.getenv("AWS_AMI_ID");
        String instanceType = System.getenv("AWS_INSTANCE_TYPE");
        String keyName = System.getenv("AWS_KEY_NAME");
        String securityGroups = System.getenv("AWS_SECURITY_GROUPS");
        String tagSpecifications = System.getenv("AWS_TAG_SPECIFICATIONS");
        int numInstances = 1;

        if (System.getenv("AWS_NUM_INSTANCES") != null) {
            numInstances = Integer.parseInt(System.getenv("AWS_NUM_INSTANCES"));
        }

        JsonParser parser = new JsonParser();
        awsConfig = new JsonObject();

        awsConfig.addProperty("region", region);
        awsConfig.addProperty("secretAccessKey", secretAccessKey);
        awsConfig.addProperty("accessKeyId", accessKeyId);
        awsConfig.addProperty("sshUser", sshUser);
        awsConfig.addProperty("sshPrivateKey",
                sshPrivateKey.replace("\\r\\n", System.lineSeparator()));

        // Instances Config

        JsonObject awsInstancesConfig = new JsonObject();
        awsInstancesConfig.addProperty("amiId", awsAmiId);
        awsInstancesConfig.addProperty("instanceType", instanceType);
        awsInstancesConfig.addProperty("keyName", keyName);

        awsInstancesConfig.addProperty("numInstances", numInstances);
        JsonArray securityGroupsElement = parser.parse(securityGroups)
                .getAsJsonArray();
        awsInstancesConfig.add("securityGroups", securityGroupsElement);

        JsonArray tagSpecificationsElement = parser.parse(tagSpecifications)
                .getAsJsonArray();
        awsInstancesConfig.add("tagSpecifications", tagSpecificationsElement);
        awsConfig.add("awsInstancesConfig", awsInstancesConfig);

        logger.info("AWS Config: {}", awsConfig);
    }
}
