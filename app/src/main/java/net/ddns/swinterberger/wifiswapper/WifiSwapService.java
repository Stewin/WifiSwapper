package net.ddns.swinterberger.wifiswapper;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Stefan on 12.03.2016.
 */
public class WifiSwapService extends Service {

    private WifiScanReceiver wifiScanReceiver = null;
    private int threshold, margin;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("WifiSwapService", "onCreate()");
        Toast.makeText(WifiSwapService.this, "WifiSwapService Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("WifiSwapService", "onDestroy()");
        Toast.makeText(WifiSwapService.this, "WifiSwapService Destroied", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Setup WiFi
//        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // Get WiFi status
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        Log.i("WiFi Status Service: ", wifiInfo.toString());

        // List available networks
//        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
//        if (configs != null) {
//            for (WifiConfiguration config : configs) {
//                Log.i("Configs of Service", config.toString());
//            }
//        }

        this.threshold = intent.getIntExtra(getResources().getString(R.string.intentextrathreshold), 3);
        this.margin = intent.getIntExtra(getResources().getString(R.string.intentextramargin), 3);

        // Register Broadcast Receiver
        if (wifiScanReceiver == null) {
            wifiScanReceiver = new WifiScanReceiver();
            wifiScanReceiver.setWifiSwapService(this);
            wifiScanReceiver.setThreshold(this.threshold);
            wifiScanReceiver.setMargin(this.margin);
        }
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        return START_REDELIVER_INTENT;
    }

    public void scanResultsAvailable() {
        Log.i("ScanResultCallback", "ScanResultAvailable called");
    }
}
