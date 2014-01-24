package ca.tannerrutgers.ImageFilter.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanner on 22/01/14.
 */
public class BitmapUtils {

    public static Bitmap decodeSampledBitmapFromFilepath(String filepath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Map<Character,int[]> getColorMap(Bitmap bitmap) {
        Map<Character,int[]> colorMap = new HashMap<Character, int[]>();
        
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        int[] pixels = new int[width*height];
        bitmap.getPixels(pixels,0,width,0,0,width,height);

        int[] red = new int[pixels.length];
        int[] green = new int[pixels.length];
        int[] blue = new int[pixels.length];
        int[] alpha = new int[pixels.length];

        for (int i = 0; i < pixels.length; i++) {
            red[i] = Color.red(pixels[i]);
            green[i] = Color.green(pixels[i]);
            blue[i] = Color.blue(pixels[i]);
            alpha[i] = Color.alpha(pixels[i]);
        }

        colorMap.put('R',red);
        colorMap.put('G',green);
        colorMap.put('B',blue);
        colorMap.put('A',alpha);

        return colorMap;
    }
}
