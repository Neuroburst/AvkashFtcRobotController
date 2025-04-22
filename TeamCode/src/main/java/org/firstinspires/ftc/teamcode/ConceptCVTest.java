package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.SortOrder;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.opencv.core.RotatedRect;

import java.util.List;


@TeleOp(name = "Testing OpenCV")
public class ConceptCVTest extends LinearOpMode
{
    private VisionPortal visionPortal;

    @Override
    public void runOpMode()
    {
        ColorBlobLocatorProcessor colorLocator = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.BLUE)         // use a predefined color match
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)    // exclude blobs inside blobs
                .setRoi(ImageRegion.asUnityCenterCoordinates(-0.5, 0.5, 0.5, -0.5))  // search central 1/4 of camera view
                .setDrawContours(true)                        // Show contours on the Stream Preview
                .setBlurSize(5)                               // Smooth the transitions between different colors in image
                .build();
        visionPortal = new VisionPortal.Builder()
                .addProcessor(colorLocator)
                .setCameraResolution(new Size(320, 240))
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .build();
        // Read the current list
        //List<ColorBlobLocatorProcessor.Blob> blobs = colorLocator.getBlobs();
//        ColorBlobLocatorProcessor.Util.filterByArea(50, 20000, blobs);  // filter out very small blobs.
//        telemetry.addLine(" Area Density Aspect  Center");
//
//        // Display the size (area) and center location for each Blob.
//        for(ColorBlobLocatorProcessor.Blob b : blobs)
//        {
//            RotatedRect boxFit = b.getBoxFit();
//            telemetry.addLine(String.format("%5d  %4.2f   %5.2f  (%3d,%3d)",
//                    b.getContourArea(), b.getDensity(), b.getAspectRatio(), (int) boxFit.center.x, (int) boxFit.center.y));
//        }
//        telemetry.addData(">", Integer.toString(blobs.size()));
//        telemetry.update();
//
        waitForStart();

        if (opModeIsActive()) {
            // ...
        }
        visionPortal.close();

    }

//    class TestPipeline extends OpenCvPipeline
//    {
//        @Override
//        public void init(Mat input) {
//            // Executed before the first call to processFrame
//        }
//
//        @Override
//        public Mat processFrame(Mat input) {
//            // Executed every time a new frame is dispatched
//
//            return input; // Return the image that will be displayed in the viewport
//            // (In this case the input mat directly)
//        }
//
//        @Override
//        public void onViewportTapped() {
//            // Executed when the image display is clicked by the mouse or tapped
//            // This method is executed from the UI thread, so be careful to not
//            // perform any sort heavy processing here! Your app might hang otherwise
//        }
//    }
}