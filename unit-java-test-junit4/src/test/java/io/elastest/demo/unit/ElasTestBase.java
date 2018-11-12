package io.elastest.demo.unit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasTestBase {

    protected static final Logger logger = LoggerFactory
            .getLogger(ElasTestBase.class);

    @Rule
    public TestName name = new TestName();

    @Before
    public void logStart() {
        logger.info("##### Start test: " + name.getMethodName());
    }

    @After
    public void logEnd() {
        logger.info("##### Finish test: " + name.getMethodName());
    }
}
