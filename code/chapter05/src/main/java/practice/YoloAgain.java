package practice;

import origami.Camera;
import origami.Filter;
import origami.Filters;
import origami.Origami;
import origami.filters.FPS;
import filters.MyYolo;

public class YoloAgain {

    public static void main(String[] args) {
        Origami.init();
        String video = "mei/597842788.852328.mp4";
//        Filter filter = new Filters(new MyYolo("networks.yolo:yolov3-tiny:1.0.0").only("car"), new FPS());
        MyYolo yolo = new MyYolo("networks.yolo:yolov3-tiny:1.0.0"); //.only("person");
        yolo.thresholds(0.4f, 1.0f);
        yolo.annotateWithTotal();

        Filter filter = new Filters(yolo, new FPS());
        new Thread(() -> {
            new Camera().device(video).filter(filter).run();
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("only cat");
                yolo.only("cat");
                Thread.sleep(5000);
                System.out.println("only person");
                yolo.only("person");
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }).start();

    }
}
