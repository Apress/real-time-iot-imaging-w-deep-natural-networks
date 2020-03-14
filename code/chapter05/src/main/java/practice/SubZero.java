package practice;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SubZero {

    public static void main(String... args) throws Exception {

        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();
        client.subscribe("hello", (topic, message) -> {
            String hello = new String(message.getPayload(), "UTF-8");
            System.out.println(hello);
        });
    }
}
