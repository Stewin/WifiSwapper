package net.ddns.swinterberger.wifiswapper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import net.ddns.swinterberger.wifiswapper.eventhandler.MarginSeekbarEventhandler;
import net.ddns.swinterberger.wifiswapper.eventhandler.ThresholdSeekbarEventhandler;

/**
 * Main Activity of the WiFi-Swapper Application.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

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
        serviceSwitch.setOnCheckedChangeListener(this);
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

    private void startSwapperService() {
        wifiSwapServiceIntent = new Intent(this, WifiSwapService.class);
        wifiSwapServiceIntent.putExtra(getResources().getString(R.string.intentextrathreshold), thresholdSeekbar.getProgress());
        wifiSwapServiceIntent.putExtra(getResources().getString(R.string.intentextramargin), marginSeekbar.getProgress());

        try {
            if (!serviceRunning) {
                startService(wifiSwapServiceIntent);
                android.content.ServiceConnection serviceConnection = new ServiceConnection();
                bindService(wifiSwapServiceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND);
                serviceRunning = true;
            }
        } catch (SecurityException secEx) {
            Log.e("WifiSwapperMain: ", "Error while starting the WifiSwapperService: " + secEx);
            debugInfos.append("Error while starting the WifiSwapperService");
        }
    }

    private void stopSwapperService() {
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            serviceSwitch.setText(serviceSwitch.getTextOff());
            stopSwapperService();
        } else {
            serviceSwitch.setText(serviceSwitch.getTextOn());
            startSwapperService();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(this.getResources().getString(R.string.serviceswitchpreferencename), isChecked);
        editor.apply();
    }

    /**
     * Prints a Log-Message to the Log-Textfield of the MainActivity.
     *
     * @param logMessage Message to log.
     */
    public void logMessage(final String logMessage) {
        this.debugInfos.append(logMessage);
    }

    private class ServiceConnection implements android.content.ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBinder = (SwapperServiceApi) service;
            serviceBinder.registerMainActivity(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBinder = null;
        }

        WifiScanReceiver receiver = null;

        // Register Broadcast Receiver
        if (receiver == null) {
            receiver = new WifiScanReceiver();
            receiver.setMainActivity(this);
        }

        registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void appendDebugInfos(String string) {
        debugInfos.append(string + "\n");
    }
}
