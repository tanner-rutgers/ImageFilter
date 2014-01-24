package ca.tannerrutgers.ImageFilter.models;

import android.graphics.Bitmap;
import android.graphics.Color;
import ca.tannerrutgers.ImageFilter.Utils.BitmapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

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

        // Extract color values from Bitmap
        Map<Character,int[]> colorMap = BitmapUtils.getColorMap(bitmap);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width*height];
        bitmap.getPixels(pixels,0,width,0,0,width,height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ArrayList<Integer> maskIndices = getMaskIndices(x, y);
                ArrayList<Integer> reds = new ArrayList<Integer>();
                ArrayList<Integer> greens = new ArrayList<Integer>();
                ArrayList<Integer> blues = new ArrayList<Integer>();
                ArrayList<Integer> alphas = new ArrayList<Integer>();

                for (Integer index : maskIndices) {
                    reds.add(colorMap.get('R')[index]);
                    greens.add(colorMap.get('G')[index]);
                    blues.add(colorMap.get('B')[index]);
                    alphas.add(colorMap.get('A')[index]);
                }

                int red = getMedian(reds);
                int green = getMedian(greens);
                int blue = getMedian(blues);
                int alpha = getMedian(alphas);

                pixels[y*width+x] = Color.argb(alpha, red, green, blue);
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
            return (values.get(size/2) + values.get(size/2 + 1))/2;
        }
    }
}
