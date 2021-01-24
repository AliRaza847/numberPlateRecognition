package com.example.numberplaterecognition;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NumberPlateDetection extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numberplatedetection);

    }
    public class cannyedgedetector
    {
        private final static float GAUSSIAN_CUT_OFF = 0.005f;
        private final static float MAGNITUDE_SCALE = 100F;
        private final static float MAGNITUDE_LIMIT = 1000F;
        private final static int MAGNITUDE_MAX = (int) (MAGNITUDE_SCALE * MAGNITUDE_LIMIT);
        private final static int MAX_DEPTH = 125;
        //Fields

        private int height;
        private int width;
        private int picsize;
        private int[] data;
        private int[] magnitude;
        private Bitmap sourceImage;
        private Bitmap edgesImage;

        private float gaussianKernelRadius;
        private float lowThreshold;
        private float highThreshold;
        private int gaussianKernelWidth;
        private boolean contrastNormalized;

        private float[] xConv;
        private float[] yConv;
        private float[] xGradient;
        private float[] yGradient;

     public cannyedgedetector()
     {
         lowThreshold = 2.5f;
         highThreshold = 7.5f;
         gaussianKernelRadius = 2f;
         gaussianKernelWidth = 16;
         contrastNormalized = false;
     }

        public Bitmap getSourceImage() {
            return sourceImage;
        }
       public void setSourceImage(Bitmap image)
       {
           sourceImage=image;
       }
       public Bitmap getEdgesImage()
       {
           return edgesImage;
       }
      public void setEdgesImage(Bitmap edgesImage)
      {
          this.edgesImage=edgesImage;
      }
      public float getLowThreshold()
      {
          return lowThreshold;
      }

      public void setLowThreshold(float threshold) {
            if (threshold < 0) throw new IllegalArgumentException();
            lowThreshold = threshold;
        }
        public float getHighThreshold() {
            return highThreshold;
        }
        public void setHighThreshold(float threshold) {
            if (threshold < 0) throw new IllegalArgumentException();
            highThreshold = threshold;
        }
        public int getGaussianKernelWidth() {
            return gaussianKernelWidth;
        }
        public void setGaussianKernelWidth(int gaussianKernelWidth) {
            if (gaussianKernelWidth < 2) throw new IllegalArgumentException();
            this.gaussianKernelWidth = gaussianKernelWidth;
        }
        public float getGaussianKernelRadius() {
            return gaussianKernelRadius;
        }
        public void setGaussianKernelRadius(float gaussianKernelRadius) {
            if (gaussianKernelRadius < 0.1f) throw new IllegalArgumentException();
            this.gaussianKernelRadius = gaussianKernelRadius;
        }
        public boolean isContrastNormalized() {
            return contrastNormalized;
        }
        public void setContrastNormalized(boolean contrastNormalized) {
            this.contrastNormalized = contrastNormalized;
        }
    }
}
