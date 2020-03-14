package practice;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class Sub {

    public static void main(String... args) throws Exception {

        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();
        client.subscribe("hermes/intent/#", new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String json = new String(message.getPayload(), "UTF-8");
                JSONObject obj = new JSONObject(json);
                JSONObject slot = ((JSONObject) obj.getJSONArray("slots").get(0)).getJSONObject("value");
                String cats = slot.getString("value");
                System.out.println(cats);
            }
        });
    }
}
