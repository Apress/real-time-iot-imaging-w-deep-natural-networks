package hello;

import java.io.File;
import java.io.IOException;
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
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.scijava.nativelib.NativeLoader;

public class Yolo {
    static final String OUTFOLDER = "out/";
    static final Scalar BLACK = new Scalar(0, 0, 0);

    static {
        new File(OUTFOLDER).mkdirs();
    }

    public static void main(String[] args) throws Exception {
        NativeLoader.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        runDarknet(new String[] { "marcel.jpg", "marcel2.jpg", "cats.jpg", });
    }

    private static void runDarknet(String[] sourceImageFile) throws IOException {
        List<String> labels = Files.readAllLines(Paths.get("yolov3/coco.names"));
        Net net = Dnn.readNetFromDarknet("yolov2-tiny/yolov2-tiny.cfg", "yolov2-tiny/yolov2-tiny.weights");
        List<String> layersNames = net.getLayerNames();
        List<String> outLayers = net.getUnconnectedOutLayers().toList().stream().map(i -> i - 1).map(layersNames::get)
                .collect(Collectors.toList());

        for (String image : sourceImageFile) {
            runInference(net, outLayers, labels, image);
        }
    }

    private static void runInference(Net net, List<String> layers, List<String> labels, String filename) {
        final Size BLOB_SIZE = new Size(416, 416);
        final double IN_SCALE_FACTOR = 0.00392157;
        final int MAX_RESULTS = 20;

        Mat frame = Imgcodecs.imread(filename, Imgcodecs.IMREAD_COLOR);
        Mat blob = Dnn.blobFromImage(frame, IN_SCALE_FACTOR, BLOB_SIZE, new Scalar(0, 0, 0), false);
        net.setInput(blob);

        List<Mat> outputs = layers.stream().map(s -> {
            return new Mat();
        }).collect(Collectors.toList());

        net.forward(outputs, layers);

        List<Integer> labelIDs = new ArrayList<>();
        List<Float> probabilities = new ArrayList<>();
        List<String> locations = new ArrayList<>();

        postprocess(filename, frame, labels, outputs, labelIDs, probabilities, locations, MAX_RESULTS);
    }

    private static void postprocess(String filename, Mat frame, List<String> labels, List<Mat> outs,
            List<Integer> classIds, List<Float> confidences, List<String> locations, int nResults) {
        List<Rect> tmpLocations = new ArrayList<>();
        List<Integer> tmpClasses = new ArrayList<>();
        List<Float> tmpConfidences = new ArrayList<>();
        int w = frame.width();
        int h = frame.height();

        for (Mat out : outs) {
            // Scan through all the bounding boxes output from the network and keep only the
            // ones with high confidence scores. Assign the box's class label as the class
            // with the highest score for the box.
            final float[] data = new float[(int) out.total()];
            out.get(0, 0, data);

            int k = 0;
            for (int j = 0; j < out.height(); j++) {

                // Each row of data has 4 values for location, followed by N confidence values
                // which correspond to the labels
                Mat scores = out.row(j).colRange(5, out.width());
                // Get the value and location of the maximum score
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
        annotateFrame(frame, labels, classIds, confidences, nResults, tmpLocations, tmpClasses, tmpConfidences);
        Imgcodecs.imwrite(OUTFOLDER + new File(filename).getName(), frame);
    }

    private static void annotateFrame(Mat frame, List<String> labels, List<Integer> classIds, List<Float> confidences,
            int nResults, List<Rect> tmpLocations, List<Integer> tmpClasses, List<Float> tmpConfidences) {
        // Perform non maximum suppression to eliminate redundant overlapping boxes with
        // lower confidences and sort by confidence

        // many overlapping results coming from yolo so have to use it
        MatOfRect locMat = new MatOfRect();
        locMat.fromList(tmpLocations);

        MatOfFloat confidenceMat = new MatOfFloat();
        confidenceMat.fromList(tmpConfidences);

        MatOfInt indexMat = new MatOfInt();
        Dnn.NMSBoxes(locMat, confidenceMat, 0.1f, 0.1f, indexMat);

        for (int i = 0; i < indexMat.total() && i < nResults; ++i) {
            int idx = (int) indexMat.get(i, 0)[0];
            classIds.add(tmpClasses.get(idx));
            confidences.add(tmpConfidences.get(idx));
            Rect box = tmpLocations.get(idx);
            String label = String.format("%s [%.0f%%]", labels.get(classIds.get(i)), 100 * tmpConfidences.get(idx));
            Imgproc.rectangle(frame, box, BLACK, 2);
            Imgproc.putText(frame, label, new Point(box.x, box.y), Imgproc.FONT_HERSHEY_PLAIN, 2.0, BLACK, 3);
        }
    }
}
