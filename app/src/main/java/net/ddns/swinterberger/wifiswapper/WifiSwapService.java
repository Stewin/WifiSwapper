package net.ddns.swinterberger.wifiswapper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Background Service, who keeps running, when the Activity is closed. Uses the Broadcast-Receiver.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public final class WifiSwapService extends Service {

    private static final int WLAN_MINIMAL_STRENGTH = -95; //Entspricht einem WLAN-Signal von zirka 1%
    private static final int WLAN_MAXIMUM_STRENGTH = -35; //Entspricht einem WLAN-Signal von zirka 100 %
    private WifiScanReceiver wifiScanReceiver = null;
    private SwapperServiceBinder swapperServiceBinder;
    private WifiManager wifiManager;

    private int margin;
    private int threshold;

    @Override
    public final void onDestroy() {
        unregisterReceiver(wifiScanReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public final IBinder onBind(final Intent intent) {
        if (swapperServiceBinder == null) {
            swapperServiceBinder = new SwapperServiceBinder();
            swapperServiceBinder.setWifiSwapService(this);
        }
        return swapperServiceBinder;
    }

    @Override
    public final int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);

        if (wifiScanReceiver == null) {
            wifiScanReceiver = new WifiScanReceiver();
            wifiScanReceiver.setWifiSwapService(this);
        }
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        return START_REDELIVER_INTENT;
    }


    private void getValuesFromBinder() {
        if (swapperServiceBinder != null) {
            margin = swapperServiceBinder.getMargin();
            threshold = swapperServiceBinder.getThreshold();
        }
    }

    /**
     * If the Activity is open, this Method logs a Massage on the Debug-Info-Textfield.
     *
     * @param logMessage The Message to log.
     */
    public final void logInformation(final String logMessage) {
        if (swapperServiceBinder.isMainActivityAvailable()) {
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
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // Calculate current WiFi Signal Strength (Normalized between 0-10)
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int currentSignalLevel = normalizeSignalStrength(wifiInfo.getRssi());

        //Determine best Alternative
        int bestAlternativeSignalLevel = 0;
        ScanResult bestAlternative = null;
        if (currentSignalLevel <= threshold) {
            bestAlternative = getBestAlternative();
            bestAlternativeSignalLevel = normalizeSignalStrength(bestAlternative.level);
            logInformation("Best Alternative Strength " + bestAlternativeSignalLevel + "\n");
        }

        //If Difference between current Strength and best Alternative is more than the Margin, then Change
        if ((bestAlternativeSignalLevel - currentSignalLevel) >= margin) {

            //List available networks
            List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
            if (configs != null && bestAlternative != null) {
                for (WifiConfiguration config : configs) {
                    if (config.SSID.contains(bestAlternative.SSID)) {
                        wifiManager.enableNetwork(config.networkId, true);
                        wifiManager.reconnect();

                    }
                }
            }
        }

        logInformation("Current Threshold: " + threshold + "\n");
        logInformation("Current Margin: " + margin + "\n");
    }

    private ScanResult getBestAlternative() {
        List<ScanResult> results = wifiManager.getScanResults();
        ScanResult bestSignal = null;
        for (ScanResult result : results) {
            if (bestSignal == null || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0) {
                bestSignal = result;
            }
        }
        return bestSignal;
    }

    private int normalizeSignalStrength(final int rssi) {
        float signalStrength = (float) (rssi - WLAN_MINIMAL_STRENGTH) / (WLAN_MAXIMUM_STRENGTH - WLAN_MINIMAL_STRENGTH) * 10;
        return Math.abs((int) signalStrength);
    }
}
