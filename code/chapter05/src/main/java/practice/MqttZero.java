package practice;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttZero {
    public static void main(String... args) throws Exception {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();
        MqttMessage message = new MqttMessage();
        message.setPayload(new String("good morning Russia").getBytes());
        client.publish("hello", message);
        client.disconnect();

    }
}