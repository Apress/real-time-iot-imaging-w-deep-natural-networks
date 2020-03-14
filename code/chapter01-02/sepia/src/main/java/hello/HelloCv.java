package hello;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.scijava.nativelib.NativeLoader;

/**
 * Using a kernel to get sepia picture
 */
public class HelloCv {

    public static void sepia(Mat source, double[] rgb) {
        // mat is in BGR
        Mat kernel = new Mat(3, 3, CvType.CV_32F);
        kernel.put(0, 0, rgb);
        Mat destination = new Mat();
        Core.transform(source, destination, kernel);
        Imgcodecs.imwrite("sepia.jpg", destination);
    }

    public static void main(String[] args) throws Exception {
        NativeLoader.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String filename = "marcel.jpg";
        // String filename = args[0];
        Mat source = Imgcodecs.imread(filename);

        double[] rgb = new double[] {
                // rgb -> blue
                0.272, 0.534, 0.131,
                // rgb -> green
                0.349, 0.686, 0.168,
                // rgb -> red
                0.393, 0.769, 0.189 };

        System.out.println("Finished...");
    }
}