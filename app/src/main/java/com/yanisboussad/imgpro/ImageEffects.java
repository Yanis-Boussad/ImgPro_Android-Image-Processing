package com.yanisboussad.imgpro;

import android.graphics.Bitmap;
import android.graphics.Color;

public final class ImageEffects {
    private Bitmap newBmp;
    public Bitmap Invert(Bitmap bitmap) {
        try {
            newBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            if (newBmp != null) {
                for (int i = 0; i < bitmap.getWidth(); i++) {
                    for (int j = 0; j < bitmap.getHeight(); j++) {
                        int r = Color.red(bitmap.getPixel(i, j));
                        int b = Color.blue(bitmap.getPixel(i, j));
                        int g = Color.green(bitmap.getPixel(i, j));
                        newBmp.setPixel(i, j, Color.rgb(255 - r, 255 - g, 255 - b));}}}
        } catch (NullPointerException ignored) {}
        return newBmp;}

    public Bitmap SepiaEffect(Bitmap bitmap) {
        try {
            newBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            if (newBmp != null) {
                for (int i = 0; i < bitmap.getWidth(); i++) {
                    for (int j = 0; j < bitmap.getHeight(); j++) {
                        int r = Color.red(bitmap.getPixel(i, j));
                        int b = Color.blue(bitmap.getPixel(i, j));
                        int g = Color.green(bitmap.getPixel(i, j));
                        int newR = (int) (r * 0.393 + g * 0.769 + b * 0.189);
                        int newG = (int) (r * 0.349 + g * 0.686 + b * 0.168);
                        int newB = (int) (r * 0.272 + g * 0.534 + b * 0.131);

                        if (newR > 255) {newR = 255;} if (newR < 0) { newR = 0; }
                        if (newB > 255) {newB = 255;} if (newB < 0) {newB = 0;}
                        if (newG > 255) {newG = 255;} if (newG < 0) { newG = 0;}

                        newBmp.setPixel(i, j, Color.rgb(newR, newG, newB));}}}
        } catch (NullPointerException ignored) {}
        return newBmp;
    }

    public Bitmap Greyscale(Bitmap bitmap) {
        try {
            newBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            if (newBmp != null) {
                for (int i = 0; i < bitmap.getWidth(); i++) {
                    for (int j = 0; j < bitmap.getHeight(); j++) {
                        int r = Color.red(bitmap.getPixel(i, j));
                        int b = Color.blue(bitmap.getPixel(i, j));
                        int g = Color.green(bitmap.getPixel(i, j));

                        int grey = (int) (r * 0.21 + g * 0.72 + b * 0.11);
                        if (grey > 255) grey = 255;
                        if (grey < 0) grey = 0;
                        newBmp.setPixel(i, j, Color.rgb(grey, grey, grey)); }}}
        } catch (NullPointerException ignored) {}
        return newBmp;
    }

    public Bitmap Contrast(Bitmap bitmap, int contrast) {
        try {
            newBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            int offset = contrast * 250 / 3;
            if (newBmp != null) {
                for (int i = 0; i < bitmap.getWidth(); i++) {
                    for (int j = 0; j < bitmap.getHeight(); j++) {
                        int r = Color.red(bitmap.getPixel(i, j)) * contrast - offset;
                        int b = Color.blue(bitmap.getPixel(i, j)) * contrast - offset;
                        int g = Color.green(bitmap.getPixel(i, j)) * contrast - offset;

                        if (r > 255) { r = 255;}if (r < 0) {r = 0;}
                        if (g > 255) {g = 255;} if (g < 0) {g = 0;}
                        if (b > 255) {b = 255;} if (b < 0) { b = 0;}

                        newBmp.setPixel(i, j, Color.rgb(r, g, b));}}}
        } catch (NullPointerException ignored) {}
        return newBmp;
    }

    public Bitmap Brightness(Bitmap bitmap, int brightness) {
        try {

            newBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),bitmap.getConfig());
            if (newBmp != null) {
                for (int i = 0; i < bitmap.getWidth(); i++) {
                    for (int j = 0; j < bitmap.getHeight(); j++) {
                        int r = Color.red(bitmap.getPixel(i, j)) + brightness;
                        int b = Color.blue(bitmap.getPixel(i, j)) + brightness;
                        int g = Color.green(bitmap.getPixel(i, j)) + brightness;

                        if (r > 255) { r = 255;}if (r < 0) {r = 0;}
                        if (g > 255) {g = 255;} if (g < 0) {g = 0;}
                        if (b > 255) {b = 255;} if (b < 0) { b = 0;}

                        newBmp.setPixel(i, j, Color.rgb(r, g, b));}}}
        } catch (NullPointerException ignored) {}
        return newBmp;
    }
    public Bitmap setBmp() {
        return newBmp;
    }
}
