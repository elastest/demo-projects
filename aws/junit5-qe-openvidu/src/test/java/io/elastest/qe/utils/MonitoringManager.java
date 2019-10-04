package io.elastest.qe.utils;

import static java.lang.invoke.MethodHandles.lookup;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;

import com.google.gson.JsonObject;

public class MonitoringManager {
    final Logger logger = getLogger(lookup().lookupClass());

    String endpoint;
    boolean withSSL;
    String execid;
    String containerName;
    String component;
    URL url;

    public MonitoringManager() {
        withSSL = true;
        endpoint = System.getenv("ET_MON_LSHTTPS_API");
        if (endpoint == null) {
            endpoint = System.getenv("ET_MON_LSHTTP_API");
            withSSL = false;
        }

        execid = System.getenv("ET_MON_EXEC");
        component = "test";
        containerName = System.getenv("CONTAINER_NAME");
        try {
            url = new URL(endpoint);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    // HTTP POST request
    private void sendMonitoring(String body) throws Exception {

        if (url != null && component != null && execid != null) {
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            logger.info("Sending monitoring to {}: {}", url, body);
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            byte[] out = body.getBytes(UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type",
                    "application/json; charset=UTF-8"); 
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            } finally {
                http.disconnect();
            }

        } else {
            throw new Exception("Trace '" + body
                    + "' not sent: url, component or execid are null");
        }
    }

    public void sendSingleMessage(String message) throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("component", component);
        jsonObject.addProperty("exec", execid);
        jsonObject.addProperty("stream", "default_log");
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("containerName", containerName);

        sendMonitoring(jsonObject.getAsString());
    }

    // public void sendMultipleLog(String... messages) {
    // String jsonMessage = "[ " + formatJsonMessage(message) + ",";
    //
    // message = String.join(" ", generateRandomWords(3));
    // jsonMessage += formatJsonMessage(message) + " ]";
    //
    //
    // JsonObject jsonObject = new JsonObject();
    // jsonObject.addProperty("component", component);
    // jsonObject.addProperty("exec", execid);
    // jsonObject.addProperty("stream", "default_log");
    // jsonObject.addProperty("messages", jsonMessage);
    // jsonObject.addProperty("containerName", containerName);
    //
    // sendMonitoring(jsonObject.getAsString());
    // }

    public void sendAtomicMetric(String metricName, String unit, String value,
            String stream) throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("component", component);
        jsonObject.addProperty("exec", execid);
        if (containerName != null) {
            jsonObject.addProperty("containerName", containerName);
        }
        jsonObject.addProperty("et_type", metricName);
        jsonObject.addProperty("stream", stream);
        jsonObject.addProperty("stream_type", "atomic_metric");
        jsonObject.addProperty("unit", unit);
        jsonObject.addProperty("metricName", metricName);
        jsonObject.addProperty(metricName, value);

        sendMonitoring(jsonObject.toString());
    }

    public void sendComposedMetric(String metricName, String stream,
            JsonObject metricsJson, JsonObject unitsJson) throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("component", component);
        jsonObject.addProperty("exec", execid);
        jsonObject.addProperty("containerName", containerName);
        jsonObject.addProperty("et_type", metricName);
        jsonObject.addProperty("stream", stream);
        jsonObject.addProperty("stream_type", "atomic_metric");
        jsonObject.add("units", unitsJson);
        jsonObject.add(metricName, metricsJson);

        sendMonitoring(jsonObject.getAsString());
    }

    public static String formatJsonMessage(String msg) {
        return "\"" + msg + "\"";
    }


    @Override
    public String toString() {
        return "MonitoringManager [endpoint=" + endpoint + ", withSSL="
                + withSSL + ", execid=" + execid + ", containerName="
                + containerName + ", component=" + component + ", url=" + url
                + "]";
    }

}
