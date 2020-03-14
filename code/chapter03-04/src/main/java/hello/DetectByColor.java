package hello;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import origami.ImShow;
import origami.Origami;

public class DetectByColor {

    public static void main(String[] args) {
        Origami.init();

        VideoCapture cap = new VideoCapture("vids/redroses.mov");

        Mat buffer = new Mat();
        ImShow ims = new ImShow("Camera", 800, 600);
        Filter filter = new Pipeline(new RedDetector(), new FPS());
        while (cap.grab()) {
            cap.retrieve(buffer);
            ims.showImage(filter.apply(buffer));
        }
        cap.release();
    }

}

class RedDetector extends ColorDetector {
    public RedDetector() {
        super(new Scalar(0, 100, 100), new Scalar(10, 255, 255));
    }
}

class ColorDetector implements Filter {

    Scalar minColor, maxColor;

    public ColorDetector(Scalar minColor, Scalar maxColor) {
        this.minColor = minColor;
        this.maxColor = maxColor;
    }

    @Override
    public Mat apply(Mat input) {
        Mat array255 = new Mat(input.height(), input.width(), CvType.CV_8UC1);
        array255.setTo(new Scalar(255));
        Mat distance = new Mat(input.height(), input.width(), CvType.CV_8UC1);

        List<Mat> lhsv = new ArrayList<Mat>(3);
        Mat circles = new Mat();

        Mat hsv_image = new Mat();
        Mat thresholded = new Mat();
        Mat thresholded2 = new Mat();

        Imgproc.cvtColor(input, hsv_image, Imgproc.COLOR_BGR2HSV);
        Core.inRange(hsv_image, minColor, maxColor, thresholded);

        Imgproc.erode(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8)));
        Imgproc.dilate(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8)));

        Core.split(hsv_image, lhsv);
        Mat S = lhsv.get(1);
        Mat V = lhsv.get(2);
        Core.subtract(array255, S, S);
        Core.subtract(array255, V, V);
        S.convertTo(S, CvType.CV_32F);
        V.convertTo(V, CvType.CV_32F);
        Core.magnitude(S, V, distance);

        Core.inRange(distance, new Scalar(0.0), new Scalar(200.0), thresholded2);
        Core.bitwise_and(thresholded, thresholded2, thresholded);
        Imgproc.GaussianBlur(thresholded, thresholded, new Size(9, 9), 0, 0);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.HoughCircles(thresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded.height() / 8, 200, 100, 0,
                0);
        Imgproc.findContours(thresholded, contours, thresholded2, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(input, contours, -2, new Scalar(10, 0, 0), 4);

        return input;
    }

}