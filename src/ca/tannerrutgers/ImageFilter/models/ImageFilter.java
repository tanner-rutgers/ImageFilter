package ca.tannerrutgers.ImageFilter.models;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanner on 1/18/14.
 */
public abstract class ImageFilter {

    private static final int SIZE_DEFAULT = 3;

    protected Bitmap bitmap;
    private int filterSize;

    public ImageFilter(Bitmap bitmap) {
        this(bitmap, SIZE_DEFAULT);
    }

    public ImageFilter(Bitmap bitmap, int filterSize) {
        this.bitmap = bitmap;
        this.filterSize = filterSize;
    }

    public abstract Bitmap applyFilter();

    /**
     * Retrieve indices of pixels belonging to the filter mask
     * centered at the pixel with coordinates x, y
     * @param x column
     * @param y row
     * @return ArrayList of indices
     */
    protected ArrayList<Integer> getMaskIndices(int x, int y) {
        ArrayList<Integer> indices = new ArrayList<Integer>();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int offset = filterSize/2;

        for (int row = y-offset; row <= y+offset; row++) {
            for (int col = x-offset; col <= x+offset; col++) {
                if (row >= 0 && col >= 0 && row < height && col < width) {
                    indices.add(row*width+col);
                }
            }
        }
        return indices;
    }
}
