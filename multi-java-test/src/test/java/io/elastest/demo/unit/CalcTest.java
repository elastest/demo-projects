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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalcTest {
    private static final String etMonitorMarkPrefix = "##elastest-monitor-mark:";

    private int leftOperand = 3;
    private int rightOperand = 2;

    private Calc calc;
    private static final Logger logger = LoggerFactory
            .getLogger(CalcTest.class);

    @BeforeEach
    public void init() {
        String leftOperandString = System.getenv("LEFT_OPERAND");
        if (leftOperandString != null) {
            leftOperand = Integer.valueOf(leftOperandString);
        }

        String rightOperandString = System.getenv("RIGHT_OPERAND");
        if (rightOperandString != null) {
            rightOperand = Integer.valueOf(rightOperandString);
        }

        this.calc = new Calc();
    }

    @AfterEach
    public void end() {
        try {
            logger.info("End");
            Thread.sleep(800);
        } catch (Exception e) {
        }
    }

    @Test
    public void subTest() {
        logger.info("##### Start test: {}", new Object() {
        }.getClass().getEnclosingMethod().getName());
        try {
            logger.info("Left operand: {}", leftOperand);
            logger.info("Right operand: {}", rightOperand);
            logger.info(etMonitorMarkPrefix + " id=action, value=Sub");
            assertThat(calc.sub(leftOperand, rightOperand)).isEqualTo(1);
        } finally {
            logger.info("##### Finish test: {}", new Object() {
            }.getClass().getEnclosingMethod().getName());
        }
    }

    @Test
    public void sumTest() {
        logger.info("##### Start test: {}", new Object() {
        }.getClass().getEnclosingMethod().getName());
        try {
            logger.info("Left operand: {}", leftOperand);
            logger.info("Right operand: {}", rightOperand);
            logger.info(etMonitorMarkPrefix + " id=action, value=Sum");
            assertThat(calc.sum(leftOperand, rightOperand)).isEqualTo(5);
        } finally {
            logger.info("##### Finish test: {}", new Object() {
            }.getClass().getEnclosingMethod().getName());
        }
    }

}
