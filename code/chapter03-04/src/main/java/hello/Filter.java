package hello;

import org.opencv.core.Mat;

public interface Filter {
    public Mat apply(Mat in);
}
