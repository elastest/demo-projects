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


public class CalcTest2 {

	private Calc calc;
	
	@BeforeEach
	public void init() {
		this.calc = new Calc();
	}
	
	@Test
	public void sumTestOK() {
		assertThat(calc.sum(3,2)).isEqualTo(5);
	}
	
	@Test
    public void sumTestKO() {
        assertThat(calc.sum(3,3)).isEqualTo(5);
    }
}
