package com.company;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.highgui.HighGui;

public class Main {

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // test code (do whatever in the main method, but leave loadLibrary alone)
        Mat src = Imgcodecs.imread("RIDB/IM000001_7.JPG");
        Mat dst = new Mat();
        Mat src_Gray = new Mat();
        Mat laplace = new Mat();
        // turn the src greyscale and store in dst
        resize(src,60);

        HighGui.imshow("Gaussian",src);
        HighGui.waitKey();
        bilateralFilter(src, dst);
        Imgproc.cvtColor(dst, src_Gray, Imgproc.COLOR_BGR2GRAY);


        gaussianSharpen(src_Gray,dst);
        HighGui.imshow("Gaussian",dst);
        HighGui.waitKey();
        autoContrast(dst);
        gaussianBlur(dst);
        gaussianSharpen(dst,src_Gray);
        autoContrast(src_Gray);
        laplaceTransform(src_Gray,dst);
        //adaptiveThreshold(src_Gray);


//        gaussianSharpen(dst,src_Gray);

        //resize(src_Gray, 60);

//        laplaceTransform(src_Gray,dst);
//        HighGui.imshow("Gaussian",dst);
//        HighGui.waitKey();
//        //autoContrast(src_Gray);
//        SobelTransform(src_Gray,dst);
//        HighGui.imshow("Gaussian",dst);
//        HighGui.waitKey();
//
//
//        //Mat abs_Dst = new Mat();
//       adaptiveThreshold(src_Gray);
//
//        // pipeline
//
//        //Dilate(src_Gray);
//        //Erode(src_Gray);
////       Imgcodecs.imwrite("test1_1.jpg", src_Gray);    // writes image out to file
//        HighGui.imshow("Gaussian",src_Gray);
//        HighGui.waitKey();
    }
    /**
     *
     */
    public static void Erode(Mat img)
    {
        int kernel_size = 2;
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(kernel_size+1,kernel_size+1),new Point(kernel_size,kernel_size));
        Imgproc.erode(img,img,element);
    }
    public static void Dilate(Mat img)
    {
        int kernel_size = 2;
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(kernel_size+1,kernel_size+1),new Point(kernel_size,kernel_size));
        Imgproc.dilate(img,img,element);
    }
    /**
     * Resizes a given image based on given scale.
     * @param img
     * @param scale percentage of the scaling
     */
    public static void resize(Mat img, int scale) {
        double width = (img.size().width * ((double)scale / 100));
        double height = (img.size().height * ((double)scale / 100));
        Imgproc.resize(img, img, new Size(width, height));
    }

    /**
     * Increases the contrast of the image
     * @param img
     */
    public static void autoContrast(Mat img) {
        Imgproc.equalizeHist(img, img);
    }

    /**
     * Applies adaptive threshold to the image
     * @param img
     */
    public static void adaptiveThreshold(Mat img) {
        Imgproc.adaptiveThreshold(img, img, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 11, 10);
    }
    public static void bilateralFilter(Mat img, Mat dst)
    {
        Imgproc.bilateralFilter(img, dst, 12,50,50,Core.BORDER_DEFAULT);
    }
    /**
     * Applies a gaussian blur to the image
     * @param img
     */
    public static void gaussianBlur(Mat img) {
        for(int i = 1; i < 11; i=i+2) {
            Imgproc.GaussianBlur(img, img, new Size(i, i), 1, 1, Core.BORDER_DEFAULT);
        }
    }
    /**
     * Sharpens the image
     */
    public static void gaussianSharpen(Mat img, Mat dst) {
        Imgproc.GaussianBlur(img, dst, new Size(0, 0), 3);
        Core.addWeighted(img, 2.0, dst, -1.5, 1, dst);
    }
    public static void SobelTransform(Mat img,Mat dst)
    {
        // First we declare the variables we are going to use
        int kernel_size = 9;
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;
        Mat grad_x = new Mat(), grad_y = new Mat();
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();
        //Imgproc.Scharr( src_gray, grad_x, ddepth, 1, 0, scale, delta, Core.BORDER_DEFAULT );
        Imgproc.Sobel( img, grad_x, ddepth, 1, 0, 3, scale, delta, Core.BORDER_DEFAULT );
        //Imgproc.Scharr( src_gray, grad_y, ddepth, 0, 1, scale, delta, Core.BORDER_DEFAULT );
        Imgproc.Sobel( img, grad_y, ddepth, 0, 1, 3, scale, delta, Core.BORDER_DEFAULT );
        // converting back to CV_8U
        Core.convertScaleAbs( grad_x, abs_grad_x );
        Core.convertScaleAbs( grad_y, abs_grad_y );
        Core.addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, dst );
    }
    /**
     * applies edge detection laplace version
     */
    public static void laplaceTransform(Mat img, Mat src)
    {
        int kernel_size = 3;
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;
        Imgproc.Laplacian(img,src,ddepth, kernel_size,scale,delta, Core.BORDER_DEFAULT);

        Core.convertScaleAbs(src,src);
    }
    /**
     *
     */
    public static void cannyDetection(Mat img, Mat src)
    {

    }
    /**
     * blurs the image via median algorithm
     * @param img
     */
    public static void medianBlur(Mat img)
    {
        for(int i = 1; i < 3; i=i+2)
        {
            Imgproc.medianBlur(img,img,i);
        }
    }
    public static void createHistogram(String string) {
        try {
            FileWriter writer = new FileWriter("pixelValues.txt");
            writer.write("");
            //Reading the image
            File file = new File(string);
            BufferedImage img = ImageIO.read(file);
            ArrayList<pixelArrayValues> pixelValues = new ArrayList<pixelArrayValues>();
            pixelArrayValues PAV;
            boolean match = false;
            //create array for storing the values of pixels
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    //Retrieving contents of a pixel
                    int pixel = img.getRGB(x, y);
                    //Creating a Color object from pixel value
                    Color color = new Color(pixel, true);
                    //Retrieving the R G B values
                    int red = color.getRed();
                    int green = color.getGreen();
                    int blue = color.getBlue();
                    for (int i = 0; i < pixelValues.size(); i++) {
                        if (pixelValues.get(i).getRed() == red && pixelValues.get(i)._getBlue() == blue && pixelValues.get(i)._getGreen() == green) {
                            match = true;
                            pixelValues.get(i).countUp();
                        }
                    }
                    if (match == false) {
                        PAV = new pixelArrayValues(red, blue, green);
                        pixelValues.add(PAV);
                    }
                    match = false;
                }
            }
            for (pixelArrayValues pv :
                    pixelValues) {
                writer.append(Integer.toString(pv.getRed()) + "," + Integer.toString(pv._getBlue()) + "," + Integer.toString(pv._getGreen()) + "," + Integer.toString(pv.getCount()));
                writer.append("\n");
                writer.flush();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


}

class pixelArrayValues {
    private int _red;
    private int _green;
    private int _blue;
    private int _count = 0;

    public pixelArrayValues(int red, int blue, int green) {
        _red = red;
        _green = green;
        _blue = blue;
    }
//    private void matchingMethod() {
//        Mat result = new Mat();
//        Mat img_display = new Mat();
//        img.copyTo(img_display);
//        int result_cols = img.cols() - templ.cols() + 1;
//        int result_rows = img.rows() - templ.rows() + 1;
//        result.create(result_rows, result_cols, CvType.CV_32FC1);
//        Boolean method_accepts_mask = (Imgproc.TM_SQDIFF == match_method || match_method == Imgproc.TM_CCORR_NORMED);
//        if (use_mask && method_accepts_mask) {
//            Imgproc.matchTemplate(img, templ, result, match_method, mask);
//        } else {
//            Imgproc.matchTemplate(img, templ, result, match_method);
//        }
//        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
//        Point matchLoc;
//        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
//        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
//            matchLoc = mmr.minLoc;
//        } else {
//            matchLoc = mmr.maxLoc;
//        }
//        Imgproc.rectangle(img_display, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
//                new Scalar(0, 0, 0), 2, 8, 0);
//        Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
//                new Scalar(0, 0, 0), 2, 8, 0);
//        Image tmpImg = HighGui.toBufferedImage(img_display);
//        ImageIcon icon = new ImageIcon(tmpImg);
//        imgDisplay.setIcon(icon);
//        result.convertTo(result, CvType.CV_8UC1, 255.0);
//        tmpImg = HighGui.toBufferedImage(result);
//        icon = new ImageIcon(tmpImg);
//        resultDisplay.setIcon(icon);
//    }


    public void countUp() {
        _count++;
    }

    public int getCount() {
        return _count;
    }

    public int getRed() {
        return _red;
    }

    public int _getBlue() {
        return _blue;
    }

    public int _getGreen() {
        return _green;
    }
}