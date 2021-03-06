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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CalcTest extends ElasTestBase {

    private Calc calc;

    static int left = 3;
    static int right = 2;

    @Before
    public void setup() {
        this.calc = new Calc();
    }

    @Test
    public void sumTest() {
        
        int expectedResult = 5;
        logger.info("Checking if {} + {} = {}", left, right, expectedResult);
        assertEquals(expectedResult, calc.sum(left, right));
    }

    @Test
    public void subTest() {

        int expectedResult = 1;
        logger.info("Checking if {} + {} = {}", left, right, expectedResult);
        assertEquals(expectedResult, calc.sub(left, right));
    }
}
