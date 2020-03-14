package hello;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.opencv.core.Mat;

public class Pipeline implements Filter {
    List<Filter> filters;

    public Pipeline(Class... __filters) {
        List<Class<Filter>> _filters = (List) Arrays.asList(__filters);
        this.filters = _filters.stream().map(i -> {
            try {
                return (Filter) Class.forName(i.getName()).newInstance();
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList());
    }

    public Pipeline(Filter... __filters) {
        this.filters = (List) Arrays.asList(__filters);
    }

    @Override
    public Mat apply(Mat in) {
        Mat dst = in.clone();
        for (Filter f : filters) {
            dst = f.apply(dst);
        }
        return dst;
    }
}
