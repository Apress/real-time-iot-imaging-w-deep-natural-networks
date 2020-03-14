package hello;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import origami.ImShow;
import origami.Origami;

public class DetectBackground {

    public static void main(final String[] args) {
        Origami.init();

        final VideoCapture cap = new VideoCapture("vids/gatos.mp4");
        final ImShow ims = new ImShow("Camera", 800, 600);
        final Mat buffer = new Mat();
        Filter filter = new Pipeline(new BackgroundSubstractor(), new FPS());
        while (cap.read(buffer)) {
            ims.showImage(filter.apply(buffer));
        }
        cap.release();
    }

}

class BackgroundSubstractor implements Filter {
    boolean useMOG2 = true;
    BackgroundSubtractor backSub;
    double learningRate = 1.0;
    boolean showMask = true;

    public BackgroundSubstractor() {
        if (useMOG2) {
            backSub = Video.createBackgroundSubtractorMOG2();
        } else {
            backSub = Video.createBackgroundSubtractorKNN();
        }
    }

    @Override
    public Mat apply(Mat in) {
        Mat mask = new Mat();
        backSub.apply(in, mask);
        Mat result = new Mat();
        if (showMask) {
            Core.bitwise_not(mask, mask);
            Imgproc.cvtColor(mask, result, Imgproc.COLOR_GRAY2RGB);
            return result;
        } else {
            in.copyTo(result, mask);
            return result;
        }
    }
}
