package hello;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import origami.ImShow;
import origami.Origami;

public class DetectByTemplate {

    public static void main(final String[] args) {
        Origami.init();

        final VideoCapture cap = new VideoCapture(0);
        final ImShow ims = new ImShow("Camera", 800, 600);
        final Mat buffer = new Mat();
        Filter filter = new Pipeline(new Template("template.jpg"), new FPS());
        while (cap.read(buffer)) {
            ims.showImage(filter.apply(buffer));
        }
        cap.release();
    }

}

class Template implements Filter {
    Mat template;

    public Template(String path) {
        this.template = Imgcodecs.imread(path);

    }

    @Override
    public Mat apply(Mat in) {

        Mat outputImage = new Mat();
        Imgproc.matchTemplate(in, template, outputImage, Imgproc.TM_CCOEFF);

        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
        Point matchLoc = mmr.maxLoc;

        Imgproc.rectangle(in, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()),
                new Scalar(255, 255, 255), 3);

        return in;

    }

}