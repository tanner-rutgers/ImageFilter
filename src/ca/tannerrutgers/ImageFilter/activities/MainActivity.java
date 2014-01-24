package ca.tannerrutgers.ImageFilter.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import ca.tannerrutgers.ImageFilter.R;
import ca.tannerrutgers.ImageFilter.dialogs.FilterSelectionDialog;
import ca.tannerrutgers.ImageFilter.dialogs.MaskSizeDialog;
import ca.tannerrutgers.ImageFilter.utils.BitmapUtils;
import ca.tannerrutgers.ImageFilter.models.ImageFilter;
import ca.tannerrutgers.ImageFilter.models.MeanFilter;
import ca.tannerrutgers.ImageFilter.models.MedianFilter;

public class MainActivity extends Activity implements FilterSelectionDialog.FilterSelectionDialogListener, MaskSizeDialog.MaskSizeDialogListener {

    private static final int SELECT_IMAGE = 100;

    private Bitmap currentImage;
    private Boolean imageLoaded;
    private int selectedMaskSize;

    private ImageView imageView;
    private Button applyFilterButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        imageLoaded = false;
        selectedMaskSize = ImageFilter.SIZE_DEFAULT;

        imageView = (ImageView)findViewById(R.id.imageView);
        applyFilterButton = (Button)findViewById(R.id.applyFilterButton);
    }

    /**
     * Handler for when apply filter button is clicked.
     * Launches a dialog to choose which filter to apply.
     */
    public void applyFilterClicked(View v) {
        selectMaskSize();
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

    /**
     * Launch dialog for selecting the filter mask size
     */
    private void selectMaskSize() {
        int maxSize = Math.max(currentImage.getWidth(), currentImage.getHeight());
        DialogFragment maskSizeSelection = MaskSizeDialog.newInstance(selectedMaskSize, maxSize);
        maskSizeSelection.show(getFragmentManager(), "maskSizeSelection");
    }

    /**
     * Handle results from spawned activities
     */
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
                    updateViews();
                }
        }
    }

    /**
     * Called when user selects filter mask size
     */
    @Override
    public void onMaskSizeSelection(int size) {
        selectedMaskSize = size;
        DialogFragment filterSelection = new FilterSelectionDialog();
        filterSelection.show(getFragmentManager(), "filterSelection");
    }

    /**
     * Called when user selects mean filter from filter selection dialog
     */
    @Override
    public void onMeanFilterSelection() {
        ImageFilter filter = new MeanFilter(currentImage, selectedMaskSize);
        new FilterTask().execute(filter);
    }

    /**
     * Called when user selects Median filter from filter selection dialog
     */
    @Override
    public void onMedianFilterSelection() {
        ImageFilter filter = new MedianFilter(currentImage, selectedMaskSize);
        new FilterTask().execute(filter);
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

    /**
     * Updates views attached to this activity
     */
    private void updateViews() {
        if (imageLoaded) {
            imageView.setBackground(null);
            imageView.setImageBitmap(currentImage);
            applyFilterButton.setEnabled(true);
        }
    }

    /**
     * Class allowing image filtering to occur in background
     */
    private class FilterTask extends AsyncTask<ImageFilter, Void, Bitmap> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Filtering...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Bitmap doInBackground(ImageFilter... filters) {
            ImageFilter filter = filters[0];
            return filter.applyFilter();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            currentImage = result;
            updateViews();
        }
    }
}
