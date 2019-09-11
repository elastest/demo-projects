package io.elastest.demo.unit;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasTestBase {

    protected static final Logger logger = LoggerFactory
            .getLogger(ElasTestBase.class);

    @BeforeEach
    public void logStart(TestInfo testInfo) throws InterruptedException {
        logger.info("##### Start test: "
                + testInfo.getTestMethod().get().getName());
        sleep(1);
    }

    @AfterEach
    public void logEnd(TestInfo testInfo) {
        sleep(1);
        logger.info("##### Finish test: "
                + testInfo.getTestMethod().get().getName());
    }

    public void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
