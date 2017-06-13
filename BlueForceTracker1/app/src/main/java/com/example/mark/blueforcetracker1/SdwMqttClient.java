package com.example.mark.blueforcetracker1;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            // TODO: what are the proper connection options?
            options = new MqttConnectOptions();
            String lwt = createStatusPayload("ERROR", "", true);
            options.setWill(topic, lwt.getBytes(), 2, true);
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
        publish("status", createStatusPayload(status, description, false));
    }

    public void publishValues(JSONArray values) {
        try {
            JSONObject a = new JSONObject()
                    .put("entity", topic)
                    .put("datetime", getDateTime())
                    .put("values", values)
                    .put("commandId", getCommandId());
            publish("value", a.toString());
        } catch (JSONException e) {
            System.out.println("Error caugh in publishValues(JSON): " + e.getMessage());
            e.printStackTrace();
            // TODO: how do we manage errors?
        }
    }

    // status must be one of : RUNNING, NOT_RUNNING, ERROR
    // description is optional
    // lwt = if true, then don't include the date/time in the payload
    public String createStatusPayload(String status, String description, boolean lwt) {
        try {
            JSONObject payload = new JSONObject()
                    .put("status", status)
                    .put("commandId", getCommandId());
            if (!lwt) payload.put("datetime", getDateTime());
            if (!description.isEmpty()) payload.put("description", description);
            return (payload.toString());
        } catch (JSONException e) {
            // TODO: handle error on status payload
            return "";
        }
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
}
