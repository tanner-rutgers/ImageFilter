package ca.tannerrutgers.ImageFilter.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import ca.tannerrutgers.ImageFilter.R;

/**
 * Created by Tanner on 1/23/14.
 */
public class FilterSelectionDialog extends DialogFragment {

    private static final int MEAN_FILTER = 0;
    private static final int MEDIAN_FILTER = 1;

    /**
     * Interface that hosting activity must implement in order
     * to handle the selection of filter
     */
    public interface FilterSelectionDialogListener {
        public void onMeanFilterSelection();
        public void onMedianFilterSelection();
    }

    FilterSelectionDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (FilterSelectionDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement FilterDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Construct filter selection dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a filter")
                .setItems(R.array.filter_types, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            // Mean filter has been selected
                            case MEAN_FILTER:
                                mListener.onMeanFilterSelection();
                                break;
                            // Median filter has been selected
                            case MEDIAN_FILTER:
                                mListener.onMedianFilterSelection();
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
