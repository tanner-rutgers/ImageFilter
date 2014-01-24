package ca.tannerrutgers.ImageFilter.models;

import android.graphics.Bitmap;
import ca.tannerrutgers.ImageFilter.utils.BitmapUtils;

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

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int offset = filterSize/2;

        int[] pixels = BitmapUtils.getPixels(bitmap);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int red = 0;
                int green = 0;
                int blue = 0;
                int alpha = 0;

                int maskPixels = 0;
                for (int row = y-offset; row <= y+offset; row++) {
                    for (int col = x-offset; col <= x+offset; col++) {
                        if (row >= 0 && col >= 0 && row < height && col < width) {

                            int color = pixels[row*width+col];

                            red += (color >> 16) & 0xFF;
                            green += (color >> 8) & 0xFF;
                            blue += color & 0xFF;
                            alpha += color >>> 24;

                            maskPixels++;
                        }
                    }
                }

                red = red/maskPixels;
                green = green/maskPixels;
                blue = blue/maskPixels;
                alpha = alpha/maskPixels;

                pixels[y*width+x] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        }
        return Bitmap.createBitmap(pixels,width,height,bitmap.getConfig());
    }


}
