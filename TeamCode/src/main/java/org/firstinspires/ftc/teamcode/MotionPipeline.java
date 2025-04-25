package org.firstinspires.ftc.teamcode;

import org.opencv.imgproc.Moments;
import org.opencv.video.KalmanFilter;
import org.opencv.video.Video;
import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MotionPipeline extends OpenCvPipeline {
    // Overlay the contours
    public final Boolean debugOverlay = false;
    // Whether to show the unsmoothed
    public final Boolean showUnsmoothed = false;

    // The size of the moving average window
    public final int MASize = 5;

    // The background subtractor
    private BackgroundSubtractorMOG2 backSub = Video.createBackgroundSubtractorMOG2();

    // The history of the centroid (smoothed and unsmoothed respectively)
    private ArrayList<Point> centroidHist = new ArrayList<Point>();
    private ArrayList<Point> centroidHistRaw = new ArrayList<Point>();

    // The queue for the moving average
    private ArrayList<Point> queue = new ArrayList<>();


    @Override
    public void init(Mat input) {
        // Reset background subtractor and centroid history
        backSub = Video.createBackgroundSubtractorMOG2();
        centroidHist.clear();
        centroidHistRaw.clear();
        queue.clear();
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

        // Plot centroid history with largest contour using the moving average to smooth it out
        if (largestContour != null) {
            Moments moments = Imgproc.moments(largestContour);
            Point centroid = new Point(moments.m10 / moments.m00, moments.m01 / moments.m00);
            queue.add(centroid);
            centroidHistRaw.add(centroid);

            if (queue.size() == MASize){
                Point avg = new Point();
                for (Point point : queue){
                    avg.x += point.x;
                    avg.y += point.y;
                }
                avg.x /= MASize;
                avg.y /= MASize;

                centroidHist.add(avg);
                queue.remove(0);
            }


            // Display centroid
            Imgproc.drawMarker(output, centroid, new Scalar(255, 255, 0));

            // Display centroid path
            MatOfPoint m = new MatOfPoint();
            m.fromList(centroidHist);
            ArrayList<MatOfPoint> poly = new ArrayList<>();
            poly.add(m);
            Imgproc.polylines(output, poly, false, new Scalar(255, 255, 0));

            if (showUnsmoothed){
                // Display centroid path
                MatOfPoint mRaw = new MatOfPoint();
                mRaw.fromList(centroidHistRaw);
                ArrayList<MatOfPoint> polyRaw = new ArrayList<>();
                polyRaw.add(mRaw);
                Imgproc.polylines(output, polyRaw, false, new Scalar(0, 255, 255));
            }

            // Draw boxes around identified contours
            Rect rect = Imgproc.boundingRect(largestContour);
            Imgproc.rectangle(output, rect, new Scalar(255,0,0));
        }else{
            // Clear history if lost
            centroidHist.clear();
            centroidHistRaw.clear();
            queue.clear();
        }

        // Draw contours for testing
        if (debugOverlay)
            Imgproc.drawContours(output, contours, -1, new Scalar(255,0,0));
            Imgproc.putText(output, Integer.toString(centroidHist.size()), new Point(0, 50), 0, 1, new Scalar(255,255,255));


        return output;
    }

    @Override
    public void onViewportTapped() {
        // Executed when the image display is clicked by the mouse or tapped
        // This method is executed from the UI thread, so be careful to not
        // perform any sort heavy processing here! Your app might hang otherwise
    }
}