package net.ddns.swinterberger.wifiswapper;

import android.os.Binder;

/**
 * Service Binder for a bound Service.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public final class SwapperServiceBinder extends Binder implements SwapperServiceApi {

    private MainActivity mainActivity;
    private int threshold;
    private int margin;
    private int timerInterval;

    private WifiSwapService wifiSwapService;

    @Override
    public final boolean isMainActivityAvailable() {
        return mainActivity != null;
    }

    @Override
    public final MainActivity getMainActivity() {
        return this.mainActivity;
    }

    @Override
    public final void setMainActivity(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public int getThreshold() {
        return this.threshold;
    }

    @Override
    public final void setThreshold(final int threshold) {
        this.threshold = threshold;
        if (wifiSwapService != null) {
            wifiSwapService.valuesChanged();
        }
    }

    @Override
    public int getMargin() {
        return this.margin;
    }

    @Override
    public final void setMargin(final int margin) {
        this.margin = margin;
        if (wifiSwapService != null) {
            wifiSwapService.valuesChanged();
        }
    }

    public void setWifiSwapService(WifiSwapService wifiSwapService) {
        this.wifiSwapService = wifiSwapService;
    }
}
