package ca.tannerrutgers.ImageFilter.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import ca.tannerrutgers.ImageFilter.R;
import ca.tannerrutgers.ImageFilter.models.ImageFilter;

/**
 * Created by Tanner on 1/23/14.
 */
public class MaskSizeDialog extends DialogFragment {

    /**
     * Interface that hosting activity must implement in order
     * to handle the selection of filter size
     */
    public interface MaskSizeDialogListener {
        public void onMaskSizeSelection(int size);
    }

    MaskSizeDialogListener mListener;

    private SeekBar sizeBar;
    private TextView sizeLabel;

    public static MaskSizeDialog newInstance(int maskSize, int maxSize) {
        MaskSizeDialog maskSizeDialog = new MaskSizeDialog();

        Bundle args = new Bundle();
        args.putInt("size", maskSize);
        args.putInt("max_size", maxSize);
        maskSizeDialog.setArguments(args);

        return maskSizeDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (MaskSizeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement FilterDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get custom filter size selection layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter_size, null);

        // Retrieve dialog views
        sizeBar = (SeekBar) dialogView.findViewById(R.id.filterSizeBar);
        sizeLabel = (TextView) dialogView.findViewById(R.id.filterSizeLabel);

        // Set default values for views
        sizeBar.setMax(getValidSize(Math.min(getArguments().getInt("max_size"), ImageFilter.SIZE_MAX)));
        sizeBar.setProgress(getValidSize(getArguments().getInt("size")));
        setSizeLabel(sizeBar.getProgress());

        // Ensure size label updates with seek bar
        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSizeLabel(sizeBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Build dialog window
        builder.setView(dialogView)
                .setTitle("Choose a mask size")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onMaskSizeSelection(getValidSize(sizeBar.getProgress()));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MaskSizeDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    private void setSizeLabel(int maskSize) {
        int validSize = getValidSize(maskSize);
        sizeLabel.setText(validSize + " x " + validSize);
    }

    private int getValidSize(int maskSize) {
        int validSize = maskSize;

        if (maskSize < ImageFilter.SIZE_MIN) {
            validSize = ImageFilter.SIZE_MIN;
        } else if (maskSize % 2 == 0) {
            validSize += 1;
        }

        return validSize;
    }
}

