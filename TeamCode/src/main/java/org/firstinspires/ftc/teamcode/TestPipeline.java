package org.firstinspires.ftc.teamcode;

import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

// TODO: need to fix separate objects combining

public class TestPipeline extends OpenCvPipeline {
    public final Boolean debugOverlay = false;
    public final Boolean labels = true;
    public final double erodeThreshold = 3000;
    // Color info
    String[] colorNames = {"Red", "Yellow", "Blue"};
    Scalar[] colorStyles = {new Scalar(255,0,0), new Scalar(255,255,0), new Scalar(0,0,255)};
    // Color Thresholds
    Scalar[] colorMax = {new Scalar(179, 255, 255), new Scalar(110, 255, 255), new Scalar(20, 255, 255)};
    Scalar[] colorMin = {new Scalar(116, 100, 100), new Scalar(90, 110, 100), new Scalar(0, 50, 0)};

    String outputText = "";

    @Override
    public void init(Mat input) {
        // Executed before the first call to processFrame
    }

    @Override
    public Mat processFrame(Mat input) {
        outputText = "";
        // Convert colors otherwise it errors
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2BGR);
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2RGB);

        // Create the frame and output
        //Imgproc.blur(input, input, new Size(2,2));
        Mat frame = input.clone();
        Mat output = input.clone();
        // Create blank debug output matrix
        Mat debugOutput = Mat.zeros(frame.size(), input.type());
        // Create HSV version
        Mat inputHSV = new Mat();
        Imgproc.cvtColor(frame, inputHSV, Imgproc.COLOR_BGR2HSV);


        // Individually process each color
        int colorIdx = 0;
        for (String color : colorNames)
        {
            // Get image of just that color
            Mat colorMask = new Mat();
            Core.inRange(inputHSV, colorMin[colorIdx], colorMax[colorIdx], colorMask);
            Mat originalColorMask = colorMask.clone();
            Imgproc.cvtColor(colorMask, colorMask, Imgproc.COLOR_GRAY2BGR); // Convert Mask to AND it
            Core.bitwise_and(frame, colorMask, colorMask);

            Imgproc.cvtColor(colorMask, colorMask, Imgproc.COLOR_BGR2GRAY); // Convert Mask to AND it
            Imgproc.equalizeHist(colorMask, colorMask);
            Imgproc.cvtColor(colorMask, colorMask, Imgproc.COLOR_GRAY2BGR); // Convert Mask to AND it

            ///Imgproc.blur(colorMaskedFrame, colorMaskedFrame, new Size(5,5));

            Imgproc.cvtColor(colorMask, colorMask, Imgproc.COLOR_BGR2GRAY); // Get grayscale version
            // Cut out darker areas using adaptive threshold
            Imgproc.adaptiveThreshold(colorMask, colorMask, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 85, 5);
            // Remove stuff that's on the outside
            Core.bitwise_and(colorMask, originalColorMask, colorMask);

            // Basic erode to get rid of junk
            float basicErodeSize = 2f;
            Mat basicErodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                    new Size( 2*basicErodeSize + 1, 2*basicErodeSize+1 ),
                    new Point( basicErodeSize, basicErodeSize ) );
            Imgproc.erode(colorMask, colorMask, basicErodeElement);
            //Imgproc.erode(colorMask, colorMask, basicErodeElement);
            //Imgproc.dilate(colorMask, colorMask, basicErodeElement);

            if (color == "Yeellow"){
                return colorMask;
            }
            List<MatOfPoint> finalContours = new ArrayList<>();


            // Find initial contours
            List<MatOfPoint> initialContours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(colorMask, initialContours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

            // Iterate through initial contours
            int contourIdx = 0;
            for (MatOfPoint blob : initialContours){
                Mat contourMask = Mat.zeros(colorMask.size(), colorMask.type());
                Imgproc.drawContours(contourMask, initialContours, contourIdx, new Scalar(255,255,255),-1);
                //Imgproc.cvtColor(contourMask, contourMask, Imgproc.COLOR_BGR2GRAY); // Convert Mask to AND it
                double contourArea = Imgproc.contourArea(blob);
//                MatOfInt hull = new MatOfInt();
//                Imgproc.convexHull(blob, hull);
//                MatOfInt4 defects = new MatOfInt4();
//                Imgproc.convexityDefects(blob, hull, defects);


                // filter out junk
                if (contourArea < 50) {
                }
                // If the contour is above the erode threshold, erode it and scan for more blob children
                else if (contourArea > erodeThreshold){

                    float selectiveErodeSize = 0f;
                    if (contourArea < 6000){
                        selectiveErodeSize = 1f;
                    }
                    else if (contourArea < 8000){
                        selectiveErodeSize = 1.5f;
                    }
                    else if (contourArea < 10000){
                        selectiveErodeSize = 2.0f;
                    }
                    else if (contourArea < 12000){
                        selectiveErodeSize = 2.5f;
                    }
                    else if (contourArea < 14000){
                        selectiveErodeSize = 3.0f;
                    }else{
                        selectiveErodeSize = 3.5f;
                    }
                    Mat selectiveErodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                            new Size( 2*selectiveErodeSize + 1, 2*selectiveErodeSize+1 ),
                            new Point( selectiveErodeSize, selectiveErodeSize ) );
                    //Imgproc.erode(contourMask, contourMask, selectiveErodeElement);
                    //Imgproc.erode(contourMask, contourMask, selectiveErodeElement);
                    Imgproc.dilate(contourMask, contourMask, selectiveErodeElement);

                    List<MatOfPoint> newContours = new ArrayList<>();
                    Imgproc.findContours(contourMask, newContours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

                    finalContours.addAll(newContours);
                }else{

                    finalContours.add(blob);
                }

                contourIdx++;
            }
            for (MatOfPoint blob : finalContours) {
                // Drawing
                Point[] extents = getContourExtents(blob);
                Imgproc.rectangle(output, extents[0], extents[1], colorStyles[colorIdx], 1);
                if (labels) {
                    Imgproc.putText(output, color, new Point(extents[1].x, extents[1].y - 2), 1, 0.5, new Scalar(255, 255, 255), 1);
                }
            }
            Imgproc.drawContours(debugOutput, finalContours, -1, colorStyles[colorIdx]);
            //outputText = outputText.concat(color + ": " + keyPoints.rows() + " ");

            colorIdx++;
        }

        // Print Debug text
        Imgproc.putText(debugOutput, outputText, new Point(0,20), 0, 0.75, new Scalar(255,255,255), 2);

        if (debugOverlay){
            Core.add(output, debugOutput, output);
        }
        return output; // Return the image that will be displayed in the viewport
    }
    // Get the extents of the contour to draw a box around them
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