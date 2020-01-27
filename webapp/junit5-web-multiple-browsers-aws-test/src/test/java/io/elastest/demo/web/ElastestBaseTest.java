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
    protected static final Logger logger = LoggerFactory.getLogger(ElastestBaseTest.class);

    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";

    protected static String browserType;
    protected static String browserVersion;
    protected static String eusURL;
    protected static String sutUrl;

    protected static JsonObject awsConfig;

    protected WebDriver driver;

    protected static final String AWS_AMI_ID = "ami-0bfc646d9bb6ad37c";
    protected static final int AWS_NUM_INSTANCES = 1;
    protected static final String AWS_TAG_SPECIFICATIONS = "[ { \"resourceType\":\"instance\", \"tags\":[ {\"key\":\"Type\", \"value\":\"OpenViduLoadTest\"} ] } ]";
    protected static final String AWS_INSTANCE_TYPE = "t2.xlarge";
    protected static final String AWS_SSH_USER = "ubuntu";
    protected static final String AWS_REGION = "eu-west-1";

    @BeforeAll
    public static void setupClass() {
        String sutHost = System.getenv("ET_SUT_HOST");
        String sutPort = System.getenv("ET_SUT_PORT");
        String sutProtocol = System.getenv("ET_SUT_PROTOCOL");

        if (sutHost == null) {
            sutUrl = "http://localhost:8080/";
        } else {
            sutPort = sutPort != null ? sutPort : "8080";
            sutProtocol = sutProtocol != null ? sutProtocol : "http";

            sutUrl = sutProtocol + "://" + sutHost + ":" + sutPort;
        }
        logger.info("Webapp URL: " + sutUrl);

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

        initAwsConfig();
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
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
            caps.setCapability("elastestTimeout", 3600);

            // AWS capabilities for browsers
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> awsConfigMap = mapper.readValue(awsConfig.toString(), Map.class);

            caps.setCapability("awsConfig", awsConfigMap);

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

    public static JsonObject initAwsConfig() {
        awsConfig = new JsonObject();

        // Aws Config
        String secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        String sshPrivateKey = System.getenv("AWS_SSH_PRIVATE_KEY");

        // Instances config
        String keyName = System.getenv("AWS_KEY_NAME");
        String securityGroups = System.getenv("AWS_SECURITY_GROUPS");

        JsonParser parser = new JsonParser();
        awsConfig.addProperty("region", AWS_REGION);
        awsConfig.addProperty("secretAccessKey", secretAccessKey);
        awsConfig.addProperty("accessKeyId", accessKeyId);
        awsConfig.addProperty("sshUser", AWS_SSH_USER);
        awsConfig.addProperty("sshPrivateKey",
                sshPrivateKey.replace("\\r\\n", System.lineSeparator()));

        // Instances Config

        JsonObject awsInstancesConfig = new JsonObject();
        awsInstancesConfig.addProperty("amiId", AWS_AMI_ID);
        awsInstancesConfig.addProperty("instanceType", AWS_INSTANCE_TYPE);
        awsInstancesConfig.addProperty("keyName", keyName);

        awsInstancesConfig.addProperty("numInstances", AWS_NUM_INSTANCES);
        JsonArray securityGroupsElement = parser.parse(securityGroups).getAsJsonArray();
        awsInstancesConfig.add("securityGroups", securityGroupsElement);

        JsonArray tagSpecificationsElement = parser.parse(AWS_TAG_SPECIFICATIONS).getAsJsonArray();
        awsInstancesConfig.add("tagSpecifications", tagSpecificationsElement);
        awsConfig.add("awsInstancesConfig", awsInstancesConfig);
        return awsConfig;
    }

}
