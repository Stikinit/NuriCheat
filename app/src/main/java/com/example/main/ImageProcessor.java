package com.example.main;

import android.app.Activity;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ImageProcessor {

    private final Size procInput = new Size(250, 250);
    private final Size cnnInput = new Size(28, 28);
    private final double scale_factor = 1.1;

    private Classifier classifier;

    public ImageProcessor(Activity activity) {
        super();
        classifier = new Classifier(activity);
    }

    public Mat focus_and_resize(Mat img) {
        // adjust the image
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY); // grayscale
        double scale = 500.0/img.width();
        Imgproc.resize(img, img, new Size(), scale, scale);
        Mat thresh = new Mat(img.size(), img.type());
        Imgproc.adaptiveThreshold(img, thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 157, 20);

        Mat openKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2));
        Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_OPEN, openKernel, new Point(-1,-1), 1);

        Mat dilateKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
        Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_DILATE, dilateKernel, new Point(-1,-1), 7);

        // find the biggest contour (c_max) by the area
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Optional<MatOfPoint> c_max_opt = contours.parallelStream().reduce((c1, c2) -> Imgproc.contourArea(c1) > Imgproc.contourArea(c2) ? c1 : c2); // NOUGAT BOUND
        MatOfPoint c_max = c_max_opt.get();

        // submat using the biggest contour
        Rect rect = Imgproc.boundingRect(c_max);
        Mat result = img.submat(rect);
        Imgproc.resize(result, result, procInput);

        return result;
    }

    private double black_px_percentage(Mat img) {
        int notblack = Core.countNonZero(img);
        double px = img.size().area();
        double black = px - notblack;

        return black/px;
    }

    public List<Integer> getMatrix(Mat img) throws IOException, IllegalStateException {
        img = focus_and_resize(img);
        Mat thresh = new Mat(img.size(), img.type());
        Imgproc.adaptiveThreshold(img, thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 57, 2);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        List<MatOfPoint> singleC = new ArrayList<MatOfPoint>();
        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        double area = 0.0;
        for (MatOfPoint c: contours) {
            area = Imgproc.contourArea(c);
            if (area < 1000) {
                singleC.add(c);
                Imgproc.drawContours(thresh, singleC, -1, new Scalar(0,0,0), -1);
            }
        }

        Mat verticalKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1,5));
        Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_CLOSE, verticalKernel, new Point(-1,-1), 4);

        Mat horizontalKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,1));
        Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_CLOSE, horizontalKernel, new Point(-1,-1), 4);

        Mat dilateKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
        Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_DILATE, dilateKernel, new Point(-1,-1), 2);

        // sort by top to bottom and each row by left to right
        contours.clear();
        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        if(contours.size() > 27) {
            throw new IllegalStateException("ValueError: the script failed to detect 25 cells, it detected: "+contours.size());
        }
        int bad_cont = contours.size() - 25;
        if (bad_cont < 0) {
            //ERROR
            throw new IllegalStateException("ValueError: the script failed to detect 25 cells, it detected: "+contours.size());
        }
        contours = contours.subList(bad_cont, contours.size());

        contours = sortContours(contours);
        if (contours.size() != 25) {
            //ERROR
            throw new IllegalStateException("ValueError: the script failed to detect 25 cells, it detected: "+contours.size());
        }

        // prepare the input image
        Mat prepImage = new Mat();
        Imgproc.adaptiveThreshold(img, prepImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 57, 20);

        // Build the matrix 8|
        List<Integer> resList = new ArrayList<Integer>();

        for (MatOfPoint c: contours) {
            Rect rect = Imgproc.boundingRect(c);
            Mat number = prepImage.submat(rect);


            Imgproc.resize(number, number, cnnInput);

            double percentage = black_px_percentage(number);
            if (percentage > 0.99) {
                resList.add(0);
            }
            else {
                resList.add(classifier.classifyMat(number));
            }
        }

        return resList;
    }

    private List<MatOfPoint> sortContours(List<MatOfPoint> contours) {
        List<MatOfPoint> res = new ArrayList<MatOfPoint>();
        List<MatOfPoint> row = new ArrayList<MatOfPoint>();
        // compare vertically
        Collections.sort(contours, (o1, o2) -> {
            Rect rect1 = Imgproc.boundingRect(o1);
            Rect rect2 = Imgproc.boundingRect(o2);
            int result = Double.compare(rect1.tl().y, rect2.tl().y);
            return result;
        });
        // compare horizontally
        int idx=1;
        for (MatOfPoint c: contours) {
            double area = Imgproc.contourArea(c);
            if (area < 50000) {
                row.add(c);
                if (idx % 5 == 0) {
                    Collections.sort(row, (o1, o2) -> {
                        Rect rect1 = Imgproc.boundingRect(o1);
                        Rect rect2 = Imgproc.boundingRect(o2);
                        int result = Double.compare(rect1.tl().x, rect2.tl().x);
                        return result;
                    });
                    res.addAll(row);
                    row.clear();
                }
            }
            idx++;
        }

        return res;
    }

}









