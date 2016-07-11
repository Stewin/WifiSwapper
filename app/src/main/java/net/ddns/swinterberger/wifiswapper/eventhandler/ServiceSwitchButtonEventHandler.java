package net.ddns.swinterberger.wifiswapper.eventhandler;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;

import net.ddns.swinterberger.wifiswapper.MainActivity;
import net.ddns.swinterberger.wifiswapper.R;

/**
 * Eventhandler for the SwitchButton, to enable/disable the Service.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public final class ServiceSwitchButtonEventHandler implements CompoundButton.OnCheckedChangeListener {

    private final MainActivity mainActivity;

    /**
     * Constructor with MainActivity of the App.
     *
     * @param mainActivity MainActivity of the App.
     */
    public ServiceSwitchButtonEventHandler(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public final void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        if (!isChecked) {
            buttonView.setText(R.string.label_disabled);
            if (mainActivity != null) {
                mainActivity.stopSwapperService();
            }
        } else {
            buttonView.setText(R.string.label_enabled);
            if (mainActivity != null) {
                mainActivity.startSwapperService();
            }
        }

        if (mainActivity != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(mainActivity.getResources().getString(R.string.serviceswitchpreferencename), isChecked);
            editor.apply();
        }
    }
}
