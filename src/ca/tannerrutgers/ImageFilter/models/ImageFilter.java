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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public static Map<Character,int[]> getColorMap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Map<Character,int[]> colorMap = new HashMap<Character, int[]>();

        int[] red = new int[width*height];
        int[] green = new int[width*height];
        int[] blue = new int[width*height];
        int[] alpha = new int[width*height];

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int index = i*width+j;
                int pixel = bitmap.getPixel(i, j);
                red[index] = Color.red(bitmap.getPixel(i, j));
                green[index] = Color.green(bitmap.getPixel(i, j));
                blue[index] = Color.blue(bitmap.getPixel(i, j));
                alpha[index] = Color.alpha(bitmap.getPixel(i, j));
            }
        }

        colorMap.put('R',red);
        colorMap.put('G',green);
        colorMap.put('B',blue);
        colorMap.put('A',alpha);

        return colorMap;
    }

    /**
     * Retrieve indices of pixels belonging to the filter mask
     * centered at the pixel with coordinates i, j
     * @param i row
     * @param j column
     * @return ArrayList of indices
     */
    protected ArrayList<Integer> getMaskIndices(int i, int j) {
        ArrayList<Integer> indices = new ArrayList<Integer>();

        int offset = filterSize/2;

        for (int row = i-offset; row <= i+offset; row++) {
            for (int col = j-offset; col <= j+offset; col++) {
                if (row >= 0 && col >= 0 && row < bitmap.getHeight() && col < bitmap.getWidth()) {
                    indices.add(i*bitmap.getWidth()+j);
                }
            }
        }
        return indices;
    }
}
