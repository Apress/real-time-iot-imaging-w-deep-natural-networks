package practice;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class JSONFun {

    public static String whatObject(String json) {
        JSONObject obj = new JSONObject(json);
        JSONObject slot = ((JSONObject) obj.getJSONArray("slots").get(0)).getJSONObject("value");
        String cats = slot.getString("value");

        return cats;
    }

    public static void main(String... args) throws Exception {
        List<String> nekos = Files.readAllLines(Paths.get("onlycats.json"));
        String json = nekos.stream().collect(Collectors.joining("\n"));
        System.out.println(whatObject(json));
    }
}
