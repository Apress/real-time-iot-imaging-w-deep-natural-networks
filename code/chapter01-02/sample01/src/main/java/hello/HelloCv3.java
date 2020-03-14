package hello;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.scijava.nativelib.NativeLoader;

public class HelloCv3 {
    public static void main(String[] args) throws Exception {
        NativeLoader.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat hello = Mat.eye(50, 50, CvType.CV_8UC3);
        hello.setTo(new Scalar(190, 119, 0));
        Mat hello2 = Mat.eye(50, 50, CvType.CV_8UC3);
        hello2.setTo(new Scalar(0, 0, 100));
        Mat dest = new Mat();
        Core.add(hello, hello2, dest);
        HighGui.imshow("hello", dest);
        HighGui.waitKey();
        System.exit(0);
    }
}
