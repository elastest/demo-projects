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
package io.elastest.demo.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalcTest {

    private Calc calc;
    private static final Logger logger = LoggerFactory
            .getLogger(CalcTest.class);

    public void init(String testName) {
        logger.info("##### Start test: " + testName);
        this.calc = new Calc();
    }

    public void end(String testName) {
        logger.info("##### Finish test: " + testName);
    }

    @org.junit.Test
    public void sumTest() {
        String testName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        init(testName);
        try {
            int left = 3;
            int right = 2;
            int expectedResult = 5;
            logger.info("Checking if {} + {} = {}", left, right,
                    expectedResult);
            assertThat(calc.sum(left, right)).isEqualTo(expectedResult);
        } finally {
            end(testName);
        }
    }

}
