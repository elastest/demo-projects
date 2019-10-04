package io.elastest.qe.openvidu;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.elastest.qe.utils.CountDownLatchWithException;
import io.elastest.qe.utils.MonitoringManager;
import io.elastest.qe.utils.CountDownLatchWithException.AbortedException;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {
    protected static final Logger logger = getLogger(lookup().lookupClass());

    public static int USERS_BY_SESSION = 3;
    public static int MAX_SESSIONS = 2;

    public static String OPENVIDU_SECRET = "MY_SECRET";
    protected static String OPENVIDU_SUT_URL;
    protected static String OPENVIDU_WEBAPP_URL;

    public static int SECONDS_OF_WAIT = 40;
    public static int BROWSER_POLL_INTERVAL = 1000;

    protected static String EUS_URL;
    protected static String ET_ETM_TJOB_ATTACHMENT_API;

    protected static List<BrowserClient> browserClientList;

    protected static JsonObject awsConfig;

    protected static boolean isDevelopment = false;
    public static MonitoringManager monitoringManager;

    public static void initParameters() {
        String openviduSecret = System.getenv("OPENVIDU_SECRET");
        String sessions = System.getenv("MAX_SESSIONS");
        String usersSession = System.getenv("USERS_BY_SESSION");
        String secondsOfWait = System.getenv("SECONDS_OF_WAIT");
        String browserPollInterval = System.getenv("BROWSER_POLL_INTERVAL");

        if (openviduSecret != null) {
            OPENVIDU_SECRET = openviduSecret;
        }
        if (sessions != null) {
            MAX_SESSIONS = Integer.parseInt(sessions);
        }
        if (usersSession != null) {
            USERS_BY_SESSION = Integer.parseInt(usersSession);
        }
        if (secondsOfWait != null) {
            SECONDS_OF_WAIT = Integer.parseInt(secondsOfWait);
        }

        if (browserPollInterval != null) {
            BROWSER_POLL_INTERVAL = Integer.parseInt(browserPollInterval);
        }

    }

    @BeforeAll
    public static void setupClass() throws Exception {
        initParameters();
        browserClientList = new ArrayList<>();

        ET_ETM_TJOB_ATTACHMENT_API = System
                .getenv("ET_ETM_TJOB_ATTACHMENT_API");

        /* *********************************** */
        /* ******** Openvidu Sut init ******** */
        /* *********************************** */

        String sutHost = System.getenv("ET_SUT_HOST");
        String sutPort = System.getenv("ET_SUT_PORT");
        String sutProtocol = System.getenv("ET_SUT_PROTOCOL");

        if (sutHost != null) {
            sutPort = sutPort != null ? sutPort : "4443";
            sutProtocol = sutProtocol != null ? sutProtocol : "https";

            OPENVIDU_SUT_URL = sutProtocol + "://" + sutHost + ":" + sutPort;
            OPENVIDU_WEBAPP_URL = sutProtocol + "://" + sutHost;
        } else {
            throw new Exception("No Sut URL");
        }

        logger.info("OpenVidu Sut URL: {}", OPENVIDU_SUT_URL);
        logger.info("OpenVidu Webapp URL: {}", OPENVIDU_WEBAPP_URL);

        /* ************************************ */
        /* ************* EUS init ************* */
        /* ************************************ */
        EUS_URL = System.getenv("ET_EUS_API");

        if (EUS_URL == null) {
            logger.warn("NOT Using EUS URL");
            WebDriverManager.chromedriver().setup();
        } else {
            logger.info("Using EUS URL: {}", EUS_URL);
        }

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
        int numInstances = Integer.parseInt(System.getenv("AWS_NUM_INSTANCES"));

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

        if (OPENVIDU_WEBAPP_URL == null || OPENVIDU_WEBAPP_URL.isEmpty()) {
            throw new Exception(
                    "OpenVidu WebApp Url is empty, probably because the stack was not obtained correctly");
        }

        monitoringManager = new MonitoringManager();
        logger.info("Configured new Monitoring Manager: {}", monitoringManager);
    }

    @BeforeEach
    public void setupTest(TestInfo info) {
        String testName = info.getTestMethod().get().getName();
        logger.info("##### Start test: {}", testName);
    }

    @AfterEach
    public void teardown(TestInfo info) {
        if (browserClientList != null) {
            ExecutorService browserDisposeTaskExecutor = Executors
                    .newCachedThreadPool();
            CountDownLatchWithException waitForBrowsersEndLatch = new CountDownLatchWithException(
                    browserClientList.size());
            List<Runnable> browserThreads = new ArrayList<>();
            for (BrowserClient browserClient : browserClientList) {
                if (browserClient != null) {
                    browserThreads.add(() -> {
                        browserClient.dispose();
                        waitForBrowsersEndLatch.countDown();
                    });
                }
            }

            for (Runnable r : browserThreads) {
                browserDisposeTaskExecutor.execute(r);
            }

            try {
                waitForBrowsersEndLatch.await();
            } catch (AbortedException e1) {
            }

            browserDisposeTaskExecutor.shutdown();
            try {
                browserDisposeTaskExecutor.awaitTermination(5,
                        TimeUnit.MINUTES);
            } catch (InterruptedException e) {
            }
        }

        String testName = info.getTestMethod().get().getName();
        logger.info("##### Finish test: {}", testName);
        browserClientList = new ArrayList<>();

    }

    @AfterAll
    public static void clear() {

    }

    public static String convertStreamToString(InputStream in)
            throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringbuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringbuilder.append(line + "\n");
        }
        in.close();
        return stringbuilder.toString();
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}
