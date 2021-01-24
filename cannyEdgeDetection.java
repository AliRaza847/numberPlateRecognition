package com.example.numberplaterecog;
import  android.graphics.Bitmap;
import android.graphics.Color;
import java.util.Arrays;
import java.util.PrimitiveIterator;

public class cannyEdgeDetection{
    private  final static float GAUSSIAN_CUT_OFF = 0.005f;
    private  final static float MAGNITUDE_LIMIT = 100f;
    private final static float MAGNITUDE_SCALE = 100F;
    private final static int MAGNITUDE_MAX = (int) (MAGNITUDE_SCALE * MAGNITUDE_LIMIT);
    private  int height;
    private  int width;
    private  int[] data;
    private  int[] magnitude;
    private Bitmap sourceImage;
    private  Bitmap edgesImage;
    int picsize;


    private float gaussianKernelRadius;
    private  float lowThreshold;
    private  float highThreshold;
    private int gaussianKernelWidth;
    private boolean contrastNormalized;

    private  float[] xConv;
    private  float[] yConv;
    private  float[] xGradient;
    private float[] yGradient;
    public void process() {
        width = sourceImage.getWidth();
        height = sourceImage.getHeight();
        picsize = width * height;
        initArrays();
        readLuminance();
        if (contrastNormalized) normalizeContrast();
        computeGradients(gaussianKernelRadius, gaussianKernelWidth);
        int low = Math.round(lowThreshold * MAGNITUDE_SCALE);
        int high = Math.round( highThreshold * MAGNITUDE_SCALE);
        performHysteresis(low, high);
        thresholdEdges();
        writeEdges();

    }
    private void initArrays() {
        if (data == null || picsize != data.length) {
            data = new int[picsize];
            magnitude = new int[picsize];

            xConv = new float[picsize];
            yConv = new float[picsize];
            xGradient = new float[picsize];
            yGradient = new float[picsize];
        }
    }


    private void computeGradients(float kernelRadius, int kernelWidth ) {
        lowThreshold = 2.5f;
        highThreshold = 7.5f;
        gaussianKernelRadius = 2f;
        gaussianKernelWidth = 16;
        contrastNormalized = false;


        float kernel[] = new float[kernelWidth];
        float diffKernel[] = new float[kernelWidth];
        int kwidth;
        for (kwidth = 0; kwidth < kernelWidth; kwidth++)
        {
            float g1 = gaussian(kwidth, kernelRadius);
            if (g1 <= GAUSSIAN_CUT_OFF && kwidth >= 2)
                break;
            float g2 = gaussian(kwidth - 0.5f, kernelRadius);
            float g3 = gaussian(kwidth + 0.5f, kernelRadius);
            kernel[kwidth] = (g1 + g2 + g3) / 3f / (2f * (float) Math.PI * kernelRadius * kernelRadius);
        }
        int initX = kwidth - 1;
        int maxX = width - (kwidth - 1);
        int initY = width * (kwidth - 1);
        int maxY = width * (height - (kwidth - 1));
        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y += width) {
                int index = x + y;
                float sumX = data[index] * kernel[0];
                float sumY = sumX;
                int xOffset = 1;
                int yOffset = width;
                for (; xOffset < kwidth; ) {
                    sumY += kernel[xOffset] * (data[index - yOffset] + data[index + yOffset]);
                    sumX += kernel[xOffset] * (data[index - xOffset] + data[index + xOffset]);
                    yOffset += width;
                    xOffset++;
                }
                yConv[index] = sumY;
                xConv[index] = sumX;
            }
        }
            for (int x = initX; x < maxX; x++)
            {
                for (int y = initY; y < maxY; y += width) {
                    float sum = 0f;
                    int index = x + y;
                    for (int i = 1; i < kwidth; i++)
                        sum += diffKernel[i] * (yConv[index - i] - yConv[index + i]);

                    xGradient[index] = sum;
                }

            }
        for(int x = kwidth; x< width - kwidth; x++)
        {
            for(int y = initY; y< maxY; y = y + width)
            {
                float sum = 0.0f;
                int index = x + y;
                int yOffset = width;
                for(int i = 1; i < kwidth; i++)
                {
                    sum = sum + diffkernel[i] * (xConv[index - yOffset] - xConv[index*yOffset] );
                    yOffset = yOffset + width;

                }
                yGradient[index] = sum;
            }
        }
        initX = kwidth;
        maxX = width - kwidth;
        initY = width * kwidth;
        maxY = width * (height - kwidth);
        for(int x = initX; x< maxX ; x++)

        {
            for(int y = initY; y< maxY; y = y + width)
            {
                int index = x + y;
                int indexN = index - width;
                int indexS = index + width;
                int indexW = index - 1;
                int indexE = index + 1;
                int indexNW = indexN - 1;
                int indexNE = indexN + 1;
                int indexSW = indexS - 1;
                int indexSE = indexS + 1;

                float xGrad = xGradient[index];
                float yGrad = yGradient[index];
                float gradMag = hypot(xGrad, yGrad);
                float nMag = hypot(xGradient[indexN], yGradient[indexN]);
                float sMag = hypot(xGradient[indexS], yGradient[indexS]);
                float wMag = hypot(xGradient[indexW], yGradient[indexW]);
                float eMag = hypot(xGradient[indexE], yGradient[indexE]);
                float neMag = hypot(xGradient[indexNE], yGradient[indexNE]);
                float seMag = hypot(xGradient[indexSE], yGradient[indexSE]);
                float swMag = hypot(xGradient[indexSW], yGradient[indexSW]);
                float nwMag = hypot(xGradient[indexNW], yGradient[indexNW]);
                float tmp;
                if (xGrad * yGrad <= (float) 0 /*(1)*/
                        ? Math.abs(xGrad) >= Math.abs(yGrad) /*(2)*/
                        ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * neMag - (xGrad + yGrad) * eMag) /*(3)*/
                        && tmp > Math.abs(yGrad * swMag - (xGrad + yGrad) * wMag) /*(4)*/
                        : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * neMag - (yGrad + xGrad) * nMag) /*(3)*/
                        && tmp > Math.abs(xGrad * swMag - (yGrad + xGrad) * sMag) /*(4)*/
                        : Math.abs(xGrad) >= Math.abs(yGrad) /*(2)*/
                        ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * seMag + (xGrad - yGrad) * eMag) /*(3)*/
                        && tmp > Math.abs(yGrad * nwMag + (xGrad - yGrad) * wMag) /*(4)*/
                        : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * seMag + (yGrad - xGrad) * sMag) /*(3)*/
                        && tmp > Math.abs(xGrad * nwMag + (yGrad - xGrad) * nMag) /*(4)*/
                ) {
                    magnitude[index] = gradMag >= MAGNITUDE_LIMIT ? MAGNITUDE_MAX : (int) (MAGNITUDE_SCALE * gradMag);

                }
                else {
                    magnitude[index] = 0;
                }

            }
        }

    }