package csv;

import com.sun.xml.internal.ws.developer.SerializationFeature;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.json.JSONArray;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Csv2Json {

    public static void main(String... args) throws IOException {
        String csvFile = "coco.csv";
        try (InputStream in = new FileInputStream(csvFile);) {
            CSV csv = new CSV(true, ',', in);
            List<String> fieldNames = null;
            if ( csv.hasNext() ) fieldNames = new ArrayList<>(csv.next());
            List<Map<String,String>> list = new ArrayList<>();
            while (csv.hasNext()) {
                List<String> x = csv.next();
                Map<String,String> obj = new LinkedHashMap<>();
                for (int i = 0 ; i < fieldNames.size() ; i++) {
                    obj.put(fieldNames.get(i), x.get(i));
                }
                list.add(obj);
            }

            JSONArray array = new JSONArray(list);
            System.out.println(array.toString(2));
        }
    }
}

