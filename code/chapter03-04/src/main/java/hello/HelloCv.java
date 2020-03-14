package hello;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.scijava.nativelib.NativeLoader;

public class HelloCv {
    public static void main(String[] args) throws Exception {
        NativeLoader.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat hello = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println(hello.dump());
    }
}
