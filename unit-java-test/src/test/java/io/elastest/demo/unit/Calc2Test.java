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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Calc2Test {
    private static final Logger logger = LoggerFactory
            .getLogger(Calc2Test.class);

    private Calc calc;

    @BeforeEach
    public void init() {
        this.calc = new Calc();
    }

    @Test
    public void sumTestOK() {
        logger.info("##### Start test: {}", new Object() {
        }.getClass().getEnclosingMethod().getName());
        try {
            logger.warn("This is a warn msg to test");
            assertThat(calc.sum(3, 2)).isEqualTo(5);
        } finally {
            logger.info("##### Finish test: {}", new Object() {
            }.getClass().getEnclosingMethod().getName());
        }
    }

    @Test
    public void sumTestKO() {
        logger.info("##### Start test: {}", new Object() {
        }.getClass().getEnclosingMethod().getName());
        try {
            logger.warn("This is a warn msg to test");
            logger.warn("This is other warn msg to test");
            assertThat(calc.sum(3, 3)).isEqualTo(5);
        } finally {
            logger.info("##### Finish test: {}", new Object() {
            }.getClass().getEnclosingMethod().getName());
        }
    }

    @Test
    public void sumStringTestKO() {
        logger.info("##### Start test: {}", new Object() {
        }.getClass().getEnclosingMethod().getName());
        try {
            logger.warn("This is a warn msg to test");
            assertThat(calc.sum("a", "b")).isEqualTo(5);
        } finally {
            logger.info("##### Finish test: {}", new Object() {
            }.getClass().getEnclosingMethod().getName());
        }

    }
}