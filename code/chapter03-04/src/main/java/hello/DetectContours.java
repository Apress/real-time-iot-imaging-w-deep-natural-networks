package hello;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import origami.ImShow;
import origami.Origami;

public class DetectContours {

    public static void main(final String[] args) {
        Origami.init();

        final VideoCapture cap = new VideoCapture(0);
        final ImShow ims = new ImShow("Camera", 800, 600);
        final Mat buffer = new Mat();
        Filter filter = new Pipeline(new BackgroundSubstractor(), new FPS());
        while (cap.read(buffer)) {
            ims.showImage(filter.apply(buffer));
        }
        cap.release();
    }

}

class Contours implements Filter {
    private int threshold = 100;

    public Mat apply(Mat srcImage) {
        Mat cannyOutput = new Mat();
        Mat srcGray = new Mat();
        Imgproc.cvtColor(srcImage, srcGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2, 7);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(256, 150, 0);
            Imgproc.drawContours(drawing, contours, i, color, 2, 8, hierarchy, 0, new Point());
        }
        return drawing;
    }

}