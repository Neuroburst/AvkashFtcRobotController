package org.firstinspires.ftc.teamcode;

import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;


import java.util.List;

public class TestPipeline extends OpenCvPipeline {

    @Override
    public void init(Mat input) {
        // Executed before the first call to processFrame
    }

    @Override
    public Mat processFrame(Mat input) {
        Mat original = input.clone();

        Imgproc.blur(input, input, new Size(5, 5));
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2RGB);

        Mat mask = new Mat();
        Core.inRange(input, new Scalar(10, 10, 10), new Scalar(255, 255, 255), mask);
        // Convert Mask
        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2BGR);

        Core.bitwise_not(mask, mask);
        Core.bitwise_and(input, mask, input);

        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2BGR);

        SimpleBlobDetector_Params params = new SimpleBlobDetector_Params();
        params.set_collectContours(true);
        params.set_filterByColor(false);
        params.set_minThreshold(10);
        params.set_maxThreshold(255);
        params.set_filterByArea(false);
        params.set_filterByCircularity(false);
        params.set_filterByConvexity(false);
        params.set_filterByInertia(false);
        params.set_minDistBetweenBlobs(50.0f);

        SimpleBlobDetector detector = SimpleBlobDetector.create(params);
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        detector.detect(input, keyPoints);
        List<MatOfPoint> contours = detector.getBlobContours();

        Features2d.drawKeypoints(input, keyPoints, input, new Scalar(0, 0, 255));

        Imgproc.polylines(input, contours, true, new Scalar(255,0,0));


        Imgproc.putText(input, Integer.toString(contours.size()) + " Blobs", new Point(0,20), 0, 0.75, new Scalar(255,0,0), 2);

////        Imgproc.rectangle(
////                input,
////                new Point(
////                        input.cols()/4,
////                        input.rows()/4),
////                new Point(
////                        input.cols()*(3f/4f),
////                        input.rows()*(3f/4f)),
////                new Scalar(0, 255, 0), 4);

        return input; // Return the image that will be displayed in the viewport
    }

    @Override
    public void onViewportTapped() {
        // Executed when the image display is clicked by the mouse or tapped
        // This method is executed from the UI thread, so be careful to not
        // perform any sort heavy processing here! Your app might hang otherwise
    }

}