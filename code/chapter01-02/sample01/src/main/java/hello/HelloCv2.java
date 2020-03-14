package hello;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.scijava.nativelib.NativeLoader;

public class HelloCv2 {
    public static void main(String[] args) throws Exception {
        NativeLoader.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat hello = Mat.eye(50, 50, CvType.CV_8UC3);
        hello.setTo(new Scalar(190, 119, 0));
        HighGui.imshow("rgb", hello);
        HighGui.waitKey();
        System.exit(0);
    }
}
