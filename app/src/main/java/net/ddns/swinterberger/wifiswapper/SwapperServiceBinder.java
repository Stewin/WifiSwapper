package net.ddns.swinterberger.wifiswapper;

import android.os.Binder;

/**
 * Created by Stefan on 07.07.2016.
 */
public class SwapperServiceBinder extends Binder implements SwapperServiceApi {

    private MainActivity mainActivity;

    @Override
    public void registerMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean isMainActivityAvailable() {
        return mainActivity != null;
    }

    @Override
    public MainActivity getMainActivity() {

        return this.mainActivity;

    }

    @Override
    public void testService() {
        if (mainActivity != null) {
            mainActivity.logMessage("Service running!\n");
        }
    }
}
