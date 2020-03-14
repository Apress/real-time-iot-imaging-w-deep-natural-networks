package practice;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Client {
    public static void main(String... args) throws Exception {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();

        List<String> nekos = Files.readAllLines(Paths.get("onlycats.json"));
        String neko = nekos.stream().collect(Collectors.joining("\n"));
        MqttMessage message = new MqttMessage();
        message.setPayload(neko.getBytes());
        client.publish("hermes/intent/hellonico:highlight", message);
        client.disconnect();

    }
}