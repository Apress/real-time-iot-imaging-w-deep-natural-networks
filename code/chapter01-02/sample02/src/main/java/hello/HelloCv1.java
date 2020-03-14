package hello;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.scijava.nativelib.NativeLoader;

/**
 * Using a kernel to get sepia picture
 */
public class HelloCv1 {

    public static void applyTransform(Mat source, Mat kernel) {
        System.out.println(source.size());
        System.out.println(source.dump());

        System.out.println(kernel.size());
        System.out.println(kernel.dump());

        Mat destination = new Mat();
        Core.transform(source, destination, kernel);

        System.out.println(destination.size());
        System.out.println(destination.dump());

    }

    public static void sample1() {
        System.out.println("sample1\n");

        Mat source = new Mat(1, 2, CvType.CV_16U);
        source.put(0, 0, 2, 3);

        Mat kernel = new Mat(1, 1, CvType.CV_32F);
        kernel.put(0, 0, 5);

        applyTransform(source, kernel);

    }

    public static void sample2() {
        System.out.println("sample2\n");

        Mat source = new Mat(1, 2, CvType.CV_16U);
        source.put(0, 0, 2, 3);

        Mat kernel = new Mat(2, 1, CvType.CV_32F);
        kernel.put(0, 0, 5, 10);

        applyTransform(source, kernel);
    }

    public static void sample3() {
        System.out.println("sample3\n");

        Mat source = new Mat(1, 1, CvType.CV_16U);
        source.put(0, 0, 2);

        Mat kernel = new Mat(2, 2, CvType.CV_32F);
        kernel.put(0, 0, 1, 2, 3, 4);

        applyTransform(source, kernel);
    }

    public static void sample4() {
        System.out.println("sample4\n");

        Mat source = new Mat(1, 1, CvType.CV_16U);
        source.put(0, 0, 2);

        Mat kernel = new Mat(3, 1, CvType.CV_32F);
        kernel.put(0, 0, 1, 2, 3);

        applyTransform(source, kernel);
    }

    public static void sample5() {
        System.out.println("sample5\n");

        Mat source = new Mat(1, 1, CvType.CV_16U);
        source.put(0, 0, 2);

        Mat kernel = new Mat(3, 2, CvType.CV_32F);
        kernel.put(0, 0, 1, 2, 3, 4, 5, 6);

        applyTransform(source, kernel);
    }

    public static void sample6() {
        System.out.println("sample6\n");

        Mat source = new Mat(1, 1, CvType.CV_8UC3);
        source.put(0, 0, 190, 119, 10);

        Mat kernel = new Mat(1, 3, CvType.CV_32F);
        kernel.put(0, 0, 0.5, 0.2, 0.3);

        applyTransform(source, kernel);
    }

    public static void main(String[] args) throws Exception {
        NativeLoader.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // sample1();
        // sample2();
        // sample3();
        // sample4();
        // sample5();
        sample6();
    }
}