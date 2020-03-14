package hello;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.scijava.nativelib.NativeLoader;

public class BluntHaar {
    public static void main(String[] args) throws Exception {
        NativeLoader.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String classifier = "haarcascade_frontalcatface.xml";
        CascadeClassifier cascadeFaceClassifier = new CascadeClassifier(classifier);

        Mat cat = Imgcodecs.imread("marcel.jpg");

        MatOfRect faces = new MatOfRect();
        cascadeFaceClassifier.detectMultiScale(cat, faces);

        for (Rect rect : faces.toArray()) {
            Imgproc.putText(cat, "Chat", new Point(rect.x, rect.y - 5), Imgproc.FONT_HERSHEY_PLAIN, 10,
                    new Scalar(255, 0, 0), 5);
            Imgproc.rectangle(cat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(255, 0, 0), 5);
        }

        Imgcodecs.imwrite("marcel_blunt_haar.jpg", cat);

    }
}
