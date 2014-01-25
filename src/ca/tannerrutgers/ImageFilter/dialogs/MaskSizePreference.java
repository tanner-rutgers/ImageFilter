package ca.tannerrutgers.ImageFilter.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import ca.tannerrutgers.ImageFilter.R;
import ca.tannerrutgers.ImageFilter.models.ImageFilter;

/**
 * Created by Tanner on 1/23/14.
 */
public class MaskSizePreference extends DialogPreference {

    private static final int SIZE_DEFAULT = ImageFilter.SIZE_DEFAULT;

    private SeekBar sizeBar;
    private TextView sizeLabel;

    private int mSelectedSize;
    private int mMaxSize;

    public MaskSizePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_mask_size);
        setDialogTitle(R.string.mask_size);
        setDialogIcon(R.drawable.ic_filter_size);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    public void onBindDialogView(View view) {
        // Retrieve dialog views
        sizeBar = (SeekBar) view.findViewById(R.id.filterSizeBar);
        sizeLabel = (TextView) view.findViewById(R.id.filterSizeLabel);

        sizeBar.setMax(getValidSize(mMaxSize));
        sizeBar.setProgress(getValidSize(mSelectedSize));
        setSizeLabel();

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSelectedSize = getValidSize(sizeBar.getProgress());
                setSizeLabel();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mSelectedSize = getValidSize(this.getPersistedInt(SIZE_DEFAULT));
        } else {
            mSelectedSize = getValidSize((Integer)defaultValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return SIZE_DEFAULT;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent, use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current setting value
        myState.value = mSelectedSize;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        mSelectedSize = myState.value;
        setSizeLabel();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(getValidSize(mSelectedSize));
        }
    }

    private void setSizeLabel() {
        int validSize = getValidSize(mSelectedSize);
        sizeLabel.setText(validSize + " x " + validSize);
    }

    private int getValidSize(int maskSize) {
        int validSize = maskSize;

        if (validSize < ImageFilter.SIZE_MIN) {
            validSize = ImageFilter.SIZE_MIN;
        } else if (validSize > mMaxSize) {
            validSize = mMaxSize;
        }

        while (validSize % 2 == 0) {
            validSize += 1;
        }

        return validSize;
    }

    public void setMaxSize(int maxSize) {
        this.mMaxSize = maxSize;
    }

    private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        // Change this data type to match the type saved by your Preference
        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            value = source.readInt();  // Change this to read the appropriate data type
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeInt(value);  // Change this to write the appropriate data type
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}

