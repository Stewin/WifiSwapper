package net.ddns.swinterberger.wifiswapper.eventhandler;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;

import net.ddns.swinterberger.wifiswapper.MainActivity;
import net.ddns.swinterberger.wifiswapper.R;

/**
 * Created by Stefan on 03.09.2017.
 */
public final class ServiceBinderSwitchHandler implements CompoundButton.OnCheckedChangeListener {

    private MainActivity mainActivity;

    public ServiceBinderSwitchHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public final void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        if (!isChecked) {
            buttonView.setText(R.string.label_unbound);
            if (mainActivity != null) {
                mainActivity.unbindSwapperService();
            }
        } else {
            buttonView.setText(R.string.label_bound);
            if (mainActivity != null) {
                mainActivity.bindSwapperService();
            }
        }

        if (mainActivity != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(mainActivity.getResources().getString(R.string.servicebinderswitchpreferencename), isChecked);
            editor.apply();
        }
    }
}
