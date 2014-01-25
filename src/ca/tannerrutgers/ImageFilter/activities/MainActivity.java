package ca.tannerrutgers.ImageFilter.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import ca.tannerrutgers.ImageFilter.R;
import ca.tannerrutgers.ImageFilter.dialogs.FilterSelectionDialog;
import ca.tannerrutgers.ImageFilter.utils.BitmapUtils;
import ca.tannerrutgers.ImageFilter.models.ImageFilter;
import ca.tannerrutgers.ImageFilter.models.MeanFilter;
import ca.tannerrutgers.ImageFilter.models.MedianFilter;

public class MainActivity extends Activity implements FilterSelectionDialog.FilterSelectionDialogListener {

    private static final int SELECT_IMAGE = 100;

    // Current state variables
    private Bitmap currentImage;
    private int selectedMaskSize;

    // View items
    private ImageView imageView;
    private Button applyFilterButton;

    // ASyncTask used for filtering
    private FilterTask filterTask;

    /**
     * Called when the activity is first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Set default values if first time launching
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Retrieve saved mask size
        selectedMaskSize = PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_mask_size",ImageFilter.SIZE_DEFAULT);

        // Initialize views
        imageView = (ImageView)findViewById(R.id.imageView);
        applyFilterButton = (Button)findViewById(R.id.applyFilterButton);
    }

    /**
     * Called when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        selectedMaskSize = PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_mask_size",ImageFilter.SIZE_DEFAULT);
    }

    /**
     * Called when the activity is destroyed
     */
    @Override
    public void onDestroy() {
        // Cancel current filter task if needed
        if (filterTask != null && filterTask.getStatus() != AsyncTask.Status.FINISHED) {
            filterTask.cancel(true);
        }
        super.onDestroy();
    }

    /**
     * Called when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        // Cancel current filter task if needed
        if (filterTask != null && filterTask.getStatus() != AsyncTask.Status.FINISHED) {
            filterTask.cancel(true);
        }
        super.onBackPressed();
    }

    /**
     * Inflates options menu in menu bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    /**
     * Called when an option is selected in the menu bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // Settings has been selected
            case R.id.menu_settings:
                launchPreferences();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Launch preferences activity
     */
    private void launchPreferences() {
        Intent preferencesIntent = new Intent(MainActivity.this, SettingsActivity.class);

        // Pass maximum possible mask size to preferences if image is loaded
        if (currentImage != null) {
            int maxSize = Math.min(currentImage.getWidth(), currentImage.getHeight());
            preferencesIntent.putExtra("max_mask_size", maxSize);
        }

        startActivity(preferencesIntent);
    }

    /**
     * Handler for when apply filter button is clicked.
     * Launches a dialog to choose which filter to apply.
     */
    public void applyFilterClicked(View v) {
        DialogFragment filterSelection = new FilterSelectionDialog();
        filterSelection.show(getFragmentManager(), "filterSelection");
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
                    updateViews();
                }
        }
    }

    /**
     * Called when user selects mean filter from filter selection dialog
     */
    @Override
    public void onMeanFilterSelection() {
        ImageFilter filter = new MeanFilter(currentImage, selectedMaskSize);
        filterTask = new FilterTask();
        filterTask.execute(filter);
    }

    /**
     * Called when user selects Median filter from filter selection dialog
     */
    @Override
    public void onMedianFilterSelection() {
        ImageFilter filter = new MedianFilter(currentImage, selectedMaskSize);
        filterTask = new FilterTask();
        filterTask.execute(filter);
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
        if (currentImage != null) {
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

        ImageFilter filter;

        /**
         * Setup dialog
         */
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Filtering...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);

            // Cancel task and filtering if dialog is cancelled
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    filter.cancelFiltering = true;
                    cancel(true);
                }
            });
            dialog.show();
        }

        /**
         * Run filter in background task
         */
        @Override
        protected Bitmap doInBackground(ImageFilter... filters) {
            filter = filters[0];
            return filter.applyFilter();
        }

        /**
         * Called when background task is finished
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            currentImage = result;
            updateViews();
        }

        /**
         * Called when the current task is cancelled
         */
        @Override
        protected void onCancelled(Bitmap result) {
            filter.cancelFiltering = true;
            onCancelled();
        }
    }
}
