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

// TODO: need to adjust edge cutting

public class TestPipeline extends OpenCvPipeline {
    String[] colorNames = {"Red", "Yellow", "Blue"};
    Scalar[] colorStyles = {new Scalar(255,0,0), new Scalar(255,255,0), new Scalar(0,0,255)};

    Scalar[] colorMax = {new Scalar(179, 255, 255), new Scalar(110, 255, 255), new Scalar(20, 255, 255)};
    Scalar[] colorMin = {new Scalar(116, 100, 100), new Scalar(100, 110, 100), new Scalar(0, 0, 0)};

    String outputText = "";

    @Override
    public void init(Mat input) {
        // Executed before the first call to processFrame
    }

    @Override
    public Mat processFrame(Mat input) {
        // Convert colors otherwise it errors
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2BGR);
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2RGB);

        // Blur it
        Imgproc.blur(input, input, new Size(2,2));
        Mat frame = input.clone();
        Mat output = input.clone();

        // Create HSV version
        Mat inputHSV = new Mat();
        Imgproc.cvtColor(frame, inputHSV, Imgproc.COLOR_BGR2HSV);

        // Filter out everything that isn't very saturated
        Mat sat = new Mat();
        Core.inRange(inputHSV, new Scalar(0, 100, 45), new Scalar(179, 255, 255), sat);
        Imgproc.cvtColor(sat, sat, Imgproc.COLOR_GRAY2BGR); // Convert Mask
        Core.bitwise_and(frame, sat, frame);

        // Create blank output matrix
        Mat debugOutput = Mat.zeros(frame.rows(), frame.cols(), input.type());

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
        params.set_minDistBetweenBlobs(50f);

        int idx = 0;
        for (String color : colorNames)
        {
            // Get image of just that color
            Mat colorMask = new Mat();
            Core.inRange(inputHSV, colorMin[idx], colorMax[idx], colorMask);
            Imgproc.cvtColor(colorMask, colorMask, Imgproc.COLOR_GRAY2BGR); // Convert Mask
            Core.bitwise_and(colorMask, sat, colorMask);

//            Mat colorMaskedFrame = new Mat();
//            Core.bitwise_and(frame, colorMask, colorMaskedFrame);
//            // Cut out edges of objects using the difference of Gaussians filter
//            Mat DoG = new Mat();
//            Mat subInput3 = new Mat();
//            Imgproc.GaussianBlur(colorMaskedFrame, subInput3, new Size(1, 1), 0);
//            Mat subInput4 = new Mat();
//            Imgproc.GaussianBlur(colorMaskedFrame, subInput4, new Size(3, 3), 0);
//            Core.subtract(subInput3, subInput4, DoG);
//
//            Imgproc.cvtColor(DoG, DoG, Imgproc.COLOR_BGR2GRAY); // Convert Mask
//            Core.inRange(DoG, new Scalar(5, 5, 5), new Scalar(255, 255, 255), DoG);
//            Core.bitwise_not(DoG, DoG);
//            Imgproc.cvtColor(DoG, DoG, Imgproc.COLOR_GRAY2BGR); // Convert Mask
//            Core.bitwise_and(DoG, colorMask, colorMask);
//
//            if (color == "Yeellow"){
//                return colorMask;
//            }

            SimpleBlobDetector detector = SimpleBlobDetector.create(params);
            MatOfKeyPoint keyPoints = new MatOfKeyPoint();
            detector.detect(colorMask, keyPoints);
            List<MatOfPoint> contours = detector.getBlobContours();
            for (MatOfPoint blob : contours){
                Point[] extents = getContourExtents(blob);
                Imgproc.rectangle(output, extents[0], extents[1], colorStyles[idx], 1);
            }

            Features2d.drawKeypoints(debugOutput, keyPoints, debugOutput, colorStyles[idx]);
            Imgproc.drawContours(debugOutput, contours, -1, colorStyles[idx]);
            outputText = outputText.concat(color + ": " + contours.size() + " ");
            idx++;
        }

        // Print Debug text
        Imgproc.putText(debugOutput, outputText, new Point(0,20), 0, 0.75, new Scalar(255,255,255), 2);

        //Core.add(output, debugOutput, output);
        return output; // Return the image that will be displayed in the viewport
    }

    private Point[] getContourExtents(MatOfPoint contour){
        Point[] points = contour.toArray();

        double xMax = -1;
        double xMin = -1;
        double yMax = -1;
        double yMin = -1;

        for (Point point : points){
            if (point.x > xMax || xMax == -1) {
                xMax = point.x;
            }
            if (point.x < xMin || xMin == -1){
                xMin = point.x;
            }
            if (point.y > yMax || yMax == -1){
                yMax = point.y;
            }
            if (point.y < yMin || yMin == -1){
                yMin = point.y;
            }
        }

        return new Point[]{new Point(xMax, yMax), new Point(xMin, yMin)};
    }

    @Override
    public void onViewportTapped() {
        // Executed when the image display is clicked by the mouse or tapped
        // This method is executed from the UI thread, so be careful to not
        // perform any sort heavy processing here! Your app might hang otherwise
    }

}