package hello;

import static org.opencv.photo.Photo.RECURS_FILTER;
import static org.opencv.photo.Photo.edgePreservingFilter;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.opencv.videoio.VideoCapture;

import origami.ImShow;
import origami.Origami;

public class WebcamWithFilters {

    public static void main(final String[] args) {
        Origami.init();

        final VideoCapture cap = new VideoCapture(0);
        final ImShow ims = new ImShow("Camera", 800, 600);
        final Mat buffer = new Mat();
        Filter filter = new Pipeline(new Contours(), new FPS());
        while (cap.read(buffer)) {
            ims.showImage(filter.apply(buffer));
        }
        cap.release();
    }

}

class Gray implements Filter {

    public Mat apply(final Mat img) {
        final Mat mat1 = new Mat();
        Imgproc.cvtColor(img, mat1, Imgproc.COLOR_RGB2GRAY);
        return mat1;
    }

}

class EdgePreserving implements Filter {
    public int flags = RECURS_FILTER;
    // int flags = NORMCONV_FILTER;
    public float sigma_s = 60;
    public float sigma_r = 0.4f;

    public Mat apply(Mat in) {
        Mat dst = new Mat();
        edgePreservingFilter(in, dst, flags, sigma_s, sigma_r);
        return dst;
    }
}

class Canny implements Filter {
    public boolean inverted = true;
    public int threshold1 = 100;
    public int threshold2 = 200;

    @Override
    public Mat apply(Mat in) {
        Mat dst = new Mat();
        Imgproc.Canny(in, dst, threshold1, threshold2);
        if (inverted) {
            Core.bitwise_not(dst, dst, new Mat());
        }
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_GRAY2RGB);
        return dst;
    }
}

class Thresh implements Filter {

    int sensitivity = 100;
    int maxVal = 255;

    public Thresh() {

    }

    public Thresh(int _sensitivity) {
        this.sensitivity = _sensitivity;
    }

    public Mat apply(Mat img) {
        Mat threshed = new Mat();
        Imgproc.threshold(img, threshed, sensitivity, maxVal, Imgproc.THRESH_BINARY);
        return threshed;
    }
}

class PencilSketch implements Filter {
    float sigma_s = 60;
    float sigma_r = 0.07f;
    float shade_factor = 0.05f;
    boolean gray = false;

    @Override
    public Mat apply(Mat in) {
        Mat dst = new Mat();
        Mat dst2 = new Mat();
        Photo.pencilSketch(in, dst, dst2, sigma_s, sigma_r, shade_factor);
        return gray ? dst : dst2;
    }
}

class FPS implements Filter {

    long start = System.currentTimeMillis();
    int count = 0;

    Point org = new Point(50, 50);
    int fontFace = Imgproc.FONT_HERSHEY_PLAIN;
    double fontScale = 4.0;
    Scalar color = new Scalar(0, 0, 0);
    int thickness = 3;

    @Override
    public Mat apply(Mat in) {
        count++;
        String text = "FPS: " + count / (1 + ((System.currentTimeMillis() - start) / 1000));
        Imgproc.putText(in, text, org, fontFace, fontScale, color, thickness);
        return in;
    }
}

class Color implements Filter {

    int colormap = 0;

    public Color(int colormap) {
        this.colormap = colormap;
    }

    public Color() {
        this.colormap = Imgproc.COLORMAP_AUTUMN;
    }

    public Mat apply(Mat img) {
        Mat threshed = new Mat();
        Imgproc.applyColorMap(img, threshed, colormap);
        return threshed;
    }
}

class Sepia implements Filter {
    public Mat apply(Mat source) {
        Mat kernel = new Mat(3, 3, CvType.CV_32F);
        kernel.put(0, 0, 0.272, 0.534, 0.131, 0.349, 0.686, 0.168, 0.393, 0.769, 0.189);
        Mat destination = new Mat();
        Core.transform(source, destination, kernel);
        return destination;
    }
}

class Cartoon implements Filter {

    public int d = 17;
    public int sigmaColor = d;
    public int sigmaSpace = 7;
    public int ksize = 7;

    public double maxValue = 255;
    public int blockSize = 19;
    public int C = 2;

    public Mat apply(Mat inputFrame) {
        Mat gray = new Mat();
        Mat co = new Mat();
        Mat m = new Mat();
        Mat mOutputFrame = new Mat();

        Imgproc.cvtColor(inputFrame, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.bilateralFilter(gray, co, d, sigmaColor, sigmaSpace);
        Mat blurred = new Mat();
        // Imgproc.medianBlur(co, blurred, ksize);
        Imgproc.blur(co, blurred, new Size(ksize, ksize));
        Imgproc.adaptiveThreshold(blurred, blurred, maxValue, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,
                blockSize, C);
        Imgproc.cvtColor(blurred, m, Imgproc.COLOR_GRAY2BGR);
        Core.bitwise_and(inputFrame, m, mOutputFrame);
        return mOutputFrame;
    }
}

class EnhanceImageSharpness implements Filter {
    public double alpha = 1.5;
    public double beta = -0.5;
    public double gamma = 0;

    public Mat apply(Mat source) {
        Mat destination = new Mat(source.rows(), source.cols(), source.type());
        Imgproc.GaussianBlur(source, destination, new Size(1, 1), 10);
        Core.addWeighted(source, alpha, destination, beta, gamma, destination);
        return destination;
    }
}