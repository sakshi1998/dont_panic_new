package com.example.sakshi.dont_panic1.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;

import com.example.sakshi.dont_panic1.TrackerService;

/**
 * Created by sakshi on 20/3/18.
 */

public class location_receiver extends BroadcastReceiver {

    Context mContext;
    private static final int PERMISSIONS_REQUEST = 1;

    public void onReceive(Context context, Intent intent) {

        this.mContext = context;

        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        if (info != null && info.isConnected()) {

            int permission = ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                startTrackerService();
            }

        }
        else {
                Intent i = new Intent(mContext, TrackerService.class);
                context.stopService(i);
            }
        }




    private void startTrackerService() {
        mContext.startService(new Intent(mContext, TrackerService.class));
        //finish();
    }

}

