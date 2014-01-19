package ca.tannerrutgers.ImageFilter.models;

import android.graphics.Bitmap;
import android.graphics.Color;

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
        Bitmap filtered = Bitmap.createBitmap(bitmap);

        // Extract color values from Bitmap
        Map<Character,int[]> colorMap = getColorMap(bitmap);

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                ArrayList<Integer> maskIndices = getMaskIndices(i, j);
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
                filtered.setPixel(i, j, Color.argb(alpha, red, green, blue));
            }
        }
        return filtered;
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
