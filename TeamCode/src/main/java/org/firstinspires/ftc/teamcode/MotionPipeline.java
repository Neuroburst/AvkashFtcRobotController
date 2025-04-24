package org.firstinspires.ftc.teamcode;

import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

public class MotionPipeline extends OpenCvPipeline {
    @Override
    public void init(Mat input) {
        // Executed before the first call to processFrame
    }

    @Override
    public Mat processFrame(Mat input) {
        return input;
    }

    @Override
    public void onViewportTapped() {
        // Executed when the image display is clicked by the mouse or tapped
        // This method is executed from the UI thread, so be careful to not
        // perform any sort heavy processing here! Your app might hang otherwise
    }
}