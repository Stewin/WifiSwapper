package net.ddns.swinterberger.wifiswapper;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import net.ddns.swinterberger.wifiswapper.eventhandler.MarginSeekbarEventhandler;
import net.ddns.swinterberger.wifiswapper.eventhandler.ServiceBinderSwitchHandler;
import net.ddns.swinterberger.wifiswapper.eventhandler.ServiceSwitchButtonEventHandler;
import net.ddns.swinterberger.wifiswapper.eventhandler.ThresholdSeekbarEventhandler;

/**
 * Main Activity of the WiFi-Swapper Application.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public final class MainActivity extends Activity {

    private TextView debugInfos;
    private SeekBar thresholdSeekbar;
    private SeekBar marginSeekbar;
    private Switch serviceSwitch;
    private final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private Intent wifiSwapServiceIntent;
    private boolean serviceRunning;
    private SwapperServiceApi serviceBinder;
    private boolean debugEnabled;
    private ScrollView debugScrollview;
    private Switch serviceBinderSwitch;
    private boolean serviceBound;
    private android.content.ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check Coarse Location Permission
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
//            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
//                // No explanation needed, we can request the permission.
//
//
//                //The callback method gets the result of the request.
//            }

            requestPermissions(new String[]{(Manifest.permission.ACCESS_COARSE_LOCATION)}, PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        } else {
            // Permission has already been granted
            initialize();
        }
    }

    private void initialize() {
        setContentView(R.layout.activity_main);
        setupViewElements();
        loadPreferences();
    }

    private void setupViewElements() {
        debugInfos = (TextView) findViewById(R.id.tvDebugInfo);
        debugScrollview = (ScrollView) findViewById(R.id.sv_debugScrollView);

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
        serviceSwitch.setOnCheckedChangeListener(new ServiceSwitchButtonEventHandler(this));

        serviceBinderSwitch = (Switch) findViewById(R.id.switch_serviceBound);
        serviceBinderSwitch.setOnCheckedChangeListener(new ServiceBinderSwitchHandler(this));

        CheckBox cbDebugEnabled = (CheckBox) findViewById(R.id.cb_Debug);
        cbDebugEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    buttonView.setChecked(false);
                    MainActivity.this.debugEnabled = false;
                    buttonView.setText(getResources().getString(R.string.label_disabled));
                    debugInfos.setText("");
                } else {
                    buttonView.setChecked(true);
                    MainActivity.this.debugEnabled = true;
                    buttonView.setText(getResources().getString(R.string.label_enabled));
                }
            }
        });
    }

    private void loadPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int currentMarginValue = preferences.getInt(getResources().getString(R.string.marginpreferencename), 2);
        int currentThresholdValue = preferences.getInt(getResources().getString(R.string.thresholdpreferencename), 7);
        serviceRunning = preferences.getBoolean(getResources().getString(R.string.serviceswitchpreferencename), false);
        serviceBound = preferences.getBoolean(getResources().getString(R.string.servicebinderswitchpreferencename), false);

        this.marginSeekbar.setProgress(currentMarginValue);
        this.thresholdSeekbar.setProgress(currentThresholdValue);
        this.serviceSwitch.setChecked(serviceRunning);
        this.serviceBinderSwitch.setChecked(serviceBound);
    }

    /**
     * Starts the Background Service for watching WiFi Signal Strength.
     */
    public final void startSwapperService() {

        //serviceRunning = isServiceRunning(WifiSwapService.class);
        //if (!serviceRunning) {
        wifiSwapServiceIntent = new Intent(this, WifiSwapService.class);
        try {
            startService(wifiSwapServiceIntent);
            serviceRunning = true;
            bindSwapperService();
        } catch (SecurityException secEx) {
            Log.e("WifiSwapperMain: ", "Error while starting the WifiSwapperService: " + secEx);
            logMessage("Error while starting the WifiSwapperService");
        }
        //}
    }

    /**
     * Stops the Background Service for watching WiFi Signal Strength.
     */
    public final void stopSwapperService() {
        if (wifiSwapServiceIntent != null) {
            try {
                unbindSwapperService();
                if (serviceRunning) {
                    stopService(wifiSwapServiceIntent);
                    serviceRunning = false;
                }
            } catch (SecurityException secEx) {
                Log.e("WifiSwapperMain: ", "Error while stoping the WifiSwapperService: " + secEx);
                debugInfos.append("Error while stopping the WifiSwapperService");
            }
        }
    }

    /**
     * Prints a Log-Message to the Log-TextField of the MainActivity.
     *
     * @param logMessage Message to log.
     */
    public void logMessage(final String logMessage) {
        if (debugEnabled && debugInfos != null) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    debugInfos.append(logMessage);
                    debugScrollview.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    /**
     * Callback for Handling the Change of the Margin SeekBar.
     */
    public void marginSeekBarChanged() {
        if (serviceBinder != null) {
            serviceBinder.setMargin(this.marginSeekbar.getProgress());
        }
    }

    /**
     * Callback for Handling the Change of the ThresholdSeekBar.
     */
    public void thresholdSeekBarChanged() {
        if (serviceBinder != null) {
            serviceBinder.setThreshold(this.thresholdSeekbar.getProgress());
        }
    }

    public void bindSwapperService() {
        serviceConnection = new ServiceConnection();
        bindService(wifiSwapServiceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND);
        serviceBound = true;
    }

    public void unbindSwapperService() {
        if (serviceConnection != null && serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        unbindSwapperService();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    initialize();
                } else {
                    // permission denied!
                    System.exit(-1);
                }
            }
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
