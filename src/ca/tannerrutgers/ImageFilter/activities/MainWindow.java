package ca.tannerrutgers.ImageFilter.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import ca.tannerrutgers.ImageFilter.R;
import ca.tannerrutgers.ImageFilter.Utils.BitmapUtils;
import ca.tannerrutgers.ImageFilter.models.ImageFilter;
import ca.tannerrutgers.ImageFilter.models.MeanFilter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainWindow extends Activity {

    private static final int SELECT_IMAGE = 100;

    private Bitmap currentImage;
    private Boolean imageLoaded;

    private ImageView imageView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        imageLoaded = false;

        imageView = (ImageView)findViewById(R.id.imageView);
    }

    public void applyFilter(View v) {

        MeanFilter filter = new MeanFilter(currentImage);
        currentImage = filter.applyFilter();

        updateViews();
    }

    /**
     * Handler for when "Select Image" button is clicked.
     * Delegates image selection to Android gallery.
     */
    public void selectImageClicked(View v) {
        Intent imageChooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageChooserIntent.setType("image/*");
        startActivityForResult(imageChooserIntent, SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {

        super.onActivityResult(requestCode, resultCode, returnedIntent);

        switch (requestCode) {

            // Request is image selection
            case SELECT_IMAGE:
                if (resultCode == RESULT_OK) {

                    // Retrieve selected image
                    Uri selectedImage = returnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filepath = cursor.getString(columnIndex);

                    // Save currently selected image in memory (scaled down if needed)
                    currentImage = getScaledBitmapFromFilepath(filepath);
                    imageLoaded = true;
                }
                break;
        }
        updateViews();
    }

    /**
     * Returns the Bitmap located at the location filepath
     * scaled down to the image view's resolution if needed
     * @param filepath Path of original bitmap
     * @return Scaled down Bitmap
     */
    private Bitmap getScaledBitmapFromFilepath(String filepath) {

        int viewWidth = imageView.getWidth();
        int viewHeight = imageView.getHeight();

        return BitmapUtils.decodeSampledBitmapFromFilepath(filepath, viewWidth, viewHeight);
    }

    private void updateViews() {

        updateImageView();

    }

    private void updateImageView() {
        imageView.setBackground(null);
        imageView.setImageBitmap(currentImage);
    }
}
