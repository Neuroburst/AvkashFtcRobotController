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
    String[] colorNames = {"Red", "Yellow", "Blue"};
    int[] colorHueMax = {179, 110, 20};
    int[] colorHueMin = {116, 100, 0};

    int[] colorSatMax = {255, 255, 255};
    int[] colorSatMin = {100, 100, 0};

    int[] colorValMax = {255, 255, 255};
    int[] colorValMin = {100, 100, 0};

    @Override
    public void init(Mat input) {
        // Executed before the first call to processFrame
    }

    @Override
    public Mat processFrame(Mat input) {
        Mat original = input.clone();
        //Imgproc.blur(input, input, new Size(5, 5));

        //Imgproc.Laplacian(input, input, 31);//CvType.CV_32F);


//        Mat subInput1 = new Mat();
//        Imgproc.GaussianBlur(original, subInput1, new Size(1, 1), 0);
//        Mat subInput2 = new Mat();
//        Imgproc.GaussianBlur(original, subInput2, new Size(51, 51), 0);
//        Core.subtract(subInput1, subInput2, input);

//        Mat DoH = new Mat();
//        Imgproc.GaussianBlur(input, DoH, new Size(5, 5), 0);
//        Mat Dxx = new Mat();
//        Imgproc.Sobel(DoH, Dxx, CvType.CV_64F, 2, 0);
//        Mat Dyy = new Mat();
//        Imgproc.Sobel(DoH, Dyy, CvType.CV_64F, 0, 2);
//        Mat Dxy = new Mat();
//        Imgproc.Sobel(DoH, Dxy, CvType.CV_64F, 1, 1);
//        Mat Mul = new Mat();
//        Core.multiply(Dxx, Dyy, Mul);
//        Core.multiply(Dxy, Dxy, Dxy);
//        Core.subtract(Mul, Dxy, input);

//        Mat subInput3 = new Mat();
//        Imgproc.GaussianBlur(original, subInput3, new Size(5, 5), 0);
//        Mat subInput4 = new Mat();
//        Imgproc.GaussianBlur(original, subInput4, new Size(11, 11), 0);
//        Core.subtract(subInput3, subInput4, input);
        //Mat original = input.clone();
        Mat inputHSV = new Mat();
        Imgproc.cvtColor(input, inputHSV, Imgproc.COLOR_BGR2HSV);


        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2RGB);

        // First filter out everything that isn't very saturated
        Mat sat = new Mat();
        Core.inRange(inputHSV, new Scalar(0, 100, 45), new Scalar(179, 255, 255), sat);
        Imgproc.cvtColor(sat, sat, Imgproc.COLOR_GRAY2BGR); // Convert Mask
        Core.bitwise_and(input, sat, input);
        //Core.bitwise_and(inputHSV, sat, inputHSV);



//        // Now extract the individual colors
//        Mat test = new Mat();
//        Core.inRange(inputHSV, new Scalar(0, 100, 100), new Scalar(20, 255, 255), test);
//        Imgproc.cvtColor(test, test, Imgproc.COLOR_GRAY2BGR); // Convert Mask
//
//        Core.bitwise_and(test, sat, test);
//        Mat blue = new Mat();
//        Core.inRange(inputHSV, new Scalar(0, 0, 0), new Scalar(20, 255, 255), blue);
//        Imgproc.cvtColor(blue, blue, Imgproc.COLOR_GRAY2BGR); // Convert Mask
//        Core.bitwise_and(blue, sat, blue);

        // Next mask out
//        Mat mask = new Mat();
//        Core.inRange(input, new Scalar(10, 10, 10), new Scalar(255, 255, 255), mask);
//        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2BGR); // Convert Mask
//        Core.bitwise_not(mask, mask);
//        Core.bitwise_and(input, mask, input);

        // Finally convert it back
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

        return test; // Return the image that will be displayed in the viewport
    }

    @Override
    public void onViewportTapped() {
        // Executed when the image display is clicked by the mouse or tapped
        // This method is executed from the UI thread, so be careful to not
        // perform any sort heavy processing here! Your app might hang otherwise
    }

}