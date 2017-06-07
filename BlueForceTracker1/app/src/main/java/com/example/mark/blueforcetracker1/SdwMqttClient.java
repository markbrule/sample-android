package com.example.mark.blueforcetracker1;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by mark on 6/3/17.
 */

public class SdwMqttClient {
    private MqttClient publisher;
    private MqttConnectOptions options;
    private final String clientId = "Envigilant";
    private final int qos = 2;
    private String topic;

    // addr is the IP address or hostname of the server running the MQTT broker
    // port is the port number that the MQTT broker is listening on
    // topic is the sensor path
    SdwMqttClient(String addr, String port, String topic) {
        try {
            this.topic = topic;
            publisher = null;
            MemoryPersistence mem = new MemoryPersistence();
            // TODO: set LWT
            publisher = new MqttClient("tcp://" + addr + ":" + port, clientId, mem);
            // TODO: is it okay to leave the session connected?
            // TODO: what are the proper connection options?
            // TODO: when should we disconnect?
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            //publisher.connect(options);
        } catch (MqttException me) {
            System.out.println("Error caught creating the Paho client: " + me.getMessage());
            me.printStackTrace();
        }
    }

    // type is either "status" or "value"
    // payload is the string version of the payload
    public void publish(String type, String payload) {
        try {
            String endpoint = "sdw" + topic + "[" + type + "]";
            publisher.connect(options);
            publisher.publish(endpoint, new MqttMessage(payload.getBytes()));
            publisher.disconnect();
        } catch (MqttException me) {
            System.out.println("Error caught publishing a message: " + me.getMessage());
            me.printStackTrace();
        }
    }

    public void publishStatus(String status, String description) {
        publish("status", createStatusPayload(status, description));
    }

    public void publishValues(List<Map<String,Object>> values) {
        String vstr = "[";
        for (Map<String,Object> v : values) {
            vstr += (vstr.equals("[") ? "" : ", ") + createJsonPayload(v);
        }
        vstr += "]";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("entity", topic);
        params.put("datetime", getDateTime());
        params.put("values", vstr);
        params.put("commandId", getCommandId());
        publish("value", createJsonPayload(params));
    }

    // status must be one of : RUNNING, NOT_RUNNING, ERROR
    // description is optional
    public String createStatusPayload(String status, String description) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("status", status);
        params.put("datetime", getDateTime());
        if (! description.isEmpty()) params.put("description", description);
        params.put("commandId", getCommandId());
        return createJsonPayload(params);
    }

    // get current DateTime in RFC3339 format
    protected String getDateTime() {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        return fmt.print(Instant.now());
    }

    // create a string usable as a commandId
    protected String getCommandId() {
        return UUID.randomUUID().toString();
    }

    // convert a map of strings (key => value) as a JSON object
    protected String createJsonPayload(Map<String,Object> params) {
        String payload = "{";
        for (Map.Entry<String,Object> entry : params.entrySet()) {
            String e;
            if (entry.getValue() == null) {
                e = String.format("\"%s\": {}", entry.getKey());
            } else if (entry.getValue() instanceof Double) {
                e = String.format("\"%s\": %f", entry.getKey(), entry.getValue());
            } else if (entry.getKey().equals("values")) {
                // TODO: values is an array, so it shouldn't be quoted.
                //       need to do this right
                e = String.format("\"%s\": %s", entry.getKey(), entry.getValue());
            } else {
                e = String.format("\"%s\": \"%s\"", entry.getKey(), entry.getValue());
            }
            payload += (payload.equals("{") ? "" : ", ") + e;
        }
        return payload + "}";
    }
}
