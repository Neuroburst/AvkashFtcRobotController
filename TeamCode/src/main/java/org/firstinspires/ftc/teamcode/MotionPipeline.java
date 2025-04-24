package org.firstinspires.ftc.teamcode;

import org.opencv.imgproc.Moments;
import org.opencv.video.Video;
import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

// TODO: get color of objects, clean centroid path

public class MotionPipeline extends OpenCvPipeline {
    public final Boolean debugOverlay = false;
    private BackgroundSubtractorMOG2 backSub = Video.createBackgroundSubtractorMOG2();

    private List<Point> centroidHist = new ArrayList<Point>();

//    String[] colorNames = {"Red", "Yellow", "Blue"};
//    Scalar[] colorStyles = {new Scalar(255,0,0), new Scalar(255,255,0), new Scalar(0,0,255)};
//    // Color Thresholds
//    Scalar[] colorMax = {new Scalar(179, 255, 255), new Scalar(110, 255, 255), new Scalar(20, 255, 255)};
//    Scalar[] colorMin = {new Scalar(116, 100, 100), new Scalar(90, 110, 100), new Scalar(0, 50, 0)};

    @Override
    public void init(Mat input) {
        // Reset background subtractor and centroid history
        backSub = Video.createBackgroundSubtractorMOG2();
        centroidHist.clear();
    }

    @Override
    public Mat processFrame(Mat input) {
        // Fix weird errors
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2BGR);
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2RGB);
        Mat output = input;

        //Apply to the background subtractor
        Mat mask = new Mat();
        backSub.apply(input, mask);
        // Obtain colored version
        Mat maskHSV = new Mat();
        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2RGB);
        Core.bitwise_and(input, mask, maskHSV);
        Imgproc.cvtColor(maskHSV, maskHSV, Imgproc.COLOR_RGB2HSV);
        // Filter out desaturated and dark stuff
        Core.inRange(maskHSV, new Scalar(0,90,100), new Scalar(255,255,255), mask);
        Imgproc.blur(mask, mask, new Size(3,3));

        //Update colored version
//        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2RGB);
//        Core.bitwise_and(input, mask, maskHSV);
//        Imgproc.cvtColor(maskHSV, maskHSV, Imgproc.COLOR_RGB2HSV);
//        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_RGB2GRAY);

        // Remove shadows
        //Imgproc.threshold(mask, mask, 200, 255, Imgproc.THRESH_BINARY);
//        Imgproc.blur(mask, mask, new Size(3,3));

//        // set the kernel
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2));
//        // Apply erosion
//        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
       // Apply erosion
//        float erodeSize = 1f;
//        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
//                new Size( 2*erodeSize + 1, 2*erodeSize+1 ),
//                new Point( erodeSize, erodeSize ) );
//        Imgproc.erode(mask, mask, erodeElement);
//        //Imgproc.dilate(mask, mask, erodeElement);

        List<MatOfPoint> allContours = new ArrayList<>();
        // Detect contours
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, allContours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);


        // Get largest contour as well
        MatOfPoint largestContour = null;
        double largestContourArea = 0f;
        // Filter contours
        List<MatOfPoint> contours = new ArrayList<>();
        for (MatOfPoint contour : allContours) {
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea > 100) {
                contours.add(contour);

                if (contourArea > largestContourArea) {
                    largestContour = contour;
                    largestContourArea = contourArea;
                }
            }
        }

        // Plot centroid history with largest contour
        if (largestContour != null) {
            Moments moments = Imgproc.moments(largestContour);
            Point centroid = new Point(moments.m10 / moments.m00, moments.m01 / moments.m00);
            centroidHist.add(centroid);

            MatOfPoint m = new MatOfPoint();
            m.fromList(centroidHist);
            ArrayList<MatOfPoint> poly = new ArrayList<>();
            poly.add(m);

            Imgproc.drawMarker(output, centroid, new Scalar(255, 255, 0));
            Imgproc.polylines(output, poly, false, new Scalar(255, 255, 0));

            // Draw boxes around identified contours
            Rect rect = Imgproc.boundingRect(largestContour);
            Imgproc.rectangle(output, rect, new Scalar(255,0,0));
        }else{
            // Clear history if lost
            centroidHist.clear();
        }

        // Draw contours for testing
        if (debugOverlay)
            Imgproc.drawContours(output, contours, -1, new Scalar(255,0,0));


        return output;
    }

    @Override
    public void onViewportTapped() {
        // Executed when the image display is clicked by the mouse or tapped
        // This method is executed from the UI thread, so be careful to not
        // perform any sort heavy processing here! Your app might hang otherwise
    }
}