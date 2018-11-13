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
package io.elastest.demo.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class RestAppTest extends ElasTestBase {

    @Test
    public void rootServiceTest() {
        String appHost = System.getenv("ET_SUT_HOST");

        if (appHost == null) {
            appHost = "localhost";
        }

        RestTemplate client = new RestTemplate();
        String url = "http://" + appHost + ":8080/";
        logger.info("Send GET request to {}", url);
        String result = client.getForObject(url, String.class);

        assertEquals("Hello World!", result);
    }

}
