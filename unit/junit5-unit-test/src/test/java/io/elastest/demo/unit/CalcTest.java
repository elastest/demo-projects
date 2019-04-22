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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CalcTest extends ElasTestBase {

    private Calc calc;

    static int left = 3;
    static int right = 2;
    static int expectedSumResult = 5;
    static int expectedSubResult = 1;

    static final String LEFT_KEY = "LEFT";
    static final String RIGHT_KEY = "RIGHT";
    static final String EXPECTED_SUM_RESULT_KEY = "EXPECTED_SUM_RESULT";
    static final String EXPECTED_SUB_RESULT_KEY = "EXPECTED_SUB_RESULT";

    @BeforeEach
    public void setup() {
        this.calc = new Calc();

        if (System.getenv(LEFT_KEY) != null
                && !"".equals(System.getenv(LEFT_KEY))

                && System.getenv(RIGHT_KEY) != null
                && !"".equals(System.getenv(RIGHT_KEY))

                && System.getenv(EXPECTED_SUM_RESULT_KEY) != null
                && !"".equals(System.getenv(EXPECTED_SUM_RESULT_KEY))

                && System.getenv(EXPECTED_SUB_RESULT_KEY) != null
                && !"".equals(System.getenv(EXPECTED_SUB_RESULT_KEY))) {
            left = Integer.parseInt(System.getenv(LEFT_KEY));
            right = Integer.parseInt(System.getenv(RIGHT_KEY));

            expectedSumResult = Integer
                    .parseInt(System.getenv(EXPECTED_SUM_RESULT_KEY));
            expectedSubResult = Integer
                    .parseInt(System.getenv(EXPECTED_SUB_RESULT_KEY));

            logger.info("Using passed values:");
            logger.info("left: {}", left);
            logger.info("right: {}", right);
            logger.info("expected Sum result: {}", expectedSumResult);
            logger.info("expected Sub result: {}", expectedSubResult);
        } else {
            logger.info("Using default values:");
        }
    }

    @Test
    public void sumTest() {
        logger.info("Checking if {} + {} = {}", left, right, expectedSumResult);
        assertEquals(expectedSumResult, calc.sum(left, right));
    }

    @Test
    public void subTest() {
        logger.info("Checking if {} + {} = {}", left, right, expectedSubResult);
        assertEquals(expectedSubResult, calc.sub(left, right));
    }
}
