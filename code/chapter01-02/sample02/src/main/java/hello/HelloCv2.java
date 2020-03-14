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
public class HelloCv2 {

    public static void main(String[] args) throws Exception {
        NativeLoader.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat marcel = Imgcodecs.imread("marcel.jpg");
        Mat sepiaKernel = new Mat(3, 3, CvType.CV_32F);
        sepiaKernel.put(0, 0,
                // bgr -> blue
                0.272, 0.534, 0.131,
                // bgr -> green
                0.349, 0.686, 0.168,
                // bgr -> red
                0.393, 0.769, 0.589);

        Mat destination = new Mat();
        Core.transform(marcel, destination, sepiaKernel);
        Imgcodecs.imwrite("sepia.jpg", destination);

    }
}