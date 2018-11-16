package io.elastest.demo.unit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasTestBase {

    protected static final Logger logger = LoggerFactory
            .getLogger(ElasTestBase.class);

    @BeforeEach
    public void logStart(TestInfo testInfo) {
        logger.info("##### Start test: " + testInfo.getTestMethod().get().getName());
    }

    @AfterEach
    public void logEnd(TestInfo testInfo) {
        logger.info("##### Finish test: " + testInfo.getTestMethod().get().getName());
    }
}
