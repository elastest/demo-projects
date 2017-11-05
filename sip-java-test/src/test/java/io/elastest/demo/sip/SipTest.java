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
package io.elastest.demo.sip;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

public class SipTest {

	@Test
	public void test() throws InterruptedException, IOException {

		String sutIp = System.getenv("ET_SUT_HOST");
		if (sutIp == null) {
			System.err.println("Environment ET_SUT_HOST is mandatory to execute SIP Test");
			System.exit(1);
		}
		
		String command = "sipp -nostdin -sn uac -m 1 " + sutIp;
		
		System.out.println("Executing command: "+command);

		Process p = exec(command);

		try {

			p.waitFor();

			assertThat(p.exitValue()).isEqualTo(0);

		} finally {
			p.destroy();
		}
	}

	public Process exec(String command) throws IOException {
		Process p = Runtime.getRuntime().exec(command);
		redirectToConsole(p.getInputStream(), false);
		redirectToConsole(p.getInputStream(), true);
		return p;
	}

	public void redirectToConsole(InputStream is, boolean err) {
		new Thread(()->{
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(is));
			
			String line = null;

			try {
				
				while ((line = stdInput.readLine()) != null) {
					if(err) {
						System.err.println(line);
					} else {
						System.out.println(line);	
					}					
				}
			
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}).start();
	}
}
