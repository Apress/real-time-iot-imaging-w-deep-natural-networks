package hello;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import origami.ImShow;
import origami.Origami;

public class PlayVideo {

    public static void main(String[] args) {
        Origami.init();

        VideoCapture cap = new VideoCapture("vids/marcel_1.mp4");

        Mat matFrame = new Mat();
        ImShow ims = new ImShow("Camera", 400, 300);
        long start = System.currentTimeMillis();
        long frame = 0;
        while (cap.grab()) {
            cap.retrieve(matFrame);
            long now = System.currentTimeMillis();
            frame++;
            Imgproc.putText(matFrame, "FPS " + (frame / (1 + (now - start) / 1000)), new Point(50, 50),
                    Imgproc.FONT_HERSHEY_COMPLEX, 2.0, new Scalar(255, 255, 255));
            ims.showImage(matFrame);
        }
        cap.release();
    }
}
