package hello;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import origami.ImShow;
import origami.Origami;

public class DetectByYolo {

    public static void main(String[] args) {
        Origami.init();

        VideoCapture cap = new VideoCapture("vids/gatos.mp4");

        Mat buffer = new Mat();
        ImShow ims = new ImShow("Camera", 400, 300);
        // Filter filter = new Pipeline(new YoloDetector("yolov3/yolov3.weights",
        // "yolov3/yolov3.cfg"), new FPS());
        Filter filter = new Pipeline(new TinyYolov3(), new FPS());
        while (cap.grab()) {
            cap.retrieve(buffer);
            ims.showImage(filter.apply(buffer));
        }

        cap.release();
    }

}

class Yolov2 extends YoloDetector {

    public Yolov2() {
        super("yolov2/yolov2.weights", "yolov2/yolov2.cfg");
    }
}

class TinyYolov2 extends YoloDetector {

    public TinyYolov2() {
        super("yolov2-tiny/yolov2-tiny.weights", "yolov2-tiny/yolov2-tiny.cfg");
    }
}

class Yolov3 extends YoloDetector {

    public Yolov3() {
        super("yolov3/yolov3.weights", "yolov3/yolov3.cfg");
    }
}

class TinyYolov3 extends YoloDetector {

    public TinyYolov3() {
        super("yolov3-tiny/yolov3-tiny.weights", "yolov3-tiny/yolov3-tiny.cfg");
    }
}

class YoloDetector implements Filter {
    final static Size sz = new Size(416, 416);
    List<String> outBlobNames;
    Net net;
    List<String> layers;
    List<String> labels;

    List<String> getOutputsNames(Net net) {
        List<String> layersNames = net.getLayerNames();
        return net.getUnconnectedOutLayers().toList().stream().map(i -> i - 1).map(layersNames::get)
                .collect(Collectors.toList());
    }

    public YoloDetector(String modelWeights, String modelConfiguration) {
        net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights);
        layers = getOutputsNames(net);

        try {
            labels = Files.readAllLines(Paths.get(LABEL_FILE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Mat apply(Mat in) {
        findShapes(in);
        return in;
    }

    final int IN_WIDTH = 416;
    final int IN_HEIGHT = 416;
    final double IN_SCALE_FACTOR = 0.00392157;
    final int MAX_RESULTS = 20;
    final boolean SWAP_RGB = true;
    final String LABEL_FILE = "yolov3/coco.names";

    void findShapes(Mat frame) {

        Mat blob = Dnn.blobFromImage(frame, IN_SCALE_FACTOR, new Size(IN_WIDTH, IN_HEIGHT), new Scalar(0, 0, 0),
                SWAP_RGB);
        net.setInput(blob);

        List<Mat> outputs = new ArrayList<>();
        for (int i = 0; i < layers.size(); i++)
            outputs.add(new Mat());

        net.forward(outputs, layers);
        postprocess(frame, outputs);
    }

    private void postprocess(Mat frame, List<Mat> outs) {

        List<Rect> tmpLocations = new ArrayList<>();
        List<Integer> tmpClasses = new ArrayList<>();
        List<Float> tmpConfidences = new ArrayList<>();

        int w = frame.width();
        int h = frame.height();

        for (Mat out : outs) {

            final float[] data = new float[(int) out.total()];
            out.get(0, 0, data);

            int k = 0;
            for (int j = 0; j < out.height(); j++) {

                Mat scores = out.row(j).colRange(5, out.width());
                Core.MinMaxLocResult result = Core.minMaxLoc(scores);
                if (result.maxVal > 0) {
                    float center_x = data[k + 0] * w;
                    float center_y = data[k + 1] * h;
                    float width = data[k + 2] * w;
                    float height = data[k + 3] * h;
                    float left = center_x - width / 2;
                    float top = center_y - height / 2;

                    tmpClasses.add((int) result.maxLoc.x);
                    tmpConfidences.add((float) result.maxVal);
                    tmpLocations.add(new Rect((int) left, (int) top, (int) width, (int) height));

                }
                k += out.width();
            }
        }

        annotateFrame(frame, tmpLocations, tmpClasses, tmpConfidences);
    }

    private void annotateFrame(Mat frame, List<Rect> tmpLocations, List<Integer> tmpClasses,
            List<Float> tmpConfidences) {

        MatOfRect locMat = new MatOfRect();
        MatOfFloat confidenceMat = new MatOfFloat();
        MatOfInt indexMat = new MatOfInt();

        locMat.fromList(tmpLocations);
        confidenceMat.fromList(tmpConfidences);

        Dnn.NMSBoxes(locMat, confidenceMat, 0.1f, 0.1f, indexMat);

        for (int i = 0; i < indexMat.total() && i < MAX_RESULTS; ++i) {
            int idx = (int) indexMat.get(i, 0)[0];
            int labelId = tmpClasses.get(idx);
            Rect box = tmpLocations.get(idx);
            String label = labels.get(labelId);
            annotateOne(frame, box, label);
        }
    }

    private void annotateOne(Mat frame, Rect box, String label) {
        Imgproc.rectangle(frame, box, new Scalar(0, 0, 0), 2);
        Imgproc.putText(frame, label, new Point(box.x, box.y), Imgproc.FONT_HERSHEY_PLAIN, 4.0, new Scalar(0, 0, 0), 3);
    }

}