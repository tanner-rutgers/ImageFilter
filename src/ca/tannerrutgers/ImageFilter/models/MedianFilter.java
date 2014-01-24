package ca.tannerrutgers.ImageFilter.models;

import android.graphics.Bitmap;
import ca.tannerrutgers.ImageFilter.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Tanner on 1/18/14.
 */
public class MedianFilter extends ImageFilter {

    public MedianFilter(Bitmap image) {
        super(image);
    }

    public MedianFilter(Bitmap image, int size) {
        super(image, size);
    }

    @Override
    public Bitmap applyFilter() {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int offset = filterSize/2;

        int[] pixels = BitmapUtils.getPixels(bitmap);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                ArrayList<Integer> reds = new ArrayList<Integer>(offset);
                ArrayList<Integer> greens = new ArrayList<Integer>(offset);
                ArrayList<Integer> blues = new ArrayList<Integer>(offset);
                ArrayList<Integer> alphas = new ArrayList<Integer>(offset);

                for (int row = y-offset; row <= y+offset; row++) {
                    for (int col = x-offset; col <= x+offset; col++) {
                        if (row >= 0 && col >= 0 && row < height && col < width) {

                            int color = pixels[row*width+col];

                            reds.add((color >> 16) & 0xFF);
                            greens.add((color >> 8) & 0xFF);
                            blues.add(color & 0xFF);
                            alphas.add(color >>> 24);
                        }
                    }
                }

                int red = getMedian(reds);
                int green = getMedian(greens);
                int blue = getMedian(blues);
                int alpha = getMedian(alphas);

                pixels[y*width+x] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        }
        return Bitmap.createBitmap(pixels,width,height,bitmap.getConfig());
    }

    private int getMedian(ArrayList<Integer> values) {
        Collections.sort(values);
        int size = values.size();

        if (size % 2 != 0) {
            return values.get(size/2);
        } else {
            return (values.get(size/2) + values.get(size/2 - 1))/2;
        }
    }
}
