import filters.MyYolo;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.JSONObject;
import origami.Camera;
import origami.Origami;

import static java.nio.charset.StandardCharsets.*;

public class Chapter05 {

    public static String whatObject(String json) {
        JSONObject obj = new JSONObject(json);
        JSONObject slot = ((JSONObject) obj.getJSONArray("slots").get(0)).getJSONObject("value");
        String cats = slot.getString("value");
        return cats;
    }

    public static void main(String... args) throws Exception {
        Origami.init();
//        String video = "mei/597842788.852328.mp4";
        String video = "mei/597842788.989592.mp4";
//        String video = "marcel/video1.mp4";
        MyYolo yolo = new MyYolo("networks.yolo:yolov3-tiny:1.0.0");
        yolo.thresholds(0.2f, 1.0f);
//        yolo.only("cat");
//        yolo.annotateWithTotal();

        new Thread(() -> {
//            new Camera().device(0).filter(yolo).run();
            new Camera().device(video).filter(yolo).run();
        }).start();

        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();
        client.subscribe("hermes/intent/hellonico:highlight", (topic, message) -> {
            yolo.only(whatObject(new String(message.getPayload(), "UTF-8")));
        });
//
//        client.subscribe("total", (topic, message) -> {
//            if(Boolean.parseBoolean(new String(message.getPayload(), UTF_8)))
//                yolo.annotateWithTotal();
//            else
//                yolo.annotateAll();
//        });

    }
}
