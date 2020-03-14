package hello;

import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.scijava.nativelib.NativeLoader;

class FeatureDetection {
    public static void main(String[] args) throws IOException {
        NativeLoader.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat src = Imgcodecs.imread("marcel2.jpg", Imgcodecs.IMREAD_GRAYSCALE);

        ORB detector = ORB.create();
        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        detector.detect(src, keypoints);

        Mat target = src.clone();
        target.setTo(new Scalar(255, 255, 255));

        Features2d.drawKeypoints(target, keypoints, target);
        Imgcodecs.imwrite("orb.png", target);

    }
}