package net.ddns.swinterberger.wifiswapper;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * Background Service, who keeps running, when the Activity is closed. Uses the Broadcast-Receiver.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public final class WifiSwapService extends Service {

    private static final String TAG = "WifiSwapperService";
    private static final int WLAN_MINIMAL_STRENGTH = -95; //Entspricht einem WLAN-Signal von zirka 1%
    private static final int WLAN_MAXIMUM_STRENGTH = -35; //Entspricht einem WLAN-Signal von zirka 100 %
    private WifiScanReceiver wifiScanReceiver = null;
    private UserPresentReceiver userPresentReceiver = null;
    private SwapperServiceBinder swapperServiceBinder;
    private WifiManager wifiManager;

    private int margin;
    private int threshold;

    @Override
    public final void onDestroy() {
        unregisterReceiver(wifiScanReceiver);
        unregisterReceiver(userPresentReceiver);
        logInformation("Service destroyed\n");
        super.onDestroy();
    }

    @Override
    public final IBinder onBind(final Intent intent) {
        Log.i(TAG, "Service bound with Intent: " + intent);
        if (swapperServiceBinder == null) {
            swapperServiceBinder = new SwapperServiceBinder();
            swapperServiceBinder.setWifiSwapService(this);
        }
        logInformation("Service bound\n");
        return swapperServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service unbound with Intent: " + intent);
        return super.onUnbind(intent);
    }

    @Override
    public final int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);

        if (wifiScanReceiver == null) {
            wifiScanReceiver = new WifiScanReceiver();
            wifiScanReceiver.setWifiSwapService(this);
        }
        registerReceiver(wifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        if (userPresentReceiver == null) {
            userPresentReceiver = new UserPresentReceiver();
            userPresentReceiver.setWifiSwapService(this);
        }
        registerReceiver(userPresentReceiver,
                new IntentFilter(Intent.ACTION_USER_PRESENT));

        logInformation("Service started\n");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new Notification.Builder(this)
                        .setContentTitle("WifiSwapperService")
                        .setContentText("Service Running")
                        .setSmallIcon(R.mipmap.wifiswapper_launcher_transparent)
                        .setContentIntent(pendingIntent)
                        .setTicker("Service Running")
                        .build();

        startForeground(1, notification);

        //Handler for periodically Scan for better results.

        return START_REDELIVER_INTENT;
    }

    private void getValuesFromBinder() {
        if (swapperServiceBinder != null) {
            margin = swapperServiceBinder.getMargin();
            threshold = swapperServiceBinder.getThreshold();
        }
        logInformation("Service got new Values\n");
        logInformation("Threshold: " + threshold + "\n");
        logInformation("Margin: " + margin + "\n");
    }

    /**
     * If the Activity is open, this Method logs a Massage on the Debug-Info-Textfield.
     *
     * @param logMessage The Message to log.
     */
    public final void logInformation(final String logMessage) {
        if (swapperServiceBinder != null && swapperServiceBinder.isMainActivityAvailable()) {
            swapperServiceBinder.getMainActivity().logMessage(logMessage);
        }
    }

    /**
     * Can be called to notice the Service, that the Values (Margin, Threshold) has changed.
     */
    public void valuesChanged() {
        getValuesFromBinder();
    }

    /**
     * Handles the Callback from the WifiScanReceiver.
     */
    public void callbackFromWifiScanReceiver() {
        logInformation("Service triggered by WifiScanReceiver\n");
        executeWifiChange();
    }

    public void callbackFromUserPresentReceiver() {
        logInformation("Service triggered by UserPresentReceiver\n");
        executeWifiChange();
    }

    private void executeWifiChange() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Calculate current WiFi Signal Strength (Normalized between 0-10)
        WifiInfo wifiInfo = null;
        if (wifiManager != null) {
            wifiInfo = wifiManager.getConnectionInfo();
        }
        int currentSignalLevel = 0;
        if (wifiInfo != null) {
            currentSignalLevel = normalizeSignalStrength(wifiInfo.getRssi());
            logInformation("Service: current Signal Level = " + currentSignalLevel + " (" + wifiInfo.getRssi() + " dBm)\n");
        }

        //Determine best Alternative
        int bestAlternativeSignalLevel = 0;
        ScanResult bestAlternative = null;
        if (currentSignalLevel <= threshold) {
            bestAlternative = getBestAlternative();
            if (bestAlternative != null) {
                bestAlternativeSignalLevel = normalizeSignalStrength(bestAlternative.level);
                logInformation("Service: Best alternative Signal Level = " + bestAlternativeSignalLevel + "\n");
                //If Difference between current Strength and best Alternative is more than the Margin, then Change
                if ((bestAlternativeSignalLevel - currentSignalLevel) >= margin) {

                    //List available networks
                    List<WifiConfiguration> configs = null;
                    if (wifiManager != null) {
                        configs = wifiManager.getConfiguredNetworks();
                    }
                    if (configs != null && bestAlternative != null) {
                        for (WifiConfiguration config : configs) {
                            if (config.SSID.contains(bestAlternative.SSID)) {
                                logInformation("Service: change Access Point to " + bestAlternative.SSID + "\n");
                                wifiManager.enableNetwork(config.networkId, true);
                                wifiManager.reconnect();
                            }
                        }
                    }
                }
            } else {
                logInformation("No alternative Signal found!" + "\n");
            }

        }
    }

    /*
    Returns the best Connection alternative to the Current (null if no Alternative is found).
     */
    private ScanResult getBestAlternative() {
        List<ScanResult> results = wifiManager.getScanResults();

        ScanResult bestSignal = null;
        if (results != null && !results.isEmpty()) {
            for (ScanResult result : results) {
                if (bestSignal == null || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0) {
                    bestSignal = result;
                }
            }
        }
        return bestSignal;
    }

    /*
    Normalize the Decibel Values (-35 to -95) to a Scale of 1 to 10.
     */
    private int normalizeSignalStrength(final int rssi) {
        float signalStrength = (float) (rssi - WLAN_MINIMAL_STRENGTH) / (WLAN_MAXIMUM_STRENGTH - WLAN_MINIMAL_STRENGTH) * 10;
        return Math.abs((int) signalStrength);
    }
}
