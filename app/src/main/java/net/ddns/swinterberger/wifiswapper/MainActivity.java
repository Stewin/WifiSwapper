package net.ddns.swinterberger.wifiswapper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import net.ddns.swinterberger.wifiswapper.eventhandler.MarginSeekbarEventhandler;
import net.ddns.swinterberger.wifiswapper.eventhandler.ServiceSwitchButtonEventHandler;
import net.ddns.swinterberger.wifiswapper.eventhandler.ThresholdSeekbarEventhandler;

/**
 * Main Activity of the WiFi-Swapper Application.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public final class MainActivity extends AppCompatActivity {

    private TextView debugInfos;
    private SeekBar thresholdSeekbar;
    private SeekBar marginSeekbar;
    private Switch serviceSwitch;
    private Intent wifiSwapServiceIntent;
    private boolean serviceRunning;
    private SwapperServiceApi serviceBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViewElements();
        loadPreferences();
    }

    private void setupViewElements() {
        debugInfos = (TextView) findViewById(R.id.tvDebugInfo);

        TextView tresholdValue = (TextView) findViewById(R.id.tvThresoldValue);
        TextView marginValue = (TextView) findViewById(R.id.tvMarginValue);

        thresholdSeekbar = (SeekBar) findViewById(R.id.seekBar_Treshold);
        ThresholdSeekbarEventhandler thresholdSeekbarEventhandler = new ThresholdSeekbarEventhandler(this);
        thresholdSeekbarEventhandler.setThresholdValue(tresholdValue);
        thresholdSeekbar.setOnSeekBarChangeListener(thresholdSeekbarEventhandler);

        marginSeekbar = (SeekBar) findViewById(R.id.seekBar_Margin);
        MarginSeekbarEventhandler marginSeekbarEventhandler = new MarginSeekbarEventhandler(this);
        marginSeekbarEventhandler.setMarginValue(marginValue);
        marginSeekbar.setOnSeekBarChangeListener(marginSeekbarEventhandler);

        serviceSwitch = (Switch) findViewById(R.id.switch_serviceSwitch);
        ServiceSwitchButtonEventHandler serviceSwitchButtonEventHandler =
                new ServiceSwitchButtonEventHandler(this);
        serviceSwitch.setOnCheckedChangeListener(serviceSwitchButtonEventHandler);
    }

    private void loadPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int currentMarginValue = preferences.getInt(getResources().getString(R.string.marginpreferencename), 0);
        int currentThresholdValue = preferences.getInt(getResources().getString(R.string.thresholdpreferencename), 0);
        serviceRunning = preferences.getBoolean(getResources().getString(R.string.serviceswitchpreferencename), false);

        this.marginSeekbar.setProgress(currentMarginValue);
        this.thresholdSeekbar.setProgress(currentThresholdValue);
        this.serviceSwitch.setChecked(serviceRunning);
    }

    /**
     * Starts the Background Service for watching WiFi Signal Strength.
     */
    public final void startSwapperService() {
        wifiSwapServiceIntent = new Intent(this, WifiSwapService.class);
        try {
            startService(wifiSwapServiceIntent);
            android.content.ServiceConnection serviceConnection = new ServiceConnection();
            bindService(wifiSwapServiceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND);
            serviceRunning = true;
        } catch (SecurityException secEx) {
            Log.e("WifiSwapperMain: ", "Error while starting the WifiSwapperService: " + secEx);
            debugInfos.append("Error while starting the WifiSwapperService");
        }
    }

    /**
     * Stops the Background Service for watching WiFi Signal Strength.
     */
    public final void stopSwapperService() {
        if (wifiSwapServiceIntent != null) {
            try {
                if (serviceRunning) {
                    stopService(wifiSwapServiceIntent);
                    serviceRunning = false;
                }
            } catch (SecurityException secEx) {
                Log.e("WifiSwapperMain: ", "Error while stoping the WifiSwapperService: " + secEx);
                debugInfos.append("Error while stoping the WifiSwapperService");
            }
        }
    }

    /**
     * Prints a Log-Message to the Log-Textfield of the MainActivity.
     *
     * @param logMessage Message to log.
     */
    public void logMessage(final String logMessage) {
        this.debugInfos.append(logMessage);
    }

    /**
     * Callback for Handling the Change of the MarginSeekbar.
     */
    public void marginSeekbarChanged() {
        if (serviceBinder != null) {
            serviceBinder.setMargin(this.marginSeekbar.getProgress());
        }
    }

    /**
     * Callback for Handling the Change of the ThresholdSeekbar.
     */
    public void thresholdSeekbarChanged() {
        if (serviceBinder != null) {
            serviceBinder.setThreshold(this.thresholdSeekbar.getProgress());
        }
    }

    private class ServiceConnection implements android.content.ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBinder = (SwapperServiceApi) service;
            serviceBinder.setMainActivity(MainActivity.this);
            serviceBinder.setThreshold(thresholdSeekbar.getProgress());
            serviceBinder.setMargin(marginSeekbar.getProgress());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBinder = null;
        }
    }
}
