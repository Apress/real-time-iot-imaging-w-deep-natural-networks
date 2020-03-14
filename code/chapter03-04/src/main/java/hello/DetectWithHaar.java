package hello;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import origami.ImShow;
import origami.Origami;

public class DetectWithHaar {

    public static void main(String[] args) {
        Origami.init();

        VideoCapture cap = new VideoCapture("vids/gatos.mp4");

        Mat buffer = new Mat();
        ImShow ims = new ImShow("Camera", 800, 600);
        Filter filter = new Pipeline(new Haar("haarcascades/haarcascade_frontalcatface_extended.xml"), new FPS());
        while (cap.grab()) {
            cap.retrieve(buffer);
            ims.showImage(filter.apply(buffer));
        }
        cap.release();
    }

}

class Haar implements Filter {

    private CascadeClassifier classifier;
    Scalar white = new Scalar(255, 255, 255);

    public Haar(String path) {
        classifier = new CascadeClassifier(path);
    }

    public Mat apply(Mat input) {
        MatOfRect faces = new MatOfRect();
        classifier.detectMultiScale(input, faces, 1.1, 2, -1, new Size(300, 300), new Size(500, 500));
        for (Rect rect : faces.toArray()) {
            Imgproc.putText(input, "Gatos", new Point(rect.x, rect.y - 5), 3, 3, white, 5);
            Imgproc.rectangle(input, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    white, 5);
        }
        return input;
    }
}

class FunWithHaar implements Filter {

    CascadeClassifier classifier;
    Mat mask;
    Scalar white = new Scalar(255, 255, 255);

    public FunWithHaar(String path) {
        classifier = new CascadeClassifier(path);
        mask = Imgcodecs.imread("masquerade_mask.png", Imgcodecs.IMREAD_UNCHANGED);
    }

    void drawTransparency(Mat frame, Mat transp, int xPos, int yPos) {
        List<Mat> layers = new ArrayList<Mat>();
        Core.split(transp, layers);
        Mat mask = layers.remove(3);
        Core.merge(layers, transp);
        Mat submat = frame.submat(yPos, yPos + transp.rows(), xPos, xPos + transp.cols());
        transp.copyTo(submat, mask);
    }

    public Mat apply(Mat input) {
        MatOfRect faces = new MatOfRect();
        classifier.detectMultiScale(input, faces);
        Mat maskResized = new Mat();
        for (Rect rect : faces.toArray()) {
            Imgproc.resize(mask, maskResized, new Size(rect.width, rect.height));
            int adjusty = (int) (rect.y - rect.width * 0.2);
            try {
                drawTransparency(input, maskResized, rect.x, adjusty);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return input;
    }
}