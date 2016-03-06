package net.ddns.swinterberger.wifiswapper.eventhandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * Created by Stefan on 05.03.2016.
 */
public class MarginSeekbarEventhandler implements OnSeekBarChangeListener {

    private final Context context;
    private String MARGIN_PREFERENCE_NAME;
    private TextView marginValue;

    public MarginSeekbarEventhandler(Context context) {
        this.context = context;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int seekBarValue = seekBar.getProgress();

        if (marginValue != null) {
            marginValue.setText(String.valueOf(seekBarValue / 10));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(this.MARGIN_PREFERENCE_NAME, seekBarValue);
        editor.apply();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public TextView getMarginValue() {
        return marginValue;
    }

    public void setMarginValue(TextView marginValue) {
        this.marginValue = marginValue;
    }

    public String getMARGIN_PREFERENCE_NAME() {
        return MARGIN_PREFERENCE_NAME;
    }

    public void setMARGIN_PREFERENCE_NAME(String MARGIN_PREFERENCE_NAME) {
        this.MARGIN_PREFERENCE_NAME = MARGIN_PREFERENCE_NAME;
    }
}
