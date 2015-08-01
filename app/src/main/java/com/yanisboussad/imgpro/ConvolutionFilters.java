package com.yanisboussad.imgpro;

import android.graphics.Bitmap;
import android.graphics.Color;

public final class ConvolutionFilters {
    private int[][] filter = new int[3][3];
    private int[][] pixelMatrix = new int[3][3];
    private Bitmap newImage;

    protected enum filterType {SHARP, EDGE_DETECT, RELIEF, BLUR}
    public Bitmap Filter(Bitmap originalImage, filterType type) {
        setFilter(type);
        filter = getFilter();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        newImage = Bitmap.createBitmap(width, height, originalImage.getConfig());
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                createPixelMatrix(x, y, originalImage);
                int sumOfBlur;
                int sumBlues;
                int sumGreens;
                int sumReds = sumGreens = sumBlues = sumOfBlur = 0;
                for (int i = 0; i < filter.length; i++) {
                    for (int j = 0; j < filter.length; j++) {
                        sumReds += (Color.red(pixelMatrix[i][j]) * filter[i][j]);
                        sumGreens += (Color.green(pixelMatrix[i][j]) * filter[i][j]);
                        sumBlues += (Color.blue(pixelMatrix[i][j]) * filter[i][j]);
                        sumOfBlur += filter[i][j];
                    }
                }
                if (type == filterType.BLUR) {
                    sumReds /= sumOfBlur;
                    sumGreens /= sumOfBlur;
                    sumBlues /= sumOfBlur;}

                int red = sumReds;
                if (red < 0) {
                    red = 0;} if (red > 255) {
                    red = 255;}
                int green = sumGreens;
                if (green < 0) {
                    green = 0;} if (green > 255) {
                    green = 255;}
                int blue = sumBlues;
                if (blue < 0) { blue = 0;} if (blue > 255) { blue = 255;}

                newImage.setPixel(x, y, Color.rgb(red, green, blue));
            }
        }
        for (int k = 0; k < width; k++) {
            newImage.setPixel(k, 0, Color.rgb(Color.red(newImage.getPixel(k, 1)),
                    Color.green(newImage.getPixel(k, 1)), Color.blue(newImage.getPixel(k, 1))));
        }
        for (int k = 0; k < width; k++) {
            newImage.setPixel(k, height - 1,
                    Color.rgb(Color.red(newImage.getPixel(k, height - 2)),
                            Color.green(newImage.getPixel(k, height - 2)), Color.blue(newImage.getPixel(k, height - 2))));
        }
        for (int k = 0; k < height; k++) {
            newImage.setPixel(0, k,
                    Color.rgb(Color.red(newImage.getPixel(1, k)),
                            Color.green(newImage.getPixel(1, k)), Color.blue(newImage.getPixel(1, k))));
        }
        for (int k = 0; k < height; k++) {
            newImage.setPixel(width - 1, k,
                    Color.rgb(Color.red(newImage.getPixel(width - 2, k)),
                            Color.green(newImage.getPixel(width - 2, k)), Color.blue(newImage.getPixel(width - 2, k))));
        }
        return newImage;
    }

    public Bitmap setBmp() {
        return newImage;
    }

    public void createPixelMatrix(int x, int y, Bitmap originalImage) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                pixelMatrix[i + 1][j + 1] = originalImage.getPixel(x + i, y + j);
            }
        }
         }
    public int[][] getFilter() {
        return filter;
    }

    public void setFilter(filterType f) {
        if (f == filterType.EDGE_DETECT) {
            filter = new int[][]{{-1, -1, -1},
                                 {-1, 8, -1},
                                 {-1, -1, -1}};}
        if (f == filterType.SHARP) {
            filter = new int[][]{ {-1, -1, -1},
                                  {-1, 9, -1},
                                  {-1, -1, -1}};}
        if (f == filterType.BLUR) {
            filter = new int[][]{{1, 1, 1},
                                 {1, 1, 1},
                                 {1, 1, 1}};}
        if(f == filterType.RELIEF){
            filter = new int[][]{{-2, -1,0},
                                 {-1, 1, 1},
                                  {0, 1, 2}};}
    }
}
