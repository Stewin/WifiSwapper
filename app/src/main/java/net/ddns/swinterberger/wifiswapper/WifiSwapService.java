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
import java.util.Timer;
import java.util.TimerTask;

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
    private int timerInterval;


    private Timer executionTimer;

    @Override
    public final void onDestroy() {
        unregisterReceiver(wifiScanReceiver);
        stopTimer();
        logInformation("Service destroyed\n");
        super.onDestroy();
    }

    @Nullable
    @Override
    public final IBinder onBind(final Intent intent) {
        if (swapperServiceBinder == null) {
            swapperServiceBinder = new SwapperServiceBinder();
            swapperServiceBinder.setWifiSwapService(this);
        }
        logInformation("Service bound\n");
        return swapperServiceBinder;
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

        startTimer();

        ((WifiManager) getSystemService(Context.WIFI_SERVICE)).startScan();
        logInformation("Service started\n");
        return START_REDELIVER_INTENT;
    }

    private void startTimer() {
        this.executionTimer = new Timer();
        if (timerInterval > 0) {
            executionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logInformation("Service triggered by ExecutionTimer\n");
                    executeWifiChange();
                }
            }, 0, timerInterval * 60 * 1000L);
        }
    }

    private void stopTimer() {
        executionTimer.cancel();
    }

    private void getValuesFromBinder() {
        if (swapperServiceBinder != null) {
            margin = swapperServiceBinder.getMargin();
            threshold = swapperServiceBinder.getThreshold();
            timerInterval = swapperServiceBinder.getTimerInterval();
        }
        logInformation("Service got new Values\n");
        logInformation("Threshold: " + threshold + "\n");
        logInformation("Margin: " + margin + "\n");
        logInformation("TimerInterval: " + timerInterval + " min\n");
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
        stopTimer();
        startTimer();
    }

    /**
     * Handles the Callback from the WifiScanReceiver.
     */
    public void callbackFromWifiScanReceiver() {
        logInformation("Service triggered by WifiScanReceiver\n");
        executeWifiChange();
    }

    private void executeWifiChange() {
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

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
            bestAlternativeSignalLevel = normalizeSignalStrength(bestAlternative.level);
            logInformation("Service: Best alternative Signal Level = " + bestAlternativeSignalLevel + "\n");
        }

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
    }

    /*
    Returns the best Connection alternative to the Current.
     */
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

    /*
    Normalize the Decibel Values (-35 to -95) to a Scale of 1 to 10.
     */
    private int normalizeSignalStrength(final int rssi) {
        float signalStrength = (float) (rssi - WLAN_MINIMAL_STRENGTH) / (WLAN_MAXIMUM_STRENGTH - WLAN_MINIMAL_STRENGTH) * 10;
        return Math.abs((int) signalStrength);
    }
}
