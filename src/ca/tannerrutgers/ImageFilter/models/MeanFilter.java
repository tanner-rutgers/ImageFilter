package ca.tannerrutgers.ImageFilter.models;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Tanner on 1/18/14.
 */
public class MeanFilter extends ImageFilter {

    public MeanFilter(Bitmap image) {
        super(image);
    }

    public MeanFilter(Bitmap image, int size) {
        super(image, size);
    }

    @Override
    public Bitmap applyFilter() {
        Bitmap filtered = Bitmap.createBitmap(bitmap);

        // Extract color values from Bitmap
        Map<Character,int[]> colorMap = getColorMap(bitmap);

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int red = 0;
                int green = 0;
                int blue = 0;
                int alpha = 0;
                ArrayList<Integer> maskIndices = getMaskIndices(i, j);
                for (Integer index : maskIndices) {
                    red += colorMap.get('R')[index];
                    green += colorMap.get('G')[index];
                    blue += colorMap.get('B')[index];
                    alpha += colorMap.get('A')[index];
                }
                red = red/maskIndices.size();
                green = green/maskIndices.size();
                blue = blue/maskIndices.size();
                alpha = alpha/maskIndices.size();
                filtered.setPixel(i, j, Color.argb(alpha,red,green,blue));
            }
        }
        return filtered;
    }


}
