package com.custommetricstest;

import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.net.HttpURLConnection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import com.sun.management.OperatingSystemMXBean;

public class CustomMetrics extends ElasTestBase {

	protected static final Logger logger = getLogger(lookup().lookupClass());

	protected static String urlLogstash;
	protected static String execId;
	protected static String containerName;
	protected static String counterStr;
	protected static OperatingSystemMXBean osBean;
	protected static boolean credentials = false;
	protected static String username;
	protected static String password;

	@BeforeAll
	public static void setupClass() {
		osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

		counterStr = System.getenv("COUNTER");
		urlLogstash = System.getenv("ET_MON_LSHTTP_API");
		execId = System.getenv("ET_MON_EXEC");
		username = System.getenv("CREDENTIALS_NAME");
		password = System.getenv("CREDENTIALS_PASS");

		if (username != null && password != null) {
			credentials = true;
			logger.info("Logstash with credentials");
		} else {
			logger.info("Logstash without credentials");
		}

		if (counterStr == null) {
			counterStr = "10";
		}

		logger.info("\n-Logstash Ip: " + urlLogstash + "\n-Execution Identifier: " + execId + "\n-Generate "
				+ counterStr + " prime numbers");
	}

	@Test
	public void test() {
		int counter = Integer.parseInt(counterStr);

		while (counter >= 0) {
			BigInteger prime = BigInteger.probablePrime(1024, new Random()); // Stress CPU

			logger.info("Generate prime number: " + prime.toString());

			double javaCpu = osBean.getProcessCpuLoad() * 100;
			double totalCpu = osBean.getSystemCpuLoad() * 100;

			try {
				sendPostToLogstash(generateMetric("component1", "use_cpu", javaCpu, "Java_usage_CPU", "%", totalCpu,
						"Total_usage_CPU", "%"));
			} catch (Exception e) {
				logger.info("Error when send information to logstash");
				e.printStackTrace();
			}

			sleep(5000);
			counter--;
		}
	}

	public String generateMetric(String component, String stream, double v1, String s1, String u1, double v2, String s2,
			String u2) {
		String trace = "{" + "\"" + s1 + "\": " + v1 + "," + "\"" + s2 + "\": " + v2 + "" + "}";

		String units = "{" + "\"" + s1 + "\":\"" + u1 + "\"," + "\"" + s2 + "\":\"" + u2 + "\"" + "}";

		String body = "{" + "\"et_type\":\"composed_example\"" + ",\"component\":\"" + component + "\"" + ",\"exec\":\""
				+ execId + "\"" + ",\"stream\":\"custom_metric\"" + ",\"stream_type\":\"composed_metrics\""
				+ ",\"composed_example\": " + trace + ",\"units\": " + units + "}";

		return body;
	}

	public void sendPostToLogstash(String body) throws Exception {
		URL url = new URL(urlLogstash);

		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection) con;
		http.setRequestMethod("POST");
		http.setDoOutput(true);

		byte[] out = body.getBytes(StandardCharsets.UTF_8);

		int length = out.length;

		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

		if (credentials) {
			String encoded = Base64.getEncoder()
					.encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
			
			http.setRequestProperty("Authorization", "Basic " + encoded);
		}
		
		http.connect();

		OutputStream os = http.getOutputStream();
		os.write(out);
		os.close();
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
