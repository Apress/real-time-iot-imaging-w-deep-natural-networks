package hello;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.scijava.nativelib.NativeLoader;

public class HelloCv4 {

    public static Mat simplyAdd() {
        Mat marcel = Imgcodecs.imread("marcel.jpg");
        Mat beach = Imgcodecs.imread("beach.jpeg");
        Mat dest = new Mat();
        Core.add(marcel, beach, dest);
        return dest;
    }

    public static Mat resizeAndAdd() {
        Mat marcel = Imgcodecs.imread("marcel.jpg");
        Mat beach = Imgcodecs.imread("beach.jpeg");
        Mat dest = new Mat();
        Imgproc.resize(beach, dest, marcel.size());
        Core.add(marcel, dest, dest);
        return dest;
    }

    public static Mat resizeAndAddWeighted() {
        Mat marcel = Imgcodecs.imread("marcel.jpg");
        Mat beach = Imgcodecs.imread("beach.jpeg");
        Mat dest = new Mat();
        Imgproc.resize(beach, dest, marcel.size());
        Core.addWeighted(marcel, 0.8, dest, 0.2, 0.5, dest);
        return dest;
    }

    public static void main(String[] args) throws Exception {
        NativeLoader.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // HighGui.imshow("hello", simplyAdd());
        // HighGui.imshow("hello", resizeAndAdd());
        HighGui.imshow("hello", resizeAndAddWeighted());

        HighGui.waitKey();
        System.exit(0);
    }
}
