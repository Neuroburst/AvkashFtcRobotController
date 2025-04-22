package org.firstinspires.ftc.teamcode;

import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;


import java.util.List;

public class TestPipeline extends OpenCvPipeline {
    String[] colorNames = {"Red", "Yellow", "Blue"};
    Scalar[] colorStyles = {new Scalar(255,0,0), new Scalar(255,255,0), new Scalar(0,0,255)};

    Scalar[] colorMax = {new Scalar(179, 255, 255), new Scalar(110, 255, 255), new Scalar(20, 255, 255)};
    Scalar[] colorMin = {new Scalar(116, 100, 100), new Scalar(100, 100, 100), new Scalar(0, 0, 0)};

    @Override
    public void init(Mat input) {
        // Executed before the first call to processFrame
    }

    @Override
    public Mat processFrame(Mat input) {
        // Convert colors otherwise it errors
        Imgproc.blur(input, input, new Size(2,2));
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2BGR);
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2RGB);
        Mat frame = input.clone();

        // Create HSV version
        Mat inputHSV = new Mat();
        Imgproc.cvtColor(frame, inputHSV, Imgproc.COLOR_BGR2HSV);

        // Filter out everything that isn't very saturated
        Mat sat = new Mat();
        Core.inRange(inputHSV, new Scalar(0, 100, 45), new Scalar(179, 255, 255), sat);
        Imgproc.cvtColor(sat, sat, Imgproc.COLOR_GRAY2BGR); // Convert Mask
        Core.bitwise_and(frame, sat, frame);


        // Create blank output mat
        Mat debugOutput = new Mat();
        Core.inRange(frame, new Scalar(255,255,255), new Scalar(0,0,0), debugOutput);

        String outputText = "";

        // Configure parameters
        SimpleBlobDetector_Params params = new SimpleBlobDetector_Params();
        params.set_collectContours(true);
        params.set_filterByColor(false);
        params.set_minThreshold(10);
        params.set_maxThreshold(255);
        params.set_filterByArea(true);
        params.set_minArea(50);
        params.set_maxArea(100000);
        params.set_filterByCircularity(false);
        params.set_filterByConvexity(false);
        params.set_filterByInertia(false);
        params.set_minDistBetweenBlobs(50.0f);

        int idx = 0;
        for (String color : colorNames)
        {
            Mat colorMask = new Mat();
            Core.inRange(inputHSV, colorMin[idx], colorMax[idx], colorMask);
            Imgproc.cvtColor(colorMask, colorMask, Imgproc.COLOR_GRAY2BGR); // Convert Mask
            Core.bitwise_and(colorMask, sat, colorMask);
            SimpleBlobDetector detector = SimpleBlobDetector.create(params);
            MatOfKeyPoint keyPoints = new MatOfKeyPoint();
            detector.detect(colorMask, keyPoints);
            List<MatOfPoint> contours = detector.getBlobContours();

            Features2d.drawKeypoints(debugOutput, keyPoints, debugOutput, colorStyles[idx]);
            Imgproc.polylines(debugOutput, contours, true, colorStyles[idx]);
            outputText = outputText.concat(color + ": " + Integer.toString(contours.size()) + " ");
            idx++;
        }

        // Print Debug text
        Imgproc.putText(debugOutput, outputText, new Point(0,20), 0, 0.75, new Scalar(255,0,0), 2);

////        Imgproc.rectangle(
////                input,
////                new Point(
////                        input.cols()/4,
////                        input.rows()/4),
////                new Point(
////                        input.cols()*(3f/4f),
////                        input.rows()*(3f/4f)),
////                new Scalar(0, 255, 0), 4);
        Mat output = new Mat();
        Core.add(input, debugOutput, output);
        return output; // Return the image that will be displayed in the viewport
    }

    @Override
    public void onViewportTapped() {
        // Executed when the image display is clicked by the mouse or tapped
        // This method is executed from the UI thread, so be careful to not
        // perform any sort heavy processing here! Your app might hang otherwise
    }

}