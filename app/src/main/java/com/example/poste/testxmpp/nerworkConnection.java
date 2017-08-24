package com.example.poste.testxmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Poste on 16/05/2015.
 */
public class nerworkConnection extends BroadcastReceiver {
    private static openfire open;
    public nerworkConnection(openfire op)
    {
        open=op;
    }
    public nerworkConnection()
    {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected && open!=null)
        {
            openfire.connecterinternet=true;
            open.reconexion();
        }
        else openfire.connecterinternet=false;
    }

}
