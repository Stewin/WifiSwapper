package net.ddns.swinterberger.wifiswapper.eventhandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import net.ddns.swinterberger.wifiswapper.R;

/**
 * Created by Stefan on 05.03.2016.
 */
public class MarginSeekbarEventhandler implements OnSeekBarChangeListener {

    private final Context context;
    private TextView marginValue;

    public MarginSeekbarEventhandler(Context context) {
        this.context = context;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int seekBarValue = seekBar.getProgress();

        if (marginValue != null) {
            marginValue.setText(String.valueOf(seekBarValue));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getResources().getString(R.string.marginpreferencename), seekBarValue);
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
}
