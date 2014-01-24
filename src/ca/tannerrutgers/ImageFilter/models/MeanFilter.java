package ca.tannerrutgers.ImageFilter.models;

import android.graphics.Bitmap;
import android.graphics.Color;
import ca.tannerrutgers.ImageFilter.Utils.BitmapUtils;

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

        // Extract color values from Bitmap
        Map<Character,int[]> colorMap = BitmapUtils.getColorMap(bitmap);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width*height];
        bitmap.getPixels(pixels,0,width,0,0,width,height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = 0;
                int green = 0;
                int blue = 0;
                int alpha = 0;
                ArrayList<Integer> maskIndices = getMaskIndices(x, y);
                double weight = 1.0/maskIndices.size();
                for (Integer index : maskIndices) {
                    red += colorMap.get('R')[index]*weight;
                    green += colorMap.get('G')[index]*weight;
                    blue += colorMap.get('B')[index]*weight;
                    alpha += colorMap.get('A')[index]*weight;
                }
                pixels[y*width+x] = Color.argb(alpha,red,green,blue);
            }
        }
        return Bitmap.createBitmap(pixels,width,height,bitmap.getConfig());
    }


}
