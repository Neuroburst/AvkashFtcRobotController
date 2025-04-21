package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;


@TeleOp(name = "Testing OpenCV")
public class ConceptCVTest extends LinearOpMode
{
    @Override
    public void runOpMode()
    {

    }

    class TestPipeline extends OpenCvPipeline
    {
        @Override
        public void init(Mat input) {
            // Executed before the first call to processFrame
        }

        @Override
        public Mat processFrame(Mat input) {
            // Executed every time a new frame is dispatched

            return input; // Return the image that will be displayed in the viewport
            // (In this case the input mat directly)
        }

        @Override
        public void onViewportTapped() {
            // Executed when the image display is clicked by the mouse or tapped
            // This method is executed from the UI thread, so be careful to not
            // perform any sort heavy processing here! Your app might hang otherwise
        }
    }
}