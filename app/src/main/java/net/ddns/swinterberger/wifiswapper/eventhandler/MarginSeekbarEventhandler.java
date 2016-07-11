package net.ddns.swinterberger.wifiswapper.eventhandler;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import net.ddns.swinterberger.wifiswapper.MainActivity;
import net.ddns.swinterberger.wifiswapper.R;

/**
 * Eventhandler for the Seekbar to set the Margin.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public final class MarginSeekbarEventhandler implements OnSeekBarChangeListener {

    private MainActivity mainActivity;
    private TextView marginValue;

    /**
     * Constructor with MainActivity of the App.
     *
     * @param mainActivity MainActivity of the App.
     */
    public MarginSeekbarEventhandler(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public final void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
        int seekBarValue = seekBar.getProgress();

        if (marginValue != null) {
            marginValue.setText(String.valueOf(seekBarValue));
        }
    }

    @Override
    public final void onStartTrackingTouch(SeekBar seekBar) {
        //Not needed
    }

    @Override
    public final void onStopTrackingTouch(SeekBar seekBar) {
        int seekBarValue = seekBar.getProgress();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(mainActivity.getResources().getString(R.string.marginpreferencename), seekBarValue);
        editor.apply();

        mainActivity.marginSeekbarChanged();
    }

    public final void setMarginValue(TextView marginValue) {
        this.marginValue = marginValue;
    }
}
